package net.vurs.service.definition.cluster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.getopt.util.hash.MurmurHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.common.ConcurrentConstructingHashMap;
import net.vurs.common.constructor.ConcurrentHashMapConstructor;
import net.vurs.service.EventDispatcher;
import net.vurs.service.definition.cluster.constructor.ClusterRingConstructor;
import net.vurs.service.definition.cluster.layer.ClusterLayer;
import net.vurs.service.definition.cluster.layer.ClusterLayerManager;
import net.vurs.service.definition.cluster.layer.definition.BaseLayer;
import net.vurs.util.ErrorControl;

public class ClusterHeartbeat {
	private final String GROUP_IP = "228.5.6.7";
	private final int GROUP_PORT = 6789;
	private final int BUFFER_SIZE = 1024;
	private final int HEARTBEAT_INTERVAL = 1000;
	private final int NODE_TIMEOUT = 1000*5;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ClusterLayerManager layerManager = null;
	
	private long liveTime = -1;
	private InetAddress localAddress = null;
	private byte[] heartbeatSignature = null;
	
	private InetAddress group = null;
	private MulticastSocket socket = null;
	private Thread heartbeatThread = null;
	private Thread receiverThread = null;
	
	private ConcurrentConstructingHashMap<InetAddress, ConcurrentHashMap<Class<? extends ClusterLayer>, Long>> layerLastHeardTimes = null;
	private ConcurrentConstructingHashMap<InetAddress, ConcurrentHashMap<Class<? extends ClusterLayer>, Long>> layerLiveTimes = null;
	
	private ArrayList<ClusterProtocol> protocols = null;
	private ConcurrentConstructingHashMap<Class<? extends ClusterLayer>, ClusterRing> rings = null;
	private Set<Class<? extends ClusterLayer>> activeLayers = null;
	
	public ClusterHeartbeat(EventDispatcher eventDispatcher, ClusterLayerManager layerManager) {		
		try {
			this.layerManager = layerManager;
			
			this.liveTime = System.currentTimeMillis();
			this.localAddress = InetAddress.getLocalHost();
			
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
			buffer.putLong(this.liveTime);
			buffer.putInt(this.localAddress.getHostAddress().getBytes().length);
			buffer.put(this.localAddress.getHostAddress().getBytes());
			
			this.heartbeatSignature = new byte[buffer.position()];
			System.arraycopy(buffer.array(), 0, this.heartbeatSignature, 0, buffer.position());
			
			this.layerLastHeardTimes = new ConcurrentConstructingHashMap<InetAddress, ConcurrentHashMap<Class<? extends ClusterLayer>, Long>>(new ConcurrentHashMapConstructor<InetAddress, Class<? extends ClusterLayer>, Long>());
			this.layerLiveTimes = new ConcurrentConstructingHashMap<InetAddress, ConcurrentHashMap<Class<? extends ClusterLayer>, Long>>(new ConcurrentHashMapConstructor<InetAddress, Class<? extends ClusterLayer>, Long>());
			
			this.protocols = new ArrayList<ClusterProtocol>();
			this.rings = new ConcurrentConstructingHashMap<Class<? extends ClusterLayer>, ClusterRing>(new ClusterRingConstructor());
			this.activeLayers = new HashSet<Class<? extends ClusterLayer>>();
			this.activeLayers.add(BaseLayer.class);
			
			this.group = InetAddress.getByName(GROUP_IP);
			
			this.socket = new MulticastSocket(GROUP_PORT);
			this.socket.joinGroup(group);
			
			this.heartbeatThread = startHeartbeat(this, this.group, this.socket);
			this.heartbeatThread.setDaemon(true);
			this.heartbeatThread.setName(getClass().getSimpleName() + ": heartbeat");
			this.heartbeatThread.start();
			
			this.receiverThread = startReceiver(this, this.group, this.socket);
			this.receiverThread.setDaemon(true);
			this.receiverThread.setName(getClass().getSimpleName() + ": receiverThread");
			this.receiverThread.start();
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	private Thread startHeartbeat(final ClusterHeartbeat heartbeat, final InetAddress group, final MulticastSocket socket) {
		Thread thread = new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				
				boolean online = true;
				
				while (true) {
					try {
						Thread.sleep(HEARTBEAT_INTERVAL);
						
						ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);						
						buffer.put(heartbeat.getSignature());
						buffer.putLong(System.currentTimeMillis());
						
						Class<? extends ClusterLayer>[] layers = (Class<? extends ClusterLayer>[]) heartbeat.activeLayers.toArray(new Class<?>[heartbeat.activeLayers.size()]);
						buffer.putInt(layers.length);
						for (Class<? extends ClusterLayer> layer: layers) {
							buffer.putInt(ClusterHeartbeat.this.layerManager.getOrdinal(layer));
						}
						
						socket.send(new DatagramPacket(buffer.array(), 
								buffer.position(), 
								group, 
								GROUP_PORT));
						
						online = true;
						
						Long curTime = System.currentTimeMillis();
						for (Entry<InetAddress, ConcurrentHashMap<Class<? extends ClusterLayer>, Long>> m: heartbeat.layerLastHeardTimes.entrySet()) {
							boolean hasTimeout = false;
							
							for (Entry<Class<? extends ClusterLayer>, Long> e: m.getValue().entrySet()) {
								if (curTime - e.getValue() > NODE_TIMEOUT) {
									heartbeat.onFailure(e.getKey(), m.getKey(), e.getValue());
									hasTimeout = true;
								}
							}
							
							if (hasTimeout) {
								ConcurrentHashMap<Class<? extends ClusterLayer>, Long> lastHeardTimes = heartbeat.layerLastHeardTimes.get(m.getKey());
								if (lastHeardTimes.size() == 0 && heartbeat.layerLastHeardTimes.remove(m.getKey(), lastHeardTimes)) {
									if (lastHeardTimes.size() > 0) {
										ConcurrentHashMap<Class<? extends ClusterLayer>, Long> existing = null;
										while ((existing = heartbeat.layerLastHeardTimes.putIfAbsent(m.getKey(), lastHeardTimes)) != null) {
											for (Entry<Class<? extends ClusterLayer>, Long> e: lastHeardTimes.entrySet()) {
												existing.putIfAbsent(e.getKey(), e.getValue());
											}
											
											if (heartbeat.layerLastHeardTimes.replace(m.getKey(), existing, existing)) {
												break;
											}
										}
									}
								}
								
								ConcurrentHashMap<Class<? extends ClusterLayer>, Long> liveTimes = heartbeat.layerLiveTimes.get(m.getKey());
								if (liveTimes.size() == 0 && heartbeat.layerLiveTimes.remove(m.getKey(), liveTimes)) {
									if (liveTimes.size() > 0) {
										ConcurrentHashMap<Class<? extends ClusterLayer>, Long> existing = null;
										while ((existing = heartbeat.layerLiveTimes.putIfAbsent(m.getKey(), lastHeardTimes)) != null) {
											for (Entry<Class<? extends ClusterLayer>, Long> e: liveTimes.entrySet()) {
												existing.putIfAbsent(e.getKey(), e.getValue());
											}
											
											if (heartbeat.layerLiveTimes.replace(m.getKey(), existing, existing)) {
												break;
											}
										}								
									}								
								}
							}
						}
					} catch (IOException e) {
						if (online) {
							ClusterHeartbeat.this.logger.warn("Cluster appears to be down, disconnected from network");
							
							for (Entry<InetAddress, ConcurrentHashMap<Class<? extends ClusterLayer>, Long>> m: heartbeat.layerLastHeardTimes.entrySet()) {
								for (Entry<Class<? extends ClusterLayer>, Long> lh: m.getValue().entrySet()) {
									heartbeat.onFailure(lh.getKey(), m.getKey(), lh.getValue());
								}							
							}
							
							heartbeat.layerLastHeardTimes.clear();
							heartbeat.layerLiveTimes.clear();
							
							online = false;
						}
					} catch (Exception e) {
						ErrorControl.logException(e);
					}
				}
			}
			
		});
		
		return thread;
	}
	
	private Thread startReceiver(final ClusterHeartbeat heartbeat, final InetAddress group, final MulticastSocket socket) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						byte[] bytes = new byte[BUFFER_SIZE];
						DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
						socket.receive(packet);
						
						InetAddress address = packet.getAddress();
						bytes = packet.getData();

						heartbeat.onHeartbeat(address, bytes);
					} catch (Exception e) {
						ErrorControl.logException(e);
					}
				}
			}
			
		});
		
		return thread;
	}

	private void onHeartbeat(InetAddress address, byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		Long liveTime = buffer.getLong();
		
		int hostLen = buffer.getInt();
		byte[] hostBytes = new byte[hostLen];
		buffer.get(hostBytes);
		
		Long sentTime = buffer.getLong();
		
		int layerLen = buffer.getInt();
		for (int i = 0; i < layerLen; i++) {
			Class<? extends ClusterLayer> layer = this.layerManager.getLayer(buffer.getInt());
			
			Long knownLive = this.layerLiveTimes.get(address).get(layer);
			Long knownTime = this.layerLastHeardTimes.get(address).get(layer);
			
			if (knownLive == null || liveTime > knownLive) {
				this.layerLiveTimes.get(address).put(layer, liveTime);
				onDiscovery(layer, address, liveTime);
			}
			
			if (knownTime == null || sentTime > knownTime) {
				this.layerLastHeardTimes.get(address).put(layer, sentTime);
			}
		}
	}

	private void onDiscovery(Class<? extends ClusterLayer> layer, InetAddress address, Long liveTime) {
		if (this.layerLiveTimes.get(address).get(layer).equals(liveTime)) {
			logger.warn("Layer " + layer.getName() + " discovered node: " + address.getHostAddress());
			
			this.rings.get(BaseLayer.class).add(address);
			ClusterNode node = new ClusterNode(address);
			for (ClusterProtocol protocol: this.protocols) {
				protocol.onNodeDiscovery(node);
			}			
		}
	}
	
	private void onFailure(Class<? extends ClusterLayer> layer, InetAddress address, Long lastHeardTime) {
		if (this.layerLastHeardTimes.get(address).remove(layer, lastHeardTime)) {
			logger.warn("Layer " + layer.getName() + " lost node: " + address.getHostAddress());
			
			for (ClusterRing ring: this.rings.values()) {
				ring.remove(address);
			}
			
			this.layerLiveTimes.get(address).remove(layer);
			ClusterNode node = new ClusterNode(address);
			for (ClusterProtocol protocol: this.protocols) {
				protocol.onNodeFailure(node);
			}
		}
	}

	private byte[] getSignature() { return this.heartbeatSignature; }
	
	public ClusterNode findPosition(int key) {
		return findPosition(BaseLayer.class, key);
	}
	
	public ClusterNode findPosition(Class<? extends ClusterLayer> layer, int key) {
		return new ClusterNode(this.rings.get(layer).findPosition(key));
	}
	
	public ClusterNode hashPosition(String key) {
		return hashPosition(BaseLayer.class, key);
	}
	
	public ClusterNode hashPosition(Class<? extends ClusterLayer> layer, String key) {
		return findPosition(layer, MurmurHash.hash(key.getBytes(), 1));
	}
	
	public void addLayer(Class<? extends ClusterLayer> layer) {
		this.activeLayers.add(layer);
	}
	
	public void removeLayer(Class<? extends ClusterLayer> layer) {
		this.activeLayers.remove(layer);
	}
	
	public boolean isActive(Class<? extends ClusterLayer> layer) {
		return this.activeLayers.contains(layer);
	}
	
}
