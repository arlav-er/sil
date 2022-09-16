package it.eng.myportal.rest.app.exception;

public class EmptyParameterException extends AppEjbException {

	private static final long serialVersionUID = -5560400827069111604L;

	public EmptyParameterException(String... parameters) {
		String paramsName = AppEjbException.EMPTY_PARAMETER_DES;

		if (parameters != null && parameters.length > 0) {
			paramsName += ": ";
			for (String par : parameters)
				paramsName += par + ", ";
			paramsName = paramsName.substring(0, paramsName.length() - 2);
		}

		this.setCode(AppEjbException.EMPTY_PARAMETER_CODE);
		this.setDescription(paramsName);
	}
}
