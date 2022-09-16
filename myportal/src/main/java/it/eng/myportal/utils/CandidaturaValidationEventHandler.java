package it.eng.myportal.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class CandidaturaValidationEventHandler implements ValidationEventHandler {

	private Map<String, String> errorsMap;
	
	private List<String> messages;

	public CandidaturaValidationEventHandler() {
		errorsMap = new HashMap<String, String>();
		messages = new ArrayList<String>();
		errorsMap
				.put("cvc-complex-type.2.4.b: The content of element 'DatiCurriculari' is not complete. One of '{\"http://servizi.lavoro.gov.it/candidatura\":ProfessioneDesiderataDisponibilita}' is expected.",
						"Devi inserire almeno una professione desiderata");
	}

	public boolean handleEvent(ValidationEvent event) {
		messages.add(event.getMessage());
		return true;
	}
	
	public List<String> getErrorMessagesList() {
		List<String> userMessages = new ArrayList<String>();
		for (String message : messages) {
			if (errorsMap.containsKey(message)) {
				userMessages.add(errorsMap.get(message));
			}
		}
		return userMessages;
	}
	
	public boolean hasErrors() {
		return messages.size() > 0;
	}
	

}