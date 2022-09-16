package it.eng.myportal.dtos;


public class MsgEspertoDTO extends MsgSupportoDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7007057362649374651L;
	// al momento la risposta può essere solo una ma se un domani dovessimo
	// cambiare idea qui è tutto pronto.
	
	public MsgEspertoDTO() {
		super();
	}

	public MsgEspertoDTO(MsgMessaggioDTO msgMessaggioDTO) {
		super(msgMessaggioDTO);
		
	}

	

}
