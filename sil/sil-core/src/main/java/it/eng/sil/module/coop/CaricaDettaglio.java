/*
 * Created on May 29, 2006
 *s
 */
package it.eng.sil.module.coop;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.RequestContextIFace;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

/**
 * Carica il dettaglio di tutte le informazioni estratte dalla lista di quella informazione. Dalla lista si preleva il
 * valore della chiave e la si usa nella request che verra' passata al modulo di caricamento del dettaglio.
 * 
 * Parametri obbligatori: 1. CONFIG.MODULO_DETTAGLIO.nome e' il nome del modulo che deve essere istanziato e che va a
 * leggere il dettaglio dell'informazione. 2. CONFIG.MODULO_DETTAGLIO.lista e' il nome del modulo che contiene la lista
 * estratta: Il modulo DEVE essere eseguito prima, e deve essere passato tramite consequences al modulo che implementa
 * questa classe. 3. CONFIG.MODULO_DETTAGLIO.KEY.NOME_LISTA e' il nome di una delle chiavi (se ve ne sono piu' di una)
 * con cui reperire il valore dalla lista. 4. CONFIG.MODULO_DETTAGLIO.KEY.NOME_DETTAGLIO e' il nome di una delle chiavi
 * (se ve ne sono piu' di una) che il modulo del dettaglio si attende nella serviceRequest.
 * 
 * Esempio: <MODULE name="M_CoopCaricaDettaglioForPro" class="it.eng.sil.module.coop.CaricaDettaglio">
 * <CONFIG pool="SIL_DATI" title=""> <MODULO_DETTAGLIO nome="M_CoopLoadForPro" lista="M_CoopListForPro">
 * <KEY NOME_LISTA="PRGCORSO" NOME_DETTAGLIO="PRGCORSO_NEW"/>
 * <KEY NOME_LISTA="CDNLAVORATORE" NOME_DETTAGLIO="CDNLAVORATORE_EX"/> </MODULO_DETTAGLIO> </CONFIG> </MODULE> In questo
 * esempio abbiamo un modulo (M_CoopLoadForPro) la cui query si attende due parametri. Nella lista si chiamano prgCorso
 * e cdnLavoratore, ma nella serviceRequest che bisognera' passare al modulo CoopLoadForPro i valori recuperati si
 * devono inserire con le chiavi prgCorso_new e cdnLavoratore_ex.
 * 
 * Il modulo che verra' istanziato e che restituira' il dettaglio:
 * <MODULE name="M_CoopLoadForPro" class="it.eng.sil.module.presel.SelectForPro"> <CONFIG pool="SIL_DATI" title="">
 * <QUERY statement="COOP_SELECT_FOR_PRO"> <PARAMETER scope="SERVICE_REQUEST" type="RELATIVE" value="PRGCORSO_NEW"/>
 * <PARAMETER scope="SERVICE_REQUEST" type="RELATIVE" value="CDNLAVORATORE_EX"/> </QUERY> </CONFIG> </MODULE>
 * 
 * @author savino
 */
public class CaricaDettaglio extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		SourceBean serviceResponseDellaPage = getResponseContainer().getServiceResponse();
		SourceBean dettaglio = (SourceBean) getConfig().getAttribute("MODULO_DETTAGLIO");
		SourceBean lista = null, row = null;
		ModuleIFace modulo = null;
		String moduloDettaglio = (String) dettaglio.getAttribute("nome");
		String moduloLista = (String) dettaglio.getAttribute("lista");
		lista = (SourceBean) serviceRequest.getAttribute(moduloLista);
		if (lista != null) {
			Vector keys = dettaglio.getAttributeAsVector("key");
			Vector v = lista.getAttributeAsVector("ROWS.ROW");
			SourceBean _request = (SourceBean) serviceRequest.cloneObject();
			for (int i = 0; i < v.size(); i++) {
				row = (SourceBean) v.get(i);
				for (int j = 0; j < keys.size(); j++) {
					SourceBean key = (SourceBean) keys.get(j);
					String nomeLista = (String) key.getAttribute("NOME_LISTA");
					String nomeDettaglio = (String) key.getAttribute("NOME_DETTAGLIO");
					Object listaValue = row.getAttribute(nomeLista);
					_request.updAttribute(nomeDettaglio, Utils.notNull(listaValue));
				}
				modulo = ModuleFactory.getModule(moduloDettaglio);
				this.getRequestContainer().setServiceRequest(_request);
				((RequestContextIFace) modulo).setRequestContext(this);
				SourceBean _response = new SourceBean(moduloDettaglio);
				modulo.service(_request, _response);
				serviceResponseDellaPage.setAttribute(_response);
			}
		} else {
			SourceBean moduloVuoto = new SourceBean(moduloDettaglio);
			moduloVuoto.setAttribute(new SourceBean("ROWS"));
			serviceResponseDellaPage.setAttribute(moduloVuoto);
		}

	}

}
