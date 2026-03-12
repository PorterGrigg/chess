package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import results.ErrorResult;
import service.AlreadyTakenException;
import service.GameService;
import service.UnauthorizedUserException;
import requests.JoinRequest;
import results.JoinResult;

public class JoinHandler {

    private final GameService gameService;

    public JoinHandler(GameService joinService) {
        this.gameService = joinService;
    }

    public void handle(Context ctx) {

        //convert json form to our request form
        String authToken = ctx.header("Authorization");

        JoinRequest partialRequest = new Gson().fromJson(ctx.body(), JoinRequest.class); //deserialize the body
        chess.ChessGame.TeamColor playerColor = partialRequest.playerColor();
        int gameID = partialRequest.gameID();

        JoinRequest request = new JoinRequest(authToken, playerColor, gameID);
        //combine data extracted from the header and body

        //throw new BadRequestResponse("Error: bad request");
        if(request.authToken() == null | request.playerColor() == null | request.gameID() == 0){
            //do I need to have gameID == 0? Cause that is default
            ctx.status(400);
            ctx.result(new Gson().toJson(new ErrorResult("Error: bad request")));
            return;
        }

        //call service
        try{ //errors will be caught at the handler because they will be serialized here
            JoinResult result = gameService.join(request);

            //convert result to json and return
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }
        catch (UnauthorizedUserException exception){
            ctx.status(401);
            ctx.result(new Gson().toJson(new ErrorResult( exception.getMessage())));
        }
        catch (AlreadyTakenException exception){
            ctx.status(403);
            ctx.result(new Gson().toJson(new ErrorResult(exception.getMessage())));
        }
        catch (DataAccessException exception) {
            ctx.status(500);
            ctx.result(new Gson().toJson(new ErrorResult( exception.getMessage())));
        }
    }
}

