package ar.edu.unp.madryn.livremarket.common.db;

import java.util.Collection;
import java.util.Map;

public interface DataProvider {
    public static String DEFAULT_ID_FIELD = "_id";

    boolean connect();

    boolean insertElement(Object elementToInsert, String collectionName);
    boolean updateElement(String searchField, String searchValue, Object elementToUpdate, String collectionName);

    <T> Collection<T> getCollection(String collectionName, Class<T> elementsType);

    Map<String,String> getDataFromCollectionByField(String collectionName, String fieldName, String value);
}
