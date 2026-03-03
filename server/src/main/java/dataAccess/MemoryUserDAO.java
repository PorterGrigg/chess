package dataAccess;


import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO extends BaseMemoryDAO<UserData> implements UserDAO { //extending the base class required to specify the type, I had forgotten

    @Override
    public UserData findUser(String username){
        for (int i = 0; i < generalStorage.size(); i++) {
//            if(generalStorage.get(i).equals(null)){ //list will never be null haha
//                continue;
//            }
            if(generalStorage.get(i).username().equals(username)){
                return generalStorage.get(i);
            }
        }
        return null;
    }

}
