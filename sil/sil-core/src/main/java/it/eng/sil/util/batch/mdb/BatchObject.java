package it.eng.sil.util.batch.mdb;

import java.io.Serializable;
import java.util.Arrays;

public class BatchObject implements Serializable {

	private static final long serialVersionUID = 7248822252676526653L;
	private String comando;
	private Boolean avvioManuale;
	private String[] comandoSplitted;
	private String className;
	private String[] params;

	/**
	 * @param comando
	 * @param avvioManuale
	 */
	public BatchObject(String comando, Boolean avvioManuale) {
		super();
		this.comando = comando;
		this.comandoSplitted = split(" +");
		this.className = initClassName();
		this.params = initParams();
		this.avvioManuale = avvioManuale;
	}

	/**
	 * @return the avvioManuale
	 */
	public Boolean getAvvioManuale() {
		return avvioManuale;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the params
	 */
	public String[] getParams() {
		return params;
	}

	private String initClassName() {
		if (this.comandoSplitted != null && this.comandoSplitted.length > 0) {
			return this.comandoSplitted[0];
		}
		return null;
	}

	private String[] initParams() {
		if (this.comandoSplitted != null && this.comandoSplitted.length > 1) {
			return Arrays.copyOfRange(this.comandoSplitted, 1, this.comandoSplitted.length);
		}
		return null;
	}

	private String[] split(String regExp) {
		if (this.comando != null && !this.comando.isEmpty()) {
			return comando.trim().split(regExp);
		}
		return null;
	}

	@Override
	public String toString() {
		return "BatchObject [comando=" + comando + ", avvioManuale=" + avvioManuale + "]";
	}

}
