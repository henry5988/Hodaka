package BOMLogic;

import com.agile.api.*;
import com.agile.px.*;
import com.anselm.plm.utilobj.LogIt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;


/**
 * Created by William Huang on 10/11/2017.
 */
public class BOMLogicPX implements IEventAction {
    public static LogIt logger;
    private static boolean problem;
    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {

        try {
            logger.setLogFile("C:/Agile/BomLogic/",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger = new LogIt("BOMLogic");
        problem = false;
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        try {
            IChange changeOrder = (IChange) info.getDataObject();

            logger.log("GetChange: " + changeOrder.getName());
            ITable affTab = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
            logger.log("GetTable: " + affTab.getName());
            Iterator it = affTab.iterator();
            if(!it.hasNext()){
                logger.log("Affected Table is empty. 需要至少一筆原料");
                problem = true;
            }
            while (it.hasNext()) {
                IRow row = (IRow) it.next();
                IItem item = (IItem) row.getReferent();
                logger.log(item.getName());
                getBOM(item, 1);
            }
            if (problem) {
                resetStatus(changeOrder, session.getCurrentUser());
            }
        } catch (APIException e) {
            e.printStackTrace();
            logger.log("error");
            logger.close();
            return new EventActionResult(event, new ActionResult(ActionResult.STRING, "程式出錯"));
        }
        logger.close();
        return new EventActionResult(event, new ActionResult());
    }

    //Checking the privileges of a user before changing the status of a change
    private void resetStatus(IChange change, IUser user)
            throws APIException {

        // Check if the user can change status
        if(user.hasPrivilege(UserConstants.PRIV_CHANGESTATUS, change)) {
            IStatus nextStatus = change.getWorkflow().getStates()[0];
            change.changeStatus(nextStatus, false, "", false, false, null, null, null, false);
        } else {
            logger.log("Insufficient privileges to change status.");
        }
    }
    private static void getBOM(IItem item, int level) throws APIException {
        IRow     row;
        String   bomNumber;
        ITable   table = item.getTable(ItemConstants.TABLE_BOM);
        Iterator it    = table.iterator();
        Iterator it2   = table.iterator();
        boolean error = false;

        if(!checkOrig(it2)){
            problem=true;
            logger.log("Affected Item需至少包含一筆原料，且數量不得為0!");
        }
        while (it.hasNext()) {
            row = (IRow)it.next();
            indent(level);
            bomNumber = (String)row.getValue(ItemConstants.ATT_BOM_ITEM_NUMBER);
            String e = "Error for BOM "+bomNumber+" :";
            logger.log("Checking "+bomNumber+"...");
            //check if BOM contains only 原料 or 回收料
            if(!checkType(bomNumber)){
                error=true;
                e += "組成的料件類型僅能為原料/回收料!// ";
            }
            //true if nonempty
            if(!checkEmpty(row)){
                error=true;
                e += "欄位不得為空!// ";
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
                indent(level);logger.log(e);
            }
            else {
                indent(level);logger.log("...OK");
            }
            //If want to recursion - uncomment
            /*IItem bomItem = (IItem)row.getReferent();
            getBOM(bomItem, level + 1);*/
        }
    }

    private static boolean checkOrig(Iterator it) throws APIException {
        IRow     row;
        String   bomNumber;
        String quantity;
        while(it.hasNext()){
            row = (IRow)it.next();
            bomNumber = (String)row.getValue(ItemConstants.ATT_BOM_ITEM_NUMBER);
            quantity = (String)row.getValue(ItemConstants.ATT_BOM_QTY);
            if(bomNumber.charAt(0) =='Y'){
                return !quantity.equals("0");
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
    private static boolean checkZero(IRow row) throws APIException {
        String quantity = (String)row.getValue(ItemConstants.ATT_BOM_QTY);
        return quantity.equals("0") || quantity.equals("");
    }
    private static boolean checkFindNum(IRow row) throws APIException {
        String findNum = (String)row.getValue(ItemConstants.ATT_BOM_FIND_NUM);
        return findNum.length()!=4;
    }
    private static boolean checkType(String bomNumber) {
        return bomNumber.charAt(0) =='2' || bomNumber.charAt(0) =='5' ||bomNumber.charAt(0) =='Y' ||bomNumber.charAt(0) =='H';
    }
    private static void indent(int level) {
        int    n = level * 2;
        char[] c = new char[n];

        Arrays.fill(c, ' ');
        System.out.print(new String(c));
    }
}



