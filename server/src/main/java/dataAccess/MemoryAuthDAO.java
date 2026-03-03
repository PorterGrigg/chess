package dataAccess;

import java.util.ArrayList;
import java.util.UUID;
import model.*;

public class MemoryAuthDAO implements AuthDAO{
    private final ArrayList<AuthData> authStorage = new ArrayList<>();

    @Override
    public void create(AuthData authData){
        authStorage.add(authData);
    }

    @Override
    public ArrayList<AuthData> readAll(){
        return authStorage;
    }

    @Override
    public void clear(){
        authStorage.clear();
    }

//    public static String generateToken() {
//        return UUID.randomUUID().toString();
//    }
}
