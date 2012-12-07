package net.vurs.conn.sql;

import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vurs.conn.Connection;
import net.vurs.util.ErrorControl;

public class SQLConnection extends Connection {
	private static String URL = "jdbc:mysql://localhost";
	private static String DB = "vurs";
	private static String USER = "root";
	private static String PASS = "";

    private java.sql.Connection conn = null;

	public SQLConnection(String poolKey) {
    	super(poolKey);
    	
		setupTransport();
	}

	private void setupTransport() {        
        try {
        	StringBuilder mysql = new StringBuilder(URL)
        								.append('/')
        								.append(DB)
        								.append("?user=")
        								.append(USER)
        								.append("&password=")
        								.append(PASS);

        	this.conn = DriverManager.getConnection(mysql.toString());
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
    }
	
	@Override
	protected void close() {
		try {
			this.conn.close();
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public List<Map<String, Object>> query(String query) {		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = this.conn.createStatement();
			rs = st.executeQuery(query);
			
			ResultSetMetaData md = rs.getMetaData();
			int cols = md.getColumnCount();
			
			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>();
				for (int i = 1; i <= cols; i++) {
					row.put(md.getColumnLabel(i), rs.getObject(i));
				}
				ret.add(row);
			}
		} catch (Exception e) {
			ErrorControl.logException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					ErrorControl.logException(e);
				}
			}
			
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {
					ErrorControl.logException(e);
				}
			}
		}
		
		return ret;
	}
	
	public int update(String query) {
		this.logger.debug(String.format("Running update: %s", query));
		
		int ret = 0;
		
		Statement st = null;
		
		try {
			st = this.conn.createStatement();
			ret = st.executeUpdate(query);
		} catch (Exception e) {
			ErrorControl.logException(e);
		} finally {			
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {
					ErrorControl.logException(e);
				}
			}
		}
		
		return ret;
	}
	
	public Object insert(String query) {
		this.logger.debug(String.format("Running insert: %s", query));
		
		Object ret = null;
		
		Statement st = null;
		
		try {
			st = this.conn.createStatement();
			if (st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) ret = rs.getObject(1);
				rs.close();
			}
		} catch (Exception e) {
			ErrorControl.logException(e);
		} finally {			
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {
					ErrorControl.logException(e);
				}
			}
		}
		
		return ret;
	}
	
}
