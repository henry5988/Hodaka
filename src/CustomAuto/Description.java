package CustomAuto;

import com.agile.api.*;
import com.agile.px.ActionResult;
import com.agile.px.ICustomAction;
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
    private String FILE_PATH = "C:/Agile/AutoDescription"+new
            SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime())+".txt";
    private static LogIt logger;
    private static int errorCount;
    private IAgileSession admin;
    @Override
    public ActionResult doAction(IAgileSession session,
                                 INode node,
                                 IDataObject change) {
        try {
            Ini ini = new Ini("C:/Agile/Config.ini");
            EXCEL_FILE = ini.getValue("File Location",
                    "EXCEL_FILE_PATH_DESCRIPTION");
            logger = new LogIt("CustomAutoDescription");
            logger.setLogFile(FILE_PATH);
            admin = getAgileSession(ini,"AgileAP");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //TODO admin get change
//            IChange changeOrder = (IChange) admin.getObject(ChangeConstants.CLASS_CHANGE_ORDERS_CLASS,
//                    change.getName());
            IChange changeOrder = (IChange) change;
            logger.log("Get Change as Admin:"+changeOrder);
            ITable affectedTable = getAffectedTable(changeOrder);
            Iterator it = affectedTable.iterator();

            IRow row;
            IItem item;
            //loop through affected Items
            while(it.hasNext()){
                //get affected item
                row = (IRow) it.next();
                item = (IItem) row.getReferent();
                logger.log("物件: "+item);
                //parse definition for excel class
                String autoDescription = getAutoDescription(item);
                if (autoDescription.equals("")){
                    errorCount++;
                    logger.log(1,item.getAgileClass()+"規則錯誤, 跳過...");continue;
                }
                logger.log(1,"依規則產生出的描述: "+autoDescription);
                //assign description based on definition
                ITable table = item.getTable(ItemConstants.TABLE_REDLINETITLEBLOCK);
                Iterator tableIterator = table.getTableIterator();
                IRow tableRow = (IRow) tableIterator.next();
                logger.log(1,"設定新的描述...");
                tableRow.getCell(ItemConstants.ATT_TITLE_BLOCK_DESCRIPTION).setValue
                        (autoDescription);
                logger.log(2,"描述設定成功.");
            }
        } catch (APIException e) {
            logger.log("Failure.");
            logger.log(e);
            logger.close();
            return new ActionResult(ActionResult.STRING,"Failure");
        }
        logger.close();
        String result = errorCount==0?"Success":errorCount+"筆item失敗，請檢查log檔";
        return new ActionResult(ActionResult.STRING,result);
    }

    private String getAutoDescription(IItem item) throws APIException {
        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = new FileInputStream
                    (EXCEL_FILE);
        } catch (FileNotFoundException e) {
            logger.log("找不到該檔案，請檢查Config.ini!");
        }
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(ExcelFileToRead);
        } catch (IOException e) {

        }
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator rows = sheet.rowIterator();
        //get agile class
        String agileClass =item.getAgileClass().getName();
        logger.log("搜索"+agileClass+"對應的規則");
        return parseExcelRule(findClassRow(agileClass,rows),sheet,item);
    }

    /*

     */
    private String parseExcelRule(int rowNum,XSSFSheet sheet,IItem item) {
        if (rowNum==-1)return "";
        String autoNumber = "";
        //Get Row
        XSSFRow row = sheet.getRow(rowNum);
        Iterator cells = row.cellIterator();
        //Skip name cell
        cells.next();
        XSSFCell cell;
        while(cells.hasNext()){
            cell = (XSSFCell) cells.next();
            if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
            {
                String c = cell.getStringCellValue();
                if(c.toLowerCase().equals("end"))break;
                else if(c.charAt(0)=='$')autoNumber+=c.substring(1);
                else{//dynamically allocate
                    String spanResult = span(c,item);
                    if(spanResult.equals(""))return"";
                    autoNumber+= spanResult;
                }
            }
            else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
            {
                int c = (int) cell.getNumericCellValue();
                autoNumber += c;
            }
        }
        return autoNumber;
    }
    /*
       span autonumber based on length and value
       assume all the specification is located on page three
     */
    private String span(String value, IItem item){
        String toReturn = "";
        String attribute = "Page Three." + value;
        IAgileClass agileClass;
        try {
            agileClass = item.getAgileClass();
            IAttribute atr = agileClass.getAttribute(attribute);
            int type = atr.getDataType();
            //assumes that we can only read from lists and texts
            if(type == DataTypeConstants.TYPE_DOUBLE || type ==
                    DataTypeConstants.TYPE_STRING) {
                toReturn += item.getValue(attribute)+" ";
            }else{
                String listVal = item.getValue(attribute).toString();
                ICell cell = item.getCell(attribute);
                IAgileList list = (IAgileList) cell.getValue();
                if(list.getChildNodes()!=null) {
                    toReturn += ((IAgileList)list.getChild(listVal))
                            .getDescription().split("\\|")[1];
                }
                else
                    toReturn += listVal;
            }

        } catch (APIException e) {
            logger.log(e.getMessage());
            return "";
        } catch (ArrayIndexOutOfBoundsException e){
            logger.log("List Description 欄位需要有個|符號。前面為流水號規則，後面為描述規則！");
            return "";
        }
        return toReturn;


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
//            logger.log(className+agileClass);
            if (className.toLowerCase().equals(agileClass.toLowerCase()))return row
                    .getRowNum();
        }
        logger.log("找不到對應的規則!跳過...");
        return -1;
    }
}
// subclass name probably can't be found due to chinese character vs apiname