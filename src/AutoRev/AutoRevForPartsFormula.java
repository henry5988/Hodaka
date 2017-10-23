package AutoRev;

import com.agile.api.*;
import com.agile.px.EventActionResult;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.IWFChangeStatusEventInfo;

import java.util.Iterator;

/**
 * Created by user on 10/23/2017.
 */
public class AutoRevForPartsFormula implements IEventAction {
    public static IAgileSession session;
    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        this.session = session;

        try {
            IChange changeOrder = (IChange) info.getDataObject();
            ITable affectedTable = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
            Iterator it = affectedTable.iterator();
            while(it.hasNext()){
                IRow row = (IRow) it.next();
                autoRev(row);
            }
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void autoRev(IRow row) throws APIException{
        String oldRev = (String) row.getValue(ChangeConstants.ATT_AFFECTED_ITEMS_OLD_REV);
        if(oldRev==null)
            row.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV,1);
        else
            row.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV,Integer.parseInt(oldRev)+1);
    }
}
