package Test;

import com.agile.api.*;

public class Sample extends ServerInfo {
    public static void main(String[] args) throws APIException{
        IAgileSession session = login();
        getCurrentUser(session);
        IChange ECO = getECO(session,"CHANGENUMBER");
        IChange ECR= getECR(session,"ECRNUMBER");
        IItem item = getItem(session,"ITEMNUMBER");
        ITable affTab = getAffectedTab(ECO);
    }
}
