package com.example.kamal.minor;


public class MultipleClients {

	private String mac;

	private String name;

	private String groupOwnerMac;
	

	private String ip;

	private boolean isDirectLink;

	public MultipleClients(String mac_address, String ip, String name, String groupOwner) {
		this.setMac(mac_address);
		this.setName(name);
		this.setIp(ip);
		this.setGroupOwnerMac(groupOwner);
		this.isDirectLink = true;
	}

	public void setIsDirectLink(boolean d) {
		this.isDirectLink = d;
	}

	public boolean getIsDirectLink() {
		return this.isDirectLink;
	}

	public String getGroupOwnerMac() {
		return groupOwnerMac;
	}


	public void setGroupOwnerMac(String groupOwnerMac) {
		this.groupOwnerMac = groupOwnerMac;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getMac() {
		return mac;
	}


	public void setMac(String mac) {
		this.mac = mac;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return getIp() + "," + getMac() + "," + getName() + "," + getGroupOwnerMac();
	}

	public static MultipleClients fromString(String serialized) {
		String[] divided = serialized.split(",");
		return new MultipleClients(divided[1], divided[0], divided[2], divided[3]);
	}

}
