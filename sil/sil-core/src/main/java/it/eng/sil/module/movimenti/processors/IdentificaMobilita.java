package it.eng.sil.module.movimenti.processors;

import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * 
 * Processor che inserisce nella risposta utente l'identificativo della mobilità, creando un tag IDMOB che contiene: -
 * il codice del tipo di mobilità (attributo codTipoMob) - il nome, il cognome e il codice fiscale del lavoratore
 * interessato (attributi nomeLav, cognomeLav, strCfLavoratore) - la ragione sociale e l'indirizzo dell'azienda
 * interessata (attributi ragSocAzienda, indirizzoAzienda)
 */
public class IdentificaMobilita implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();

	public IdentificaMobilita() {
		super();
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", null, null);
		}

		String codTipoMob = "";
		String cognomeLav = "";
		String nomeLav = "";
		String ragSocAz = "";
		String indirAz = "";
		String strCFlavoratore = "";
		String strNumAnnoProt = "";

		codTipoMob = (String) record.get("CODTIPOMOB");
		cognomeLav = (String) record.get("STRCOGNOME");
		nomeLav = (String) record.get("STRNOME");
		ragSocAz = (String) record.get("STRAZRAGIONESOCIALE");
		indirAz = (String) record.get("STRUAINDIRIZZO");
		strCFlavoratore = (String) record.get("STRCODICEFISCALE");

		return ProcessorsUtils.createIdMob(codTipoMob, nomeLav, cognomeLav, ragSocAz, indirAz, strCFlavoratore,
				strNumAnnoProt);
	}
}
