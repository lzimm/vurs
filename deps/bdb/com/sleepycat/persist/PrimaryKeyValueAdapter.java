/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002-2010 Oracle.  All rights reserved.
 *
 */

package bdb.com.sleepycat.persist;

import bdb.com.sleepycat.bind.EntryBinding;
import bdb.com.sleepycat.je.DatabaseEntry;

/**
 * A ValueAdapter where the "value" is the primary key.
 *
 * @author Mark Hayes
 */
class PrimaryKeyValueAdapter<V> implements ValueAdapter<V> {

    private EntryBinding keyBinding;

    PrimaryKeyValueAdapter(Class<V> keyClass, EntryBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    public DatabaseEntry initKey() {
        return new DatabaseEntry();
    }

    public DatabaseEntry initPKey() {
        return new DatabaseEntry();
    }

    public DatabaseEntry initData() {
        return BasicIndex.NO_RETURN_ENTRY;
    }

    public void clearEntries(DatabaseEntry key,
                             DatabaseEntry pkey,
                             DatabaseEntry data) {
        key.setData(null);
        pkey.setData(null);
    }

    public V entryToValue(DatabaseEntry key,
                          DatabaseEntry pkey,
                          DatabaseEntry data) {
        return (V) keyBinding.entryToObject(pkey);
    }

    public void valueToData(V value, DatabaseEntry data) {
        throw new UnsupportedOperationException
            ("Cannot change the data in a key-only index");
    }
}
