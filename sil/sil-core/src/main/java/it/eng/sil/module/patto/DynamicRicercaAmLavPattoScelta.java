/*
 * Creato il Dec 30, 2004
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class DynamicRicercaAmLavPattoScelta implements IDynamicStatementProvider {

	private static final String STM = " SELECT aps.prgpattolavoratore, " + "      	aps.prglavpattoscelta,"
			+ "      	aps.strchiavetabella, " + "		aps.STRCHIAVETABELLA2, "
			+ "		to_char(aps.datProtocollo,'dd/mm/yyyy') as datProtocollo,"
			+ "		to_char(pl.datUltimoProtocollo,'dd/mm/yyyy') as datUltimoProtocollo,"
			+ "		to_char(pl.datstipula,'dd/mm/yyyy') as datstipula,"
			+ "		to_char(pl.datFine,'dd/mm/yyyy') as datFine," + "		pl.flgpatto297,"
			+ "		de_stato_atto.strdescrizione AS statoatto, "
			+ "		de_motivo_fine_atto.strDescrizione as motivoFine," +
			// GG 1-3-05, rimosso: " doc.NUMPROTOCOLLO, "+
			"		to_char (doc.datprotocollo, 'dd/mm/yyyy HH24:MI') as datOraProtocollo, "
			+ "		doc.numprotocollo as numProtocollo," + "		de_doc_ambito.STRDESCRIZIONE as docambito,"
			+ "		decode(doc.CODMONOIO,'I', 'Input', 'Output') as descio " + " FROM am_lav_patto_scelta aps, "
			+ "		am_patto_lavoratore pl," + "		de_stato_atto," + "		de_motivo_fine_atto, "
			+ "		am_documento doc, " + "		am_documento_coll coll, " + "		de_doc_tipo,"
			+ "		de_doc_ambito" + " WHERE aps.prgPattoLavoratore = pl.prgPattoLavoratore"
			+ "		and pl.codStatoAtto = de_stato_atto.codStatoAtto "
			+ "		and pl.codMotivofineAtto = de_motivo_fine_atto.codMotivoFineAtto(+) "
			+ " 		and doc.PRGDOCUMENTO = coll.PRGDOCUMENTO and doc.codstatoatto = 'PR'"
			+ "		and coll.STRCHIAVETABELLA = to_char(pl.PRGPATTOLAVORATORE)"
			+ "		and coll.CDNCOMPONENTE in (select t.CDNCOMPONENTE"
			+ "	  							   from ts_componente t "
			+ "		  						   where upper(t.STRPAGE) = 'PATTOLAVDETTAGLIOPAGE') "
			+ "	    and de_doc_tipo.CODTIPODOCUMENTO = doc.CODTIPODOCUMENTO"
			+ "		and de_doc_tipo.CODAMBITODOC = de_doc_ambito.CODAMBITODOC";

	// " and pl.codstatoatto = 'PR' ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		StringBuffer stm = null;
		String ret = null;
		SourceBean serviceRequest = requestContainer.getServiceRequest();
		String prgPattoLavoratore = Utils.notNull(serviceRequest.getAttribute("PRGPATTOLAVORATORE"));
		String codLstTab = Utils.notNull(serviceRequest.getAttribute("CODICE_TABELLA"));
		String strChiaveTabella = Utils.notNull(serviceRequest.getAttribute("STRCHIAVETABELLA"));
		if (prgPattoLavoratore.equals("") || codLstTab.equals("") || strChiaveTabella.equals(""))
			ret = null;
		else {
			stm = new StringBuffer(STM);
			String strChiaveTabella2 = (String) serviceRequest.getAttribute("STRCHIAVETABELLA3");
			String strChiaveTabella3 = (String) serviceRequest.getAttribute("STRCHIAVETABELLA2");
			stm.append(" and aps.prgPattoLavoratore = ");
			stm.append(prgPattoLavoratore);
			stm.append(" and aps.codLstTab = '");
			stm.append(codLstTab);
			stm.append("' and aps.strChiaveTabella = '");
			stm.append(strChiaveTabella);
			stm.append("'");
			if (strChiaveTabella2 != null) {
				stm.append("' and aps.strChiaveTabella2 = '");
				stm.append(strChiaveTabella2);
				stm.append("'");
			}
			if (strChiaveTabella3 != null) {
				stm.append("' and aps.strChiaveTabella3 = '");
				stm.append(strChiaveTabella3);
				stm.append("'");
			}
			ret = stm.toString();
		}
		return ret;

	}

}
