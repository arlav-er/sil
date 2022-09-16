/*
 * Creato il 31-ago-05
 *
 */

package it.eng.afExt.utils;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

import it.eng.sil.util.Sottosistema;

/**
 * @author Vuoto
 * 
 */
public class InizializzaInterruttori implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InizializzaInterruttori.class.getName());

	private SourceBean config;

	public InizializzaInterruttori() {
		super();
		config = null;
	}

	public void init(SourceBean config) {

		this.config = config;

		String flag = null;
		flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.AS.attivo");
		Sottosistema.setAS(flag.equalsIgnoreCase("true"));

		flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.CM.attivo");
		Sottosistema.setCM(flag.equalsIgnoreCase("true"));

		flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.MO.attivo");
		Sottosistema.setMO(flag.equalsIgnoreCase("true"));

		flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.DWH.attivo");
		Sottosistema.setDWH(flag.equalsIgnoreCase("true"));

		_logger.debug(Sottosistema.showInfo());

	}

	public SourceBean getConfig() {
		return config;
	}

}