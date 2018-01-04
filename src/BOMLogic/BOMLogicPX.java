package BOMLogic;

import com.agile.api.*;
import com.agile.px.*;
import com.anselm.plm.util.AUtil;
import com.anselm.plm.utilobj.Ini;
import william.util.LogIt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;



/**
 * Created by William Huang on 10/11/2017.
 */
public class BOMLogicPX implements IEventAction {
    private static LogIt logger;
    private static boolean problem;
    private String FILE_PATH = "C:/Agile/BomLogic"+new SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime())+".txt";
    private final String INI_FILE_PATH = "C:/Agile/Config.ini";
    private IAgileSession admin;
    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {
        try {
            logger = new LogIt("BOMLogic");
            logger.setLogFile(FILE_PATH);
            Ini ini = new Ini(INI_FILE_PATH);
            admin = AUtil.getAgileSession(ini, "AgileAP");
        } catch (Exception e) {
            e.printStackTrace();
        }

        problem = false;
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;

        try {
            IChange changeOrder = (IChange) info.getDataObject();

            logger.log("GetChange: " + changeOrder.getName());
            ITable affTab = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
            logger.log("GetTable: " + affTab.getName());
            Iterator it = affTab.iterator();
            if(!it.hasNext()){
                //應該會被criteria鎖住所以其實不會出現這個情況
                logger.log("Affected Table is empty. 需要至少一筆配方");
                problem = true;
            }
            while (it.hasNext()) {
                IRow row = (IRow) it.next();
                IItem item = (IItem) row.getReferent();
                logger.log(item.getName());
                getBOM(item, 1);
            }
            if (problem) {
                logger.log("由於邏輯失敗，不可進站，請維護好后再重新嘗試!");
                resetStatus(changeOrder, admin.getCurrentUser());
                logger.close();
                ITable attachment = changeOrder.getAttachments();
                attachment.createRow(FILE_PATH);
                new File(FILE_PATH).delete();
                return new EventActionResult(event, new ActionResult
                        (ActionResult.STRING, "過站失敗，請讀取附件的log檔"));
            }
            else{
                logger.close();
                new File(FILE_PATH).delete();
                return new EventActionResult(event, new ActionResult
                        (ActionResult.STRING,"程式執行成功"));
            }

        } catch (APIException e) {
            logger.log(e.toString());
            logger.close();
            try {
                IChange changeOrder = (IChange) info.getDataObject();
//                resetStatus( changeOrder, admin
//                        .getCurrentUser());
                ITable attachment = changeOrder.getAttachments();
                attachment.createRow(FILE_PATH);
                new File(FILE_PATH).delete();
            } catch (APIException e1) {
                e1.printStackTrace();
                return new EventActionResult(event, new ActionResult(ActionResult
                        .STRING, "變更狀態時出錯"));
            }
            new File(FILE_PATH).delete();
            return new EventActionResult(event, new ActionResult(ActionResult
                    .STRING, "程式出錯請檢查attachment裏的log檔！"));
        }

    }
    
    
    private void resetStatus(IChange change, IUser user)
            throws APIException {
        // Check if the user can change status - 以admin的身份應該不會有問題的。
        if(user.hasPrivilege(UserConstants.PRIV_CHANGESTATUS, change)) {
            IChange changeOrder = (IChange) admin.getObject(ChangeConstants
                    .CLASS_ECO,change.getName());
            IStatus currentStatus = changeOrder.getStatus();
            IWorkflow wf = changeOrder.getWorkflow();
            for(int i = 0; i<wf.getStates().length;i++){
                if (currentStatus.equals(wf.getStates()[i])) {
                    IStatus nextStatus = changeOrder.getWorkflow().getStates()
                            [i-1];
                    changeOrder.changeStatus(nextStatus, false, "", false,
                            false, null, null, null,  false);
                    break;
                }
            }


        } else {
            logger.log("Insufficient privileges to change status.");
        }
    }
    private static void getBOM(IItem item, int level) throws APIException {
        IRow     row;
        String   bomNumber;
        ITable   table = item.getTable(ItemConstants.TABLE_REDLINEBOM);
        Iterator it    = table.iterator();
        Iterator it2   = table.iterator();
        boolean error = false;
        int count=0;
        //check empty
        if(it.hasNext()&&!checkOrig(it2)){
            problem = true;
            logger.log(1, "需至少包含一筆原料，且數量不得為0!");
        }

        while (it.hasNext()) {
            error=false;
            row = (IRow)it.next();
            if(row.isFlagSet(ItemConstants.FLAG_IS_REDLINE_REMOVED))
                continue;
            count++;
            bomNumber = (String)row.getValue(ItemConstants.ATT_BOM_ITEM_NUMBER);
            String e = "Error for 半成品 "+bomNumber+" :";
            logger.log(1,"Checking "+bomNumber+"...");
            //check if BOM contains only 原料 or 回收料
            //暫時不需要
            /*if(!checkType(bomNumber)){
                error=true;
                e += "組成的料件類型僅能為原料/回收料!// ";
            }*/
            //true if nonempty
            if(!checkEmpty(row)){
                error=true;
                e += errEmptyBOMUnit(row)+errEmptyProp(row)+errEmptyType(row);
            }
            //true if problem
            if(!checkKGandPC(row)){
                error=true;
                e += "[BOM單位]需要與主單位+次單位一致！//";
            }
            //true if problem
            if(!checkConversionRate(row)){
                error=true;
                e += "[標準單位轉換率]由於kg與pc一致所以值只能為1！//";
            }
            //true if zero
            if(checkZero(row)){
                error=true;
                e += "[BOM數量]欄位資訊不可為0!// ";
            }
            if(checkFindNum(row)){
                error=true;
                e += "[Find Num]格式必須為四碼 ";
            }
            if (error){
                problem=true;
                logger.log(level,e);
            }
            else {
                logger.log(level,"...OK");
            }

            //If want recursion - uncomment
            /*IItem bomItem = (IItem)row.getReferent();
            getBOM(bomItem, level + 1);*/
        }
        if(count==0){
            problem=true;
            logger.log(level,"無BOM ITEM!");
        }
    }

    private static boolean checkOrig(Iterator it) throws APIException {
        IRow     row;
        String   bomType;
        String quantity;
        while(it.hasNext()){
            row = (IRow)it.next();
            if(row.isFlagSet(ItemConstants.FLAG_IS_REDLINE_REMOVED))
                continue;
            IAgileList value = (IAgileList)row.getValue(ItemConstants.ATT_BOM_BOM_LIST01);
            bomType = value.toString();
            quantity = (String)row.getValue(ItemConstants.ATT_BOM_QTY);
            if(bomType.equals("原料")){
                return !quantity.equals("0") && !quantity.equals("");
            }
        }
        return false;
    }
    private static boolean checkEmpty(IRow row) throws APIException {
        boolean toReturn;
        /*[原料/副產品] bom list 01
        [BOM單位] bom list 02
        [比例型態 ] bom list 03*/
        IAgileList value1 = (IAgileList)row.getValue(ItemConstants.ATT_BOM_BOM_LIST01);
        IAgileList value2 = (IAgileList)row.getValue(ItemConstants.ATT_BOM_BOM_LIST02);
        IAgileList value3 = (IAgileList)row.getValue(ItemConstants.ATT_BOM_BOM_LIST03);
        toReturn = !value1.toString().equals("")&&!value2.toString().equals("")&&!value3.toString().equals("");
        return toReturn;
    }
    private static String errEmptyType(IRow row) throws APIException {
        IAgileList value = (IAgileList)row.getValue(ItemConstants.ATT_BOM_BOM_LIST01);
        return !value.toString().equals("") ? "":"[原料/副產品]不能空";
    }
    private static String errEmptyBOMUnit(IRow row) throws APIException {
        IAgileList value = (IAgileList)row.getValue(ItemConstants.ATT_BOM_BOM_LIST02);
        return !value.toString().equals("") ? "":"[BOM單位]不能空";
    }
    private static String errEmptyProp(IRow row) throws APIException {
        IAgileList value = (IAgileList)row.getValue(ItemConstants.ATT_BOM_BOM_LIST03);
        return !value.toString().equals("") ? "":"[比例形態]不能空";
    }
    private static boolean checkZero(IRow row) throws APIException {
        String quantity = (String)row.getValue(ItemConstants.ATT_BOM_QTY);
        String value = row.getValue(ItemConstants
                .ATT_BOM_BOM_LIST01).toString();
        return quantity.equals("") || (quantity.equals("0") && !value.equals
                ("副產品"));
    }
    private static boolean checkFindNum(IRow row) throws APIException {
        String findNum = (String)row.getValue(ItemConstants.ATT_BOM_FIND_NUM);
        return findNum.length()!=4;
    }
    //return false if fails
    //returns true if pass
    private static boolean checkKGandPC(IRow row) throws APIException{
        String main = row.getValue(ItemConstants.ATT_BOM_ITEM_LIST11).toString();
        String sub = row.getValue(ItemConstants.ATT_BOM_ITEM_LIST12).toString();
        if(main.equals(sub)){
            String BOMUnit = row.getValue(ItemConstants.ATT_BOM_BOM_LIST02).toString();
            if(!BOMUnit.equals(main)){
                return false;
            }
        }
        return true;
    }
    private static boolean checkConversionRate(IRow row) throws APIException{
        String main = row.getValue(ItemConstants.ATT_BOM_ITEM_LIST11).toString();
        String sub = row.getValue(ItemConstants.ATT_BOM_ITEM_LIST12).toString();
        if(main.equals(sub)){
            double conversionRate = (double) row.getValue(ItemConstants.ATT_BOM_ITEM_P2_NUMERIC01);
            if(conversionRate!=1.0){
                return false;
            }
        }
        return true;
    }
    private static boolean checkType(String bomNumber) {
        //2 是原物料 5 是回收料
        return bomNumber.charAt(0) =='2' || bomNumber.charAt(0) =='5';
    }
}



