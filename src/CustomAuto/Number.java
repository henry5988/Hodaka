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


public class Number implements ICustomAction{
    static String EXCEL_FILE;
    private String FILE_PATH = "C:/Agile/AutoNumber"+new
            SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime())+".txt";
    private static LogIt logger;
    private IAgileSession admin;
    private static int errorCount;
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
                    if(c.toLowerCase().equals("end"))break;
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
                    "EXCEL_FILE_PATH_NUMBER");
            logger = new LogIt("CustomAutoNumber");
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
                String autoNumber = getAutoNumber(item);
                if (autoNumber.equals("")){
                    errorCount++;
                    logger.log(1,item.getAgileClass()+"規則錯誤, 跳過...");continue;
                }
                logger.log(1,"依規則產生出的流水號: "+autoNumber);
                //assign description based on definition
//                ITable table = item.getTable(ItemConstants.TABLE_REDLINETITLEBLOCK);
//                Iterator tableIterator = table.getTableIterator();
//                IRow tableRow = (IRow) tableIterator.next();

                logger.log(1,"設定新的流水號...");
                item.setValue(ItemConstants.ATT_TITLE_BLOCK_NUMBER,autoNumber);
//                tableRow.getCell(ItemConstants.ATT_TITLE_BLOCK_NUMBER).setValue(autoNumber);
                logger.log(2,"流水號設定成功.");
            }
        } catch (APIException e) {
            logger.log("Failure.");
            logger.log(e);
            logger.close();
            return new ActionResult(ActionResult.STRING,"Failure");
        }
        logger.close();
        String result = errorCount==0?"程式執行成功":errorCount+"筆item失敗，請檢查log檔";
        return new ActionResult(ActionResult.STRING,result);
    }

    private String getAutoNumber(IItem item) throws APIException {
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
        return parseRule(findClassRow(agileClass,rows),sheet,item);
    }

    /*

     */
    private String parseRule(int rowNum, XSSFSheet sheet, IItem item) {
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
                if(c.equals("end"))break;
                else if(c.charAt(0)=='$')autoNumber+=c.substring(1);
                else if(c.charAt(0)=='~'){
                    if(c.length()==1) {
                        logger.log(1,"規則錯誤...跳過");
                        return "";
                    }
                    //length of variable
                    //needs more checking
                    //what if there are numbers in the attribute name already
                    try {
                        int length = Integer.parseInt(c.replaceAll
                                ("[^0-9]", ""));
                        String value = c.replaceAll("[0-9]","").replace
                                ("~","");
                        String spanResult;
                        if (value.length()==0){
                            spanResult= span(autoNumber,length,item);
                        }else{
                            spanResult= span(value,length,item,false);
                        }
                        if(spanResult.equals(""))return "";
                        autoNumber+=spanResult;

                    }catch(Exception e){
                        logger.log(1,"沒有指定長度！跳過... ");
                        e.printStackTrace();
                        return "";
                    }
                }else{//dynamically allocate
                    String spanResult = span(c,-1,item,true);
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
      example: ~4 => 0000 but if 0000 already used, increment to 0001, do
      this until query yields no result
     */
    private String span(String autoNumber, int length, IItem item) throws APIException {
        int count = 0;
        while(true) {
            String temp = autoNumber;
            int countLength = String.valueOf(count).length();
            for (int i = 0; i < length-countLength; i++) {
                temp += 0;
            }
            temp+=count;

            IQuery query = (IQuery) admin.createObject(IQuery.OBJECT_TYPE,
                    item.getAgileClass().toString());
            String criteria = "[" + ItemConstants.ATT_TITLE_BLOCK_NUMBER + "] " +
                    "Contains'"+temp+"'";
            query.setCriteria(criteria);
            ITable table = query.execute();
            if(table.size()==0){
                return temp.replaceAll(autoNumber,"");
            }
            count++;
        }
    }

    /*
       span autonumber based on length and value
       assume all the specification is located on page three
     */
    private String span(String value, int length, IItem item, boolean dynamic){
        String toReturn = "";
        //        String attribute = "Page Three." + value;
        String attribute = "第三頁." + value;
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
                if(list.getChildNodes()!=null)
                    toReturn += ((IAgileList)list.getChild(listVal))
                            .getDescription().split("\\|")[0];
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
        if (!dynamic) {
            if(toReturn.length()!=length){
                logger.log(1,"維護欄位與指定長度不一樣!");
                return "";
            }
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