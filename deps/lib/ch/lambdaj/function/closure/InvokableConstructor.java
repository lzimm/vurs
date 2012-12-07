// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.closure;

import java.lang.reflect.*;

/**
 * @author Mario Fusco
 */
class InvokableConstructor implements Invokable {

    private final Constructor<?> constructor;

    public InvokableConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Object invoke(Object obj, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new WrongClosureInvocationException("Error invoking constroctor for " + constructor.getDeclaringClass(), e);
        }
    }

    public boolean isStatic() {
        return true;
    }
}
