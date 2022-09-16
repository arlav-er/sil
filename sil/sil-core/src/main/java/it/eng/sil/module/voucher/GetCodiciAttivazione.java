package it.eng.sil.module.voucher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetCodiciAttivazione extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		String strPrgAzioneDaAss = request.containsAttribute("prgazione") ? request.getAttribute("prgazione").toString()
				: "";
		BigDecimal prgAzioneDaAssociare = new BigDecimal(strPrgAzioneDaAss);
		ArrayList<String> codiciAttivazioneRet = new ArrayList<String>();
		disableMessageIdFail();
		disableMessageIdSuccess();
		SourceBean risultato = new SourceBean("ROWS");

		setSectionQuerySelect("QUERY_ENTE_AZIONE");
		SourceBean rowEnti = doSelect(request, response, false);
		if (rowEnti != null) {
			setSectionQuerySelect("QUERY_CODICE_ATTIVAZIONE");
			SourceBean rowCodici = doSelect(request, response, false);
			if (rowCodici != null) {
				Vector<SourceBean> codici = rowCodici.getAttributeAsVector("ROW");
				Vector<SourceBean> enti = rowEnti.getAttributeAsVector("ROW");
				String codAttivazione = "";
				String codAttivazioneOld = "";
				BigDecimal prgAzione = null;
				String cfEnte = "";
				String cfEnteOld = "";
				String codSede = "";
				String codSedeOld = "";
				Vector<BigDecimal> azioniEnte = null;
				if (enti.size() > 0 && codici.size() > 0) {
					Map<String, Vector<BigDecimal>> mappaturaEnti = new HashMap<String, Vector<BigDecimal>>();
					for (int j = 0; j < enti.size(); j++) {
						SourceBean rowEnte = enti.get(j);
						rowEnte = rowEnte.containsAttribute("ROW") ? (SourceBean) rowEnte.getAttribute("ROW") : rowEnte;
						cfEnte = rowEnte.getAttribute("strcodicefiscale").toString();
						codSede = rowEnte.getAttribute("codSede").toString();
						prgAzione = (BigDecimal) rowEnte.getAttribute("prgazioni");
						if (j == 0) {
							azioniEnte = new Vector<BigDecimal>();
							azioniEnte.add(prgAzione);
							cfEnteOld = cfEnte;
							codSedeOld = codSede;
						} else {
							if (cfEnte.equalsIgnoreCase(cfEnteOld) && codSede.equalsIgnoreCase(codSedeOld)) {
								azioniEnte.add(prgAzione);
							} else {
								String keyEnte = cfEnteOld + "!" + codSedeOld;
								mappaturaEnti.put(keyEnte, azioniEnte);
								azioniEnte = new Vector<BigDecimal>();
								azioniEnte.add(prgAzione);
								cfEnteOld = cfEnte;
								codSedeOld = codSede;
							}
						}
					}
					String keyEnte = cfEnteOld + "!" + codSedeOld;
					mappaturaEnti.put(keyEnte, azioniEnte);

					Vector<BigDecimal> azioniAssegnate = null;

					for (int j = 0; j < codici.size(); j++) {
						SourceBean rowCod = codici.get(j);
						rowCod = rowCod.containsAttribute("ROW") ? (SourceBean) rowCod.getAttribute("ROW") : rowCod;
						codAttivazione = rowCod.getAttribute("codattivazione").toString();
						prgAzione = (BigDecimal) rowCod.getAttribute("prgazioni");

						if (j == 0) {
							azioniAssegnate = new Vector<BigDecimal>();
							azioniAssegnate.add(prgAzioneDaAssociare);
							azioniAssegnate.add(prgAzione);
							codAttivazioneOld = codAttivazione;
						} else {
							if (codAttivazione.equalsIgnoreCase(codAttivazioneOld)) {
								azioniAssegnate.add(prgAzione);
							} else {
								Set<String> keySet = mappaturaEnti.keySet();
								Iterator<String> it = keySet.iterator();
								boolean exit = false;
								while (it.hasNext() && !exit) {
									String key = it.next();
									Vector<BigDecimal> azioniMappate = mappaturaEnti.get(key);
									boolean codiceAttivazioneOK = true;
									for (int k = 0; k < azioniAssegnate.size(); k++) {
										BigDecimal prgAz = azioniAssegnate.get(k);
										if (!azioniMappate.contains(prgAz)) {
											codiceAttivazioneOK = false;
										}
									}
									if (codiceAttivazioneOK) {
										exit = true;
										if (!codiciAttivazioneRet.contains(codAttivazioneOld)) {
											codiciAttivazioneRet.add(codAttivazioneOld);
										}
									}
								}
								azioniAssegnate = new Vector<BigDecimal>();
								azioniAssegnate.add(prgAzioneDaAssociare);
								azioniAssegnate.add(prgAzione);
								codAttivazioneOld = codAttivazione;
							}
						}
					}

					if (azioniAssegnate != null) {
						Set<String> keySet = mappaturaEnti.keySet();
						Iterator<String> it = keySet.iterator();
						boolean exit = false;
						while (it.hasNext() && !exit) {
							String key = it.next();
							Vector<BigDecimal> azioniMappate = mappaturaEnti.get(key);
							boolean codiceAttivazioneOK = true;
							for (int k = 0; k < azioniAssegnate.size(); k++) {
								BigDecimal prgAz = azioniAssegnate.get(k);
								if (!azioniMappate.contains(prgAz)) {
									codiceAttivazioneOK = false;
								}
							}
							if (codiceAttivazioneOK) {
								exit = true;
								if (!codiciAttivazioneRet.contains(codAttivazioneOld)) {
									codiciAttivazioneRet.add(codAttivazioneOld);
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < codiciAttivazioneRet.size(); i++) {
			String codiceCurr = codiciAttivazioneRet.get(i);
			SourceBean risCurr = new SourceBean("ROW");
			risCurr.setAttribute("codice", codiceCurr);
			risCurr.setAttribute("descrizione", codiceCurr);
			risultato.setAttribute(risCurr);
		}
		response.setAttribute(risultato);
	}
}