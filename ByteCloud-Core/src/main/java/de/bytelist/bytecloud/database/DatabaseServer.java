package de.bytelist.bytecloud.database;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by ByteList on 21.04.2017.
 */
public class DatabaseServer {

    private MongoCollection<Document> collection;

    private final Executor executor;

    DatabaseServer(DatabaseManager databaseManager) {
        this.collection = databaseManager.getCollection();
        this.executor = databaseManager.getExecutor();
    }
    public void addServer(String group, String id, Integer port, String state, Integer slots, String motd) {
        if(existsServer(id)) return;

        this.executor.execute(()-> {
            Document document = new Document()
                    .append(DatabaseServerObject.GROUP.getName(), group)
                    .append(DatabaseServerObject.SERVER_ID.getName(), id)
                    .append(DatabaseServerObject.PORT.getName(), port)
                    .append(DatabaseServerObject.STATE.getName(), state)
                    .append(DatabaseServerObject.STARTED.getName(), new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime()))
                    .append(DatabaseServerObject.SLOTS.getName(), slots)
                    .append(DatabaseServerObject.MOTD.getName(), motd);

            this.collection.insertOne(document);
        });
    }

    public void removeServer(String id) {
        if(!existsServer(id)) return;
        this.executor.execute(()-> collection.deleteOne(new BasicDBObject(DatabaseServerObject.SERVER_ID.getName(), id)));
    }

    public boolean existsServer(String id) {
        FindIterable<Document> find = collection.find(Filters.eq(DatabaseServerObject.SERVER_ID.getName(), id));
        Document document = find.first();
        return document != null;
    }

    public List<String> getServer() {
        List<String> list = new ArrayList<>();
        collection.find().forEach((Block<? super Document>) document -> list.add(document.getString(DatabaseServerObject.SERVER_ID.getName())));
        return list;
    }

    public List<String> getServer(String group) {
        List<String> list = new ArrayList<>();
        collection.find(Filters.eq(DatabaseServerObject.GROUP.getName(), group)).forEach((Block<? super Document>) document ->
                        list.add(document.getString(DatabaseServerObject.SERVER_ID.getName())));
        return list;
    }

    public void setDatabaseObject(String id, DatabaseServerObject databaseServerObject, Object value) {
        this.executor.execute(()-> collection.updateOne(
                new BasicDBObject(DatabaseServerObject.SERVER_ID.getName(), id),
                new BasicDBObject("$set", new BasicDBObject(databaseServerObject.getName(), value))));
    }

    public DatabaseElement getDatabaseElement(String id, DatabaseServerObject databaseServerObject) {
        FindIterable<Document> find = collection.find(Filters.eq(DatabaseServerObject.SERVER_ID.getName(), id));
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databaseServerObject.getName()));
    }
}
