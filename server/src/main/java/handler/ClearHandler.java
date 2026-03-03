package handler;

import io.javalin.http.Context;

public class ClearHandler extends BaseHandler{

    @Override
    public void handle(Context ctx) {

        // call cleara service

        ctx.status(200).result();
    }
}
