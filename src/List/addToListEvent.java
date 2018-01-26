package List;

import com.agile.api.*;
import com.agile.px.*;
import william.util.Ini;
import william.util.LogIt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static common.Utils.*;


/*
  Event Trigger: Status Change
  Adds all the value from TEXT01 to TEXT10
  If any of the value already exist in list, exit and reset to original status
 */
public class addToListEvent implements IEventAction {

    private Ini ini;
    private LogIt logger;
    private final String FILE_PATH = "C:/Agile/Log/addToListEvent"+new
            SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime())+".txt";;
    private IAgileSession admin;
    private int valueLength;

    //constructor
    public addToListEvent(){
        try {
            ini = new Ini("C:/Agile/Config.ini");
            logger = new LogIt("addToListEvent");
            logger.setLogFile(FILE_PATH);
            admin = getAgileSession(ini,"AgileAP");
            valueLength = Integer.parseInt(ini.getValue("List","Length"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public EventActionResult doAction(IAgileSession session, INode iNode, IEventInfo event) {
        IWFChangeStatusEventInfo info = (IWFChangeStatusEventInfo) event;
        IChange changeOrder;
        String result = "程式執行失敗";
        try {
            changeOrder = (IChange) info.getDataObject();
            changeOrder = getChange(admin,changeOrder.getName());
            logger.log("Get Change: "+changeOrder);
            logger.log("讀取TEXT01~TEXT10欄位...若任何值為重複或空值，程式將自動退出");
            ArrayList<String> listToAdd = getTextValues(changeOrder);
            IAdminList adminList = getAgileList(session,"合金碼");
            boolean success = addToList(adminList,listToAdd);
            if(!success) {
                logger.log("由於出錯，程式進不了下一站");
                resetStatus(changeOrder, session,logger);
            }
            result = success?"程式進行成功":"欄位以存在";

        } catch (APIException e) {
            logger.log(e);
        }finally{
            logger.close();
            return new EventActionResult(event, new ActionResult(ActionResult.STRING, result));
        }
    }

//    private IAdminList getAgileList(IAgileSession session) throws APIException {
//        IAdmin admin = session.getAdminInstance();
//        IListLibrary listLib = admin.getListLibrary();
//        IAdminList adminList = listLib.getAdminList("合金碼");
//        return adminList;
//    }

    /*
      必填欄位
     */
    private ArrayList getTextValues(IChange changeOrder) throws APIException {
        String t1 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT01).toString().trim();
        String t2 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT02).toString().trim();
        String t3 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT03).toString().trim();
        String t4 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT04).toString().trim();
        String t5 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT05).toString().trim();
        String t6 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT06).toString().trim();
        String t7 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT07).toString().trim();
        String t8 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT08).toString().trim();
        String t9 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT09).toString().trim();
        String t10 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT10).toString().trim();
        if(t1.length()!=valueLength||t2.length()!=valueLength||t3.length()!=valueLength||t4.length()!=valueLength
                ||t5.length()!=valueLength||t6.length()!=valueLength||t7.length()!=valueLength||t8.length()!=valueLength
                ||t9.length()!=valueLength||t10.length()!=valueLength){
            logger.log(1,"1~10裏有欄位長度與config指定的長度不一！");
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(t1);arrayList.add(t2);arrayList.add(t3);arrayList.add(t4);arrayList.add(t5);
        arrayList.add(t6);arrayList.add(t7);arrayList.add(t8);arrayList.add(t9);arrayList.add(t10);
        //if any are empty. fail it
        if(arrayList.contains("")){
            logger.log(1,"1~10裏有欄位是空值！");
            return null;
        }
        return arrayList;
    }
    /*
        return true if added
        false if value exists or if array has problem
     */
    private boolean addToList(IAdminList list, ArrayList<String> value) throws APIException {
        if(value==null)return false;
        IAgileList agileList = list.getValues();
        //add each value from arraylist into adminlist
        while(value.size()!=0) {
            String listVal = value.remove(0);
            if (agileList.getChildNode(listVal) == null) {
                logger.log(1,"將"+listVal+"新增進list裏");
                agileList.addChild(listVal);
                IAgileList node = (IAgileList) agileList.getChild(listVal);
                node.setDescription("|"+listVal+"|"+listVal);
            }else{
                logger.log(1,listVal+"已存在,程式即將退出!");
                return false;
            }
        }
        //assign new list back to adminlist
        list.setValues(agileList);
        return true;
    }

}
