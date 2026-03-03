package dataAccess;


import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO extends BaseMemoryDAO<UserData> implements UserDAO { //extending the base class required to specify the type, I had forgotten

    @Override
    public UserData read(String username){

        return null;
    }

}
