package AutoAddBOM;

import com.agile.api.*;
import com.agile.px.*;
import com.anselm.plm.util.AUtil;
import com.anselm.plm.utilobj.Ini;
import william.util.LogIt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by user on 10/18/2017.
 */
public class AutoAddBOMPX implements ICustomAction {
    private static IChange autoChange;
    private static ArrayList<IItem> list;
    private static IAgileSession admin;
    private static LogIt logger;
    private final String FILE_PATH = "C:/Agile/AutoAddBOM.txt";
    private final String INI_FILE_PATH = "C:/Agile/Config.ini";
    private static IChange changeOrder;

    @Override
    public ActionResult doAction(IAgileSession session, INode
            actionNode, IDataObject change) {
        try {
            logger = new LogIt("AutoAddBOM");
            logger.setLogFile(FILE_PATH);
            Ini ini = new Ini(INI_FILE_PATH);
            //Get Admin Session
            admin = AUtil.getAgileSession(ini, "AgileAP");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            changeOrder = (IChange) change;
            //Arraylist to do duplicate checking.
            list = new ArrayList<IItem>();

            logger.log("GetChange: " + changeOrder.getName());
            ITable affectedTable = changeOrder.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
            Iterator it = affectedTable.iterator();
            logger.log("建立自動變更表單..");
            autoChange = createChange();
            logger.log(1, autoChange.getName());
            while (it.hasNext()) {
                IRow row = (IRow) it.next();
                IItem part = (IItem) row.getReferent();
                logger.log("受影響的料號: " + part.getName());
                //Assuming PageThree.List02 is a list of IItem.
                String val = part.getValue(ItemConstants.ATT_PAGE_THREE_LIST02).toString();
                IItem full = (IItem) session.getObject(ItemConstants.CLASS_PART, val);
                logger.log(1, "對應的成品號碼: " + full.getName());
                addAffectedItems(full, part);
            }
            logger.log("發行自動表單...");
            releaseChange();
            logger.log("發行成功...");
            logger.close();
            new File(FILE_PATH).delete();
            return new ActionResult(ActionResult.STRING, "程式進行成功");

        } catch (APIException e) {
            logger.log("程式出錯");
            logger.close();
            try {
                ITable attachment = changeOrder.getAttachments();
                attachment.createRow(FILE_PATH);
                new File(FILE_PATH).delete();
            } catch (APIException e2) {
                e.printStackTrace();
            }
            e.printStackTrace();
            return new ActionResult(ActionResult.STRING, "程式運行失敗");

        }

    }

    /*
     * Create a New Change Order and Set Workflow.
     */
    private static IChange createChange() throws APIException {
        //Get Change Class and Autonumber
        IAgileClass objClass = admin.getAdminInstance().getAgileClass("C32-自動加入配方(客製程式使用)申請單");
        //This assumes that only one autonumber option is available.
        IAutoNumber autoNumber = objClass.getAutoNumberSources()[0];
        IChange change = (IChange) admin.createObject("C32-自動加入配方(客製程式使用)申請單", autoNumber);
        //This assumes that only one workflow is available
        change.setWorkflow(change.getWorkflows()[0]);
        return change;
    }

    /*
     * full: 成品
     * part: 配方
     */
    private static void addAffectedItems(IItem full, IItem part) throws APIException {
        ITable affectedItems = autoChange.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
        IRow affectedItemRow = null;
        logger.log("檢查" + full.getName() + "是否已在受影響的料號裏..");
        if (list.contains(full)) {
            logger.log(1, "..是");
            Iterator it = affectedItems.iterator();
            while (it.hasNext()) {
                IRow row = (IRow) it.next();
                IItem item = (IItem) row.getReferent();
                if (item.equals(full)) {
                    affectedItemRow = row;
                    break;
                }
            }
        } else {
            logger.log(1, "..否");
            admin.disableAllWarnings();
            affectedItemRow = affectedItems.createRow(full);
            admin.enableAllWarnings();
            list.add(full);
        }
        logger.log(1, "自動添加" + part.getName() + "進" + full.getName() + "的半成品裏..");
        addRedlineBOM(affectedItemRow, part);
        admin.disableWarning(new Integer(568));
        logger.log(1, "自動更新版本中..");
        autoRev(affectedItemRow);
        admin.enableWarning(new Integer(568));
    }

    private static void addRedlineBOM(IRow row, IItem part) {
        try {
            IItem full = (IItem) row.getReferent();
            ITable redlinebomTable = full.getTable(ItemConstants.TABLE_REDLINEBOM);
            redlinebomTable.createRow(part);
        } catch (APIException e) {
            logger.log(2,"配方已經在產品裏！");
        }
    }

    private static void autoRev(IRow affectedItemRow) throws APIException {
        String oldRev = (String) affectedItemRow.getValue(ChangeConstants.ATT_AFFECTED_ITEMS_OLD_REV);
        if (oldRev == "")
            affectedItemRow.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV, 1);
        else
            affectedItemRow.setValue(ChangeConstants.ATT_AFFECTED_ITEMS_NEW_REV, Integer.parseInt(oldRev) + 1);

    }

    /*
     * Assumes two status. One Pending, One Release
     */
    private static void releaseChange() throws APIException {
        admin.disableAllWarnings();
        autoChange.changeStatus(autoChange.getDefaultNextStatus(), false, null, false, false, null, null, null, false);
        admin.enableAllWarnings();
        logger.log(1, "表單發行成功");
    }
}
