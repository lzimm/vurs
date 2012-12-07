/*
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002-2010 Oracle.  All rights reserved.
 *
 */

package bdb.com.sleepycat.je.log.entry;

import bdb.com.sleepycat.je.DatabaseException;
import bdb.com.sleepycat.je.dbi.DatabaseId;
import bdb.com.sleepycat.je.dbi.EnvironmentImpl;
import bdb.com.sleepycat.je.tree.IN;

/**
 * An INContainingEntry is a log entry that contains internal nodes.
 */
public interface INContainingEntry {
        
    /**
     * @return the IN held within this log entry.
     */
    public IN getIN(EnvironmentImpl env)
        throws DatabaseException;
        
    /**
     * @return the database id held within this log entry.
     */
    public DatabaseId getDbId();

    /**
     * @return the LSN that represents this IN.
     */
    public long getLsnOfIN(long lastReadLsn);
}
