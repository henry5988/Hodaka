package CustomAutoNumber;

import com.agile.api.IAgileSession;
import com.agile.api.IDataObject;
import com.agile.api.INode;
import com.agile.px.ActionResult;
import com.agile.px.ICustomAction;

//Author: William

public class AutoNumberPX implements ICustomAction {
    @Override
    public ActionResult doAction(IAgileSession session,
                                 INode node,
                                 IDataObject change) {

        return new ActionResult(ActionResult.STRING,"Success");
    }
}
