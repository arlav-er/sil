package it.eng.sil.myaccount.model.pojo.notification;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmailPOJO implements Serializable {

	private static final Log log = LogFactory.getLog(EmailPOJO.class);
	private static final long serialVersionUID = 5638575348777751939L;

	private String from;

	private List<String> tos;
	private List<String> ccs;
	private List<String> ccns;
	private String subject;
	private String messageBody;

	public EmailPOJO() {

	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTos() {
		return tos;
	}

	public void setTos(List<String> tos) {
		this.tos = tos;
	}

	public List<String> getCcs() {
		return ccs;
	}

	public void setCcs(List<String> ccs) {
		this.ccs = ccs;
	}

	public List<String> getCcns() {
		return ccns;
	}

	public void setCcns(List<String> ccns) {
		this.ccns = ccns;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (subject!=null){
			builder.append(String.format("Oggetto='%s'", subject));
		}
		
		if (from!=null){
			builder.append(String.format(" From:' %s'", from));
		}

		append(builder, tos, " To");
		append(builder, ccs, " Cc");
		append(builder, ccns, " Ccn");

		if (log.isDebugEnabled()) {
			log.debug(builder.toString());
		}
		return builder.toString();

	}

	private void append(StringBuilder builder, List<String> l, String label) {

		if (l != null) {
			builder.append(label + ":{");

			Iterator<String> iterator = l.iterator();
			while (iterator.hasNext()) {
				String item = iterator.next();
				if (iterator.hasNext()) {
					builder.append(item + ",");
				} else {
					builder.append(item);
				}
			}

			builder.append("}");
		}
	}

	public boolean isReadyForSending(){
		if (from==null){
			log.error("Manca la sezione From: nella mail);");
			
			return false;
		}
		
		if (subject==null){
			log.error("Manca l'oggetto della mail");
			return false;
		}

		if (
				(tos==null || tos.size()==0) && 
				(ccs==null || ccs.size()==0) && 
				(ccns==null || ccns.size()==0) 
			){
			log.error("Bisogna indicare almeno un destinatario");
			return false;
		}
		
		
		return true;
	}
	
	
}
