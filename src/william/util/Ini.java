package william.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Ini {
    public static final String INI_FILE_PATH = "C:\\Agile\\Config.ini";
    final String SKIP_PREFIX = "##";
    final String KeyDelimiter= "##";
    Map<String, String> Parameter = new HashMap<String, String>();
    public boolean IsPackageIni = false;
    public String IniFilePath = "";

    public Ini(){
        init(INI_FILE_PATH);
    }

    public Ini(String SRC_INI) {init(SRC_INI);}

    private void init(String SRC_INI){
        File file = new File(SRC_INI);
        try{
            System.out.println(file.getAbsolutePath());
        }catch(Exception e){}
        init(file);
    }

    private void init(File src_ini){
//		Calendar calendar = new GregorianCalendar();
//    	if(calendar.get(Calendar.YEAR)>2016&&(new GregorianCalendar()).getTimeInMillis()%3==0)return;
        if(src_ini==null||!src_ini.exists()||!src_ini.isFile()){
            System.out.println("## The specified ini: "+src_ini+"==>不存在!");
            //若指定的設定檔不存在，自動使用預設的設定檔路俓(AConstants.INI_FILE_PATH)
            src_ini=new File("Config.ini");
            System.out.println("## Using default ini: Config.ini"+
                    ((src_ini==null||!src_ini.exists()||!src_ini.isFile())?"==>不存在!":""));
            IsPackageIni=true;
        }
        //this.IniFilePath=IsPackageIni?("(package)"+AConstants.INI_FILE_PATH):src_ini.getPath();
        this.IniFilePath=src_ini.getPath();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(src_ini));

            String category = "$";
            while (in.ready()) {
                String str = in.readLine();

                if(str.trim().startsWith(SKIP_PREFIX))continue;
                str=str.split(SKIP_PREFIX)[0].trim();

                if (str.length()==0) {
                    // do nothing
                }else if(str.startsWith("[") && str.endsWith("]")){
                    category=str.substring(1,str.length()-1);
                }else {
                    if(str.indexOf("=")>0){
                        String[] content = new String[2];
                        content[0]=str.substring(0,str.indexOf("="));
                        content[1]=str.substring(str.indexOf("=")+1);
                        Parameter.put(category+KeyDelimiter+content[0],content[1]);
                    }//else Parameter.put(category+KeyDelimiter,str);
                }
            }
        } catch (IOException e) {
            System.err.println("Catch IOException:" + e.getMessage());
        } finally{try{in.close();}catch(Exception e){}}
    }

    public void printParameters(){
        Iterator<Entry<String, String>> itr = Parameter.entrySet().iterator();
        for (int i = 0; i < Parameter.size(); i++) {
            Entry<String,String> entry = (Entry<String,String>) itr.next();
            String key = (String)entry.getKey();
            String content = (String)entry.getValue();
            key=key.replaceAll(KeyDelimiter,".");
            System.out.println(key+"="+content);
        }
    }

    public String getValue(String category, String parameter){
        return (String) Parameter.get(category+KeyDelimiter+parameter);
    }

}
