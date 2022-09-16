/*
 * Created on 18-set-07
 */
package it.eng.sil.bean;

import java.io.File;
import java.util.ArrayList;

import com.engiweb.framework.base.SessionContainer;

import it.eng.sil.action.report.amministrazione.MobilitaEsportatore;

/**
 * @author vuoto
 * 
 */
public class MobilitaExpThread extends ArrayList implements Runnable {

	private SessionContainer sessionContainer = null;
	private boolean isRunning = true;

	public static final String THE_ZIP_FILE = "_THE_ZIP_FILE_";
	public static final String THE_ZIP_FILE_NAME = "_THE_ZIP_FILE_NAME_";

	public MobilitaExpThread() {

	}

	public MobilitaExpThread(SessionContainer sessionContainer) {
		this.sessionContainer = sessionContainer;

	}

	public void run() {
		// isRunning = true;

		String sid = (String) sessionContainer.getAttribute("ID_SESSIONE_STAMPA");
		MobilitaEsportatore exp = new MobilitaEsportatore(
				(String) sessionContainer.getAttribute("ESPORTA_ANCHE_INVIATI"));

		exp.setSessionid(sid);
		File theFile = exp.execute(this);
		sessionContainer.setAttribute(THE_ZIP_FILE, theFile);
		sessionContainer.setAttribute(THE_ZIP_FILE_NAME, exp.getOutputFileName());
		isRunning = false;
	}

	public boolean isRunning() {
		return isRunning;
	}

	/*
	 * public void setRunning(boolean b) { isRunning = b; }
	 */
}
