package edu.ou.mlfw.ladder;

import java.net.InetAddress;

public class GameRequest implements LadderMessage {
	private final InetAddress clientAddress;

	public GameRequest(InetAddress clientAddress) {
		this.clientAddress = clientAddress;
	}

	public InetAddress getClientAddress() {
		return clientAddress;
	}
}
