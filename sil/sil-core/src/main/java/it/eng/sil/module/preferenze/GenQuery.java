package it.eng.sil.module.preferenze;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.tags.Util;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.sil.Values;
import it.eng.sil.util.ga.db.Colonna;
import it.eng.sil.util.ga.db.Tabella;

/**
 * Crea le query relative a una tabella
 * 
 * @author Franco Vuoto
 */

public class GenQuery {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GenQuery.class.getName());

	private Tabella tabella;
	private List colonne = null;
	private List pkColonne = null;

	public GenQuery(Tabella tabella) {
		this.tabella = tabella;
	}

	/**
	 * Genera la SELECT utilizzata per la sola lista senza parti WHERE.
	 */
	public StringBuffer generaSelectLista() {

		StringBuffer buf = generaSelect();
		retrievePKs();

		boolean first = true;
		for (Iterator iter = pkColonne.iterator(); iter.hasNext();) {
			Colonna col = (Colonna) iter.next();
			if (first) {
				buf.append(" ORDER BY " + col.getNome());
				first = false;
			} else {
				buf.append(", " + col.getNome());
			}
		}
		return buf;
	}

	private StringBuffer generaSelect() {
		StringBuffer buf = new StringBuffer("SELECT ");
		colonne = this.tabella.getArrColonne();

		for (Iterator iter = colonne.iterator(); iter.hasNext();) {
			Colonna colonna = (Colonna) iter.next();
			String colName = colonna.getNome();
			String colType = colonna.getNometipo().toUpperCase();

			if (colType.equals("DATE")) {
				buf.append("to_char(" + colName + ", 'dd/mm/yyyy') " + colName);
			} else {
				buf.append(colName);
			}
			if (iter.hasNext()) {
				buf.append(", ");
			}
		}

		buf.append(" FROM ");

		buf.append(this.tabella.getNome());

		return buf;

	}

	/**
	 * Genera la SELECT utilizzata per la sola lista con parti WHERE per la selezione del dettaglio
	 */

	public StringBuffer generaSelectDettaglio(SourceBean request, SourceBean response) {

		StringBuffer buf = generaSelect();
		retrievePKs();

		boolean nextIsWhere = true;
		for (Iterator iter = pkColonne.iterator(); iter.hasNext();) {
			Colonna pk = (Colonna) iter.next();
			String nome = pk.getNome();
			String tipo = pk.getNometipo();

			String valore = (String) request.getAttribute(nome);

			if ((valore == null) || valore.equals("")) {
				BigDecimal sequence = (BigDecimal) request.getAttribute("_SEQUENCE_");
				if (sequence != null) {
					valore = sequence.toString();
				} else {
					_logger.fatal("::generaSelectDettaglio() PK nulla");

				}
			}

			if (nextIsWhere) {
				buf.append(" WHERE ");
				nextIsWhere = false;
			} else {
				buf.append(" AND ");
			}
			if (tipo.equals("NUMBER")) {
				buf.append(nome + "=" + valore);
			}
			if (tipo.indexOf("CHAR") > -1) {
				// buf.append(nome + "='" + Util.replace(valore, "'", "''") +
				// "'");

				String tmpVal = Util.replace(valore, "'", "''");
				if (nome.toUpperCase().startsWith("COD")) {
					tmpVal = tmpVal.toUpperCase();
				}

				buf.append(nome + "='" + tmpVal + "'");

			}

			if (tipo.equals("DATE")) {
				buf.append("to_char(" + nome + ", 'dd/mm/yyyy') = '" + valore + "'");
			}

		}

		return buf;
	}

	public StringBuffer generaUpdate(SourceBean request, SourceBean response) {

		StringBuffer buf = new StringBuffer("UPDATE " + tabella.getNome() + " SET ");
		retrievePKs();

		for (Iterator iter = colonne.iterator(); iter.hasNext();) {
			Colonna colonna = (Colonna) iter.next();
			String nome = colonna.getNome();
			String tipo = colonna.getNometipo();

			String valore = (String) request.getAttribute(nome);
			if ((valore == null) || valore.equals("")) {
				if (colonna.isNullabile()) {
					buf.append(nome + " = NULL ");
					if (iter.hasNext()) {
						buf.append(", ");
					}
					continue;
				}
			}

			if (tipo.equals("DATE")) {
				buf.append(nome + " = TO_DATE('" + valore + "', 'dd/mm/yyyy') ");
			}

			if (tipo.equals("NUMBER")) {
				if (nome.toUpperCase().startsWith("NUMKLO")) {
					long longVal = new Long(valore).longValue();
					longVal++;
					valore = String.valueOf(longVal);
				}
				buf.append(nome + "=" + valore);
			}

			if (tipo.indexOf("CHAR") > -1) {
				String tmpVal = Util.replace(valore, "'", "''");
				if (nome.toUpperCase().startsWith("COD") && (colonna.isPK() || colonna.isFK())) {
					tmpVal = tmpVal.toUpperCase();
				}

				buf.append(nome + "='" + tmpVal + "'");
			}
			if (iter.hasNext()) {
				buf.append(", ");
			}
		}

		boolean nextIsWhere = true;
		for (Iterator iter = pkColonne.iterator(); iter.hasNext();) {
			Colonna pk = (Colonna) iter.next();
			String nome = pk.getNome();
			String tipo = pk.getNometipo();

			String valore = (String) request.getAttribute(nome);
			// System.out.println(valore);
			// System.out.println(tipo);

			if (nextIsWhere) {
				buf.append(" WHERE ");
				nextIsWhere = false;
			} else {
				buf.append(" AND ");
			}
			if (tipo.equals("NUMBER")) {
				buf.append(nome + "=" + valore);
			}
			if (tipo.indexOf("CHAR") > -1) {
				buf.append(nome + "='" + Util.replace(valore, "'", "''") + "'");
			}

			if (tipo.equals("DATE")) {
				buf.append("to_char(" + nome + ", 'dd/mm/yyyy') = '" + valore + "'");
			}

		}

		// System.out.println("UPDATE: " + buf.toString());
		return buf;

	}

	// Davide
	public StringBuffer generaDelete(SourceBean request, SourceBean response) {

		StringBuffer buf = new StringBuffer("DELETE FROM " + tabella.getNome());
		retrievePKs();

		boolean nextIsWhere = true;
		for (Iterator iter = pkColonne.iterator(); iter.hasNext();) {
			Colonna pk = (Colonna) iter.next();
			String nome = pk.getNome();
			String tipo = pk.getNometipo();

			String valore = (String) request.getAttribute(nome);

			if (nextIsWhere) {
				buf.append(" WHERE ");
				nextIsWhere = false;
			} else {
				buf.append(" AND ");
			}

			if (tipo.equals("NUMBER")) {
				buf.append(nome + "=" + valore);
			}
			if (tipo.indexOf("CHAR") > -1) {
				buf.append(nome + "='" + Util.replace(valore, "'", "''") + "'");
			}
			if (tipo.equals("DATE")) {
				buf.append("to_char(" + nome + ", 'dd/mm/yyyy') = '" + valore + "'");
			}

		}

		// System.out.println("UPDATE: " + buf.toString());
		return buf;

	}

	public List retrievePKs() {
		pkColonne = new ArrayList(1);
		colonne = this.tabella.getArrColonne();

		for (Iterator iter = colonne.iterator(); iter.hasNext();) {
			Colonna colonna = (Colonna) iter.next();
			if (colonna.isPK()) {
				pkColonne.add(colonna);
			}
		}
		return pkColonne;
	}

	public StringBuffer generaInsert(SourceBean request, SourceBean response) {

		BigDecimal sequence = null;
		StringBuffer buf = new StringBuffer("INSERT INTO  " + tabella.getNome());
		retrievePKs();

		boolean useSequence = false;
		if (pkColonne.size() == 1) {
			Colonna pkSeq = null;
			pkSeq = (Colonna) pkColonne.get(0);
			useSequence = pkSeq.getNometipo().equals("NUMBER");
		}

		if (useSequence) {
			sequence = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_" + tabella.getNome());
			try {
				response.setAttribute("_SEQUENCE_", sequence);
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "::generaInsert() ", e);

			}
		}

		buf.append("(");
		for (Iterator iter = colonne.iterator(); iter.hasNext();) {
			Colonna colonna = (Colonna) iter.next();
			String nome = colonna.getNome();
			buf.append(nome);
			if (iter.hasNext()) {
				buf.append(", ");
			}
		}

		buf.append(" ) VALUES (");

		for (Iterator iter = colonne.iterator(); iter.hasNext();) {
			Colonna colonna = (Colonna) iter.next();
			String nome = colonna.getNome();
			String tipo = colonna.getNometipo();

			String valore = (String) request.getAttribute(nome);
			if ((valore == null) || valore.equals("")) {
				if (colonna.isNullabile()) {
					buf.append("NULL ");
					if (iter.hasNext()) {
						buf.append(", ");
					}
					continue;
				}
			}

			if (tipo.equals("DATE")) {
				buf.append("TO_DATE('" + valore + "', 'dd/mm/yyyy') ");
			}

			if (tipo.equals("NUMBER")) {
				if (nome.toUpperCase().startsWith("NUMKLO")) {
					valore = "0";
				}
				if (colonna.isPK() && useSequence) {
					valore = sequence.toString();
				}

				buf.append(valore);
			}

			if (tipo.indexOf("CHAR") > -1) {
				String tmpVal = Util.replace(valore, "'", "''");

				if (nome.toUpperCase().startsWith("COD") && (colonna.isPK() || colonna.isFK())) {
					tmpVal = tmpVal.toUpperCase();
				}

				buf.append("'" + tmpVal + "'");
			}
			if (iter.hasNext()) {
				buf.append(", ");
			}

			if (colonna.isPK()) {
				try {
					response.setAttribute(nome, valore);

				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "::generaInsert() ", e);

				}

			}

		}

		buf.append(")");

		_logger.debug("::generaInsert() INSERT: " + buf.toString());

		return buf;

	}

}