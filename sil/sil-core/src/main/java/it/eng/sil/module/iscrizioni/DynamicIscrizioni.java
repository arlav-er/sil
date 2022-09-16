package it.eng.sil.module.iscrizioni;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

public class DynamicIscrizioni implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " select iscr.cdnlavoratore, iscr.prgazienda, iscr.prgunita, iscr.prgaltraiscr, "
			+ "	lav.strCognome || ' ' || lav.strNome as lavoratore, lav.strindirizzores || ' ' || comune.strdenominazione residenza, "
			+ " lav.strindirizzodom || '<br>' || comuneDom.strdenominazione indirizzoDomCompleto, "
			+ " lav.strTelDom || '<br>' || lav.strCell telefonoCompleto, "
			+ " lav.strtelres, lav.strTelDom, lav.strCell, " + " comuneDom.strdenominazione comDomicilio, "
			+ " to_char(lav.datnasc, 'dd/mm/yyyy') as datnasc, citt.strdescrizione cittadinanza, lav.strindirizzodom, "
			+ " case " + " when azi.strCodiceFiscale is not null "
			+ " then (azi.strragionesociale || '<br>' ||  '- CF ' || azi.strCodiceFiscale) " + " else '' "
			+ " end as datiAzienda, "
			+ "	azi.strragionesociale, azi.strCodiceFiscale, to_char(iscr.datInizio, 'DD/MM/YYYY') as datInizio, "
			+ " to_char(iscr.datFine, 'DD/MM/YYYY') as datFine, "
			+ " to_char(iscr.datCompetenza, 'DD/MM/YYYY') as datCompetenza, "
			+ " cpiLav.strDescrizione || decode (inf.codMonoTipoCpi, 'C', ' (Competente)', 'T', ' (Titolare)',' (Esterno)') as descrCpiLav, "
			+ " tipo.strDescrizione || DECODE(SYSDATE, GREATEST(SYSDATE, tipo.DATINIZIOVAL, tipo.DATFINEVAL),' (scaduto) ',"
			+ " LEAST(SYSDATE, tipo.DATINIZIOVAL, tipo.DATFINEVAL),' (scaduto) ', "
			+ " '') AS tipoIscr, motChius.strDescrizione motChiusura, "
			+ " statoIscr.strdescrizione descrStato, statoIscr.flgSl, iscr.strNota, acc.CODACCORDO "
			+ " from am_altra_iscr iscr " + " inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
			+ " inner join an_lav_storia_inf inf on (inf.cdnlavoratore = lav.cdnlavoratore) "
			+ " inner join de_comune comune on (comune.codcom = lav.codcomres) "
			+ " inner join de_comune comuneDom on (comuneDom.codcom = lav.codcomdom) "
			+ " inner join de_cittadinanza citt on (citt.codcittadinanza = lav.codcittadinanza) "
			+ " inner join de_tipo_iscr tipo on (tipo.codtipoiscr = iscr.codtipoiscr) "
			+ " left join de_motivo_chiusuraiscr motChius on (motChius.codMotChiusuraIscr = iscr.codMotChiusuraIscr) "
			+ " left join de_stato_altra_iscr statoIscr on (statoIscr.codstato = iscr.codstato) "
			+ " left join de_cpi cpiLav on (inf.codCpiTit = cpiLav.codCpi) "
			+ " left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) "
			+ " left join an_unita_azienda uni_azi on (uni_azi.prgazienda = iscr.prgazienda AND uni_azi.prgunita = iscr.prgunita)"
			+ " left join de_comune comuAz on (comuAz.codCom = uni_azi.codCom) "
			+ " left join ci_accordo acc on (acc.prgaccordo = iscr.prgaccordo) " + " where inf.datFine is null ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String codAccordo = SourceBeanUtils.getAttrStrAvoidInjection(req, "codAccordo");
		String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(req, "cdnLavoratore");
		String prgAzienda = SourceBeanUtils.getAttrStrNotNull(req, "prgAzienda");
		String prgUnita = SourceBeanUtils.getAttrStrNotNull(req, "prgUnita");

		String datInizioDa = SourceBeanUtils.getAttrStrNotNull(req, "datinizioda");
		String datInizioA = SourceBeanUtils.getAttrStrNotNull(req, "datinizioa");

		String datFineDa = SourceBeanUtils.getAttrStrNotNull(req, "datfineda");
		String datFineA = SourceBeanUtils.getAttrStrNotNull(req, "datfinea");

		String codstato = SourceBeanUtils.getAttrStrNotNull(req, "codstato");

		String datCompetenzaDa = SourceBeanUtils.getAttrStrNotNull(req, "datcompetenzada");
		String datCompetenzaA = SourceBeanUtils.getAttrStrNotNull(req, "datcompetenzaa");

		String codMotChiusuraIscr = SourceBeanUtils.getAttrStrNotNull(req, "codmotchiusuraiscr");

		String datChiusuraIscrDa = SourceBeanUtils.getAttrStrNotNull(req, "datchiusuraiscrda");
		String datChiusuraIscrA = SourceBeanUtils.getAttrStrNotNull(req, "datchiusuraiscra");

		String codCPILav = SourceBeanUtils.getAttrStrNotNull(req, "CodCPILav");

		String lista = req.containsAttribute("LISTA") ? "1" : "";

		String codListaStr = "";
		StringTokenizer st = null;
		Vector vTipoLista = new Vector();
		if (req.containsAttribute("stampa")) {
			try {
				codListaStr = StringUtils.getAttributeStrNotNull(req, "tipoIscr");
				st = new StringTokenizer(codListaStr, ",");
				for (; st.hasMoreTokens();) {
					vTipoLista.add(st.nextToken().trim());
				}
			} catch (Exception e) {
				vTipoLista = req.getAttributeAsVector("tipoIscr");
			}
		} else {
			vTipoLista = req.getAttributeAsVector("codTipoIscr");
		}
		String codTipoIscr = "";
		if (vTipoLista.size() > 0) {
			for (int i = 0; i < vTipoLista.size(); i++) {
				if (!vTipoLista.elementAt(i).equals("")) {
					if (codTipoIscr.length() > 0) {
						codTipoIscr = codTipoIscr + "," + "'" + vTipoLista.elementAt(i) + "'";
					} else {
						codTipoIscr += "'" + vTipoLista.elementAt(i) + "'";
					}
				}
			}
		}

		String codCPIAz = SourceBeanUtils.getAttrStrNotNull(req, "CodCPIAz");
		String codProvAz = SourceBeanUtils.getAttrStrNotNull(req, "codProvAz");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (!cdnLavoratore.equals("")) {
			buf.append(" AND iscr.CDNLAVORATORE ='" + cdnLavoratore + "' ");
		}

		if (lista.equals("")) {
			if (!prgAzienda.equals("")) {
				buf.append(" AND iscr.PRGAZIENDA ='" + prgAzienda + "' ");
			}

			if (!prgUnita.equals("")) {
				buf.append(" AND iscr.PRGUNITA ='" + prgUnita + "' ");
			}

			if (!datInizioDa.equals("")) {
				buf.append(" AND iscr.DATINIZIO >=to_date('" + datInizioDa + "', 'DD/MM/YYYY') ");
			}
			if (!datInizioA.equals("")) {
				buf.append(" AND iscr.DATINIZIO <=to_date('" + datInizioA + "', 'DD/MM/YYYY') ");
			}

			if (!datFineDa.equals("")) {
				buf.append(" AND iscr.DATFINE >=to_date('" + datFineDa + "', 'DD/MM/YYYY') ");
			}
			if (!datFineA.equals("")) {
				buf.append(" AND iscr.DATFINE <=to_date('" + datFineA + "', 'DD/MM/YYYY') ");
			}

			if (!codstato.equals("")) {
				if (codstato.equalsIgnoreCase("VA")) {
					buf.append(" AND iscr.CODSTATO is null ");
				} else {
					buf.append(" AND iscr.CODSTATO ='" + codstato + "' ");
				}
			}

			if (!datCompetenzaDa.equals("")) {
				buf.append(" AND iscr.DATCOMPETENZA >=to_date('" + datCompetenzaDa + "', 'DD/MM/YYYY') ");
			}
			if (!datCompetenzaA.equals("")) {
				buf.append(" AND iscr.DATCOMPETENZA <=to_date('" + datCompetenzaA + "', 'DD/MM/YYYY') ");
			}

			if (!codMotChiusuraIscr.equals("")) {
				buf.append(" AND iscr.CODMOTCHIUSURAISCR ='" + codMotChiusuraIscr + "' ");
			}

			if (!datChiusuraIscrDa.equals("")) {
				buf.append(" AND iscr.DATCHIUSURAISCR >=to_date('" + datChiusuraIscrDa + "', 'DD/MM/YYYY') ");
			}
			if (!datChiusuraIscrA.equals("")) {
				buf.append(" AND iscr.DATCHIUSURAISCR <=to_date('" + datChiusuraIscrA + "', 'DD/MM/YYYY') ");
			}

			if (!codCPILav.equals("")) {
				buf.append(" AND inf.codCpiTit ='" + codCPILav + "' ");
				buf.append(" and inf.codmonotipocpi = 'C' ");
			}

			if (req.containsAttribute("compNonAmm")) {
				buf.append(" AND iscr.CODMONOTIPOCOMPETENZA <> 'A'");
			}

			if (!codTipoIscr.equals("")) {
				buf.append(" AND iscr.CODTIPOISCR in (" + codTipoIscr + ")");
			}

			if (!codCPIAz.equals("")) {
				buf.append(" AND comuAz.codCpi ='" + codCPIAz + "' ");
			}

			if (!codProvAz.equals("")) {
				buf.append(" AND comuAz.codProvincia ='" + codProvAz + "' ");
			}

			if (!codAccordo.equals("")) {
				buf.append(" AND upper(acc.codaccordo) like upper('%" + codAccordo + "')");
			}
		}

		boolean existCFLav = req.containsAttribute("cbCfLav");
		boolean existCogNomLav = req.containsAttribute("cbCogNomLav");
		boolean existDatIscr = req.containsAttribute("cbDatIscr");
		boolean existTipoIscr = req.containsAttribute("cbTipoIscr");
		boolean existComuneRes = req.containsAttribute("cbComuneRes");
		boolean alreadyCheck = false;

		if (existCFLav || existCogNomLav || existDatIscr || existTipoIscr || existComuneRes) {

			buf.append(" ORDER BY");

			if (existCFLav) {
				alreadyCheck = true;
				String radioCfLav = SourceBeanUtils.getAttrStrNotNull(req, "radioCfLav");
				String ord = "";
				if (radioCfLav.equals("DESC")) {
					ord = " DESC";
				} else {
					ord = " ASC";
				}
				buf.append(" lav.strCodiceFiscale " + ord);
			}

			if (existCogNomLav) {
				if (alreadyCheck) {
					buf.append(", ");
				} else {
					alreadyCheck = true;
				}
				String radioCogNomLav = SourceBeanUtils.getAttrStrNotNull(req, "radioCogNomLav");
				String ord = "";
				if (radioCogNomLav.equals("DESC")) {
					ord = " DESC";
				} else {
					ord = " ASC";
				}
				buf.append(" lav.strCognome " + ord + ", lav.strNome " + ord);
			}

			if (existDatIscr) {
				if (alreadyCheck) {
					buf.append(", ");
				} else {
					alreadyCheck = true;
				}
				String radioDatIscr = SourceBeanUtils.getAttrStrNotNull(req, "radioDatIscr");
				String ord = "";
				if (radioDatIscr.equals("DESC")) {
					ord = " DESC";
				} else {
					ord = " ASC";
				}
				buf.append(" iscr.datInizio " + ord);
			}

			if (existTipoIscr) {
				if (alreadyCheck) {
					buf.append(", ");
				} else {
					alreadyCheck = true;
				}
				String radioTipoIscr = SourceBeanUtils.getAttrStrNotNull(req, "radioTipoIscr");
				String ord = "";
				if (radioTipoIscr.equals("DESC")) {
					ord = " DESC";
				} else {
					ord = " ASC";
				}
				buf.append(" iscr.codtipoIscr " + ord);
			}

			if (existComuneRes) {
				if (alreadyCheck) {
					buf.append(", ");
				} else {
					alreadyCheck = true;
				}
				String radioComuneRes = SourceBeanUtils.getAttrStrNotNull(req, "radioComuneRes");
				String ord = "";
				if (radioComuneRes.equals("DESC")) {
					ord = " DESC";
				} else {
					ord = " ASC";
				}
				buf.append(" comune.strdenominazione " + ord);
			}

		} else {
			buf.append(" ORDER BY iscr.datInizio DESC");
		}
		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
