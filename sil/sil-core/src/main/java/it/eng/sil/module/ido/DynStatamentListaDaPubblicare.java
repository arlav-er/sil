package it.eng.sil.module.ido;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

public class DynStatamentListaDaPubblicare implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = "SELECT RIE.PRGRICHIESTAAZ, NUMRICHIESTA, NUMANNO, STRRAGIONESOCIALE, to_char(DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE, to_char(DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE "
			+ "FROM do_richiesta_az RIE left join do_evasione ev on (ev.prgRichiestaAz = rie.prgRichiestaAz), an_azienda AZ "
			+ "WHERE RIE.FLGPUBBLICATA='S' " + "AND ev.cdnstatorich <> 5 " + "AND RIE.PRGAZIENDA=AZ.PRGAZIENDA "
			+ "AND RIE.DATSCADENZAPUBBLICAZIONE >=SYSDATE " + "AND RIE.NUMSTORICO=0 " +
			// questa clausola è stata aggiunta con l'Art 16 , è sempre valida
			"AND ev.codevasione <> 'AS' " +
			// devono essere escluse anche le pubblicazioni con modalità di
			// evasione CMA
			// ovvero CM: Avviamento Numerico
			"AND ev.codevasione <> 'CMA' " +
			// ***************************************************************
			"AND RIE.prgrichiestaaz not in " + "  (SELECT DG.PRGRICHIESTAAZ "
			+ "  FROM do_elencopubb_giornali EG, do_richiesta_az RI, DO_DETTAGLIOPUB_GIORNALI DG "
			+ "  WHERE EG.PRGELENCOGIORNALE=DG.PRGELENCOGIORNALE  " + "  AND DG.PRGRICHIESTAAZ=RI.PRGRICHIESTAAZ ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		String codGiornale = (String) req.getAttribute("CODGIORNALE");
		String datInizioSett = (String) req.getAttribute("DATINIZIOSETT");
		String datFineSett = (String) req.getAttribute("DATFINESETTIMANA");
		String datPubblicazioneDal = (String) req.getAttribute("DATPUBBLICAZIONEDAL");
		String datPubblicazioneAl = (String) req.getAttribute("DATPUBBLICAZIONEAL");
		String utric = (String) req.getAttribute("UTRIC");
		String cdnut = (String) req.getAttribute("UTENTE");
		Vector modPubblicazione = req.getAttributeAsVector("modPubblicazione");

		buf.append("  AND EG.CODGIORNALE='" + codGiornale + "' " + "  AND EG.DATINIZIOSETT = to_date('" + datInizioSett
				+ "','dd/mm/yyyy') " + "  AND EG.DATFINESETTIMANA = to_date('" + datFineSett + "','dd/mm/yyyy') )");

		if ((datPubblicazioneDal != null) && (!datPubblicazioneDal.equals("")) && (datPubblicazioneAl != null)
				&& (!datPubblicazioneAl.equals(""))) {
			buf.append(" AND DATSCADENZAPUBBLICAZIONE >= to_date('" + datPubblicazioneDal + "','dd/mm/yyyy')");
			buf.append(" AND DATPUBBLICAZIONE <= to_date('" + datPubblicazioneAl + "','dd/mm/yyyy')");
		}
		if ((utric != null) && ((utric.equals("MIE")) || (utric.equals("GRUP")))) {
			if (utric.equals("MIE"))
				buf.append(" AND RIE.cdnutins = " + cdnut);
			if (utric.equals("GRUP"))
				buf.append("AND RIE.cdngruppo = " + user.getCdnGruppo());
			/*
			 * buf.append(" AND RIE.cdnutins IN " + "(SELECT distinct cdnut FROM TS_PROFILATURA_UTENTE A " + "WHERE
			 * cdngruppo=" + "(SELECT distinct cdngruppo " + "FROM TS_PROFILATURA_UTENTE B " + "WHERE
			 * B.CDNUT=" + cdnut + "))");
			 */
		}

		// buf.append(" order by az.strRagioneSociale, raz.prgRichiestaAz,
		// datRichiesta ");
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
		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}