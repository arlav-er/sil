/*
 * Created on Jun 24, 2005
 *
 */
package it.eng.sil.module.amministrazione.accorpamento;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Utils;

/**
 * Classe che contiene i dati da visualizzare nella pagina di VISUALIZZA E CONFRONTA dell'accorpamento lavoratore ,
 * sezione dichiarazione immediata disponibilita'. I dati vengono estratti dai SourceBean di did, protocollo, ultimo
 * movimento, gli stessi che vengono caricati nella pagina di dettaglio della did del lavoratore.
 * 
 * @author savino
 */
public class Did {

	public BigDecimal cdnLavoratore = null;
	// public String strCognome = null;
	// public String strNome = null;
	public BigDecimal prgDichDisponibilita = null;
	public String CODSTATOATTO = null;
	public String datDichiarazione = null;
	public BigDecimal prgElencoAnagrafico = null;
	public String CODTIPODICHDISP = null;
	public String CODULTIMOCONTRATTO = null;
	public BigDecimal PRGSTATOOCCUPAZ = null;
	public String CODSTATOOCCUPAZ = null;
	public String descStato = null;
	public String DATSCACDNUTINSERMA = null;
	public String DATSCADCONFERMA = null;
	public String DATSCADEROGAZSERVIZI = null;
	public String STRNOTE = null;
	public String CODMOTIVOFINEATTO = null;
	public String DATFINE = null;
	public BigDecimal cdnUtIns = null;
	public String dtmIns = null;
	public BigDecimal cdnUtMod = null;
	public String dtmMod = null;
	public BigDecimal NUMKLODICHDISP = null;
	public String datInizio = null;
	public String codCPI = null;
	public String descCPI = null;
	public String codStatoOccRaggiunto = null;
	public String numColloq = "";
	public String numStipP = "";
	public String accertamentoSanitario = "";
	public String dataInizioCM = "";
	public String datInizioDocIdent = "";
	public String datScadDocIdent = "";
	// protocollazione
	public String annoProt = "";
	public String dataProt = "";
	public String oraProt = "";
	public String docInOrOut = "";
	public String docDiIO = "";
	public String docRif = "";
	public BigDecimal numProt = null;
	// ultimo movimento
	public String codContrattoMovimento = null;

	/**
	 * 
	 * @param did
	 *            SourceBean che contiene le info sulla dichiarazione di immediata disponibilita'
	 * @param protocollo
	 *            SourceBean che contiene le informazioni sulla protocollazione della did
	 * @param ultimoMov
	 *            SourceBean che contiene le informazioni sul contratto dell'ultimo movimento del lavoratore
	 */
	public Did(SourceBean did, SourceBean protocollo, SourceBean ultimoMov) {
		if (did == null) {
			return;
		}
		cdnLavoratore = (BigDecimal) did.getAttribute("CDNLAVORATORE");
		// strCognome = (String) did.getAttribute("strCognome");
		// strNome = (String) did.getAttribute("strNome");
		prgDichDisponibilita = (BigDecimal) did.getAttribute("prgDichDisponibilita");
		CODSTATOATTO = (String) did.getAttribute("CODSTATOATTO");
		PRGSTATOOCCUPAZ = (BigDecimal) did.getAttribute("prgStatooccupaz");
		CODSTATOOCCUPAZ = (String) did.getAttribute("codStatoOccupaz");
		datDichiarazione = (String) did.getAttribute("datDichiarazione");
		prgElencoAnagrafico = (BigDecimal) did.getAttribute("prgElencoAnagrafico");
		CODTIPODICHDISP = (String) did.getAttribute("CODTIPODICHDISP");
		CODULTIMOCONTRATTO = (String) did.getAttribute("CODULTIMOCONTRATTO");
		DATSCADCONFERMA = (String) did.getAttribute("DATSCADCONFERMA");
		DATSCADEROGAZSERVIZI = (String) did.getAttribute("DATSCADEROGAZSERVIZI");
		STRNOTE = (String) did.getAttribute("STRNOTE");
		CODMOTIVOFINEATTO = (String) did.getAttribute("CODMOTIVOFINEATTO");
		DATFINE = (String) did.getAttribute("DATFINE");
		NUMKLODICHDISP = (BigDecimal) did.getAttribute("NUMKLODICHDISP");
		datInizio = (String) did.getAttribute("datInizio");
		codCPI = (String) did.getAttribute("codCpi");
		descCPI = (String) did.getAttribute("descCPI");
		descStato = (String) did.getAttribute("DESCRIZIONESTATO");
		codStatoOccRaggiunto = (String) did.getAttribute("codStatoOccupazRagg");

		accertamentoSanitario = Utils.notNull((String) did.getAttribute("CODACCERTSANITARIO"));
		dataInizioCM = Utils.notNull(did.getAttribute("dataInizioCM"));
		if (accertamentoSanitario.equals("N") || accertamentoSanitario.equals("M")
				|| accertamentoSanitario.equals("A")) {
			CODTIPODICHDISP = "OC";
		}
		// protocollazione
		if (protocollo != null) {
			numProt = SourceBeanUtils.getAttrBigDecimal(protocollo, "numprotocollo", null);
			BigDecimal annoP = (BigDecimal) protocollo.getAttribute("ANNOPROT");
			annoProt = annoP != null ? annoP.toString() : "";
			dataProt = StringUtils.getAttributeStrNotNull(protocollo, "datprot");
			oraProt = StringUtils.getAttributeStrNotNull(protocollo, "oraProt");
			docInOrOut = StringUtils.getAttributeStrNotNull(protocollo, "CODMONOIO");
			docRif = StringUtils.getAttributeStrNotNull(protocollo, "STRDESCRIZIONE");
			if (docInOrOut.equalsIgnoreCase("I")) {
				docDiIO = "Input";
			} else if (docInOrOut.equalsIgnoreCase("O")) {
				docDiIO = "Output";
			}
		}
		// ultimo movimenot
		if (ultimoMov != null)
			codContrattoMovimento = StringUtils.getAttributeStrNotNull(ultimoMov, "codContratto");
	}

}
