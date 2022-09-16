package it.eng.myportal.dtos;


/**
 * DTO legati alle tabelle informative
 * delle tabelle di decodifica.
 * 
 * 
 * 
 * @author girotti
 *
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public abstract class AbstractDecodeInfoDTO extends GenericDecodeDTO implements ISuggestible,IDecodeInfo {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String titoloInfo;

	/**
	 * @return the titoloInfo
	 */
	@Override
	public String getTitoloInfo() {
		return titoloInfo;
	}

	/**
	 * @param titoloInfo
	 *            the titoloInfo to set
	 */
	@Override
	public void setTitoloInfo(String titoloInfo) {
		this.titoloInfo = titoloInfo;
	}
}
