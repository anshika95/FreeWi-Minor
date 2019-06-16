package com.example.kamal.minor;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.widget.Toast;

public class Receiver implements Runnable {

    public static boolean running = false;

    static WiFiDirectActivity activity;

    public Receiver(WiFiDirectActivity a) {
        Receiver.activity = a;
        running = true;
    }

    public void run() {

        ConcurrentLinkedQueue<Packet> packetQueue = new ConcurrentLinkedQueue<Packet>();

        new Thread(new TcpReciever(socketInfo.RECEIVE_PORT, packetQueue)).start();

        Packet p;

        while (true) {

            while (packetQueue.isEmpty()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            p = packetQueue.remove();

            if (p.getType().equals(Packet.TYPE.HELLO)) {
                // Put it in your routing table
                for (MultipleClients c : RouterManager.routingTable.values()) {
                    if (c.getMac().equals(RouterManager.getSelf().getMac()) || c.getMac().equals(p.getSenderMac()))
                        continue;
                    Packet update = new Packet(Packet.TYPE.UPDATE, Packet.getMacAsBytes(p.getSenderMac()), c.getMac(),
                            RouterManager.getSelf().getMac());
                    Sender.queuePacket(update);
                }

                RouterManager.routingTable.put(p.getSenderMac(),
                        new MultipleClients(p.getSenderMac(), p.getSenderIP(), p.getSenderMac(),
                                RouterManager.getSelf().getMac()));

                // Send routing table back as HELLO_ACK
                byte[] rtable = RouterManager.serializeRoutingTable();

                Packet ack = new Packet(Packet.TYPE.HELLO_ACK, rtable, p.getSenderMac(), RouterManager.getSelf()
                        .getMac());
                Sender.queuePacket(ack);
                somebodyJoined(p.getSenderMac());
                updatePeerList();
            } else {
                // If you're the intended target for a non hello message
                if (p.getMac().equals(RouterManager.getSelf().getMac())) {
                    //if we get a hello ack populate the table
                    if (p.getType().equals(Packet.TYPE.HELLO_ACK)) {
                        RouterManager.deserializeRoutingTableAndAdd(p.getData());
                        RouterManager.getSelf().setGroupOwnerMac(p.getSenderMac());
                        somebodyJoined(p.getSenderMac());
                        updatePeerList();
                    } else if (p.getType().equals(Packet.TYPE.UPDATE)) {
                        //if it's an update, add to the table
                        String emb_mac = Packet.getMacBytesAsString(p.getData(), 0);
                        RouterManager.routingTable.put(emb_mac,
                                new MultipleClients(emb_mac, p.getSenderIP(), p.getMac(), RouterManager
                                        .getSelf().getMac()));

                        final String message = emb_mac + " joined the conversation";
                        final String name = p.getSenderMac();
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (activity.isVisible) {
                                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                } else {
                                    ChatActivity.addMessage(name, message);
                                }
                            }
                        });
                        updatePeerList();

                    } else if (p.getType().equals(Packet.TYPE.MESSAGE)) {
                        //If it's a message display the message and update the table if they're not there
                        // for whatever reason
                        final String message = p.getSenderMac() + " says:\n" + new String(p.getData());
                        final String msg = new String(p.getData());
                        final String name = p.getSenderMac();

                        if (!RouterManager.routingTable.contains(p.getSenderMac())) {

                            RouterManager.routingTable.put(p.getSenderMac(),
                                    new MultipleClients(p.getSenderMac(), p.getSenderIP(), p.getSenderMac(),
                                            RouterManager.getSelf().getGroupOwnerMac()));
                        }

                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (activity.isVisible) {
                                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                } else {
                                    ChatActivity.addMessage(name, msg);
                                }
                            }
                        });
                        updatePeerList();
                    }
                } else {
                    // otherwise forward it if you're not the recipient
                    int ttl = p.getTtl();
                    // Have a ttl so that they don't bounce around forever
                    ttl--;
                    if (ttl > 0) {
                        Sender.queuePacket(p);
                        p.setTtl(ttl);
                    }
                }
            }

        }
    }


    public static void somebodyJoined(String smac) {

        final String message;
        final String msg;
        message = msg = smac + " has joined.";
        final String name = smac;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (activity.isVisible) {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                } else {
                    ChatActivity.addMessage(name, msg);
                }
            }
        });
    }

    public static void somebodyLeft(String smac) {

        final String message;
        final String msg;
        message = msg = smac + " has left.";
        final String name = smac;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (activity.isVisible) {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                } else {
                    ChatActivity.addMessage(name, msg);
                }
            }
        });
    }


    public static void updatePeerList() {
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DeviceDetailFragment.updateGroupMessage();
            }

        });
    }

}