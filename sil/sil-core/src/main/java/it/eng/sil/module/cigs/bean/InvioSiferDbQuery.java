/**
 * 
 */
package it.eng.sil.module.cigs.bean;

/**
 * 
 * @author User1
 * 
 */
public class InvioSiferDbQuery {
	static final String GET_CDN_LAVORATORE = "" + "SELECT lav.cdnlavoratore " + "FROM   an_lavoratore lav "
			+ "WHERE  lav.strcodicefiscale = ?";

	static final String GET_ESAME_CONGIUNTO = "SELECT   strCognomeRappresCong," + "         strNomeRappresCong,"
			+ "         strCognomeOrgDator," + "         strNomeOrgDator," + "         strMotivoRichiesta,"
			+ "         TO_CHAR ( datrichiestadal, 'DD/MM/YYYY' ) AS datRichiestaDal,"
			+ "         TO_CHAR ( datrichiestaal, 'DD/MM/YYYY' ) AS datRichiestaAl," + "         numMesi,"
			+ "         numLavoratori," + "         flgEsameCongAnno1," + "         flgEsameCongAnno2,"
			+ "         flgEsameCongAnno3," + "         flgEsameCongAnno4," + "         flgEsameCongAnno5,"
			+ "         flgSospAnno1," + "         flgSospAnno2," + "         flgSospAnno3," + "         flgSospAnno4,"
			+ "         flgSospAnno5" + "  FROM   ci_esame_congiunto" + " WHERE   prgaccordo = ?";

	// Sistemare cassa_integrazione__tipo - rilassato il vincolo con left join
	static final String GET_INFO_AZIENDA_BY_CDNLAVORATORE = "SELECT    aai.prgaltraiscr, az.strcodicefiscale AS azienda__codice_fiscale, az.strragionesociale AS azienda__ragione_sociale, uaz.codcom AS azienda__codice_catastale, uaz.strindirizzo AS azienda__indirizzo, uaz.strcap AS azienda__cap, (SELECT am.codtipocontratto "
			+ "          FROM    am_movimento am " + "          WHERE   am.cdnlavoratore = aai.cdnlavoratore "
			+ "          AND     am.prgazienda    = aai.prgazienda " + "          AND     am.codstatoatto = 'PR'"
			+ "          AND     datiniziomov IN (SELECT MAX (datiniziomov) "
			+ "                  FROM    am_movimento am2 "
			+ "                  WHERE   am2.cdnlavoratore = aai.cdnlavoratore "
			+ "                  AND     am2.prgazienda    = aai.prgazienda) "
			+ "          AND     ROWNUM                    = 1) AS tipo_contratto, "
			+ "          decode(aai.codtipoiscr, 'P', 'S', 'Q', 'S', aai.codtipoiscr) AS cassa_integrazione__tipo, "
			+ "          TO_CHAR (aai.datinizio, 'dd/mm/yyyy') AS cassa_integr__data_inizio, TO_CHAR (aai.datfine, 'dd/mm/yyyy') AS cassa_integrazione__data_fine "
			+ "FROM      am_altra_iscr aai " + "LEFT JOIN an_azienda az        ON az.prgazienda  = aai.prgazienda "
			+ "LEFT JOIN an_unita_azienda uaz ON aai.prgazienda = uaz.prgazienda AND       aai.prgunita = uaz.prgunita "
			+ "WHERE     aai.codtipoiscr IN ('O', 'S', 'G', 'M', 'P', 'Q') " + "AND       aai.cdnlavoratore = ?";

	static final String GET_LAV_PARTECIPANTE_BY_CDNLAVORATORE = "SELECT lav.cdnlavoratore, "
			+ "       lav.strcodicefiscale                  AS codice_fiscale, "
			+ "       NULL                                  AS codice_fiscale_vecchio, "
			+ "       lav.strcognome                        AS cognome, "
			+ "       lav.strnome                           AS nome, "
			+ "       lav.strsesso                          AS sesso, "
			+ "       TO_CHAR ( lav.datnasc, 'dd/mm/yyyy' ) AS nascita__data, "
			+ "       lav.codcomnas                         AS nascita__codice_catastale, "
			+ "       lav.codcittadinanza                 AS cittadinanza , "
			+ "       lav.codcittadinanza2                AS cittadinanza_seconda , "
			+ "       CASE               WHEN " + "                     ( SELECT cl.strcell "
			+ "                             FROM    ci_lavoratore cl "
			+ "                             WHERE   cl.cdnlavoratore = lav.cdnlavoratore "
			+ "                             AND     ROWNUM           = 1 " + "                     ) "
			+ "                     IS NULL " + "              THEN lav.strcell "
			+ "              ELSE ( SELECT cl.strcell " + "                     FROM    ci_lavoratore cl "
			+ "                     WHERE   cl.cdnlavoratore = lav.cdnlavoratore "
			+ "                     AND     ROWNUM           = 1 ) "
			+ "       END                 AS telefono_cellulare, " + "       lav.stremail        AS email, "
			+ "       lav.codcomres       AS residenza__codice_catastale, "
			+ "       lav.strindirizzores AS residenza__indirizzo, " + "       lav.strcapres       AS residenza__cap, "
			+ "       lav.strtelres       AS residenza__telefono, "
			+ "       lav.codcomdom       AS domicilio__codice_catastale, "
			+ "       lav.strindirizzodom AS domicilio__indirizzo, " + "       lav.strcapdom       AS domicilio__cap, "
			+ "       lav.strteldom       AS domicilio__telefono, ( SELECT ps.codtitolo "
			+ "       FROM    pr_studio ps " + "       WHERE   ps.cdnlavoratore = lav.cdnlavoratore "
			+ "       AND     ps.flgprincipale = 'S' " + "       AND     ROWNUM           = 1 ) AS titolo_studio "
			+ "FROM   an_lavoratore lav " + "WHERE  lav.cdnlavoratore = ?";

	public static final String GET_INFO_ACCORDO_BY_CDNLAVORATORE = "SELECT ca.prgaccordo, "
			+ "       aai.prgaltraiscr, " + "       to_char(opc.dateffettiva,'dd/MM/yyyy' ) AS presa_in_carico__data, "
			+ "       oc.codcpi AS presa_in_carico__cpi_sportello, " + "       CASE "
			+ "              WHEN oc.codmotivochiusuraper='TP' "
			+ "                     THEN TO_CHAR( oc.datchiusuraper,'dd/MM/yyyy' ) " + "              ELSE NULL "
			+ "       END AS presa_in_carico__fine, " + "       NULL AS politica_attiva__data_ritiro, " + "       CASE "
			+ "              WHEN oc.codmotivochiusuraper='DE' "
			+ "                     THEN TO_CHAR( oc.datchiusuraper,'dd/MM/yyyy' ) " + "              ELSE NULL "
			+ "       END AS data_decadenza, " + "       CASE " + "              WHEN oc.codmotivochiusuraper='IS' "
			+ "                     THEN TO_CHAR( oc.datchiusuraper,'dd/MM/yyyy' ) " + "              ELSE NULL "
			+ "       END AS data_rientro_lavoro, " + "       ca.codaccordo AS accordo_crisi__codice, "
			+ "       ca.codaccordoorig AS accordo_crisi__codice_prec, "
			+ "       DECODE( ca.codaccordo,NULL,'S',NULL ) AS accordo_crisi__codice_temp, " + "       ( "
			+ "              SELECT TO_CHAR( m.datiniziomov,'dd/mm/yyyy' ) " + "              FROM   am_movimento m "
			+ "              WHERE  aai.cdnlavoratore=m.cdnlavoratore "
			+ "              AND    aai.prgazienda=m.prgazienda " + "              AND    aai.prgunita=m.prgunita "
			+ "              AND    m.codtipomov='CES' " + "              AND    m.codmvcessazione<>'SC' "
			+ "              AND    m.datiniziomov>aai.datinizio " + "              AND    m.codstatoatto='PR' "
			+ "              AND    ROWNUM=1 ) AS accordo_crisi__data_cessazione, " + "       ( "
			+ "              SELECT m.codmvcessazione " + "              FROM   am_movimento m "
			+ "              WHERE  aai.cdnlavoratore=m.cdnlavoratore "
			+ "              AND    aai.prgazienda=m.prgazienda " + "              AND    aai.prgunita=m.prgunita "
			+ "              AND    m.codtipomov='CES' " + "              AND    m.codmvcessazione<>'SC' "
			+ "              AND    m.datiniziomov>aai.datinizio " + "              AND    m.codstatoatto='PR' "
			+ "              AND    ROWNUM=1 ) AS accordo_crisi__motivo_cess, "
			+ "       TO_CHAR( ca.datconcessione,'dd/mm/yyyy' ) AS accordo_crisi__data_autoriz, " + "       CASE "
			+ "              WHEN( " + "                            SELECT COUNT(* ) "
			+ "                            FROM   ci_lav_accordo lacc "
			+ "                            WHERE  ca.datconcessione IS NOT NULL "
			+ "                            AND    lacc.prglavoratore=cl.prglavoratore "
			+ "                            AND    lacc.prgaccordo=ca.prgaccordo "
			+ "                            AND    ca.codtipoconcessione IN( 'S','P' ) "
			+ "                            AND    lacc.flgaccolto='S' ) " + "                     >0 "
			+ "                     THEN 'S' " + "              WHEN(( ca.codtipoconcessione IN( 'N' ) "
			+ "                            OR     ca.codstatoatto IN( 'AN' ) ) "
			+ "                     AND    NOT EXISTS " + "                            ( "
			+ "                                   SELECT 1 "
			+ "                                   FROM   ci_accordo ca2 "
			+ "                                   WHERE  ca2.codaccordoorig=ca.codaccordo ) ) "
			+ "                     THEN 'N' " + "              ELSE NULL "
			+ "       END AS accordo_crisi__autorizzato, " + "       oc.codlineaaz AS linea_azione, " + "       CASE "
			+ "              WHEN ca.codstatoatto IN( 'AN' ) " + "                     THEN 'S' "
			+ "              ELSE NULL " + "       END AS annullamento_accordo " + " FROM   am_altra_iscr aai "
			+ "       LEFT JOIN ci_accordo ca              ON ca.prgaccordo=aai.prgaccordo "
			+ "       LEFT JOIN ci_lavoratore cl           ON cl.prgaccordo=ca.prgaccordo AND    aai.cdnlavoratore=cl.cdnlavoratore "
			+ "       LEFT JOIN an_azienda az              ON az.prgazienda=aai.prgazienda "
			+ "       LEFT JOIN an_unita_azienda uaz       ON uaz.prgazienda=az.prgazienda AND    uaz.prgunita=aai.prgunita "
			+ "       LEFT JOIN or_colloquio oc            ON oc.prgaltraiscr=aai.prgaltraiscr "
			+ "       LEFT JOIN or_percorso_concordato opc ON opc.prgcolloquio=oc.prgcolloquio AND opc.prgazioni=151 AND opc.codesitorendicont = 'E' "
			+ " WHERE  aai.cdnlavoratore=? ";

	public static final String GET_INFO_OPERAZIONE_BY_CDNLAVORATORE = "SELECT cc.prgcorsoci, "
			+ "       aai.prgaltraiscr, " + "       cco.codrifpa AS rif_pa_operazione, "
			+ "       cco.codsede  AS ente_sede__id, " + "       CASE "
			+ "              WHEN cc.dtmcancellazione IS NULL " + "              THEN 'N' " + "              ELSE 'S' "
			+ "       END AS annullamento_operazione " + "FROM   ci_corso_orienter cco "
			+ "       INNER JOIN ci_corso cc " + "       ON     cc.prgcorsoci = cco.prgcorsoci "
			+ "       INNER JOIN am_altra_iscr aai " + "       ON     cc.prgaltraiscr = aai.prgaltraiscr "
			+ "WHERE  aai.cdnlavoratore      = ?";

	public static final String GET_INFO_PROPOSTA_CATALOGO_BY_CDNLAVORATORE = "SELECT cc.prgcorsoci, "
			+ "       aai.prgaltraiscr, " + "       ccc.numidproposta        AS id_proposta_catalogo, "
			+ "       ccc.flgitalianostranieri AS italiano_per_stranieri, "
			+ "       dcs.strcodsede           AS ente_sede__id, " + "       CASE "
			+ "              WHEN cc.dtmcancellazione IS NULL " + "              THEN 'N' " + "              ELSE 'S' "
			+ "       END AS annullamento_proposta_catalogo " + "FROM   ci_corso_catalogo ccc "
			+ "       INNER JOIN ci_corso cc " + "       ON     cc.prgcorsoci = ccc.prgcorsoci "
			+ "       INNER JOIN de_catalogo_sede dcs " + "       ON     dcs.numidproposta = ccc.numidproposta "
			+ "       AND    dcs.numrecid      = ccc.numrecid " + "       INNER JOIN am_altra_iscr aai "
			+ "       ON     cc.prgaltraiscr = aai.prgaltraiscr " + "WHERE  aai.cdnlavoratore      = ?";

	public static final String GET_LIST_PERIODI_BY_CDNLAVORATORE = "SELECT aai.prgaltraiscr, "
			+ "       ca.prgaccordo, " + "       TO_CHAR ( cla.datiniziocigs, 'dd/MM/yyyy' ) AS datinizio, "
			+ "       TO_CHAR ( cla.datfinecigs, 'dd/MM/yyyy' )   AS datfine, " + "       cla.numoreptsett, "
			+ "       cla.numoreftsett, " + "       cla.numtotggcigs, " + "       cla.numtotorecigs "
			+ "FROM   am_altra_iscr aai " + "       INNER JOIN an_lavoratore l "
			+ "       ON     l.cdnlavoratore = aai.cdnlavoratore " + "       INNER JOIN ci_accordo ca "
			+ "       ON     ca.prgaccordo = aai.prgaccordo " + "       INNER JOIN ci_lavoratore cl "
			+ "       ON     cl.prgaccordo    = ca.prgaccordo " + "       AND    cl.cdnlavoratore = aai.cdnlavoratore "
			+ "       INNER JOIN ci_lav_accordo cla " + "       ON     cla.prgaccordo    = ca.prgaccordo "
			+ "       AND    cla.prglavoratore = cl.prglavoratore "
			+ " WHERE  aai.codtipoiscr IN ('O', 'S', 'G', 'M', 'P', 'Q') " + " AND    aai.cdnlavoratore = ?";

	public static final String GET_LIST_SERVIZI_BY_CDNLAVORATORE = "SELECT oc.prgaltraiscr, "
			+ "       sc.codservizicig                                                   AS tipo, "
			+ "       sc.numminutistandard                                               AS durata, "
			+ "       opc.flgmediatore                                                   AS mediatore, "
			+ "       opc.flgabilita                                                     AS disabile, "
			+ "		  opc.codesitorendicont                                              AS esito,"
			+ "       TO_CHAR ( NVL ( opc.dateffettiva, opc.datstimata ), 'dd/MM/yyyy' ) AS data "
			+ "FROM   or_percorso_concordato opc " + "       INNER JOIN or_colloquio oc "
			+ "       ON     oc.prgcolloquio = opc.prgcolloquio " + "       INNER JOIN de_esito es "
			+ "       ON     es.codesito = opc.codesito " + "       INNER JOIN de_servizicig sc "
			+ "       ON     sc.codservizicig     = opc.codservizicig " + " WHERE  NVL ( sc.codservizicig, 0 ) > 0 "
			+ " AND    opc.codesitorendicont != 'ENR' " + " AND    oc.cdnlavoratore            = ?";

	// query per ottenere il codice fiscale accorpato in caso di CAMBIO CODICE
	// FISCALE
	public static final String GET_CF_ACCORPATO_X_CAMBIO = "SELECT strcodicefiscaleaccorpato codice_fiscale_vecchio "
			+ " FROM     an_lavoratore_accorpa " + " WHERE    cdnlavoratore = cdnlavoratoreaccorpato "
			+ " AND      rownum        = 1 " + " AND      cdnlavoratore = ? " + " ORDER BY dtmins DESC";

	// query per ottenere i codici fiscali accorpati in caso di ACCORPAMENTO
	public static final String GET_CF_ACCORPATI_X_ACCORPAMENTO = " SELECT strcodicefiscaleaccorpato codice_fiscale_accorpato, "
			+ " to_char(dtmins,'yyyy-mm-dd') data_accorpamento " + " FROM     an_lavoratore_accorpa "
			+ " WHERE    cdnlavoratore  = ? " + " ORDER BY dtmins desc ";
}
