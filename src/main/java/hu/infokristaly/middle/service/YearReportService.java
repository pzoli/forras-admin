package hu.infokristaly.middle.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.ScheduleEvent;

import hu.infokristaly.back.domain.Card;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientChanges;
import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.back.domain.Message;
import hu.infokristaly.back.domain.SubjectType;
import hu.infokristaly.back.model.AppProperties;
import hu.infokristaly.back.model.CatMap;
import hu.infokristaly.back.model.MonthMap;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.back.model.YearMap;


@Stateless
public class YearReportService implements Serializable {

    private static final long serialVersionUID = 3947995879482874010L;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private ClientsService clientsService;

    @Inject
    private AppProperties appProperties;

    @Inject
    private LogService logService;
    
    @Inject
    private MessageService messageService;
    
    @Inject
    private Logger log;

    private static final byte EVENT = 6;

    private CellStyle cellStyle;
    private static final long DEFAULT_SUBJ_TYPE_EGYEB = 5L;
    private static final int TOP_ROW_INDEX = 6;

    public void createReport(SystemUser user, Integer reportStartDate,Integer reportEndDate, Boolean visibleByActiveBool, String[] selectedClientTypes) {
        StringBuffer reportLog = new StringBuffer();

        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd. HH:mm:ss");

        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, reportStartDate);
        startDate.set(Calendar.MONTH, 0);
        startDate.set(Calendar.DATE, 1);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);

        if (reportEndDate == null) {
            reportEndDate = reportStartDate;
        }

        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.YEAR, reportEndDate);
        endDate.set(Calendar.MONTH, 11);
        endDate.set(Calendar.DATE, 31);
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);

        Calendar currentStartDate = new GregorianCalendar();
        currentStartDate.setTime(startDate.getTime());

        Calendar currentEndDate = new GregorianCalendar();
        currentEndDate.setTime(startDate.getTime());
        currentEndDate.set(Calendar.HOUR_OF_DAY, 24);
        currentEndDate.set(Calendar.MINUTE, 0);
        currentEndDate.set(Calendar.SECOND, 0);
        currentEndDate.add(Calendar.MONTH, 1);
        currentEndDate.set(Calendar.DATE, 1);

        int eventCount = 0;
        int nullEventCount = 0;
        int startYear = startDate.get(Calendar.YEAR);
        int monthIndex = 0;
        int yearIndex = currentStartDate.get(Calendar.YEAR) - startYear;
        log.info("Jelentés gyártása elkezdődött.");
        log.info("==============================");
        YearMap yearMap = new YearMap();
        while (endDate.after(currentStartDate)) {
            HashMap<String, Integer> clientMap = new HashMap<String, Integer>();
            List<ScheduleEvent> report = (List<ScheduleEvent>) scheduleService.loadEvents(currentStartDate.getTime(), currentEndDate.getTime(), TimeZone.getDefault(), false, null);
            Iterator<ScheduleEvent> iter = report.iterator();
            MonthMap monthMap = yearMap.getMonthMap(yearIndex);
            while (iter.hasNext()) {
                EventHistory item = (EventHistory) iter.next();
                if ((item.getSubject() != null) && (item.getSubject().getSubjectType() != null) && (item.getSubject().getSubjectType().getId() != null) && (item.getSubject().getSubjectType().getId() < 0)) {
                    continue;
                }
                log.info("Event: " + item.getTitle() + ":" + dateFormatter.format(item.getStartDate()) + "-" + dateFormatter.format(item.getEndDate()));
                SubjectType type = item.getSubject().getSubjectType();
                if (type == null) {
                    type = new SubjectType();
                    type.setId(DEFAULT_SUBJ_TYPE_EGYEB);
                }
                List<Client> clients = item.getClients();
                for (Client client : clients) {
                    try {
                        ClientChanges version = clientsService.getClientVersion(client, item.getStartDate());
                        if (version == null) {
                            continue;
                        }
                        boolean containsType = (((version.getClientType() != null) && ArrayUtils.contains(selectedClientTypes, String.valueOf(version.getClientType().getId())))) || ((version.getClientType() == null) && containsNull(selectedClientTypes));
                        if (((version.getActive().equals(visibleByActiveBool)) || (visibleByActiveBool == null)) && containsType) {
                            String nySzam = client.getNyilvantartasiSzam();

                            CatMap catMap = monthMap.getCatMap(monthIndex);
                            catMap.incCatCounter(type.getId());

                            monthMap.put(monthIndex, catMap);
                            yearMap.put(yearIndex, monthMap);

                            if (clientMap.containsKey(nySzam)) {
                                Integer cCount = clientMap.get(nySzam);
                                if (cCount == null) {
                                    cCount = 1;
                                } else {
                                    cCount += 1;
                                }
                                clientMap.put(nySzam, cCount);
                            } else {
                                clientMap.put(nySzam, 1);
                            }
                        }
                    } catch (Exception ex) {
                        reportLog.append("Dátum inkonzisztencia! ("+ex.getMessage()+");");                        
                    }
                }
                log.info("Kliensek száma: " + clientMap.size());
                eventCount++;
            }
            int clientCount = clientMap.size();
            CatMap catMap = monthMap.getCatMap(monthIndex);
            catMap.setClientCount(clientCount);
            yearMap.put(yearIndex, monthMap);
            currentStartDate.add(Calendar.MONTH, 1);
            currentStartDate.set(Calendar.DATE, 1);
            currentEndDate.add(Calendar.MONTH, 1);
            currentEndDate.set(Calendar.DATE, 1);
            monthIndex = currentStartDate.get(Calendar.MONTH);
            yearIndex = currentStartDate.get(Calendar.YEAR) - startYear;
        }
        try {
            exportClientCatsToOneSheet(yearMap, currentStartDate.getTime());
            log.info("=== sheets exported ========");
        } catch (IOException e) {
            log.finest(e.getLocalizedMessage());
            reportLog.append("Jelentés gyártása közben hiba történt: " + e.getLocalizedMessage() + ";");
        } catch (InvalidFormatException e) {
            if (!isWeekend(currentStartDate)) {
                nullEventCount++;
            }
        } catch (Exception ex) {
            log.finest(ex.getLocalizedMessage());
            reportLog.append("Jelentés gyártása közben hiba történt: " + ex.getLocalizedMessage() + ";");
        }

        reportLog.append(" Jelentésgyártás befejeződött. " + dateFormatter.format(startDate.getTime()) + " - " + dateFormatter.format(endDate.getTime()) + " (" + eventCount + " feldolgozott esemény, " + nullEventCount + " napra nincs esemény) ");
        
        logService.logUserActivity(user, EVENT, LogService.EXECUTE, reportLog.toString());

        Message message = new Message();
        Card card = new Card();
        card.setRecipientSystemUser(user);
        List<Card> cards = new LinkedList<Card>();
        cards.add(card);
        message.setCards(cards);
        message.setTitle("Éves jelentésgyártás befejeződött");
        message.setSender(null);
        message.setSentDate(new Date());
        message.setMessage(reportLog.toString());
        messageService.persist(message);

        log.info("Jelentésgyártása befejeződött.");
        log.info("===============================");
    }

    private boolean isWeekend(Calendar executeCalendar) {
        int weekDay = executeCalendar.get(Calendar.DAY_OF_WEEK);
        boolean result = (weekDay == Calendar.SATURDAY) || (weekDay == Calendar.SUNDAY);
        return result;
    }

    private boolean containsNull(String[] clientTypes) {
        boolean result = false;
        for (String clientType : clientTypes) {
            if ((clientType == null) || "".equals(clientType)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void exportClientCatsToOneSheet(YearMap yearMap, Date startDate) throws InvalidFormatException, IOException {

        Calendar month = new GregorianCalendar();
        month.setTime(startDate);

        if (yearMap.isEmpty()) {
            throw new InvalidFormatException("Foglalkozáslista üres a megadott szűrési paraméterek alapján.");
        }

        FileOutputStream fileOut = null;
        OPCPackage pkg = null;
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd.");

        String folder = "summary";
        String targetDir = appProperties.getDestinationPath() + "/" + folder;
        File targetDirFile = new File(targetDir);
        if (!targetDirFile.exists() || !targetDirFile.isDirectory()) {
            targetDirFile.mkdir();
            log.info("Könyvtár létrehozása: " + targetDirFile);
        }
        String destinationFileName = targetDir + "/" + "len_month_sum.xlsx";
        log.info("Generált fájl neve: " + destinationFileName);
        try {

            FileUtils.copyFile(new File(appProperties.getTemplatePath() + "/" + "len_month_sum.xlsx"), new File(destinationFileName));
            pkg = OPCPackage.open(destinationFileName);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);

            cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd"));

            int firstTab = wb.getFirstVisibleTab();
            XSSFSheet sheet = wb.getSheetAt(firstTab);
            int yearIndex = 0;
            Iterator<Entry<Integer, MonthMap>> yearIter = yearMap.entrySet().iterator();
            while (yearIter.hasNext()) {
                XSSFRow row;
                Entry<Integer, MonthMap> item = yearIter.next();
                yearIndex = item.getKey();
                MonthMap monthMap = item.getValue();

                Iterator<Entry<Integer, CatMap>> monthIter = monthMap.entrySet().iterator();
                while (monthIter.hasNext()) {
                    Entry<Integer, CatMap> monthEntry = monthIter.next();
                    int monthIndex = monthEntry.getKey();
                    row = sheet.getRow(TOP_ROW_INDEX + monthIndex);
                    Integer nySzamCount = monthMap.getCatMap(monthIndex).getClientCount();

                    XSSFCell cell = row.getCell(1 + yearIndex);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(nySzamCount);

                    CatMap catTypeMap = monthEntry.getValue();
                    Iterator<Entry<Long, Long>> catIter = catTypeMap.entrySet().iterator();
                    while (catIter.hasNext()) {
                        Entry<Long, Long> catEntry = catIter.next();
                        long count = catEntry.getValue();
                        long catType = catEntry.getKey();
                        row = sheet.getRow(TOP_ROW_INDEX + monthIndex);
                        cell = row.getCell(new Long(yearMap.size() + 1 + yearIndex + ((catType - 1) * yearMap.size())).intValue());
                        cell.setCellValue(count);
                    }
                    monthIndex++;
                }
            }
            fileOut = new FileOutputStream(destinationFileName + ".out");
            wb.write(fileOut);
        } catch (Exception ex) {
            log.throwing("ScheduleManager.class", "exportClientCats", ex);
            throw ex;
        } finally {
            try {
                if (fileOut != null) {
                    fileOut.close();
                    new File(destinationFileName + ".out").delete();
                }
                pkg.close();
            } catch (Exception e) {
                log.fine(e.getLocalizedMessage());
            }
        }
    }

}
