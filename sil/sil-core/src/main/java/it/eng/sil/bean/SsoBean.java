package it.eng.sil.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutorObjectExt;

/**
 * @author vuoto
 * 
 */

public class SsoBean {
	private DataConnection connSIL = null;
	private DataConnection connDWH = null;

	private int cdnUtente;
	private String username;
	boolean mustChangePwdNextLogin = false;

	private final String UPDATE_SQL_SIL = "UPDATE TS_UTENTE SET STRPASSWORD=?, DTMCAMBIOPWD=SYSDATE WHERE CDNUT=?";
	private final String UPDATE_SQL_SIL_MUST_CHANGE = "UPDATE TS_UTENTE SET STRPASSWORD=?, DTMCAMBIOPWD=NULL WHERE CDNUT=?";
	private final String SELECT_USERNAME_SIL = "SELECT STRLOGIN FROM TS_UTENTE WHERE CDNUT=?";
	private final String SELECT_USERNAME_DWH = "SELECT ID USERNAME FROM EXO_USER where ID=?";
	private final String UPDATE_SQL_DWH = "UPDATE EXO_USER SET PASSWORD=? WHERE ID=?";

	private final String UPDATE_SQL_STORICO_PWD_SIL = "insert into ts_utente_password " + "	(PRGUTENTEPWD, "
			+ "	  CDNUTENTE," + "	  STRPASSWORD," + "	  DTMCAMBIOPWD," + "	  CDNUTINS," + "   DTMINS,"
			+ "	  CDNUTMOD," + "	  DTMMOD)" + "	values" + "	(s_ts_utente_password.nextval," + "	?," + "	?,"
			+ "	sysdate," + "	?," + "	sysdate," + "	?," + " sysdate)";

	public static void main(String[] args) {
	}

	private SsoBean() {
	}

	public SsoBean(DataConnection _connSIL, DataConnection _connDWH, int _cdnUtente) throws Exception {
		this.connSIL = _connSIL;
		this.connDWH = _connDWH;
		this.cdnUtente = _cdnUtente;
	}

	private void changePswdSIL(String newPwd) throws Exception {
		QueryExecutorObjectExt qo = new QueryExecutorObjectExt();
		qo.setDataConnection(connSIL);
		qo.setTransactional(true);
		if (mustChangePwdNextLogin)
			qo.setStatement(UPDATE_SQL_SIL_MUST_CHANGE);
		else
			qo.setStatement(UPDATE_SQL_SIL);

		qo.setType(QueryExecutorObjectExt.UPDATE);

		List inputParameters = new ArrayList(2);
		inputParameters.add(connSIL.createDataField("password", Types.VARCHAR, newPwd));
		inputParameters.add(connSIL.createDataField("cdnUtente", Types.BIGINT, new BigDecimal(cdnUtente)));
		qo.setInputParameters(inputParameters);

		Object esito = qo.execExt();
		if (esito == null) {
			throw new Exception("Errore nel cambio della password nel DB del SIL\tCdnUtente:" + cdnUtente);
		}

		// aggiorno la tabella STORICO PASWORD
		QueryExecutorObjectExt qoPwd = new QueryExecutorObjectExt();
		qoPwd.setDataConnection(connSIL);
		qoPwd.setTransactional(true);
		qoPwd.setStatement(UPDATE_SQL_STORICO_PWD_SIL);
		qoPwd.setType(QueryExecutorObjectExt.INSERT);

		List inputParametersPwd = new ArrayList(4);
		inputParametersPwd.add(connSIL.createDataField("cdnUtente", Types.BIGINT, new BigDecimal(cdnUtente)));
		inputParametersPwd.add(connSIL.createDataField("password", Types.VARCHAR, newPwd));
		inputParametersPwd.add(connSIL.createDataField("cdnUtente", Types.BIGINT, new BigDecimal(cdnUtente)));
		inputParametersPwd.add(connSIL.createDataField("cdnUtente", Types.BIGINT, new BigDecimal(cdnUtente)));
		qoPwd.setInputParameters(inputParametersPwd);

		Object esitoPwd = qoPwd.execExt();
		if (esitoPwd == null) {
			throw new Exception("Errore nel cambio della password nel DB del SIL\tCdnUtente:" + cdnUtente);
		}

	}

	private void changePswdDwh(String newPwd) throws Exception {

		// 1. si ricava lo username dal SIL
		QueryExecutorObjectExt qoSIL = new QueryExecutorObjectExt();
		qoSIL.setDataConnection(connSIL);
		qoSIL.setTransactional(true);
		qoSIL.setStatement(SELECT_USERNAME_SIL);
		qoSIL.setType(QueryExecutorObjectExt.SELECT);

		List inputParameters = new ArrayList(1);
		inputParameters.add(connSIL.createDataField("cdnUtente", Types.BIGINT, new BigDecimal(cdnUtente)));
		qoSIL.setInputParameters(inputParameters);

		SourceBean esitoSB = (SourceBean) qoSIL.execExt();
		this.username = (String) esitoSB.getAttribute("ROW.STRLOGIN");

		// 2. Si controlla che tale utente abbia accesso anche al DWH

		QueryExecutorObjectExt qoDWH = new QueryExecutorObjectExt();
		qoDWH.setDataConnection(connDWH);
		qoDWH.setTransactional(true);

		qoDWH.setStatement(SELECT_USERNAME_DWH);
		qoDWH.setType(QueryExecutorObjectExt.SELECT);

		inputParameters = new ArrayList(2);
		inputParameters.add(connDWH.createDataField("username", Types.VARCHAR, username));
		qoDWH.setInputParameters(inputParameters);

		esitoSB = (SourceBean) qoDWH.execExt();
		String usernameDWH = (String) esitoSB.getAttribute("ROW.USERNAME");

		if (usernameDWH != null) {

			// 3. Si aggiorna la password

			qoDWH.setStatement(UPDATE_SQL_DWH);
			qoDWH.setType(QueryExecutorObjectExt.UPDATE);
			qoDWH.setTransactional(true);

			inputParameters = new ArrayList(2);
			inputParameters.add(connSIL.createDataField("password", Types.VARCHAR, newPwd));
			inputParameters.add(connSIL.createDataField("login", Types.VARCHAR, usernameDWH));
			qoDWH.setInputParameters(inputParameters);

			Object esito = qoDWH.execExt();
			if (esito == null)
				throw new Exception("Errore nel cambio della password nel DB del SIL\tCdnUtente:" + cdnUtente);
		}

	}

	public void changePswd(String newPwd) throws Exception {

		if (connSIL != null)
			changePswdSIL(newPwd);

		if (connDWH != null)
			changePswdDwh(newPwd);

	}

	/**
	 * @return
	 */
	public boolean isToChangePwdNextLogin() {
		return mustChangePwdNextLogin;
	}

	/**
	 * @param b
	 */
	public void setMustChangePwdNextLogin(boolean b) {
		mustChangePwdNextLogin = b;
	}

}
