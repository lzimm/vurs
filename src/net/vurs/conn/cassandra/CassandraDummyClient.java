package net.vurs.conn.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.vurs.common.ConcurrentConstructingHashMap;
import net.vurs.common.ConstructingHashMap;
import net.vurs.common.constructor.ArrayListConstructor;
import net.vurs.common.constructor.ConcurrentHashMapConstructor;

import org.apache.cassandra.thrift.AuthenticationException;
import org.apache.cassandra.thrift.AuthenticationRequest;
import org.apache.cassandra.thrift.AuthorizationException;
import org.apache.cassandra.thrift.Cassandra.Iface;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.TokenRange;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

@SuppressWarnings("unused")
public class CassandraDummyClient implements Iface {

	private static final ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<ColumnOrSuperColumn>>> 
		store = new ConcurrentConstructingHashMap<String, ConcurrentHashMap<String, ArrayList<ColumnOrSuperColumn>>>(
				new ConcurrentHashMapConstructor<String, String, ArrayList<ColumnOrSuperColumn>>());
	
	@Override
	public void batch_mutate(String keyspace,
			Map<String, Map<String, List<Mutation>>> mutationMap, ConsistencyLevel consistencyLevel)
			throws InvalidRequestException, UnavailableException,
			TimedOutException, TException {
		for (Entry<String, Map<String, List<Mutation>>> e: mutationMap.entrySet()) {
			String key = e.getKey();
			for (Entry<String, List<Mutation>> mutations: e.getValue().entrySet()) {
				String family = mutations.getKey();
				for (Mutation m: mutations.getValue()) {
					CassandraDummyClient.store.get(family).get(key).add(m.column_or_supercolumn);
				}
			}
		}
	}
	
	@Override
	public List<KeySlice> get_range_slices(String keyspace, ColumnParent columnParent,
			SlicePredicate slicePredicate, KeyRange keyRange, ConsistencyLevel consistencyLevel)
			throws InvalidRequestException, UnavailableException,
			TimedOutException, TException {
		List<KeySlice> ret = new ArrayList<KeySlice>();
		
		
		
		return ret;
	}

	@Override
	public Map<String, List<ColumnOrSuperColumn>> multiget_slice(String keyspace,
			List<String> keys, ColumnParent columnParent, SlicePredicate slicePredicate,
			ConsistencyLevel consistencyLevel) throws InvalidRequestException,
			UnavailableException, TimedOutException, TException {
		Map<String, List<ColumnOrSuperColumn>> ret = new ConstructingHashMap<String, List<ColumnOrSuperColumn>>(new ArrayListConstructor<String, ColumnOrSuperColumn>());
		
		for (String key: keys) {
			List<ColumnOrSuperColumn> cols = CassandraDummyClient.store.get(key).get(columnParent.getColumn_family());
			if (columnParent.isSetSuper_column()) {
				for (ColumnOrSuperColumn superCol: cols) {
					if (superCol.isSetSuper_column() && Arrays.equals(superCol.getSuper_column().getName(), columnParent.getSuper_column())) {
						if (slicePredicate.isSetSlice_range()) {
							for (Column col: superCol.getSuper_column().getColumns()) {
								
							}							
						} else if (slicePredicate.isSetColumn_names()) {
							for (Column col: superCol.getSuper_column().getColumns()) {
								
							}
						}
					}
				}
			} else {
				for (ColumnOrSuperColumn colOr: cols) {
					Column col = colOr.getColumn();
					if (slicePredicate.isSetSlice_range()) {
						
					} else if (slicePredicate.isSetColumn_names()) {
						
					}
				}
			}
		}
		
		return ret;
	}

	@Override
	public void remove(String keyspace, String key, ColumnPath columnPath, long timestamp,
			ConsistencyLevel consistencyLevel) throws InvalidRequestException,
			UnavailableException, TimedOutException, TException {
		List<ColumnOrSuperColumn> cols = CassandraDummyClient.store.get(key).get(columnPath.getColumn_family());
		for (ColumnOrSuperColumn col: cols) {
			if (col.isSetColumn() && columnPath.isSetColumn()) {
				if (Arrays.equals(col.getColumn().getName(), columnPath.getColumn())) {
					CassandraDummyClient.store.get(key).get(columnPath.getColumn_family()).remove(col);
				}
			} else if (col.isSetSuper_column() && columnPath.isSetSuper_column()) {
				if (Arrays.equals(col.getSuper_column().getName(), columnPath.getSuper_column())) {
					CassandraDummyClient.store.get(key).get(columnPath.getColumn_family()).remove(col);
				}				
			}
		}
	}

	@Override public void batch_insert(String arg0, String arg1, Map<String, List<ColumnOrSuperColumn>> arg2, ConsistencyLevel arg3) throws InvalidRequestException, UnavailableException, TimedOutException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public String describe_cluster_name() throws TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public Map<String, Map<String, String>> describe_keyspace(String arg0) throws NotFoundException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public Set<String> describe_keyspaces() throws TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public List<TokenRange> describe_ring(String arg0) throws TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public List<String> describe_splits(String arg0, String arg1, int arg2) throws TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public String describe_version() throws TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public ColumnOrSuperColumn get(String arg0, String arg1, ColumnPath arg2, ConsistencyLevel arg3) throws InvalidRequestException, NotFoundException, UnavailableException, TimedOutException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public int get_count(String arg0, String arg1, ColumnParent arg2, ConsistencyLevel arg3) throws InvalidRequestException, UnavailableException, TimedOutException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public List<KeySlice> get_range_slice(String arg0, ColumnParent arg1, SlicePredicate arg2, String arg3, String arg4, int arg5, ConsistencyLevel arg6) throws InvalidRequestException, UnavailableException, TimedOutException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public List<ColumnOrSuperColumn> get_slice(String arg0, String arg1, ColumnParent arg2, SlicePredicate arg3, ConsistencyLevel arg4) throws InvalidRequestException, UnavailableException, TimedOutException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public List<String> get_string_list_property(String arg0) throws TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public String get_string_property(String arg0) throws TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public void insert(String arg0, String arg1, ColumnPath arg2, byte[] arg3, long arg4, ConsistencyLevel arg5) throws InvalidRequestException, UnavailableException, TimedOutException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public void login(String arg0, AuthenticationRequest arg1) throws AuthenticationException, AuthorizationException, TException { throw new Error("Unsupported operation in dummy client"); }
	@Override public Map<String, ColumnOrSuperColumn> multiget(String arg0, List<String> arg1, ColumnPath arg2, ConsistencyLevel arg3) throws InvalidRequestException, UnavailableException, TimedOutException, TException { throw new Error("Unsupported operation in dummy client"); }
	
}
