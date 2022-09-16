package it.eng.myportal.dtos;

import java.math.BigDecimal;

/**
 * Data transfer object della tabella di decodifica Retribuzione
 * 
 * @author iescone
 *
 * @see GenericDecodeDTO
 * @see ISuggestible
 */
public class DeRetribuzioneDTO extends GenericDecodeDTO implements ISuggestible {

	private BigDecimal limiteInferiore;
	private BigDecimal limiteSuperiore;
	
	public DeRetribuzioneDTO() {
	}
	
	@Override
	public String getDescrizione() {		
		if (limiteInferiore != null)
		return this.limiteInferiore + " - " + this.limiteSuperiore;
		else
			return null;
	}
	
	@Override
	public void setDescrizione(String descrizione) {
		throw new UnsupportedOperationException("Che fai!?!?!");
	}

	public BigDecimal getLimiteInferiore() {
		return limiteInferiore;
	}

	public void setLimiteInferiore(BigDecimal limiteInferiore) {
		this.limiteInferiore = limiteInferiore;
	}

	public BigDecimal getLimiteSuperiore() {
		return limiteSuperiore;
	}

	public void setLimiteSuperiore(BigDecimal limiteSuperiore) {
		this.limiteSuperiore = limiteSuperiore;
	}
	
	
	
}
