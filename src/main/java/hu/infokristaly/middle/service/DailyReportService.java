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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
import hu.exprog.beecomposit.back.model.SystemUser;

@Stateless
public class DailyReportService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final byte EVENT = 5;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private ClientsService clientsService;

    @Inject
    private LogService logService;

    @Inject
    private MessageService messageService;

    @Inject
    private Logger log;

    @Inject
    private AppProperties appProperties;

    private int pageSize = 52;
    private CellStyle cellStyle;
    private static int DEFAULT_SUBJ_TYPE_EGYEB = 5;

    public void createReport(SystemUser user, Date reportStartDate, Date reportEndDate, Boolean visibleByActiveBool, String[] selectedClientTypes) {

        StringBuffer reportLog = new StringBuffer();

        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd. HH:mm");

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(reportStartDate);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        if (reportEndDate == null) {
            reportEndDate = reportStartDate;
        }

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(reportEndDate);
        endDate.set(Calendar.HOUR_OF_DAY, 24);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);

        Calendar currentStartDate = new GregorianCalendar();
        currentStartDate.setTime(startDate.getTime());

        Calendar currentEndDate = new GregorianCalendar();
        currentEndDate.setTime(startDate.getTime());
        currentEndDate.set(Calendar.HOUR_OF_DAY, 24);
        currentEndDate.set(Calendar.MINUTE, 0);
        currentEndDate.set(Calendar.SECOND, 0);
        currentEndDate.set(Calendar.MILLISECOND, 0);

        int eventCount = 0;
        int nullEventCount = 0;
        log.info("Jelentés gyártása elkezdődött.");
        log.info("==============================");
        while (endDate.after(currentStartDate)) {
            List<ScheduleEvent> report = (List<ScheduleEvent>) scheduleService.loadEvents(currentStartDate.getTime(), currentEndDate.getTime(), TimeZone.getDefault(), false, null);
            TreeMap<String, HashMap<Integer, Integer>> clientCats = new TreeMap<String, HashMap<Integer, Integer>>();
            Iterator<ScheduleEvent> iter = report.iterator();

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
                        boolean containsType = ((version.getClientType() != null) && ArrayUtils.contains(selectedClientTypes, String.valueOf(version.getClientType().getId()))) || ((version.getClientType() == null) && containsNull(selectedClientTypes));
                        if ((version.getActive().equals(visibleByActiveBool) || (visibleByActiveBool == null) || "".equals(visibleByActiveBool)) && containsType) {
                            String nySzam = client.getNyilvantartasiSzam();
                            HashMap<Integer, Integer> cats;
                            if (clientCats.containsKey(nySzam)) {
                                cats = clientCats.get(nySzam);
                                if (cats.containsKey(type.getId())) {
                                    Integer value = cats.get(type.getId());
                                    if (value == null) {
                                        value = 1;
                                    } else {
                                        value += 1;
                                    }
                                    cats.put(type.getId(), value);
                                } else {
                                    cats.put(type.getId(), 1);
                                }
                                clientCats.put(nySzam, cats);
                            } else {
                                cats = new HashMap<Integer, Integer>();
                                cats.put(type.getId(), 1);
                                clientCats.put(nySzam, cats);
                            }
                        }
                    } catch (Exception ex) {
                        reportLog.append("Dátum inkonzisztencia! (" + ex.getMessage() + ")");
                    }
                }
                log.info("Kliensek száma: " + clientCats.size());
            }
            try {
                exportClientCatsToOneSheet(clientCats, currentStartDate.getTime());
                log.info("=== sheets exported ========");
                eventCount++;
            } catch (IOException e) {
                log.finest(e.getLocalizedMessage());
                reportLog.append("Jelentés gyártása közben hiba történt: " + e.getLocalizedMessage());
            } catch (InvalidFormatException e) {
                if (!isWeekend(currentStartDate)) {
                    nullEventCount++;
                }
            } catch (Exception ex) {
                log.finest(ex.getLocalizedMessage());
                reportLog.append("Jelentés gyártása közben hiba történt: " + ex.getLocalizedMessage());
            }
            currentStartDate.add(Calendar.DATE, 1);
            currentEndDate.add(Calendar.DATE, 1);
        }

        reportLog.append("Jelentésgyártás befejeződött. " + dateFormatter.format(reportStartDate.getTime()) + " - " + dateFormatter.format(reportEndDate.getTime()) + " (" + eventCount + " generált fájl, " + nullEventCount + " napra nincs esemény)");
        logService.logUserActivity(user, EVENT, LogService.EXECUTE, reportLog.toString());

        if (user != null) {
            Message message = new Message();
            Card card = new Card();
            card.setRecipientSystemUser(user);
            List<Card> cards = new LinkedList<Card>();
            cards.add(card);
            message.setCards(cards);
            message.setTitle("Jelentésgyártás befejeződött");
            message.setSender(null);
            message.setSentDate(new Date());
            message.setMessage(reportLog.toString());
            messageService.persist(message);
        }
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
            if (clientType == null || "".equals(clientType)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void exportClientCatsToOneSheet(Map<String, HashMap<Integer, Integer>> clientCats, Date startDate) throws InvalidFormatException, IOException {

        int rowIndex = 10;
        int maxRow = 34;
        int summClientRow = 0;
        int pageIndex = 0;
        Iterator<Entry<String, HashMap<Integer, Integer>>> iter = clientCats.entrySet().iterator();
        if (clientCats.isEmpty()) {
            throw new InvalidFormatException("Foglalkozáslista üres a megadott szűrési paraméterek alapján.");
        }

        FileOutputStream fileOut = null;
        OPCPackage pkg = null;
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd.");
        Calendar todayCal = GregorianCalendar.getInstance();
        Calendar startCal = GregorianCalendar.getInstance();
        startCal.setTime(startDate); // String.format("%02d",
                                     // startCal.get(Calendar.MONTH))
        String folder = startCal.get(Calendar.YEAR) + ". " + startCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String targetDir = appProperties.getDestinationPath() + "/" + folder;
        File targetDirFile = new File(targetDir);
        if (!targetDirFile.exists() || !targetDirFile.isDirectory()) {
            targetDirFile.mkdir();
            log.info("Könyvtár létrehozása: " + targetDirFile);
        }
        String destinationFileName = targetDir + "/" + "statisztika-" + dateFormatter.format(startDate) + "xlsx";
        log.info("Generált fájl neve: " + destinationFileName);
        try {
            int maxClientNum = clientCats.entrySet().size();
            int pageCount = (int) Math.ceil(maxClientNum / 25D);
            int countOnPage = 0;
            String srcFileName = "statisztika" + pageCount + ".xlsx";
            log.info("Forrás fájl: " + srcFileName);
            FileUtils.copyFile(new File(appProperties.getTemplatePath() + "/" + srcFileName), new File(destinationFileName));
            pkg = OPCPackage.open(destinationFileName);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);

            cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd"));

            int firstTab = wb.getFirstVisibleTab();
            XSSFSheet sheet = wb.getSheetAt(firstTab);

            setHeaderValuesOnSheet(sheet, 1, startDate);

            XSSFRow row;
            // XSSFRow row = sheet.getRow(50);
            // row.getCell(5).setCellValue(pageIndex);

            while (iter.hasNext()) {

                if (rowIndex > ((pageSize * pageIndex) + maxRow)) {
                    setFooterValuesOnSheet(sheet, countOnPage, -1, pageCount, pageIndex);
                    countOnPage = 0;
                    pageIndex++;
                    log.info("Új lap: " + (pageIndex + 1));
                    rowIndex = (pageSize * pageIndex) + 10;

                    setHeaderValuesOnSheet(sheet, pageIndex + 1, startDate);

                    // row = sheet.getRow(50);
                    // row.getCell(5).setCellValue(pageIndex);

                }

                summClientRow++;
                countOnPage++;

                row = sheet.getRow(rowIndex);

                Entry<String, HashMap<Integer, Integer>> item = iter.next();
                XSSFCell cell = row.getCell(1);
                cell.setCellType(CellType.STRING);
                String nySzam = item.getKey();
                cell.setCellValue(nySzam);
                log.info(rowIndex + ". row 1. cell value: " + cell.getStringCellValue());
                Iterator<Entry<Integer, Integer>> subjTypeIter = item.getValue().entrySet().iterator();
                while (subjTypeIter.hasNext()) {
                    Entry<Integer, Integer> subjtype = subjTypeIter.next();
                    cell = row.getCell(1 + subjtype.getKey());
                    cell.setCellValue(subjtype.getValue());
                }
                rowIndex++;
            }
            setFooterValuesOnSheet(sheet, countOnPage, summClientRow, pageCount, pageIndex);
            log.info("Mindösszessen: " + summClientRow);
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
                if (pkg != null)
                	pkg.close();
            } catch (Exception e) {
                log.fine(e.getLocalizedMessage());
            }
        }
    }

    private void setFooterValuesOnSheet(XSSFSheet sheet, int countOnPage, int summClientRow, int pageCount, int pageIndex) {
        XSSFRow row = sheet.getRow((52 * pageIndex) + 35);
        XSSFCell cell = row.getCell(3);
        if (cell != null) {
            cell.setCellValue(countOnPage);
            if ((summClientRow > -1) && (pageCount > 1)) {
                row = sheet.getRow((52 * pageIndex) + 36);
                cell = row.getCell(3);
                if (cell != null) {
                    cell.setCellValue(summClientRow);
                }
            }
        }
    }

    private void setHeaderValuesOnSheet(XSSFSheet sheet, int pageIndex, Date startDate) {
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // XSSFRow row = sheet.getRow(1);
        // row.getCell(6).setCellValue(startDate);
        // row.getCell(6).setCellStyle(cellStyle);

        // XSSFRow row = sheet.getRow(2);
        // XSSFCell cell = row.getCell(0);
        // String summPages = cell.getStringCellValue();
        // summPages = summPages.replaceAll("\\{lapok\\}",
        // String.valueOf(sheet.getWorkbook().getNumberOfSheets()));
        // cell.setCellValue(summPages);

        XSSFRow row = sheet.getRow((pageSize * (pageIndex - 1)) + 7);
        row.getCell(0).setCellValue(startDate);
        row.getCell(0).setCellStyle(cellStyle);

        // row = sheet.getRow(36);
        // row.getCell(6).setCellValue(pageIndex);

        // row = sheet.getRow(38);
        // row.getCell(6).setCellValue(startDate);
        // row.getCell(6).setCellStyle(cellStyle);
    }

}
