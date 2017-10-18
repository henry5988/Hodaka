package Test;


import com.agile.api.*;

import java.util.HashMap;

/**
 * Created by user on 10/18/2017.
 */
public class testWorkflow extends ServerInfo {
    private static final String ITEM_NUMBER = "FG1-0000000001";
    public static IAgileSession session;
    public static void main(String[] args){
        try {
            session = login(username,password,connectString);
            String user = getCurrentUser(session);
            //IItem item = create(session);
            IItem item = get(ITEM_NUMBER);
            IChange change = createChange();
            addAffectedItems(change,item);
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
     * <p> create an ECO </p>
     *
     * @return a Change object
     * @throws APIException
     */
    private static IChange createChange() throws APIException {
        final String CHANGE_NUMBER  = "C-00003";
        HashMap params = new HashMap();
        params.put(ChangeConstants.ATT_COVER_PAGE_NUMBER, CHANGE_NUMBER);
        IChange change = (IChange)session.createObject("Change Order", params);
        //IChange change = (IChange)session.getObject(ChangeConstants.CLASS_CHANGE_BASE_CLASS, "C-00001");
        System.out.println("Change created successfully");
        change.setWorkflow(change.getWorkflows()[1]);
        return change;
    }
    private static void addAffectedItems(IChange change, IItem item) throws APIException {
        ITable affectedItems   = change.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
        session.disableWarning(new Integer(568));
        IRow   affectedItemRow = affectedItems.createRow(item);
        session.enableWarning(new Integer(568));
    }
    private static void releaseChange(IChange change) throws APIException {
        IWorkflow workflow = change.getWorkflow();
        IStatus review        = getStatus(workflow, StatusConstants.TYPE_REVIEW);
        IStatus released   = getStatus(workflow, StatusConstants.TYPE_RELEASED);
        change.changeStatus(review, false, null, false, false, null, null, null, false);
        session.disableWarning(new Integer(506));
        session.disableWarning(new Integer(344));
        change.changeStatus(released, false, null, false, false, null, null, null, false);
        session.enableWarning(new Integer(506));
        session.enableWarning(new Integer(344));
    }
    private static IStatus getStatus(IWorkflow wf, StatusConstants status)
            throws APIException {
        IStatus[] states = wf.getStates(status);
        IStatus   state  = states[0];
        return state;
    }
}
