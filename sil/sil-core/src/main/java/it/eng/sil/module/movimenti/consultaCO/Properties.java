package it.eng.sil.module.movimenti.consultaCO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.util.blen.StringUtils;

public class Properties {

	public static final String FORMATO_DATA = "dd/MM/yyyy";
	public static final String FORMATO_DATA_RESULT = "yyyyMMdd";

	public static final int WS_STORICO_CO_NA = 950001;
	public static final int WS_RLA_NA = 950002;
	public static final int NO_RESULT_FOUND = 950003;

	public static String trasformaData(String dateFrom) throws ParseException {
		String dateTo = new String();
		if (StringUtils.isFilledNoBlank(dateFrom) && !dateFrom.equalsIgnoreCase("0")) {
			SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA_RESULT);
			Date d = sdf.parse(dateFrom);
			sdf.applyPattern(FORMATO_DATA);
			dateTo = sdf.format(d);
		}
		return dateTo;
	}

	@SuppressWarnings("rawtypes")
	public static String getDescrizioneFromCodice(SourceBean serviceResponse, String moduleName, String codice) {
		String descrizione = "";

		SourceBean listaSB = (SourceBean) serviceResponse.getAttribute(moduleName + ".ROWS");
		if ((listaSB != null) && listaSB.containsAttribute("ROW")) {
			Vector vectSB = listaSB.getAttributeAsVector("ROW");
			Iterator iter = vectSB.iterator();
			while (iter.hasNext()) {
				SourceBean attuale = (SourceBean) iter.next();
				String codiceAttuale = (attuale.getAttribute("CODICE")).toString();
				if (codiceAttuale.equalsIgnoreCase(codice)) {
					descrizione = (String) attuale.getAttribute("DESCRIZIONE");
					break;
				}
			}

		}
		return descrizione;

	}

}
