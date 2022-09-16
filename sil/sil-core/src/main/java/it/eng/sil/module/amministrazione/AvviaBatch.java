package it.eng.sil.module.amministrazione;

import java.util.Arrays;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.batch.mdb.BatchMDBHelper;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

public class AvviaBatch extends AbstractSimpleModule {

	private static final long serialVersionUID = 2921871712645467755L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvviaBatch.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		String comando = "";
		String className = "";
		String data1 = "";
		String data2 = "";
		String datainsda = "";
		String datainsa = "";
		String numMaxMesi = "";
		String numLavDaProcessare = "";
		Integer tipoBatch = null;
		String params[] = new String[2];

		User objUser = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String user = RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_").toString();

		int cdnProfilo = objUser.getCdnProfilo();
		int cdnGruppo = objUser.getCdnGruppo();

		data1 = request.containsAttribute("DATABATCH1") ? (String) request.getAttribute("DATABATCH1") : "";
		data2 = request.containsAttribute("DATABATCH2") ? (String) request.getAttribute("DATABATCH2") : "";
		datainsda = (request.containsAttribute("DATAINSDA") && !request.getAttribute("DATAINSDA").toString().equals(""))
				? (String) request.getAttribute("DATAINSDA")
				: "00/00/0000";
		datainsa = (request.containsAttribute("DATAINSA") && !request.getAttribute("DATAINSA").toString().equals(""))
				? (String) request.getAttribute("DATAINSA")
				: "00/00/0000";
		String dataSiferDa = StringUtils.getAttributeStrNotNull(request, "DATASIFERDA");
		String dataSiferA = StringUtils.getAttributeStrNotNull(request, "DATASIFERA");
		String codiceFiscale = StringUtils.getAttributeStrNotNull(request, "codiceFiscale");
		tipoBatch = Integer.valueOf(request.getAttribute("tipoBatch").toString());
		numMaxMesi = request.containsAttribute("numMaxMesi") ? (String) request.getAttribute("numMaxMesi") : "";
		numLavDaProcessare = request.containsAttribute("numLavDaProcessare") ? (String) request.getAttribute("numLavDaProcessare") : "";

		boolean nonTrattati = request.containsAttribute("nonTrattati")
				&& request.getAttribute("nonTrattati").equals("S");
		boolean forzaInserimento = request.containsAttribute("FORZA_INSERIMENTO")
				&& request.getAttribute("FORZA_INSERIMENTO").equals("S");
		boolean sOccAnnoBatchAnni = request.containsAttribute("SoccAnno")
				&& request.getAttribute("SoccAnno").equals("S");

		switch (tipoBatch.intValue()) {
		case 0:
			// Batch per le cessazioni
			className = comando = "it.eng.sil.util.amministrazione.impatti.CessazioniGiornaliere";// "avviaBatch.bat";
																									// // MDB OK
			break;
		case 1:
			// Batch per i movimenti con data futura
			className = comando = "it.eng.sil.util.amministrazione.impatti.MovimentiGiornalieri"; // MDB OK
			break;
		case 2:
			// Batch per i tirocini senza contartto
			className = comando = "it.eng.sil.util.amministrazione.impatti.BatchSenzaContratto"; // MDB OK: non è
																									// richiamabile
																									// puntualmente da
																									// pagina e non
																									// esiste nemmeno il
																									// .bat per avvio
																									// massivo. Quindi
																									// non si è
																									// implementato il
																									// timer
			break;
		case 3:
			// comando = "it.eng.sil.util.batch.BatchLauncher
			// it.eng.sil.util.amministrazione.impatti.BatchFineValiditaCurriculum";
			className = comando = "it.eng.sil.util.amministrazione.impatti.BatchFineValiditaCurriculum"; // MDB OK
			break;
		case 4:
			// comando = "it.eng.sil.util.batch.BatchLauncher
			// it.eng.sil.util.amministrazione.impatti.BatchInizioValiditaCurriculum";
			className = comando = "it.eng.sil.util.amministrazione.impatti.BatchInizioValiditaCurriculum"; // MDB OK
			break;
		case 5:
			// Batch per la fine della mobilità
			className = comando = "it.eng.sil.util.amministrazione.impatti.BatchFineMobilita"; // MDB OK
			nonTrattati = false;
			break;
		case 6:
			// Batch per movimenti a cavallo di anni
			className = comando = "it.eng.sil.util.amministrazione.impatti.BatchImpattiTraAnni"; // MDB OK
			break;
		case 7:
			// Batch per l'aggiornamento della validità curriculum dei
			// lavoratori
			className = comando = "it.eng.sil.module.presel.BatchAggiornamentoValiditaCurriculum"; // Classe non
																									// presente...
			break;
		case 8:
			// Batch per la cancellazione di movimenti doppi
			className = comando = "it.eng.sil.util.batch.BatchCancellaMovAppErrore"; // MDB OK
			break;
		case 9:
			// Batch per la chiusura delle iscrizioni CM per superamento età
			className = comando = "it.eng.sil.util.batch.BatchChiusuraCmSuperamentoEta"; // MDB OK
			comando = comando + " " + data2 + " " + user + " " + cdnProfilo + " " + cdnGruppo;
			break;
		case 10:
			// Batch per la chiusura delle iscrizioni CM per superamento età
			className = comando = "it.eng.sil.util.batch.BatchInvioWsSifer"; // MDB OK
			break;
		case 11:
			// Batch di aggiornamento degli avvii a selezione consultando gli avviamenti nei movimenti
			className = comando = "it.eng.sil.util.batch.BatchAvvASelezioni"; // MDB OK
			break;
		case 12:
			// Batch per fine sospensioni decreto 150
			className = comando = "it.eng.sil.util.batch.BatchFineSospensione";// "avviaBatchFineSospensione.bat"; //
																				// MDB OK
			nonTrattati = false;
			break;
		case 13:
			// Batch ricalcola impatti
			className = comando = "it.eng.sil.util.batch.BatchRicalcolaImpatti"; // MDB OK
			break;
		default:
			// Errore nella scelta
			break;
		}

		switch (tipoBatch.intValue()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 12:
			params[0] = data1;
			params[1] = data2;
			comando = impostaParametri(comando, params);
			if (nonTrattati) {
				comando = comando + " nonTrattati";
			} else {
				comando = comando + " ancheTrattati";
			}

			// per i batch cessazioni, i movimenti con data futura, e fine mobilità
			// mi serve il profilo user e il gruppo user
			// per determinare se l'utente ha la competenza sul lavoratore per far
			// scattare gli impatti
			if (tipoBatch.intValue() == 0 || tipoBatch.intValue() == 1 || tipoBatch.intValue() == 5
					|| tipoBatch.intValue() == 6 || tipoBatch.intValue() == 12) {
				comando = comando + " " + String.valueOf(forzaInserimento) + " " + user + " " + cdnProfilo + " "
						+ cdnGruppo;
				if (tipoBatch.intValue() == 6) {
					comando = comando + " " + String.valueOf(sOccAnnoBatchAnni);
				}
			} else if (tipoBatch.intValue() == 7) {
				comando = comando + " " + user + " " + cdnProfilo + " " + cdnGruppo;
			} else {
				comando = comando + " " + String.valueOf(forzaInserimento) + " " + user;
			}

			break;

		case 8:
		case 9:
			params[0] = "00/00/0000";
			params[1] = "00/00/0000";
			comando = impostaParametri(comando, params);

			comando = comando + " X X X X 50133";
			params[0] = datainsda;
			params[1] = datainsa;

			comando = impostaParametri(comando, params);

			comando = comando + " 365";

			comando = comando + " " + user + " " + cdnProfilo + " " + cdnGruppo;
			break;
		case 10:
			comando = comando + " " + dataSiferDa + " " + dataSiferA + " " + codiceFiscale;
			break;
		case 11:
			comando = comando + " " + numMaxMesi + " " + user + " " + cdnProfilo + " " + cdnGruppo;
			break;
		case 13:
			comando = comando + " " + "pagina" + " " + numLavDaProcessare + " " + user + " " + cdnProfilo + " " + cdnGruppo;
			break;
		}

		/*
		 * if((tipoBatch.intValue() == 9)){ comando = comando + " " + data2 + " " + user + " " + cdnProfilo + " " +
		 * cdnGruppo; }else{
		 * 
		 * if( (tipoBatch.intValue() != 8) && (tipoBatch.intValue() != 9) ){ params[0] = data1; params[1] = data2;
		 * comando = impostaParametri(comando, params); if (nonTrattati) { comando = comando + " nonTrattati"; } else {
		 * comando = comando + " ancheTrattati"; }
		 * 
		 * // per i batch cessazioni, i movimenti con data futura, e fine mobilità // mi serve il profilo user e il
		 * gruppo user // per determinare se l'utente ha la competenza sul lavoratore per far // scattare gli impatti if
		 * (tipoBatch.intValue() == 0 || tipoBatch.intValue() == 1 || tipoBatch.intValue() == 5 || tipoBatch.intValue()
		 * == 6) { comando = comando + " " + String.valueOf(forzaInserimento) + " " + user + " " + cdnProfilo + " " +
		 * cdnGruppo; if (tipoBatch.intValue() == 6) { comando = comando + " " + String.valueOf(sOccAnnoBatchAnni); } }
		 * else if (tipoBatch.intValue() == 7) { comando = comando + " " + user + " " + cdnProfilo + " " + cdnGruppo; }
		 * else { comando = comando + " " + String.valueOf(forzaInserimento) + " " + user; } }else{
		 * 
		 * params[0] = "00/00/0000"; params[1] = "00/00/0000"; comando = impostaParametri(comando, params);
		 * 
		 * comando = comando + " X X X X 50133"; params[0] = datainsda; params[1] = datainsa;
		 * 
		 * comando = impostaParametri(comando, params);
		 * 
		 * comando = comando + " 365";
		 * 
		 * comando = comando + " " + user + " " + cdnProfilo + " " + cdnGruppo;
		 * 
		 * } }
		 */

		response.setAttribute("tipoBatch", tipoBatch);
		response.setAttribute("DATABATCH1", data1);
		response.setAttribute("DATABATCH2", data2);
		response.setAttribute("DATAINSDA", datainsda.equals("00/00/0000") ? "" : datainsda);
		response.setAttribute("DATAINSA", datainsa.equals("00/00/0000") ? "" : datainsa);
		response.setAttribute("DATASIFERDA", dataSiferDa);
		response.setAttribute("DATASIFERA", dataSiferA);
		response.setAttribute("codiceFiscale", codiceFiscale);
		response.setAttribute("FORZA_INSERIMENTO", String.valueOf(forzaInserimento));
		response.setAttribute("SoccAnno", String.valueOf(sOccAnnoBatchAnni));
		response.setAttribute("numMaxMesi", numMaxMesi);
		response.setAttribute("numLavDaProcessare", numLavDaProcessare);

		try {
			List<Class<?>> interfaces = Arrays.asList(Class.forName(className).getInterfaces());

			if (interfaces.contains(IBatchMDBConsumer.class)) {
				// La classe implementa l'interfaccia IBatchMDBConsumer, è possibile produrre il messaggio
				BatchMDBHelper batchHelper = new BatchMDBHelper();
				batchHelper.enqueue(new BatchObject(comando, true));
			} else {
				_logger.error("*** La classe non implementa l'interfaccia IBatchMDBConsumer!!! E' necessario che "
						+ className + " implementi l'interfaccia IBatchMDBConsumer per poter essere istanziata ***");
			}
		} catch (Exception ex) {
			// Errore nell'avviamneto dell'applicazione
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'avviamento del batch", ex);

			MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_FAIL);
		}
		MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_SUCCESS);

	}

	/**
	 * Metodo per l'impostazione dei parametri da passare alla classe
	 */
	public String impostaParametri(String com, String[] params) {
		return com + " " + params[0] + " " + params[1];
	}
}