package hu.infokristaly.front.view;

import java.io.Serializable;

import javax.faces.bean.ViewScoped;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@ViewScoped
@Named
public class ExporterView implements Serializable {

    private static final long serialVersionUID = -1581286862041458582L;

    private void removeLastCells(HSSFRow row, int maxColCount) {
        HSSFCell cell = row.getCell(maxColCount);
        if (cell != null) {
            row.removeCell(cell);
        }
        cell = row.getCell(maxColCount - 1);
        if (cell != null) {
            row.removeCell(cell);
        }
        cell = row.getCell(maxColCount - 2);
        if (cell != null) {
            row.removeCell(cell);
        }
    }

    public void postProcessXLS(Object document) {

        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        int maxRowCount = sheet.getPhysicalNumberOfRows();
        for (int rowIdx = 0; rowIdx < maxRowCount; rowIdx++) {
            HSSFRow row = sheet.getRow(rowIdx);
            int maxColCount = row.getPhysicalNumberOfCells();
            removeLastCells(row, maxColCount);
            removeFirstCell(row, maxColCount);
        }
    }

    private void removeFirstCell(HSSFRow row, int maxColCount) {
        HSSFCell cell = row.getCell(1);
        row.removeCell(row.getCell(0));
        row.moveCell(cell, (short) 0);
        cell = row.getCell(2);
        if (cell != null) {
            row.moveCell(cell, (short) 1);
        }
        cell = row.getCell(3);
        if (cell != null) {
            row.moveCell(cell, (short) 2);
        }
        cell = row.getCell(4);
        if (cell != null) {
            row.moveCell(cell, (short) 3);
        }
    }
}
