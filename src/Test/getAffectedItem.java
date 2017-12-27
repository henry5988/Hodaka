package Test;

import com.agile.api.*;

import java.util.Iterator;

/**
 * Created by user on 10/19/2017.
 * testing affected item
 */
public class getAffectedItem extends Utils {
    public static IAgileSession session;
    public static void main(String[] args) throws APIException {
        session = login();
        String user = getCurrentUser(session);
        /*IAgileClass objClass = session.getAdminInstance().getAgileClass("配方變更申請單");
        System.out.println(objClass.getName());
        IAutoNumber[] an = objClass.getAutoNumberSources();
        IChange change = (IChange)session.createObject(objClass,an[0]);
        IWorkflow[] wf =  change.getWorkflows();*/
        IItem part = (IItem) session.getObject("配方","FG1-0000000002");
        String val = part.getValue(ItemConstants.ATT_PAGE_THREE_LIST02).toString();
        IItem full = (IItem) session.getObject(ItemConstants.CLASS_PART,val);
        IItem itm = (IItem) session.getObject("Part","YWL0000000001");
        ITable tb = itm.getTable(ItemConstants.TABLE_BOM);
        try {
            tb.createRow(session.getObject("Part", "YWL0000000002"));
        }catch(Exception e){
            System.out.println(e);
            Iterator it = tb.iterator();
            while(it.hasNext()){
                IRow row = (IRow) it.next();
                IItem asdf = (IItem) row.getReferent();
                if(asdf.equals(session.getObject("Part","YWL0000000002")))
                    System.out.println("hello");
            }
        }
        int x = 0;
        /*IChange change = getChange(session,"C-00002");
        ITable affTable = getAffTab(change);
        IAgileClass objClass = session.getAdminInstance().getAgileClass(
                ChangeConstants.CLASS_ECO);
        IAutoNumber autoNumber = objClass.getAutoNumberSources()[2];
        System.out.println(autoNumber);
        IChange change2 = (IChange)session.createObject(ChangeConstants.CLASS_ECO,autoNumber);
        for(IWorkflow a: change2.getWorkflows()){
            System.out.println(a.getName());
        }*/



    }
}
