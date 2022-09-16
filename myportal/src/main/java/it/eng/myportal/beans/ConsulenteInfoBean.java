package it.eng.myportal.beans;

import it.eng.myportal.dtos.ConsulenteInfoDTO;
import it.eng.myportal.entity.home.ConsulenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeAtpEnteConsulenteHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPraticaHome;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * BackingBean della pagina delle preferenze dell'utente.
 * 
 * @author turro
 * 
 */
@ManagedBean
@ViewScoped
public class ConsulenteInfoBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(ConsulenteInfoBean.class);

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	ConsulenteInfoHome consulenteInfoHome;

	@EJB
	DeTipoPraticaHome deTipoPraticaHome;
	@EJB
	DeAtpEnteConsulenteHome deAtpEnteConsulenteHome;

	private ConsulenteInfoDTO data;

	private List<SelectItem> tipiPratica;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			tipiPratica = deTipoPraticaHome.getListItems(true);

			data = consulenteInfoHome.findDTOById(session.getPrincipalId());

		} catch (EJBException e) { // in caso di errori durante il recupero dei
									// dati ritorna all'HomePage
			addErrorMessage("data.error_loading");
			redirectHome();
		}
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

	public ConsulenteInfoDTO getData() {
		return data;
	}

	public void setData(ConsulenteInfoDTO data) {
		this.data = data;
	}

	/**
	 * Metodo collegato al bottone 'Aggiorna' del detail
	 * 
	 */
	public void update() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".update");
		try {

			data = consulenteInfoHome.mergeDTO(data, getSession().getPrincipalId());
			// ricarico in sessione i dati aggiornati
			session.refreshSession();

			addInfoMessage("data.updated");
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_updating");
		} finally {
			jamonMonitor.stop();
		}
	}

	public List<SelectItem> getTipiPratica() {
		return tipiPratica;
	}

	public void setTipiPratica(List<SelectItem> tipiPratica) {
		this.tipiPratica = tipiPratica;
	}

	public Integer getPfPrincipalId() {
		return getSession().getPrincipalId();
	}

}
