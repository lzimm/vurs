package net.vurs.service.definition.cluster;

import java.net.InetAddress;

public class ClusterNode {

	private InetAddress address = null;
	
	public ClusterNode(InetAddress address) {
		this.address = address;
	}
	
	public InetAddress getAddress() { return this.address; }
	
}
