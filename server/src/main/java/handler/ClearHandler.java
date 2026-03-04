package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import results.ClearResult;
import service.ClearService;

public class ClearHandler extends BaseHandler{

    private final ClearService clearService;

    //object constructor
    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public void handle(Context ctx) {
        ClearResult result = clearService.clearAll();

        ctx.status(200);
        ctx.result(new Gson().toJson(result));
    }
}









