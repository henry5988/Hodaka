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
public class addToListEvent implements ICustomAction {

    private static Ini ini;
    private static LogIt logger;
    private final static String FILE_PATH = "C:/Agile/Log/addToListEvent"+new
            SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime())+".txt";;
    private static IAgileSession admin;
    private static int valueLength;

    //constructor
    public static void init(){
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
    public ActionResult doAction(IAgileSession session, INode iNode, IDataObject obj) {
    	
    	init();
        IChange changeOrder;
        String result = "程式執行失敗";
        try {
            changeOrder = (IChange) obj;
            changeOrder = getChange(admin,changeOrder.getName());
            logger.log("Get Change: "+changeOrder);
            logger.log("讀取TEXT01~TEXT10欄位...若任何值為重複或空值，程式將自動退出");
            ArrayList<String> listToAdd = getTextValues(changeOrder);
            IAdminList adminList = getAgileList(session,"HODAKA_PARTS_ALL-PARTS_P3_合金碼 (共用)");
            boolean success = addToList(adminList,listToAdd);
            if(success) {
            	IStatus nextStatus = changeOrder.getDefaultNextStatus();
            	changeOrder.changeStatus(nextStatus, false, "", false, false, null, null, null, null, false);
            }
            result = success?"程式進行成功":"欄位以存在";

        } catch (APIException e) {
            logger.log(e);
        }finally{
            logger.close();
            return new ActionResult(ActionResult.STRING, result);
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
        logger.log("TEXT01 ->"+t1);
        String t2 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT02).toString().trim();
        logger.log("TEXT02 ->"+t2);
        String t3 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT03).toString().trim();
        logger.log("TEXT03 ->"+t3);
        String t4 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT04).toString().trim();
        logger.log("TEXT04 ->"+t4);
        String t5 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT05).toString().trim();
        logger.log("TEXT05 ->"+t5);
        String t6 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT06).toString().trim();
        logger.log("TEXT06 ->"+t6);
        String t7 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT07).toString().trim();
        logger.log("TEXT07 ->"+t7);
        String t8 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT08).toString().trim();
        logger.log("TEXT08 ->"+t8);
        String t9 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT09).toString().trim();
        logger.log("TEXT09 ->"+t9);
        String t10 = changeOrder.getValue(ChangeConstants.ATT_PAGE_THREE_TEXT10).toString().trim();
        logger.log("TEXT10 ->"+t10);
        
        int t1Length = t1.length();
        int t2Length = t2.length();
        int t3Length = t3.length();
        int t4Length = t4.length();
        int t5Length = t5.length();
        int t6Length = t6.length();
        int t7Length = t7.length();
        int t8Length = t8.length();
        int t9Length = t9.length();
        int t10Length = t10.length();
        
        if((t1Length>0&&t1Length!=valueLength)||(t2Length>0&&t2Length!=valueLength)
        		||(t3Length>0&&t3Length!=valueLength)||(t4Length>0&&t4Length!=valueLength)
                ||(t5Length>0&&t5Length!=valueLength)||(t6Length>0&&t6Length!=valueLength)
                ||(t7Length>0&&t7Length!=valueLength)||(t8Length>0&&t8Length!=valueLength)
                ||(t9Length>0&&t9Length!=valueLength)||(t10Length>0&&t10Length!=valueLength)){
            logger.log(1,"1~10裏有欄位長度與config指定的長度不一！");
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();

        if(t1Length>0)arrayList.add(t1);
        if(t2Length>0)arrayList.add(t2);
        if(t3Length>0)arrayList.add(t3);
        if(t4Length>0)arrayList.add(t4);
        if(t5Length>0)arrayList.add(t5);
        if(t6Length>0)arrayList.add(t6);
        if(t7Length>0)arrayList.add(t7);
        if(t8Length>0)arrayList.add(t8);
        if(t9Length>0)arrayList.add(t9);
        if(t10Length>0)arrayList.add(t10);
        
        return arrayList;
    }
    /*
        return true if added
        false if value exists or if array has problem
     */
    private boolean addToList(IAdminList list, ArrayList<String> value) throws APIException {
        if(value==null) {
        	logger.log(1,"加入List值為Null!");
        	return false;
        }
        IAgileList agileList = list.getValues();
        Object target = null;
        //add each value from arraylist into adminlist
        while(value.size()!=0) {
            String listVal = value.remove(0);
            if (agileList.getChildNode(listVal) == null) {
                logger.log(1,"將"+listVal+"新增進list裏");
                String apilistValue = listVal.trim().replaceAll("(\\W|^_)", "_");
                target = agileList.addChild(listVal, apilistValue); // List的Child指到target
				((IAgileList) target).setDescription(listVal); // target是List的一部分，更改後即更新List
				((IAgileList) target).setValue(listVal);

            }else{
                logger.log(1,listVal+"已存在!");
            }
        }
        //assign new list back to adminlist
        list.setValues(agileList);
        return true;
    }

}
