package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import results.ClearResult;
import results.ErrorResult;
import service.ClearService;

public class ClearHandler implements Handler {

    private final ClearService clearService;

    //object constructor
    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public void handle(Context ctx) {
        try {
            ClearResult result = clearService.clearAll();

            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }catch (DataAccessException exception) {
            ctx.status(500);
            ctx.result(new Gson().toJson(new ErrorResult(exception.getMessage())));
        }
    }
}









