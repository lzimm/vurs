package net.vurs.common.constructor;

import java.util.ArrayList;
import java.util.List;

import net.vurs.common.functional.F1;

public class ArrayListConstructor<K, T> implements F1<K, List<T>> {

	@Override
	public List<T> f(K a) {
		return new ArrayList<T>();
	}

}
