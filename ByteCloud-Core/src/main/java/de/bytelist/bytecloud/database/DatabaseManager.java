package de.bytelist.bytecloud.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ByteList on 09.04.2017.
 */
public class DatabaseManager {
    @Getter
    private static DatabaseManager instance;

    @Getter
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase mongoDatabase;
    @Getter
    private MongoCollection<Document> mongoCollection;
    @Getter
    private final String collectionName = "cloud-cloud";
    @Getter
    private DatabaseServer databaseServer;

    @Getter
    private Executor executor;

    public DatabaseManager(String host, int port, String username, String password, String database) {
        instance = this;

        // Disable the stupid log messages from mongodb
        Logger mongoLog = Logger.getLogger("org.mongodb.driver");
        mongoLog.setLevel(Level.OFF);

        this.executor = Executors.newCachedThreadPool();

        // Support for new mongodb standard uuid's
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                MongoClient.getDefaultCodecRegistry()
        );
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();

        if(username != null && password != null)
            this.mongoClient = new MongoClient(new ServerAddress(host, port), Collections.singletonList(MongoCredential.createCredential(username, database, password.toCharArray())), options);
        else
            this.mongoClient = new MongoClient(new ServerAddress(host, port), options);

        this.mongoDatabase = this.mongoClient.getDatabase(database);
        if(!existsCollection()) this.mongoDatabase.createCollection(collectionName);
        this.mongoCollection = this.mongoDatabase.getCollection(collectionName);

        this.databaseServer = new DatabaseServer(this);
    }
    private boolean existsCollection() {
        for(String cl : mongoDatabase.listCollectionNames()) {
            if(cl.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }

    public MongoCollection<Document> getCollection() {
        return this.mongoCollection;
    }

}
