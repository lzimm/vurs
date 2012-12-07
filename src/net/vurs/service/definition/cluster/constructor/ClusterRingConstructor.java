package net.vurs.service.definition.cluster.constructor;

import net.vurs.common.functional.F1;
import net.vurs.service.definition.cluster.ClusterRing;
import net.vurs.service.definition.cluster.layer.ClusterLayer;

public class ClusterRingConstructor implements F1<Class<? extends ClusterLayer>, ClusterRing> {

	@Override
	public ClusterRing f(Class<? extends ClusterLayer> a) {
		return new ClusterRing(a);
	}

}
