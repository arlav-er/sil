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
 * componente). Controlla che il file in upload sia .png, .bmp, .jpg, .jpeg, .gif o .pdf.
 * 
 * @author gicozza
 *
 */
@FacesValidator(value = "fileUploadSoloImmaginiValidator")
public class FileUploadSoloImmaginiValidator implements Validator {

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

		if (!uploadedFile.getContentType().equals("image/bmp") && !uploadedFile.getContentType().equals("image/jpeg")
				&& !uploadedFile.getContentType().equals("image/png")
				&& !uploadedFile.getContentType().equals("image/gif")
				&& !uploadedFile.getContentType().equals("application/pdf")) {
			FacesMessage msg = new FacesMessage("Il file caricato deve essere un'immagine o un pdf.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}
}
