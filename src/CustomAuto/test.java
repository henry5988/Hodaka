package CustomAuto;

import com.agile.api.*;
import william.util.Ini;

import java.util.Iterator;

import static Test.Utils.login;

public class test {
    static Ini ini = new Ini();
    static String bbb= ini.getValue("File Location","EXCEL_FILE_PATH");
    public static void main(String[] args) throws APIException {
//        String lol = "~abc";
//        String b = lol.replaceAll
//                ("[0-9]", "");
//        String c = b.replace("~","");
//        System.out.println("【10】CPU".equals("[10]CPU"));
////        Ini ini = new Ini();
////        String bbb= ini.getValue("File Location","EXCEL_FILE_PATH");
//        int length = Integer.parseInt(c.replaceAll
//                ("[^0-9]", ""));
//        System.out.println(length);
//        IAgileSession session = login();
//        IQuery query = (IQuery) session.createObject(IQuery.OBJECT_TYPE,
//                "10CPU");
//        String criteria = "["+ ItemConstants.ATT_TITLE_BLOCK_NUMBER+"] " +
//                "Contains 10014252";
//        query.setCriteria(criteria);
//        ITable table = query.execute();
//        Iterator it  = table.iterator();
//        while(it.hasNext()){
//            System.out.println(((IRow)it.next()).getReferent().toString());
//        }
//        System.out.println(table.size());
//        int i = 0;
//        while(i<10){
//            i++;
//            if(i==5){
//                continue;
//            }
//            System.out.println(i);
//        }
//        String a ="";
//        for (int i = 0; i < 10; i++) {
//            a += 0;
//        }
//        System.out.println(a);
//        String a = "aaabbb";
//        String aa = a.split("\\|")[0];
//        String bb = a.split("\\|")[1];
//        System.out.println(aa);
//        System.out.println(bb);
          String a = "a b c";
          a=a.replaceAll("\\s","");
        System.out.println(a);


    }
}
