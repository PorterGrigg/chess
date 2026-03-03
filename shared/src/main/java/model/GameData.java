package model;

import chess.*;

public record GameData(int gameID, String whiteUsername, String blackUserName, String gameName, ChessGame game) {}
