package it.eng.myportal.beans.candidature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.entity.decodifiche.DeIdoneitaCandidatura;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.rest.report.GetCurriculumUtente;

/**
 * BackingBean della pagina di visualizzazione dell'elenco delle candidature
 * 
 * @author Enrico D'Angelo
 * 
 */
@ManagedBean
@ViewScoped
public class VisualizzaListaCandidatureOldVacancyBean extends AbstractVisualizzaListaCandidatureAziendaBean {

	private List<AcVisualizzaCandidaturaDTO> filterCandidatureList;
	public boolean flagDownload = true;
	private StreamedContent zipCurriculumSelected;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	GetCurriculumUtente getCurriculumUtente;

	/**
	 * Restituisce la lista di tutte le candidature inviate alla vacancy passata come input
	 * 
	 * @param vacacyId
	 *            id della vacancy
	 * @return lista delle candidature inviate alla vacancy
	 */
	@Override
	protected List<AcVisualizzaCandidaturaDTO> getListaCandidature(Integer vacacyId) {
		return acCandidaturaHome.findDtosByVacancyId(vacacyId);
	}

	/*
	@Override
	public void postConstruct() {
		// TODO Auto-generated method stub
		super.postConstruct();
	}
	*/
	
	public void updateDownload() {
		this.flagDownload = true;
		for (AcVisualizzaCandidaturaDTO candidatura : this.getCandidature()) {
			if (candidatura.isFlagCVSelected()) {
				this.flagDownload = false;
				break;
			}
		}
	}

	public StreamedContent getZipCurriculumSelected() throws IOException {
		ByteArrayInputStream stream = null;
		try {
			stream = this.generaZipListaCandidature();
			zipCurriculumSelected = new DefaultStreamedContent(stream, "application/zip", "zipCurriculum.zip");

		} catch (IOException e) {
			log.error("Errore durante la generazione del file zip");
			addCustomErrorMessage("Errore durante la generazione del file zip");
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

		return zipCurriculumSelected;
	}

	private ByteArrayInputStream generaZipListaCandidature() throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ByteArrayInputStream bis = null;
		ByteArrayInputStream bisZip = null;
		int number_of_cv = 1;
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		for (AcVisualizzaCandidaturaDTO candidatura : getCandidature()) {
			if (candidatura.isFlagCVSelected()) {
				Response response = getCurriculumUtente.getCurriculumUtente(candidatura.getIdCvDatiPersonali(),
						request);
				bis = (ByteArrayInputStream) response.getEntity();
				out.putNextEntry(new ZipEntry("cv_per_candidatura_" + candidatura.getCognomeCandidato() + "_"
						+ candidatura.getNomeCandidato() + "_" + number_of_cv + ".pdf"));
				IOUtils.copy(bis, out);
				bis.close();
				out.flush();
				out.closeEntry();
				number_of_cv++;
			}
		}
		if (out != null) {
			out.finish();
			out.flush();
			IOUtils.closeQuietly(out);
		}
		if (bis != null) {
			bis.close();
		}
		bisZip = new ByteArrayInputStream(baos.toByteArray());

		if (baos != null) {
			baos.close();
		}
		return bisZip;
	}

	public boolean isFlagDownload() {
		return flagDownload;
	}

	public void setFlagDownload(boolean flagDownload) {
		this.flagDownload = flagDownload;
	}

	public List<AcVisualizzaCandidaturaDTO> getFilterCandidatureList() {
		return filterCandidatureList;
	}

	public void setFilterCandidatureList(List<AcVisualizzaCandidaturaDTO> filterCandidatureList) {
		this.filterCandidatureList = filterCandidatureList;
	}

	public String candidatureLabel = "Seleziona...";

	// Backing bean
	public void populateLabel() {
		/* Populating the label with the selected options */
		candidatureLabel = new String("");
		if (getLivelliValutazioneSelezionati().size() == 0) {
			candidatureLabel = "Seleziona...";
		} else {
			getLivelliValutazioneSelezionati();
			candidatureLabel = "";
			for (DeIdoneitaCandidatura current : getLivelliValutazioneSelezionati()) {
				candidatureLabel = candidatureLabel + "," + current.getDescrizione();
			}
		}
	}

	public String getCandidatureLabel() {
		return candidatureLabel;
	}

	public void setCandidatureLabel(String candidatureLabel) {
		this.candidatureLabel = candidatureLabel;
	}
	
	@Override
	public String getBackTo() {
		if (utils.isVDA() && isRedoBySess()) {
			//PURTROPPO IL SEGUENTE CODICE NON SI PUO' USARE PERCHE' FORZANDOLO A MANO IL TOKEN SCAZZA. IN ALTERNATIVA VIENE USATA UNA SOLUZIONE VELOCE MA NON BUONA
//			RestoreParameters restoreParameters = session.getParams().get(SESSION_TOKEN_HISTORY+RicercaOfferteBean.class.getSimpleName());
//			if(restoreParameters !=null){
//				String backTo = restoreParameters.get(BACK_TO).toString();
//				if(backTo != null &&
//						!"".equals(backTo)){
//					return restoreParameters.get(BACK_TO).toString();					
//				}
//			}
			//TODO: TROVARE UNA SOLUZIONE MIGLIORE
			//Forzo il redirect alla pagina corrente.
			return getExternalContext().getRequestContextPath() + "/faces/secure/azienda/vacancies/ricerca.xhtml";
		}
		return super.getBackTo();
	}
}
