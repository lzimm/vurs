package net.vurs.conn.binary;

import java.util.List;

public class BinaryPayload {

	private byte[] name = null;
	private byte[] bytes = null;
	private long ts = 0;
	private int pos = 0;
	
	public BinaryPayload(byte[] name, long ts, byte[]... bytes) {
		this.name = name;
		this.ts = ts;
		this.pos = 0;
		
		if (bytes.length == 1) {
			this.bytes = bytes[0];
		} else {
			int len = 0;
			for (byte[] b: bytes) {
				len += b.length;
			}
			
			this.bytes = new byte[len];
			
			int offset = 0;
			for (byte[] b: bytes) {
				System.arraycopy(b, 0, this.bytes, offset, b.length);
				offset += b.length;
			}
		}
	}
	
	public byte[] getName() {
		return this.name;
	}
	
	public byte[] getValue() {
		return this.bytes;
	}
	
	public long getTime() {
		return this.ts;
	}
	
	public void reset() {
		this.pos = 0;
	}
	
	public byte[] read(int len) {
		if (len == this.bytes.length) {
			return this.bytes;
		}
		
		byte[] ret = new byte[len];
		
		System.arraycopy(this.bytes, this.pos, ret, 0, len);		
		this.pos += len;
		
		return ret;
	}

	public boolean hasMore() {
		return this.pos < this.bytes.length;
	}

	public static BinaryPayload fromList(byte[] name, long ts, int len, List<byte[]> list) {
		byte[] bytes = new byte[len];
		
		int pos = 0;
		for (byte[] b: list) {
			System.arraycopy(b, 0, bytes, pos, b.length);
		}
		
		return new BinaryPayload(name, ts, bytes);
	}

}
