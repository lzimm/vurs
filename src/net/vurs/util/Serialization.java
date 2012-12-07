package net.vurs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Serialization {

	public static abstract class Deserializer<T> {
		public abstract T deserialize(byte[] bytes);
	}

	public static byte[] serialize(String object) {
		try {
			return object.getBytes("UTF-8");
		} catch (Exception e) {
			ErrorControl.logException(e);
			return new byte[0];
		}
	}

	public static byte[] serialize(Double object) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeDouble(object);
			dos.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return new byte[0];
		}
	}
	
	public static byte[] serialize(Float object) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeFloat(object);
			dos.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return new byte[0];
		}
	}
	
	public static byte[] serialize(Long object) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeLong(object);
			dos.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return new byte[0];
		}
	}

	public static byte[] serialize(Integer object) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeInt(object);
			dos.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return new byte[0];
		}
	}

	public static byte[] serialize(Boolean object) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeBoolean(object);
			dos.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return new byte[0];
		}
	}
	
	public static Long deserializeLong(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			return dis.readLong();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}
	
	public static Integer deserializeInt(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			return dis.readInt();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}
	
	public static Float deserializeFloat(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			return dis.readFloat();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}
	
	public static Double deserializeDouble(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			return dis.readDouble();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}
	
	public static Boolean deserializeBoolean(byte[] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			return dis.readBoolean();
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}
	
	public static String deserializeString(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8");
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}
	
}
