package com.engiweb.framework.dbaccess.pool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class JDCConnection implements Connection {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(JDCConnection.class.getName());
	private JDCConnectionPool _pool = null;
	private Connection _conn = null;
	private boolean _inuse = false;
	private long _timestamp = 0;

	public JDCConnection(Connection conn, JDCConnectionPool pool) {
		_conn = conn;
		_pool = pool;
		_inuse = false;
		_timestamp = 0;
	} // public JDCConnection(Connection conn, JDCConnectionPool pool)

	public synchronized boolean lease() {
		if (_inuse)
			return false;
		else {
			_inuse = true;
			_timestamp = System.currentTimeMillis();
			return true;
		} // if (_inuse) else
	} // public synchronized boolean lease()

	public boolean validate() {
		try {
			_conn.getMetaData();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "JDCConnection::validate: _conn.getMetaData()", ex);

			return false;
		} // catch (Exception ex)
		return true;
	} // public boolean validate()

	public boolean inUse() {
		return _inuse;
	} // public boolean inUse()

	public long getLastUse() {
		return _timestamp;
	} // public long getLastUse()

	public void close() throws SQLException {
		_pool.returnConnection(this);
	} // public void close() throws SQLException

	public void forceClose() throws SQLException {
		_conn.close();
	} // public void forceClose() throws SQLException

	protected void expireLease() {
		_inuse = false;
	} // protected void expireLease()

	protected Connection getConnection() {
		return _conn;
	} // protected Connection getConnection()

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return _conn.prepareStatement(sql);
	} // public PreparedStatement prepareStatement(String sql) throws
		// SQLException

	public PreparedStatement prepareStatement(String p0, int p1, int p2) throws SQLException {
		return _conn.prepareStatement(p0, p1, p2);
	} // public PreparedStatement prepareStatement(String p0, int p1, int p2)
		// throws SQLException

	public CallableStatement prepareCall(String sql) throws SQLException {
		return _conn.prepareCall(sql);
	} // public CallableStatement prepareCall(String sql) throws SQLException

	public CallableStatement prepareCall(String p0, int p1, int p2) throws SQLException {
		return _conn.prepareCall(p0, p1, p2);
	} // public CallableStatement prepareCall(String p0, int p1, int p2)
		// throws SQLException

	public Statement createStatement() throws SQLException {
		return _conn.createStatement();
	} // public Statement createStatement() throws SQLException

	public Statement createStatement(int p0, int p1) throws SQLException {
		return _conn.createStatement(p0, p1);
	} // public Statement createStatement(int p0, int p1) throws SQLException

	public String nativeSQL(String sql) throws SQLException {
		return _conn.nativeSQL(sql);
	} // public String nativeSQL(String sql) throws SQLException

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		_conn.setAutoCommit(autoCommit);
	} // public void setAutoCommit(boolean autoCommit) throws SQLException

	public boolean getAutoCommit() throws SQLException {
		return _conn.getAutoCommit();
	} // public boolean getAutoCommit() throws SQLException

	public void commit() throws SQLException {
		_conn.commit();
	} // public void commit() throws SQLException

	public void rollback() throws SQLException {
		_conn.rollback();
	} // public void rollback() throws SQLException

	public boolean isClosed() throws SQLException {
		return _conn.isClosed();
	} // public boolean isClosed() throws SQLException

	public DatabaseMetaData getMetaData() throws SQLException {
		return _conn.getMetaData();
	} // public DatabaseMetaData getMetaData() throws SQLException

	public void setReadOnly(boolean readOnly) throws SQLException {
		_conn.setReadOnly(readOnly);
	} // public void setReadOnly(boolean readOnly) throws SQLException

	public boolean isReadOnly() throws SQLException {
		return _conn.isReadOnly();
	} // public boolean isReadOnly() throws SQLException

	public void setCatalog(String catalog) throws SQLException {
		_conn.setCatalog(catalog);
	} // public void setCatalog(String catalog) throws SQLException

	public String getCatalog() throws SQLException {
		return _conn.getCatalog();
	} // public String getCatalog() throws SQLException

	public void setTransactionIsolation(int level) throws SQLException {
		_conn.setTransactionIsolation(level);
	} // public void setTransactionIsolation(int level) throws SQLException

	public int getTransactionIsolation() throws SQLException {
		return _conn.getTransactionIsolation();
	} // public int getTransactionIsolation() throws SQLException

	public SQLWarning getWarnings() throws SQLException {
		return _conn.getWarnings();
	} // public SQLWarning getWarnings() throws SQLException

	public void clearWarnings() throws SQLException {
		_conn.clearWarnings();
	} // public void clearWarnings() throws SQLException

	public Map getTypeMap() throws SQLException {
		return _conn.getTypeMap();
	} // public Map getTypeMap() throws SQLException

	/*
	 * public Statement createStatement( int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws
	 * SQLException { return _conn.createStatement( resultSetType, resultSetConcurrency, resultSetHoldability); } //
	 * public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws
	 * SQLException
	 * 
	 * public int getHoldability() throws SQLException { return _conn.getHoldability(); } // public int getHoldability()
	 * throws SQLException
	 * 
	 * public CallableStatement prepareCall( String sql, int resultSetType, int resultSetConcurrency, int
	 * resultSetHoldability) throws SQLException { return _conn.prepareCall( sql, resultSetType, resultSetConcurrency,
	 * resultSetHoldability); } // public CallableStatement prepareCall(String sql, int resultSetType, int
	 * resultSetConcurrency, int resultSetHoldability) throws SQLException
	 * 
	 * public PreparedStatement prepareStatement( String sql, int resultSetType, int resultSetConcurrency, int
	 * resultSetHoldability) throws SQLException { return _conn.prepareStatement( sql, resultSetType,
	 * resultSetConcurrency, resultSetHoldability); } // public PreparedStatement prepareStatement(String sql, int
	 * resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	 * 
	 * public PreparedStatement prepareStatement( String sql, int autoGeneratedKeys) throws SQLException { return
	 * _conn.prepareStatement(sql, autoGeneratedKeys); } // public PreparedStatement prepareStatement(String sql, int
	 * autoGeneratedKeys) throws SQLException
	 * 
	 * public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return
	 * _conn.prepareStatement(sql, columnIndexes); } // public PreparedStatement prepareStatement(String sql, int[]
	 * columnIndexes) throws SQLException
	 * 
	 * public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return
	 * _conn.prepareStatement(sql, columnNames); } // public PreparedStatement prepareStatement(String sql, String[]
	 * columnNames) throws SQLException
	 * 
	 * public void releaseSavepoint(Savepoint savepoint) throws SQLException { _conn.releaseSavepoint(savepoint); } //
	 * public void releaseSavepoint(Savepoint savepoint) throws SQLException
	 * 
	 * public void rollback(Savepoint savepoint) throws SQLException { _conn.rollback(savepoint); } // public void
	 * rollback(Savepoint savepoint) throws SQLException
	 * 
	 * public void setHoldability(int holdability) throws SQLException { _conn.setHoldability(holdability); } // public
	 * void setHoldability(int holdability) throws SQLException
	 * 
	 * public Savepoint setSavepoint() throws SQLException { return _conn.setSavepoint(); } // public Savepoint
	 * setSavepoint() throws SQLException
	 * 
	 * public Savepoint setSavepoint(String name) throws SQLException { return _conn.setSavepoint(name); } // public
	 * Savepoint setSavepoint(String name) throws SQLException
	 */

	@Override
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClientInfo(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValid(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHoldability(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Savepoint setSavepoint(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
} // public class JDCConnection implements Connection
