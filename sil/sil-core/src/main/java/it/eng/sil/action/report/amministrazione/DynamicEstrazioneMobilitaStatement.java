package it.eng.sil.action.report.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider2;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * @author Coppola
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynamicEstrazioneMobilitaStatement implements IDynamicStatementProvider2 {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicEstrazioneMobilitaStatement.class.getName());
	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " LAV.STRCOGNOME COGNOME," + " LAV.STRNOME NOME,"
			+ " LAV.STRCODICEFISCALE CF," + " MOB.PRGMOBILITAISCR," + " MOB.CDNLAVORATORE,"
			+ " to_char(MOB.DATINIZIO,'DD/MM/YYYY') DATINIZIO," + " to_char(MOB.DATFINE  ,'DD/MM/YYYY') DATFINE,"
			+ " to_char(MOB.DATMAXDIFF ,'DD/MM/YYYY') DATMAXDIFF," + " MOB.CODTIPOMOB,"
			+ " to_char(MOB.DATCRT ,'DD/MM/YYYY') DATCRT," + " case " + " when ma.codlistespec != '0' "
			+ " then (DE_MB_TIPO.STRDESCRIZIONE || ' (Min: ' || mn.des_listespec|| ')' ) "
			+ " else DE_MB_TIPO.STRDESCRIZIONE " + " end as STRDESCRIZIONEMOB";

	public String getStatement(SourceBean req, SourceBean config) {
		_logger.debug(className + ".getStatement() INIZIO");

		DynamicStatementUtils dsu = new DynamicStatementUtils();

		String nome = (String) req.getAttribute("nome");
		String cognome = (String) req.getAttribute("cognome");
		String cf = (String) req.getAttribute("CF");
		String codTipoMob = (String) req.getAttribute("CodTipoLista");
		String datInizioDa = (String) req.getAttribute("DATINIZIODA");
		String datInizioA = (String) req.getAttribute("DATINIZIOA");
		String datFineDa = (String) req.getAttribute("DATFINEDA");
		String datFineA = (String) req.getAttribute("DATFINEA");
		String datCRTDa = (String) req.getAttribute("DATCRTDA");
		String datCRTA = (String) req.getAttribute("DATCRTA");
		String datMaxDiffDa = (String) req.getAttribute("DATMAXDIFFDA");
		String datMaxDiffA = (String) req.getAttribute("DATMAXDIFFA");

		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "codCpi");

		dsu.addSelect(SELECT_SQL_BASE);
		dsu.addFrom("AN_LAVORATORE LAV");
		dsu.addFrom("AM_MOBILITA_ISCR MOB");
		dsu.addFrom("DE_MB_TIPO");
		dsu.addFrom("ma_listespeciali ma");
		dsu.addFrom("mn_listespeciali mn");

		dsu.addWhere("MOB.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO");
		dsu.addWhere("MOB.CDNLAVORATORE = LAV.CDNLAVORATORE");
		dsu.addWhere("DE_MB_TIPO.codmbtipo = ma.codmbtipo");
		dsu.addWhere("ma.codlistespec= mn.cod_listespec (+)");

		if (!codCpi.equals("")) {
			dsu.addFrom("an_lav_storia_inf inf");
			// commentate il 16/05/2005
			// dsu.addFrom("an_lav_storia_inf_coll coll");
			// dsu.addFrom("am_elenco_anagrafico ea");

			dsu.addWhere("inf.cdnlavoratore = LAV.CDNLAVORATORE");
			// commentate il 16/05/2005
			// dsu.addWhere("inf.prgLavStoriaInf = coll.prgLavStoriaInf");
			// dsu.addWhere("coll.codlsttab = 'EA'");
			// dsu.addWhere("coll.strChiaveTabella = ea.prgElencoAnagrafico");

			dsu.addWhere("DECODE(inf.DATFINE,NULL,'S','N')='S'");
			// dsu.addWhere("nvl(inf.datFine,
			// to_date('01/01/2100','dd/mm/yyyy')) =
			// to_date('01/01/2100','dd/mm/yyyy') ");
			dsu.addWhere("inf.codCpiTit = '" + codCpi + "' ");
			dsu.addWhere("inf.codMonoTipoCpi = 'C' ");
		}

		if (tipoRic.equalsIgnoreCase("esatta")) {
			dsu.addWhereIfFilledStrUpper("LAV.STRNOME", nome);
			dsu.addWhereIfFilledStrUpper("LAV.STRCOGNOME", cognome);
			dsu.addWhereIfFilledStrUpper("LAV.STRCODICEFISCALE", cf);
		} else {
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRNOME", nome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRCOGNOME", cognome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRCODICEFISCALE", cf, DynamicStatementUtils.DO_LIKE_INIZIA);
		} // else

		// tipo mobilità
		dsu.addWhereIfFilledStr("MOB.CODTIPOMOB", codTipoMob);
		dsu.addWhereIfFilledDateBetween("MOB.DATINIZIO", datInizioDa, datInizioA);
		dsu.addWhereIfFilledDateBetween("MOB.DATFINE", datFineDa, datFineA);
		dsu.addWhereIfFilledDateBetween("MOB.DATMAXDIFF", datMaxDiffDa, datMaxDiffA);
		dsu.addWhereIfFilledDateBetween("MOB.DATCRT", datCRTDa, datCRTA);

		dsu.addOrder("COGNOME");
		dsu.addOrder("NOME");
		dsu.addOrder("DATINIZIO");

		String query = dsu.getStatement();
		_logger.debug(className + ".getStatement() FINE, con query=" + query);

		return query;
	}
}
