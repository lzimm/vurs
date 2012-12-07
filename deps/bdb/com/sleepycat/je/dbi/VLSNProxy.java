/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002-2010 Oracle.  All rights reserved.
 *
 */

package bdb.com.sleepycat.je.dbi;

import bdb.com.sleepycat.je.log.LogEntryHeader;
import bdb.com.sleepycat.je.log.entry.LogEntry;

/**
 * The VLSNProxy is a handle for invoking VLSN tracking at recovery time.
 */
public interface VLSNProxy {

    public void trackMapping(long lsn,
                             LogEntryHeader currentEntryHeader,
                             LogEntry targetLogEntry);
}
