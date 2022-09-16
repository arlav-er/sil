package it.eng.myportal.beans.curriculum;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import it.eng.myportal.dtos.AbstractUpdatableDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.helpers.CurriculumSearchParams;

/**
 * Classe contenitore delle informazioni su un CV in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 * 
 * @author turro
 * @see AbstractUpdatableDTO
 * 
 */

public class RicercaCvAziendaModel extends LazyDataModel<CvDatiPersonali> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2698965085713786797L;

	protected static Log log = LogFactory.getLog(RicercaCvAziendaModel.class);
	private String comuneDomicilio;
	private Date dataModifica;
	private String attivitaPrincipale;
	private Integer idMsgMessaggio;
	private Integer idVaDatiVacancy;
	private Date dataMessaggioAz;
	/**
	 * Messaggio di contatto inviato dall'azienda all'utente
	 */
	private Integer idMsgMessaggioAz;

	private CurriculumSearchParams curriculumSearchParams;
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	public RicercaCvAziendaModel(CurriculumSearchParams curriculumSearchParams,
			CvDatiPersonaliHome cvDatiPersonaliHome) {
		this.curriculumSearchParams = curriculumSearchParams;
		this.cvDatiPersonaliHome = cvDatiPersonaliHome;
	}

	@Override
	public List<CvDatiPersonali> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		curriculumSearchParams.setStartFrom(first);
		curriculumSearchParams.setChunkSize(pageSize);
		curriculumSearchParams.setOrderBy(sortField);
		curriculumSearchParams.setAscending(SortOrder.ASCENDING.equals(sortOrder));
		// Cerco i risultati da visualizzare nella prima pagina.

		List<CvDatiPersonali> risultato = cvDatiPersonaliHome
				.ricercaCVaziendaLazyModelNewVersion(curriculumSearchParams);

		Long tCou = cvDatiPersonaliHome.ricercaCVaziendaLazyModelCountNewVersion(curriculumSearchParams);
		log.info("Ricerca CV eseguita con risultati: " + tCou + " - " + curriculumSearchParams.toString());
		this.setRowCount(tCou.intValue());

		return risultato;
	}

	public String getComuneDomicilio() {
		return comuneDomicilio;
	}

	public void setComuneDomicilio(String comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public String getAttivitaPrincipale() {
		return attivitaPrincipale;
	}

	public void setAttivitaPrincipale(String attivitaPrincipale) {
		this.attivitaPrincipale = attivitaPrincipale;
	}

	public Integer getIdMsgMessaggio() {
		return idMsgMessaggio;
	}

	public void setIdMsgMessaggio(Integer idMsgMessaggio) {
		this.idMsgMessaggio = idMsgMessaggio;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Date getDataMessaggioAz() {
		return dataMessaggioAz;
	}

	public void setDataMessaggioAz(Date dataMessaggioAz) {
		this.dataMessaggioAz = dataMessaggioAz;
	}

	public Integer getIdMsgMessaggioAz() {
		return idMsgMessaggioAz;
	}

	public void setIdMsgMessaggioAz(Integer idMsgMessaggioAz) {
		this.idMsgMessaggioAz = idMsgMessaggioAz;
	}

}
