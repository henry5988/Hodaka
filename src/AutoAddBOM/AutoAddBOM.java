package AutoAddBOM;

import com.agile.api.*;
import com.agile.px.EventActionResult;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.IWFChangeStatusEventInfo;
import java.util.Iterator;

/**
 * Created by user on 10/18/2017.
 */
public class AutoAddBOM implements IEventAction{
    public static IAgileSession session;
    private static IChange autoChange;
    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        try{
            this.session = session;
            IChange changeOrder = (IChange) info.getDataObject();
            ITable affectedTable = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);

            Iterator it = affectedTable.iterator();
            autoChange = createChange();
            while(it.hasNext()){
                IRow row = (IRow) it.next();
                IItem part = (IItem) row.getReferent();
                String val = part.getValue(ItemConstants.ATT_PAGE_THREE_LIST02).toString();
                IItem full = (IItem) session.getObject(ItemConstants.CLASS_PART,val);
                addAffectedItems(full,part);
            }
            releaseChange();


        }catch(APIException e){
            e.printStackTrace();
        }
        return null;
    }
    /*
     * Create a New Change Order and Set Workflow.
     * TODO: Need to grab the right change class + right workflow
     */
    private static IChange createChange() throws APIException {
        IAgileClass objClass = session.getAdminInstance().getAgileClass(
                ChangeConstants.CLASS_ECO);
        IAutoNumber autoNumber = objClass.getAutoNumberSources()[2];
        IChange change = (IChange)session.getObject(ChangeConstants.CLASS_ECO, autoNumber);
        change.setWorkflow(change.getWorkflows()[1]);
        return change;
    }
    /*
     * full: 成品
     * part: 配方
     */
    private static void addAffectedItems(IItem full, IItem part) throws APIException {
        ITable affectedItems   = autoChange.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
        IRow   affectedItemRow = affectedItems.createRow(full);
        addRedlineBOM(full, part);
        session.disableWarning(new Integer(568));
        autoRev(affectedItemRow);
        session.enableWarning(new Integer(568));
    }

    private static void addRedlineBOM(IItem full, IItem part) throws APIException {
        ITable      redlinebomTable = full.getTable(ItemConstants.TABLE_REDLINEBOM);
        redlinebomTable.createRow(part);
    }

    private static void autoRev(IRow affectedItemRow) throws APIException {
        String oldRev = (String) affectedItemRow.getValue(ChangeConstants.ATT_AFFECTED_ITEMS_OLD_REV);
        if(oldRev==null)
            affectedItemRow.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV,1);
        else
            affectedItemRow.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV,Integer.parseInt(oldRev)+1);

    }
    /*
     * Assumes three status. One Pending, One Review, One Release
     * TODO make sure that correct workflow is selected in createChange
     */
    private static void releaseChange() throws APIException {
        autoChange.changeStatus(autoChange.getDefaultNextStatus(), false, null, false, false, null, null, null, false);
        session.disableWarning(new Integer(506));
        session.disableWarning(new Integer(344));
        autoChange.changeStatus(autoChange.getDefaultNextStatus(), false, null, false, false, null, null, null, false);
        session.enableWarning(new Integer(506));
        session.enableWarning(new Integer(344));
    }
}
