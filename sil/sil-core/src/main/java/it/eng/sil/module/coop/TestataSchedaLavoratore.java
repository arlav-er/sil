/*
 * Created on May 31, 2006
 *
 */
package it.eng.sil.module.coop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.message.MessageBundle;

import it.eng.sil.security.User;
import it.eng.sil.util.Linguetta;
import it.eng.sil.util.Linguette;
import it.eng.sil.util.Utils;

/**
 * @author savino
 * 
 */
public class TestataSchedaLavoratore extends AbstractModule {

	public static final String TESTATA_SCHEDA_LAVORATORE = "TESTATA";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		SourceBean schedaLav = (SourceBean) getRequestContainer().getSessionContainer()
				.getAttribute(GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
		if (schedaLav == null)
			return;
		// formato: <response><nome_modulo><rows><row>.....
		// riprendo la lista della pages coinvolte
		// ad ogni page e' associato il modulo informativo
		// se la page e' tra quelle della linguetta ed esistono informazioni per
		// quel modulo riprendo l'etichetta
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
		Linguette l = new Linguette(user, Integer.parseInt(cdnFunzione), "CoopAnagDatiPersonaliPage",
				new BigDecimal(0));
		List linguette = l.getLinguette();
		SourceBean testata = new SourceBean("TESTATA");
		SourceBean par = null;
		Vector errors = schedaLav.getAttributeAsVector("ERRORS.USER_ERROR");
		boolean esistonoErrori = errors.size() > 0;
		SourceBean sezioneErrori = new SourceBean("ERRORI");
		AccessoSchedaLavoratore accessoSL = new AccessoSchedaLavoratore();
		HashMap moduloSezioni = accessoSL.keyDettaglioModuli(); // caricaModuloSezioni();
		for (int i = 0; i < linguette.size(); i++) {
			Linguetta linguetta = (Linguetta) linguette.get(i);
			if (Utils.notNull(linguetta.getStrpage()).equals("")) {
				par = new SourceBean("PARAGRAFO");
				par.setAttribute("nome", linguetta.getStrdescliv());
				testata.setAttribute(par);
				continue;
			}
			// TODO Savino: gestire errore se si verifica un errore
			String keyDettaglioModulo = (String) moduloSezioni.get(linguetta.getStrpage().toUpperCase());
			if (schedaLav.getAttributeAsVector("SERVICE_RESPONSE." + keyDettaglioModulo).size() > 0)
				par.setAttribute("sezioni", linguetta.getStrdescliv());
			// controlla che per questa informazione non si sia verificato un
			// errore.
			if (esistonoErrori) {
				SourceBean moduloBean = (SourceBean) schedaLav
						.getAttribute("SERVICE_RESPONSE." + accessoSL.getModuleSessionName(linguetta.getStrpage()));
				String errorID = (String) moduloBean.getAttribute("ERROR_ID");
				if (errorID != null) {
					String messaggio = codiceErroreDi(errorID, errors);
					SourceBean errore = new SourceBean("ERRORE");
					errore.setAttribute("generato_da", par.getAttribute("nome") + ": " + linguetta.getStrdescliv());
					errore.setAttribute("descrizione", "operazione fallita");
					sezioneErrori.setAttribute(errore);
				}
			}
		}
		// si riprendono i dati anagrafici del lavoratore dalla request o dal
		// modulo getLavoratoreIR
		SourceBean mod = null;
		SourceBean lavoratoreIR = (SourceBean) getResponseContainer().getServiceResponse()
				.getAttribute("M_COOP_GETLAVORATOREIR");
		// se nella response non e' presente il source bean del lavoratore
		// significa che siamo arrivati fin qui direttamente
		// dalla scheda lavoratore. I dati li trovero' nella serviceRequest.
		String provenienza;
		if (lavoratoreIR != null) {
			mod = (SourceBean) lavoratoreIR.getAttribute("ROWS.ROW");
			provenienza = "SCHDEDA_LAVORATORE";
		} else {
			mod = serviceRequest;
			provenienza = "LISTA_LAVORATORE";
		}
		String codiceFiscale = Utils.notNull(mod.getAttribute("strCodiceFiscale"));
		String cognome = Utils.notNull(mod.getAttribute("strCognome"));
		String nome = Utils.notNull(mod.getAttribute("strNome"));
		String dataNascita = Utils.notNull(mod.getAttribute("dataNascita"));
		String comuneNascita = Utils.notNull(mod.getAttribute("comNas"));
		String codProvinciaMaster = Utils.notNull(mod.getAttribute("codProvinciaMaster"));
		String provinciaMaster = Utils.notNull(mod.getAttribute("provinciaMaster"));
		String tipoMaster = Utils.notNull(mod.getAttribute("tipoMaster"));

		testata.setAttribute("strCodiceFiscale", codiceFiscale);
		testata.setAttribute("strCognome", cognome);
		testata.setAttribute("strNome", nome);
		testata.setAttribute("dataNascita", dataNascita);
		testata.setAttribute("comNas", comuneNascita);
		testata.setAttribute("codProvinciaMaster", codProvinciaMaster);
		testata.setAttribute("provinciaMaster", provinciaMaster);
		testata.setAttribute("tipoMaster", tipoMaster);
		testata.setAttribute("provenienza", provenienza);
		// ora si controlla che non si siano verificati errori.
		testata.setAttribute(sezioneErrori);
		schedaLav.setAttribute(testata);
	}

	private String codiceErroreDi(String errorID, Vector errors) {
		String messaggio = "";
		for (int i = 0; i < errors.size(); i++) {
			String AFErrorID = (String) ((SourceBean) errors.get(i)).getAttribute("ERROR_ID");
			if (AFErrorID.equals(errorID)) {
				messaggio = MessageBundle
						.getMessage((String) ((SourceBean) errors.get(i)).getAttribute(EMFUserError.USER_ERROR_CODE));
				break;
			}
		}
		return messaggio;
	}
}
