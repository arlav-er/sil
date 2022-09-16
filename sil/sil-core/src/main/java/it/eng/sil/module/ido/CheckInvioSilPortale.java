package it.eng.sil.module.ido;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;

import java.util.Vector;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

public class CheckInvioSilPortale extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse)
			throws Exception {
		ReportOperationResult ror = new ReportOperationResult(this, serviceResponse);
		disableMessageIdSuccess();
		disableMessageIdFail();
 		SourceBean rows = doSelect(serviceRequest, serviceResponse);
		if(rows!=null){
			
			//controllo per flag candidatura
			ResponseContainer responseCont = getResponseContainer();
			SourceBean servResponse = responseCont.getServiceResponse();
			
			boolean checkCandidatura = false;
			if (servResponse.containsAttribute("M_CONFIG_CANDID_PALE_VAC.ROWS.ROW.NUM")) {
				String config = servResponse.getAttribute("M_CONFIG_CANDID_PALE_VAC.ROWS.ROW.NUM").toString();
				if(config.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){
					checkCandidatura = true;
				}
			}
			
			Vector result = (Vector)rows.getAttributeAsVector("ROW");
			if(result.isEmpty()){
				ror.reportFailure(MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL);
			}
			else {
				boolean checkInvio = true;
				String dettaglioErrore = "";
				SourceBean sb = (SourceBean) result.get(0);

				String dataScadenza = StringUtils.getAttributeStrNotNull(sb, "DATSCADENZA");
				String dataScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(sb, "DATSCADENZAPUBBLICAZIONE");
				String codiceEvasione = StringUtils.getAttributeStrNotNull(sb, "CODEVASIONE");
				String statoRichiesta = StringUtils.getAttributeStrNotNull(sb, "CDNSTATORICH");
				String flgPubblicata = StringUtils.getAttributeStrNotNull(sb, "FLGPUBBLICATA");
				String flgPubblicataWeb = StringUtils.getAttributeStrNotNull(sb, "FLGPUBBWEB");
				String flgMansioneInvioCL = StringUtils.getAttributeStrNotNull(sb, "MFLGINVIOCL");
				String flgComuneInvioCL = StringUtils.getAttributeStrNotNull(sb, "CFLGINVIOCL");
				String strMansionePubb = StringUtils.getAttributeStrNotNull(sb, "STRMANSIONEPUBB");
				String txtFiguraProfessionale = StringUtils.getAttributeStrNotNull(sb, "TXTFIGURAPROFESSIONALE");
				String flagCandidatura = StringUtils.getAttributeStrNotNull(sb, "FLG_CANDIDATURA");
				String emailLinguettaQuattro = StringUtils.getAttributeStrNotNull(sb, "STREMAILRIFPUBB");
				String emailRifSare = StringUtils.getAttributeStrNotNull(sb, "emailRifSare");
				String emailRif = StringUtils.getAttributeStrNotNull(sb, "stremailriferimento");
				String nomeRifSare = StringUtils.getAttributeStrNotNull(sb, "nomeRifSare");
				String cognomeRifSare = StringUtils.getAttributeStrNotNull(sb, "cognomeRifSare");
				String nomeRif = StringUtils.getAttributeStrNotNull(sb, "strnomeriferimento");
				String cognomeRif = StringUtils.getAttributeStrNotNull(sb, "strcognomeriferimento");
								
				if(checkCandidatura && (codiceEvasione.equalsIgnoreCase("DPR") || codiceEvasione.equalsIgnoreCase("DFD")) && StringUtils.isEmptyNoBlank(flagCandidatura)){
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_PALESE_CANDIDATURA);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				
				if((codiceEvasione.equalsIgnoreCase("DPR") || codiceEvasione.equalsIgnoreCase("DFD")) && 
						(StringUtils.isEmptyNoBlank(emailLinguettaQuattro) && StringUtils.isEmptyNoBlank(emailRifSare) && StringUtils.isEmptyNoBlank(emailRif))){
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_PALESE_DATI_AZ_REF);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}else if(codiceEvasione.equalsIgnoreCase("DPR") || codiceEvasione.equalsIgnoreCase("DFD")){
					/*
					if((StringUtils.isEmptyNoBlank(nomeRifSare) && StringUtils.isEmptyNoBlank(cognomeRifSare)) &&
					 (StringUtils.isEmptyNoBlank(nomeRif) && StringUtils.isEmptyNoBlank(cognomeRif))){
						checkInvio = false;
						EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_PALESE_DATI_AZ_REF);
						dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
					 }
					*/
					if((StringUtils.isEmptyNoBlank(nomeRifSare) || StringUtils.isEmptyNoBlank(cognomeRifSare) || StringUtils.isEmptyNoBlank(emailRifSare))){ 											
					  if((StringUtils.isEmptyNoBlank(nomeRif) || StringUtils.isEmptyNoBlank(cognomeRif) || StringUtils.isEmptyNoBlank(emailRif))){
						checkInvio = false;
						EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_PALESE_DATI_AZ_REF);
						dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
					  }
					}					
				}
				
				String dataOggi = DateUtils.getNow();
				if (DateUtils.compare(dataScadenza, dataOggi) < 0) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_SCADUTA);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (DateUtils.compare(dataScadenzaPubblicazione, dataOggi) < 0) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_PUBB_SCADUTA);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (!codiceEvasione.equalsIgnoreCase("DFA") && !codiceEvasione.equalsIgnoreCase("DFD") &&
						!codiceEvasione.equalsIgnoreCase("DPR") && !codiceEvasione.equalsIgnoreCase("DRA")) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_MOD_EVASIONE);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (!statoRichiesta.equalsIgnoreCase("1") && !statoRichiesta.equalsIgnoreCase("2") && !statoRichiesta.equalsIgnoreCase("3") &&
						!statoRichiesta.equalsIgnoreCase("4") && !statoRichiesta.equalsIgnoreCase("5")) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_STATO_EVASIONE);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (!flgPubblicata.equalsIgnoreCase(Values.FLAG_TRUE)) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_NO_PUBBLICATA);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (!flgPubblicataWeb.equalsIgnoreCase(Values.FLAG_TRUE)) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_PUBB_WEB);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (!flgMansioneInvioCL.equalsIgnoreCase(Values.FLAG_TRUE)) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_INVIO_MANSIONE);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (!flgComuneInvioCL.equalsIgnoreCase(Values.FLAG_TRUE)) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_INVIO_COMUNE);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (strMansionePubb.equals("")) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_MANSIONE);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}
				if (txtFiguraProfessionale.equals("")) {
					checkInvio = false;
					EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL_CONTENUTO_LAVORO);
					dettaglioErrore = dettaglioErrore.equals("")?error.getDescription():dettaglioErrore + "<br>" + error.getDescription();
				}

				if (checkInvio) {
					serviceResponse.setAttribute("CHECKINVIO", "OK");
				}
				else {
					ror.reportFailure(MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL, true, dettaglioErrore);		
				}
			}
		}
		else {
			ror.reportFailure(MessageCodes.IDO.ERR_CHECK_VACANCY_SIL_PORTAL);
		}
	}

}
