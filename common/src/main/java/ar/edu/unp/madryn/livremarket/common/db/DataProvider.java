package ar.edu.unp.madryn.livremarket.common.db;

import java.util.Collection;

public interface DataProvider {

    boolean connect();

    boolean insertElement(Object elementToInsert, String collectionName);
    boolean updateElement(String id, Object elementToUpdate, String collectionName);

    <T> Collection<T> getCollection(String collectionName, Class<T> elementsType);
}
