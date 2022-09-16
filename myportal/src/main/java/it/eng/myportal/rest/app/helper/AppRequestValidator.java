package it.eng.myportal.rest.app.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.rest.app.exception.WrongParameterException;
import it.eng.myportal.utils.ConstantsSingleton;

public class AppRequestValidator {

	private final Log log = LogFactory.getLog(this.getClass());

	// Max length tokenSecurity
	private static final int MAX_LENGTH_TOKEN_SECURITY = 256;
	// Max length username e password
	private static final int MAX_LENGTH_LOGIN = 128;
	// Max length token legato all'utente
	private static final int MAX_LENGTH_TOKEN = 128;
	// Max length campi contenenti numerici ma esposti come stringa
	private static final int MAX_LENGTH_NUMERIC = 15;
	// Max length latitudine/longitudine
	private static final int MAX_LENGTH_LAT_LON = 30;
	// Max length chiave delle tabelle di decodifica (codice)
	private static final int MAX_LENGTH_CODICE = 50;
	// Max length del campo descrizione specifico del metodo di aggiunta ricerca salvata
	private static final int MAX_LENGTH_DES_RIC_SALV = 100;
	// Max length messaggio
	private static final int MAX_LENGTH_MESSAGGIO = 4000;
	// Max length telefono
	private static final int MAX_LENGTH_TELEFONO = 20;
	// Max length del messaggio specifico del metodo di valutazione
	private static final int MAX_LENTH_MSG_VALUTAZIONE = 1000;
	// Max length dei campi cosa e dove dei metodi di aggiunta ricerca salvata e lista vacancies
	private static final int MAX_LENGTH_COSA_DOVE = 200;
	// Max length campi contenenti date ma esposti come stringa
	private static final int MAX_LENGTH_DATE = 10;
	// Max length delle collezioni
	private static final int MAX_LENGTH_COLLECTION = 25;
	// Max length campi contenenti de_comune.cod_com
	private static final int MAX_LENGTH_COD_COM = 4;
	// Max size del file CV
	private static final int MAX_LENGTH_FILE_CV = 5242880;

	private String param;
	private List<String> params;
	private byte[] paramData;

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		if (params != null) {
			// Remove empty elements
			this.params = new ArrayList<String>();

			for (String item : params) {
				if (StringUtils.isNotEmpty(item)) {
					this.params.add(item);
				}
			}
		}
	}

	public byte[] getParamData() {
		return paramData;
	}

	public void setParamData(byte[] paramData) {
		this.paramData = paramData;
	}

	public AppRequestValidator() {
		super();
	}

	public AppRequestValidator(String param) {
		super();
		this.param = param;
	}

	public AppRequestValidator checkMaxLengthTokenSecurity() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_TOKEN_SECURITY);
	}

	public AppRequestValidator checkMaxLengthLogin() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_LOGIN);
	}

	public AppRequestValidator checkMaxLengthToken() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_TOKEN);
	}

	public AppRequestValidator checkMaxLengthLatLon() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_LAT_LON);
	}

	public AppRequestValidator checkMaxLengthNumeric() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_NUMERIC);
	}

	public AppRequestValidator checkMaxLengthCodice() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_CODICE);
	}

	public AppRequestValidator checkMaxLengthDesRicSalv() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_DES_RIC_SALV);
	}

	public AppRequestValidator checkMaxLengthMessaggio() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_MESSAGGIO);
	}

	public AppRequestValidator checkMaxLengthTelefono() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_TELEFONO);
	}

	public AppRequestValidator checkMaxLengthMsgValutazione() throws WrongParameterException {
		return checkMaxLength(MAX_LENTH_MSG_VALUTAZIONE);
	}

	public AppRequestValidator checkMaxLengthCosaDove() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_COSA_DOVE);
	}

	public AppRequestValidator checkMaxLengthDate() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_DATE);
	}

	public AppRequestValidator checkMaxLengthCodCom() throws WrongParameterException {
		return checkMaxLength(MAX_LENGTH_COD_COM);
	}

	public AppRequestValidator checkMaxLengthFileCv() throws WrongParameterException {
		return checkMaxLengthFile(MAX_LENGTH_FILE_CV);
	}

	public AppRequestValidator checkMaxLengthFileFoto() throws WrongParameterException {
		return checkMaxLengthFile(ConstantsSingleton.FileUpload.MAX_ATTACHMENT_SIZE);
	}

	public AppRequestValidator checkEmail() throws WrongParameterException {
		return checkPattern(
				"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
	}

	public AppRequestValidator checkMaxLengthCodiceInCollection() throws WrongParameterException {
		if (this.params != null) {
			if (this.params.size() > MAX_LENGTH_COLLECTION) {
				log.error("Superata dimensione massima collezione (corrente " + this.params.size() + " - massimo "
						+ MAX_LENGTH_COLLECTION + ")");

				throw new WrongParameterException();
			}

			for (String item : this.params) {
				AppRequestValidator validator = new AppRequestValidator(item);
				validator.checkMaxLengthCodice();
			}
		}
		return this;
	}

	private AppRequestValidator checkMaxLength(int length) throws WrongParameterException {
		if (this.param != null) {
			if (this.param.length() > length) {
				log.error("Superata lunghezza massima parametro: \"" + this.param + "\" (corrente "
						+ this.param.length() + " - massimo " + length + ")");

				throw new WrongParameterException();
			}
		}
		return this;
	}

	private AppRequestValidator checkMaxLengthFile(int length) throws WrongParameterException {
		if (this.paramData != null) {
			if (this.paramData.length > length) {
				log.error("Superata lunghezza massima parametro: \"" + this.paramData + "\" (corrente "
						+ this.paramData.length + " - massimo " + length + ")");

				throw new WrongParameterException();
			}
		}
		return this;
	}

	private AppRequestValidator checkPattern(String regex) throws WrongParameterException {
		if (this.param != null && !this.param.isEmpty()) {
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(this.param);
			if (!matcher.matches()) {
				log.error("Parametro non valido: \"" + this.param + "\" (regEx \"" + regex + "\")");

				throw new WrongParameterException();
			}
		}
		return this;
	}

	public AppRequestValidator checkPatternAlphaNumNoSpace() throws WrongParameterException {
		return checkPattern("^[a-zA-Z0-9\\-_àèéìòù']+$");
	}

	public AppRequestValidator checkPatternAlphaNumSpace() throws WrongParameterException {
		return checkPattern("^[a-zA-Z0-9\\-_àèéìòù' ]+$");
	}

	public AppRequestValidator checkPatternNumeric() throws WrongParameterException {
		return checkPattern("^[0-9\\-\\.]+$");
	}

	public AppRequestValidator checkPatternDate() throws WrongParameterException {
		return checkPattern("^[0-9/]+$");
	}

	public AppRequestValidator checkPatternMessaggio() throws WrongParameterException {
		return checkPattern("^[a-zA-Z0-9\\-_àèéìòù'`\\s!\"/()=?\\[\\]@#,;.:€&\\+]+$");
	}

	public AppRequestValidator checkPatternAlphaNumNoSpaceInCollection() throws WrongParameterException {
		if (this.params != null) {
			for (String item : this.params) {
				AppRequestValidator validator = new AppRequestValidator(item);
				validator.checkPatternAlphaNumNoSpace();
			}
		}
		return this;
	}
}
