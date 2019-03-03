package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import de.bytelist.bytecloud.common.CloudSoftware;
import de.bytelist.bytecloud.common.Executable;
import de.bytelist.bytecloud.common.packet.PacketFlag;
import de.bytelist.bytecloud.common.packet.PacketInfo;
import de.bytelist.bytecloud.common.packet.client.ClientKeepAlivePacket;
import de.bytelist.bytecloud.common.packet.client.ClientServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudKeepAlivePacket;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketCloudSessionListener extends SessionAdapter {

    private long lastPingTime = 0;
    private int lastPingId = 0;


    @Override
    public void packetReceived(PacketReceivedEvent event) {
        PacketInfo packetInfo = PacketInfo.fromClass(event.getPacket().getClass());
        switch (packetInfo) {
            case UNKNOWN_PACKET:
                break;
            case CLIENT_KEEP_ALIVE_PACKET:
                ClientKeepAlivePacket clientKeepAlivePacket = event.getPacket();
                if(clientKeepAlivePacket.getPingId() == this.lastPingId) {
                    long time = System.currentTimeMillis() - this.lastPingTime;
                    event.getSession().setFlag(PacketFlag.PING_KEY, time);
                }
                break;
            case CLIENT_SERVER_STARTED_PACKET:
                ClientServerStartedPacket clientServerStartedPacket = event.getPacket();
                Executable executable = CloudSoftware.getInstance().getExecutable(clientServerStartedPacket.getServerId());
                executable.setSession(event.getSession());
                executable.onStart();
                break;
            case CLIENT_SERVER_STOPPED_PACKET:
                break;


            case CLOUD_KEEP_ALIVE_PACKET:
                break;
            case CLOUD_SERVER_STARTED_PACKET:
                break;
            case CLOUD_SERVER_STOPPED_PACKET:
                break;
            case CLOUD_SERVER_GROUP_INFO_PACKET:
                break;


            case CLOUD_PLAYER_CONNECT_PACKET:
                break;
            case CLOUD_PLAYER_DISCONNECT_PACKET:
                break;
            case CLOUD_PLAYER_SERVER_SWITCH_PACKET:
                break;
        }
    }

    @Override
    public void connected(ConnectedEvent event) {
        System.out.println("Connected ("+event.getSession().getHost() + ":" + event.getSession().getPort()+").");
        event.getSession().setFlag(PacketFlag.PING_KEY, 0);
        new Thread(new KeepAliveTask(event.getSession())).start();
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        System.out.println("Disconnecting ("+event.getSession().getHost() + ":" + event.getSession().getPort()+"): " + event.getReason());
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        System.out.println("Disconnected ("+event.getSession().getHost() + ":" + event.getSession().getPort()+"): " + event.getReason());
    }


    private class KeepAliveTask implements Runnable {
        private Session session;

        KeepAliveTask(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            while(this.session.isConnected()) {
                lastPingTime = System.currentTimeMillis();
                lastPingId = (int) lastPingTime;
                this.session.send(new CloudKeepAlivePacket(lastPingId));

                try {
                    Thread.sleep(4000);
                } catch(InterruptedException e) {
                    break;
                }
            }
        }
    }
}
