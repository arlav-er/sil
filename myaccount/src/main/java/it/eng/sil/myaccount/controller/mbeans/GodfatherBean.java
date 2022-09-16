package it.eng.sil.myaccount.controller.mbeans;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "godfatherBean")
@ApplicationScoped
public class GodfatherBean extends AbstractBackingBean implements Serializable {
	private static final long serialVersionUID = 8330539843716140216L;

	private String theme;

	/**
	 * Available Themes
	 */
	private final String emiliaRomagnaTheme = "emiliaRomagna";
	private final String trentoTheme = "trento";
	private final String umbriaTheme = "umbria";
	private final String calabriaTheme = "calabria";
	private final String vdaTheme = "vda";
	private final String pugliaTheme = "puglia";

	@Override
	protected void initPostConstruct() {
		defineTheme();
	}

	private void defineTheme() {
		if (constantsSingleton.isEmiliaRomagna())
			theme = emiliaRomagnaTheme;
		else if (constantsSingleton.isTrento())
			theme = trentoTheme;
		else if (constantsSingleton.isUmbria())
			theme = umbriaTheme;
		else if (constantsSingleton.isCalabria())
			theme = calabriaTheme;
		else if (constantsSingleton.isVDA())
			theme = vdaTheme;
		else if(constantsSingleton.isPuglia())
			theme = pugliaTheme;
		else
			theme = emiliaRomagnaTheme;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

}
