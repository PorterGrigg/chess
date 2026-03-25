package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;


import results.ErrorResult;
import service.GameService;
import service.UnauthorizedUserException;
import requests.CreateRequest;
import results.CreateResult;

public class CreateHandler {

    private final GameService gameService;

    public CreateHandler(GameService createService) {
        this.gameService = createService;
    }

    public void handle(Context ctx) {

//        System.out.println("RAW JSON BODY: " + ctx.body());
//        System.out.println("AUTH HEADER: " + ctx.header("Authorization"));

        //convert json form to our request form
        String authToken = ctx.header("Authorization");

        CreateRequest partialRequest = new Gson().fromJson(ctx.body(), CreateRequest.class); //deserialize the body
        String gameName = partialRequest.gameName();

        CreateRequest request = new CreateRequest(authToken, gameName); //combine data extracted from the header and body

        //throw new BadRequestResponse("Error: bad request");
        if(request.authToken() == null | request.gameName() == null){
            ctx.status(400);
            ctx.result(new Gson().toJson(new ErrorResult("ClientError", "Error: bad request")));
            return;
        }

        //call service
        try{ //errors will be caught at the handler because they will be serialized here
            CreateResult result = gameService.create(request);

            //convert result to json and return
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }
        catch (UnauthorizedUserException exception){
            ctx.status(401);
            ctx.result(new Gson().toJson(new ErrorResult("ClientError", exception.getMessage())));
        }catch (DataAccessException exception) {
            ctx.status(500);
            ctx.result(new Gson().toJson(new ErrorResult("ServerError", exception.getMessage())));
        }

    }
}

