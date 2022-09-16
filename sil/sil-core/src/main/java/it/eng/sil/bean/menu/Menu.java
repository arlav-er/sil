/*
 * Creato il 29-ott-04
 * Author: vuoto
 * 
 */
package it.eng.sil.bean.menu;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.Values;

/**
 * @author vuoto
 * 
 */
public class Menu {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Menu.class.getName());

	private int cdnMenu = 0;
	// private int idxVoceMenu=0;

	private String descrizione = null;

	// private boolean standard = true;

	private it.eng.sil.bean.menu.VoceMenu voceMenuRoot;

	private int cdnMenuClonato = 0;

	/**
	 * @return
	 */
	public int getCdnMenu() {
		return cdnMenu;
	}

	/**
	 * @param i
	 */
	public void setCdnMenu(int i) {
		cdnMenu = i;
	}

	/**
	 * @return
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param string
	 */
	public void setDescrizione(String string) {
		descrizione = string;
	}

	// /**
	// * @return
	// */
	// public boolean isStandard() {
	// return standard;
	// }
	//
	// /**
	// * @param b
	// */
	// public void setStandard(boolean b) {
	// standard = b;
	// }

	/**
	 * @return
	 */
	public it.eng.sil.bean.menu.VoceMenu getVoceMenuRoot() {
		return voceMenuRoot;
	}

	/**
	 * @param menu
	 */
	public void setVoceMenuRoot(it.eng.sil.bean.menu.VoceMenu menu) {
		voceMenuRoot = menu;
	}

	public void loadNew() {

		descrizione = "Nuovo menu";
		voceMenuRoot = new VoceMenu();
		voceMenuRoot.setDescrizione("Menu");

		// Eliminato il caricamento di tutte le voci dalle funzioni
		// voluto da Angela in data 30-11-2004

		/*
		 * DataConnection dataConnection = null; SQLCommand sqlCommand = null; DataResult dataResult = null;
		 * 
		 * try { DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance(); dataConnection =
		 * dataConnectionManager.getConnection(Values.DB_SIL_DATI); // Caricamento Di tutte le funzioni String statement
		 * = SQLStatements.getStatement("FUNZIONI_MENU"); sqlCommand = dataConnection.createSelectCommand(statement);
		 * 
		 * dataResult = sqlCommand.execute();
		 * 
		 * ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject(); SourceBean sb
		 * = scrollableDataResult.getSourceBean();
		 * 
		 * List righe = sb.getAttributeAsVector("ROW");
		 * 
		 * 
		 * 
		 * 
		 * for (int i = 0; i < righe.size(); i++) { SourceBean riga = (SourceBean) righe.get(i); VoceMenu vmCurr = new
		 * VoceMenu();
		 * 
		 * String _strdescvocemenu = (String) riga.getAttribute("strDescrizione");
		 * vmCurr.setDescrizione(_strdescvocemenu);
		 * 
		 * BigDecimal _cdnfunzione = (BigDecimal) riga.getAttribute("cdnfunzione"); String _strdescfunz =
		 * _strdescvocemenu; Funzione funz = new Funzione(); funz.setCdnFunzione(_cdnfunzione.intValue());
		 * funz.setDescrizione(_strdescfunz); vmCurr.setFunzione(funz);
		 * 
		 * voceMenuRoot.getFigli().add(vmCurr); vmCurr.setPadre(voceMenuRoot); } // try { // // FileWriter file = new
		 * FileWriter("C:\\tmp\\menu_new.xml"); // file.write(toXML().toString()); // file.close(); // // } catch
		 * (IOException e) { // // TracerSingleton.log(// it.eng.sil.Values.APP_NAME,// TracerSingleton.CRITICAL,//
		 * "QueryExecutor::executeQuery:",// (Exception) e); // return; // } } catch
		 * (com.engiweb.framework.error.EMFInternalError ex) { it.eng.sil.util.TraceWrapper.fatal(
		 * _logger,"QueryExecutor::executeQuery:", (Exception) ex); } finally {
		 * com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult); }
		 * 
		 */
	}

	public void load() {

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			// Caricamento info di testata
			String statement = SQLStatements.getStatement("CARICA_MENU_HOME");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, new Integer(this.cdnMenu)));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");

			for (int i = 0; i < righe.size(); i++) {
				SourceBean riga = (SourceBean) righe.get(i);

				this.descrizione = (String) riga.getAttribute("strdescrizione");
				// String strStandard = (String)
				// riga.getAttribute("flgstandard");
				//
				// if (strStandard != null) {
				// this.standard = strStandard.equalsIgnoreCase("S") ? true :
				// false;
				//
				// }

			}

			// Caricamento righe del menu

			statement = SQLStatements.getStatement("CARICA_MENU");
			sqlCommand = dataConnection.createSelectCommand(statement);

			dataResult = sqlCommand.execute(inputParameter);

			scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			sb = scrollableDataResult.getSourceBean();

			righe = sb.getAttributeAsVector("ROW");
			int livCurr = 0;

			VoceMenu vmPrev = null;

			for (int i = 0; i < righe.size(); i++) {
				SourceBean riga = (SourceBean) righe.get(i);

				VoceMenu vmCurr = new VoceMenu();
				if (voceMenuRoot == null)
					voceMenuRoot = vmCurr;

				String _strdescvocemenu = (String) riga.getAttribute("strvocemenu");
				BigDecimal level = (BigDecimal) riga.getAttribute("level");
				BigDecimal cdnvocemenu = (BigDecimal) riga.getAttribute("cdnvocemenu");

				vmCurr.setLivello(level.intValue());
				vmCurr.setDescrizione(_strdescvocemenu);
				vmCurr.setCdnVoceMenu(cdnvocemenu.intValue());

				BigDecimal _cdnfunzione = (BigDecimal) riga.getAttribute("cdnfunzione");

				if (_cdnfunzione != null) {
					String _strdescfunz = (String) riga.getAttribute("strdescrizionefunzione");
					Funzione funz = new Funzione();
					funz.setCdnFunzione(_cdnfunzione.intValue());
					funz.setDescrizione(_strdescfunz);
					vmCurr.setFunzione(funz);
				}

				if (vmPrev != null) {

					if (vmPrev.getLivello() < vmCurr.getLivello()) {
						vmPrev.getFigli().add(vmCurr);
						vmCurr.setPadre(vmPrev);
					}

					if (vmPrev.getLivello() == vmCurr.getLivello()) {
						vmPrev.getPadre().getFigli().add(vmCurr);
						vmCurr.setPadre(vmPrev.getPadre());
					}

					if (vmPrev.getLivello() > vmCurr.getLivello()) {

						VoceMenu nonno = vmPrev.getPadre();
						while (nonno.getLivello() > (vmCurr.getLivello() - 1))
							nonno = nonno.getPadre();

						nonno.getFigli().add(vmCurr);
						vmCurr.setPadre(nonno);
					}

				}

				vmPrev = vmCurr;

			}

			// try {
			//
			// FileWriter file = new FileWriter("C:\\tmp\\menu.xml");
			// file.write(toXML().toString());
			// file.close();
			//
			// } catch (IOException e) {
			//
			// TracerSingleton.log(// it.eng.sil.Values.APP_NAME,//
			// TracerSingleton.CRITICAL,// "QueryExecutor::executeQuery:",//
			// (Exception) e);
			// return;
			// }

		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}

	}

	public StringBuffer toXML() {

		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");

		buf.append("<menu ");

		buf.append(" idmenu=\"" + this.hashCode() + "\"");

		buf.append(" cdnmenu=\"");
		buf.append(cdnMenu);
		buf.append("\"");

		if (this.descrizione != null) {
			buf.append(" descrizionemenu=\"");
			buf.append(Util.unmarkup(descrizione));
			buf.append("\"");
		}

		// buf.append(" standard=\"");
		// if (standard) {
		// buf.append("S");
		// } else {
		// buf.append("N");
		// }
		// buf.append("\"");

		buf.append(">");

		if (voceMenuRoot != null) {
			buf.append(voceMenuRoot.toXML());

		}
		buf.append("</menu>");

		return buf;

	}

	public void su(int hashCode) {

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		VoceMenu padre = vm.getPadre();
		if (padre != null) {
			int idxFiglio = calcIdxFiglio(padre, vm);

			if (idxFiglio == 0)
				return;

			List figli = padre.getFigli();
			VoceMenu fratello = (VoceMenu) figli.set(idxFiglio - 1, vm);

			figli.set(idxFiglio, fratello);

		}

		// }

	}

	public void giu(int hashCode) {

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		VoceMenu padre = vm.getPadre();
		if (padre != null) {
			int idxFiglio = calcIdxFiglio(padre, vm);

			List figli = padre.getFigli();

			if (idxFiglio == (figli.size() - 1))
				return;

			VoceMenu fratello = (VoceMenu) figli.set(idxFiglio + 1, vm);

			figli.set(idxFiglio, fratello);

		}

		// }

	}

	public void suLivello(int hashCode) {

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		VoceMenu padre = vm.getPadre();
		if (padre != null) {

			VoceMenu nonno = padre.getPadre();

			if (nonno != null) {

				// rimozione dai fratelli
				int idxFiglio = calcIdxFiglio(padre, vm);
				List figli = padre.getFigli();
				figli.remove(idxFiglio);
				vm.setPadre(nonno);

				int idxPadre = calcIdxFiglio(nonno, padre);

				nonno.getFigli().add(idxPadre, vm);
			}

		}

		// }

	}

	public void giuLivello(int hashCode) {

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		VoceMenu padre = vm.getPadre();
		if (padre != null) {

			int idxFiglio = calcIdxFiglio(padre, vm);
			List figli = padre.getFigli();

			boolean trovataCartella = false;
			VoceMenu fratello = null;
			for (int idxNewFolder = idxFiglio + 1; idxNewFolder < figli.size() && !trovataCartella; idxNewFolder++) {
				fratello = (VoceMenu) figli.get(idxNewFolder);
				// if (fratello.getFigli().size() > 0)
				if (fratello.getFunzione() == null)
					trovataCartella = true;

			}
			if (trovataCartella) {

				fratello.getFigli().add(0, vm);
				vm.setPadre(fratello);
				figli.remove(idxFiglio);

			}

			// }

		}

	}

	public void cancellaLivello(int hashCode) {

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		VoceMenu padre = vm.getPadre();
		if (padre != null) {

			int idxFiglio = calcIdxFiglio(padre, vm);
			List figli = padre.getFigli();
			figli.remove(idxFiglio);
		}
		vm.setPadre(null);
		// }

	}

	public void addCartella(int hashCode, String cartella) {

		cartella = Util.unmarkup(cartella);

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		if (vm.getFunzione() != null) {
			vm = vm.getPadre();
		}
		if (vm != null) {
			VoceMenu nuovaVm = new VoceMenu();
			nuovaVm.setDescrizione(cartella);
			nuovaVm.setPadre(vm);
			vm.getFigli().add(0, nuovaVm);

		}

		// }

	}

	public void addFunzione(int hashCode, int cdnFunzione, String descFunzione) {

		descFunzione = Util.unmarkup(descFunzione);

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		if (vm.getFunzione() != null) {
			vm = vm.getPadre();
		}
		if (vm != null) {
			VoceMenu nuovaVm = new VoceMenu();
			nuovaVm.setDescrizione(descFunzione);
			Funzione f = new Funzione();
			f.setCdnFunzione(cdnFunzione);
			f.setDescrizione(descFunzione);
			nuovaVm.setFunzione(f);

			vm.getFigli().add(0, nuovaVm);
			nuovaVm.setPadre(vm);

		}

		// }

	}

	public void rinomina(int hashCode, String nome) {

		nome = Util.unmarkup(nome);

		VoceMenu vm = find(voceMenuRoot, hashCode);

		/*
		 * if (vm == null) { System.out.println("non trovato!!!"); } else {
		 */
		vm.setDescrizione(nome);
		// }

	}

	private VoceMenu find(VoceMenu vm, int hashCode) {

		if (vm.hashCode() == hashCode)
			return vm;
		else {
			Iterator iter = vm.getFigli().iterator();
			while (iter.hasNext()) {
				VoceMenu vmfiglio = (VoceMenu) iter.next();
				VoceMenu vmnipote = find(vmfiglio, hashCode);
				if (vmnipote != null)
					return vmnipote;
			}

		}
		return null;
	}

	private int calcIdxFiglio(VoceMenu padre, VoceMenu figlio) {
		List figli = padre.getFigli();

		int idxFiglio = 0;

		for (idxFiglio = 0; idxFiglio < figli.size(); idxFiglio++) {
			VoceMenu f = (VoceMenu) figli.get(idxFiglio);

			if (f.hashCode() == figlio.hashCode())
				return idxFiglio;
		}
		return -1;
	}

	public void save() throws Exception {

		// /////////////////////////////////
		// reperimento della connessione
		// /////////////////////////////////

		DataConnection dataConn = null;
		Connection conn = null;
		try {
			dataConn = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
			conn = dataConn.getInternalConnection();

		} catch (EMFInternalError e) {

			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName(), (Exception) e);

			if (dataConn != null) {
				Utils.releaseResources(dataConn, null, null);
			}
			throw new Exception("Non e' possibile reperire la connesione al DB");

		}

		// /////////////////////////////////
		// Inizio della TRANSAZIONE
		// /////////////////////////////////

		try {
			conn.setAutoCommit(false);

			salvaTestata(conn);
			cancellaVecchioMenu(conn);

			voceMenuRoot.save(conn, cdnMenu, 0);
			conn.commit();

			conn.setAutoCommit(true);

			if (conn != null)
				conn.close();

		} catch (SQLException e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName(), (Exception) e1);

			try {
				conn.rollback();
				if (conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}

			} catch (SQLException e2) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName(), (Exception) e2);

			}

			throw e1;

		}

	}

	private void cancellaVecchioMenu(Connection conn) throws SQLException {

		CallableStatement cs = (CallableStatement) conn.prepareCall("{ CALL pg_profil.cancella_voci_menu(?)}");
		cs.setInt(1, cdnMenu);

		cs.execute();

	}

	private void salvaTestata(Connection conn) throws SQLException {

		CallableStatement cs = (CallableStatement) conn.prepareCall("{ CALL pg_profil.salva_testata_menu(?, ?, ?)}");
		cs.setInt(1, cdnMenu);
		cs.setString(2, descrizione);
		cs.registerOutParameter(3, Types.INTEGER);
		cs.execute();

		cdnMenu = cs.getInt(3);

	}

	public int clonaMenu() throws Exception {

		// /////////////////////////////////
		// reperimento della connessione
		// /////////////////////////////////

		DataConnection dataConn = null;
		Connection conn = null;

		try {
			dataConn = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
			conn = dataConn.getInternalConnection();

		} catch (EMFInternalError e) {

			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName(), (Exception) e);

			if (dataConn != null) {
				Utils.releaseResources(dataConn, null, null);
			}
			throw new Exception("Non e' possibile reperire la connesione al DB");

		}

		// /////////////////////////////////
		// Inizio della TRANSAZIONE
		// /////////////////////////////////

		try {
			conn.setAutoCommit(false);

			CallableStatement cs = (CallableStatement) conn.prepareCall("{ CALL pg_profil.clona_menu(?, ?)}");
			cs.setInt(1, cdnMenu);
			cs.registerOutParameter(2, Types.INTEGER);

			cs.execute();

			conn.commit();

			cdnMenuClonato = cs.getInt(2);

			conn.setAutoCommit(true);

			if (conn != null)
				conn.close();

			return cdnMenuClonato;

		} catch (SQLException e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName(), (Exception) e1);

			try {
				conn.rollback();
				if (conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}

			} catch (SQLException e2) {
				it.eng.sil.util.TraceWrapper.debug(_logger, getClass().getName(), (Exception) e2);

			}

			throw e1;

		}

		catch (Exception e) {
			conn.rollback();
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
			throw e;
		}

	}

}