/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002-2010 Oracle.  All rights reserved.
 *
 */

package bdb.com.sleepycat.je.rep.impl.node;

import java.net.InetSocketAddress;
import java.util.Set;

import bdb.com.sleepycat.je.DatabaseException;
import bdb.com.sleepycat.je.rep.elections.Utils;
import bdb.com.sleepycat.je.rep.impl.RepGroupImpl;
import bdb.com.sleepycat.je.rep.impl.TextProtocol.RequestMessage;
import bdb.com.sleepycat.je.rep.impl.node.NameIdPair;
import bdb.com.sleepycat.je.rep.impl.node.RepNode;
import bdb.com.sleepycat.je.rep.monitor.GroupChangeEvent.GroupChangeType;
import bdb.com.sleepycat.je.rep.monitor.LeaveGroupEvent.LeaveReason;
import bdb.com.sleepycat.je.rep.monitor.MonitorService;
import bdb.com.sleepycat.je.rep.monitor.Protocol.GroupChange;
import bdb.com.sleepycat.je.rep.monitor.Protocol.JoinGroup;
import bdb.com.sleepycat.je.rep.monitor.Protocol.LeaveGroup;
import bdb.com.sleepycat.je.utilint.LoggerUtils;

/**
 * @hidden
 * For internal use only.
 *
 * The class for firing MonitorChangeEvents. 
 *
 * Each time when there happens a MonitorChangeEvents, it refreshes the group
 * information so that it can send messages to current monitors.
 */
public class MonitorEventManager {

    /* The time when this node joins the group, 0 if it hasn't joined yet. */
    private long joinTime = 0L;

    private final RepNode repNode;

    public MonitorEventManager(RepNode repNode) {
        this.repNode = repNode;
    }

    /* Return the time when JoinGroupEvent for this RepNode fires. */
    public long getJoinTime() {
        return joinTime;
    }

    /* Disable the LeaveGroupEvent because the node is abnormally closed. */
    public void disableLeaveGroupEvent() {
        joinTime = 0L;
    }

    /**
     * Fire a GroupChangeEvent.
     */
    public void notifyGroupChange(String nodeName, GroupChangeType opType) 
        throws DatabaseException {

        RepGroupImpl repGroup = repNode.getGroup();
        GroupChange changeEvent = 
            getProtocol(repGroup).new GroupChange(repGroup, nodeName, opType);
        refreshMonitors(repGroup, changeEvent);
    }

    /**
     * Fire a JoinGroupEvent.
     */
    public void notifyJoinGroup() 
        throws DatabaseException {

        if (joinTime > 0) {
            /* Already notified. */
            return;
        }

        joinTime = System.currentTimeMillis();
        RepGroupImpl repGroup = repNode.getGroup();
        JoinGroup joinEvent = 
            getProtocol(repGroup).new JoinGroup(repNode.getNodeName(),
                                                repNode.getMasterName(), 
                                                joinTime);
        refreshMonitors(repGroup, joinEvent);
    }

    /**
     * Fire a LeaveGroupEvent.
     */
    public void notifyLeaveGroup(LeaveReason reason)
        throws DatabaseException {

        if (joinTime == 0) {
            /* No join event, therefore no matching leave event. */
            return;
        }

        RepGroupImpl repGroup = repNode.getGroup();
        LeaveGroup leaveEvent = 
            getProtocol(repGroup).new LeaveGroup(repNode.getNodeName(),
                                                 repNode.getMasterName(), 
                                                 reason, 
                                                 joinTime, 
                                                 System.currentTimeMillis());
        refreshMonitors(repGroup, leaveEvent);
    }

    /* Create a monitor protocol. */ 
    private bdb.com.sleepycat.je.rep.monitor.Protocol 
        getProtocol(RepGroupImpl repGroup) {
        
        return new bdb.com.sleepycat.je.rep.monitor.Protocol
            (repGroup.getName(), NameIdPair.NOCHECK, null);
    }

    /* Refresh all the monitors with specified message. */
    private void refreshMonitors(RepGroupImpl repGroup,
                                 RequestMessage requestMessage) {
        Set<InetSocketAddress> monitors = repGroup.getMonitorSockets();
        LoggerUtils.info(repNode.getLogger(), repNode.getRepImpl(),
                         "Refreshed " + monitors.size() + " monitors.");
        /* Broadcast and forget. */
        Utils.broadcastMessage(monitors,
                               MonitorService.SERVICE_NAME,
                               requestMessage,
                               repNode.getElections().getThreadPool());
    }
}
