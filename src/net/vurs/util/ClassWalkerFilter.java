package net.vurs.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class ClassWalkerFilter {
	
	public static interface Interface<T> {}
	
	public static ClassWalkerFilter ACCEPT_ANY = new ClassWalkerFilter() {
		@Override
		public boolean visit(Class<?> cls) { return true; }
	};
	
	public static ClassWalkerFilter extending(final Class<?> superClass) { return extending(superClass, new HashSet<Class<?>>()); }
	public static ClassWalkerFilter extending(final Class<?> superClass, final Class<?> ... excluding) { return extending(superClass, new HashSet<Class<?>>(Arrays.asList(excluding))); }
	public static ClassWalkerFilter extending(final Class<?> superClass, final Set<Class<?>> excluding) {
		return new ClassWalkerFilter() {
			@Override
			public boolean visit(Class<?> cls) {
				return !excluding.contains(superClass) && !superClass.equals(cls) && superClass.isAssignableFrom(cls);
			}
		};
	}

	public static ClassWalkerFilter extendingWithParam(final Class<?> c, final Class<?> p) { return extendingWithParam(c, p, new HashSet<Class<?>>()); }
	public static ClassWalkerFilter extendingWithParam(final Class<?> c, final Class<?> p, final Class<?> ... excluding) { return extendingWithParam(c, p, new HashSet<Class<?>>(Arrays.asList(excluding))); }
	public static ClassWalkerFilter extendingWithParam(final Class<?> c, final Class<?> p, final Set<Class<?>> excluding) {
		return new ClassWalkerFilter() {
			@Override
			public boolean visit(Class<?> cls) {
				try {
					if (!excluding.contains(cls) && !c.equals(cls) && c.isAssignableFrom(cls) && Reflection.getParamType(cls, 0).equals(p)) {
						return true;
					}
				} catch (Exception e) {
					return false;
				}
				
				return false;
			}
		};
	}
	
	public static ClassWalkerFilter excluding(final Class<?> ... classes) { return excluding(new HashSet<Class<?>>(Arrays.asList(classes))); }
	public static ClassWalkerFilter excluding(final Set<Class<?>> classes) {
		return new ClassWalkerFilter() {
			@Override
			public boolean visit(Class<?> cls) {
				return !classes.contains(cls);
			}
		};
	}
	
	public static ClassWalkerFilter isConcreteClass() {
		return new ClassWalkerFilter() {
			@Override
			public boolean visit(Class<?> cls) {
				return !cls.isInterface() && !Arrays.asList(cls.getInterfaces()).contains(Interface.class);
			}
		};
	}
	
	public abstract boolean visit(Class<?> cls);
	
}
