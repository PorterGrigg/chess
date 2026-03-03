package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;

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
        RegisterResult result = userService.register(request);

        //convert result to json and return
        ctx.status(200);
        ctx.result(new Gson().toJson(result));
    }
}
