package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;

/**
 * Effettua la ricerca dinamica di una richieste dati: - o nessuna delle precedenti (restituisce TUTTO)
 * 
 * @author Alessio Rolfini
 * 
 */
public class DynStatementRicercaRichieste implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynStatementRicercaRichieste.class.getName());

	private static final String SELECT_SQL_BASE = " select distinct raz.prgRichiestaAz, az.STRRAGIONESOCIALE, uaz.strIndirizzo, "
			+ "raz.numanno, raz.numrichiesta, raz.numstorico, " + "(case "
			+ "when paz.strtarga is not null then (nvl(raz.NUMRICHIESTAORIG, raz.NUMRICHIESTA) || ' / ' || raz.NUMANNO || ' / ' || paz.strtarga) "
			+ " else (nvl(raz.NUMRICHIESTAORIG, raz.NUMRICHIESTA) || ' / ' || raz.NUMANNO) " + "end) NUMRICH, "
			+ "decode(de_evasione_rich.FLGINCROCIO, 'S', '1','N','1','0') as viewMatchBtn, "
			+ "com.STRDENOMINAZIONE || nvl(' (' || trim(p.strIstat) ||') ', p.codProvincia )  as desComune, "
			+ "TO_CHAR(raz.DATRICHIESTA, 'DD/MM/YY') as DATRICHIESTA, "
			+ "TO_CHAR(raz.datScadenza,'DD/MM/YY') as datScadenza, " + "raz.prgAzienda as prgAzienda, "
			+ "raz.prgUnita as prgUnita, paz.strtarga strtargaordinamento, "
			+ " decode(raz.numrichiestaorig, null, 1, 0) numrichiestaorigordinamento, "
			+ " nvl(raz.numrichiestaorig, raz.numrichiesta) numrichiestaordinamento, " +
			// campi aggiunti per la ricerca nel modulo AS
			"DO_EVASIONE.codEvasione, " + "case " + "when DO_EVASIONE.codEvasione in ('MIR','MPP','MPA') " + "then( "
			+ "case " + "when raz.CODMONOCMCATEGORIA = 'D' then DE_EVASIONE_RICH.STRDESCRIZIONE || '<br>' ||'Disabili'"
			+ "when raz.CODMONOCMCATEGORIA = 'A' then DE_EVASIONE_RICH.STRDESCRIZIONE || '<br>' ||'Ex. Art. 18' "
			+ "when raz.CODMONOCMCATEGORIA = 'E' then DE_EVASIONE_RICH.STRDESCRIZIONE || '<br>' ||'Entrambi' "
			+ "else DE_EVASIONE_RICH.STRDESCRIZIONE || '<br>' ||'' " + "end) " + "else DE_EVASIONE_RICH.STRDESCRIZIONE "
			+ "end " + "EVASIONE," +
			// "(DE_EVASIONE_RICH.STRDESCRIZIONE || '/' || raz.CODMONOCMCATEGORIA) AS EVASIONE, "+
			"TO_CHAR(raz.datChiamata,'DD/MM/YYYY') AS dataChiam, " + "do_evasione.cdnstatorich, "
			+ "DE_STATO_EV_RICH.STRDESCRIZIONE AS STATOEV, " + "DE_STATO_RICHIESTA.STRDESCRIZIONE AS STATORICH " +
			// fine per il modulo AS
			"from do_richiesta_az raz " + "inner join an_azienda az on az.prgAzienda=raz.prgAzienda "
			+ "inner join an_unita_azienda uaz on (uaz.prgUnita=raz.prgUnita and uaz.prgAzienda=raz.prgAzienda)  "
			+ "inner join de_comune com on uaz.codcom=com.codcom "
			+ "inner join de_provincia p on com.codprovincia=p.codprovincia "
			+ "left outer join do_evasione on (raz.PRGRICHIESTAAZ=do_evasione.PRGRICHIESTAAZ) "
			+ "left outer join de_evasione_rich on (do_evasione.CODEVASIONE=de_evasione_rich.CODEVASIONE) " +
			// join per il modulo AS
			"LEFT OUTER JOIN de_stato_ev_rich ON (do_evasione.cdnstatorich = de_stato_ev_rich.cdnstatorich)"
			+ "LEFT OUTER JOIN DE_STATO_RICHIESTA ON (do_evasione.codmonostatorich = DE_STATO_RICHIESTA.codmonostatorich)"
			+ "LEFT OUTER JOIN DE_PROVINCIA paz ON (raz.codprovinciaprov = paz.codprovincia)";

	private String className = this.getClass().getName();

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		String prgRichiestaAz = req.containsAttribute("prgRichiestaAz") ? req.getAttribute("prgRichiestaAz").toString()
				: "";
		String anno = (String) req.getAttribute("anno");
		String dataDal = (String) req.getAttribute("datRichiestaDal");
		String dataAl = (String) req.getAttribute("datRichiestaAl");
		String dataScadenzaDal = (String) req.getAttribute("datScadenzaDal");
		String dataScadenzaAl = (String) req.getAttribute("datScadenzaAl");
		String codTipoAzienda = (String) req.getAttribute("codTipoAzienda");
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String prgUnita = (String) req.getAttribute("prgUnita");
		String codCPI = (String) req.getAttribute("codCPI");
		String codMansione = (String) req.getAttribute("CODMANSIONE");
		String cdnStatoRich = (String) req.getAttribute("cdnStatoRich");
		String codMonoStatoRich = (String) req.getAttribute("codMonoStatoRich");
		String utente = (String) req.getAttribute("utente");
		String codMonoTipoGrad = (String) req.getAttribute("codMonoTipoGrad");
		String codMonoCMcategoria = (String) req.getAttribute("codMonoCMcategoria");
		String codStatoInvioCL = (String) req.getAttribute("codStatoInvioVacancy");
		String flagCresco = (String) req.getAttribute("flagCresco");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((prgRichiestaAz != null) && (!prgRichiestaAz.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" nvl(raz.numrichiestaorig, raz.numrichiesta)=" + prgRichiestaAz);
		}

		if (buf.length() == 0) {
			buf.append(" WHERE ");
		} else {
			buf.append(" AND ");
		}

		buf.append(" raz.numstorico=0");

		if ((anno != null) && (!anno.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" raz.numanno = " + anno);
		}
		if ((dataDal != null) && (!dataDal.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" raz.datrichiesta >= TO_DATE('" + dataDal + "', 'DD/MM/YYYY') ");
		}

		if ((dataAl != null) && (!dataAl.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" raz.datrichiesta <= TO_DATE('" + dataAl + "', 'DD/MM/YYYY') ");
		}

		if ((dataScadenzaDal != null) && (!dataScadenzaDal.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" raz.datscadenza >= TO_DATE('" + dataScadenzaDal + "', 'DD/MM/YYYY') ");
		}

		if ((dataScadenzaAl != null) && (!dataScadenzaAl.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" raz.datscadenza <= TO_DATE('" + dataScadenzaAl + "', 'DD/MM/YYYY') ");
		}

		if ((codTipoAzienda != null) && (!codTipoAzienda.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" az.codtipoazienda='" + codTipoAzienda + "'");
		}

		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" raz.prgAzienda=" + prgAzienda);
			if ((prgUnita != null) && (!prgUnita.equals(""))) {
				buf.append(" AND raz.prgUnita=" + prgUnita);
			}
		}

		if ((codCPI != null) && (!codCPI.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" raz.codCPI='" + codCPI + "' ");
		}

		if ((flagCresco != null) && (!flagCresco.equals("")) && (flagCresco.equals("on"))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" DO_EVASIONE.Flgpubbcresco='S' ");
		}
		if ((cdnStatoRich != null) && (!cdnStatoRich.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" ev.cdnStatoRich=" + cdnStatoRich);
			query_totale.append("left join do_evasione ev on ev.prgRichiestaAz = raz.prgRichiestaAz ");
		}

		if ((codMonoStatoRich != null) && (!codMonoStatoRich.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" ev.codMonoStatoRich='" + codMonoStatoRich + "' ");
			if ((cdnStatoRich == null) || (cdnStatoRich.equals(""))) {
				query_totale.append("left join do_evasione ev on ev.prgRichiestaAz = raz.prgRichiestaAz ");
			}
		}

		if ((codMansione != null) && (!codMansione.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" man.codMansione='" + codMansione + "'");
			query_totale.append(
					"left join (do_alternativa left join do_mansione man on (man.prgRichiestaAz = do_alternativa.prgRichiestaAz and man.prgAlternativa = do_alternativa.prgAlternativa)) ");
			query_totale.append("on (raz.prgRichiestaAz = do_alternativa.PRGRICHIESTAAZ) ");
		}

		if ((utente != null) && (!utente.equals(""))) {
			if (utente.equals("1")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" raz.cdnutins=" + user.getCodut());
			} else if (utente.equals("2")) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append("raz.cdngruppo = " + user.getCdnGruppo());
				// buf.append(" raz.cdnutins in ( select put.cdnut from ts_profilatura_utente put where
				// put.cdngruppo="+user.getCdnGruppo()+")");
			}
		}

		if ((codStatoInvioCL != null) && (!codStatoInvioCL.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			if ("INVIATE".equals(codStatoInvioCL)) {
				buf.append(" vacancy.CODSTATOINVIOCL IN ('PI', 'VI') ");
			} else {
				buf.append(" vacancy.CODSTATOINVIOCL IN ('PA', 'VA') ");
			}
			query_totale.append("left join CL_VACANCY vacancy ON raz.PRGRICHIESTAAZ = vacancy.PRGRICHIESTAAZ ");
		}

		// codice aggiunto da Gritti per modulo AS
		// INIT-PARTE-TEMP
		if (Sottosistema.AS.isOff()) {
			// END-PARTE-TEMP

			// INIT-PARTE-TEMP
		} else {
			// END-PARTE-TEMP
			String codEvasione = (String) req.getAttribute("codEvasione");
			String dataChiam = (String) req.getAttribute("dataChiam");

			if ((dataChiam != null) && (!dataChiam.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				if (codEvasione == null || codEvasione.equals("")) {
					buf.append(" (raz.datChiamata = TO_DATE('" + dataChiam + "', 'DD/MM/YYYY') OR "
							+ "  raz.datChiamataCM = TO_DATE('" + dataChiam + "', 'DD/MM/YYYY') ) ");
				} else if (codEvasione.equals("CMA")) {
					buf.append(" raz.datChiamataCM = TO_DATE('" + dataChiam + "', 'DD/MM/YYYY') ");
				} else {
					buf.append(" raz.datChiamata = TO_DATE('" + dataChiam + "', 'DD/MM/YYYY') ");
				}
			}

			if ((codEvasione != null) && (!codEvasione.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" (upper(DO_EVASIONE.codEvasione) = '" + codEvasione + "'" + ")");
			}

			if ((codMonoTipoGrad != null) && (!codMonoTipoGrad.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" raz.CODMONOTIPOGRAD = '" + codMonoTipoGrad + "'");
			}

			if ((codMonoCMcategoria != null) && (!codMonoCMcategoria.equals(""))) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" raz.codMonoCMcategoria = '" + codMonoCMcategoria + "'");
			}

			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

		buf.append(
				" ORDER BY raz.numanno DESC, decode(raz.numrichiestaorig, null, 1, 0) desc, nvl(raz.numrichiestaorig, raz.numrichiesta) desc, "
						+ " nvl(paz.strtarga, '1') asc");

		query_totale.append(buf.toString());

		_logger.debug("Query ricerca: " + query_totale.toString());

		return query_totale.toString();

	}

}