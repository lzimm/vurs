// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.convert;

import java.util.*;

/**
 * Convert an Object in map of key/value pairs by projecting them using the given projectors
 * @author Mario Fusco
 */
public class ProjectConverter<F> implements Converter<F, Map<String, Object>> {

    private final Converter<F, Map.Entry<String, Object>>[] projectors;

    public ProjectConverter(Converter<F, Map.Entry<String, Object>>... projectors) {
        this.projectors = projectors;
    }

    public Map<String, Object> convert(F from) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Converter<F, Map.Entry<String, Object>> projector : projectors) {
            Map.Entry<String, Object> entry = projector.convert(from);
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
