package client;

import com.google.gson.Gson;
import model.*;
import results.*;
import requests.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

//.delete("/db", clearHandler::handle)
//            .post("/user", registerHandler::handle)
//            .post("/session", loginHandler::handle)
//            .delete("/session", logoutHandler::handle)
//            .get("/game", listHandler::handle)
//            .post("/game", createHandler::handle)
//            .put("/game", joinHandler::handle);

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ClearResult clearAll() throws ResponseException {
        var httpRequest = buildRequest("DELETE", "/db", null);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, ClearResult.class);
    }

    public RegisterResult registerUser(RegisterRequest request) throws ResponseException {
        var httpRequest = buildRequest("POST", "/user", request);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, RegisterResult.class);
    }

    public LoginResult loginUser(LoginRequest request) throws ResponseException {
        var httpRequest = buildRequest("POST", "/session", request);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, LoginResult.class);
    }

    public LogoutResult logoutUser(LogoutRequest request) throws ResponseException {
        var httpRequest = buildRequest("DELETE", "/user", request);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, LogoutResult.class);
    }

    public ListResult listGames(ListRequest request) throws ResponseException {
        var httpRequest = buildRequest("GET", "/game", request);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, ListResult.class);
    }

    public CreateResult createGame(CreateRequest request) throws ResponseException {
        var httpRequest = buildRequest("POST", "/game", request);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, CreateResult.class);
    }

    public JoinResult joinGame(CreateRequest request) throws ResponseException {
        var httpRequest = buildRequest("PUT", "/game", request);
        var httpResponse = sendRequest(httpRequest);
        return handleResponse(httpResponse, JoinResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
