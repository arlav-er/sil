package it.eng.sil.module.ido;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class DynStatamentRicercaPubb implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT DISTINCT RIC.NUMRICHIESTA, NVL(RIC.NUMRICHIESTAORIG, RIC.NUMRICHIESTA) AS NUMRICHIESTAORIG, RIC.PRGRICHIESTAAZ, RIC.NUMANNO AS ANNO, "
			+ " AZ.STRRAGIONESOCIALE, UAZ.STRINDIRIZZO, RIC.PRGAZIENDA, RIC.PRGUNITA, "
			+ " TO_CHAR(RIC.DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE, TO_CHAR(RIC.DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE, RIC.CDNUTINS, RIC.FLGPUBBLICATA, "
			+ "		   case " + "		   when RIC.CODMONOCMCATPUBB = 'D' " + "		   then 'Disabili'"
			+ "		   when RIC.CODMONOCMCATPUBB = 'A' " + "		   then 'Ex. Art. 18' "
			+ "		   when RIC.CODMONOCMCATPUBB = 'E' " + "		   then 'Entrambi' "
			+ "		   when RIC.CODMONOCMCATPUBB = '' " + "		   then '' " + "		   end "
			+ "		   CODMONOCMCATPUBB" + " FROM DO_RICHIESTA_AZ RIC "
			+ " INNER JOIN AN_AZIENDA AZ ON RIC.PRGAZIENDA=AZ.PRGAZIENDA"
			+ " INNER JOIN AN_UNITA_AZIENDA UAZ ON (RIC.PRGAZIENDA=UAZ.PRGAZIENDA AND RIC.PRGUNITA=UAZ.PRGUNITA)"
			+ " INNER JOIN DO_EVASIONE EV ON (RIC.PRGRICHIESTAAZ=EV.PRGRICHIESTAAZ) ";

	private static final String SELECT_PUBBL_BASE = "SELECT  RIC.CODCPI, TO_CHAR(RIC.DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE,"
			+ "		   RIC.NUMANNO, RIC.NUMRICHIESTA, NVL(RIC.NUMRICHIESTAORIG, RIC.NUMRICHIESTA) AS NUMRICHIESTAORIG,"
			+ "		   RIC.FLGPUBBLICATA," + "		   RIC.STRMANSIONEPUBB, EV.CDNSTATORICH,"
			+ "		   RIC.STRDATIAZIENDAPUBB," + "		   RIC.STRLUOGOLAVORO," + "		   RIC.STRFORMAZIONEPUBB,"
			+ "		   RIC.TXTCARATTERISTFIGPROF," + "		   RIC.TXTCONDCONTRATTUALE,"
			+ "		   RIC.STRNOTEORARIOPUBB," + "		   RIC.STRCONOSCENZEPUBB,"
			+ "		   RIC.STRRIFCANDIDATURAPUBB,"
			+ "		   TO_CHAR(RIC.DATSCADENZA,'dd/mm/yyyy') DATSCADENZA, RIC.PRGRICHIESTAAZ,"
			+ "		   RIC.TXTFIGURAPROFESSIONALE," + "		   DE_PROVINCIA.STRDENOMINAZIONE, DE_CPI.STRDESCRIZIONE,"
			+ "		   DE_CPI.STRINDIRIZZO, DE_CPI.STRTEL, DE_CPI.STRFAX," + "		   DE_CPI.STREMAIL, RIC.NUMSTORICO,"
			+ "		   TO_CHAR(RIC.DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE, "
			+ "        EV.CODEVASIONE, " + "		   case " + "		   when RIC.CODMONOCMCATPUBB = 'D' "
			+ "		   then 'Disabili'" + "		   when RIC.CODMONOCMCATPUBB = 'A' " + "		   then 'Ex. Art. 18' "
			+ "		   when RIC.CODMONOCMCATPUBB = 'E' " + "		   then 'Entrambi' "
			+ "		   when RIC.CODMONOCMCATPUBB = '' " + "		   then '' " + "		   end "
			+ "		   CODMONOCMCATPUBB," + "		   DE_PROVINCIA.STRINTESTAZIONESTAMPA "
			+ "	  FROM DO_RICHIESTA_AZ RIC," + "		   DO_EVASIONE EV," + "		   DE_CPI DE_CPI,"
			+ "		   DE_PROVINCIA DE_PROVINCIA " + "	 WHERE RIC.PRGRICHIESTAAZ = EV.PRGRICHIESTAAZ "
			+ "	   AND RIC.CODCPI = DE_CPI.CODCPI " + "	   AND DE_CPI.CODPROVINCIA = DE_PROVINCIA.CODPROVINCIA ";

	private static final String SELECT_PUBBL_AZ_BASE = "SELECT  DE_CPI.CODCPI, DE_CPI.STREMAIL, DE_CPI.STRTEL, "
			+ "		   DE_CPI.STRFAX, DE_CPI.STRINDIRIZZO,"
			+ "		   DE_CPI.STRDESCRIZIONE, DE_PROVINCIA.STRDENOMINAZIONE,"
			+ "		   RIC.NUMRICHIESTA, NVL(RIC.NUMRICHIESTAORIG, RIC.NUMRICHIESTA) AS NUMRICHIESTAORIG, RIC.NUMANNO,"
			+ "		   TO_CHAR(RIC.DATRICHIESTA,'dd/mm/yyyy') DATRICHIESTA, TO_CHAR(RIC.DATSCADENZA,'dd/mm/yyyy') DATSCADENZA,"
			+ "		   RIC.STRMANSIONEPUBB," + "		   RIC.PRGRICHIESTAAZ," + "		   RIC.STRLUOGOLAVORO,"
			+ "		   RIC.TXTCARATTERISTFIGPROF," + "		   RIC.TXTCONDCONTRATTUALE,"
			+ "		   RIC.STRNOTEORARIOPUBB," + "		   RIC.STRCONOSCENZEPUBB,"
			+ "		   RIC.STRRIFCANDIDATURAPUBB," + "		   RIC.STRDATIAZIENDAPUBB," + "		   RIC.FLGPUBBLICATA,"
			+ "		   TO_CHAR(RIC.DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE,"
			+ "		   TO_CHAR(RIC.DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE, EV.CDNSTATORICH,"
			+ "		   RIC.NUMSTORICO, " + " 	   RIC.NUMPROFRICHIESTI, " + "        EV.CODEVASIONE, "
			+ "		   case " + "		   when RIC.CODMONOCMCATPUBB = 'D' " + "		   then 'Disabili'"
			+ "		   when RIC.CODMONOCMCATPUBB = 'A' " + "		   then 'Ex. Art. 18' "
			+ "		   when RIC.CODMONOCMCATPUBB = 'E' " + "		   then 'Entrambi' "
			+ "		   when RIC.CODMONOCMCATPUBB = '' " + "		   then '' " + "		   end "
			+ "		   CODMONOCMCATPUBB," + "		   DE_PROVINCIA.STRINTESTAZIONESTAMPA " + "	  FROM DE_CPI DE_CPI,"
			+ "		   DO_RICHIESTA_AZ RIC," + "		   DE_PROVINCIA DE_PROVINCIA," + "		   DO_EVASIONE EV "
			+ "   WHERE DE_CPI.CODCPI = RIC.CODCPI " + "         AND DE_CPI.CODPROVINCIA = DE_PROVINCIA.CODPROVINCIA "
			+ "         AND RIC.PRGRICHIESTAAZ = EV.PRGRICHIESTAAZ ";

	private static final String sWhere = " FLGPUBBLICATA='S' " + " AND EV.CDNSTATORICH<>5  ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();

		String nomeFile = (String) req.getAttribute("nomeFile");
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String prgUnita = (String) req.getAttribute("prgUnita");
		String flgPubblicata = (String) req.getAttribute("FLGPUBBLICATA");
		// String prgRichiestaAz =(String) req.getAttribute("PRGRICHIESTAAZ");
		String anno = (String) req.getAttribute("ANNO");
		String utric = (String) req.getAttribute("UTENTE");
		String cdnut = (String) req.getAttribute("CDNUT");
		String annoDal = (String) req.getAttribute("NUMANNODAL");
		String annoAl = (String) req.getAttribute("NUMANNOAL");
		String prgRichiestaAz = (String) req.getAttribute("NUMRICHIESTA");
		String numPubblicazioneDal = (String) req.getAttribute("NUMPUBBLICAZIONEDAL");
		String numPubblicazioneAl = (String) req.getAttribute("NUMPUBBLICAZIONEAL");

		String datPubblicazione = null;
		String datScadenzaPubblicazione = null;

		Vector modPubblicazione = new Vector();
		String modPubblStr = "";
		try {
			modPubblStr = StringUtils.getAttributeStrNotNull(req, "modPubblicazione");
			StringTokenizer st = new StringTokenizer(modPubblStr, ",");

			for (; st.hasMoreTokens();) {
				modPubblicazione.add(st.nextToken());
			}
		} catch (Exception e) {
			modPubblicazione = req.getAttributeAsVector("modPubblicazione");
		}

		Vector macroCategoriaVec = (Vector) req.getAttributeAsVector("MACROCATEGORIA");

		if (nomeFile == null) {
			datPubblicazione = (String) req.getAttribute("DATPUBBLICAZIONE");
			datScadenzaPubblicazione = (String) req.getAttribute("DATSCADENZAPUBBLICAZIONE");
		} else if (nomeFile.equalsIgnoreCase("Pubbl_CC.rpt") || nomeFile.equalsIgnoreCase("NewPubbl_az_CC.rpt")) {
			datPubblicazione = (String) req.getAttribute("DATPUBBLICAZIONEDAL");
			datScadenzaPubblicazione = (String) req.getAttribute("DATPUBBLICAZIONEAL");
		}

		// Mansione
		String codMansione = (String) req.getAttribute("CODMANSIONE");
		StringBuffer query_totale = null;
		if (nomeFile == null) {
			query_totale = new StringBuffer(SELECT_SQL_BASE);
		} else {
			if (nomeFile.equalsIgnoreCase("Pubbl_CC.rpt")) {
				query_totale = new StringBuffer(SELECT_PUBBL_BASE);
			}
			if (nomeFile.equalsIgnoreCase("NewPubbl_az_CC.rpt")) {
				query_totale = new StringBuffer(SELECT_PUBBL_AZ_BASE);
			}
		}

		StringBuffer buf = new StringBuffer();

		if ((nomeFile == null) && ((flgPubblicata != null) && (!flgPubblicata.equals("")))) {
			buf.append(" AND ");

			if (flgPubblicata.equals("DAPUB")) {
				buf.append(" RIC.DATPUBBLICAZIONE > SYSDATE");
			}
			if (flgPubblicata.equals("INPUB")) {
				buf.append(" RIC.DATPUBBLICAZIONE <= SYSDATE AND SYSDATE <= RIC.DATSCADENZAPUBBLICAZIONE ");
			}
			if (flgPubblicata.equals("SCAD")) {
				buf.append(" SYSDATE > RIC.DATSCADENZAPUBBLICAZIONE");
			}
		}

		if ((nomeFile != null)
				&& (nomeFile.equalsIgnoreCase("Pubbl_CC.rpt") || nomeFile.equalsIgnoreCase("NewPubbl_az_CC.rpt"))) {
			buf.append("AND RIC.DATPUBBLICAZIONE <= SYSDATE AND SYSDATE <= RIC.DATSCADENZAPUBBLICAZIONE ");
		}

		if ((prgAzienda != null) && (!prgAzienda.equals("")) && (prgUnita != null) && (!prgUnita.equals(""))) {
			buf.append(" AND ");
			buf.append(" RIC.PRGAZIENDA=" + prgAzienda);
			buf.append(" AND ");
			buf.append(" RIC.PRGUNITA=" + prgUnita);
		}

		if ((datPubblicazione != null) && (!datPubblicazione.equals(""))) {
			buf.append(" AND ");
			buf.append(" RIC.DATPUBBLICAZIONE >= TO_DATE('" + datPubblicazione + "', 'DD/MM/YYYY') ");
		}

		if ((datScadenzaPubblicazione != null) && (!datScadenzaPubblicazione.equals(""))) {
			buf.append(" AND ");
			buf.append(" RIC.DATPUBBLICAZIONE <= TO_DATE('" + datScadenzaPubblicazione + "', 'DD/MM/YYYY') ");
		}

		// Numero richiesta
		if ((prgRichiestaAz != null) && (!prgRichiestaAz.equals(""))) {
			buf.append(" AND ");
			buf.append(" NVL(RIC.NUMRICHIESTAORIG, RIC.NUMRICHIESTA) = " + prgRichiestaAz);
		}

		if ((numPubblicazioneDal != null) && (!numPubblicazioneDal.equals(""))) {
			buf.append(" AND ");
			buf.append(" NVL(RIC.NUMRICHIESTAORIG, RIC.NUMRICHIESTA) >= " + numPubblicazioneDal);
		}

		if ((numPubblicazioneAl != null) && (!numPubblicazioneAl.equals(""))) {
			buf.append(" AND ");
			buf.append(" NVL(RIC.NUMRICHIESTAORIG, RIC.NUMRICHIESTA) <= " + numPubblicazioneAl);
		}

		buf.append(" AND RIC.NUMSTORICO = 0");
		buf.append(" AND EV.codevasione in ('DFA','DFD','DPR','DRA','AS','CMA','MPP','MPA')");

		// ANNO
		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" AND ");
			buf.append(" RIC.NUMANNO = " + anno);
		}
		if ((annoDal != null) && (!annoDal.equals(""))) {
			buf.append(" AND ");
			buf.append(" RIC.NUMANNO >= " + annoDal);
		}
		if ((annoAl != null) && (!annoAl.equals(""))) {
			buf.append(" AND ");
			buf.append(" RIC.NUMANNO <= " + annoAl);
		}

		// Mansione
		if ((codMansione != null) && (!codMansione.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" man.codMansione = '" + codMansione + "'");
			query_totale.append(
					"left join (do_alternativa left join do_mansione man on (man.prgRichiestaAz = do_alternativa.prgRichiestaAz and man.prgAlternativa = do_alternativa.prgAlternativa)) ");
			query_totale.append("on (ric.prgRichiestaAz = do_alternativa.PRGRICHIESTAAZ) ");
		}

		// Categoria CM
		String codMonoCMcatPubb = (String) req.getAttribute("codMonoCMcatPubb");
		if ((codMonoCMcatPubb != null) && (!codMonoCMcatPubb.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" ric.codMonoCMcatPubb = '" + codMonoCMcatPubb + "'");
		}

		// Ricerca annunci pubblici/riservati

		if (macroCategoriaVec.size() > 0 && macroCategoriaVec.size() != 2) {
			if (macroCategoriaVec.get(0).equals("1")) {
				buf.append(" AND ");
				buf.append(" (EV.CODEVASIONE = 'DFD' OR EV.CODEVASIONE = 'DPR')");
			} else {
				buf.append(" AND ");
				buf.append(" (EV.CODEVASIONE = 'DFA' OR EV.CODEVASIONE = 'DRA')");
			}
		}

		if (modPubblicazione != null && modPubblicazione.size() != 0) {
			boolean first = true;
			buf.append(" AND ( ");
			for (int i = 0; i < modPubblicazione.size(); i++) {
				String elem = modPubblicazione.get(i).toString();
				if (elem.equalsIgnoreCase("1")) {
					buf.append(" EV.FLGPUBBWEB = 'S'");
					first = false;
				}
				if (elem.equalsIgnoreCase("2")) {
					if (first) {
						buf.append(" EV.FLGPUBBGIORNALI = 'S'");
						first = false;
					} else {
						buf.append(" OR EV.FLGPUBBGIORNALI = 'S'");
					}
				}
				if (elem.equalsIgnoreCase("3")) {
					if (first) {
						buf.append(" EV.FLGPUBBBACHECA = 'S'");
						first = false;
					} else {
						buf.append(" OR EV.FLGPUBBBACHECA = 'S'");
					}
				}

			}
			buf.append(" ) ");

		}

		if ((utric != null) && ((utric.equals("1")) || (utric.equals("2")))) {
			buf.append(" AND ");
			if (utric.equals("1"))
				buf.append(" RIC.CDNUTINS = " + cdnut);
			if (utric.equals("2"))
				buf.append("RIC.CDNGRUPPO = " + user.getCdnGruppo());
			/*
			 * buf.append(" RIC.CDNUTINS IN " + "(SELECT distinct cdnut FROM TS_PROFILATURA_UTENTE A " +
			 * "WHERE cdngruppo=" + "(SELECT distinct cdngruppo " + "FROM TS_PROFILATURA_UTENTE B " + "WHERE B.CDNUT=" +
			 * cdnut + "))");
			 */
		}

		if (nomeFile == null) {
			// buf.append(" order by az.strRagioneSociale, ric.numanno desc, ric.numrichiesta desc");
			buf.append(" ORDER BY ric.numanno DESC, nvl(ric.numrichiestaorig, ric.numrichiesta) DESC");
		} else {
			if (nomeFile.equalsIgnoreCase("Pubbl_CC.rpt")) {
				buf.append(" ORDER BY RIC.CODCPI");
			}
			if (nomeFile.equalsIgnoreCase("NewPubbl_az_CC.rpt")) {
				buf.append(" ORDER BY RIC.CODCPI");
			}
		}

		if (nomeFile == null) {
			query_totale.append(" WHERE ");
		} else {
			query_totale.append(" AND ");
		}
		query_totale.append(sWhere + buf.toString());
		return query_totale.toString();

	}

}