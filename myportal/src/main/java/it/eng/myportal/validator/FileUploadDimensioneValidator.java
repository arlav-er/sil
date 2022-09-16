package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.model.UploadedFile;

/**
 * Questo validatore può essere usato per i componenti
 * <p:fileUpload>
 * di Primefaces in modalità "simple" (dato che in questa modalità non si può usare l'opzione allowedTypes del
 * componente). Controlla che le dimensioni di un file siano inferiori al parametro "maxDimensione".
 * 
 * @author gicozza
 *
 */

@FacesValidator(value = "fileUploadDimensioneValidator")
public class FileUploadDimensioneValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null)
			return;

		if (!(value instanceof UploadedFile)) {
			FacesMessage msg = new FacesMessage("Errore di validazione: oggetto non è un UploadedFile.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		UploadedFile uploadedFile = (UploadedFile) value;

		Long maxDimensione = null;

		Object maxDimensioneObj = component.getAttributes().get("maxDimensione");
		if (maxDimensioneObj != null && maxDimensioneObj instanceof String) {
			String maxDimensioneString = (String) maxDimensioneObj;
			maxDimensione = Long.parseLong(maxDimensioneString);
		}

		if (maxDimensione != null) {
			Long uploadedFileSize = uploadedFile.getSize();
			if (uploadedFileSize > maxDimensione) {
				FacesMessage msg = new FacesMessage("Il file caricato è troppo grande.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}

	}
}
