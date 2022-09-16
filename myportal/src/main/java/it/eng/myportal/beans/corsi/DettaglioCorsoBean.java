package it.eng.myportal.beans.corsi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.OrCorsoDTO;
import it.eng.myportal.entity.home.OrCorsoHome;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class DettaglioCorsoBean extends AbstractBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2658037728758176775L;
	
	@EJB
	OrCorsoHome orCorsoHome;
	
	private static final String PARAMETRI = "data";
	
	private OrCorsoDTO data;
	
	public OrCorsoDTO getData() {
		return data;
	}

	public void setData(OrCorsoDTO data) {
		this.data = data;
	}
	
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		try {
			Map<String, String> map = getRequestParameterMap();
			int orCorsoId = Integer.parseInt(map.get("id"));
			
			data = orCorsoHome.findDTOById(orCorsoId);
			
		
			putParamsIntoSession();
			
			
		} catch (EJBException e) { // in caso di errori durante il recupero dei dati ritorna all'HomePage
			addErrorMessage("data.error_loading",e);
			redirectHome();
		}
	}
	
	
	@Override
	protected RestoreParameters generateRestoreParams() {		
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(PARAMETRI, data);
		return ret;
	}
	
	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParameters) {
		super.ricreaStatoDaSessione(restoreParameters);
		data = (OrCorsoDTO) restoreParameters.get(PARAMETRI);		
	};
}
