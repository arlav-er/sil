package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/*
 * Questa classe restituisce la query per la ricerca di un'azienda
 * quando il lavoratore si trova in mobilità
 * <br>25/09/2007 new revision 1.4: si estraggono tutti i movimenti del lavoratore non legati a mobilita'
 * 
 * @author: Stefania Orioli
 * 
 * @author: modificata da Davide Giuliani
 */

public class RicAziendaPerMobilita implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicAziendaPerMobilita.class.getName());

	// 25/09/2007 query modificata
	private static final String SELECT_SQL_BASE = "SELECT " + "AM_MOVIMENTO.PRGMOVIMENTO, "
			+ "AM_MOVIMENTO.PRGMOVIMENTOPREC, " + "AM_MOVIMENTO.PRGMOVIMENTOSUCC, " + "AM_MOVIMENTO.CDNLAVORATORE, "
			+ "AM_MOVIMENTO.CODMANSIONE, AM_MOVIMENTO.CODGRADO, AM_MOVIMENTO.NUMLIVELLO, "
			+ "AM_MOVIMENTO.CODORARIO, TO_CHAR(AM_MOVIMENTO.NUMORESETT) NUMORESETTIMANALI, "
			+ "AN_UNITA_AZIENDA.CODCCNL, CONTRATTO.STRDESCRIZIONE DESCCCNL, "
			+ "DE_MANSIONE.STRDESCRIZIONE as MANSIONE, "
			+ "to_char(AM_MOVIMENTO.DATFINEMOVEFFETTIVA,'DD/MM/YYYY') DATFINEMOVEFFETTIVA, "
			+ "to_char(AM_MOVIMENTO.DATINIZIOMOV,'DD/MM/YYYY') DATINIZIOMOV, "
			+ "DE_MV_CESSAZIONE.STRDESCRIZIONE as MOTCESSAZIONE, " + "an_unita_azienda.PRGAZIENDA, "
			+ "an_unita_azienda.PRGUNITA, " + "InitCap(AN_AZIENDA.STRRAGIONESOCIALE) as strRagioneSociale, "
			+ "AN_AZIENDA.STRPARTITAIVA, " + "AN_AZIENDA.STRCODICEFISCALE, " + "AN_UNITA_AZIENDA.STRTEL, "
			+ "AN_UNITA_AZIENDA.STRINDIRIZZO, " + "DE_COMUNE.CODCOM, " + "AM_MOVIMENTO.CODTIPOMOV ,"
			+ "AM_MOVIMENTO.CODMVCESSAZIONE ," + "am_movimento.codmonotempo, "
			+ "AM_MOVIMENTO.CODTIPOCONTRATTO CODTIPOASS ," + "InitCap(DE_COMUNE.STRDENOMINAZIONE) as comune_az "
			+ "FROM AM_MOVIMENTO, DE_MV_CESSAZIONE, DE_MANSIONE, DE_CONTRATTO_COLLETTIVO CONTRATTO, "
			+ "     AN_UNITA_AZIENDA, AN_AZIENDA, DE_COMUNE, AM_MOBILITA_ISCR "// ,
																				// AM_MOVIMENTO
																				// MOV1
																				// "
			+ "WHERE "
			// "(AM_MOVIMENTO.CODTIPOMOV <> 'CES') "
			// +"AND (AM_MOVIMENTO.DATFINEMOVEFFETTIVA IS NOT NULL) "
			// +" (AM_MOVIMENTO.CODMONOTEMPO = 'I') "
			+ " (AM_MOVIMENTO.CODSTATOATTO = 'PR') " + "AND (AM_MOVIMENTO.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA) "
			+ "AND (AM_MOVIMENTO.PRGUNITA = AN_UNITA_AZIENDA.PRGUNITA) "
			+ "AND (AN_UNITA_AZIENDA.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA) "
			+ "AND (AN_UNITA_AZIENDA.CODCCNL = CONTRATTO.CODCCNL(+)) "
			+ "AND (AN_UNITA_AZIENDA.CODCOM = DE_COMUNE.CODCOM) "
			// +"AND (AM_MOVIMENTO.CODMONOTIPOFINE = 'C') "
			// +"AND (AM_MOVIMENTO.PRGMOVIMENTOSUCC IS NOT NULL) "
			// +"AND (AM_MOVIMENTO.PRGMOVIMENTOSUCC = MOV1.PRGMOVIMENTO) "
			// +"AND (MOV1.CODMVCESSAZIONE NOT IN ('S1','S2','S3')) "
			// +"AND (MOV1.CODMVCESSAZIONE = DE_MV_CESSAZIONE.CODMVCESSAZIONE
			// (+)) "
			+ " and am_movimento.CODMVCESSAZIONE = de_mv_cessazione.CODMVCESSAZIONE (+)"
			+ "AND (AM_MOVIMENTO.CODMANSIONE = DE_MANSIONE.CODMANSIONE (+) ) "
			+ "AND (AM_MOVIMENTO.PRGMOVIMENTO = AM_MOBILITA_ISCR.PRGMOVIMENTO (+) ) "
			+ "AND (AM_MOBILITA_ISCR.PRGMOBILITAISCR IS NULL) ";

	/*
	 * 25/09/2007 questa parte di query non serve piu', in quanto la classe del modulo provvede a trovare nella lista
	 * dei movimenti le cessazioni associabili alla mobilita'
	 * 
	 * private static final String SELECT_SQL_UNION ="SELECT " +"AM_MOVIMENTO.PRGMOVIMENTO PRGMOVIMENTO, "
	 * +"AM_MOVIMENTO.CDNLAVORATORE, " +"AM_MOVIMENTO.CODMANSIONE, AM_MOVIMENTO.CODGRADO, AM_MOVIMENTO.NUMLIVELLO,
	 * " +"AN_UNITA_AZIENDA.CODCCNL, CONTRATTO.STRDESCRIZIONE DESCCCNL, " +"DE_MANSIONE.STRDESCRIZIONE as MANSIONE,
	 * " +"to_char(AM_MOVIMENTO.DATINIZIOMOV,'DD/MM/YYYY') DATFINEMOVEFFETTIVA, " +"to_char(nvl(MOV1.DATINIZIOAVV,
	 * MOV1.DATINIZIOMOV),'DD/MM/YYYY') DATINIZIOMOV, " +"DE_MV_CESSAZIONE.STRDESCRIZIONE as MOTCESSAZIONE, "
	 * +"an_unita_azienda.PRGAZIENDA, " +"an_unita_azienda.PRGUNITA, "
	 * +"InitCap(AN_AZIENDA.STRRAGIONESOCIALE) as strRagioneSociale, " +"AN_AZIENDA.STRPARTITAIVA, "
	 * +"AN_AZIENDA.STRCODICEFISCALE, " +"AN_UNITA_AZIENDA.STRTEL, " +"AN_UNITA_AZIENDA.STRINDIRIZZO, "
	 * +"DE_COMUNE.CODCOM, " +"InitCap(DE_COMUNE.STRDENOMINAZIONE) as comune_az " +"FROM AM_MOVIMENTO, AM_MOVIMENTO
	 * MOV1, DE_MV_CESSAZIONE, DE_MANSIONE, DE_CONTRATTO_COLLETTIVO CONTRATTO, " +" AN_UNITA_AZIENDA, AN_AZIENDA,
	 * DE_COMUNE, AM_MOBILITA_ISCR " +"WHERE (AM_MOVIMENTO.CODTIPOMOV <> 'CES') "
	 * +"AND (AM_MOVIMENTO.CODSTATOATTO = 'PR') " +"AND (AM_MOVIMENTO.CODTIPOASS = 'RS3') " +"AND
	 * (AM_MOVIMENTO.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA) "
	 * +"AND (AM_MOVIMENTO.PRGUNITA = AN_UNITA_AZIENDA.PRGUNITA) " +"AND (AN_UNITA_AZIENDA.PRGAZIENDA =
	 * AN_AZIENDA.PRGAZIENDA) " +"AND (AN_UNITA_AZIENDA.CODCCNL = CONTRATTO.CODCCNL(+)) " +"AND (AN_UNITA_AZIENDA.CODCOM
	 * = DE_COMUNE.CODCOM) " +"AND (AM_MOVIMENTO.CODMANSIONE = DE_MANSIONE.CODMANSIONE) " +"AND
	 * (AM_MOVIMENTO.PRGMOVIMENTO = AM_MOBILITA_ISCR.PRGMOVIMENTO (+) ) " +"AND (AM_MOBILITA_ISCR.PRGMOBILITAISCR IS
	 * NULL) " +"AND (MOV1.CODMVCESSAZIONE = DE_MV_CESSAZIONE.CODMVCESSAZIONE (+)) " +"and ( MOV1.prgmovimento = (SELECT
	 * MAX(prgmovimento) " +"FROM AM_MOVIMENTO MOV2 WHERE MOV2.PRGAZIENDA = AM_MOVIMENTO.PRGAZIENDA " +"AND
	 * MOV2.PRGUNITA = AM_MOVIMENTO.PRGUNITA AND MOV2.CODMONOTEMPO = 'I' " +"AND MOV2.CODTIPOMOV = 'CES' AND
	 * MOV2.CODMVCESSAZIONE IN ('S1', 'S2', 'S3') " +"AND MOV2.CODSTATOATTO = 'PR' " +"AND MOV2.CDNLAVORATORE =
	 * AM_MOVIMENTO.CDNLAVORATORE " +"AND TRUNC(NVL(MOV2.DATINIZIOAVV, MOV2.DATINIZIOMOV)) <
	 * TRUNC(AM_MOVIMENTO.DATINIZIOMOV)) ) ";
	 * 
	 */

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");
		// String prgAzienda = (String) req.getAttribute("PRGAZIENDA");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		query_totale.append("AND (AM_MOVIMENTO.CDNLAVORATORE = ");
		query_totale.append(cdnLavoratore);
		query_totale.append(")");

		/*
		 * 25/09/2007 commentato
		 * 
		 * 
		 * if (prgAzienda != null) { query_totale.append("AND (AM_MOVIMENTO.PRGAZIENDA = ");
		 * query_totale.append(prgAzienda); query_totale.append(")"); }
		 * 
		 * 
		 * 
		 * query_totale.append(" UNION " + SELECT_SQL_UNION); query_totale.append("AND (AM_MOVIMENTO.CDNLAVORATORE = ");
		 * query_totale.append(cdnLavoratore); query_totale.append(")"); if (prgAzienda != null) {
		 * query_totale.append("AND (AM_MOVIMENTO.PRGAZIENDA = "); query_totale.append(prgAzienda);
		 * query_totale.append(")"); }
		 */
		query_totale.append(" ORDER BY AM_MOVIMENTO.datiniziomov asc, AM_MOVIMENTO.prgmovimento asc");

		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}// RicAziendaPerMobilita
