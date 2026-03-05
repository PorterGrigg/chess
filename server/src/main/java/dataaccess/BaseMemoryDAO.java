package dataaccess;

import java.util.ArrayList;

public abstract class BaseMemoryDAO<T> { //this abstract class allows the other classes to extend and use this common code (avoid code duplication)
    public final ArrayList<T> generalStorage = new ArrayList<>(); //T is a generic type

    public void create(T data) {
        generalStorage.add(data);
    }

    public ArrayList<T> readAll() {
        return generalStorage;
    }

    public void clear() {
        generalStorage.clear();
    }
}
