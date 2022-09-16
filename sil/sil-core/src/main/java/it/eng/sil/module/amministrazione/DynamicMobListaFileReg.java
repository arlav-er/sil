/*
 * Creato il 4-gen-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author riccardi
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;

public class DynamicMobListaFileReg implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " mob.strcodicefiscale || '<br>' || mob.strcognomenome as CFcognomenome,"
			+ " mob.strazragionesociale," + " mob.codtipomob, " + " mob.prgMobIscrDaEnteBk, " + " case "
			+ " when ma.codlistespec is not null "
			+ " then (mbt.STRDESCRIZIONE || ' (Min: ' || mn.des_listespec|| ')' ) "
			+ " else mbt.STRDESCRIZIONE || ' (Min: nessuna corrispondenza)' " + " end as strtipomob, "
			+ " to_char(mob.datinizio, 'dd/mm/yyyy') as strdatinizio, "
			+ " to_char(mob.datfine, 'dd/mm/yyyy') as strdatfine, "
			+ " to_char(mob.datmaxdiff, 'dd/mm/yyyy') as strdatmaxdiff, " + " mob.codcpi, "
			+ " cpi.strdescrizione as strcpi, " + " mob.strnumatto, "
			+ " to_char(mob.datCRT, 'dd/mm/yyyy') as strdatcrt, " + " mob.codentedetermina ";

	private static final String FROM_SQL_BASE = "   am_mob_iscr_daente_bk mob,"
			+ "   de_mb_tipo mbt, ma_listespeciali ma, mn_listespeciali mn, " + "   de_cpi cpi ";
	private static final String WHERE_SQL_BASE = "  mob.codtipomob = mbt.codmbtipo " + "  AND mob.codcpi = cpi.codcpi "
			+ " AND mbt.codmbtipo = ma.codmbtipo " + " AND ma.codlistespec= mn.cod_listespec (+) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		SourceBean req = requestContainer.getServiceRequest();

		String strCodiceFiscale = (String) req.getAttribute("strCodiceFiscale");
		String strCognomeNome = (String) req.getAttribute("strCognomeNome");
		String CodCPI = (String) req.getAttribute("CodCPI");
		String CodProvincia = (String) req.getAttribute("CodProvincia");

		String strAzRagioneSociale = (String) req.getAttribute("strAzRagioneSociale");

		String CodMbTipo = (String) req.getAttribute("CodMbTipo");
		String datInizioDa = (String) req.getAttribute("datInizioDa");
		String datInizioA = (String) req.getAttribute("datInizioA");
		String datFineDa = (String) req.getAttribute("datFineDa");
		String datFineA = (String) req.getAttribute("datFineA");
		String strNumAtto = (String) req.getAttribute("strNumAtto");
		String datCRT = (String) req.getAttribute("datCRT");
		String codEnteDetermina = (String) req.getAttribute("codEnteDetermina");

		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(SELECT_SQL_BASE);
		dsu.addFrom(FROM_SQL_BASE);
		dsu.addWhere(WHERE_SQL_BASE);

		dsu.addWhereIfFilledStrLikeUpper("mob.strcodicefiscale", strCodiceFiscale,
				DynamicStatementUtils.DO_LIKE_INIZIA);
		dsu.addWhereIfFilledStrLikeUpper("mob.strcognomenome", strCognomeNome, DynamicStatementUtils.DO_LIKE_INIZIA);
		dsu.addWhereIfFilledStr("mob.codCpi", CodCPI);
		if (CodProvincia != null && !CodProvincia.equals("")) {
			dsu.addWhere("mob.codCpi in (select codcpi from de_cpi where codprovincia = '" + CodProvincia + "') ");
		}
		dsu.addWhereIfFilledStrLikeUpper("mob.strAzRagioneSociale", strAzRagioneSociale,
				DynamicStatementUtils.DO_LIKE_INIZIA);

		dsu.addWhereIfFilledStr("mob.codTipoMob", CodMbTipo);
		dsu.addWhereIfFilledDateBetween("mob.datInizio", datInizioDa, datInizioA);
		dsu.addWhereIfFilledDateBetween("mob.datFine", datFineDa, datFineA);

		dsu.addWhereIfFilledStrLikeUpper("mob.strNumAtto", strNumAtto, DynamicStatementUtils.DO_LIKE_no);
		if (datCRT != null && !datCRT.equals("")) {
			dsu.addWhere("trunc(mob.datCRT) = TO_DATE('" + datCRT + "','DD/MM/YYYY')");
		}
		dsu.addWhereIfFilledStrLikeUpper("mob.codentedetermina", codEnteDetermina, DynamicStatementUtils.DO_LIKE_no);

		dsu.addOrder("mob.strcognomenome");
		dsu.addOrder("mob.strcodicefiscale");

		String query = dsu.getStatement();
		return query;
	}
}