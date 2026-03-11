package server;

import dataaccess.*; //the .* imports the entire package
import handler.*;
import service.*;

import io.javalin.*;

import static io.javalin.apibuilder.ApiBuilder.post;


public class Server {

    private final Javalin httpHandler;

    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    private final ClearHandler clearHandler;
    private final RegisterHandler registerHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateHandler createHandler;
    private final JoinHandler joinHandler;
    private final ListHandler listHandler;

    public Server() {

        //this is where it is specified if the DAOs are memory or if they are SQL database
        //initialize the DAOs (which are memory here)
        try {
            AuthDAO authDAO = new SQLAuthDAO();
            UserDAO userDAO = new SQLUserDAO();
            GameDAO gameDAO = new SQLGameDAO();
        } catch(DataAccessException ex) {
            //what is done with this exception?
        }

        //initialize the DAOs (which are SQL database access here)

        //initialize the services
        this.clearService = new ClearService(authDAO, userDAO, gameDAO);
        this.userService = new UserService(authDAO, userDAO);
        this.gameService = new GameService(authDAO, gameDAO);

        //initialize the handlers
        this.clearHandler = new ClearHandler(clearService);
        this.registerHandler = new RegisterHandler(userService);
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);
        this.listHandler = new ListHandler(gameService);
        this.createHandler = new CreateHandler(gameService);
        this.joinHandler = new JoinHandler(gameService);


        //redirect requests to their respective handlers
        httpHandler = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/db", clearHandler::handle)
            .post("/user", registerHandler::handle)
            .post("/session", loginHandler::handle)
            .delete("/session", logoutHandler::handle)
            .get("/game", listHandler::handle)
            .post("/game", createHandler::handle)
            .put("/game", joinHandler::handle);

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
