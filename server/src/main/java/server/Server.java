package server;

import dataAccess.*; //the .* imports the entire package
import handler.*;
import service.*;

import io.javalin.*;


public class Server {

    private final Javalin httpHandler;

    private final ClearService clearService;

    private final ClearHandler clearHandler;

    public Server() {

        //this is where it is specified if the DAOs are memory or if they are SQL database
        //initialize the DAOs (which are memory here)
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        //initialize the DAOs (which are SQL database access here)
//        AuthDAO authDAO = new SQLAuthDAO();
//        UserDAO userDAO = new SQLUserDAO();
//        GameDAO gameDAO = new SQLGameDAO();

        //initialize the services
        this.clearService = new ClearService(authDAO, userDAO, gameDAO);

        //initialize the handlers
        this.clearHandler = new ClearHandler(clearService);

        //define handling
        httpHandler = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", clearHandler::handle);

    }

    public int run(int desiredPort) {
        httpHandler.start(desiredPort);
        return httpHandler.port();
    }

    public int port() {
        return httpHandler.port();
    }

    public void stop() {
        httpHandler.stop();
    }

}
