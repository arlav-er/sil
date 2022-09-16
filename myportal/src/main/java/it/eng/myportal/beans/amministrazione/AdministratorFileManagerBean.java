package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean per l'amministratore
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class AdministratorFileManagerBean extends AbstractBaseBean {

	
	protected static Log log = LogFactory.getLog(AdministratorFileManagerBean.class);

	File logDirectory;
	
	String[] fileList;

	@PostConstruct
	public void postConstruct() {
		if (!"amministratore".equals(getSession().getUsername())) {
		 redirectPublicIndex();
		}
		
		logDirectory = new File(System.getProperty("jboss.server.log.dir"));
		fileList = logDirectory.list();
		
	}

	public File getLogDirectory() {
		return logDirectory;
	}

	public void setLogDirectory(File logDirectory) {
		this.logDirectory = logDirectory;
	}

	public String[] getFileList() {
		return fileList;
	}

	public void setFileList(String[] fileList) {
		this.fileList = fileList;
	}
	

}
