package it.eng.sil.coop.webservices.voucher;

public interface AttivaVoucherInterface extends java.rmi.Remote {
	public String attivaVoucher(String cfEnte, String sedeEnte, String cfLavoratore, String codiceAttivazione)
			throws java.rmi.RemoteException, Exception;
}
