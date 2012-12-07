// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.closure;

import java.lang.reflect.*;

/**
 * @author Mario Fusco
 */
class InvokableMethod implements Invokable {

    private final Method method;

    public InvokableMethod(Method method) {
        this.method = method;
    }

    public Object invoke(Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new WrongClosureInvocationException("Error invoking " + method + " on " + obj, e);
        }
    }

    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }
}
