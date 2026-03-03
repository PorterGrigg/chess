package handler;

import com.google.gson.Gson;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

import service.AlreadyTakenException;
import service.UserService;
import requests.RegisterRequest;
import results.RegisterResult;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService registerService) {
        this.userService = registerService;
    }

    public void handle(Context ctx) {

        //convert json form to our request form
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class); //copying the pet shop deserialization

        //call service
        try{ //errors will be caught at the handler because they will be serialized here
            RegisterResult result = userService.register(request);
            //convert result to json and return
            ctx.status(200);
            ctx.result(new Gson().toJson(result));

        }
        catch (AlreadyTakenException exception){
            ctx.status(403);
            ctx.result(new Gson().toJson(new RegisterResult("message", exception.getMessage(), null, null)));
        }

        //check for bad request at the end because this will be last thing returned
        if(request.username() == null | request.password() == null | request.email() == null){
            //throw new BadRequestResponse("Error: bad request");
            ctx.status(400);
            ctx.result(new Gson().toJson(new RegisterResult("message", "Error: bad request", null, null)));
        }

    }
}
