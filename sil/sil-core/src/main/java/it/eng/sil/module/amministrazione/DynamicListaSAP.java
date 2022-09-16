package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class DynamicListaSAP implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicListaSAP.class.getName());

	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " sp_lav.codMinSap, "
			+ " lav.strCodiceFiscale || ' ' || lav.strCognome || ' ' ||  lav.strNome as Lavoratore, "
			+ " sp_lav.cdnLavoratore, " + " sp_lav.prgSpLav, " + " sp_lav.codStato as codStatoSap, "
			+ " de_stato_sap.strDescrizione as descStatoSap, "
			+ " DECODE(cpi.codCpi, NULL, '', cpi.strDescrizione || ' (' || ai.codMonoTipoCpi || ') ') as CPI, "
			+ " to_char(sp_lav.datInizioVal,'dd/mm/yyyy') as datInizioVal, "
			+ " to_char(sp_lav.datFineVal,'dd/mm/yyyy') as datFineVal, " + " sp_lav.cdnUtMod, "
			+ " to_char(sp_lav.dtmMod,'dd/mm/yyyy hh24:mi') as dtmMod, "
			+ " ts_utente.strCognome || ' ' ||  ts_utente.strNome as Utente, " + " case "
			+ " 	when sp_tracc.prgestrazionesap is not null and upper(sp_tracc.stresitomin) like 'KO%' "
			+ "     then to_char(sp_tracc.prgestrazionesap) " + "  	else '0' " + " end as prgestrazionehiddencolumn ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		String codCpi = user.getCodRif();

		DynamicStatementUtils dynamicStatementUt = new DynamicStatementUtils();

		String cf = StringUtils.getAttributeStrNotNull(req, "strCodiceFiscale");
		String cercaNellaLista = StringUtils.getAttributeStrNotNull(req, "cercaNellaLista");
		if (("si").equalsIgnoreCase(cercaNellaLista)) {
			cf = StringUtils.getAttributeStrNotNull(req, "strCodiceFiscaleLista");
		}
		String cognome = StringUtils.getAttributeStrNotNull(req, "strCognome");
		String nome = StringUtils.getAttributeStrNotNull(req, "strNome");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String dataInvioMinDa = StringUtils.getAttributeStrNotNull(req, "dataInvioMinDa");
		String dataInvioMinA = StringUtils.getAttributeStrNotNull(req, "dataInvioMinA");
		String dtmModDa = StringUtils.getAttributeStrNotNull(req, "dtmModDa");
		String dtmModA = StringUtils.getAttributeStrNotNull(req, "dtmModA");
		String codMinSap = StringUtils.getAttributeStrNotNull(req, "codMinSap");
		String codStatoSap = StringUtils.getAttributeStrNotNull(req, "codStatoSap");
		String ricercaSoloUltimoStato = StringUtils.getAttributeStrNotNull(req, "ricercaSoloUltimoStato");
		String ricercaSoloConNotificheMin = StringUtils.getAttributeStrNotNull(req, "ricercaSoloConNotificheMin");
		String ricercaSoloMieiLavoratori = StringUtils.getAttributeStrNotNull(req, "ricercaSoloMieiLavoratori");
		String ricercaDidAttiva = StringUtils.getAttributeStrNotNull(req, "ricercaDidAttiva");
		String ricercaPattoAttivo = StringUtils.getAttributeStrNotNull(req, "ricercaPattoAttivo");
		String ricercaErroreSAP = StringUtils.getAttributeStrNotNull(req, "ricercaErroreSAP");
		String ricercaAutoSAP = StringUtils.getAttributeStrNotNull(req, "ricercaAutoSAP");

		dynamicStatementUt.addSelect(SELECT_SQL_BASE);

		dynamicStatementUt.addFrom("sp_lavoratore sp_lav");
		dynamicStatementUt.addFrom("de_stato_sap");
		dynamicStatementUt.addFrom("an_lavoratore lav");
		dynamicStatementUt.addFrom("an_lav_storia_inf ai");
		dynamicStatementUt.addFrom("de_cpi cpi");
		dynamicStatementUt.addFrom("ts_utente");
		dynamicStatementUt.addFrom("ts_tracciamento_sap sp_tracc");

		if (("si").equalsIgnoreCase(ricercaAutoSAP)) {
			dynamicStatementUt.addFrom("ts_invio_aut_sap aut_sap");
		}

		dynamicStatementUt.addWhere(" lav.cdnlavoratore = sp_lav.cdnlavoratore ");
		dynamicStatementUt.addWhere(" sp_lav.codStato = de_stato_sap.codStato (+) ");
		dynamicStatementUt.addWhere(" sp_lav.cdnlavoratore = sp_tracc.cdnlavoratore (+) ");
		dynamicStatementUt.addWhere(" lav.cdnLavoratore = ai.cdnLavoratore and ai.datFine is null ");
		dynamicStatementUt.addWhere(" ai.codCpiTit = cpi.codCpi (+) ");
		dynamicStatementUt.addWhere(" sp_lav.cdnUtMod = ts_utente.cdnUt ");

		if (("si").equalsIgnoreCase(ricercaAutoSAP)) {
			dynamicStatementUt.addWhere(" aut_sap.cdnLavoratore = sp_lav.cdnLavoratore ");
		}

		if (codMinSap.length() != 0) {
			dynamicStatementUt.addWhereIfFilledStrUpper("sp_lav.codMinSap", codMinSap);
		} else {
			if (tipoRic.equalsIgnoreCase("esatta")) {
				dynamicStatementUt.addWhereIfFilledStrUpper("sp_lav.strCodiceFiscale", cf);
				dynamicStatementUt.addWhereIfFilledStrUpper("lav.strCognome", cognome);
				dynamicStatementUt.addWhereIfFilledStrUpper("lav.strNome", nome);
			} else {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("sp_lav.strCodiceFiscale", cf,
						DynamicStatementUtils.DO_LIKE_INIZIA);
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strCognome", cognome,
						DynamicStatementUtils.DO_LIKE_INIZIA);
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strNome", nome,
						DynamicStatementUtils.DO_LIKE_INIZIA);
			}
			dynamicStatementUt.addWhereIfFilledDateBetween("sp_lav.dataInvioMin", dataInvioMinDa, dataInvioMinA);
			dynamicStatementUt.addWhereIfFilledDateBetween("sp_lav.dtmMod", dtmModDa, dtmModA);
			dynamicStatementUt.addWhereIfFilledStrUpper("sp_lav.codStato", codStatoSap);

			if (("si").equalsIgnoreCase(ricercaErroreSAP) && !("si").equalsIgnoreCase(ricercaAutoSAP)) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("sp_tracc.strEsitoMin", "KO",
						DynamicStatementUtils.DO_LIKE_INIZIA);
			} else if (("si").equalsIgnoreCase(ricercaErroreSAP) && ("si").equalsIgnoreCase(ricercaAutoSAP)) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("aut_sap.strEsitoMin", "KO",
						DynamicStatementUtils.DO_LIKE_INIZIA);
			}
			if (("si").equalsIgnoreCase(ricercaSoloUltimoStato)) {
				dynamicStatementUt.addWhere(" sp_lav.datFineVal is null ");
				dynamicStatementUt.addOrder("sp_lav.datInizioVal desc");
			} else {
				dynamicStatementUt.addOrder("sp_lav.strCodiceFiscale asc");
			}

			if (("si").equalsIgnoreCase(ricercaSoloConNotificheMin)) {
				dynamicStatementUt.addWhere(
						" exists (select 1 from sp_notifica sp_not where sp_not.codMinSap = sp_lav.codMinSap) ");
			}

			if (("si").equalsIgnoreCase(ricercaSoloMieiLavoratori)) {
				dynamicStatementUt.addWhere(" ai.codCpiTit = '" + codCpi + "' ");
			}

			if (("si").equalsIgnoreCase(ricercaDidAttiva)) {
				dynamicStatementUt.addFrom("am_dich_disponibilita did");
				dynamicStatementUt.addFrom("am_elenco_anagrafico ea");
				dynamicStatementUt.addWhere(" did.prgelencoanagrafico = ea.prgelencoanagrafico");
				dynamicStatementUt.addWhere(" ea.cdnlavoratore = lav.cdnlavoratore");
				dynamicStatementUt.addWhere(" did.codStatoAtto = 'PR'");
				dynamicStatementUt.addWhere(" did.datFine is null");
			}

			if (("si").equalsIgnoreCase(ricercaPattoAttivo)) {
				dynamicStatementUt.addFrom("am_patto_lavoratore pt");
				dynamicStatementUt.addWhere(" pt.cdnlavoratore = lav.cdnlavoratore");
				dynamicStatementUt.addWhere(" pt.codstatoatto = 'PR'");
				dynamicStatementUt.addWhere(" pt.datFine is null");
			}

		}

		_logger.debug(className + "::Stringa di ricerca:" + dynamicStatementUt.toString());

		return (dynamicStatementUt.getStatement());
	}

}
