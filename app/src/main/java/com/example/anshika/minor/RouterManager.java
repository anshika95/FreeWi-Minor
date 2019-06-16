package com.example.kamal.minor;
import java.util.concurrent.ConcurrentHashMap;


public class RouterManager {

	public static ConcurrentHashMap<String, MultipleClients> routingTable = new ConcurrentHashMap<String, MultipleClients>();

	private static MultipleClients self;

	public static void newClient(MultipleClients c) {
		routingTable.put(c.getMac(), c);
	}

	public static void clientGone(MultipleClients c) {
		routingTable.remove(c.getMac());
	}


	public static MultipleClients getSelf() {
		return self;
	}


	public static void setSelf(MultipleClients self) {
		RouterManager.self = self;
		newClient(self);
	}

	public static String getIPForClient(MultipleClients c) {


		if (self.getGroupOwnerMac() == c.getGroupOwnerMac()) {
			// share the same GO then just give its IP
			System.out.println("Have the same group owner, sending to :" + c.getIp());
			return c.getIp();
		}

        MultipleClients go = routingTable.get(c.getGroupOwnerMac());

		
		// I am the group owner so can propagate
		if (self.getGroupOwnerMac() == self.getMac()) {
			if (self.getGroupOwnerMac() != c.getGroupOwnerMac() && go.getIsDirectLink()) {
				// not the same group owner, but we have the group owner as a
				// direct link
				return c.getIp();
			} else if (go != null && self.getGroupOwnerMac() != c.getGroupOwnerMac() && !go.getIsDirectLink()) {
				for(MultipleClients aclient : routingTable.values()){
					if(aclient.getGroupOwnerMac().equals(aclient.getMac())){
						//try sending it to a random group owner
						//can also expand this to all group owners
						return aclient.getIp();
					}
				}
				//no other group owners, don't know who to send it to
				return "0.0.0.0";
			}
		} else if (go != null) { // I am not the group owner - need to sent it to my GO
			return socketInfo.GO_IP;
		}

		//Will drop the packet
		return "0.0.0.0";

	}


	public static byte[] serializeRoutingTable() {
		StringBuilder serialized = new StringBuilder();

		for (MultipleClients v : routingTable.values()) {
			serialized.append(v.toString());
			serialized.append("\n");
		}

		return serialized.toString().getBytes();
	}


	public static void deserializeRoutingTableAndAdd(byte[] rtable) {
		String rstring = new String(rtable);

		String[] div = rstring.split("\n");
		for (String s : div) {
            MultipleClients a = MultipleClients.fromString(s);
			routingTable.put(a.getMac(), a);
		}
	}


	public static String getIPForClient(String mac) {

        MultipleClients c = routingTable.get(mac);
		if (c == null) {
			System.out.println("NULL ENTRY in ROUTING TABLE FOR MAC");
			return socketInfo.GO_IP;
		}

		return getIPForClient(c);

	}

}
