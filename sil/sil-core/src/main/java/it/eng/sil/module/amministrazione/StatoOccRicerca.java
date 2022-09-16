package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * Questa classe restituisce la select per la ricerca dello stato occupazionale
 * 
 * @author Togna Cosimo 21/04/05
 * 
 */
public class StatoOccRicerca implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StatoOccRicerca.class.getName());
	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " an_lavoratore.strcognome cognome, an_lavoratore.strnome nome, an_lavoratore.strcodicefiscale cf, "
			+ "am_stato_occupaz.prgstatooccupaz, am_stato_occupaz.cdnlavoratore, am_stato_occupaz.codstatooccupaz, "
			+ "pg_utils.trunc_desc (de_stato_occupaz.strdescrizione, 22, '...' ) AS descrizionestato, "
			+ "TO_CHAR (am_stato_occupaz.datinizio, 'DD/MM/YYYY') datinizio, "
			+ "DECODE (am_stato_occupaz.datricorsogiurisdiz, NULL, DECODE (am_stato_occupaz.datrichrevisione, "
			+ "NULL, NULL, 'Revisione<BR/>' || TO_CHAR (am_stato_occupaz.datrichrevisione, 'DD/MM/YYYY')), 'Ric.' "
			+ "|| TO_CHAR (am_stato_occupaz.datricorsogiurisdiz, 'DD/MM/YYYY')) rev_ric ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		DynamicStatementUtils dynamicStatementUt = new DynamicStatementUtils();
		SourceBean request = requestContainer.getServiceRequest();

		// Recupero i dati dalla request
		String nome = (String) request.getAttribute("nome");
		String cognome = (String) request.getAttribute("cognome");
		String cf = (String) request.getAttribute("CF");
		String statoOcc = (String) request.getAttribute("codStatoOcc");
		// String legge407 = (String) request.getAttribute("legge407_90");
		// String lungaDur = (String) request.getAttribute("lungaDur");
		String revRic = (String) request.getAttribute("REVRIC");
		// Savino 30/09/05: la data dalla quale i soggetti non si sono
		// presentati sara' valorizzata solo
		// se did richiesta<>'S' e soggetti che non ... ='S'
		String dallaData = (String) request.getAttribute("dataNP");
		String soggettiNonPresent = (String) request.getAttribute("viewNonPresentati");
		String numAppNonPresentaz = (String) request.getAttribute("numVol");
		String tipoRic = StringUtils.getAttributeStrNotNull(request, "tipoRicerca");
		String codCpi = StringUtils.getAttributeStrNotNull(request, "codCpi");

		dynamicStatementUt.addSelect(SELECT_SQL_BASE);
		dynamicStatementUt.addFrom("an_lavoratore");
		dynamicStatementUt.addFrom("am_stato_occupaz");
		dynamicStatementUt.addFrom("de_stato_occupaz");
		dynamicStatementUt.addFrom("de_stato_occupaz_ragg");

		dynamicStatementUt.addWhere(
				"NVL (am_stato_occupaz.datfine, TO_DATE ('01/01/1900', 'DD/MM/YYYY')) = TO_DATE ('01/01/1900', 'DD/MM/YYYY')");
		dynamicStatementUt.addWhere("am_stato_occupaz.codstatooccupaz = de_stato_occupaz.codstatooccupaz");
		dynamicStatementUt.addWhere("de_stato_occupaz.codstatooccupazragg =de_stato_occupaz_ragg.codstatooccupazragg");
		dynamicStatementUt.addWhere("am_stato_occupaz.cdnlavoratore = an_lavoratore.cdnlavoratore");
		dynamicStatementUt.addWhere(
				"NVL (inf.datfine, TO_DATE ('01/01/1900', 'DD/MM/YYYY')) =TO_DATE ('01/01/1900', 'DD/MM/YYYY')");

		// Ricerca del Cpi
		if (!codCpi.equals("")) {
			dynamicStatementUt.addFrom("an_lav_storia_inf inf");
			dynamicStatementUt.addWhere("inf.cdnlavoratore = an_lavoratore.cdnlavoratore");
			dynamicStatementUt.addWhere(
					"NVL (inf.datfine, TO_DATE ('01/01/1900', 'DD/MM/YYYY')) =TO_DATE ('01/01/1900', 'DD/MM/YYYY')");
			dynamicStatementUt.addWhere("inf.codCpiTit = '" + codCpi + "' ");
			dynamicStatementUt.addWhere("inf.codMonoTipoCpi = 'C'");
		}

		// Soggetti convocati che non si sono presentati
		if (soggettiNonPresent.equals("S")) {
			String queryApp = " (" + "select COUNT(*) numAppNoPresentaz "
					+ "from AG_LAVORATORE agl,AG_AGENDA agg, DE_STATO_APPUNTAMENTO deap "
					+ "where agl.cdnlavoratore=AN_LAVORATORE.CDNLAVORATORE "
					+ "and agl.prgappuntamento=agg.prgappuntamento "
					+ "and agg.codstatoappuntamento=deap.codstatoappuntamento " + "and deap.flgattivo='S' "
					+ "and agg.prgtipoprenotazione=3 " + "and agg.codesitoappunt='NLA' ";
			if (dallaData.equals("")) {
				queryApp += "and trunc(agg.dtmdataora) >= (select amd.datDichiarazione from AM_DICH_DISPONIBILITA amd, "
						+ "am_elenco_anagrafico amel "
						+ "where nvl(amd.datfine,TO_DATE ('01/01/1900', 'DD/MM/YYYY')) = "
						+ "      TO_DATE ('01/01/1900', 'DD/MM/YYYY') "
						+ "and amel.prgelencoanagrafico = amd.prgelencoanagrafico " + "and amd.datFine is null "
						+ "and amd.codStatoAtto = 'PR' " + "and amel.datCan is null "
						+ "and amel.cdnlavoratore=agl.cdnlavoratore)) >=" + numAppNonPresentaz;
			} else {
				queryApp += "and trunc(agg.dtmdataora) >= to_date('" + dallaData + "','DD/MM/YYYY')) >="
						+ numAppNonPresentaz;
			}
			dynamicStatementUt.addWhere(queryApp);
		}

		// ricerca esatta
		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrUpper("strnome", nome.toUpperCase());
			}
			if ((cognome != null) && (!cognome.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrUpper("strcognome", cognome.toUpperCase());
			}
			if ((cf != null) && (!cf.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrUpper("strCodiceFiscale", cf.toUpperCase());
			}
			// ricereca inizia per
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("strnome", nome.toUpperCase(),
						DynamicStatementUtils.DO_LIKE_INIZIA);
			}
			if ((cognome != null) && (!cognome.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("strcognome", cognome.toUpperCase(),
						DynamicStatementUtils.DO_LIKE_INIZIA);
			}
			if ((cf != null) && (!cf.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("strCodiceFiscale", cf.toUpperCase(),
						DynamicStatementUtils.DO_LIKE_INIZIA);
			}
		}

		// Stato occupazionale
		if ((statoOcc != null) && (!statoOcc.equals(""))) {
			dynamicStatementUt.addWhere("AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ = '" + statoOcc + "'");
		}

		/*
		 * Commento aggiunto per il congelamento della versione in fase 3 Cosimo Togna 28/04/2005 // Soggetti alla legge
		 * 407/90 if ((legge407 != null) && (legge407.equals("S"))) {
		 * dynamicStatementUt.addWhere("NVL(TRUNC(MONTHS_BETWEEN(sysdate,AM_STATO_OCCUPAZ.DATANZIANITADISOC)),0) >=
		 * 24"); } else if ((legge407 != null) && (legge407.equals("N"))) {
		 * dynamicStatementUt.addWhere("NVL(TRUNC(MONTHS_BETWEEN(sysdate,AM_STATO_OCCUPAZ.DATANZIANITADISOC)),0) < 24");
		 * } // Disoccupato/inoccupato di lunga durata if ((lungaDur != null) && (lungaDur.equals("S"))) {
		 * dynamicStatementUt.addWhere("(( NVL(TRUNC(MONTHS_BETWEEN(sysdate,AM_STATO_OCCUPAZ.DATANZIANITADISOC)),0) > 12
		 * ) OR (( NVL(TRUNC(MONTHS_BETWEEN(sysdate,AM_STATO_OCCUPAZ.DATANZIANITADISOC)),0) > 6 ) AND
		 * AM_STATO_OCCUPAZ.CODCATEGORIA181 = 'G' ))"); } else if ((lungaDur != null) && (lungaDur.equals("N"))) {
		 * dynamicStatementUt.addWhere("(( NVL(TRUNC(MONTHS_BETWEEN(sysdate,AM_STATO_OCCUPAZ.DATANZIANITADISOC)),0) < 6
		 * ) OR ( ( NVL(TRUNC(MONTHS_BETWEEN(sysdate,AM_STATO_OCCUPAZ.DATANZIANITADISOC)),0) < 12 ) AND
		 * AM_STATO_OCCUPAZ.CODCATEGORIA181 != 'G' ))"); }
		 * 
		 */

		// Revisione / Ricorso
		if ((revRic != null) && (revRic.equals("S"))) {
			dynamicStatementUt.addWhere(
					"(nvl(am_stato_occupaz.datrichrevisione,TO_DATE ('01/01/1900', 'DD/MM/YYYY'))= am_stato_occupaz.datrichrevisione OR (nvl(am_stato_occupaz.datricorsogiurisdiz,TO_DATE ('01/01/1900', 'DD/MM/YYYY'))=am_stato_occupaz.datricorsogiurisdiz))");
		} else if ((revRic != null) && (revRic.equals("N"))) {
			dynamicStatementUt.addWhere(
					"(nvl(am_stato_occupaz.datrichrevisione,TO_DATE ('01/01/1900', 'DD/MM/YYYY'))= TO_DATE ('01/01/1900', 'DD/MM/YYYY') OR (nvl(am_stato_occupaz.datricorsogiurisdiz,TO_DATE ('01/01/1900', 'DD/MM/YYYY'))=TO_DATE ('01/01/1900', 'DD/MM/YYYY'))) )) ");
		}

		dynamicStatementUt.addOrder("COGNOME");
		dynamicStatementUt.addOrder("NOME");
		dynamicStatementUt.addOrder("DATINIZIO");

		// Debug commentato
		// TracerSingleton.log("M_InfStorStatoOcc",TracerSingleton.DEBUG,className
		// + "::Stringa di ricerca:"+ dynamicStatementUt.getStatement());

		return (dynamicStatementUt.getStatement());

	}

} // class StatoOccRicerca
