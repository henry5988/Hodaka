package Test;

import com.agile.api.*;
import william.util.Ini;

import java.util.HashMap;

/**
 * Created by William on 10/18/2017.
 */
public class Utils {
    // Update the following fields with information for your server
    final static String connectString = "http://hodakadb:7001/Agile/";
    final static String username = "admin";
    final static String password = "agile934";
    public static AgileSessionFactory m_factory;

    public static IAgileSession login() throws APIException {

        // Create the params variable to hold login parameters
        HashMap params = new HashMap();

        // Put username and password values into params
        params.put(AgileSessionFactory.USERNAME, username);
        params.put(AgileSessionFactory.PASSWORD, password);

        // Get an Agile server instance. ("agileserver" is the name of the Agile
        // proxy server,
        // and "virtualPath" is the name of the virtual path used for the Agile
        // system.)
        m_factory = AgileSessionFactory.getInstance(connectString);

        // Create the Agile PLM session and log in
        return m_factory.createSession(params);

    }
    public static String getCurrentUser(IAgileSession session) throws APIException {
        IUser currentUser = session.getCurrentUser();
        String userName = currentUser.getName();

        System.out.println("Logged in user: " + userName);
        return userName;
    }
    public static IChange getECO(IAgileSession session, String changeNum)
            throws APIException{
        IChange change;
        change = (IChange) session.getObject(ChangeConstants.CLASS_ECO,changeNum);
        System.out.println("GET ECO CHANGE: "+change.getName());
        return change;
    }
    public static IChange getECR(IAgileSession session, String changeNum)
            throws APIException{
        IChange change;
        change = (IChange) session.getObject(ChangeConstants.CLASS_ECR,
                changeNum);
        System.out.println("GET ECR CHANGE: "+change.getName());
        return change;
    }

    public static IItem getItem(IAgileSession session, String itemNum) throws
            APIException{
        IItem item;
        item = (IItem) session.getObject(ItemConstants.CLASS_ITEM_BASE_CLASS,itemNum);
        System.out.println("GET ITEM: "+item.getName());
        return item;
    }

    public static ITable getAffectedTable(IChange change) throws APIException{
        ITable table;
        table = change.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
        System.out.println("Size of Affected table: "+table.size());
        return table;
    }

    public static ITable getBOMTable(IItem item) throws APIException{
        ITable table;
        table = item.getTable(ItemConstants.TABLE_BOM);
        System.out.println("Size of BOM table: "+table.size());
        return table;
    }

    public static ITable getRLBOMTable(IItem item) throws APIException{
        ITable table;
        table = item.getTable(ItemConstants.TABLE_REDLINEBOM);
        System.out.println("Size of BOM table: "+table.size());
        return table;
    }

    private void resetStatus(IChange change, IUser user)
            throws APIException {
        // Check if the user can change status - 以admin的身份應該不會有問題的。
        if(user.hasPrivilege(UserConstants.PRIV_CHANGESTATUS, change)) {
            IStatus currentStatus = change.getStatus();
            IWorkflow wf = change.getWorkflow();
            for(int i = 0; i<wf.getStates().length;i++){
                if (currentStatus.equals(wf.getStates()[i])) {
                    IStatus previousStatus = change.getWorkflow().getStates()[i-1];
                    change.changeStatus(previousStatus, false, "", false, false, null, null, null, false);
                    break;
                }
            }


        } else {
            System.out.println("Insufficient privileges to change status.");
        }
    }

    public static IAgileSession getAgileSession(Ini ini, String target) {
        return getAgileSession(ini.getValue("AgileAP", "url"), ini.getValue("AgileAP", "username"), ini.getValue("AgileAP", "password"));
    }

    public static IAgileSession getAgileSession(String agileurl, String agileusr, String agilepwd) {
        try {
            HashMap<Integer, String> params = new HashMap();
            params.put(AgileSessionFactory.USERNAME, agileusr);
            params.put(AgileSessionFactory.PASSWORD, agilepwd);
            params.put(AgileSessionFactory.URL, agileurl);
            return AgileSessionFactory.createSessionEx(params);
        } catch (Exception var4) {
            System.out.println("<getAgileSession>error: " + agileusr + "/" + agilepwd + "," + agileurl);
            var4.printStackTrace();
            return null;
        }
    }

}
