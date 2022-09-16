package it.eng.myportal.rest.cliclavoro;

import it.eng.myportal.dtos.CvCandidaturaClDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.DeMotivoChiusuraDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.dtos.VaVacancyClDTO;
import it.eng.myportal.entity.home.CvCandidaturaClHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaVacancyClHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

@Stateless
@Path("/rest/cliclavoro")
public class GetDatiInvioCliclavoro {

	protected Log log = LogFactory.getLog(this.getClass());

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	CvCandidaturaClHome cvCandidaturaClHome;

	@EJB
	VaVacancyClHome vaVacancyClHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@GET
	@Path("getDatiInvioCliclavoroCV")
	@Produces("application/json; charset=UTF-8")
	public String getDatiInvioCliclavoroCV(@QueryParam("idCvDatiPersonali") Integer idCvDatiPersonali) {
		JSONObject jsonObject = new JSONObject();

		CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.findDTOById(idCvDatiPersonali);
		CvCandidaturaClDTO cvCandidaturaClDTO = cvCandidaturaClHome.findDTOById(idCvDatiPersonali);

		if (cvDatiPersonaliDTO != null && cvCandidaturaClDTO != null) {
			String codDeStatoInvioCl = cvCandidaturaClDTO.getDeStatoInvioCl().getId();
			String descrizioneDeStatoInvioCl = cvCandidaturaClDTO.getDeStatoInvioCl().getDescrizione();

			/*
			 * se lo stato di invio e' in uno stato di errore inserisco il
			 * corrispondente stato di attesa
			 */
			if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE)) {
				descrizioneDeStatoInvioCl = deStatoInvioClHome.findById(
				        ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO).getDescrizione();
			} else if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.VARIAZIONE_INVIATA_ERRORE)) {
				descrizioneDeStatoInvioCl = deStatoInvioClHome.findById(
				        ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE).getDescrizione();
			} else if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE)) {
				descrizioneDeStatoInvioCl = deStatoInvioClHome.findById(
				        ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA).getDescrizione();
			}

			DeMotivoChiusuraDTO deMotivoChiusuraDTO = cvDatiPersonaliDTO.getMotivoChiusura();
			String descrizioneMotivoChiusura = "";
			if (deMotivoChiusuraDTO != null) {
				descrizioneMotivoChiusura = deMotivoChiusuraDTO.getDescrizione();
			}

			String dtInvio = new SimpleDateFormat("dd/MM/yyyy").format(cvCandidaturaClDTO.getDtInvio());
			String dtScadenza = new SimpleDateFormat("dd/MM/yyyy").format(cvDatiPersonaliDTO.getDtScadenza());

			try {
				/* costruisco il JSON coi dati */
				jsonObject.put("codComunicazione", cvCandidaturaClDTO.getCodComunicazione());
				jsonObject.put("codComunicazionePrec", cvCandidaturaClDTO.getCodComunicazionePrec());
				jsonObject.put("motivoChiusura", descrizioneMotivoChiusura);
				jsonObject.put("statoInvio", descrizioneDeStatoInvioCl);
				jsonObject.put("dtInvio", dtInvio);
				jsonObject.put("dtScadenza", dtScadenza);
			} catch (JSONException e) {
				return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
			}
		} else {
			/* mando un messagio di errore */
			return Utils.buildErrorResponse("Nessun dato trovato").toString();
		}

		return jsonObject.toString();
	}

	@GET
	@Path("getDatiInvioCliclavoroVA")
	@Produces("application/json; charset=UTF-8")
	public String getDatiInvioCliclavoroVA(@QueryParam("idVaDatiVacancy") Integer idVaDatiVacancy) {
		JSONObject jsonObject = new JSONObject();

		VaDatiVacancyDTO vaDatiVacancyDTO = vaDatiVacancyHome.findDTOById(idVaDatiVacancy);
		VaVacancyClDTO vaVacancyClDTO = vaVacancyClHome.findDTOById(idVaDatiVacancy);

		if (vaDatiVacancyDTO != null && vaVacancyClDTO != null) {
			String codDeStatoInvioCl = vaVacancyClDTO.getDeStatoInvioCl().getId();
			String descrizioneDeStatoInvioCl = vaVacancyClDTO.getDeStatoInvioCl().getDescrizione();

			/*
			 * se lo stato di invio e' in uno stato di errore inserisco il
			 * corrispondente stato di attesa
			 */
			if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE)) {
				descrizioneDeStatoInvioCl = deStatoInvioClHome.findById(
				        ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO).getDescrizione();
			} else if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.VARIAZIONE_INVIATA_ERRORE)) {
				descrizioneDeStatoInvioCl = deStatoInvioClHome.findById(
				        ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE).getDescrizione();
			} else if (codDeStatoInvioCl.equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE)) {
				descrizioneDeStatoInvioCl = deStatoInvioClHome.findById(
				        ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA).getDescrizione();
			}

			DeMotivoChiusuraDTO deMotivoChiusuraDTO = vaDatiVacancyDTO.getMotivoChiusura();
			String descrizioneMotivoChiusura = "";
			if (deMotivoChiusuraDTO != null) {
				descrizioneMotivoChiusura = deMotivoChiusuraDTO.getDescrizione();
			}

			String dtInvio = new SimpleDateFormat("dd/MM/yyyy").format(vaVacancyClDTO.getDtInvio());
			String dtScadenza = new SimpleDateFormat("dd/MM/yyyy").format(vaDatiVacancyDTO.getDtScadenza());

			try {
				/* costruisco il JSON coi dati */
				jsonObject.put("codComunicazione", vaVacancyClDTO.getCodComunicazione());
				jsonObject.put("codComunicazionePrec", vaVacancyClDTO.getCodComunicazionePrec());
				jsonObject.put("motivoChiusura", descrizioneMotivoChiusura);
				jsonObject.put("statoInvio", descrizioneDeStatoInvioCl);
				jsonObject.put("dtInvio", dtInvio);
				jsonObject.put("dtScadenza", dtScadenza);
			} catch (JSONException e) {
				return Utils.buildErrorResponse("Errore durante il recupero dei dati").toString();
			}
		} else {
			/* mando un messagio di errore */
			return Utils.buildErrorResponse("Nessun dato trovato").toString();
		}

		return jsonObject.toString();
	}
}
