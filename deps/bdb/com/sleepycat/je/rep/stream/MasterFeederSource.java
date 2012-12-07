/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002-2010 Oracle.  All rights reserved.
 *
 */
package bdb.com.sleepycat.je.rep.stream;

import java.io.IOException;

import bdb.com.sleepycat.je.DatabaseException;
import bdb.com.sleepycat.je.config.EnvironmentParams;
import bdb.com.sleepycat.je.dbi.EnvironmentImpl;
import bdb.com.sleepycat.je.rep.impl.node.NameIdPair;
import bdb.com.sleepycat.je.rep.vlsn.VLSNIndex;
import bdb.com.sleepycat.je.utilint.DbLsn;
import bdb.com.sleepycat.je.utilint.VLSN;

/**
 * Implementation of a master node acting as a FeederSource. The
 * MasterFeederSource is stateful, because it keeps its own FeederReader which
 * acts as a cursor or scanner across the log files, so it can only be used by
 * a single Feeder.
 */
public class MasterFeederSource implements FeederSource {

    private final FeederReader feederReader;

    public MasterFeederSource(EnvironmentImpl envImpl,
                              VLSNIndex vlsnIndex,
                              NameIdPair nameIdPair)
        throws DatabaseException {

        int readBufferSize =
            envImpl.getConfigManager().getInt
            (EnvironmentParams.LOG_ITERATOR_READ_SIZE);

        feederReader = new FeederReader(envImpl,
                                        vlsnIndex,
                                        DbLsn.NULL_LSN, // startLsn
                                        readBufferSize,
                                        nameIdPair);
    }

    /*
     * @see bdb.com.sleepycat.je.rep.stream.FeederSource#init
     */
    public void init(VLSN startVLSN)
        throws DatabaseException, IOException {

        feederReader.initScan(startVLSN);
    }

    /*
     * @see bdb.com.sleepycat.je.rep.stream.FeederSource#getLogRecord
     * (bdb.com.sleepycat.je.utilint.VLSN, int)
     */
    public OutputWireRecord getWireRecord(VLSN vlsn, int waitTime)
        throws DatabaseException, InterruptedException, IOException {

        try {
            return feederReader.scanForwards(vlsn, waitTime);
        } catch (DatabaseException e) {
            /* Add more information */
            e.addErrorMessage
                ("MasterFeederSource fetching vlsn=" + vlsn +
                 " waitTime=" + waitTime);
            throw e;
        }
    }

    public String dumpState() {
        return feederReader.dumpState();
    }
}