/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002-2010 Oracle.  All rights reserved.
 *
 */

package bdb.com.sleepycat.persist;

import bdb.com.sleepycat.je.DatabaseException;
import bdb.com.sleepycat.je.LockMode;
import bdb.com.sleepycat.util.keyrange.RangeCursor;

/**
 * The cursor for a SubIndex treats Dup and NoDup operations specially because
 * the SubIndex never has duplicates -- the keys are primary keys.  So a
 * next/prevDup operation always returns null, and a next/prevNoDup operation
 * actually does next/prev.
 *
 * @author Mark Hayes
 */
class SubIndexCursor<V> extends BasicCursor<V> {

    SubIndexCursor(RangeCursor cursor, ValueAdapter<V> adapter) {
        super(cursor, adapter, false/*updateAllowed*/);
    }

    @Override
    public EntityCursor<V> dup()
        throws DatabaseException {

        return new SubIndexCursor<V>(cursor.dup(true), adapter);
    }

    @Override
    public V nextDup(LockMode lockMode) {
        checkInitialized();
        return null;
    }

    @Override
    public V nextNoDup(LockMode lockMode)
        throws DatabaseException {

        return returnValue(cursor.getNext(key, pkey, data, lockMode));
    }

    @Override
    public V prevDup(LockMode lockMode) {
        checkInitialized();
        return null;
    }

    @Override
    public V prevNoDup(LockMode lockMode)
        throws DatabaseException {

        return returnValue(cursor.getPrev(key, pkey, data, lockMode));
    }
}