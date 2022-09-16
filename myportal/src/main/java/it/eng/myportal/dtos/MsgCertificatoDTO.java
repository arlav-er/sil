package it.eng.myportal.dtos;



/**
 * La richiesta di un certificato.
 * @author Rodi A.
 *
 */
public class MsgCertificatoDTO extends MsgSupportoDTO {

	private static final long serialVersionUID = -5112333219145663135L;

	public MsgCertificatoDTO() {
		super();
		
	}

	public MsgCertificatoDTO(MsgMessaggioDTO msgMessaggioDTO) {
		super(msgMessaggioDTO);		
	}
}
