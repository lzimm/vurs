package net.vurs.common.constructor;

import net.vurs.common.functional.F1;

public class FloatConstructor<K> implements F1<K, Float> {

	@Override
	public Float f(K a) {
		return new Float(0);
	}

}
