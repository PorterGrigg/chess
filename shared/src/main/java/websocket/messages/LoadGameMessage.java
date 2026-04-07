package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    public final String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}