package Test;

import com.agile.api.*;
import static Test.Utils.*;

public class Sample{
    public static void main(String[] args) throws APIException{
        IAgileSession session = login();
        getCurrentUser(session);
//        IChange ECO = getECO(session,"CHANGENUMBER");
//        IChange ECR= getECR(session,"ECRNUMBER");
//        IItem item = getItem(session,"ITEMNUMBER");
//        ITable affTab = getAffectedTable(ECO);
//        ITable BOM = getBOMTable(item);
//        ITable RLBOM = getRLBOMTable(item);
    }
}
