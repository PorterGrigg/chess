package server;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;

import io.javalin.*;
import handler.ClearHandler;
import service.ClearService;

public class Server {

    private final Javalin httpHandler;

    private final ClearService clearService;

    public Server() {
        //initialize the DAOs (which are memory here)
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        //initialize the services
        this.clearService = new ClearService(authDAO, userDAO, gameDAO);

        //initialize the handlers
        ClearHandler clearHandler = new ClearHandler(clearService);

        //define handling
        httpHandler = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", this::clearAll);

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

    private void clearAll(Context ctx){
        clearService.clearAll();
        ctx.status(200);
    }
}
