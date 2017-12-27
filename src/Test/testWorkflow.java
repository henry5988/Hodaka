package Test;


import com.agile.api.*;

import java.util.HashMap;

/**
 * Created by user on 10/18/2017.
 */
public class testWorkflow extends Utils {
    private static final String ITEM_NUMBER = "FG1-0000000001";
    public static IAgileSession session;
    public static void main(String[] args){
        try {
            session = login();
            String user = getCurrentUser(session);
            //IItem item = create(session);
            IItem item = get(ITEM_NUMBER);
            IItem bomItem = get("YWL0000000001");

            //create change
            IChange change = createChange();
            //add affected item
            addAffectedItems(change,item);

            ITable      redlinebomTable = item.getTable(ItemConstants.TABLE_REDLINEBOM);
            //add redline bom
            redlinebomTable.createRow(bomItem);
            //release
            releaseChange(change);

        } catch (APIException e) {
            e.printStackTrace();
        }
    }
    private static IItem create(IAgileSession myServer) throws APIException {
        IItem item = null;

        item = (IItem)myServer.createObject("配方", ITEM_NUMBER);
        System.out.println("Item " + ITEM_NUMBER + " created");
        return item;
    }
    private static IItem get(String itemNumber) throws APIException {
        IItem item = null;

        item = (IItem)session.getObject(ItemConstants.CLASS_PART,itemNumber);
        if(item!=null)
            System.out.println("Item " + item.toString() + " retrieved");
        else
            System.out.println("Item not found");
        return item;
    }
    /**
     * <p> create an change</p>
     *
     * @return a Change object
     * @throws APIException
     */
    private static IChange createChange() throws APIException {
        final String CHANGE_NUMBER  = "C-00003";
        HashMap params = new HashMap();
        params.put(ChangeConstants.ATT_COVER_PAGE_NUMBER, CHANGE_NUMBER);
        IChange change = (IChange)session.getObject("Change Order", params);
        //IChange change = (IChange)session.getObject(ChangeConstants.CLASS_CHANGE_BASE_CLASS, "C-00001");
        System.out.println("Change created successfully");
        change.setWorkflow(change.getWorkflows()[1]);
        return change;
    }
    private static void addAffectedItems(IChange change, IItem item) throws APIException {
        ITable affectedItems   = change.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
        session.disableWarning(new Integer(568));
        IRow   affectedItemRow = affectedItems.createRow(item);
        affectedItemRow.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV,
                1);
        session.enableWarning(new Integer(568));
    }
    //自動過站
    private static void releaseChange(IChange change) throws APIException {
        change.changeStatus(change.getDefaultNextStatus(), false, null, false, false, null, null, null, false);
        session.disableWarning(new Integer(506));
        session.disableWarning(new Integer(344));
        change.changeStatus(change.getDefaultNextStatus(), false, null, false, false, null, null, null, false);
        session.enableWarning(new Integer(506));
        session.enableWarning(new Integer(344));
    }

}
