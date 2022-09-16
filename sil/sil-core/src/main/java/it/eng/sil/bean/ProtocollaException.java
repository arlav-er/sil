package it.eng.sil.bean;

import it.eng.afExt.utils.MessageCodes;

public class ProtocollaException extends Exception {
	private int code = 0;

	public ProtocollaException(String str) {
		super(str);
	}

	public ProtocollaException(int _code) {
		super();
		switch (_code) {
		case -1:
			code = MessageCodes.Protocollazione.ERR_GENERICO_NELLA_SP;
			break;
		case -2:
			code = MessageCodes.Protocollazione.NUM_PROT_TROPPO_GRANDE;
			break;
		case -3:
			code = MessageCodes.Protocollazione.NUM_PROT_TROPPO_PICCOLO;
			break;
		case -31:
			code = MessageCodes.Protocollazione.NUM_PROT_GIA_INSERTITO;
			break;
		case -40:
			code = MessageCodes.Protocollazione.DATA_PROT_NULLA;
			break;
		case -41:
			code = MessageCodes.Protocollazione.DATA_PROT_ERRATA;
			break;
		case -5:
			code = MessageCodes.Protocollazione.TIPO_PROT_NULL;
			break;
		}
	}

	public int getMessageIdFail() {
		return code;
	}

	public void setCode(int value) {
		this.code = value;
		;
	}

}