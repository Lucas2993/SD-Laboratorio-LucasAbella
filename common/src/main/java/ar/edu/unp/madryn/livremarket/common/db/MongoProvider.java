package ar.edu.unp.madryn.livremarket.common.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MongoProvider implements DataProvider {
    private MongoConnection connection;

    private String databaseName;

    private MongoDatabase mongoDatabase;

    private Gson gson;

    public MongoProvider(MongoConnection connection, String databaseName) {
        this.connection = connection;
        this.databaseName = databaseName;
        this.gson = new Gson();
    }

    @Override
    public boolean connect() {
        if(this.connection == null){
            return false;
        }

        if(!this.connection.isConnected()){
            if(!this.connection.connect()){
                return false;
            }
        }

        if(StringUtils.isEmpty(this.databaseName)){
            return false;
        }

        this.mongoDatabase = this.connection.getClient().getDatabase(this.databaseName);
        return true;
    }

    @Override
    public boolean insertElement(Object elementToInsert, String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        collection.insertOne(Document.parse(gson.toJson(elementToInsert)));
        return true;
    }

    @Override
    public boolean updateElement(String searchField, String searchValue, Object elementToUpdate, String collectionName) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);

        Document updated = collection.findOneAndReplace(Filters.eq(searchField, searchValue), Document.parse(gson.toJson(elementToUpdate)));

        return updated != null;
    }

    @Override
    public <T> Collection<T> getCollection(String collectionName, Class<T> elementsType) {
        MongoCollection<Document> databaseCollection = this.mongoDatabase.getCollection(collectionName);

        Collection<T> result = new ArrayList<>();

        FindIterable<Document> findIterable = databaseCollection.find();

        for (Document document : findIterable) {
            T element = gson.fromJson(document.toJson(), elementsType);
            if (element != null) {
                result.add(element);
            }
        }

        return result;
    }

    @Override
    public <T> T getFirstElementInCollection(String collectionName, Class<T> elementsType) {
        MongoCollection<Document> databaseCollection = this.mongoDatabase.getCollection(collectionName);

        FindIterable<Document> findIterable = databaseCollection.find();

        Document document = findIterable.first();
        if(document == null){
            return null;
        }

        return gson.fromJson(document.toJson(), elementsType);
    }

    @Override
    public Map<String, String> getDataFromCollectionByField(String collectionName, String fieldName, String value) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);

        Document document = new Document();

        document.put(fieldName, value);

        FindIterable<Document> findIterable = collection.find(document).limit(1);

        Document found = findIterable.first();

        if(found == null){
            return new HashMap<>();
        }

        found.remove(DataProvider.DEFAULT_ID_FIELD);

        Type dataType = new TypeToken<Map<String, String>>() {
        }.getType();

        return gson.fromJson(found.toJson(), dataType);
    }

    @Override
    public Map<String, String> getFirstDataInCollection(String collectionName) {
        return getFirstDataInCollection(collectionName, false);
    }

    @Override
    public Map<String, String> getFirstDataInCollection(String collectionName, boolean removing) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);

        FindIterable<Document> findIterable = collection.find();

        Document found = findIterable.first();

        if(found == null){
            return new HashMap<>();
        }

        if(removing) {
            collection.findOneAndDelete(found);
        }

        found.remove(DataProvider.DEFAULT_ID_FIELD);

        Type dataType = new TypeToken<Map<String, String>>() {
        }.getType();

        return gson.fromJson(found.toJson(), dataType);
    }

    @Override
    public void clearCollectionContent(String collectionName) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(collectionName);

        collection.drop();
    }
}
