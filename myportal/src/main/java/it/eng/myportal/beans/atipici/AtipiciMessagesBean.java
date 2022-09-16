package it.eng.myportal.beans.atipici;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.home.MsgMessaggioAtipicoHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * BackingBean riepilogativo della pagina Atipici che conta il numero di messaggi non letti
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class AtipiciMessagesBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AtipiciMessagesBean.class);

	@EJB
	private MsgMessaggioAtipicoHome msgMessaggioAtipicoHome;
	
	private Integer nuoviMsgTematica = 0;
	private Integer nuoviMsgLavoro = 0;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		// Map<String, Integer> res = msgMessaggioAtipicoHome.findNumMessaggiNonLetti(getSession().getPrincipalId());
		// nuoviMsgTematica = res.get(TipoQuesito.TEMATICA);
		// nuoviMsgLavoro = res.get(TipoQuesito.LAVORO);
		
	}

	public Integer getNuoviMsgTematica() {
		return nuoviMsgTematica;
	}

	public void setNuoviMsgTematica(Integer nuoviMsgTematica) {
		this.nuoviMsgTematica = nuoviMsgTematica;
	}

	public Integer getNuoviMsgLavoro() {
		return nuoviMsgLavoro;
	}

	public void setNuoviMsgLavoro(Integer nuoviMsgLavoro) {
		this.nuoviMsgLavoro = nuoviMsgLavoro;
	}
	
	public boolean isTematicheEnabled(){
		if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
			return false;
		}
		return true;
	}
	
}
