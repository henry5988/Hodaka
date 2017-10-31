package AutoRev;

import com.agile.api.*;
import com.agile.px.*;
import william.util.LogIt;

import java.io.File;
import java.util.Iterator;

/**
 * Created by user on 10/23/2017.
 */
public class AutoRevForPartsFormula implements IEventAction {
    public static IAgileSession session;
    private static LogIt logger;
    private final String FILE_PATH = "C:/Agile/AutoRevForPartsFormula.txt";
    @Override
    public EventActionResult doAction(IAgileSession session, INode actionNode, IEventInfo event) {
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        this.session = session;
        try {
            logger = new LogIt("AutoRevForPartsFormula");
            logger.setLogFile(FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            IChange changeOrder = (IChange) info.getDataObject();
            logger.log("Change Order = "+changeOrder.toString());
            ITable affectedTable = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
            Iterator it = affectedTable.iterator();
            while(it.hasNext()){
                IRow row = (IRow) it.next();
                autoRev(row);
            }
            logger.close();
            ITable attachment = changeOrder.getAttachments();
            attachment.createRow(FILE_PATH);
            new File(FILE_PATH).delete();
        } catch (APIException e) {
            logger.close();
            e.printStackTrace();
        }
        return new EventActionResult(event, new ActionResult(ActionResult.STRING, "成功"));
    }

    private void autoRev(IRow row) throws APIException{
        String oldRev = (String) row.getValue(ChangeConstants.ATT_AFFECTED_ITEMS_OLD_REV);
        if(oldRev=="")
            row.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV,1);
        else
            row.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV,Integer.parseInt(oldRev)+1);
        logger.log(1,"New Rev for "+row.getReferent().toString()+" is "+row.getValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV));
    }
}
