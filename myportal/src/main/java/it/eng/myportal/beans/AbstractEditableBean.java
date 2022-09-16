package it.eng.myportal.beans;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.validation.Valid;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Classe astratta per gestire una pagina nella quale è presente una form.<br/>
 * Questa form può essere compilata e salvata,
 * visualizzata e aggiornata. Nella pagina sono presenti i tasti SALVA, MODIFICA, AGGIORNA e ANNULLA <br/>
 * Stati possibili del BB <br/>
 * stato         | saved | editing <br/>
 * visualizza    | true  | false <br/>
 * inserimento   | false | true <br/>
 * aggiornamento | true  | true <br/>
 * nessun pulsante | false | false <br/>
 *
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>TODO L'utente connesso deve avere i permessi per visualizzare i dati del DTO.</li>
 * </ul>
 *
 * @author Rodi A., Girotti S.
 *
 * @param <DTO> Classe del DTO associato alla pagina. Ogni pagina ha un DTO contenente i dati associato ad essa.
 */
public abstract class AbstractEditableBean<DTO> extends AbstractBaseBean {

	/**
	 * I dati sono già stati salvati su DB?
	 * Quindi mi trovo in fase di aggiornamento e non di inserimento ex-novo.
	 */
	protected boolean saved;

	/**
	 * La variabile è a true se si è in fase di modifica di dati.
	 */
	protected boolean editing;

	/**
	 * Dati della form/pagina.
	 */
	@Valid
	protected DTO data;
	
	
	/**
	 * Impostando questa variabile a false viene nascosto il pulsante per la cancellazione dei dati
	 */
	protected boolean showDeleteButton = true;
	

	/**
	 * Metodo eseguito successivamente alla costruzione del BB.<br/>
	 * Il metodo recupera i dati da DB e, se presenti, imposta
	 * la variabile 'saved' a true, altrimenti, costruisce un nuovo DTO.<br/>
	 */
	@PostConstruct
	@Override
	protected void postConstruct() {
		super.postConstruct();
		try {
			data = retrieveData(); // recupera i dati
			if (data != null) {
				saved = true;
			} else { // se non trovo niente allora costruisco una nuova istanza in modifica.
				data = buildNewDataIstance();
				editing = true;
			}
		} catch (EJBException e) { // in caso di errori durante il recupero dei dati ritorna all'HomePage
			addErrorMessage("data.error_loading",e);
			redirectHome();
//			data = buildNewDataIstance();
//			editing = true;
		}
	}

	/**
	 * Restituisce una nuova istanza del DTO che contiene i dati all'interno della form. Gli EJB e le risorse saranno
	 * già state iniettate e quindi utilizzabili.
	 *
	 * @return l'istanza nuova del DTO
	 */
	protected abstract DTO buildNewDataIstance();

	/**
	 * Implementare il metodo per recuperare il DTO a partire dal DB. Gli EJB e le risorse saranno già state iniettate e
	 * quindi utilizzabili.
	 *
	 * @return DTO i dati recuperati da DB
	 */
	protected abstract DTO retrieveData();

	/**
	 * Implementare la parte di salvataggio dei dati su DB.
	 */
	protected abstract void saveData();

	/**
	 * Implementare la parte di aggiornamento dei dati su DB.
	 */
	protected abstract void updateData();

	/**
	 * Metodo collegato al bottone 'Salva'
	 *
	 */
	public void save() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".save");
		try {
			saveData();
			saved = true;
			editing = false;
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_saving");
		} finally {
			jamonMonitor.stop();
		}
	}

	/**
	 * Metodo collegato al bottone 'Aggiorna'
	 *
	 */
	public void update() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".update");
		try {
			updateData();
			editing = false;
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_updating");
		} finally {
			jamonMonitor.stop();
		}
	}

	/**
	 * Metodo collegato al bottone 'Modifica'
	 */
	public void edit() {
		editing = true;
	}

	/**
	 * Metodo collegato al bottone 'Annulla'
	 */
	public void dontedit() {
		editing = false;
		try {
			data = retrieveData();
		} catch (EJBException e) {
			addErrorMessage("data.error_loading",e);
		}
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public DTO getData() {
		return data;
	}

	public void setData(DTO data) {
		this.data = data;
	}

	public boolean isShowDeleteButton() {
		return showDeleteButton;
	}

	public void setShowDeleteButton(boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}
	
	

}
