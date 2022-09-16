package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import it.eng.afExt.utils.MessageCodes.NuovoRedditoAttivazione;

public class MappaturaErroriClientNRA {
	private static final Map<String, Integer> myMap;
	static {
		Map<String, Integer> aMap = new HashMap<String, Integer>();
		aMap.put("A100", NuovoRedditoAttivazione.COM_RIC_CORRETTA);
		aMap.put("A101", NuovoRedditoAttivazione.COGNOME_ASSENTE);
		aMap.put("A102", NuovoRedditoAttivazione.NOME_ASSENTE);
		aMap.put("A103", NuovoRedditoAttivazione.CODICE_FISCALE_ASSENTE);
		aMap.put("A104", NuovoRedditoAttivazione.DATA_VAR_RES_ASSENTE);
		aMap.put("A105", NuovoRedditoAttivazione.NUOVO_IND_RES_ASSENTE);
		aMap.put("A106", NuovoRedditoAttivazione.NUOVO_CAP_RES_ASSENTE);
		aMap.put("A107", NuovoRedditoAttivazione.CODICE_REIEZIONE_ASSENTE);
		aMap.put("A108", NuovoRedditoAttivazione.AZIONE_RICHIESTA_NON_ESISTENTE);
		aMap.put("A109", NuovoRedditoAttivazione.ID_DOMANDA_INTRANET_NON_PRESENTE);
		aMap.put("A110", NuovoRedditoAttivazione.ID_DOMANDA_WEB_NON_PRESENTE);
		aMap.put("A999", NuovoRedditoAttivazione.ALTRI_ERRORI_NON_PRESENTI);
		myMap = Collections.unmodifiableMap(aMap);
	}

	public static Integer getSilCode(String codeWs) {
		return myMap.get(codeWs);
	}
}
