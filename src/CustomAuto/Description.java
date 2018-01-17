package CustomAuto;

import com.agile.api.*;
import com.agile.px.ActionResult;
import com.agile.px.ICustomAction;
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


public class Description implements ICustomAction{
    static String EXCEL_FILE;
    private String FILE_PATH = "C:/Agile/Description"+new
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
                    else if(c.charAt(0)=='$')autoNumber+=c.substring(1);
                    else if(c.charAt(0)=='~'){
                        //length of variable
                        int length = Integer.parseInt(c.replaceAll
                                ("[^0-9]", ""));
//                        System.out.println(length);
                        String value = c.replaceAll("[0-9]","").replace
                                ("~","");
//                        System.out.println(length+" "+value);
                        autoNumber+= c;
                    }else{
                        autoNumber+=c;
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
            Ini ini = new Ini("C:/Agile/Config.ini");
            EXCEL_FILE = ini.getValue("File Location",
                    "EXCEL_FILE_PATH");
            logger = new LogIt("Number");
            logger.setLogFile(FILE_PATH);
            admin = getAgileSession(ini,"AgileAP");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            IChange changeOrder = (IChange) admin.getObject(ChangeConstants.CLASS_ECO,change.getName());
            logger.log("Get Change as Admin:"+changeOrder);
            ITable affectedTable = getAffectedTable(changeOrder);
            Iterator it = affectedTable.iterator();

            IRow row;
            IItem item;
            //loop through affected Items
            logger.log("Affected Table:");
            while(it.hasNext()){
                //get affected item
                row = (IRow) it.next();
                item = (IItem) row.getReferent();
                logger.log("Get Item: "+item);
                //parse definition for excel class
                String autoNumber = getAutoDescription(item);
                logger.log(1,"Generated Autonumber: "+autoNumber);
                //assign description based on definition
                ITable table = item.getTable(ItemConstants.TABLE_REDLINETITLEBLOCK);
                Iterator tableIterator = table.getTableIterator();
                IRow tableRow = (IRow) tableIterator.next();
                logger.log(1,"Setting New Autonumber...");
                tableRow.getCell(ItemConstants.ATT_TITLE_BLOCK_NUMBER).setValue(autoNumber);
                logger.log(2,"Success.");
            }
        } catch (APIException e) {
            logger.log("Failure.");
            logger.log(e.getMessage());
            logger.close();
            return new ActionResult(ActionResult.STRING,"Failure");
        }
        logger.close();
        return new ActionResult(ActionResult.STRING,"Success");    }

    private String getAutoDescription(IItem item) throws APIException {
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
        String autoDescription = "";
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
                else if(c.charAt(0)=='$')autoDescription+=c.substring(1);
                    //TODO 會不會就只有'~'
                else{
                    autoDescription+= getValue(c,item);
                }

            }
            else if(cell.getCellTypeEnum() == CellType.NUMERIC)
            {
                int c = (int) cell.getNumericCellValue();
                autoDescription += c;

            }
            else{
                //U Can Handel Boolean, Formula, Errors
                //ignore null
            }
        }



        return autoDescription;
    }

    /*
        Different method
     */
    private String getValue(String value, IItem item){
        String toReturn = "";
        String attribute = "Page Three." + value;

        IAgileClass agileClass = null;
        try {
            agileClass = item.getAgileClass();
            IAttribute atr = agileClass.getAttribute(attribute);
            int type = atr.getDataType();
            //assumes that we can only read from lists and texts
            if(type == DataTypeConstants.TYPE_DOUBLE || type == DataTypeConstants.TYPE_STRING) {
                toReturn += item.getValue(attribute);
            }else{
                String listVal = item.getValue(attribute).toString();
                ICell cell = item.getCell(attribute);
                IAgileList list = (IAgileList) cell.getValue();
                //TODO does description have more rules. ie: |
                if(list.getChildNodes()!=null)
                    toReturn += ((IAgileList)list.getChild(listVal)).getDescription();
                else
                    toReturn += listVal;
            }

        } catch (APIException e) {
            logger.log("執行span submethod時出錯");
            logger.log(e.getMessage());
        }
        finally {
            return toReturn;
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