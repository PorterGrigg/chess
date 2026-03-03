package handler;

import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler extends BaseHandler{

    private final ClearService clearService;

    //object constructor
    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public void handle(Context ctx) {
        clearService.clearAll();
        ctx.status(200);
    }
}









