package dataaccess;


import model.UserData;

public class MemoryUserDAO extends BaseMemoryDAO<UserData> implements UserDAO { //extending the base class require type

    @Override
    public UserData findUser(String username){
        for (int i = 0; i < generalStorage.size(); i++) {
            if(generalStorage.get(i).username().equals(username)){
                return generalStorage.get(i);
            }
        }
        return null;
    }

}
