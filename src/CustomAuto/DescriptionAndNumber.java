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
        if(!description.statusInConfig((IChange) change))return new ActionResult(ActionResult.STRING,"Config檔未指定此站別為可執行站別！");
        description.action(changeOrder);
        Number number = new Number();
        number.action(changeOrder);
        int errorCount = description.getErrorCount() + number.getErrorCount();
        description.resetCount();
        number.resetCount();
        String result = errorCount ==0?"程式執行成功": "執行自動編碼/描述總共有"+errorCount +"筆條件失敗，請檢查log檔";
        return new ActionResult(ActionResult.STRING,result);
    }
}
