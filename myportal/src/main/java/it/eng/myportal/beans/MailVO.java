package it.eng.myportal.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.UploadedFile;

public class MailVO implements Serializable {

	
	private static final long serialVersionUID = -1951459087187189254L;
	
	private String subject = "";
	private String body = "";
	private String from = "";
	private List<String> toList = null;
	private List<String> ccnList = null;
	private List<UploadedFile> uploadedFileList = null;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getToList() {
		return toList;
	}

	public void setToList(List<String> toList) {
		this.toList = toList;
	}

	public List<String> getCCNList() {
		return ccnList;
	}

	public void setCCNList(List<String> cCNList) {
		ccnList = cCNList;
	}

	public List<UploadedFile> getUploadedFileList() {
		return uploadedFileList;
	}

	public void setUploadedFileList(List<UploadedFile> uploadedFileList) {
		this.uploadedFileList = uploadedFileList;
	}
	
	private String listAsString(List<String> lista){
		StringBuilder b = new StringBuilder();
		for(String str : lista){
			b.append(str + ";");
		}
		return b.toString();
	}
	
	public String toString(){
		if (this.ccnList==null) {
			this.ccnList= new ArrayList<String>();
		}
			
		return "mittente: " + from + ";\r\n" + 
				"destinari: [" + listAsString(toList) + "];\r\n" +
				"numero destinari in CCN: [" + this.ccnList.size() + "];\r\n" +
				"oggetto: " + subject + ";\r\n" +
				"lunghezza corpo: " + body.length() +";\r\n";
	}

}
