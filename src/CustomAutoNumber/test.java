package CustomAutoNumber;

import william.util.Ini;

public class test {
    static Ini ini = new Ini();
    static String bbb= ini.getValue("File Location","EXCEL_FILE_PATH");
    public static void main(String[] args) {
        String lol = "~abc123";
        String b = lol.replaceAll
                ("[0-9]", "");
        String c = b.replace("~","");
        System.out.println("【10】CPU".equals("[10]CPU"));
//        Ini ini = new Ini();
//        String bbb= ini.getValue("File Location","EXCEL_FILE_PATH");
        System.out.println(bbb);
    }
}
