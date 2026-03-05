package dataaccess;

import model.*;

public class MemoryAuthDAO extends BaseMemoryDAO<AuthData> implements AuthDAO{

    @Override
    public AuthData findAuth(String authToken){
        for (int i = 0; i < generalStorage.size(); i++) {

            if(generalStorage.get(i).authToken().equals(authToken)){
                return generalStorage.get(i);
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken){
        //find auth first, then delete
        for (int i = 0; i < generalStorage.size(); i++) {

            if(generalStorage.get(i).authToken().equals(authToken)){
                generalStorage.remove(i);
                break;
            }
        }
    }


}
