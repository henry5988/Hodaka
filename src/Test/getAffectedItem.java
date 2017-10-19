package Test;

import com.agile.api.*;

/**
 * Created by user on 10/19/2017.
 * testing affected item
 */
public class getAffectedItem extends ServerInfo{
    public static IAgileSession session;
    public static void main(String[] args) throws APIException {
        session = login(username,password,connectString);
        String user = getCurrentUser(session);
        IChange change = getChange(session,"C-00002");
        ITable affTable = getAffTab(change);
        IAgileClass objClass = session.getAdminInstance().getAgileClass(
                ChangeConstants.CLASS_ECO);
        IAutoNumber autoNumber = objClass.getAutoNumberSources()[2];
        System.out.println(autoNumber);
        IChange change2 = (IChange)session.createObject(ChangeConstants.CLASS_ECO,autoNumber);
        for(IWorkflow a: change2.getWorkflows()){
            System.out.println(a.getName());
        }



    }
}
