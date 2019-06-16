package com.example.kamal.minor;

import java.util.concurrent.ConcurrentLinkedQueue;



public class Sender implements Runnable {

	private static ConcurrentLinkedQueue<Packet> ccl;

	public Sender() {
		if (ccl == null)
			ccl = new ConcurrentLinkedQueue<Packet>();
	}

	public static boolean queuePacket(Packet p) {
		if (ccl == null)
			ccl = new ConcurrentLinkedQueue<Packet>();
		return ccl.add(p);
	}

	@Override
	public void run() {
		TcpSender packetSender = new TcpSender();

		while (true) {
			//Sleep to give up CPU cycles
			while (ccl.isEmpty()) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Packet p = ccl.remove();
			String ip = RouterManager.getIPForClient(p.getMac());
			packetSender.sendPacket(ip, socketInfo.RECEIVE_PORT, p);

		}
	}

}
