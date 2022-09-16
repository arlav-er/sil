/*
 * Creato il 17-mar-05
 * Author: rolfini
 * 
 */
package it.eng.sil.bean.SilProLabor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author rolfini
 * 
 */
public class TracciatoManager {

	// header
	private String header = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n" + "<Root>\r\n";
	private String sys = "";
	private String intesta = "";
	// /header

	// body
	// nell'elaborazione dell'anagrafica il body può essere un clob
	private oracle.sql.CLOB bodyCLOB = null;
	private String body = "";

	// footer
	private String footer = "</Root>\r\n";

	// /footer

	/*
	 * COSTRUTTORE
	 */
	protected TracciatoManager() {

	}

	private void setSys(String cpi, String utente, String macchina) {

		sys = "<Sys>\r\n" + "  <Sys_Cpi>" + cpi + "</Sys_Cpi>\r\n" + "  <Sys_Utente>" + utente + "</Sys_Utente>\r\n"
				+ "  <Sys_Macchina>" + macchina + "</Sys_Macchina>\r\n" + "</Sys>\r\n";

	}

	private void setIntesta(String data, String tipo, String msgErr, String inOut) {

		intesta = "<Intesta>\r\n" + "  <Int_Data>" + data + "</Int_Data>\r\n" + "  <Int_Tipo>" + tipo
				+ "</Int_Tipo>\r\n" + "  <Int_MsgErr>" + msgErr + "</Int_MsgErr>\r\n" + "  <Int_InOut>" + inOut
				+ "</Int_InOut>\r\n" + "</Intesta>\r\n";

	}

	protected void setHeader(String tipo, String msgErr) {

		setSys("", "", ""); // VUOTI DI DEFAULT
		String data = (new SimpleDateFormat("dd-MM-yyyy")).format(new Date());
		setIntesta(data, tipo, msgErr, "SILPL");
		header += sys + intesta;
	}

	protected void setBodyCLOB(oracle.sql.CLOB bodyCLOB) {
		this.bodyCLOB = bodyCLOB;
	}

	protected void setBody(String body) {
		this.body = body;
	}

	protected void writeCLOB(FileSystemManager fileSystem) throws Exception {
		fileSystem.writeTracciato(this.header, this.bodyCLOB, this.footer);
	}

	protected void write(FileSystemManager fileSystem) throws Exception {
		fileSystem.writeTracciato(this.header, this.body, this.footer);
	}

}
