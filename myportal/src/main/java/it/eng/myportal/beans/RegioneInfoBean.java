package it.eng.myportal.beans;

import static it.eng.myportal.utils.ConstantsSingleton.DEFAULT_BUFFER_SIZE;
import static it.eng.myportal.utils.ConstantsSingleton.TMP_DIR;
import it.eng.myportal.dtos.RegioneDTO;
import it.eng.myportal.dtos.RegioneInfoDTO;
import it.eng.myportal.entity.home.RegioneHome;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * BackingBean della pagina delle preferenze della provincia.<br/>
 * <br/>
 *
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere una provincia.</li>
 * </ul>
 *
 * @author Rodi A.
 *   
 */
@ManagedBean
@ViewScoped
public class RegioneInfoBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(RegioneInfoBean.class);

	@EJB
	private RegioneHome regioneHome;

	private boolean editing;

	private RegioneDTO data;

	private RegioneInfoDTO regioneInfo;

	private boolean saved;

	private Integer regioneId;

	private String logoEditedName = "";


	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		log.debug("Costruito il Bean per Informazioni Provincia!");
		try {
			if (session.isRegione()) {
				retrieveData();
//				Clean-up delle informazioni in sessione
				session.getParams().clear();
				if (data != null) {
					saved = true;
				} else { // se non trovo niente allora costruisco una nuova
							// istanza
							// in modifica.
					buildNewDataIstance();
					editing = true;
				}
			} else {
				addErrorMessage("regione.is_not");
				redirectHome();
			}
		} catch (EJBException e) { // in caso di errori durante il recupero dei
									// dati ritorna all'HomePage
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	private void retrieveData() {
		data = session.getConnectedRegione();
	}

	private void buildNewDataIstance() {
		data = new RegioneDTO(); 
		regioneInfo = new RegioneInfoDTO();
	}
	
	/**
	 * Aggiorna il tema selezionato su DB
	 */
	public void updateCSS() {
		data.setStileSelezionato(session.getCssStyle());
		try {
			pfPrincipalHome.updateCSS(data.getStileSelezionato(), session.getPrincipalId());
			
			addInfoMessage("data.updated");
		} catch (EJBException e) {
			addErrorMessage("data.error_updating", e);
		}
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public RegioneDTO getData() {
		return data;
	}

	public void setData(RegioneDTO data) {
		this.data = data;
	}

	/**
	 * Metodo collegato al bottone 'Salva' del detail
	 *
	 */
	public void save() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".save");
		try {
			saveData();
			buildNewDataIstance();
			editing = false;
		} catch (EJBException e) {
			addErrorMessage("data.error_saving", e);
		} finally {
			jamonMonitor.stop();
		}
	}

	private void saveData() {
		data.setDtmMod(new Date());
		data = homePersist(regioneHome, data);
		regioneInfo.setDtmMod(new Date());
		//provinciaInfo = homePersist(provinciaInfoHome, provinciaInfo);
	}

	/**
	 * Metodo collegato al bottone 'Aggiorna' del detail
	 *
	 */
	public void update() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".update");
		try {
			updateData();
			//ricarico in sessione i dati aggiornati
			session.refreshSession();
			editing = false;
			saved = true;
		} catch (EJBException e) {
			addErrorMessage("data.error_updating", e);
		} finally {
			jamonMonitor.stop();
		}
	}

	private void updateData() {
		data.setDtmMod(new Date());
		data = homeMerge(regioneHome, data);
		try {
			if (!ObjectUtils.toString(getLogoEditedName()).trim().isEmpty()) {
				popolaLogo();
			}
		} catch (IOException e) {
			log.error(e);
		}
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}



	public boolean getHasLogo() {
		if (regioneInfo == null) {
			return false;
		}
		if (regioneInfo.getLogo() == null) {
			return false;
		}
		if (regioneInfo.getLogo().length == 0) {
			return false;
		}
		return true;
	}

	/**
	 * @return the logoEditedName
	 */
	public String getLogoEditedName() {
		return logoEditedName;
	}

	/**
	 * @param logoEditedName
	 *            the logoEditedName to set
	 */
	public void setLogoEditedName(String logoEditedName) {
		this.logoEditedName = logoEditedName;
	}

	private void popolaLogo() throws FileNotFoundException, IOException {
		String baseDir = TMP_DIR;
		File file2send = new File(baseDir + File.separator + getLogoEditedName());
		// UtenteInfoDTO dataXfoto = utenteInfoHome.findDTOById(userId);
		FileInputStream input = new FileInputStream(file2send);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int length;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		byte[] foto = output.toByteArray();
		output.close();
		input.close();
		boolean delete = file2send.delete();
		if (!delete) {
			log.debug("File non cancellato:" + file2send.getAbsolutePath());
		}
		regioneInfo.setLogo(foto);
	}

}
