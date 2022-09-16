/*
 * Created on May 31, 2007
 */
package it.eng.sil.coordinamento.servizi;

import java.math.BigDecimal;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.message.MessageBundle;

import it.eng.afExt.SynchronizedSourceBean;
import it.eng.sil.coop.services.InviaMigrazioni;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;

/**
 * @author savino
 */
public class ImportaComunicazioneObbligatoria implements ServizioSoap {

	// uso un log diverso per sfruttare il livello di log a debug presente nel properties di log4j.
	// Con questo utilizzo si bypassa il file di configurazione del log del framework.
	private static Logger log = Logger.getLogger(ImportaComunicazioneObbligatoria.class);

	private String codiceComunicazione;

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.sil.coordinamento.ServizioSoap#elabora(it.eng.sil.coordinamento.wsClient.np.Execute)
	 */
	public String elabora(Execute parametri) {
		SourceBean movimento = null;
		BigDecimal user = new BigDecimal(190);
		// TODO poloMittente CHI E'?
		String poloMittente = null;
		String risultato = null;

		log.debug("Il servizio di importazione movimento e' stato chiamato");
		InviaMigrazioni importaComunicazione = new InviaMigrazioni();
		try {
			movimento = preparaSourceBean(parametri);
			log.debug(movimento.toString());

			log.info("SILERService: comunicazione obbligatoria: codice comunicazione=" + this.codiceComunicazione);
			// 12/06/2008 savino: aggiunto parametro oggetto tx connection
			SourceBean result = importaComunicazione.inserisciMovimento(movimento, user, "MOVIMENTO", null);
			if (result.containsAttribute("RESPONSE") && ((String) result.getAttribute("RESPONSE")).equals("OK")) {
				// System.out.println("Ho inserito il movimento in AM_MOVIMENTO_APPOGGIO");
				log.debug("movimento importato con successo");
				RispostaXML risposta = new RispostaXML("101", "Ok", "I");
				risultato = risposta.toXMLString();
			} else {
				// System.out.println("DEVO inserire il movimento in CA_MIGRAZIONI_SCARTATE");
				log.fatal(
						"Impossibile inserire la migrazione/comunicazione obbligatoria nella tabella di appoggio. Si tenta di inserirla nella tabella degli scarti. "
								+ result.toString());
				log.fatal("Errore Importazione Comunicazione: " + parametri.toXMLString());
				log.fatal("Errore Importazione Comunicazione: " + parametri.getDati());
				String errorCode = (String) result.getAttribute("MESSAGECODE");
				String errorMsg = MessageBundle.getMessage(errorCode);
				log.fatal("Errore nell'importazione del movimento. Errore=" + errorMsg);
				movimento = preparaSourceBean(parametri);
				importaComunicazione.insMovimentoScartato(poloMittente, movimento, errorMsg);
				RispostaXML risposta = new RispostaXML("999",
						"Impossibile importare il movimento. Inserito nella tabella degli scarti.", "E");
				risultato = risposta.toXMLString();
			}

		} catch (Exception e) {
			log.fatal("Errore Importazione Comunicazione: ", e);
			log.fatal("Errore Importazione Comunicazione: " + parametri.toXMLString());
			log.fatal("Errore Importazione Comunicazione: " + parametri.getDati());
			RispostaXML risposta = new RispostaXML("999",
					"Errore durante l'esecuzione del servizio di importazione  del movimento. " + e.getMessage(), "E");
			risultato = risposta.toXMLString();
		}
		return risultato;
	}

	/**
	 * @param parametri
	 * @return
	 */
	private SourceBean preparaSourceBean(Execute parametri) throws Exception {
		String dati = parametri.getDati();
		SourceBean sb = SynchronizedSourceBean.fromXMLStringSynch(dati);
		Vector v = sb.getAttributeAsVector("MOVIMENTO");
		for (int i = 0; i < v.size(); i++) {
			((SourceBean) v.get(i)).setAttribute("CODMONOPROV", "C");
			((SourceBean) v.get(i)).setAttribute("CONTEXT", "importa");
		}
		this.codiceComunicazione = (String) ((SourceBean) v.get(0)).getAttribute("CODCOMUNICAZIONE");
		return sb;
	}

}
