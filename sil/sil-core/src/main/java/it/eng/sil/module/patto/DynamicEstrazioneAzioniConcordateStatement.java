package it.eng.sil.module.patto;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.constant.Properties;

/**
 * Effettua la ricerca dinamica delle azioni concordate
 * 
 * @author Alessio Rolfini
 * 
 */
public class DynamicEstrazioneAzioniConcordateStatement implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "select col.prgColloquio as prgColloquio, "
			+ " lav.cdnLavoratore as cdnLavoratore, " + " perc.prgPercorso as prgPercorso, "
			+ " perc.prgcolloquio || '-' || perc.prgpercorso as pkAzioneConcordata, "
			+ " ob.prgAzioniRagg as prgAzioneRagg, " + " to_char(col.datColloquio, 'DD/MM/YYYY') as datColloquio, "
			+ " lav.strCodiceFiscale || '<BR/>' || lav.strCognome || '<BR/>' || lav.strNome as strCFCognomeNome, "
			+ " deaz.strDescrizione as azione, " + " to_char(perc.datStimata, 'DD/MM/YYYY') as datStimata, "
			+ " deesito.strDescrizione || '<BR/>' || der.strDescrizione "
			+ " || (case when de_vch_stato.codstatovoucher is not null then '<BR/>' || de_vch_stato.strdescrizione else ' ' end) "
			+ " || (case when perc.datEffettiva is not null then '<BR/>' || to_char(perc.datEffettiva, 'DD/MM/YYYY') else ' ' end) as esitoDataSvolgimento, "
			+ " deesito_sifer.strDescrizione as esitoFormazione, " + " ob.strDescrizione as obiettivo, "
			+ " lav.strIndirizzoDom || '<BR/>' || lav.strCapDom || '<BR/>' || com.strDenominazione || ' (' || trim(prov.strIstat) || ')' as indirizzoDom, "
			+ " decode(lav.strTelDom, null, '', 'dom '|| lav.strTelDom || '<BR/>') || "
			+ " decode(lav.strCell, null, '', 'cell ' || lav.strCell || '<BR/>' ) || "
			+ " decode(lav.strTelAltro, null, '', 'altro ' || lav.strtelAltro) as telefono, "
			+ " cpi.strDescrizione as cpiTitComp, " + " case "
			+ " when perc.datadesionegg is not null then to_char(perc.datadesionegg, 'dd/mm/yyyy') "
			+ " when perAdesione.datadesionegg is not null then to_char(perAdesione.datadesionegg, 'dd/mm/yyyy') "
			+ " else null " + " end as datadesionegg, vch.prgvoucher, case "
			+ " when vch.prgvoucher is not null then to_char(vch.prgvoucher) "
			+ " else '0' end as prgvoucherhiddencolumn, " +
			// dati aggiuntivi per la stampa

			" lav.strCodiceFiscale as CF, " + " lav.strCognome as cognome, " + " lav.strNome as nome, "
			+ " to_char(lav.datNasc, 'DD/MM/YYYY') || ' ' ||  comnas.strDenominazione || ' (' || trim(provnas.strIstat) ||')' as dataLuogoNas, "
			+ " lav.strIndirizzoDom || ' - ' || lav.strCapDom || ' ' || com.strDenominazione || ' (' || trim(prov.strIstat) || ') ' as indirizzoStampa, "
			+ " lav.strTelDom as telDom, lav.strTelRes as telRes, lav.strCell as telCell, "
			+ " deesito.strDescrizione || ' ' || to_char(perc.datEffettiva, 'DD/MM/YYYY') as esito, "
			+ " PG_STORIA_ROSA.MESIANZIANITA(lav.cdnlavoratore) as mesianz, "
			+ " PG_STORIA_ROSA.CANDIDATOROSALISTESPECIALICC(lav.cdnlavoratore) as listeSpeciali, "
			+ " PG_STORIA_ROSA.STATODID(lav.cdnlavoratore) as statoDid";

	// fine dati aggiuntivi per la stampa

	private static final String SELECT_SQL_BASE_2 = " from or_colloquio col "
			+ " inner join an_lavoratore lav on (col.cdnLavoratore=lav.cdnLavoratore) "
			+ " inner join de_comune com on (lav.codcomdom=com.codCom) "
			+ " inner join de_comune comnas on (lav.codcomnas=comnas.codcom) "
			+ " inner join de_provincia prov on (com.codProvincia=prov.codProvincia) "
			+ " inner join de_provincia provnas on (comnas.codProvincia=provnas.codProvincia) "
			+ " inner join an_lav_storia_inf lavinf on (lav.cdnLavoratore=lavinf.cdnLavoratore and DECODE(lavinf.DATFINE, NULL, 'S','N') = 'S') "
			+ " inner join de_cpi cpi on (cpi.codCpi=lavinf.codCpiTit) "
			+ " inner join or_percorso_concordato perc on (perc.prgColloquio=col.prgColloquio) "
			+ " inner join de_azione deaz on (deaz.prgAzioni=perc.prgAzioni) "
			+ " inner join de_azione_ragg ob on (deaz.prgAzioneRagg=ob.prgAzioniRagg) "
			+ " left join de_esito deesito on (deesito.codEsito = perc.codEsito ) "
			+ " left join or_percorso_concordato perAdesione on (perc.prgpercorsoadesione = perAdesione.prgpercorso and "
			+ " perc.prgcolloquioadesione = perAdesione.prgcolloquio) "
			+ " left join de_esito deesito_sifer on (deesito_sifer.codEsito = perc.codEsitoFormazione ) "
			+ " left join de_esito_rendicont der on (der.codEsitoRendicont = perc.codEsitoRendicont ) ";

	SourceBean req;

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		req = requestContainer.getServiceRequest();

		// leggo il cdnLavoratore, se presente siamo in un contesto
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "cdnLavoratore");

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		String esitoFiltro = StringUtils.getAttributeStrNotNull(req, "esitoFiltro");
		String codServizio = StringUtils.getAttributeStrNotNull(req, "codServizio");
		String dataColloquioDa = StringUtils.getAttributeStrNotNull(req, "dataColloquioDa");
		String dataColloquioA = StringUtils.getAttributeStrNotNull(req, "dataColloquioA");
		String obiettivo = StringUtils.getAttributeStrNotNull(req, "prgAzioniRag");
		String azione = StringUtils.getAttributeStrNotNull(req, "prgAzioni");
		String dataStimataDa = StringUtils.getAttributeStrNotNull(req, "dataStimataDa");
		String dataStimataA = StringUtils.getAttributeStrNotNull(req, "dataStimataA");

		String dataSvolgimentoDa = StringUtils.getAttributeStrNotNull(req, "dataSvolgimentoDa");
		String dataSvolgimentoA = StringUtils.getAttributeStrNotNull(req, "dataSvolgimentoA");
		String azioniNonConclCheck = StringUtils.getAttributeStrNotNull(req, "azioniNonConclCheck");

		Vector<String> azioniConcordate = req.getAttributeAsVector("pkAzioneConcordata");

		String azioniVoucherCheck = "";
		String cfEnteAtt = "";
		String sedeEnteAtt = "";
		UtilsConfig objConfig = new UtilsConfig("VOUCHER");
		String configVoucher = objConfig.getConfigurazioneDefault_Custom();

		String codCPI = StringUtils.getAttributeStrNotNull(req, "codCPI");

		String codEsitoSifer = StringUtils.getAttributeStrNotNull(req, "codEsitoSifer");
		String azioniEsitoDiverso = StringUtils.getAttributeStrNotNull(req, "azioniEsitoDiverso");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		if (StringUtils.getAttributeStrNotNull(req, "canCIG").equals("true")) {
			query_totale.append(", nvl(prest.strDescrizione,'') as prestazioneCIG ");
		}

		String sqlBase2 = SELECT_SQL_BASE_2;
		if (configVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
			azioniVoucherCheck = StringUtils.getAttributeStrNotNull(req, "azioniVoucherCheck");
			cfEnteAtt = StringUtils.getAttributeStrNotNull(req, "cfEnteAtt");
			sedeEnteAtt = StringUtils.getAttributeStrNotNull(req, "sedeEnteAtt");
			if ((azioniVoucherCheck != null && !azioniVoucherCheck.equals("")
					&& azioniVoucherCheck.equalsIgnoreCase("on")) || (!cfEnteAtt.equals(""))
					|| (!sedeEnteAtt.equals(""))) {
				sqlBase2 = sqlBase2
						+ " inner join or_vch_voucher vch on (perc.prgpercorso = vch.prgpercorso and perc.prgcolloquio = vch.prgcolloquio) ";
			} else {
				sqlBase2 = sqlBase2
						+ " left join or_vch_voucher vch on (perc.prgpercorso = vch.prgpercorso and perc.prgcolloquio = vch.prgcolloquio) ";
			}
			sqlBase2 = sqlBase2 + " left join de_vch_stato on (vch.codstatovoucher = de_vch_stato.codstatovoucher) ";
		} else {
			sqlBase2 = sqlBase2
					+ " left join or_vch_voucher vch on (perc.prgpercorso = vch.prgpercorso and perc.prgcolloquio = vch.prgcolloquio) ";
			sqlBase2 = sqlBase2 + " left join de_vch_stato on (vch.codstatovoucher = de_vch_stato.codstatovoucher) ";
		}

		query_totale.append(sqlBase2);

		if (StringUtils.getAttributeStrNotNull(req, "canCIG").equals("true")) {
			query_totale.append(" left join DE_SERVIZICIG prest on (prest.codServiziCig = perc.codServiziCig ) ");
		}

		StringBuffer buf = new StringBuffer();

		// Gestione programma
		String prgprogrammaq = StringUtils.getAttributeStrNotNull(req, "prgprogrammaq");

		if ((prgprogrammaq != null) && (!prgprogrammaq.equals(""))) {

			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.prgprogrammaq=" + prgprogrammaq);
			query_totale.append(buf.toString());
			return query_totale.toString();
		}
		// Fine programma

		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {

			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" col.cdnLavoratore=" + cdnLavoratore);
		}

		if ((codServizio != null) && (!codServizio.equals(""))) {

			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" col.codServizio='" + codServizio + "' ");
		}

		if ((dataColloquioDa != null) && (!dataColloquioDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" col.datColloquio>=to_date('" + dataColloquioDa + "', 'DD/MM/YYYY') ");
		}
		if ((dataColloquioA != null) && (!dataColloquioA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" col.datColloquio<=to_date('" + dataColloquioA + "', 'DD/MM/YYYY') ");
		}

		if ((obiettivo != null) && (!obiettivo.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" deaz.prgAzioneRagg='" + obiettivo + "' ");
		}

		if ((azione != null) && (!azione.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.prgAzioni='" + azione + "' ");
		}

		if ((dataStimataDa != null) && (!dataStimataDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.datStimata>=to_date('" + dataStimataDa + "', 'DD/MM/YYYY') ");
		}

		if ((dataStimataA != null) && (!dataStimataA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.datStimata<=to_date('" + dataStimataA + "', 'DD/MM/YYYY') ");
		}

		addCodEsito(buf);

		if (esitoFiltro != null && !esitoFiltro.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.codesito='" + esitoFiltro + "' ");
		}

		if (codEsitoSifer != null && !codEsitoSifer.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.codesitoformazione='" + codEsitoSifer + "' ");
		}

		if (azioniEsitoDiverso != null && !azioniEsitoDiverso.equals("") && azioniEsitoDiverso.equalsIgnoreCase("on")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(
					" (perc.codEsito is not null and perc.codesitoformazione is not null and perc.codEsito <> perc.codesitoformazione)  ");
		}

		addCodEsitoRendicont(buf);

		if ((dataSvolgimentoDa != null) && (!dataSvolgimentoDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.datEffettiva>=to_date('" + dataSvolgimentoDa + "', 'DD/MM/YYYY') ");
		}
		if ((dataSvolgimentoA != null) && (!dataSvolgimentoA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" perc.datEffettiva<=to_date('" + dataSvolgimentoA + "', 'DD/MM/YYYY') ");
		}

		if ((azioniNonConclCheck != null) && (!azioniNonConclCheck.equals("")) && (azioniNonConclCheck.equals("on"))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" nvl(perc.codEsito, ' ')<>'FC' ");
		}

		if (!sedeEnteAtt.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}

			String[] sedeEnte = sedeEnteAtt.split("!");
			buf.append(" upper(vch.strcfenteaccreditato) = '" + sedeEnte[0].toUpperCase()
					+ "' and upper(vch.codsede) = '" + sedeEnte[1].toUpperCase() + "' ");
		} else {
			if (!cfEnteAtt.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(vch.strcfenteaccreditato) = '" + cfEnteAtt.toUpperCase() + "' ");
			}
		}

		if ((codCPI != null) && (!codCPI.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" lavinf.codCpiTit='" + codCPI + "' ");
		}

		if (azioniConcordate.size() > 0) {
			if (buf.length() == 0) {
				buf.append("WHERE (");
			} else {
				buf.append(" AND (");
			}

			StringBuffer sqlTemp = new StringBuffer();
			for (String azioneConcordata : azioniConcordate) {
				String[] pkAzioneConcordata = azioneConcordata.split("-");
				String prgColloquio = pkAzioneConcordata[0];
				String prgPercorso = pkAzioneConcordata[1];

				sqlTemp.append(" (perc.prgcolloquio=" + prgColloquio + " AND perc.prgPercorso=" + prgPercorso + ") OR");
			}
			sqlTemp.setCharAt(sqlTemp.length() - 2, ' ');
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')');
			buf.append(sqlTemp.toString());
		}

		buf.append(
				" order by col.datColloquio desc, perc.datStimata asc, deesito.strDescrizione asc, perc.datEffettiva asc ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

	/* aggiunge nella clausula where la ricerca del codEsito */
	private void addCodEsito(StringBuffer buf) {
		/* lista contenente gli esiti da cercare */
		List<String> codEsito = new ArrayList<String>();

		try {
			String esitoStr = ""; // parametro della request contenente gli esiti
			esitoStr = StringUtils.getAttributeStrNotNull(req, "esito");

			StringTokenizer st = new StringTokenizer(esitoStr, ",");

			for (; st.hasMoreTokens();) {
				codEsito.add(st.nextToken());
			}
		} catch (Exception e) {
			// codEsito = req.getAttributeAsVector("esito");
		}

		// se ci sono degli esiti
		if (codEsito.size() > 0) {
			String temp = "";
			// non capita se l'esito è obbligatorio
			if (buf.length() == 0) {
				buf.append("WHERE (");
			} else
				buf.append(" AND ( ");

			StringBuffer sqlTemp = new StringBuffer("perc.codEsito IN (");
			for (int i = 0; i < codEsito.size(); i++) {
				sqlTemp.append('\'');
				String elem = codEsito.get(i).toString();
				sqlTemp.append(elem);
				sqlTemp.append("',");
			}
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')');
			buf.append(sqlTemp.toString());
			buf.append(" ) ");
		}

	}

	/* aggiunge nella clausula where la ricerca del codEsitoRendicontazione */
	private void addCodEsitoRendicont(StringBuffer buf) {
		/* lista contenente gli esiti rendicontazione */
		List<String> codEsitoRendicont = new ArrayList<String>();

		try {
			String esitoRendicontStr = ""; // parametro della request contenente gli esiti
			esitoRendicontStr = StringUtils.getAttributeStrNotNull(req, "esitorendicont");

			StringTokenizer st = new StringTokenizer(esitoRendicontStr, ",");

			for (; st.hasMoreTokens();) {
				codEsitoRendicont.add(st.nextToken());
			}
		} catch (Exception e) {
			// codEsitoRendicont = req.getAttributeAsVector("esitorendicont");
		}

		// se ci sono degli esiti rendicontazione
		if (codEsitoRendicont.size() > 0) {
			// non capita se l'esito è obbligatorio
			if (buf.length() == 0) {
				buf.append("WHERE (");
			} else
				buf.append(" AND ( ");

			StringBuffer sqlTemp = new StringBuffer("perc.codEsitoRendicont IN (");
			for (int i = 0; i < codEsitoRendicont.size(); i++) {
				sqlTemp.append('\'');
				String elem = codEsitoRendicont.get(i).toString();
				sqlTemp.append(elem);
				sqlTemp.append("',");
			}
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')');
			buf.append(sqlTemp.toString());
			buf.append(" ) ");
		}

	}

}