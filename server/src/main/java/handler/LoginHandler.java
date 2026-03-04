package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;

import service.AlreadyTakenException;
import service.UnauthorizedUserException;
import service.UserService;
import requests.LoginRequest;
import results.LoginResult;

public class LoginHandler {

    private final UserService userService;

    public LoginHandler(UserService loginService) {
        this.userService = loginService;
    }

    public void handle(Context ctx) {

        //convert json form to our request form
        LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class); //copying the pet shop deserialization

        //throw new BadRequestResponse("Error: bad request");
        if(request.username() == null | request.password() == null){
            ctx.status(400);
            ctx.result(new Gson().toJson(new LoginResult("message", "Error: bad request", null, null)));
            return; //needed this to stop the execution of other parts
        }

        //call service
        try{ //errors will be caught at the handler because they will be serialized here
            LoginResult result = userService.login(request);
            //convert result to json and return
            ctx.status(200);
            ctx.result(new Gson().toJson(result));

        }
        catch (UnauthorizedUserException exception){
            ctx.status(401);
            ctx.result(new Gson().toJson(new LoginResult("message", exception.getMessage(), null, null)));
        }

    }
}

