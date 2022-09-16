/*
 * Created on May 21, 2007
 */
package it.eng.sil.coordinamento.helpers;

import it.eng.sil.coordinamento.servizi.ServizioSoap;
import it.eng.sil.coordinamento.utils.ConfigurazioneCoordinamentoRegionale;
import it.eng.sil.coordinamento.wsClient.np.Execute;

/**
 * Restituisce l'istanza della classe che dovra' elaborare la richiesta
 * 
 * @author savino
 */
public class ServizioSoapHelper {

	public static ServizioSoap getRichiesta(Execute parametri) throws Exception {

		String servizio = parametri.getNomeServizio();
		String metodo = parametri.getNomeMetodo();
		String destinatario = parametri.getDestinatario();

		String mittente = parametri.getMittente();
		destinatario = destinatario.replace(' ', '_');
		destinatario = destinatario.replace('\'', '_');
		String chiave = mittente + "." + servizio + "." + metodo + "." + destinatario + ".CLASSE";

		String classe = ConfigurazioneCoordinamentoRegionale.getInstance().getProperty(chiave.toUpperCase());
		if (classe == null)
			throw new Exception("Il servizio richiesto non e' gestito. Servizio=" + servizio + ", metodo=" + metodo);
		Class classeServizio = Class.forName(classe);
		Object objServizio = classeServizio.newInstance();
		return (ServizioSoap) objServizio;
	}
}
