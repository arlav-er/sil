package it.eng.sil.module.sap;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Modulo base per l'importazione delle sezioni di MySAP.
 *
 * @author Guido Zuccaro
 * @since 23/03/2016
 */
public class InsertSAP extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertSAP.class.getName());
	public static final String SAP_SEZ_TITOLI = "frmTitStu";
	public static final String SAP_SEZ_FOR_PRO = "frmForPro";
	public static final String SAP_SEZ_LINGUE = "frmLingue";
	public static final String SAP_SEZ_CON_INF = "frmConInf";
	public static final String SAP_SEZ_ABILITA_PATENTI = "frmAbilita_Patenti";
	public static final String SAP_SEZ_ABILITA_PATENTINI = "frmAbilita_Patentini";
	public static final String SAP_SEZ_ABILITA_ALBI = "frmAbilita_Albi";
	public static final String SAP_SEZ_ESP_LAV = "frmEspLav";
	public static final String SAP_SEZ_PROPEN = "frmPropen";

	/**
	 * Converte il livello di conoscenza SAP in quello SIL.
	 *
	 * @param codGradoSAP
	 *            Codice conoscenza linguistica SAP.
	 */
	public String getGradoLingua(String codGradoSAP) {
		final String XML_SELECT = "SELECT_GRADOCONOSC_SIL_DA_GRADOCONOSC_SAP";
		String grado = "";
		SelectSAP select = null;
		try {
			select = new SelectSAP(XML_SELECT);
			select.parametro("COD_CONOSCENZA", codGradoSAP);
			select.parametro("CDNGRADO", codGradoSAP);
			ScrollableDataResult risultato = select.esegui();
			if (risultato != null && risultato.hasRows())
				grado = risultato.getResultSet().getString(1);
			select.chiudi();
		} catch (Exception e) {
			_logger.error(e.getMessage());
		} finally {
			if (select != null) {
				select.chiudi();
			}
		}
		return grado;
	}

	/**
	 * Restituisce il codice titolo padre a partire dal figlio.
	 *
	 * @param codTitolo
	 *            Codice titolo figlio.
	 * @return Codice titolo padre.
	 */
	private static String getTitoloPadre(String codTitolo) {
		final String XML_SELECT = "SELECT_TITOLO_PADRE";
		String padre = "";
		SelectSAP select = null;
		try {
			select = new SelectSAP(XML_SELECT);
			select.parametro("codTitolo", codTitolo);
			ScrollableDataResult risultato = select.esegui();
			if (risultato != null && risultato.hasRows())
				padre = risultato.getResultSet().getString(1);
			select.chiudi();
		} catch (Exception e) {
			_logger.error(e.getMessage());
		} finally {
			if (select != null) {
				select.chiudi();
			}
		}
		return padre;
	}

	/**
	 * Funzione ricorsiva che restituisce il nodo radice di una lista, risalendo attraverso i nodi padre.
	 *
	 * @param codTitolo
	 *            Ultimo elemento della lista.
	 * @return Primo elemento della lista.
	 */
	public static String getTipoTitolo(String codTitolo) {
		String figlio = codTitolo;
		String padre = figlio;
		// se il padre è nullo ho trovato la radice
		while (!padre.equals("0")) {
			padre = getTitoloPadre(figlio);
			// se il padre non è nullo aggiorno il codice figlio
			if (!padre.equals("0"))
				figlio = padre;
		}
		return figlio;
	}

	/**
	 * Recupera la descrizione estesa del titolo di studio.
	 *
	 * @param codTitolo
	 *            Codice del titolo di studio.
	 * @return La descrizione estesa del titolo di studio.
	 */
	public static String getDescrTitolo(String codTitolo) {
		final String XML_SELECT = "SELECT_DESC_PARLANTE";
		String grado = "";
		SelectSAP select = null;
		try {
			select = new SelectSAP(XML_SELECT);
			select.parametro("codTitolo", codTitolo);
			ScrollableDataResult risultato = select.esegui();
			if (risultato != null && risultato.hasRows())
				grado = risultato.getResultSet().getString(1);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		} finally {
			if (select != null) {
				select.chiudi();
			}
		}
		return grado;
	}

	public SourceBean getPatenteSIL(String codPatenteSAP) {
		final String XML_SELECT = "SELECT_CODPATENTE_SIL_DA_CODPATENTE_SAP";
		SourceBean patente = null;
		SelectSAP select = null;
		try {
			select = new SelectSAP(XML_SELECT);
			select.parametro("codAbilitazione", codPatenteSAP);
			select.parametro("codAbilitazioneGen", codPatenteSAP);
			ScrollableDataResult risultato = select.esegui();
			if (risultato != null && risultato.hasRows())
				patente = risultato.getSourceBean();
		} catch (Exception e) {
			_logger.error(e.getMessage());
		} finally {
			if (select != null) {
				select.chiudi();
			}
		}
		return patente;
	}

	public SourceBean getPatentinoSIL(String codPatentinoSAP) {
		final String XML_SELECT = "SELECT_CODPATENTINO_SIL_DA_CODPATENTINO_SAP";
		SourceBean patentino = null;
		SelectSAP select = null;
		try {
			select = new SelectSAP(XML_SELECT);
			select.parametro("codAbilitazione", codPatentinoSAP);
			ScrollableDataResult risultato = select.esegui();
			if (risultato != null && risultato.hasRows())
				patentino = risultato.getSourceBean();
		} catch (Exception e) {
			_logger.error(e.getMessage());
		} finally {
			if (select != null) {
				select.chiudi();
			}
		}
		return patentino;
	}

	public SourceBean getAlboSIL(String codAlboSAP) {
		final String XML_SELECT = "SELECT_CODALBO_SIL_DA_CODALBO_SAP";
		SourceBean albo = null;
		SelectSAP select = null;
		try {
			select = new SelectSAP(XML_SELECT);
			select.parametro("codAbilitazione", codAlboSAP);
			ScrollableDataResult risultato = select.esegui();
			if (risultato != null && risultato.hasRows())
				albo = risultato.getSourceBean();
		} catch (Exception e) {
			_logger.error(e.getMessage());
		} finally {
			if (select != null) {
				select.chiudi();
			}
		}
		return albo;
	}

	@Override
	public void service(SourceBean request, SourceBean response) throws Exception {
	}
}