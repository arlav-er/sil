package it.eng.myportal.components;

import it.eng.myportal.validator.RecaptchaValidator;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class RecaptchaComponent extends UIInput {

	static final String RECAPTCHA_COMPONENT_FAMILY = "RecaptchaComponentFamily";
	private String publicKey;
	private String privateKey;

	public RecaptchaComponent() {
		super();
		addValidator(new RecaptchaValidator());
	}

	@Override
	public final String getFamily() {
		return RECAPTCHA_COMPONENT_FAMILY;
	}

	public void setPublicKey(String s) {
		publicKey = s;
	}

	public void setPrivateKey(String s) {
		privateKey = s;
	}

	public String getPublicKey() {
		if (publicKey != null)
			return publicKey;

		ValueExpression ve = this.getValueExpression("publicKey");
		if (ve != null) {
			return (String) ve.getValue(getFacesContext().getELContext());
		} else {
			return publicKey;
		}
	}

	public String getPrivateKey() {
		if (privateKey != null)
			return privateKey;

		ValueExpression ve = this.getValueExpression("privateKey");
		if (ve != null) {
			return (String) ve.getValue(getFacesContext().getELContext());
		} else {
			return privateKey;
		}
	}
		
	@Override
	public void validate(FacesContext ctx) {
	  Validator[] validators = getValidators();
	  for (Validator v : validators) {
	    try {
	      v.validate(ctx, this, null);
	    } catch (ValidatorException ex) {
	      setValid(false);
	      FacesMessage message = ex.getFacesMessage();
	      if (message != null) {
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        ctx.addMessage(getClientId(ctx), message);
	      }
	    }

	    super.validate(ctx);
	  }
	}
}