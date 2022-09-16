package it.eng.myportal.validator;

import it.eng.myportal.components.RecaptchaComponent;
import it.eng.myportal.utils.ConstantsSingleton.Register;
import it.eng.myportal.utils.MyPortalHttpLoader;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import net.tanesha.recaptcha.http.HttpLoader;

@FacesValidator(value = "recaptchaValidator")
public class RecaptchaValidator implements Validator {

	private static final boolean USEPROXY = true;

	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

		if (component instanceof RecaptchaComponent) {
			RecaptchaComponent c = (RecaptchaComponent) component;
			String remoteAddr = request.getRemoteAddr();
			ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
			
			reCaptcha.setPrivateKey(Register.CAPTCHA_PRIVATE_KEY);
			reCaptcha.setPublicKey(Register.CAPTCHA_PUBLIC_KEY);
			
			HttpLoader mio = new MyPortalHttpLoader();
			reCaptcha.setHttpLoader(mio);

			String challenge = request.getParameter("recaptcha_challenge_field");
			String uresponse = request.getParameter("recaptcha_response_field");

			ReCaptchaResponse reCaptchaResponse = null;
			try {
			reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
			} catch (Exception e) {
				//nothing...captcha non disponibile...prosegui
				return;
			}
			if (!reCaptchaResponse.isValid()) {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Captcha non valido",
						"Captcha non valido"));
			}
		}
		
	}
}