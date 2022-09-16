package it.eng.sil.myaccount.utils;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.omnifaces.util.Faces;

import it.eng.sil.myaccount.controller.mbeans.UtilsBean;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;

/**
 * Questa classe carica lo ui.properties e, se necessario, ulteriori file .properties specifici per la regione. Per ora
 * viene usato solo per la VDA.
 */
public class UIMessagesResolver extends ResourceBundle {

	private UtilsBean utilsBean;

	public UIMessagesResolver() {
		FacesContext context = FacesContext.getCurrentInstance();
		setParent(ResourceBundle.getBundle(ConstantsSingleton.UI_BASE_BUNDLE, Faces.getLocale()));
		utilsBean = context.getApplication().evaluateExpressionGet(context, "#{utils}", UtilsBean.class);
	}

	@Override
	protected Object handleGetObject(String key) {
		Object message = null;

		if (parent.containsKey(key)) {
			message = parent.getObject(key);
		}

		// in case VDA bundle messages file has the same key return it from that file, otherwise it's already loaded
		// from base
		if (utilsBean.isVDA()) {
			ResourceBundle uiVDABundle = ResourceBundle.getBundle(ConstantsSingleton.UI_VDA_BUNDLE, Faces.getLocale());
			if (uiVDABundle.containsKey(key)) {
				message = uiVDABundle.getObject(key);
			}
		}

		// in case Umbria bundle messages file has the same key return it from that file, otherwise it's already loaded
		// from base
		if (utilsBean.isUmbria()) {
			ResourceBundle uiUmbriaBundle = ResourceBundle.getBundle(ConstantsSingleton.UI_UMBRIA_BUNDLE,
					Faces.getLocale());
			if (uiUmbriaBundle.containsKey(key)) {
				message = uiUmbriaBundle.getObject(key);
			}
		}
		
		if (utilsBean.isPat()) {
			ResourceBundle uiUmbriaBundle = ResourceBundle.getBundle(ConstantsSingleton.UI_PAT_BUNDLE,
					Faces.getLocale());
			if (uiUmbriaBundle.containsKey(key)) {
				message = uiUmbriaBundle.getObject(key);
			}
		}

		// in case Calabria bundle messages file has the same key return it from that file, otherwise it's already
		// loaded from base
		if (utilsBean.isCalabria()) {
			ResourceBundle uiCalabriaBundle = ResourceBundle.getBundle(ConstantsSingleton.UI_CALABRIA_BUNDLE,
					Faces.getLocale());
			if (uiCalabriaBundle.containsKey(key)) {
				message = uiCalabriaBundle.getObject(key);
			}
		}

		return message;
	}

	@Override
	public Enumeration<String> getKeys() {
		return parent.getKeys();
	}
}
