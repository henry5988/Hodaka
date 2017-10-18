package Test;

import com.agile.api.APIException;
import com.agile.api.AgileSessionFactory;
import com.agile.api.IAgileSession;
import com.agile.api.IUser;

import java.util.HashMap;

/**
 * Created by user on 10/18/2017.
 */
public class ServerInfo {
    // Update the following fields with information for your server
    final static String connectString = "http://plmdemo/Agile/";
    final static String username = "admin";
    final static String password = "agile935";
    public static AgileSessionFactory m_factory;

    public static IAgileSession login(String username, String password, String connectString) throws APIException {

        // Create the params variable to hold login parameters
        HashMap params = new HashMap();

        // Put username and password values into params
        params.put(AgileSessionFactory.USERNAME, username);
        params.put(AgileSessionFactory.PASSWORD, password);

        // Get an Agile server instance. ("agileserver" is the name of the Agile
        // proxy server,
        // and "virtualPath" is the name of the virtual path used for the Agile
        // system.)
        m_factory = AgileSessionFactory.getInstance(connectString);

        // Create the Agile PLM session and log in
        return m_factory.createSession(params);

    }
    public static String getCurrentUser(IAgileSession session) throws APIException {
        IUser currentUser = session.getCurrentUser();
        String userName = currentUser.getName();

        System.out.println("Logged in user: " + userName);
        return userName;
    }

}
