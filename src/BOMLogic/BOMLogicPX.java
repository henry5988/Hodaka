package BOMLogic;

import com.agile.api.*;
import com.agile.px.*;

import java.util.Iterator;

/**
 * Created by user on 10/11/2017.
 */
public class BOMLogicPX implements IEventAction {

    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        try {
            IChange changeOrder = (IChange) info.getDataObject();
            System.out.println("GetChange:" + changeOrder.getName());
            ITable affTab = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
            System.out.println("GetTable:" + affTab.getName());
            Iterator it = affTab.iterator();
            System.out.println("entering while loop");
            while(it.hasNext()){
                System.out.println("==aff item==");
                IRow row = (IRow) it.next();
                IItem item = (IItem) row.getReferent();
                System.out.println("ITEM "+item.getName()+"\n");
            }
            System.out.println("exiting while loop");

        } catch (APIException e) {
            e.printStackTrace();
        }
        return new EventActionResult(event, new ActionResult(ActionResult.NORESULT, null));
    }
}



