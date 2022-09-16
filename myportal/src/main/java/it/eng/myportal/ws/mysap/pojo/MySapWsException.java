package it.eng.myportal.ws.mysap.pojo;

public class MySapWsException extends Exception{

	private static final long serialVersionUID = -3571942024954037828L;

	private int errCode;
	private String errDescrption;

	public MySapWsException() {
	}

	public MySapWsException(int errCode, String errDescrption) {
		super();
		this.errCode = errCode;
		this.errDescrption = errDescrption;
	}

	public int getErrCode() {
		return errCode;
	}

	public String getErrDescrption() {
		return errDescrption;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public void setErrDescrption(String errDescrption) {
		this.errDescrption = errDescrption;
	}


	
}
