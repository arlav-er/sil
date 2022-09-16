package it.eng.sil.action.report.pubb;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.action.report.AbstractSimpleReport;

public class PubblSpecifica extends AbstractSimpleReport {
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		// Al momento non gestiamo il salvataggio quindi l'unico parametro
		// passato è apri e tipoDoc, in futuro si vedrà.....
		// Il codice comunque lo lascio senza commentarlo tanto (se i parametri
		// sono corretti) non da problemi
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String utente = (String) request.getAttribute("UTENTE");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null)
				setStrNomeDoc("PubblSpecifica." + tipoFile);
			else
				setStrNomeDoc("PubblSpecifica.pdf");

			setStrDescrizione("Pubblicazione specifica");
			String nomeReport = (String) request.getAttribute("NOMEFILE");
			setReportPath("pubb/" + nomeReport);

			Vector params = new Vector(1);
			String prgRichAzienda = (String) request.getAttribute("prgRichAzienda");
			if (prgRichAzienda != null && !prgRichAzienda.equals("")) {
				params.add(prgRichAzienda);
			}

			setParams(params);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);
			// Al momento non gestiamo il salvataggio quindi l'unico parametro
			// passato è apri e tipoDoc, in futuro si vedrà.....
			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true"))
				insertDocument(request, response);
			else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);
		} // else
	}

}// class PubblSpecifica
