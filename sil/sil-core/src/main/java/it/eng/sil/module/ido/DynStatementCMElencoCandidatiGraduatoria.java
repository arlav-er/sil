package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynStatementCMElencoCandidatiGraduatoria implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynStatementCMElencoCandidatiGraduatoria.class.getName());

	public DynStatementCMElencoCandidatiGraduatoria() {
	}

	private String className = this.getClass().getName();

	private static final String SELECT_SQL = "select r.PRGNOMINATIVO,   " + "r.CDNLAVORATORE,   " + "r.numordine,   "
			+ "rich1.prgrichiestaaz,  " + "do_rosa.PRGINCROCIO,  " + "DO_EVASIONE.CDNSTATORICH,   "
			+ "do_incrocio.prgtipoincrocio, 	  " + "ti.strdescrizione as descr_incr,  "
			+ "tr.strdescrizione as descr_rosa,	  " + "rich1.datchiamata,  " + "rich1.datchiamatacm,  "
			+ "rich1.datpubblicazione,  " + "Initcap(an.STRCOGNOME) || ' ' || Initcap(an.STRNOME) as strCognomeNome,  "
			+ "provraz.strtarga || an.cdnlavoratore as codiceAdesione, "
			+ "to_char(an.DATNASC, 'dd/mm/yyyy') as DatNasc,   " + "r.numpunteggio as punteggio,   "
			+ "r.numpunteggiopres as punteggiopres,  " + "r.NUMANNOPUNTART1 as annoPunteggioArt1, " + "r.strpriorita,  "
			+ "to_char(r.DATISCRART1, 'dd/mm/yyyy') as datIscrAlboArt1,  "
			+ " decode(an.STRINDIRIZZODOM,null,' ',Initcap(an.STRINDIRIZZODOM) || ' ') || Initcap(dom.STRDENOMINAZIONE) || '(' || pr.STRTARGA || ')' as Domicilio,   ";

	private static final String SELECT_SQL_ISCR_CM = " case "
			+ "  when (select codmonotipogradcm from ts_generale) = 4 " + "    then  "
			+ "           (select v.numvaloreisee " + " from AS_VALORE_ISEE v "
			+ " where v.cdnlavoratore = r.CDNLAVORATORE " + " and v.datinizioval <= rich1.datchiamatacm "
			+ " and v.datinizioval >= add_months(rich1.datchiamatacm, -6) "
			+ " and (v.datfineval >= rich1.datchiamatacm " + " or v.datfineval is null) ) " + "    else "
			+ "   	(select red.numreddito  " + " 	from cm_lav_reddito red   "
			+ "  	where red.cdnlavoratore = r.CDNLAVORATORE  "
			+ " 	and ((DECODE(red.datfineval,NULL,'S','N') = 'S') or red.datfineval >= sysdate)  "
			+ " 	and red.numanno = rich1.numannoredditocm ) " + "   end as reddito, " + "(select car.numpersone  "
			+ "from cm_lav_carico car    " + "where car.cdnlavoratore = r.CDNLAVORATORE)  " + "as carico,"
			+ " DQ.STRDESCRIZIONE as QUALIFICATO, ";

	// SELECT_SQL_ISCR_CM_2a e SELECT_SQL_ISCR_CM_3a n erano utilizzati
	private static final String SELECT_SQL_ISCR_CM_2a = " , (select to_char(i.datdatainizio, 'dd/mm/yyyy') "
			+ "from am_cm_iscr i  " + "inner join de_cm_tipo_iscr t on t.codcmtipoiscr = i.codcmtipoiscr   "
			+ "where DECRYPT(i.cdnlavoratore, '";

	private static final String SELECT_SQL_ISCR_CM_3a = "' ) = r.CDNLAVORATORE  "
			+ "and DECODE(i.DATDATAFINE,NULL,'S','N') = 'S' "
			+ "and t.codmonotiporagg = DECODE(do_incrocio.prgtipoincrocio, 10, 'D', 11, 'A')  "
			+ "AND I.CODSTATOATTO = 'PR')    " + "as dataIscrCM, ";

	// SELECT_SQL_ISCR_CM_4a invece di SELECT_SQL_ISCR_CM_2b e SELECT_SQL_ISCR_CM_3b
	private static final String SELECT_SQL_ISCR_CM_4a = " (to_char(i.datanzianita68, 'dd/mm/yyyy') || '</br>' || "
			+ " to_char(i.datdatainizio, 'dd/mm/yyyy'))   as dateCM, ";

	private static final String SELECT_SQL_ISCR_CM_2b = " , (select to_char(i.datanzianita68, 'dd/mm/yyyy') || '</br>' || to_char(i.datdatainizio, 'dd/mm/yyyy') "
			+ "from am_cm_iscr i  " + "inner join de_cm_tipo_iscr t on t.codcmtipoiscr = i.codcmtipoiscr   "
			+ "where DECRYPT(i.cdnlavoratore, '";

	private static final String SELECT_SQL_ISCR_CM_3b = "' ) = r.CDNLAVORATORE  "
			+ "and DECODE(i.DATDATAFINE,NULL,'S','N') = 'S' "
			+ "and t.codmonotiporagg = DECODE(do_incrocio.prgtipoincrocio, 10, 'D', 11, 'A')  "
			+ "AND I.CODSTATOATTO = 'PR')    " + "as dateCM,  ";

	private static final String SELECT_SQL_ISCR_ART_1 = "to_char(ia.datiscrlistaprov, 'dd/mm/yyyy')   as dataAnzArt1, "
			+ "to_char(ia.datiscralbo, 'dd/mm/yyyy') as dataIscrArt1,  "
			+ "ia.numannopunteggio  as numannopunteggioArt1, " + "ia.numpunteggio numpunteggioArt1,  "
			+ "(select   pc.strdenominazione  from CM_ISCR_ART1 cia	"
			+ "inner join an_lav_storia_inf ai on (cia.cdnLavoratore=ai.cdnLavoratore and ai.datFine is null)	"
			+ "left join de_cpi cpi on (ai.codCpiTit=cpi.codCpi)   "
			+ "left join de_cpi cpiO on (ai.codCpiOrig=cpiO.codCpi) "
			+ "left join de_provincia pc on (cpi.CODPROVINCIA=pc.codProvincia)	"
			+ "where cia.cdnlavoratore = r.CDNLAVORATORE	"
			+ "and cia.prgiscrart1 = ia.prgiscrart1) as descrProvCpiComp,	" + "(SELECT  "
			+ "tab.codTipoMov || ' - ' || TO_CHAR (tab.datiniziomov, 'dd/mm/yyyy')  "
			+ "FROM am_movimento tab, de_contratto con  " + "WHERE tab.cdnlavoratore = r.CDNLAVORATORE  "
			+ "AND tab.codstatoatto = 'PR'  " + "AND (       (   (    tab.datfinemovEffettiva IS NULL  "
			+ "AND tab.datiniziomov =  " + "(SELECT MAX (tab1.datiniziomov)  " + "FROM am_movimento tab1  "
			+ "WHERE tab1.datfinemovEffettiva IS NULL  " + "AND tab1.cdnlavoratore = tab.cdnlavoratore  "
			+ "AND tab1.codstatoatto = 'PR'  " + "AND tab1.codtipomov <> 'CES')  " + ")  " + "OR (    NOT EXISTS (  "
			+ "SELECT tab1.datiniziomov  " + "FROM am_movimento tab1  " + "WHERE tab1.datfinemovEffettiva IS NULL  "
			+ "AND tab1.cdnlavoratore = tab.cdnlavoratore  " + "AND tab1.codstatoatto = 'PR'  "
			+ "AND tab1.codtipomov <> 'CES')  " + "AND tab.datfinemovEffettiva =  "
			+ "(SELECT MAX (tab1.datfinemovEffettiva)  " + "FROM am_movimento tab1  "
			+ "WHERE NOT tab1.datfinemovEffettiva IS NULL  " + "AND tab1.cdnlavoratore = tab.cdnlavoratore  "
			+ "AND tab1.codstatoatto = 'PR'  " + "AND tab1.codtipomov <> 'CES')  " + ")  " + ")  "
			+ "AND tab.codtipomov <> 'CES'  " + "OR (    NOT EXISTS (  " + "SELECT tab1.datiniziomov  "
			+ "FROM am_movimento tab1  " + "WHERE tab1.cdnlavoratore = tab.cdnlavoratore  "
			+ "AND tab1.codstatoatto = 'PR'  " + "AND tab1.codtipomov <> 'CES')  " + "AND tab.datiniziomov =  "
			+ "(SELECT MAX (tab1.datiniziomov)  " + "FROM am_movimento tab1  "
			+ "WHERE tab1.cdnlavoratore = tab.cdnlavoratore  " + "AND tab1.codstatoatto = 'PR'  "
			+ "AND tab1.codtipomov = 'CES')  " + "AND tab.codtipomov = 'CES'  " + ")  " + ")  "
			+ "AND tab.codcontratto = con.codcontratto(+)  " + " AND ROWNUM = 1) as ultMov ";

	private static final String FROM_SQL_ISCR_CM = " from do_nominativo r   "
			+ " inner join an_lavoratore an on (r.CDNLAVORATORE=an.CDNLAVORATORE)   "
			+ " inner join do_rosa on (r.PRGROSA=do_rosa.PRGROSA)   "
			+ " inner join de_comune dom on (an.CODCOMDOM=dom.CODCOM)   "
			+ " inner join de_provincia pr on (dom.CODPROVINCIA=pr.CODPROVINCIA)   "
			+ " left outer join de_cpi on (r.CODCPITIT=de_cpi.CODCPI)   "
			+ " inner join do_incrocio on (do_rosa.PRGINCROCIO=do_incrocio.PRGINCROCIO)   "
			+ " inner join do_richiesta_az rich1 on (do_incrocio.PRGRICHIESTAAZ=rich1.PRGRICHIESTAAZ)   "
			+ " inner join de_tipo_incrocio ti on ti.prgtipoincrocio = do_incrocio.prgtipoincrocio  "
			+ " inner join de_tipo_rosa tr on tr.prgtiporosa = do_rosa.prgtiporosa  "
			+ " inner join de_cpi cpiraz on (rich1.codcpi = cpiraz.codcpi) "
			+ " LEFT OUTER JOIN de_comune comraz on (cpiraz.codcom = comraz.codcom) "
			+ " LEFT OUTER JOIN de_provincia provraz on (comraz.codprovincia = provraz.codprovincia) "
			+ " LEFT OUTER JOIN DO_EVASIONE ON (rich1.PRGRICHIESTAAZ=DO_EVASIONE.PRGRICHIESTAAZ)  "
			+ " LEFT OUTER JOIN DE_EVASIONE_RICH ON (DO_EVASIONE.CODEVASIONE=DE_EVASIONE_RICH.CODEVASIONE)"
			+ " LEFT OUTER JOIN DE_QUALIFICATO DQ ON (DQ.CDNQUALIFICATO=r.CDNQUALIFICATO) ";

	private static final String FROM_SQL_ISCR_ART_1 = " from do_nominativo r   "
			+ " inner join an_lavoratore an on (r.CDNLAVORATORE=an.CDNLAVORATORE)   "
			+ " left outer join cm_iscr_art1 ia on ia.cdnlavoratore = r.cdnlavoratore	  "
			+ " inner join am_documento_coll dc on dc.strchiavetabella = ia.prgiscrart1 "
			+ " inner join am_documento ad on ad.prgdocumento = dc.prgdocumento "
			+ " inner join do_rosa on (r.PRGROSA=do_rosa.PRGROSA)   "
			+ " inner join de_comune dom on (an.CODCOMDOM=dom.CODCOM)   "
			+ " inner join de_provincia pr on (dom.CODPROVINCIA=pr.CODPROVINCIA)   "
			+ " left outer join de_cpi on (r.CODCPITIT=de_cpi.CODCPI)   "
			+ " inner join do_incrocio on (do_rosa.PRGINCROCIO=do_incrocio.PRGINCROCIO)   "
			+ " inner join do_richiesta_az rich1 on (do_incrocio.PRGRICHIESTAAZ=rich1.PRGRICHIESTAAZ)   "
			+ " inner join de_tipo_incrocio ti on ti.prgtipoincrocio = do_incrocio.prgtipoincrocio  "
			+ " inner join de_tipo_rosa tr on tr.prgtiporosa = do_rosa.prgtiporosa  "
			+ " inner join de_cpi cpiraz on (rich1.codcpi = cpiraz.codcpi) "
			+ " LEFT OUTER JOIN de_comune comraz on (cpiraz.codcom = comraz.codcom) "
			+ " LEFT OUTER JOIN de_provincia provraz on (comraz.codprovincia = provraz.codprovincia) "
			+ " LEFT OUTER JOIN DO_EVASIONE ON (rich1.PRGRICHIESTAAZ=DO_EVASIONE.PRGRICHIESTAAZ)  "
			+ " LEFT OUTER JOIN DE_EVASIONE_RICH ON (DO_EVASIONE.CODEVASIONE=DE_EVASIONE_RICH.CODEVASIONE) "
			+ " LEFT OUTER JOIN DE_QUALIFICATO DQ ON (DQ.CDNQUALIFICATO=r.CDNQUALIFICATO) ";

	private static final String WHERE_SQL = " where r.prgrosa = ";

	private static final String WHERE_SQL_2 = " and r.CODTIPOCANC is null ";

	private static final String WHERE_SQL_ISCR_ART_1 = " and ia.datfine is null  AND ad.codtipodocumento = 'ILS' "
			+ " AND ad.CODSTATOATTO != 'AN' " + " and ia.codtipolista = rich1.codtipolista ";

	// Ottimizzazione Query - Donisi & Donato
	private static final String WHERE_SQL_3 = "  and (i.codcmtipoiscr in (select t.codcmtipoiscr from de_cm_tipo_iscr t where t.codmonotiporagg =  DECODE(do_incrocio.prgtipoincrocio, 10, 'D', 11, 'A'))  "
			+ "  or i.codcmtipoiscr is null)  ";

	private static final String ORDER_SQL_BASE = " order by numordine ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		String prgRosa = StringUtils.getAttributeStrNotNull(req, "PRGROSA");

		String prgTipoIncrocio = StringUtils.getAttributeStrNotNull(req, "PRGTIPOINCROCIO");

		/*
		 * old String SELECT_SQL_WARNING = " CASE  " + "	WHEN nvl(r.numpunteggiopres,0) <> nvl(r.numpunteggio,0) " +
		 * "		 THEN '<IMG name=\"image\" border=\"0\" src=\"../../img/warning_trasp.gif\" alt=\"Punteggi diversi\" title=\"Punteggi diversi\" />' "
		 * + "	WHEN nvl(r.codcmannota,0) <> nvl((select i.codcmannota " + " from am_cm_iscr i  " +
		 * " inner join de_cm_tipo_iscr t on t.codcmtipoiscr = i.codcmtipoiscr   " +
		 * " where DECRYPT(i.cdnlavoratore,'"+encryptKey+"') = r.CDNLAVORATORE " +
		 * " and DECODE(i.DATDATAFINE,NULL,'S','N') = 'S' " +
		 * " and t.codmonotiporagg = DECODE(do_incrocio.prgtipoincrocio, 10, 'D', 11, 'A')  " +
		 * " AND I.CODSTATOATTO = 'PR'),0) " +
		 * "		 THEN '<IMG name=\"image\" border=\"0\" src=\"../../img/warning_rosso_trasp.gif\" alt=\"Annota fuori lista diverso\" title=\"Annota fuori lista diverso\" />' "
		 * + "	ELSE '' " + "	END  as checkPunteggio ";
		 */

		String SELECT_SQL_WARNING = " CASE " + " WHEN nvl(r.numpunteggiopres, 0) <> nvl(r.numpunteggio, 0) THEN "
				+ " '<IMG name=\"image\" border=\"0\" src=\"../../img/warning_trasp.gif\" alt=\"Punteggi diversi\" title=\"Punteggi diversi\" />' "
				+ " WHEN nvl(r.codcmannota, 0) <>   nvl(i.codcmannota, 0) then "
				+ "  '<IMG name=\"image\" border=\"0\" src=\"../../img/warning_rosso_trasp.gif\" alt=\"Annota fuori lista diverso\" title=\"Annota fuori lista diverso\" />' "
				+ "	ELSE '' " + "	END  as checkPunteggio ";

		String joinAm_Cm_Iscr = " left join am_cm_iscr i on (r.CDNLAVORATORE =  DECRYPT(i.cdnlavoratore,'";

		String joinAm_Cm_Iscr_2 = "' )) and  I.CODSTATOATTO = 'PR' and i. datdatafine  is null ";

		String buf = SELECT_SQL;

		// art 8/18
		if (("10").equalsIgnoreCase(prgTipoIncrocio) || ("11").equalsIgnoreCase(prgTipoIncrocio)) {

			buf += SELECT_SQL_ISCR_CM;

			// new
			buf += SELECT_SQL_ISCR_CM_4a;

			buf += SELECT_SQL_WARNING;
			buf += FROM_SQL_ISCR_CM;

			// new
			buf += joinAm_Cm_Iscr + encryptKey + joinAm_Cm_Iscr_2;

			buf += WHERE_SQL + prgRosa;
			buf += WHERE_SQL_2;

			// new
			buf += WHERE_SQL_3;
			buf += ORDER_SQL_BASE;

		} else {
			// art 1
			buf += SELECT_SQL_ISCR_ART_1;
			buf += FROM_SQL_ISCR_ART_1;
			buf += WHERE_SQL + prgRosa;
			buf += WHERE_SQL_2;
			buf += WHERE_SQL_ISCR_ART_1;
			buf += ORDER_SQL_BASE;
		}

		// TracerSingleton.log("DynStatementCMElencoCandidatiGraduatoria", TracerSingleton.DEBUG,//
		// "sil.module.ido.DynStatementCMElencoCandidatiGraduatoria" + "::Stringa di ricerca:" + buf);

		return (buf);
	}
}