package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/*
 * Crea lo statement per estrarre l'elenco dei candidati di una rosa ordinato
 * secondo i criteri predeterminati o secondo il parametro opzionale che
 * visualizza per primi i candidati la cui titolarità è nel master.
 * 
 * @author: Stefania Orioli
 */

public class DynStatementCandidatiRosa implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynStatementCandidatiRosa.class.getName());

	public DynStatementCandidatiRosa() {
	}

	private String className = this.getClass().getName();

	/*
	 * NOTA del 16/05/2005: i campi Reperib, ReperibCC, listeSpeciali, listeSpecialiCC, condizioni, condizioniCC
	 * risultavano inutili duplicazioni -> sono stati lasciati solamente i campi *CC per compatibilità con le stampe. La
	 * tag library standard è stata modificata per la corretta interpretazione di "\n". Inoltre è stato commentato il
	 * flagStessaAz che indica se il lavoratore è già stato segnalato alla medesima azienda. Le condizioni su DATFINE is
	 * null sono state sostituite con le funzioni nvl in modo da sfruttare le indicizzazioni create appositamente per il
	 * trattamento dei valori null.
	 */
	private static final String SELECT_SQL = "select "
			// + "/*+ "
			// + "Index(do_nominativo IX_DO_NOMINATIV_AN_LAVORATOR) "
			// + "*/ "
			+ "r.PRGNOMINATIVO, r.CDNLAVORATORE, " + "r.decIndiceVicinanza, do_rosa.PRGINCROCIO, " + "CASE  "
			+ "	WHEN pdl.codgradoocc = 'PER'  "
			+ "		 THEN '<IMG name=\"image\" border=\"0\" src=\"../../img/warning_trasp.gif\" alt=\"Grado di occupabilità: Percorsi di sostegno\" title=\"Grado di occupabilità: Percorsi di sostegno\" />' "
			+ "	ELSE '' " + "	END  as checkgradoocc, "
			+ "PG_STORIA_ROSA.ORDINEDIDROSA(r.CDNLAVORATORE) as ordineDid, "
			// Non utilizzati al momento
			// + "(nvl(to_number(to_char(r.DATDICHIARAZIONE, 'yyyymmdd')),0) -
			// nvl(to_number(to_char(st_rosa.dtmUltimaSegn,'yyyymmdd')),0) "
			// + ") as ordineSegn, "
			// + "st_rosa.nroSegnalazioni, to_char(st_rosa.dtmUltimaSegn,'dd/mm/yyyy') as dtmUltimaSegn, "
			// + "PG_STORIA_ROSA.NROESCLUSIONIROSA(r.CDNLAVORATORE) as OrdineEsclusioni, "
			+ "rich2.PRGRICHIESTAAZ AS PRGORIGINALE, " + "NVL(rich1.PRGSPI, 0) as PRGSPI, "
			+ "Initcap(an.STRCOGNOME) || ' ' || Initcap(an.STRNOME) as strCognomeNome, "
			+ "to_char(an.DATNASC, 'dd/mm/yy') as DatNasc, "
			// + "Initcap(dom.STRDENOMINAZIONE) as comuneDomicilio, "
			+ "decode(an.STRINDIRIZZODOM,null,'',Initcap(an.STRINDIRIZZODOM) || '\n') || Initcap(dom.STRDENOMINAZIONE) || '(' || pr.STRTARGA || ')' as Domicilio, "
			// + "de_stato_occupaz.STRDESCRIZIONE as statoOccupaz, "
			// savino 27/05/05 rimesso il campo codStatoOccupaz
			+ "occ.codStatoOccupaz," + "(decode(an.STRTELDOM, null, '', 'Tel. ' || an.STRTELDOM) || '\n' || "
			+ "decode(an.STRCELL, null, '', 'Cell. ' || an.STRCELL) || '\n' || "
			+ "decode(an.STREMAIL, null, '', 'E-mail ' || an.STREMAIL) " + ") as ReperibCC, "
			+ "an.STRTELDOM tel,  an.STRCELL cell, an.STREMAIL email, "
			+ "PG_STORIA_ROSA.CANDIDATOROSACONDIZIONICC(r.CDNLAVORATORE) as condizioniCC, "
			+ "PG_STORIA_ROSA.CANDIDATOROSALISTESPECIALICC(r.CDNLAVORATORE) as listeSpecialiCC, "
			+ "Initcap(DE_CPI.STRDESCRIZIONE) as cpiCompetenza, "
			+ "decode(de_disponibilita_rosa.CODDISPONIBILITAROSA,'D',de_disponibilita_rosa.STRDESCRIZIONE || '(' || do_disponibilita.NUMCONTANONRINTRACCIATO || ')',de_disponibilita_rosa.STRDESCRIZIONE) as disponibilita,"
			+ "decode(AM_PATTO_LAVORATORE.PRGPATTOLAVORATORE, null, '', to_char(AM_PATTO_LAVORATORE.DATSCADCONFERMA,'dd/mm/yy')) as patto297, "
			+ "to_char(uc.DataUltimoContatto, 'dd/mm/yy') as DataUltimoContatto, "
			+ "case when nvl(ev.FLGPUBBCRESCO, 'N') = 'S' "
			+ "then decode((select count(mov.cdnLavoratore) from am_movimento mov "
			+ "				where mov.CDNLAVORATORE = an.CDNLAVORATORE and mov.codContratto = 'LI' and mov.codStatoAtto = 'PR' and mov.codTipoMov = 'AVV' "
			+ "				and mov.datiniziomov <= nvl(rich2.datscadenzapubblicazione,sysdate) and rich2.datrichiesta <= nvl(PG_ANAGRAFICA_PROFESSIONALE_RP.GetDataFineMovEffForRp(mov.prgMovimento), sysdate)), 0, 'No', 'Sì') "
			+ "else '' " + "end as intermittenti, " + "case when nvl(ev.FLGPUBBCRESCO, 'N') = 'S' "
			+ "then decode((select count(st.cdnLavoratore) from am_stato_occupaz st "
			+ "				inner join de_stato_occupaz ds on st.CODSTATOOCCUPAZ = ds.CODSTATOOCCUPAZ "
			+ "				where st.CDNLAVORATORE = an.CDNLAVORATORE and (ds.CODSTATOOCCUPAZRAGG='D' or ds.CODSTATOOCCUPAZRAGG='I') and ds.CODSTATOOCCUPAZ <> 'B1' "
			+ "				and st.datinizio <= nvl(rich2.datscadenzapubblicazione,sysdate) and rich2.datrichiesta <= nvl(st.datfine, sysdate)), 0, 'No', 'Sì') "
			+ "else '' " + "end as disoccupati, " + "case when nvl(ev.FLGPUBBCRESCO, 'N') = 'S' "
			+ "then decode((select count(col.cdnLavoratore) from or_colloquio col "
			+ "				inner join or_percorso_concordato perc on (col.prgcolloquio=perc.prgcolloquio) "
			+ "				inner join ma_azione_tipoattivita ma on (perc.prgazioni=ma.prgazioni) "
			+ "				inner join de_azione on (perc.prgazioni=de_azione.prgazioni) "
			+ "				inner join de_azione_ragg ragg on (de_azione.prgazioneragg=ragg.prgazioniragg) "
			+ "				left join or_percorso_concordato percgg on (perc.prgcolloquioadesione=percgg.prgcolloquio and perc.prgpercorsoadesione=percgg.prgpercorso) "
			+ "				where col.cdnLavoratore = an.cdnLavoratore and ma.codtipoattivita='A02' and ragg.flg_misurayei='S' and perc.codesito='FC' "
			+ "				and nvl(perc.dateffettiva,perc.datstimata) between rich2.datrichiesta and nvl(rich2.datscadenzapubblicazione,sysdate)), 0, 'No', 'Sì') "
			+ "else '' " + "end as garanziagiovani, " + "case when nvl(ev.FLGPUBBCRESCO, 'N') = 'S' "
			+ "then decode((select count(patto.cdnLavoratore) from am_patto_lavoratore patto "
			+ "				where patto.cdnLavoratore = an.cdnLavoratore and "
			+ "	   		 	( (patto.codtipopatto IN ('MGO30','MGO45','MINAT') and patto.datstipula <= nvl(rich2.datscadenzapubblicazione,sysdate) AND rich2.datrichiesta <= nvl(patto.datfine,sysdate)) or "
			+ "                 (patto.codtipopatto = 'ANP' and getEsisteProgrammaAllaData(patto.prgpattolavoratore, '''MGO30'',''MGO45'',''MINAT''', rich2.datrichiesta) > 0) "
			+ "               ) " + "				and patto.codstatoatto = 'PR' " + "				), 0, 'No', 'Sì') "
			+ "else '' " + "end as pacchettoadulti, ";
	// + "(PG_STORIA_ROSA.flagStessaAz(r.CDNLAVORATORE, ";

	private static final String SELECT_SQL_2p = // "52)) as stessaAzienda, "
			"de_cpi.CODPROVINCIA, ts_generale.CODPROVINCIASIL, "
					+ "decode(de_cpi.CODPROVINCIA,ts_generale.CODPROVINCIASIL,1, decode(do_rosa.NUMORDPROVINCIA,1,0,1)) as OrdineAmm ";

	private static final String FROM_SQL = "from do_nominativo r "
			+ "inner join an_lavoratore an on (r.CDNLAVORATORE=an.CDNLAVORATORE) "
			+ "inner join do_rosa on (r.PRGROSA=do_rosa.PRGROSA) "
			+ "inner join de_comune dom on (an.CODCOMDOM=dom.CODCOM) "
			+ "inner join de_provincia pr on (dom.CODPROVINCIA=pr.CODPROVINCIA) "
			// + "left outer join am_stato_occupaz occ on (r.CDNLAVORATORE=occ.CDNLAVORATORE and occ.DATFINE is null) "
			+ "left outer join am_stato_occupaz occ on (r.CDNLAVORATORE=occ.CDNLAVORATORE "
			+ "and nvl(occ.DATFINE, TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))=TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')) "
			+ "left outer join pr_dispo_l68 pdl on (pdl.CDNLAVORATORE=an.CDNLAVORATORE) "
			// + "left outer join de_stato_occupaz on (occ.CODSTATOOCCUPAZ=de_stato_occupaz.CODSTATOOCCUPAZ) "
			+ "left outer join VW_AG_ULTIMO_CONTATTO uc on (r.CDNLAVORATORE=uc.CDNLAVORATORE and uc.CODCPICONTATTO='";

	private static final String FROM_SQL_2p = "') "
			// + "left outer join VW_DO_STORIA_ROSA st_rosa on (r.CDNLAVORATORE=st_rosa.CDNLAVORATORE) "
			+ "left outer join de_cpi on (r.CODCPITIT=de_cpi.CODCPI) "
			+ "left outer join ts_generale on (de_cpi.CODPROVINCIA=ts_generale.CODPROVINCIASIL) "
			+ "inner join do_incrocio on (do_rosa.PRGINCROCIO=do_incrocio.PRGINCROCIO) "
			+ "inner join do_richiesta_az rich1 on (do_incrocio.PRGRICHIESTAAZ=rich1.PRGRICHIESTAAZ) "
			+ "inner join do_richiesta_az rich2 on (rich1.NUMANNO=rich2.NUMANNO and rich1.NUMRICHIESTA=rich2.NUMRICHIESTA and rich2.NUMSTORICO=0) "
			+ "inner join do_evasione ev on (rich2.PRGRICHIESTAAZ = ev.PRGRICHIESTAAZ) "
			+ "left outer join do_disponibilita on (rich2.PRGRICHIESTAAZ=do_disponibilita.PRGRICHIESTAAZ and do_disponibilita.CDNLAVORATORE=r.cdnLavoratore) "
			+ "left outer join de_disponibilita_rosa on (do_disponibilita.CODDISPONIBILITAROSA=de_disponibilita_rosa.CODDISPONIBILITAROSA) "
			// + "left outer join AM_PATTO_LAVORATORE on (r.CDNLAVORATORE=AM_PATTO_LAVORATORE.CDNLAVORATORE and
			// AM_PATTO_LAVORATORE.DATFINE is null and AM_PATTO_LAVORATORE.CODCPI='";
			+ "left outer join AM_PATTO_LAVORATORE on (r.CDNLAVORATORE=AM_PATTO_LAVORATORE.CDNLAVORATORE "
			+ "and nvl(AM_PATTO_LAVORATORE.DATFINE,TO_DATE ('01/01/2100', 'DD/MM/YYYY'))=TO_DATE ('01/01/2100', 'DD/MM/YYYY') "
			+ "and AM_PATTO_LAVORATORE.CODCPI='";

	private static final String FROM_SQL_3p = "') where r.prgrosa=";
	private static final String WHERE_SQL = " and r.CODTIPOCANC is null "
			+ "and (do_disponibilita.CODDISPONIBILITAROSA like decode(do_rosa.PRGTIPOROSA,3,'A','%') "
			+ "or do_disponibilita.CODDISPONIBILITAROSA is null) ";

	private static final String ORDER_SQL_BASE = "order by decIndiceVicinanza desc, strCognomeNome asc ";
	// private static final String ORDER_SQL_1 = "order by decIndiceVicinanza desc, ordineDid asc, ordineSegn desc,
	// ordineEsclusioni desc, strCognomeNome asc ";
	// private static final String ORDER_SQL_2 = "order by codProvinciaSil, decIndiceVicinanza desc, ordineDid asc,
	// ordineSegn desc, ordineEsclusioni desc, strCognomeNome asc ";
	// private static final String ORDER_SQL_4 = "order by decIndiceVicinanza desc, ordineDid asc ";

	private static final String ORDER_SQL_5 = "order by OrdineAmm desc, decIndiceVicinanza desc, ordineDid desc, an.strCognome, an.strNome ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		// SourceBean req = requestContainer.getServiceRequest();
		String cpiRose = StringUtils.getAttributeStrNotNull(req, "CPIROSE");
		String prgAzienda = StringUtils.getAttributeStrNotNull(req, "PRGAZIENDA");
		String prgRosa = StringUtils.getAttributeStrNotNull(req, "PRGROSA");
		String ordMaster = StringUtils.getAttributeStrNotNull(req, "ORDMASTER");

		// cifratura cdnLavoratore temporanea
		String SELECT_SQL_1p = "";

		String buf = SELECT_SQL;// + prgAzienda;
		buf += SELECT_SQL_1p + SELECT_SQL_2p + FROM_SQL + cpiRose;
		buf += FROM_SQL_2p + cpiRose;
		buf += FROM_SQL_3p + prgRosa;
		buf += WHERE_SQL;
		// L'ordinamento avviene in base al campo NUMORDPROVINCIA e al valore calcolato nella query "ordineAmm" */
		buf += ORDER_SQL_5;

		// Debug
		_logger.debug("sil.module.ido.DynStatementCandidatiRosa" + "::Stringa di ricerca:" + buf);

		return (buf);
	}
}