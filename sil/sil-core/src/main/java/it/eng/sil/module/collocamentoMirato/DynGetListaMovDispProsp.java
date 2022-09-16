/*
 * Creato il 19-nov-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynGetListaMovDispProsp implements IDynamicStatementProvider {

	public DynGetListaMovDispProsp() {
	}

	private static final String SELECT_SQL_BASE1 =

			"SELECT prgmovimento, prgazienda, cdnlavoratore, " + "strcognome, strnome, strcodicefiscale, "
					+ "cognomenomelav, descrcontratto, datiniziomov, datInMovi, "
					+ "codmonocategoria, datUltimaMod, codmansione, "
					+ "codmonomovdich, prgmansione, strragionesociale, codfiscaleazienda, "
					+ "strpartitaiva, codnatgiuridica, codateco, codcom, codprovincia, "
					+ "strindirizzo, descrmansione, datfinemov, datFinMovi, indirizzocompletoaz, ";

	private static final String SELECT_SQL_BASE2 =

			"FROM " + "( " + "SELECT DISTINCT am.prgmovimento, am.prgazienda, am.cdnlavoratore, "
					+ "la.strcognome, la.strnome, la.strcodicefiscale, "
					+ "la.strCognome || ' ' || la.strNome cognomenomelav, " + "DECODE "
					+ "(pg_anagrafica_professionale_rp.getcodcontrattoforrp " + "(am.prgmovimento), " + "'LT', DECODE "
					+ "(pg_anagrafica_professionale_rp.getcodmonotempoforrp " + "(am.prgmovimento), "
					+ "'I', 'Lavoro dipendente TI', " + "'Lavoro dipendente TD' " + "), " + "'LP', DECODE "
					+ "(pg_anagrafica_professionale_rp.getcodmonotempoforrp " + "(am.prgmovimento), "
					+ "'I', 'Lavoro dipendente TI', " + "'Lavoro dipendente TD' " + "), " + "'LTP', DECODE "
					+ "(pg_anagrafica_professionale_rp.getcodmonotempoforrp " + "(am.prgmovimento), "
					+ "'I', 'Lavoro dipendente TI', " + "'Lavoro dipendente TD' " + "), "
					+ "(select strdescrizione from de_contratto where codcontratto = "
					+ "pg_anagrafica_professionale_rp.getcodcontrattoforrp(am.prgmovimento)) " + ") AS descrcontratto, "
					+ "nvl(TO_CHAR (pg_anagrafica_professionale_rp.GetDatInizioComputoForRp (am.prgmovimento), 'DD/MM/YYYY'), "
					+ "TO_CHAR (am.datiniziomov, 'DD/MM/YYYY')) as datiniziomov, "
					+ "nvl(pg_anagrafica_professionale_rp.GetDatInizioComputoForRp (am.prgmovimento), am.datiniziomov) as datInMovi, "
					+ "pg_anagrafica_professionale_rp.GetCategoriaDaNullaOstaForRp(am.cdnlavoratore,am.prgazienda,am.datiniziomov,30) as codmonocategoria, "
					+ "TO_CHAR (pg_anagrafica_professionale_rp.GetDtmModForRp(am.prgmovimento), 'DD/MM/YYYY') as datUltimaMod, "
					+ "am.codmansione, am.codmonomovdich, pm.prgmansione, "
					+ "an.strragionesociale, an.strcodicefiscale codfiscaleazienda, "
					+ "an.strpartitaiva, an.codnatgiuridica, " + "an_unita_azienda.codateco, an_unita_azienda.codcom, "
					+ "co.codprovincia, an_unita_azienda.strindirizzo, " + "dm.strdescrizione descrmansione, "
					+ "TO_CHAR (pg_anagrafica_professionale_rp.getdatafinemoveffforrp (am.prgmovimento), 'dd/mm/yyyy') datfinemov, "
					+ "pg_anagrafica_professionale_rp.getdatafinemoveffforrp (am.prgmovimento) as datFinMovi, "
					+ "an_unita_azienda.strindirizzo || ' ' || co.strdenominazione || '(' || prov.strtarga || ')' as indirizzocompletoaz "
					+ "FROM am_movimento am, " + "an_azienda an, " + "an_unita_azienda, " + "de_mansione dm, "
					+ "pr_mansione pm, " + "de_contratto dc, " + "de_comune co, " + "ts_generale, "
					+ "de_provincia prov, " + "an_lavoratore la " + "WHERE am.prgazienda = an.prgazienda "
					+ "AND am.prgunita = an_unita_azienda.prgunita "
					+ "AND an.prgazienda = an_unita_azienda.prgazienda " + "AND am.cdnlavoratore = pm.cdnlavoratore(+) "
					+ "AND am.codmansione = dm.codmansione " + "AND an_unita_azienda.codcom = co.codcom "
					+ "AND co.codprovincia = prov.codprovincia " + "AND am.cdnlavoratore = la.cdnlavoratore "
					+ "AND am.codtipomov = 'AVV' " + "AND am.codmansione = pm.codmansione(+) "
					+ "AND am.codcontratto = dc.codcontratto " + "AND NVL (dc.flgtirocini, 'N') != 'S' "
					+ "AND am.codstatoatto = 'PR' " + "AND (flginterasspropria = 'S' OR an.codtipoazienda <> 'INT') "
					+ "AND ( " + "am.flglegge68= 'S' or " + "am.codtipoass = 'NOH' or " + "am.codtipoass = 'NU2' or "
					+ "decode(pg_anagrafica_professionale_rp.GetCategoriaDaNullaOstaForRp(am.cdnlavoratore,am.prgazienda,am.datiniziomov,30), null, 'N', 'S') = 'S' or "
					+ "pg_anagrafica_professionale_rp.ExistsDatInizioComputoForRp(am.prgmovimento) = 'true' " + ") ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String prgAzienda = "";
		String numAnnoRifProsp = "";
		String codProvincia = "";
		String prgProspettoInf = "";

		StringBuffer query_parte1 = new StringBuffer(SELECT_SQL_BASE1);
		StringBuffer query_parte2 = new StringBuffer(SELECT_SQL_BASE2);
		StringBuffer bufInside = new StringBuffer();
		StringBuffer bufLast = new StringBuffer();

		prgProspettoInf = StringUtils.getAttributeStrNotNull(req, "PRGPROSPETTOINF");
		prgAzienda = StringUtils.getAttributeStrNotNull(req, "PRGAZIENDA");
		numAnnoRifProsp = StringUtils.getAttributeStrNotNull(req, "NUMANNORIFPROSPETTO");
		codProvincia = StringUtils.getAttributeStrNotNull(req, "CODPROVINCIA");

		bufInside.append("case " + "when pg_anagrafica_professionale_rp.gettipoorarioforrp(prgmovimento) = 'P' "
				+ "then pg_anagrafica_professionale_rp.getorelavorateforrp(prgmovimento) " + "else "
				+ "(select numoreccnl from cm_prospetto_inf where prgprospettoinf = " + prgProspettoInf + ") " + "end "
				+ "AS oreLavorate, " + "(select numoreccnl from cm_prospetto_inf where prgprospettoinf = "
				+ prgProspettoInf + ") as oretotali, " + "decode((select count(lr.prglavriserva) "
				+ "from cm_pi_lav_riserva lr "
				+ "inner join cm_prospetto_inf pi on pi.prgprospettoinf = lr.prgprospettoinf "
				+ "where lr.strcodicefiscalelav = strcodicefiscale " + "and lr.datiniziorapp = datInMovi "
				+ "and pi.prgprospettoinf = " + prgProspettoInf + "),0,'Inserimento','Modifica' ) as checkLavRiserva, "
				+ "decode((select count(lr.prglavriserva) " + "from cm_pi_lav_riserva lr "
				+ "inner join cm_prospetto_inf pi on pi.prgprospettoinf = lr.prgprospettoinf "
				+ "where lr.strcodicefiscalelav = strcodicefiscale " + "and lr.datiniziorapp = datInMovi "
				+ "and pi.prgprospettoinf = " + prgProspettoInf
				+ "),0, prgMovimento||'-I', prgMovimento||'-U') as PRG_CHECK ");

		if (!prgAzienda.equals("")) {
			bufLast.append(" and an.prgazienda = " + prgAzienda);
		}

		if (!codProvincia.equals("")) {
			bufLast.append(" and co.codprovincia = '" + codProvincia + "' ");
			bufLast.append(" )");
			//bufLast.append(" and (");
			//bufLast.append(
			//		" (nvl(ts_generale.flgpoloreg, 'N') = 'N' and co.codprovincia = '" + codProvincia + "') or ");
			//bufLast.append(
			//		" (nvl(ts_generale.flgpoloreg, 'N') = 'S' and prov.codregione = ts_generale.codregionesil) ");
			//bufLast.append(") )");
		}

		if (!numAnnoRifProsp.equals("")) {
			bufLast.append(" WHERE TO_NUMBER (TO_CHAR (datInMovi, 'YYYY')) <= " + numAnnoRifProsp);
			bufLast.append(" AND NVL (TO_NUMBER (TO_CHAR (datFinMovi, 'YYYY')), " + numAnnoRifProsp + ") >= "
					+ numAnnoRifProsp);
		}

		bufLast.append(" ORDER BY datInMovi DESC, datFinMovi DESC ");

		query_parte1.append(bufInside.toString());
		query_parte2.append(bufLast.toString());
		StringBuffer query_totale = query_parte1.append(query_parte2.toString());
		return query_totale.toString();

	}

}