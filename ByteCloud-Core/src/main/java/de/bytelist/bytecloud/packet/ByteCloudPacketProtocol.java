package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.crypt.AESEncryption;
import com.github.steveice10.packetlib.crypt.PacketEncryption;
import com.github.steveice10.packetlib.packet.DefaultPacketHeader;
import com.github.steveice10.packetlib.packet.PacketHeader;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import de.bytelist.bytecloud.common.packet.PingPacket;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketProtocol extends PacketProtocol {

    private PacketHeader header = new DefaultPacketHeader();
    private AESEncryption encrypt;

    public ByteCloudPacketProtocol(SecretKey key) {
        this.setSecretKey(key);
    }

    void setSecretKey(SecretKey key) {
        this.register(0, PingPacket.class);

        try {
            this.encrypt = new AESEncryption(key);
        } catch(GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSRVRecordPrefix() {
        return "_test";
    }

    @Override
    public PacketHeader getPacketHeader() {
        return this.header;
    }

    @Override
    public PacketEncryption getEncryption() {
        return this.encrypt;
    }

    @Override
    public void newClientSession(Client client, Session session) {
        session.addListener(new ByteCloudPacketClientSessionListener());
    }

    @Override
    public void newServerSession(Server server, Session session) {
        session.addListener(new ByteCloudPacketServerSessionListener());
    }
}
