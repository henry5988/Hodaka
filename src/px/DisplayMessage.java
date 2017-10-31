package px;

import com.agile.api.IAgileSession;
import com.agile.api.IDataObject;
import com.agile.api.INode;
import com.agile.px.ActionResult;
import com.agile.px.ICustomAction;

/**
 * Created by user on 10/26/2017.
 */
public class DisplayMessage implements ICustomAction{
    @Override
    public ActionResult doAction(IAgileSession iAgileSession, INode iNode, IDataObject iDataObject) {
        String html =                 "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border: 5px solid black;\n" +
                "    border-collapse: collapse;\n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <caption>Memerdogger</caption>\n" +
                "  <tr>\n" +
                "    <th>Month</th>\n" +
                "    <th>Savings</th>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>January</td>\n" +
                "    <td>$100</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>February</td>\n" +
                "    <td>$50</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n";
                

        return new ActionResult(ActionResult.STRING,html);
    }
}
