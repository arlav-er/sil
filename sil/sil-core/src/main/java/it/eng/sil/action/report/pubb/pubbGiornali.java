package it.eng.sil.action.report.pubb;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.action.report.AbstractSimpleReport;

public class pubbGiornali extends AbstractSimpleReport {
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			// String utente = (String) request.getAttribute("UTENTE");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("ProvaPubblicazione." + tipoFile);
			else
				setStrNomeDoc("ProvaPubblicazione.pdf");

			setStrDescrizione("Prova pubblicazione");
			// String nomeReport=(String)request.getAttribute("NOMEFILE");
			setReportPath("pubb/NewPubbl_gior_CC.rpt");

			Vector params = new Vector(1);
			// params.add(request.getAttribute("cdnLavoratore"));
			// params.add("101");

			/*
			 * String datPubblicazioneDal = (String) request.getAttribute("DATPUBBLICAZIONEDAL"); if
			 * (datPubblicazioneDal != null && !datPubblicazioneDal.equals("")) {
			 * params.add(request.getAttribute("datPubblicazioneDal")); } else{ params.add("01/01/1900"); }
			 * 
			 * String datPubblicazioneAl = (String) request.getAttribute("DATPUBBLICAZIONEAL"); if (datPubblicazioneAl
			 * != null && !datPubblicazioneAl.equals("")) { params.add(request.getAttribute("datPubblicazioneAl")); }
			 * else{ params.add("31/12/2100"); }
			 * 
			 * String sessionId=(String) request.getAttribute("SESSIONID"); if (sessionId != null &&
			 * !sessionId.equals("")) { params.add(request.getAttribute("SESSIONID")); } else{ params.add(""); }
			 */
			String codGiornale = (String) request.getAttribute("CODGIORNALE");
			if (codGiornale != null && !codGiornale.equals("")) {
				params.add(request.getAttribute("CODGIORNALE"));
			} else {
				params.add("");
			}

			String datInizioSett = (String) request.getAttribute("DATINIZIOSETT");
			if (datInizioSett != null && !datInizioSett.equals("")) {
				params.add(request.getAttribute("DATINIZIOSETT"));
			} else {
				params.add("");
			}

			String datFineSettimana = (String) request.getAttribute("DATFINESETTIMANA");
			if (datFineSettimana != null && !datFineSettimana.equals("")) {
				params.add(request.getAttribute("DATFINESETTIMANA"));
			} else {
				params.add("");
			}

			/*
			 * String utRic = (String) request.getAttribute("UTRIC"); if (utRic != null && !utRic.equals("")) { if
			 * (utRic.equalsIgnoreCase("MIE")) params.add(utente); if (utRic.equalsIgnoreCase("GRUP"))
			 * params.add("GRUP"); if (utRic.equalsIgnoreCase("TUTTE")) params.add(""); } else{ params.add(""); }
			 */

			BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
			if (numProt != null) {
				setNumProtocollo(numProt);
				params.add(numProt.toString());
			}

			String annoProt = (String) request.getAttribute("annoProt");
			if (annoProt != null && !annoProt.equals("")) {
				setNumAnnoProt(new BigDecimal(annoProt));
				params.add(annoProt);
			}

			String kLock = (String) request.getAttribute("kLockProt");
			if (kLock != null && !kLock.equals("")) {
				setNumKeyLock(new BigDecimal(kLock));
			}

			setParams(params);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true"))
				insertDocument(request, response);
			else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);
		} // else
	}

}
