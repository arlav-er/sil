package it.eng.sil.myaccount.model.exceptions;

public class MyAccountException extends Exception {
	private static final long serialVersionUID = -6439925710698017594L;

	public MyAccountException() {
        super();
    }

    public MyAccountException(String message) {
        super(message);
    }

    public MyAccountException(Exception e) {
        super(e);
    }
}
