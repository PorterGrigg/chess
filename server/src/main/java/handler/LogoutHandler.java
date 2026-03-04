package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;

import service.AlreadyTakenException;
import service.UnauthorizedUserException;
import service.UserService;
import requests.LogoutRequest;
import results.LogoutResult;

public class LogoutHandler {

    private final UserService userService;

    public LogoutHandler(UserService logoutService) {
        this.userService = logoutService;
    }

    public void handle(Context ctx) {

        //convert json form to our request form
        LogoutRequest request = new Gson().fromJson(ctx.body(), LogoutRequest.class); //copying the pet shop deserialization

        //throw new BadRequestResponse("Error: bad request");
        if(request.authToken() == null){
            ctx.status(400);
            ctx.result(new Gson().toJson(new LogoutResult("message", "Error: bad request")));
            return;
        }

        //call service
        try{ //errors will be caught at the handler because they will be serialized here
            LogoutResult result = userService.logout(request);
            //convert result to json and return
            ctx.status(200);
            ctx.result(new Gson().toJson(result));

        }
        catch (UnauthorizedUserException exception){
            ctx.status(401);
            ctx.result(new Gson().toJson(new LogoutResult("message", exception.getMessage())));
        }

    }
}

