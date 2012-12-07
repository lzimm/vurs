// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.closure;

/**
 * @author Mario Fusco
 */
interface Invokable {

    Object invoke(Object obj, Object... args);

    boolean isStatic();
}
