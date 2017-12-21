package CustomAutoNumber;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class AutoNumberCustomAction {
    public static void main(String[] args) throws IOException {
        InputStream ExcelFileToRead = new FileInputStream
                ("C:\\Users\\user\\Desktop\\Anselm\\Hodaka\\src" +
                        "\\CustomAutoNumber\\AN.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;
        XSSFCell cell;

        Iterator rows = sheet.rowIterator();

        while (rows.hasNext())
        {
            row=(XSSFRow) rows.next();
            Iterator cells = row.cellIterator();
            while (cells.hasNext())
            {
                cell=(XSSFCell) cells.next();

                if (cell.getCellTypeEnum() == CellType.STRING)
                {

                    System.out.print(cell.getStringCellValue()+" ");
                }
                else if(cell.getCellTypeEnum() == CellType.NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                }
                else{
                    //U Can Handel Boolean, Formula, Errors
                }
            }
            System.out.println();
        }
    }
}
