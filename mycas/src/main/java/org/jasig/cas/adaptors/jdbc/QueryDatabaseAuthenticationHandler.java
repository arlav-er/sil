/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.adaptors.jdbc;

import it.eng.sil.utils.SHA1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.BlockedCredentialsAuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that if provided a query that returns a password (parameter of query
 * must be username) will compare that password to a translated version of the
 * password provided by the user. If they match, then authentication succeeds.
 * Default password translator is plaintext translator.
 * 
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class QueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(QueryDatabaseAuthenticationHandler.class);
	
	private static final long WAIT_SECONDS = 300;
	private static final int MAX_ATTEMPTS = 3;

	private static final String JNDI_DS = "java:/jdbc/MyCasDS";
	private static final String SQL_N_TENTATIVI_LAST_CHECKIN = "select num_tentativi, dt_last_checkin from mycas.pf_principal where upper(username)=?";
	private static final String SQL_RESET_COUNTER = "update mycas.pf_principal set  num_tentativi=0, dt_last_checkin = current_timestamp  where upper(username)=?";
	private static final String SQL_INC_TENTATIVI = "update mycas.pf_principal set  num_tentativi=num_tentativi+1 where upper(username)=?";
	
	@NotNull
    private String sql;

    protected final boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) throws AuthenticationException {
        final String username = getPrincipalNameTransformer().transform(credentials.getUsername());
        final String password = credentials.getPassword();
        final String encryptedPassword = this.getPasswordEncoder().encode(
            password);
        
        if (username!= null && username.startsWith("\\\\")){
        	// Autenticazione tramite social network!	

        	String salted="NNN";
			try {
				salted = SHA1.encrypt(username.toUpperCase() + "<TOCHANGE>");
			} catch (Throwable e) {
	        	String msg = String.format("Problema nella generazione dello SAH1 per '%s'", username);
	        	LOG.error(msg);
	        	return false;
			}
        	
        	if (salted.equals(password)){
	        	String msg = String.format("L'utente %s ha acceduto tramite social network", username);
	        	LOG.info(msg);
	        	return true;
        	}else{
	        	String msg = String.format("L'utente %s ha cercato di accedere tramite social network in maniera NON CONFORME", username);
	        	LOG.error(msg);
	        	return false;
        	}
        }
        long numSeconds=0;
        try {

        	numSeconds=isLocked(username);
        	
        	if (numSeconds<=0){        	
        		final String dbPassword = getJdbcTemplate().queryForObject(this.sql, String.class, username);
        	
        		boolean okPassword=dbPassword.equals(encryptedPassword);
        		if (okPassword){
        			resetCounter(username);
        		} else{
        			incTentativi(username);
        		}
        		
        		return okPassword;
        		
        	} else {
//        		String msg = String.format("Troppi tentativi falliti! L'account %s rimarrà bloccato per altri %ds!", 
//        				username.toUpperCase(), numSeconds);
//        		LOG.warn(msg);
        		
        		throw new BlockedCredentialsAuthenticationException();
        	//	throw new BadCredentialsAuthenticationException();
        	}
        		
        } catch (final IncorrectResultSizeDataAccessException e) {
          
        	LOG.error("Username inesistente");
            return false;
        }
    }

    private void resetCounter(String username) {
    	execSQL(username, SQL_RESET_COUNTER);
	}
    
    private void incTentativi(String username){
    	execSQL(username, SQL_INC_TENTATIVI);
    }
    
    
    private void execSQL(String username, String SQL){
    	Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement(SQL);
			pstmt.setString(1, username.toUpperCase());
			pstmt.executeUpdate();
			pstmt.close();

		} catch (Exception ex) {
			String msg = String.format("Errore nell'esecuzione della query %s",SQL);
			LOG.error(msg, ex);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					String msg = String.format("Errore nella chiudura della connessione del CAS");
					LOG.error(msg, e);
				}
		}
    	
    }
    
    
    
    public Long isLocked(String username) {
        
        PfAuthMin pfAuthMin = getPfAuthMin(username);
        
        if (pfAuthMin.getNumTentativi() >= MAX_ATTEMPTS   && pfAuthMin.getDtLastCheckin() != null ) {
        		Date now = new java.util.Date();
        		
                // conta quanti secondi sono passati dall'ultimo tentativo fallito.
                long secondsSinceLastLogin = (now.getTime() - pfAuthMin.getDtLastCheckin().getTime()) / 1000;
                // se ha aspettato a sufficienza
                if (secondsSinceLastLogin >= WAIT_SECONDS) {
                        // resetta il contatore
                	resetCounter(username);
                    
                        return 0L;
                }
                // altrimento restituisci il numero di secondi che deve aspettare
                else {
                        return WAIT_SECONDS - secondsSinceLastLogin;
                }
        }
        // può ancora provare il login
        else {
                return 0L;
        }
}
    
    


	/**
     * @param sql The sql to set.
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }
    
    
    
	private Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			DataSource dbDS = (DataSource) ctx.lookup(JNDI_DS);
			conn = dbDS.getConnection();
		} catch (Exception dbEx) {
			String msg = String.format("Errore nell'accesso al database" );
			LOG.error(msg, dbEx);
			
			throw new SQLException(dbEx.getMessage());
		}
		return conn;
	}

    
	
	private PfAuthMin getPfAuthMin(String username) {
		PfAuthMin auth= new PfAuthMin();
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement(SQL_N_TENTATIVI_LAST_CHECKIN);
			pstmt.setString(1, username.toUpperCase());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				
				auth.setNumTentativi( rs.getInt("num_tentativi"));
				auth.setDtLastCheckin(rs.getTimestamp("dt_last_checkin"));
				
			}
			rs.close();
			pstmt.close();
			

		} catch (Exception ex) {
			String msg = String.format("Errore nel recupero delle info sull'utente %s", username);
			LOG.error(msg, ex);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					String msg = String.format("Errore nella chiudura della connessione del CAS");
					LOG.error(msg, e);
				}

		}
		return auth;

	}

	
	
	
	class PfAuthMin{
		private int numTentativi;
		private Date dtLastCheckin ;
		
		
		public int getNumTentativi() {
			return numTentativi;
		}
		public void setNumTentativi(int numTentativi) {
			this.numTentativi = numTentativi;
		}
		public Date getDtLastCheckin() {
			return dtLastCheckin;
		}
		public void setDtLastCheckin(Date dtLastCheckin) {
			this.dtLastCheckin = dtLastCheckin;
		}
		
	}
    
    
}
