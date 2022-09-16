package it.eng.sil.module.ido;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.dispatching.module.impl.DynamicStatementListModule;

/*
 * Modulo che effettua la select della lista dei candidati aderenti alla grqduatoria
 * 
 * la particolarità di questo modulo sta nel fatto che viene chiamato
 * prima della select il metodo che permette di inserire in un vettore 
 * recuperato dalla session tutti i cdnLavoratori dei candidati selezionati
 * tramite il checkbox nella lista
 * 
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMCandidatiGraduatoria extends DynamicStatementListModule {

	public void service(SourceBean request, SourceBean response) {

		// recupero i prgNominativi checkkati e li inserisce in sessione
		// viene verificato anche se qualche checkbox è stato dececkato

		// metodo che utilizza il sourcebean in sessione
		// getNominativiSelezionati(request, response);

		// metodo che in sessione setta un vettore (verifico se è migliore rispetto al SB)
		getNominativiSelezionatiVector(request, response);

		super.service(request, response);

	}

	/*
	 * metodo che si occupa di inserire nel SuorceBean in sessione i candidati checkati presenti nella ServiceRequest
	 * vengono eliminati da SourceBean quei candidati che sono stati dechekkati che sono presenti nell'attributo
	 * ARRAY_CDNLAVORATORE
	 * 
	 */
	public void getNominativiSelezionati(SourceBean request, SourceBean response) {

		ResponseContainer responseContainer = getResponseContainer();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");
		String keySB = numRichiesta + "_" + prgTipoIncrocio;
		SourceBean nominativiSB = (SourceBean) sessionContainer.getAttribute(keySB);
		if (nominativiSB == null) {
			try {
				nominativiSB = new SourceBean("CANDIDATI_SEL");
			} catch (SourceBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Vector attrsRequest = request.getContainedAttributes();
		for (int i = 0; i < attrsRequest.size(); i++) {
			SourceBeanAttribute attr = (SourceBeanAttribute) attrsRequest.get(i);
			String keyAttr = attr.getKey();
			if (keyAttr.length() >= 3) {
				if (keyAttr.substring(0, 3).equalsIgnoreCase("CK_")) {
					String ckSel = (String) request.getAttribute(keyAttr);

					String valueCdnLavoratore = null;

					if (ckSel != null) {
						valueCdnLavoratore = ckSel;

						// lo inserisco in un sourcebean
						CMCandidatiGraduatoriaUtil.insertNominativoSB(valueCdnLavoratore, nominativiSB);
					}
				}
			}
		}

		// verifico se l'utente mi ha dechekkato qualche nominativo
		SourceBean newNominativiSB = null;
		try {
			newNominativiSB = new SourceBean("CANDIDATI_SEL");
			Vector newRows = CMCandidatiGraduatoriaUtil.deleteNominativoSB(request, nominativiSB);
			for (int i = 0; i < newRows.size(); i++) {
				SourceBean newRow = (SourceBean) newRows.get(i);
				newNominativiSB.setAttribute(newRow);
			}
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// setta il SB in sessione con la medesima key
		sessionContainer.setAttribute(keySB, newNominativiSB);
	}

	/*
	 * metodo che si occupa di inserire nel vettore in sessione i candidati checkati presenti nella ServiceRequest
	 * vengono eliminati dal Vector quei candidati che sono stati dechekkati che sono presenti nell'attributo
	 * ARRAY_CDNLAVORATORE
	 * 
	 */
	public void getNominativiSelezionatiVector(SourceBean request, SourceBean response) {

		ResponseContainer responseContainer = getResponseContainer();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		String prgTipoIncrocio = (String) request.getAttribute("PRGTIPOINCROCIO");
		String keySB = numRichiesta + "_" + prgTipoIncrocio;
		Vector nominativiVector = (Vector) sessionContainer.getAttribute(keySB);
		if (nominativiVector == null) {
			nominativiVector = new Vector();
		}

		Vector attrsRequest = request.getContainedAttributes();
		for (int i = 0; i < attrsRequest.size(); i++) {
			SourceBeanAttribute attr = (SourceBeanAttribute) attrsRequest.get(i);
			String keyAttr = attr.getKey();
			if (keyAttr.length() >= 3) {
				if (keyAttr.substring(0, 3).equalsIgnoreCase("CK_")) {
					Object objCh = request.getAttribute(keyAttr);
					String ckSel = null;
					if (objCh instanceof Vector) {
						ckSel = (String) ((Vector) objCh).get(0);
					} else {
						ckSel = (String) request.getAttribute(keyAttr);
					}

					String valueCdnLavoratore = null;

					if (ckSel != null) {
						valueCdnLavoratore = ckSel;

						// lo inserisco in un Vector
						ASCandidatiGraduatoriaUtil.insertNominativoVector(valueCdnLavoratore, nominativiVector);
					}
				}
			}
		}

		// verifico se l'utente mi ha dechekkato qualche nominativo
		Vector newNominativiVector = null;
		newNominativiVector = ASCandidatiGraduatoriaUtil.deleteNominativoVector(request, nominativiVector);

		// setta il Vector in sessione con la medesima key
		sessionContainer.setAttribute(keySB, newNominativiVector);
	}

}