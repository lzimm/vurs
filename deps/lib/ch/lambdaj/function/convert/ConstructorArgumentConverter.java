// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.convert;

import java.lang.reflect.*;
import java.util.*;

/**
 * Creates an object of the given Class by invoking its constructor passing to it the values taken
 * from the object to be converted using the given arguments.
 * @author Mario Fusco
 */
public class ConstructorArgumentConverter<F, T> implements Converter<F, T> {

    private Constructor<T> constructor;

    private final List<ArgumentConverter<F, Object>> argumentConverters = new ArrayList<ArgumentConverter<F, Object>>();

    public ConstructorArgumentConverter(Class<T> clazz, Object... arguments) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (isCompatible(constructor, arguments)) {
                this.constructor = (Constructor<T>)constructor;
                break;
            }
        }

        if (constructor == null)
            throw new RuntimeException("Unable to find a constructor of " + clazz.getName() + " compatible with the given arguments");

        if (arguments != null)
            for (Object argument : arguments) argumentConverters.add(new ArgumentConverter<F, Object>(argument));
    }

    private boolean isCompatible(Constructor<?> constructor, Object... arguments) {
        try {
            constructor.newInstance(arguments);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public T convert(F from) {
        Object[] initArgs = new Object[argumentConverters.size()];
        int i = 0;
        for (ArgumentConverter<F, Object> argumentConverter : argumentConverters)
            initArgs[i++] = argumentConverter.convert(from);
        try {
            return constructor.newInstance(initArgs);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create an object of class " + constructor.getDeclaringClass().getName(), e);
        }
    }
}
