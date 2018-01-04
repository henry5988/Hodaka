package CustomAutoNumber;

import com.agile.api.*;
import com.agile.px.ActionResult;
import com.agile.px.ICustomAction;
import com.anselm.plm.util.AUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import william.util.Ini;
import william.util.LogIt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import static Test.Utils.getAffectedTable;
import static Test.Utils.getAgileSession;


public class AutoNumberCustomAction implements ICustomAction{
    static Ini ini = new Ini();
    static final String EXCEL_FILE = ini.getValue("File Location",
            "EXCEL_FILE_PATH");
    private String FILE_PATH = "C:/Agile/AutoNumberCustomAction"+new
            SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime())+".txt";
    private static LogIt logger;
    private IAgileSession admin;
    public static void main(String[] args) throws IOException {
        InputStream ExcelFileToRead = new FileInputStream
                (EXCEL_FILE);
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
            String autoNumber = "";
            //Skip name
            cells.next();
            while (cells.hasNext())
            {
                cell=(XSSFCell) cells.next();

                if (cell.getCellTypeEnum() == CellType.STRING)
                {
                    String c = cell.getStringCellValue();
                    if(c.equals("end"))break;
                    if(c.charAt(0)=='$')autoNumber+=c.substring(1);
                    if(c.charAt(0)=='~'){
                        //length of variable
                        int length = Integer.parseInt(c.replaceAll
                                ("[^0-9]", ""));
                        String value = c.replaceAll("[0-9]","").replace
                                ("~","");
//                        System.out.println(length+" "+value);
                        autoNumber+= c;
                    }
//                    System.out.print(c+" ");


                }
                else if(cell.getCellTypeEnum() == CellType.NUMERIC)
                {
                    int c = (int) cell.getNumericCellValue();
                    autoNumber += c;
                }
                else{
//                    System.out.println(cell.getRawValue());
                    //U Can Handel Boolean, Formula, Errors
                }
            }
            System.out.println(autoNumber);
        }
    }
    @Override
    public ActionResult doAction(IAgileSession session,
                                 INode node,
                                 IDataObject change) {
        try {
            logger = new LogIt("AutoNumberCustomAction");
            logger.setLogFile(FILE_PATH);
            admin = getAgileSession(ini,"AgileAP");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                //parse definition for excel class
                String autoNumber = getAutoNumber(item);
                //assign description based on definition
//                item.setValue(ItemConstants.ATT_TITLE_BLOCK_NUMBER,autoNumber);

            }
        } catch (APIException e) {
            e.printStackTrace();
        }
        return new ActionResult(ActionResult.STRING,"Success");    }

    private String getAutoNumber(IItem item) throws APIException {
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
        //get agile class
        String agileClass =item.getAgileClass().getAPIName();
        return parseExcelRule(findClassRow(agileClass,rows),sheet,item);
    }

    /*

     */
    private String parseExcelRule(int rowNum,XSSFSheet sheet,IItem item) {
        if (rowNum==-1)return "Cannot Find Rule";
        String autoNumber = "";
        //Get Row
        XSSFRow row = sheet.getRow(rowNum);
        Iterator cells = row.cellIterator();
        //Skip name cell
        cells.next();
        XSSFCell cell;
        while(cells.hasNext()){
            cell = (XSSFCell) cells.next();
            if (cell.getCellTypeEnum() == CellType.STRING)
            {
                String c = cell.getStringCellValue();
                if(c.equals("end"))break;
                if(c.charAt(0)=='$')autoNumber+=c.substring(1);
                //會不會就只有'~'
                if(c.charAt(0)=='~'){
                    //length of variable
                    int length = Integer.parseInt(c.replaceAll
                            ("[^0-9]", ""));
                    String value = c.replaceAll("[0-9]","").replace
                            ("~","");
                    span(value,length,item);
                    autoNumber+= c;
                }

            }
            else if(cell.getCellTypeEnum() == CellType.NUMERIC)
            {
                int c = (int) cell.getNumericCellValue();
                autoNumber += c;

            }
            else{
                //U Can Handel Boolean, Formula, Errors
                //ignore null
            }
        }



        return autoNumber;
    }

    private String span(String value, int length, IItem item){
        String toReturn = "";
        if (value.length()==0){
            for(int i = 0; i<length;i++){
                toReturn+=0;
            }
            return toReturn;
        }else{
            IAgileClass agileClass = null;
            try {
                agileClass = item.getAgileClass();
                IAttribute atr = agileClass.getAttribute("Page Three." + value);
                atr.getDataType();
            } catch (APIException e) {
                e.printStackTrace();
            }

            //search for value
            return null;
        }
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
            String className = row.getCell(0).getStringCellValue();
            if (className.equals(agileClass))return row.getRowNum();
        }
        return -1;
    }
}
// subclass name probably can't be found due to chinese character vs apiname