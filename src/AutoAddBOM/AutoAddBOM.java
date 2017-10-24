package AutoAddBOM;

import com.agile.api.*;
import com.agile.px.EventActionResult;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.IWFChangeStatusEventInfo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by user on 10/18/2017.
 */
public class AutoAddBOM implements IEventAction{
    public static IAgileSession session;
    private static IChange autoChange;
    private static ArrayList<IItem> list;

    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        try{
            this.session = session;
            IChange changeOrder = (IChange) info.getDataObject();
            ITable affectedTable = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);

            Iterator it = affectedTable.iterator();
            autoChange = createChange();
            list = new ArrayList<IItem>();
            while(it.hasNext()){
                IRow row = (IRow) it.next();
                IItem part = (IItem) row.getReferent();
                //Assuming PageThree.List02 is a list of IItem.
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
     */
    private static IChange createChange() throws APIException {
        //Get Change Class and Autonumber
        IAgileClass objClass = session.getAdminInstance().getAgileClass("M01-自動加入配方(客製程式使用)申請單");
        //This assumes that only one autonumber option is available.
        IAutoNumber autoNumber = objClass.getAutoNumberSources()[0];
        IChange change = (IChange)session.createObject("M01-自動加入配方(客製程式使用)申請單", autoNumber);
        //This assumes that only one workflow is available
        change.setWorkflow(change.getWorkflows()[0]);
        return change;
    }
    /*
     * full: 成品
     * part: 配方
     */
    private static void addAffectedItems(IItem full, IItem part) throws APIException {
        ITable affectedItems   = autoChange.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
        IRow affectedItemRow   = null;
        if(list.contains(full)){
            Iterator it = affectedItems.iterator();
            while(it.hasNext()){
                IRow row = (IRow) it.next();
                IItem item = (IItem) row.getReferent();
                if(item.equals(full)){
                    affectedItemRow = row;
                    break;
                }
            }
        }else{
            affectedItemRow = affectedItems.createRow(full);
        }
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
        if(oldRev=="")
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
