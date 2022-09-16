package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaLavDichAnnuale implements IDynamicStatementProvider {
	public DynRicercaLavDichAnnuale() {
	}

	public static final int ANNO_NUOVA_REGOLA = 2014;

	private static final String SELECT_SQL_BASE = "select lav.CDNLAVORATORE CDNLAVORATORE, "
			+ " lav.strcodicefiscale codiceFiscaleLav, lav.STRCOGNOME cognomeLav, lav.STRNOME nomeLav, "
			+ " lav.strindirizzodom || ' ' || com.strdenominazione domicilioLav, "
			+ " to_char(did.datdichiarazione,'dd/mm/yyyy') datDichiarazioneDid, "
			+ " to_char(max(ann1.datdichiarazione), 'dd/mm/yyyy') datDichAnnUlt " + " from an_lavoratore lav "
			+ " inner join de_comune com on (lav.codcomdom = com.codcom) "
			+ " inner join am_elenco_anagrafico el on (lav.cdnlavoratore = el.cdnlavoratore) "
			+ " inner join am_dich_disponibilita did on (did.prgelencoanagrafico = el.prgelencoanagrafico) "
			+ " left join am_did_annuale ann1 on (ann1.prgdichdisponibilita = did.prgdichdisponibilita and ann1.codstatoatto = 'PR' ) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		String codCpi = StringUtils.getAttributeStrNotNull(req, "CodCPI");
		String annoConferma = StringUtils.getAttributeStrNotNull(req, "annoRif");
		String annoDid = StringUtils.getAttributeStrNotNull(req, "annoDid");
		String dataInizioDid = "01/01/" + annoDid;
		String dataFineDid = "31/12/" + annoDid;
		String dataFineMob = StringUtils.getAttributeStrNotNull(req, "datFineMob");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		query_totale.append(
				" where did.datfine is null AND did.codstatoatto = 'PR' AND did.datdichiarazione BETWEEN to_date('")
				.append(dataInizioDid).append("', 'dd/mm/yyyy') AND to_date('").append(dataFineDid)
				.append("', 'dd/mm/yyyy')").append(" AND lav.cdnlavoratore in ");

		String SELECT_SQL_INTERNA = "( (select lav.cdnlavoratore cdnlav from an_lavoratore lav "
				+ " inner join an_lav_storia_inf inf on (inf.cdnlavoratore = lav.cdnlavoratore) "
				+ " inner join am_elenco_anagrafico el on (inf.cdnlavoratore = el.cdnlavoratore) "
				+ " inner join am_dich_disponibilita did on (did.prgelencoanagrafico = el.prgelencoanagrafico) ";

		if (codCpi.equals("")) {
			SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " inner join de_cpi on (de_cpi.codcpi = inf.codcpitit) ";
			SELECT_SQL_INTERNA = SELECT_SQL_INTERNA
					+ " inner join ts_generale on (ts_generale.codprovinciasil = de_cpi.codprovincia) ";
			SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " where inf.codmonotipocpi = 'C' ";
		} else {
			SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " where inf.codcpitit = '" + codCpi + "' ";
			SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " and inf.codmonotipocpi = 'C' ";
		}

		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " and inf.datFine is null ";

		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " and did.datdichiarazione >= to_date('" + dataInizioDid
				+ "', 'dd/mm/yyyy') ";
		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " and did.datdichiarazione <= to_date('" + dataFineDid
				+ "', 'dd/mm/yyyy') ";
		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " and did.codstatoatto = 'PR' and did.datfine is null ";

		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA
				+ " and not exists (select 1 from am_movimento mov where mov.cdnlavoratore = lav.cdnlavoratore and mov.codstatoatto = 'PR' "
				+ " and mov.codtipomov <> 'CES' and to_number(to_char(mov.datiniziomov, 'yyyy')) <= " + annoConferma
				+ " and to_number(to_char(nvl(mov.datfinemoveffettiva, to_date('31/12/2100', 'dd/mm/yyyy')), 'yyyy')) >= "
				+ annoConferma + ") ";

		if (!dataFineMob.equals("")) {
			SELECT_SQL_INTERNA = SELECT_SQL_INTERNA
					+ " and not exists (select 1 from am_mobilita_iscr mob where mob.cdnlavoratore = lav.cdnlavoratore "
					+ " and nvl(mob.datFine, to_date('" + dataFineMob + "', 'dd/mm/yyyy')) >= to_date('" + dataFineMob
					+ "', 'dd/mm/yyyy')) ";
		}

		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " and not exists (select 1 from am_did_annuale ann "
				+ " where ann.prgdichdisponibilita = did.prgdichdisponibilita and ann.codstatoatto = 'PR' "
				+ " and ann.numannodichiarazione = " + annoConferma + ") )";

		if (!annoConferma.equals("")) {
			Integer annoDidAnnuale = new Integer(annoConferma);
			if (annoDidAnnuale < ANNO_NUOVA_REGOLA) {
				SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " minus (select lav.cdnlavoratore cdnlav "
						+ " from an_lavoratore lav inner join am_cm_iscr on (to_char(lav.cdnlavoratore) = decrypt(am_cm_iscr.cdnlavoratore, '"
						+ encryptKey + "')) "
						+ " inner join de_cm_tipo_iscr on (de_cm_tipo_iscr.codcmtipoiscr = am_cm_iscr.codcmtipoiscr) "
						+ " where am_cm_iscr.codstatoatto = 'PR' and de_cm_tipo_iscr.codmonotiporagg = 'D' "
						+ " and to_number(to_char(am_cm_iscr.datdatainizio, 'yyyy')) <= " + annoConferma
						+ " and to_number(to_char(nvl(am_cm_iscr.datdatafine, to_date('31/12/2100', 'dd/mm/yyyy')), 'yyyy')) >= "
						+ annoConferma + ") ";
			}
		}

		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " ) ";

		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA
				+ " group by lav.cdnlavoratore, lav.strcodicefiscale, lav.strcognome, lav.strnome, "
				+ " lav.strindirizzodom || ' ' || com.strdenominazione, to_char(did.datdichiarazione, 'dd/mm/yyyy') ";

		SELECT_SQL_INTERNA = SELECT_SQL_INTERNA + " order by lav.strcognome, lav.strnome";
		query_totale.append(SELECT_SQL_INTERNA);
		return query_totale.toString();
	}

}