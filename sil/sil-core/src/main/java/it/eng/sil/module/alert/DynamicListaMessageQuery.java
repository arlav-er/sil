/*
 * Creato il 25-gen-05
 *
 */
package it.eng.sil.module.alert;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;

/**
 * @author Togna Cosimo
 * @author D'Auria Giovanni
 * 
 *         Questa classe fornisce la query per la ricerca dei messaggi presenti nel sil Possono essere selezionati
 *         messaggi scaduti, non ancora scaduti e tutti i messaggi.
 */
public class DynamicListaMessageQuery implements IDynamicStatementProvider2 {

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2#getStatement(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public String getStatement(SourceBean request, SourceBean response) {
		String query = "SELECT " + "ms.STRCORPO corpo, ms.STROGGETTO oggetto, "
				+ "ms.PRIORITA priorita, to_char(ms.DTMINSERIMENTO, 'dd/mm/yyyy hh24:mi:ss') datainserimento, to_char(ms.DTMINIZIOVALIDITA,'dd/mm/yyyy hh24:mi:ss') iniziovalidita, "
				+ "to_char(ms.DTMFINEVALIDITA, 'dd/mm/yyyy hh24:mi:ss') finevalidita, ut.STRCOGNOME cognome, ut.STRNOME nome, ut.STRLOGIN login, ut.CDNUT codmittente, ms.PRGTSMESSAGGI codmessaggio "
				+ "FROM TS_MESSAGGI ms, TS_UTENTE ut " + "WHERE ms.CDNMITTENTE = ut.CDNUT ";
		String tiporicerca = "";
		if (request.getAttribute("tiporicerca") != null) {
			tiporicerca = (String) request.getAttribute("tiporicerca");
		} else {
			tiporicerca = "tutti";
		}

		if (tiporicerca.equalsIgnoreCase("validi")) {
			query += "AND ( (sysdate BETWEEN ms.DTMINIZIOVALIDITA AND ms.DTMFINEVALIDITA) OR (sysdate < ms.DTMINIZIOVALIDITA) ) ";
		} else if (tiporicerca.equalsIgnoreCase("nonvalidi")) {
			query += "AND sysdate NOT BETWEEN ms.DTMINIZIOVALIDITA AND ms.DTMFINEVALIDITA "
					+ "AND sysdate > ms.DTMINIZIOVALIDITA ";
		}
		query += "ORDER BY datainserimento DESC";

		return query;
	}

}
