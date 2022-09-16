package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.webservices.utils.Utils;

/**
 * Questa classe restituisce la select per la ricerca di lavoratori immediatamente disponibili
 * 
 * @author Togna Cosimo date 22/03/2005
 */
public class DynamicDisponibilita implements IDynamicStatementProvider {

	private static String SELECT_SQL_BASE = " lav.strnome, lav.strcognome, " + " lav.strCodiceFiscale, "
			+ " to_char(did.datScadConferma, 'dd/mm/yyyy') as DCONF,  "
			+ " to_char(did.datScadErogazServizi, 'dd/mm/yyyy') as DSCEROG,  "
			+ " to_char(did.datdichiarazione, 'dd/mm/yyyy') as DDICH,   " + " did.strnote, "
			+ " to_char(did.dtmins, 'dd/mm/yyyy') as dtmins," + " to_char(did.dtmmod, 'dd/mm/yyyy') as dtmod,  "
			+ " did.numklodichdisp," + " did.prgDichDisponibilita,        "
			+ " lav.cdnLavoratore as CDNLAVORATORE,              " + " lav.cdnUtIns, " + " lav.cdnUtMod,           "
			+ " de_did.CODTIPODICHDISP as codtipo, "
			+ " decode(de_did.CODTIPODICHDISP, 'ID','Imm. Disp.','CD','Conf. Imm. Disp.', "
			+ "				          		   'IM','Coll. Mirato','nessuno') as DESCTIPODISP, "
			+ " to_char(did.datfine, 'dd/mm/yyyy') as DFINE,   "
			+ "PG_UTILS.TRUNC_DESC(mot.STRDESCRIZIONE, 30, '...') as MOTIVOFINE ";

	// + " mot.STRDESCRIZIONE as MOTIVOFINE ";

	/**
	 * Restituisce la stringa che rappresenta la select per effettuare la ricerca
	 * 
	 * @return String
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean request = requestContainer.getServiceRequest();
		DynamicStatementUtils dynamicStatementUt = new DynamicStatementUtils();

		/*
		 * Recupero i dati dalla request
		 */
		String codiceFiscale = (String) request.getAttribute("cf");
		String nome = (String) request.getAttribute("nome");
		String cognome = (String) request.getAttribute("cognome");
		String tipoRicerca = StringUtils.getAttributeStrNotNull(request, "tipoRicerca");
		String dataStipulaDa = StringUtils.getAttributeStrNotNull(request, "dataStipulaDa");
		String dataStipulaA = StringUtils.getAttributeStrNotNull(request, "dataStipulaA");
		String dataColloquioDa = StringUtils.getAttributeStrNotNull(request, "dataColloquioDa");
		String dataColloquioA = StringUtils.getAttributeStrNotNull(request, "dataColloquioA");
		String dataPattoDa = StringUtils.getAttributeStrNotNull(request, "dataPattoDa");
		String dataPattoA = StringUtils.getAttributeStrNotNull(request, "dataPattoA");
		String attiAperti = StringUtils.getAttributeStrNotNull(request, "AttiAperti");
		String dataFineAttoDa = StringUtils.getAttributeStrNotNull(request, "dataFineAttoDa");
		String dataFineAttoA = StringUtils.getAttributeStrNotNull(request, "dataFineAttoA");
		String motivoFine = StringUtils.getAttributeStrNotNull(request, "MotivoFine");
		String codCPI = (String) request.getAttribute("codcpi");
		String flgDidTelematica = StringUtils.getAttributeStrNotNull(request, "didTelematica");
		String flgDidRiapertaTelematica = StringUtils.getAttributeStrNotNull(request, "didRiapertaTelematica");
		String numDelibera = StringUtils.getAttributeStrNotNull(request, "NUMDELIBERA");
		String precarioDataDid = StringUtils.getAttributeStrNotNull(request, "precarioDataDid");
		String flgDidL68 = StringUtils.getAttributeStrNotNull(request, "flgDidL68");

		/*
		 * Preparo la query
		 */
		/*
		 * String sqlSel = ""; if (attiAperti.equals("No") || attiAperti.equals("")){ sqlSel = ",to_char(did.datfine,
		 * 'dd/mm/yyyy') as DFINE," + " mot.STRDESCRIZIONE as MOTIVOFINE"; }
		 * 
		 * String querySel = SELECT_SQL_BASE + sqlSel;
		 */
		dynamicStatementUt.addSelect(SELECT_SQL_BASE);

		dynamicStatementUt.addFrom("am_dich_disponibilita did");
		dynamicStatementUt.addFrom("de_tipo_dich_disp de_did");
		dynamicStatementUt.addFrom("am_elenco_anagrafico ea");
		dynamicStatementUt.addFrom("an_lavoratore lav");
		// commentato il 16/05/2005
		// dynamicStatementUt.addFrom("an_lav_storia_inf_coll coll");
		dynamicStatementUt.addFrom("an_lav_storia_inf inf");
		dynamicStatementUt.addFrom("de_stato_atto st");

		// if (attiAperti.equals("No") || attiAperti.equals("")){
		dynamicStatementUt.addFrom("de_motivo_fine_atto mot");
		// }
		// dynamicStatementUt.addFrom("de_motivo_fine_atto mot");
		dynamicStatementUt.addWhere("did.codstatoatto = st.codstatoatto");
		dynamicStatementUt.addWhere("st.codmonoprimadopoins != 'D'");

		dynamicStatementUt.addWhere("did.prgelencoanagrafico = ea.prgelencoanagrafico");
		dynamicStatementUt.addWhere("de_did.codtipodichdisp = did.codtipodichdisp");
		dynamicStatementUt.addWhere("ea.cdnlavoratore = lav.cdnlavoratore");

		// aggiunto il 16/05/2005
		dynamicStatementUt.addWhere("lav.CDNLAVORATORE = inf.CDNLAVORATORE");

		// ricerca degli atti aperti, chiusi o entrambi
		if (attiAperti.equals("Si")) {
			dynamicStatementUt.addWhere("did.codmotivofineatto = mot.codmotivofineatto (+)");
			dynamicStatementUt.addWhere("did.datFine is null");
		}

		if (attiAperti.equals("No")) {
			dynamicStatementUt.addWhere("did.codmotivofineatto = mot.codmotivofineatto (+)");
			dynamicStatementUt.addWhereIfFilledStr("did.codmotivofineatto", motivoFine);
			dynamicStatementUt.addWhere("did.datFine is not null ");
			dynamicStatementUt.addWhereIfFilledDateBetween("did.datFine", dataFineAttoDa, dataFineAttoA);
			dynamicStatementUt.addWhereIfFilledStr("did.numdelibera", numDelibera, "=");
		}

		if (attiAperti.equals("")) {
			dynamicStatementUt.addWhere("did.codmotivofineatto = mot.codmotivofineatto (+)");
			String addWhereTemp = "";
			// atti aperti Si
			addWhereTemp = addWhereTemp + " (did.datFine is null ";
			// atti aperti No
			if (!dataFineAttoDa.equals("")) {
				addWhereTemp = addWhereTemp + " or (trunc(did.datFine) >= TO_DATE('" + dataFineAttoDa
						+ "','DD/MM/YYYY') ";
			}
			if (!dataFineAttoA.equals("")) {
				if (!dataFineAttoDa.equals("")) {
					addWhereTemp = addWhereTemp + " and trunc(did.datFine) <= TO_DATE('" + dataFineAttoA
							+ "','DD/MM/YYYY') ";
				} else {
					addWhereTemp = addWhereTemp + " or (trunc(did.datFine) <= TO_DATE('" + dataFineAttoA
							+ "','DD/MM/YYYY') ";
				}
			}
			if (dataFineAttoA.equals("") && dataFineAttoDa.equals("")) {
				addWhereTemp = addWhereTemp + " or (did.datFine is not null ";
			}
			if (!motivoFine.equals("")) {
				addWhereTemp = addWhereTemp + " and did.codmotivofineatto = '" + motivoFine + "'";
			}
			addWhereTemp = addWhereTemp + ") ) ";
			dynamicStatementUt.addWhere(addWhereTemp);
		}

		// Ricerca esatta
		if (tipoRicerca.equalsIgnoreCase("esatta")) {
			dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strnome", nome, DynamicStatementUtils.DO_LIKE_no);

			dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strcognome", cognome,
					DynamicStatementUtils.DO_LIKE_no);

			dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strCodiceFiscale", codiceFiscale,
					DynamicStatementUtils.DO_LIKE_no);
			// Ricerca inizia per
		} else {
			dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strnome", nome, DynamicStatementUtils.DO_LIKE_INIZIA);

			dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strcognome", cognome,
					DynamicStatementUtils.DO_LIKE_INIZIA);

			dynamicStatementUt.addWhereIfFilledStrLikeUpper("lav.strCodiceFiscale", codiceFiscale,
					DynamicStatementUtils.DO_LIKE_INIZIA);
		}

		if ((codCPI != null) && (!codCPI.trim().equals(""))) {
			dynamicStatementUt.addWhereIfFilledStr("inf.codCPITit", codCPI);
			dynamicStatementUt.addWhereIfFilledStr("inf.codMonoTipoCpi", "C");
		}

		dynamicStatementUt.addWhereIfFilledDateBetweenOrNull("did.datdichiarazione", dataStipulaDa, dataStipulaA);
		// Savino 02/11/05 i campi datScadConferma e datScadErogazServizi erano
		// invertiti
		dynamicStatementUt.addWhereIfFilledDateBetween("did.datScadConferma", dataColloquioDa, dataColloquioA);
		dynamicStatementUt.addWhereIfFilledDateBetween("did.datScadErogazServizi", dataPattoDa, dataPattoA);

		if (flgDidTelematica.equalsIgnoreCase("S")) {
			dynamicStatementUt.addWhereIfFilledNum("did.cdnutins", Utils.utenteServiziPortale);
		}

		if ("S".equalsIgnoreCase(flgDidRiapertaTelematica)) {
			dynamicStatementUt.addWhere("(did.cdnutmod = 150 and (did.dtmmod > (did.dtmins + 60/(24*60*60))))");
		}

		if (("S").equalsIgnoreCase(precarioDataDid)) {
			dynamicStatementUt.addWhere("exists (select 1 from am_stato_occupaz "
					+ " where am_stato_occupaz.cdnlavoratore = lav.cdnlavoratore and "
					+ " trunc(did.datdichiarazione) between trunc(am_stato_occupaz.datinizio) and trunc(nvl(am_stato_occupaz.datfine,sysdate)) "
					+ " and am_stato_occupaz.codstatooccupaz = 'A212')");
		}

		if (("S").equalsIgnoreCase(flgDidL68)) {
			dynamicStatementUt.addWhereIfFilledStr("upper(did.flgDidL68)", "S");
		}

		// commentato il 16/05/2005
		// dynamicStatementUt.addWhere("coll.codLstTab = 'EA'");
		// dynamicStatementUt.addWhere("ea.prgElencoAnagrafico =
		// coll.strChiaveTabella");
		// dynamicStatementUt.addWhere("coll.prgLavStoriaInf =
		// inf.prgLavStoriaInf");

		// dynamicStatementUt.addWhere("nvl(inf.datFine,
		// to_date('01/01/2100','dd/mm/yyyy')) =
		// to_date('01/01/2100','dd/mm/yyyy') ");
		dynamicStatementUt.addWhere("DECODE(inf.DATFINE,NULL,'S','N')='S'");
		dynamicStatementUt.addOrder("did.datdichiarazione desc");
		dynamicStatementUt.addOrder("lav.strcognome");
		dynamicStatementUt.addOrder("lav.strnome");
		dynamicStatementUt.addOrder("lav.strcodicefiscale");

		return (dynamicStatementUt.getStatement());

	}

}