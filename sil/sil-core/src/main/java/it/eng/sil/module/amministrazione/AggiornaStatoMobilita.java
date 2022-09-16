package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class AggiornaStatoMobilita extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		String codStato = null;
		int msgId = 0;

		String forzaAggStor = null;
		SourceBean codAttiva = null;
		SourceBean serviceResponse = getResponseContainer().getServiceResponse();
		String configurazioneMob = "0";
		if (serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")) {
			configurazioneMob = serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString();
		}
		forzaAggStor = (String) request.getAttribute("CONTINUA_AGGIORNAMENTO_STORICO");

		codStato = (String) request.getAttribute("codStatoMob");
		msgId = this.disableMessageIdSuccess();

		if (configurazioneMob.equals("0")) {
			if (forzaAggStor != null && !forzaAggStor.equals("true")) {
				try {
					// verifica se lo stato è impostato e se la mobilità è sospesa, in caso contrario chiedo
					// conferma per continuare l'operazione di inserimento
					if (codStato != null && !codStato.equals("")) {
						this.setSectionQuerySelect("GET_CODMONO_MOBILITA");
						codAttiva = doSelect(request, response);
						if (!codAttiva.getAttribute("ROW.CODMONOATTIVA").equals("S")) {
							throw new Exception("Situazione di incongruenza");
						}
					}
				} catch (Exception e) {
					addConfirm(request, response);
					return;
				}
			}
		}

		this.setMessageIdSuccess(msgId);

		doUpdate(request, response);

	}

	private void addConfirm(SourceBean request, SourceBean response) throws Exception {

		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		response.setAttribute(sb);
	}

}