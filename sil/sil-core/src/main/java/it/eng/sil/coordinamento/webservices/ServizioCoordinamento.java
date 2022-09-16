/**
 * ServizioCoordinamento.java
 */

package it.eng.sil.coordinamento.webservices;

import org.apache.log4j.Logger;

import it.eng.sil.coordinamento.helpers.ServizioSoapHelper;
import it.eng.sil.coordinamento.servizi.ServizioSoap;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.ExecuteResponse;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;

public class ServizioCoordinamento {
	private static Logger log = Logger.getLogger(ServizioCoordinamento.class.getName());

	public ExecuteResponse execute(Execute parametri) {

		String dati = parametri.getDati();
		String destinatario = parametri.getDestinatario();
		String mittente = parametri.getMittente();
		String metodo = parametri.getNomeMetodo();
		String servizio = parametri.getNomeServizio();
		String token = parametri.getToken();
		ExecuteResponse risposta = null;

		log.info("ServizioCoordinamento chiamato");
		log.info("parametri richiesta: " + parametri);

		try {
			check(parametri);
			// la classe gestisce due servizi diversi.
			// controllo mittente. Importante perche' ci dice cosa dobbiamo
			// fare..
			// se sare devo fare una cosa, se pdd debbo farne un'altra
			ServizioSoap richiesta = ServizioSoapHelper.getRichiesta(parametri);
			log.debug("Viene eseguita l'operazione della classe " + richiesta.getClass().getName());
			String risultato = richiesta.elabora(parametri);
			risposta = new ExecuteResponse(risultato);
		} catch (Exception e) {
			log.fatal("Errore esecuzione servizio", e);
			log.fatal(parametri);
			log.fatal(parametri.getDati());
			RispostaXML rispostaXml = new RispostaXML("999",
					"Impossibile elaborare la richista. Motivo:" + e.getMessage(), "E");
			risposta = new ExecuteResponse(rispostaXml.toXMLString());
		}
		return risposta;
	}

	/**
	 * @param parametri
	 */
	private void check(Execute parametri) throws Exception {
		if (parametri.getDati() == null || parametri.getDati().equals(""))
			throw new Exception("I dati da processare sono assenti.");
		if (parametri.getDestinatario() == null || parametri.getDestinatario().equals(""))
			throw new Exception("Il destinatario e' assente.");
		if (parametri.getMittente() == null || parametri.getMittente().equals(""))
			throw new Exception("Il mittente e' assente.");
		if (parametri.getNomeMetodo() == null || parametri.getNomeMetodo().equals(""))
			throw new Exception("Il nome metodo e' assente.");
		if (parametri.getNomeServizio() == null || parametri.getNomeServizio().equals(""))
			throw new Exception("Il nome servizio e' assente.");
	}
}
