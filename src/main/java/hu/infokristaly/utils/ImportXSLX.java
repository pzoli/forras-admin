package hu.infokristaly.utils;

import hu.infokristaly.back.domain.Accessible;
import hu.infokristaly.back.domain.AccessibleType;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.middle.service.ClientsService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

//import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportXSLX {

    private static final String ACC_KEY = "{acc}->";
    DateFormat formatter = DateFormat.getInstance();

    private LinkedList<String[]> cells = new LinkedList<String[]>();
    private LinkedList<Object[]> objects = new LinkedList<Object[]>();
    private LinkedList<Client> clients = new LinkedList<Client>();
    private List<String> importResults = new LinkedList<String>();
    private XSSFRow row = null;
    private int firstCell;
    private int lastCell;
    private int cellidx = 0;
    private int rowidx = 0;
    private Properties prop = null;
    private Field field;
    private int firstRowNumber = -1;
    private int lastRowNumber = -1;
    private int firstTab = -1;
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    public Accessible getAcc(String key, Cell cell) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        String typekey = key.substring(ACC_KEY.length());
        int typeid = Integer.parseInt(typekey.substring(1, 2));
        Accessible acc = new Accessible();
        AccessibleType type = new AccessibleType();
        type.setId(typeid);
        acc.setAccessible_type(type);
        String trimmedValue = cell.getStringCellValue().trim().replace('\n', ' ').replace('\r', ' ');
        acc.setAccessibleValue(trimmedValue);
        if (trimmedValue.isEmpty())
            return null;
        else
            return acc;
    }

    private Object getCellValue(XSSFCell cell) throws IllegalArgumentException, IllegalAccessException {
        Object result = null;
        switch (cell.getCellType()) {
        case BOOLEAN:
            result = cell.getBooleanCellValue();
            break;
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                result = cell.getDateCellValue();
            } else {
                result = cell.getNumericCellValue();
            }
            break;
        case STRING:
            String trimmedValue = cell.getStringCellValue().trim().replace('\n', ' ').replace('\r', ' ');
            if (trimmedValue.equalsIgnoreCase("x")) {
                result = true;
            } else {
                result = trimmedValue;
            }
            break;
        default:
            break;
        }
        return result;
    }

    private void setFieldValue(Client client, Object value, String key) throws NoSuchFieldException, SecurityException, IllegalAccessException {
        field = Client.class.getDeclaredField(key);
        field.setAccessible(true);
        if (value instanceof Accessible) {
            client.getAccessibles().add((Accessible) value);
        } else {
            field.set(client, value);
        }
    }

    private Object getCellObjectValue(XSSFCell cell, String key) throws IllegalAccessException, NoSuchFieldException {
        Object result;
        if (key.startsWith(ACC_KEY)) {
            result = getAcc(key, cell);
        } else {
            result = getCellValue(cell);
        }
        return result;
    }

    public void getCells() {
        String key = null;
        String[] cellValues = new String[getMaxIndex(prop.entrySet().iterator())];
        Object[] objectValues = new Object[getMaxIndex(prop.entrySet().iterator())];
        Enumeration<Object> enumer = prop.keys();
        while (enumer.hasMoreElements()) {
            key = (String) enumer.nextElement();
            Object fieldIdx = prop.getProperty(key);
            cellidx = Integer.parseInt((String) fieldIdx);
            XSSFCell cell = row.getCell(cellidx - 1);
            try {
                objectValues[cellidx - 1] = getCellObjectValue(cell, key);
                cellValues[cellidx - 1] = formatResult(objectValues[cellidx - 1]);
            } catch (IllegalAccessException | NoSuchFieldException | NullPointerException e) {
                importResults.add("[row: " + rowidx + ", col: " + cellidx + "] " + e.getLocalizedMessage());
            }
        }
        cells.add(cellValues);
        objects.add(objectValues);
    }

    public void resetLists() {
        importResults.clear();
        cells.clear();
        objects.clear();
    }

    private String formatResult(Object cellObject) {
        String result = null;
        if (cellObject instanceof Accessible) {
            result = ((Accessible) cellObject).getAccessibleValue();
        } else if (cellObject instanceof Boolean) {
            result = (Boolean.toString((Boolean) cellObject));
        } else if (cellObject instanceof Date) {
            result = formatter.format((Date) cellObject);
        } else if (cellObject instanceof Number) {
            result = ((Number) cellObject).toString();
        } else {
            result = String.valueOf(cellObject);
        }
        return result;
    }

    private int getMaxIndex(Iterator<Entry<Object, Object>> iterator) {
        int result = 0;
        while (iterator.hasNext()) {
            cellidx = Integer.parseInt((String) iterator.next().getValue());
            if (result < cellidx) {
                result = cellidx;
            }
        }
        return result;
    }

    public void importXlsx(File file) throws Exception {
        resetLists();
        OPCPackage pkg = OPCPackage.open(file);
        XSSFWorkbook wb = new XSSFWorkbook(pkg);
        firstTab = wb.getFirstVisibleTab();
        XSSFSheet sheet = wb.getSheetAt(firstTab);
        setFirstRowNumber(sheet.getFirstRowNum() + 1);
        row = sheet.getRow(getFirstRowNumber());
        setFirstCell(row.getFirstCellNum());
        setLastCell(row.getLastCellNum());
        prop = new Properties();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("import.properties");
        prop.load(stream);
        rowidx = firstRowNumber;
        boolean processRows = true;
        while ( processRows ) {
            row = sheet.getRow(rowidx);
            if ((row != null) && (row.getCell(0) != null) && (!row.getCell(0).getRawValue().isEmpty())) {
                getCells();
                rowidx++;
            } else {
                processRows = false;
                setLastRowNumber(rowidx);
            }
        }
        pkg.revert();
    }

    public LinkedList<Client> getClients() {
        return clients;
    }

    public List<String> getImportResults() {
        return importResults;
    }

    /**
     * @return the firstCell
     */
    public int getFirstCell() {
        return firstCell;
    }

    /**
     * @param firstCell
     *            the firstCell to set
     */
    public void setFirstCell(int firstCell) {
        this.firstCell = firstCell;
    }

    /**
     * @return the lastCell
     */
    public int getLastCell() {
        return lastCell;
    }

    /**
     * @param lastCell
     *            the lastCell to set
     */
    public void setLastCell(int lastCell) {
        this.lastCell = lastCell;
    }

    public void setCellList(LinkedList<String[]> cells) {
        this.cells = cells;
    }

    public LinkedList<String[]> getCellList() {
        return this.cells;
    }

    public int getFirstRowNumber() {
        return firstRowNumber;
    }

    public int getLastRowNumber() {
        return lastRowNumber;
    }

    public void setFirstRowNumber(int firstRowNumber) {
        this.firstRowNumber = firstRowNumber;
    }

    public void setLastRowNumber(int lastRowNumber) {
        this.lastRowNumber = lastRowNumber;
    }

    /**
     * @return the objects
     */
    public LinkedList<Object[]> getObjects() {
        return objects;
    }

    /**
     * @param objects
     *            the objects to set
     */
    public void setObjects(LinkedList<Object[]> objects) {
        this.objects = objects;
    }

    public void createClients() {
        clients.clear();
        Iterator<Object[]> iter = objects.iterator();
        while (iter.hasNext()) {
            Object[] cellObjectArray = iter.next();
            Enumeration<Object> enumer = prop.keys();
            Client client = new Client();
            while (enumer.hasMoreElements()) {
                String fieldkey = (String) enumer.nextElement();
                Object fieldIdx = prop.getProperty(fieldkey);
                cellidx = Integer.parseInt((String) fieldIdx);
                Object value = cellObjectArray[cellidx - 1];
                if (fieldkey.startsWith(ACC_KEY)) {
                    if (value != null) {
                        ((Accessible) value).setId(null);
                        client.getAccessibles().add((Accessible) value);
                    }
                } else {
                    if (value != null) {
                        try {
                            Field field = Client.class.getDeclaredField(fieldkey);
                            String propType = field.getType().getSimpleName();
                            String cellType = value.getClass().getSimpleName();
                            if ((field.getType() == Date.class) && (value.getClass() == String.class)) {
                                try {
                                    setFieldValue(client, dateFormat.parse((String)value), fieldkey);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (propType.equalsIgnoreCase(cellType)) {
                                setFieldValue(client, value, fieldkey);
                            } else {
                                importResults.add("field["+fieldkey+"]: with cell type ("+cellType+") not equals prop type ("+propType+")");
                            }
                        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                            importResults.add("client setFieldValue error: " + e.getMessage());
                        }
                    }
                }
            }
            clients.add(client);
        }
    }
}
