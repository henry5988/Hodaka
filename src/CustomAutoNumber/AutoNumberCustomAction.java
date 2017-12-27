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
        //Skip first row
        rows.next();
        while (rows.hasNext())
        {
            row=(XSSFRow) rows.next();
            Iterator cells = row.cellIterator();
            String output = "";
            //Skip name
            cells.next();
            while (cells.hasNext())
            {
                cell=(XSSFCell) cells.next();

                if (cell.getCellTypeEnum() == CellType.STRING)
                {
                    String c = cell.getStringCellValue();
                    if(c.equals("end"))break;
                    if(c.charAt(0)=='$')output+=c.substring(1);
                    //會不會就只有'~'
                    if(c.charAt(0)=='~'){
                        //length of variable
                        int length = Integer.parseInt(c.replaceAll
                                ("[^0-9]", ""));
                        String value = c.replaceAll("[0-9]","").replace
                                ("~","");
                        System.out.println(length+" "+value);
                        output+= c;
                    }
//                    System.out.print(c+" ");
                    output += c;
                }
                else if(cell.getCellTypeEnum() == CellType.NUMERIC)
                {
                    int c = (int) cell.getNumericCellValue();
//                    System.out.print(c+" ");
                    output += c;
                }
                else{
                    //U Can Handel Boolean, Formula, Errors
                }
            }
//            System.out.println(output);
        }
    }
}
