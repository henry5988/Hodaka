package CustomAutoNumber;

import com.agile.api.*;
import com.agile.px.ActionResult;
import com.agile.px.ICustomAction;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import william.util.Ini;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static Test.Utils.getAffectedTable;


public class AutoNumberCustomAction implements ICustomAction{
    static Ini ini = new Ini();
    static final String EXCEL_FILE = ini.getValue("File Location",
            "EXCEL_FILE_PATH");
    public static void main(String[] args) throws IOException {

    }

    @Override
    public ActionResult doAction(IAgileSession session,
                                 INode node,
                                 IDataObject change) {
        IChange changeOrder = (IChange)change;
        try {
            ITable affectedTable = getAffectedTable(changeOrder);
            Iterator it = affectedTable.iterator();

            IRow row;
            IItem item;
            //loop through affected Items
            while(it.hasNext()){
                //get affected item
                row = (IRow) it.next();
                item = (IItem) row.getReferent();
                //get agile class
                String agileClass =item.getAgileClass().getAPIName();


                //find agile class in excel
                String autoNumber = getAutoNumber(agileClass);
                //parse definition for excel class

                //assign description based on definition
            }
        } catch (APIException e) {
            e.printStackTrace();
        }
        return new ActionResult(ActionResult.STRING,"Success");    }

    private String getAutoNumber(String agileClass) {
        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = new FileInputStream
                    (EXCEL_FILE);
        } catch (FileNotFoundException e) {

        }
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(ExcelFileToRead);
        } catch (IOException e) {

        }
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator rows = sheet.rowIterator();
        return parseExcelRule(findClassRow(agileClass,rows),sheet);
    }

    /*

     */
    private String parseExcelRule(int rowNum,XSSFSheet sheet) {
        if (rowNum==-1)return "Cannot Find Rule";
        String autoNumber = "";
        //Get Row
        XSSFRow row = sheet.getRow(rowNum);


        return autoNumber;
    }

    /*
     * if exist, return row num
     * if not exist, return -1
     */
    private int findClassRow(String agileClass, Iterator it){
        XSSFRow row;
        //Skip first row
        it.next();

        while (it.hasNext())
        {
            row=(XSSFRow) it.next();
            String className = (String) row.getCell(0).getStringCellValue();
            if (className.equals(agileClass))return row.getRowNum();
        }
        return -1;
    }
}
