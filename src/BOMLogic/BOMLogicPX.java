package BOMLogic;

import com.agile.api.*;
import com.agile.px.*;
import com.anselm.plm.utilobj.LogIt;

import java.util.Arrays;
import java.util.Iterator;


/**
 * Created by user on 10/11/2017.
 */
public class BOMLogicPX implements IEventAction {
    public static LogIt logger = new LogIt();
    public static String e = "過站失敗：";
    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        try {
            IChange changeOrder = (IChange) info.getDataObject();
            logger.log("GetChange:" + changeOrder.getName());
            ITable affTab = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
            logger.log("GetTable:" + affTab.getName());
            Iterator it = affTab.iterator();
            while(it.hasNext()){
                IRow row = (IRow) it.next();
                IItem item = (IItem) row.getReferent();
                logger.log(item.getName());
                getBOM(item,1);

            }

        } catch (APIException e) {
            e.printStackTrace();
        }
        return new EventActionResult(event, new ActionResult(ActionResult.STRING, e));
    }
    private static void getBOM(IItem item, int level) throws APIException {
        IRow     row;
        String   bomNumber;
        ITable   table = item.getTable(ItemConstants.TABLE_BOM);
        Iterator it    = table.iterator();

        while (it.hasNext()) {
            row = (IRow)it.next();
            indent(level);
            bomNumber = (String)row.getValue(ItemConstants.ATT_BOM_ITEM_NUMBER);
            //check if BOM contains only 原料 or 回收料
            if(!checkType(bomNumber)){
                e += "組成的料件類型僅能為原料/回收料!";
            }
            checkEmpty(row);
            checkNonZero(row);
            checkFindNum(row);
            logger.log(bomNumber);

            //If want to recursion - uncomment
            /*IItem bomItem = (IItem)row.getReferent();
            getBOM(bomItem, level + 1);*/
        }
    }

    private static boolean checkFindNum(IRow row) {
        return false;
    }

    private static boolean checkNonZero(IRow row) {
        return false;
    }

    private static boolean checkEmpty(IRow row) {
        //TODO check if item has empty columns - do later
        return false;
    }

    private static boolean checkType(String bomNumber) {
        return bomNumber.charAt(0) =='2' || bomNumber.charAt(0) =='5';
    }

    private static void indent(int level) {
        int    n = level * 2;
        char[] c = new char[n];

        Arrays.fill(c, ' ');
        System.out.print(new String(c));
    }
}



