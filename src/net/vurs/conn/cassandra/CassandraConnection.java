package net.vurs.conn.cassandra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vurs.conn.Connection;
import net.vurs.util.ErrorControl;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Cassandra.Iface;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;

public class CassandraConnection extends Connection {
	public static String KEYSPACE = "VURS";
	
	private TTransport tr = null;
	private TProtocol proto = null;
	private Iface client = null;
	
    public CassandraConnection(String poolKey) {
    	super(poolKey);
    	
    	setupTransport();
    }

	private void setupTransport() {
        this.tr = new TSocket("localhost", 9160);
        this.proto = new TBinaryProtocol(this.tr);
        this.client = new Client(this.proto);
        
        try {
			this.tr.open();
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
    }
	
	@Override
	protected void close() {
		try {
			this.tr.close();
		} catch (Exception e) {}
	}
	
	public Map<String, List<ColumnOrSuperColumn>> get(List<String> keys, String family, String superColumn, SlicePredicate predicate, ConsistencyLevel consistencyLevel) {
		try {
			ColumnParent columnParent = new ColumnParent();
			columnParent.column_family = family;
			
			if (superColumn != null) {
				columnParent.super_column = superColumn.getBytes("UTF-8");
			}
			
			return this.client.multiget_slice(KEYSPACE, keys, columnParent, predicate, consistencyLevel);
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}
	
	public List<KeySlice> range(String family, SlicePredicate predicate, String start, String end, int limit, ConsistencyLevel consistencyLevel) {
		ColumnParent columnParent = new ColumnParent();
		columnParent.column_family = family;
		
		KeyRange range = new KeyRange();
		range.count = limit;
		range.start_key = start;
		range.end_key = end;
		
		try {
			return this.client.get_range_slices(KEYSPACE, columnParent, predicate, range, consistencyLevel);
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		}
	}

	public void insert(String key, String family, List<ColumnOrSuperColumn> values, ConsistencyLevel consistencyLevel) {
		try {
			Map<String, Map<String, List<Mutation>>> mutationMap = new HashMap<String, Map<String, List<Mutation>>>();
			
			Map<String, List<Mutation>> keyMutations = new HashMap<String, List<Mutation>>();
			List<Mutation> mutationList = new ArrayList<Mutation>();
			
			for (ColumnOrSuperColumn csc: values) {
				Mutation m = new Mutation();		
				m.column_or_supercolumn = csc;
				mutationList.add(m);
			}
			
			keyMutations.put(family, mutationList);
			mutationMap.put(key, keyMutations);
			
			this.logger.debug(String.format("Inserting into %s: %s", key,keyMutations));
			
			this.client.batch_mutate(KEYSPACE, mutationMap, consistencyLevel);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public void remove(String key, String family, ConsistencyLevel consistencyLevel) {
		remove(key, family, null, null, consistencyLevel);
	}
	
	public void remove(String key, String family, String column, ConsistencyLevel consistencyLevel) {
		remove(key, family, null, column, consistencyLevel);
	}
	
	public void remove(String key, String family, String superColumn, String column, ConsistencyLevel consistencyLevel) {
		try {
			ColumnPath colPath = new ColumnPath();
			colPath.column_family = family;
			
			if (superColumn != null) {
				colPath.super_column = superColumn.getBytes("UTF-8");
			}
			
			if (superColumn != null) {
				colPath.column = column.getBytes("UTF-8");
			}

			this.client.remove(KEYSPACE, key, colPath, System.currentTimeMillis(), consistencyLevel);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
}
