package dataaccess;


import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO extends BaseMemoryDAO<GameData> implements GameDAO{

    @Override
    public GameData findGame(int gameID){
        for (int i = 0; i < generalStorage.size(); i++) {

            if(generalStorage.get(i).gameID() == gameID){
                return generalStorage.get(i);
            }
        }
        return null;
    }

    @Override
    public int create(GameData data) {
        generalStorage.add(data);
        return data.gameID();
    }

    @Override
    public void updateGameDataUsername(int gameID, ChessGame.TeamColor playerColor, String username){

        for (int i = 0; i < generalStorage.size(); i++) {

            if(generalStorage.get(i).gameID() == gameID){
                //because records are immutable their fields cannot be updated
                //extract and save old data
                String whiteUsername;
                String blackUsername;
                if(playerColor == ChessGame.TeamColor.BLACK){
                    whiteUsername = generalStorage.get(i).whiteUsername();
                    blackUsername = username;
                }
                else{
                    blackUsername = generalStorage.get(i).blackUsername();
                    whiteUsername = username;
                }
                ChessGame game = generalStorage.get(i).game();
                String gameName = generalStorage.get(i).gameName();

                //create new GameData from old and new info combined
                GameData newGData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

                //delete old game data
                generalStorage.remove(i);

                //add new game data to storage
                generalStorage.add(newGData);

                break; //break loop cause already updated the game

            }
        }
    }

    public void updateGameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)
            throws DataAccessException{
        for (int i = 0; i < generalStorage.size(); i++) {

            if (generalStorage.get(i).gameID() == gameID) {
                //because records are immutable their fields cannot be updated

                //delete old game data
                generalStorage.remove(i);

                //create new GameData from old and new info combined
                GameData newGData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                //add new game data to storage
                generalStorage.add(newGData);

                break;
            }
        }
    }

}
