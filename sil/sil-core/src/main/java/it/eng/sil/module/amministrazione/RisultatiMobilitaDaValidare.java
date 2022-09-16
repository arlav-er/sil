package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

public class RisultatiMobilitaDaValidare implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RisultatiMobilitaDaValidare.class.getName());
	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " MOB.STRCODICEFISCALE || '<br>' || MOB.STRCOGNOME || ' ' || MOB.STRNOME lavoratore,"
			+ " MOB.STRCODICEFISCALE strCodiceFiscaleLav," + " MOB.STRCOGNOME COGNOME," + " MOB.STRNOME NOME,"
			+ " MOB.CODUACOM CODCOMUNE," + " MOB.STRAZCODICEFISCALE strCodiceFiscale,"
			+ " MOB.STRAZRAGIONESOCIALE strRagioneSociale," + " MOB.STRUAINDIRIZZO strIndirizzoUA,"
			+ " MOB.PRGMOBILITAISCRAPP," + " to_char(MOB.DATINIZIO,'DD/MM/YYYY') DATINIZIO,"
			+ " to_char(MOB.DATFINE  ,'DD/MM/YYYY') DATFINE," + " to_char(MOB.DATMAXDIFF ,'DD/MM/YYYY') DATMAXDIFF,"
			+ " MOB.CODTIPOMOB," + " to_char(MOB.DATCRT ,'DD/MM/YYYY') DATCRT," + " case "
			+ " when ma.codlistespec is not null "
			+ " then (DE_MB_TIPO.STRDESCRIZIONE || ' (Min: ' || mn.des_listespec|| ')' ) "
			+ " else DE_MB_TIPO.STRDESCRIZIONE || ' (Min: nessuna corrispondenza)' " + " end as STRDESCRIZIONEMOB";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();
		DynamicStatementUtils dsu = new DynamicStatementUtils();

		String nome = (String) req.getAttribute("nome");
		String cognome = (String) req.getAttribute("cognome");
		String cf = (String) req.getAttribute("codiceFiscaleLavoratore");
		String codTipoMob = (String) req.getAttribute("CodTipoLista");
		String datInizioDa = (String) req.getAttribute("DATINIZIODA");
		String datInizioA = (String) req.getAttribute("DATINIZIOA");
		String datFineDa = (String) req.getAttribute("DATFINEDA");
		String datFineA = (String) req.getAttribute("DATFINEA");
		String datCRTDa = (String) req.getAttribute("DATCRTDA");
		String datCRTA = (String) req.getAttribute("DATCRTA");
		String datMaxDiffDa = (String) req.getAttribute("DATMAXDIFFDA");
		String datMaxDiffA = (String) req.getAttribute("DATMAXDIFFA");
		String ragsocaz = (String) req.getAttribute("ragioneSociale");
		String piva = (String) req.getAttribute("pIva");
		String cfazienda = (String) req.getAttribute("codFiscaleAzienda");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "codCpi");
		String numeroDelReg = (String) req.getAttribute("NumDelReg");
		String codMobRespinte = StringUtils.getAttributeStrNotNull(req, "FlgMobRespinte");

		dsu.addSelect(SELECT_SQL_BASE);
		dsu.addFrom("AM_MOBILITA_ISCR_APP MOB");
		dsu.addFrom("DE_MB_TIPO");
		dsu.addFrom("ma_listespeciali ma");
		dsu.addFrom("mn_listespeciali mn");

		dsu.addWhere("MOB.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO");
		dsu.addWhere("DE_MB_TIPO.codmbtipo = ma.codmbtipo");
		dsu.addWhere("ma.codlistespec= mn.cod_listespec (+)");

		if (!codCpi.equals("")) {
			dsu.addWhere("MOB.codCpi = '" + codCpi + "' ");
		}

		if ((nome != null) && (!nome.equals("")))
			dsu.addWhereIfFilledStrLikeUpper("MOB.STRNOME", nome, DynamicStatementUtils.DO_LIKE_CONTIENE);
		if ((cognome != null) && (!cognome.equals("")))
			dsu.addWhereIfFilledStrLikeUpper("MOB.STRCOGNOME", cognome, DynamicStatementUtils.DO_LIKE_CONTIENE);
		if ((cf != null) && (!cf.equals("")))
			dsu.addWhereIfFilledStrLikeUpper("MOB.STRCODICEFISCALE", cf, DynamicStatementUtils.DO_LIKE_CONTIENE);
		if ((ragsocaz != null) && (!ragsocaz.equals("")))
			dsu.addWhereIfFilledStrLikeUpper("MOB.STRAZRAGIONESOCIALE", ragsocaz,
					DynamicStatementUtils.DO_LIKE_CONTIENE);
		if ((piva != null) && (!piva.equals("")))
			dsu.addWhereIfFilledStrLikeUpper("MOB.STRAZPARTITAIVA", piva, DynamicStatementUtils.DO_LIKE_CONTIENE);
		if ((cfazienda != null) && (!cfazienda.equals("")))
			dsu.addWhereIfFilledStrLikeUpper("MOB.STRAZCODICEFISCALE", cfazienda,
					DynamicStatementUtils.DO_LIKE_CONTIENE);

		dsu.addWhereIfFilledStr("MOB.CODMOTIVOFINE", codMobRespinte);
		// tipo mobilità
		dsu.addWhereIfFilledStr("MOB.CODTIPOMOB", codTipoMob);
		dsu.addWhereIfFilledDateBetween("MOB.DATINIZIO", datInizioDa, datInizioA);
		dsu.addWhereIfFilledDateBetween("MOB.DATFINE", datFineDa, datFineA);
		dsu.addWhereIfFilledDateBetween("MOB.DATMAXDIFF", datMaxDiffDa, datMaxDiffA);
		dsu.addWhereIfFilledDateBetween("MOB.DATCRT", datCRTDa, datCRTA);
		if ((numeroDelReg != null) && (!numeroDelReg.equals("")))
			dsu.addWhereIfFilledStrLikeUpper("MOB.STRNUMATTO", numeroDelReg, DynamicStatementUtils.DO_LIKE_CONTIENE);
		dsu.addOrder("COGNOME");
		dsu.addOrder("NOME");
		dsu.addOrder("DATINIZIO");

		String query = dsu.getStatement();
		_logger.debug(className + ".getStatement() FINE, con query=" + query);

		return query;
	}
}