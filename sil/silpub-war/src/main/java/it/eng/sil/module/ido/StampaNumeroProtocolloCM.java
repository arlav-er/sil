/*
 * Creato il 18-giu-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

/**
 * @author melandri
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

import java.util.StringTokenizer;
import java.util.Vector;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

import com.engiweb.framework.base.SourceBean;

/**
 * @author gritti
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class StampaNumeroProtocolloCM extends AbstractSimpleModule {

	/* (non Javadoc)
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean, com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response)
		throws Exception {

		SourceBean finale 	= new SourceBean("PROTOCOLLO");
		SourceBean rows 	= new SourceBean("ROWS");

		String codCpi		= StringUtils.getAttributeStrNotNull(request, "CODCPI");
		String datChiam 	= StringUtils.getAttributeStrNotNull(request, "DATCHIAMATACM");
		String numProt 		= StringUtils.getAttributeStrNotNull(request, "NUMPROT");
		String moreToken 	= "";
		String codCpiSt 	= "";
		String datChiamSt 	= "";

		if (!numProt.equals("")) {
			//Tokenizer per reperire il cpi e la data chiamata da usare nella query	se il punto di arrivo non è
			//la griglia interna delle stampe, caso in cui i dati necessari sono passati singolarmente nella request 			

			//conta i "|" in numprot per stabilire il range del vettore			
			Vector counts = null;
			counts = it.eng.afExt.utils.StringUtils.split(numProt, "|");

			StringTokenizer st = new StringTokenizer(numProt, "|");
			for (int i = 0; i < counts.size(); i++) {
				if (st.hasMoreTokens()) {
					moreToken = st.nextToken();
					StringTokenizer st2 = new StringTokenizer(moreToken, ",");
					for (int j = 0; j < st2.countTokens(); j++) {
						if (st2.hasMoreTokens()) {
							codCpiSt = st2.nextToken();
							datChiamSt = st2.nextToken();
							//aggiorno le variabili 	
							codCpi = codCpiSt;
							datChiam = datChiamSt;
							//aggiorno i parametri nella request
							request.updAttribute("CODCPI", codCpi);
							request.updAttribute("DATCHIAMATACM", datChiam);
							//eseguo la query, la risposta viene inserita in un SourceBean vuoto, sarà riempita successivamente							 	
							SourceBean vuoto = new SourceBean("");
							SourceBean sb = doDynamicSelect(request, vuoto);

							if (sb.getAttribute("ROW") != null) {
								Vector vet = sb.getAttributeAsVector("ROW");
								for (int z = 0; z < vet.size(); z++) {
									sb = (SourceBean) vet.get(z);
									rows.setAttribute(sb);
								}
							}
						}
					}
				}
			}
			finale.setAttribute(rows);
			response.setAttribute(finale);
		} else {
			doDynamicSelect(request, response);
		}

	}
}
