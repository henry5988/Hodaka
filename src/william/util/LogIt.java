package william.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.agile.api.CommonConstants;
import com.agile.api.IDataObject;
import com.anselm.plm.constants.AConstants;
import com.anselm.plm.util.AUtil;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class LogIt {
    private String Topic = "";
    private String SubTopic = "";
    private boolean enableTopic = true;
    private boolean enableSubTopic = true;
    private boolean nolog = false;
    private boolean enableHistory = false;
    private String identify = "";
    private long count = 0L;
    private StringBuffer history = new StringBuffer();
    private int tmpy = 0;
    private boolean hasAddedTopicPrefix = false;
    private int topicLenAdju = 25;
    private int tabcount = 0;
    private Date logStartTime = new Date();
    private Date logCloseTime = null;
    private List<String> timeRecords = new ArrayList();
    private boolean logtofile = false;
    private String logFilePath = "";
    private FileOutputStream fos = null;
    private OutputStreamWriter osw = null;
    private String[] SeparatorBarChars = new String[]{"▅", "═", "█"};

    public LogIt() {
    }

    public LogIt(String topic) {
        this.init(topic, "", true);
    }

    public LogIt(String topic, String subtopic) {
        this.init(topic, subtopic, true);
    }

    public LogIt(String topic, String subtopic, boolean enableStartMsg) {
        this.init(topic, subtopic, enableStartMsg);
    }

    public void init(String topic, String subtopic, boolean enableStartMsg) {
        this.Topic = topic;
        this.SubTopic = subtopic;
        this.identify = AUtil.getRandom(2);
        if(enableStartMsg) {
            this.logStartMsg();
        }

    }

    public String getTopic() {
        return this.Topic;
    }

    public String getSubTopic() {
        return this.SubTopic;
    }

    public StringBuffer getHistory() {
        return this.history;
    }

    public void close(){
        this.history = null;
        this.Topic = null;
        this.SubTopic = null;
        this.identify = null;
        try{
            this.osw.close();
        }catch(IOException e){
            System.out.println("osw is null or already closed");
        }
    }

    public void setTopic(String topic) {
        this.Topic = topic;
    }

    public void addTopicPrefix(Object prefix) {
        if(!this.hasAddedTopicPrefix && prefix != null) {
            this.Topic = prefix + " " + this.Topic;
        }

        this.hasAddedTopicPrefix = true;
    }

    public void setLogFile(String filepath, boolean append) throws IOException {
        this.fos = new FileOutputStream(filepath, append);
        this.osw = new OutputStreamWriter(this.fos, "UTF-8");
        this.logtofile = true;
        this.logFilePath = filepath;
    }

    public void setLogFileWithDateInFileName(String fileFolderPath, String logname, boolean append) throws IOException {
        GregorianCalendar calendar = new GregorianCalendar();
        this.setLogFile(fileFolderPath + (fileFolderPath.endsWith("\\")?"":"\\") + logname + "_" + (calendar.get(2) + 1) + "_" + calendar.get(5) + ".log", append);
        this.log();
        this.log();
        this.log((Object)this.getCurrentTimeString());
    }

    public void setLogFileWithDateTimeInFileName(String fileFolderPath, String logname, boolean append) throws IOException {
        GregorianCalendar calendar = new GregorianCalendar();
        this.setLogFile(fileFolderPath + (fileFolderPath.endsWith("\\")?"":"\\") + logname + "_" + (calendar.get(2) + 1) + "_" + calendar.get(5) + "_" + calendar.get(11) + "_" + calendar.get(12) + "_" + calendar.get(13) + ".log", append);
        this.log();
        this.log();
        this.log((Object)this.getCurrentTimeString());
    }

    public File getLogFile() {
        return new File(this.logFilePath);
    }

    public void setLogFile(String filepath) throws IOException {
        this.setLogFile(filepath, true);
    }

    private void logStartMsg() {
        this.logHeading(this.Topic);
    }

    public void disableTopic() {
        this.enableTopic = false;
        this.enableSubTopic = false;
    }

    public void enableTopic() {
        this.enableTopic = true;
        this.enableSubTopic = true;
    }

    public void nolog() {
        this.nolog = true;
    }

    public void enableHistory() {
        this.enableHistory = true;
    }

    public void disableHistory() {
        this.enableHistory = false;
    }

    public void setTabPrefix(int xd) {
        this.tabcount = xd;
    }

    public void clearTabPrefix() {
        this.tabcount = 0;
    }

    public void log() {
        this.log((Object)"");
    }

    public void log(Object log) {
        this.log(this.tabcount, log, "");
    }

    public void log(int xd, Object log) {
        this.log(xd, log, "");
    }

    public void log(int xd, Object log, String prefix) {
        ++this.count;
        if(!this.nolog) {
            for(int tmp = 0; tmp < xd + this.tabcount; ++tmp) {
                prefix = "\t|" + prefix;
            }

            String[] var8 = ("" + log).split("\n");

            for(int i = 0; i < var8.length; ++i) {
                if(i != 0 || !("" + (log == null?"":log)).startsWith("\n")) {
                    log = (this.enableTopic && this.Topic.length() > 0?this.lengthAdjustment("[" + this.Topic) + "#" + this.identify + "-" + this.linenum() + "] ":"") + prefix + (this.enableSubTopic && this.SubTopic.length() > 0?"(" + this.SubTopic + ") ":"") + var8[i];
                    System.out.println(log);
                    if(this.logtofile) {
                        try {
                            this.osw.append("\n" + log);
                            this.osw.flush();
                        } catch (Exception var7) {
                            ;
                        }
                    }

                    if(this.enableHistory) {
                        this.history.append(log + "\n");
                    }
                }
            }

        }
    }

    public String lengthAdjustment(String str) {
        return this.lengthAdjustment(str, this.topicLenAdju, ".");
    }

    public String lengthAdjustment(String str, int len, String c) {
        this.tmpy = len - str.length();

        for(int i = 0; i < this.tmpy; ++i) {
            str = str + c;
        }

        return str;
    }

    public void logHeading(Object log) {
        this.logHeading(0, log);
    }

    public void logHeading(int xd, Object log) {
        char[] cr = ("" + log).toCharArray();
        int twolen = 0;

        for(int xxx = 0; xxx < cr.length; ++xxx) {
            if(("" + cr[xxx]).getBytes().length > 1) {
                ++twolen;
            }
        }

        if((("" + log).length() + twolen) % 2 > 0) {
            log = log + " ";
            ++twolen;
        }

        String var7 = "";

        for(int i = 0; i < (("" + log).length() + twolen) / 2; ++i) {
            var7 = var7 + "═";
        }

        this.log(xd, (Object)("╔" + var7 + "╗"));
        this.log(xd, (Object)("║" + log + "║"));
        this.log(xd, (Object)("╚" + var7 + "╝"));
    }

    public void log(Map<?, ?> map) {
        this.log(0, (String)"", (Map)map);
    }

    public void log(int xd, Map<?, ?> map) {
        this.log(xd, "", map);
    }

    public void log(String name, Map<?, ?> map) {
        this.log(0, name, (Map)map, "");
    }

    public void log(int xd, String name, Map<?, ?> map) {
        this.log(xd, name, map, "");
    }

    public void log(int xd, String name, Map<?, ?> map, String prefix) {
        if(map == null) {
            map = new HashMap();
        }

        Iterator itr = ((Map)map).entrySet().iterator();
        this.log(xd, (Object)(prefix + "╔[" + name + "]"));
        int len = 0;

        Map.Entry entry;
        while(itr.hasNext()) {
            entry = (Map.Entry)itr.next();
            if(("" + entry.getKey()).length() > len) {
                len = ("" + entry.getKey()).length();
            }
        }

        itr = ((Map)map).entrySet().iterator();

        while(itr.hasNext()) {
            entry = (Map.Entry)itr.next();
            this.log(xd, (Object)(prefix + "║ " + this.cramLeft("" + entry.getKey(), " ", len) + " == " + this.getArrayToString(entry.getValue())));
        }

        this.log(xd, (Object)(prefix + "╚══════════"));
    }

    public void log(List<?> list) {
        this.log(0, (String)"", (List)list);
    }

    public void log(int xd, List<?> list) {
        this.log(xd, "", list);
    }

    public void log(String name, List<?> list) {
        this.log(0, name, (List)list, "");
    }

    public void log(int xd, String name, List<?> list) {
        this.log(xd, name, list, "");
    }

    public void log(int xd, String name, List<?> list, String prefix) {
        this.log(xd, name, list.toArray(), prefix);
    }

    public void log(Object[] array) {
        this.log(0, (String)"", (Object[])array);
    }

    public void log(int xd, Object[] array) {
        this.log(xd, "", array);
    }

    public void log(String name, Object[] array) {
        this.log(0, name, (Object[])array, "");
    }

    public void log(int xd, String name, Object[] array) {
        this.log(xd, name, (Object[])array, (String)null);
    }

    public void log(int xd, String name, Object[] array, String prefix) {
        if(array == null) {
            array = new Object[0];
        }

        if(prefix == null) {
            prefix = name;
        }

        String tmp = "══════════════════════════════";
        this.log(xd, (Object)("[" + name + "]"));
        this.log(xd, (Object)(prefix + "\t╔══" + tmp));
        if(array.length > 0) {
            for(int i = 0; i < array.length; ++i) {
                this.log(xd, (Object)(prefix + "\t║Array[" + i + "]\t= " + array[i]));
            }
        } else {
            this.log(xd, (Object)(prefix + "\t║Array is empty"));
        }

        this.log(xd, (Object)(prefix + "\t╚══" + tmp));
    }

    public void logException(Exception e) {
        this.logException(0, e, "");
    }

    public void logException(Exception e, String filter) {
        this.logException(0, e, filter);
    }

    public void logException(int xd, Exception e) {
        this.logException(xd, e, "");
    }

    public void logException(int xd, Exception e, String filter) {
        this.log(xd, (Object)("■[Exception] " + (filter.length() > 0?"(" + filter + ") ":"") + e.getMessage()));
        StackTraceElement[] stackElements = e.getStackTrace();
        StackTraceElement[] var8 = stackElements;
        int var7 = stackElements.length;

        for(int var6 = 0; var6 < var7; ++var6) {
            StackTraceElement em = var8[var6];
            if(em.getClassName().startsWith(filter)) {
                this.log(xd + 1, (Object)(em.getClassName() + ":" + em.getLineNumber() + " (/" + em.getMethodName() + ")"));
            }
        }

    }
    public String logTimeRecord(int xd, String recordName, long sa1, long sa2) {
        String t = this.lengthAdjustment("◇[" + recordName, 25, " ") + "]\t" + com.anselm.plm.utilobj.Timer.getTimeDiff(sa1, sa2);
        this.log(xd, (Object)t);
        this.timeRecords.add(t);
        return t;
    }

    public void logTimeSummary() {
        this.clearTabPrefix();
        if(this.logCloseTime == null) {
            this.logCloseTime = new Date();
        }

        String xxx = "[Time Summary]";
        String xxx1 = "══" + xxx + "════════════════";
        String xxx2 = "══" + xxx + "════════════════";
        this.log((Object)("╔" + xxx1 + "╗"));
        Iterator var5 = this.timeRecords.iterator();

        while(var5.hasNext()) {
            String t = (String)var5.next();
            this.log((Object)("║" + t));
        }

        this.log((Object)("║◆Time Usage:\t" + com.anselm.plm.utilobj.Timer.getTimeDiff(this.logStartTime.getTime(), this.logCloseTime.getTime())));
        this.log((Object)("╚" + xxx2 + "╝"));
    }

    public String getCurrentTimeString() {
        return AConstants.SYS_DATE_FORMAT.format(new Date());
    }

    private String getArrayToString(Object object) {
        String tmp = "";

        try {
            Object[] e = (Object[])object;

            for(int i = 0; i < e.length; ++i) {
                tmp = tmp + (i > 0?",":"") + e[i];
            }
        } catch (Exception var5) {
            tmp = tmp + object;
        }

        return tmp;
    }

    private String linenum() {
        byte length = 3;
        String linenum = "" + this.count;
        int gap = length - linenum.length();

        for(int i = 0; i < gap; ++i) {
            linenum = "0" + linenum;
        }

        return linenum;
    }

    private String cramLeft(String str, String fill, int length) {
        int gap = length - str.length();

        for(int i = 0; i < gap; ++i) {
            str = fill + str;
        }

        return str;
    }

    public void logSeparatorBar() {
        this.logSeparatorBar(0);
    }

    public void logSeparatorBar(int x) {
        String SeparatorBar = "";

        for(int i = 0; i < 100; ++i) {
            SeparatorBar = SeparatorBar + this.SeparatorBarChars[x >= this.SeparatorBarChars.length - 1?x:0];
        }

        this.logSeparatorBar(SeparatorBar);
    }

    private void logSeparatorBar(String SeparatorBar) {
        SeparatorBar = SeparatorBar;
        System.out.println(SeparatorBar);
        if(this.logtofile) {
            try {
                this.osw.append("\n" + SeparatorBar);
                this.osw.flush();
            } catch (Exception var3) {
                ;
            }
        }

        if(this.enableHistory) {
            this.history.append(SeparatorBar + "\n");
        }

    }

    public String getLogFilePath() {
        return this.logFilePath;
    }

    public void deleteLogFile() {
        if(this.logtofile) {
            try {
                (new File(this.logFilePath)).delete();
            } catch (Exception var2) {
                var2.printStackTrace();
            }

            this.logtofile = false;
        }

    }

    public void uploadLogFile(IDataObject ido, Exception e) {
        if(!this.logtofile) {
            this.log((Object)"logtofile is false.");
        }

        try {
            this.log((Object)">>uploadLogFile...");

            try {
                if(e != null) {
                    this.log((Object)e);
                }

                this.log((Object)("upload log file (" + this.getLogFilePath() + ") to " + ido));
                ido.getTable(CommonConstants.TABLE_ATTACHMENTS).createRow(this.getLogFilePath());
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            this.deleteLogFile();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }
}
