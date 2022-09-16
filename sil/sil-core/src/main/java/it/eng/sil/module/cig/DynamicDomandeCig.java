package it.eng.sil.module.cig;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

public class DynamicDomandeCig implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " select iscr.cdnlavoratore, iscr.prgazienda, iscr.prgaltraiscr, "
			+ "	lav.strCodiceFiscale || '<BR/>' || lav.strCognome || '<BR/>' || lav.strNome as lavoratore,"
			+ " lav.strIndirizzoRes || ' ' || comuneRes.strdenominazione as strIndirizzoRes, lav.strTelRes,"
			+ " decode(azi.strragionesociale,null,'',azi.strragionesociale || '<BR/>- ') || "
			+ " decode(azi.strcodicefiscale,null,'','CF ' || azi.strcodicefiscale) as strragionesociale , "
			+ " to_char(iscr.datInizio, 'DD/MM/YYYY') as datInizio,"
			+ " to_char(iscr.datFine, 'DD/MM/YYYY') as datFine,"
			+ " to_char(iscr.datCompetenza, 'DD/MM/YYYY') as datCompetenza,"
			+ " cpiLav.strDescrizione || decode (inf.codMonoTipoCpi, 'C', ' (Competente)', 'T', ' (Titolare)',' (Esterno)') as descrCpiLav, "
			+ " tipo.strDescrizione tipoIscr, motChius.strDescrizione motChiusura,"
			+ " statoIscr.strdescrizione descrStato, acc.CODACCORDO, iscr.prgaccordo, statoIscr.flgSl, iscr.strNota"
			+ " from am_altra_iscr iscr" + " inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore)"
			+ " inner join an_lav_storia_inf inf on (inf.cdnlavoratore = lav.cdnlavoratore and inf.datFine is null)"
			+ " inner join de_comune comuneRes on (comuneRes.codcom = lav.codcomres) "
			+ " inner join de_tipo_iscr tipo on (tipo.codtipoiscr = iscr.codtipoiscr)"
			+ " left join de_motivo_chiusuraiscr motChius on (motChius.codMotChiusuraIscr = iscr.codMotChiusuraIscr) "
			+ " left join de_stato_altra_iscr statoIscr on (statoIscr.codstato = iscr.codstato) "
			+ " left join de_cpi cpiLav on (inf.codCpiTit = cpiLav.codCpi) "
			+ " left join an_azienda azi on (azi.prgazienda = iscr.prgazienda)"
			+ " left join an_unita_azienda uni_azi on (uni_azi.prgazienda = iscr.prgazienda AND uni_azi.prgunita = iscr.prgunita)"
			+ " left join de_comune comu on (comu.codCom = uni_azi.codCom)"
			+ " left join ci_accordo acc on (acc.prgaccordo = iscr.prgaccordo)" + " where 1=1 ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String codAccordo = SourceBeanUtils.getAttrStrAvoidInjection(req, "codAccordo");

		String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(req, "cdnLavoratore");
		String prgAzienda = SourceBeanUtils.getAttrStrNotNull(req, "prgAzienda");

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

		Vector codTipoIscr = new Vector();
		String tipoIscrStr = "";
		String tipoIscr = "";

		try {
			tipoIscr = StringUtils.getAttributeStrNotNull(req, "tipoIscr");

			StringTokenizer st = new StringTokenizer(tipoIscr, ",");

			for (; st.hasMoreTokens();) {
				codTipoIscr.add(st.nextToken());
			}

		} catch (Exception e) {
			codTipoIscr = req.getAttributeAsVector("tipoIscr");
		}

		/*
		 * String codTipoIscr = ""; Vector list = req.getAttributeAsVector("codTipoIscr"); if(list.size()!=0) { for(int
		 * i=0; i<list.size(); i++) { if(codTipoIscr.length()>0) codTipoIscr += ","; if(!list.elementAt(i).equals("")) {
		 * codTipoIscr += "'" + list.elementAt(i) + "'"; } } }
		 */

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

			/*
			 * if (!codTipoIscr.equals("")) { buf.append(" AND iscr.CODTIPOISCR in (" + codTipoIscr + ")"); }
			 */

			if ((codTipoIscr != null && codTipoIscr.size() > 0)) {
				StringBuffer sqlTemp = new StringBuffer(" AND iscr.CODTIPOISCR in (");
				for (int i = 0; i < codTipoIscr.size(); i++) {
					sqlTemp.append('\'');
					String elem = codTipoIscr.get(i).toString();
					sqlTemp.append(elem);
					sqlTemp.append("',");
				}
				sqlTemp.setCharAt(sqlTemp.length() - 1, ')');
				buf.append(sqlTemp.toString());
			}

			if (!codCPIAz.equals("")) {
				buf.append(" AND comu.codCpi ='" + codCPIAz + "' ");
			}

			if (!codProvAz.equals("")) {
				buf.append(" AND comu.codProvincia ='" + codProvAz + "' ");
			}

			if (!codAccordo.equals("")) {
				buf.append(" AND upper(acc.codaccordo) like upper('%" + codAccordo + "')");
			}
		}

		boolean existCFLav = req.containsAttribute("cbCfLav");
		boolean existCogNomLav = req.containsAttribute("cbCogNomLav");
		boolean existDatIscr = req.containsAttribute("cbDatIscr");
		boolean existTipoIscr = req.containsAttribute("cbTipoIscr");
		boolean alreadyCheck = false;

		if (existCFLav || existCogNomLav || existDatIscr || existTipoIscr) {

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

		} else {
			buf.append(" ORDER BY iscr.datInizio DESC");
		}
		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
