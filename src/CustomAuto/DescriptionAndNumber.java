package CustomAuto;

import com.agile.api.IAgileSession;
import com.agile.api.IChange;
import com.agile.api.IDataObject;
import com.agile.api.INode;
import com.agile.px.ActionResult;
import com.agile.px.ICustomAction;

public class DescriptionAndNumber implements ICustomAction {
    @Override
    public ActionResult doAction(IAgileSession iAgileSession, INode iNode, IDataObject change) {
        IChange changeOrder = (IChange) change;
        Description description = new Description();
        Number number = new Number();
        description.action(changeOrder);
        number.action(changeOrder);
        int errorCount = description.getErrorCount() + number.getErrorCount();
        description.resetCount();
        number.resetCount();
        String result = errorCount ==0?"程式執行成功": "執行自動編碼/描述總共有"+errorCount +"筆物件失敗，請檢查log檔";
        return new ActionResult(ActionResult.STRING,result);
    }
}
