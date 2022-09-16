package it.eng.myportal.ws.mysap;

import it.eng.myportal.dtos.CvAgevolazioneDTO;
import it.eng.myportal.dtos.CvAlboDTO;
import it.eng.myportal.dtos.CvAltreInfoDTO;
import it.eng.myportal.dtos.CvCompetenzeTrasvDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.CvEsperienzeProfDTO;
import it.eng.myportal.dtos.CvFormazioneDTO;
import it.eng.myportal.dtos.CvInformaticaDTO;
import it.eng.myportal.dtos.CvIstruzioneDTO;
import it.eng.myportal.dtos.CvLinguaDTO;
import it.eng.myportal.dtos.CvPatenteDTO;
import it.eng.myportal.dtos.CvPatentinoDTO;
import it.eng.myportal.dtos.CvProfDesiderateDTO;
import it.eng.myportal.dtos.GenericFiltroDecodeDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.CvAgevolazioneHome;
import it.eng.myportal.entity.home.CvAlboHome;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.CvCompetenzeTrasvHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.CvFormazioneHome;
import it.eng.myportal.entity.home.CvInformaticaHome;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.CvPatenteHome;
import it.eng.myportal.entity.home.CvPatentinoHome;
import it.eng.myportal.entity.home.CvProfDesiderateHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.ws.mysap.pojo.CurriculumVitaeFull;
import it.eng.myportal.ws.mysap.pojo.CurriculumVitaeHeader;
import it.eng.myportal.ws.mysap.pojo.MySapWsException;
import it.eng.myportal.ws.mysap.pojo.NewCvAltreInfoDTO;
import it.eng.myportal.ws.mysap.pojo.NewCvProfDesiderateDTO;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
public class MySapEjb {
	protected static Log log = LogFactory.getLog(MySapEjb.class);

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private CvFormazioneHome cvFormazioneHome;

	@EJB
	CvAgevolazioneHome cvAgevolazioneHome;

	@EJB
	private CvAlboHome cvAlboHome;

	@EJB
	private CvCompetenzeTrasvHome cvCompetenzeTrasvHome;

	@EJB
	private CvEsperienzeProfHome cvEsperienzeProfHome;

	@EJB
	private CvIstruzioneHome cvIstruzioneHome;

	@EJB
	private CvLinguaHome cvLinguaHome;

	@EJB
	private CvPatenteHome cvPatenteHome;

	@EJB
	private CvPatentinoHome cvPatentinoHome;

	@EJB
	private CvProfDesiderateHome cvProfDesiderateHome;

	@EJB
	private CvAltreInfoHome cvAltreInfoHome;

	@EJB
	private CvInformaticaHome cvInformaticaHome;

	public List<CurriculumVitaeHeader> getCurriculaVitae(String username) throws MySapWsException {

		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
		if (pfPrincipal == null) {
			String msg = "Impossibile recuperare l'utente " + username;
			log.error(msg);
			throw new MySapWsException(1, msg);
		}

		List<CurriculumVitaeHeader> curriculaVitae = new ArrayList<CurriculumVitaeHeader>();

		List<CvDatiPersonali> listCv = cvDatiPersonaliHome.findByIdPfPrincipal(pfPrincipal.getIdPfPrincipal());
		for (CvDatiPersonali cvDatiPersonali : listCv) {
			if (!cvDatiPersonali.getFlagEliminato()) {
				CurriculumVitaeHeader cvh = new CurriculumVitaeHeader();
				Integer idCvDatiPersonali = cvDatiPersonali.getIdCvDatiPersonali();
				String descrizione = cvDatiPersonali.getDescrizione();
				

				cvh.setId(idCvDatiPersonali);
				cvh.setDescrizione(descrizione);
				cvh.setFlagEliminato(cvDatiPersonali.getFlagEliminato());
				cvh.setFlagInviato(cvDatiPersonali.getFlagInviato());

				curriculaVitae.add(cvh);
			}
		}

		return curriculaVitae;

	}

	public CurriculumVitaeFull getCurriculumVitae(int id) throws MySapWsException {
		CurriculumVitaeFull cvP = new CurriculumVitaeFull();

		CvDatiPersonaliDTO cvDto = cvDatiPersonaliHome.findDTOById(id);
		if (cvDto == null) {
			String msg = "Impossibile recuperare il CV con id= " + id;
			log.error(msg);
			throw new MySapWsException(2, msg);
		}

		List<CvFormazioneDTO> listaCvFormazione = cvFormazioneHome.findDTOByCurriculumId(id);

		List<CvAgevolazioneDTO> listaCvAgevolazioneDTO = cvAgevolazioneHome.findDTOByCurriculumId(id);

		List<CvAlboDTO> listaCvAlboDTO = cvAlboHome.findDTOByCurriculumId(id);
		CvCompetenzeTrasvDTO cvCompetenzeTrasvDTO = cvCompetenzeTrasvHome.findDTOByCurriculumId(id);

		// // I caso particolare
		CvAltreInfoDTO cvAltreInfoDTO = cvAltreInfoHome.findDTOByCurriculumId(id);
		NewCvAltreInfoDTO newCvAltreInfoDTO = new NewCvAltreInfoDTO();
		if (cvAltreInfoDTO != null) {
			newCvAltreInfoDTO.setAutomunito(cvAltreInfoDTO.getAutomunito());
			newCvAltreInfoDTO.setMotomunito(cvAltreInfoDTO.getMotomunito());

			List<IDecode> listaAgevolazioniDTO = cvAltreInfoDTO.getListaAgevolazioniDTO();

			List<GenericFiltroDecodeDTO> newListaAgevolazioni = retrieveListaDTO(listaAgevolazioniDTO);

			newCvAltreInfoDTO.setListaAgevolazioniDTO(newListaAgevolazioni);
			newCvAltreInfoDTO.setUlterioriInfo(cvAltreInfoDTO.getUlterioriInfo());
		}

		// // II caso particolare

		List<CvProfDesiderateDTO> listaCvProfDesiderateDTO = cvProfDesiderateHome.findDTOByCurriculumId(id);
		List<NewCvProfDesiderateDTO> newListaCvProfDesiderateDTO = new ArrayList<NewCvProfDesiderateDTO>();

		for (CvProfDesiderateDTO cvProfDesiderateDTO : listaCvProfDesiderateDTO) {
			NewCvProfDesiderateDTO newCvProfDesiderateDTO = new NewCvProfDesiderateDTO();

			newCvProfDesiderateDTO.setBreveDescrProfessione(cvProfDesiderateDTO.getBreveDescrProfessione());
			newCvProfDesiderateDTO.setDeMansione(cvProfDesiderateDTO.getDeMansione());
			newCvProfDesiderateDTO.setDeMansioneMin(cvProfDesiderateDTO.getDeMansioneMin());

			newCvProfDesiderateDTO.setDescrizioneProfessione(cvProfDesiderateDTO.getDescrizioneProfessione());
			newCvProfDesiderateDTO.setFlagDispMezzoProprio(cvProfDesiderateDTO.getFlagDispMezzoProprio());
			newCvProfDesiderateDTO.setFlagDispTrasferte(cvProfDesiderateDTO.getFlagDispTrasferte());
			newCvProfDesiderateDTO.setFlagEspSettore(cvProfDesiderateDTO.getFlagEspSettore());

			List<IDecode> listaOrario = cvProfDesiderateDTO.getListaOrario();
			List<GenericFiltroDecodeDTO> newListaOrario = retrieveListaDTO(listaOrario);

			newCvProfDesiderateDTO.setListaOrario(newListaOrario);

			List<IDecode> listaRapportoLavoro = cvProfDesiderateDTO.getListaRapportoLavoro();
			List<GenericFiltroDecodeDTO> newListaRapportoLavoro = retrieveListaDTO(listaRapportoLavoro);

			newCvProfDesiderateDTO.setListaRapportoLavoro(newListaRapportoLavoro);

			newListaCvProfDesiderateDTO.add(newCvProfDesiderateDTO);

		}

		CvInformaticaDTO cvInformaticaDTO = cvInformaticaHome.findDTOByCurriculumId(id);

		List<CvEsperienzeProfDTO> listaCvEsperienzeProfDTO = cvEsperienzeProfHome.findDTOByCurriculumId(id);
		List<CvIstruzioneDTO> listaCvIstruzioneDTO = cvIstruzioneHome.findDTOByCurriculumId(id);
		List<CvLinguaDTO> listaCvLinguaDTO = cvLinguaHome.findDTOByCurriculumId(id);
		List<CvPatenteDTO> listaCvPatenteDTO = cvPatenteHome.findDTOByCurriculumId(id);
		List<CvPatentinoDTO> listaCvPatentinoDTO = cvPatentinoHome.findDTOByCurriculumId(id);

		cvP.setListaCvAgevolazioneDTO(listaCvAgevolazioneDTO);

		cvP.setListaCvAlboDTO(listaCvAlboDTO);
		cvP.setListaCvEsperienzeProfDTO(listaCvEsperienzeProfDTO);
		cvP.setListaCvIstruzioneDTO(listaCvIstruzioneDTO);
		cvP.setListaCvLinguaDTO(listaCvLinguaDTO);
		cvP.setListaCvPatenteDTO(listaCvPatenteDTO);
		cvP.setListaCvPatentinoDTO(listaCvPatentinoDTO);
		cvP.setListaCvProfDesiderateDTO(newListaCvProfDesiderateDTO);
		cvP.setCvCompetenzeTrasvDTO(cvCompetenzeTrasvDTO);

		cvP.setCvDatiPersonaliDTO(cvDto);
		cvP.setListaCvFormazioneDTO(listaCvFormazione);

		cvP.setCvAltreInfoDTO(newCvAltreInfoDTO);
		cvP.setCvInformaticaDTO(cvInformaticaDTO);

		return cvP;

	}

	private List<GenericFiltroDecodeDTO> retrieveListaDTO(List<IDecode> listaAgevolazioniDTO) {
		List<GenericFiltroDecodeDTO> newListaAgevolazioni = new ArrayList<GenericFiltroDecodeDTO>();
		for (IDecode dec : listaAgevolazioniDTO) {
			GenericFiltroDecodeDTO gfd = new GenericFiltroDecodeDTO();
			gfd.setId(dec.getId());
			gfd.setDtInizioVal(dec.getDtInizioVal());
			gfd.setDtFineVal(dec.getDtFineVal());
			gfd.setDescrizione(dec.getDescrizione());

			newListaAgevolazioni.add(gfd);
		}
		return newListaAgevolazioni;
	}

}
