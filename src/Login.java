// =======================================================================================================================================
// Copyright © 2017, Oracle and/or its affiliates. All rights reserved.
//
// This software and related documentation are provided under a license agreement containing restrictions on use and disclosure and are protected by intellectual
// property laws. Except as expressly permitted in your license agreement or allowed by law, you may not use, copy, reproduce, translate, broadcast, modify,
// license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or decompilation of
// this software, unless required by law for interoperability, is prohibited.
//
// The Sample Code is for illustrative purposes only. This Sample code is provided ‘AS-IS’ and provided without Warranty of any kind. Sample code are not
// covered by STANDARD SUPPORT SERVICES.
//
// Oracle is a registered trademark of Oracle Corporation and/or its affiliates. Other names may be trademarks of their respective owners.
// =======================================================================================================================================

/**
 * This sample demonstrates:
 * 1. Login to Agile server;
 * 2. Get the logged in user name from server.
 */
import com.agile.api.*;
import java.util.*;

import static sun.misc.Version.print;


public class Login {
    private static final String COMMAND_NAME = "Login";

    public static IAgileSession       session  = null;
    public static AgileSessionFactory factory;

    public static void main(String[] args) {
        checkArguments(args);

        try {
            // Create an IAgileSession instance
            session = connect(args);
            // Get the current user name
            String userName = getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * <p> Create an IAgileSession instance </p>
     * @param args command line arguments
     *
     * @return IAgileSession
     * @throws APIException
     */
    private static IAgileSession connect(String[] args) throws APIException {
        factory = AgileSessionFactory.getInstance(args[0]);
        HashMap params = new HashMap();

        params.put(AgileSessionFactory.USERNAME, args[1]);
        params.put(AgileSessionFactory.PASSWORD, args[2]);
        session = factory.createSession(params);
        return session;
    }

    /**
     * <p> Get the current user name </p>
     *
     * @return String
     * @throws APIException
     */
    private static String getCurrentUser() throws APIException {
        IUser  currentUser = session.getCurrentUser();
        String userName = currentUser.getName();

        System.out.println("Logged in user: " + userName);
        return userName;
    }

    /**
     * This sample can be configured by passing server url, user name and
     * password as program arguments in the same order. This method checks for
     * these values.
     *
     * @param args
     */
    private static void checkArguments(String[] args) {
        if (args.length != 3) {
            // should pass arguments through the command line
            printUsage();
            System.exit(-1);
        }
    }

    /**
     * print usage message to the standard error
     */
    private static void printUsage() {
        System.err.println("Usage: " + COMMAND_NAME + " server_url user_name password");
        System.err.println("\t" + "server_url: the server URL");
        System.err.println("\t" + "user_name: user name");
        System.err.println("\t" + "password: password");
    }
}
