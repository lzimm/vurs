package net.vurs.common;

import com.eaio.uuid.UUID;

public class TimeUUID {
	
	private UUID uuid = null;
	
	public TimeUUID() {
		this.uuid = new UUID();
	}
	
	public TimeUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		return this.uuid.toString();
	}
	
	public byte[] getBytes() {
		long msb = this.uuid.time;
		long lsb = this.uuid.clockSeqAndNode;
		byte[] bytes = new byte[16];

        for (int i = 0; i < 8; i++) bytes[i] = (byte) (msb >>> 8 * (7 - i));
        for (int i = 8; i < 16; i++) bytes[i] = (byte) (lsb >>> 8 * (7 - i));
        
        return bytes;
	}
	
	public static TimeUUID fromBytes(byte[] bytes) {
		long msb = 0;
        long lsb = 0;
        
        for (int i=0; i<8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
        for (int i=8; i<16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
        
        UUID uuid = new UUID(msb, lsb);
        return new TimeUUID(uuid);
	}
	
}
