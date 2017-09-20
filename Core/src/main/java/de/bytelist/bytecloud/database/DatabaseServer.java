package de.bytelist.bytecloud.database;

import com.mongodb.BasicDBObject;
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
    public void addServer(String group, String id, Integer port, String state, Integer maxPlayer, Integer maxSpectator, String motd, String eventMode) {
        if(existsServer(id)) return;

        this.executor.execute(()-> {
            Document document = new Document()
                    .append(DatabaseServerObject.GROUP.getName(), group)
                    .append(DatabaseServerObject.SERVER_ID.getName(), id)
                    .append(DatabaseServerObject.PORT.getName(), port)
                    .append(DatabaseServerObject.STATE.getName(), state)
                    .append(DatabaseServerObject.STARTED.getName(), new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime()))
                    .append(DatabaseServerObject.PLAYER_MAX.getName(), maxPlayer)
                    .append(DatabaseServerObject.PLAYER_ONLINE.getName(), 0)
                    .append(DatabaseServerObject.SPECTATOR_MAX.getName(), maxSpectator)
                    .append(DatabaseServerObject.SPECTATOR_ONLINE.getName(), 0)
                    .append(DatabaseServerObject.MOTD.getName(), motd)
                    .append(DatabaseServerObject.EVENT_MODE.getName(), eventMode)
                    .append(DatabaseServerObject.PLAYERS.getName(), "")
                    .append(DatabaseServerObject.SPECTATORS.getName(), "");

            this.collection.insertOne(document);
        });
    }

    public void removeServer(String id) {
        if(!existsServer(id)) return;
        this.executor.execute(()-> {
            BasicDBObject dbObject = new BasicDBObject()
                    .append(DatabaseServerObject.SERVER_ID.getName(), id);
            collection.deleteOne(dbObject);
        });
    }

    public boolean existsServer(String id) {
        FindIterable<Document> find = collection.find(Filters.eq(DatabaseServerObject.SERVER_ID.getName(), id));
        Document document = find.first();
        return document != null;
    }

    public List<String> getServer() {
        FindIterable<Document> find = collection.find();
        List<String> list = new ArrayList<>();
        for(Document document : find) {
            list.add(document.getString(DatabaseServerObject.SERVER_ID.getName()));
        }
        return list;
    }

    public List<String> getServer(String group) {
        FindIterable<Document> find = collection.find(Filters.eq(DatabaseServerObject.GROUP.getName(), group));
        List<String> list = new ArrayList<>();
        for(Document document : find) {
            list.add(document.getString(DatabaseServerObject.SERVER_ID.getName()));
        }
        return list;
    }

    public void setDatabaseObject(String id, DatabaseServerObject databaseServerObject, Object value) {
        this.executor.execute(()-> {
            BasicDBObject doc = new BasicDBObject();
            doc.append("$set", new BasicDBObject().append(databaseServerObject.getName(), value));

            BasicDBObject basicDBObject = new BasicDBObject().append(DatabaseServerObject.SERVER_ID.getName(), id);
            collection.updateOne(basicDBObject, doc);
        });
    }

    public DatabaseElement getDatabaseElement(String id, DatabaseServerObject databaseServerObject) {
        FindIterable<Document> find = collection.find(Filters.eq(DatabaseServerObject.SERVER_ID.getName(), id));
        Document document = find.first();

        if(document == null) return null;

        return new DatabaseElement(document.get(databaseServerObject.getName()));
    }
}
