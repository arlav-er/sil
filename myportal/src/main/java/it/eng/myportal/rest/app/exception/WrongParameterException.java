package it.eng.myportal.rest.app.exception;

public class WrongParameterException extends AppEjbException {

	private static final long serialVersionUID = 3951523709815798567L;

	public WrongParameterException(String... parameters) {
		String paramsName = AppEjbException.WRONG_PARAMETER_DES;

		if (parameters != null && parameters.length > 0) {
			paramsName += ": ";
			for (String par : parameters)
				paramsName += par + ", ";
			paramsName = paramsName.substring(0, paramsName.length() - 2);
		}

		this.setCode(AppEjbException.WRONG_PARAMETER_CODE);
		this.setDescription(paramsName);
	}
}
