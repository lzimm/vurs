package net.vurs.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Reflection {
	
	public static Class<?> getParamType(Field field, int i) {
		return getParamType(field.getGenericType(), i);
	}

	public static Class<?> getParamType(Class<?> cls, int i) {
		return getParamType(cls.getGenericSuperclass(), i);
	}
	
	public static Class<?> getParamType(Type type, int i) {
		if (type.equals(Object.class)) return Object.class;
		ParameterizedType aType = (ParameterizedType) type;
		Object typeArg = aType.getActualTypeArguments()[i];
		if (typeArg.getClass().equals(Class.class)) {
			return (Class<?>) typeArg;
		} else {
			ParameterizedType subType = (ParameterizedType) typeArg;
			return (Class<?>) subType.getRawType();
		}
	}
	
	public static ParameterizedType getParamSubType(Field field, int i) {
		return getParamSubType(field.getGenericType(), i);
	}
	
	public static ParameterizedType getParamSubType(Class<?> cls, int i) {
		return getParamSubType(cls.getGenericSuperclass(), i);
	}
	
	public static ParameterizedType getParamSubType(Type type, int i) {
		try {
			ParameterizedType aType = (ParameterizedType) type;
			Object typeArg = aType.getActualTypeArguments()[i];
			return (ParameterizedType) typeArg;
		} catch (Exception e) {
			return null;
		}
	}

}
