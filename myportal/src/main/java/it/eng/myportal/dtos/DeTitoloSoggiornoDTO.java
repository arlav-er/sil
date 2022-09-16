package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella Titolo Soggiorno
 * 
 * @author Turrini
 * 
 * @see GenericDecodeDTO
 * @see ITreeable
 */

public class DeTitoloSoggiornoDTO extends GenericDecodeDTO implements ITreeable, Comparable<DeTitoloSoggiornoDTO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1589877346507763660L;
	
	

	

	@Override
	public int compareTo(DeTitoloSoggiornoDTO other) {
		return this.getDescrizione().compareTo(other.getDescrizione());
	}
	
	public Boolean getRinnovo() {
		if (descrizione == null) return false;
		if (descrizione.equals("IN RINNOVO")) return true;
		else return false;
	}

	public void setRinnovo(Boolean rinnovo) {
		
	}
}
