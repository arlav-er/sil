package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * crea uno stm di aggiornamento in base a quali paramatri note sono presenti nella request.
 * 
 * 
 */
public class DynamicUpdateNotePatto implements IDynamicStatementProvider {
	static final String sqlBase = "update am_patto_lavoratore set ";
	static final String tail = " where prgpattolavoratore = ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		StringBuffer stm = new StringBuffer();
		stm.append(sqlBase);

		SourceBean request = (SourceBean) requestContainer.getServiceRequest();
		/*        */
		// SourceBean request = getRequestTest();

		String noteAmbitoProf = (String) request.getAttribute("STRNOTEAMBITOPROF");
		String noteAzioni = (String) request.getAttribute("STRNOTEAZIONI");
		String noteImpegni = (String) request.getAttribute("STRNOTEIMPEGNI");
		String noteSituazioneAmm = (String) request.getAttribute("STRNOTESITUAZIONEAMM");
		String prgPattoLavoratore = (String) request.getAttribute("PRGPATTOLAVORATORE");
		String numKloPatto = (String) request.getAttribute("NUMKLOPATTOLAVORATORE");
		if (noteAmbitoProf != null) {
			if (noteAmbitoProf.length() >= 4000) {
				noteAmbitoProf = noteAmbitoProf.substring(0, 4000);
			}

			stm.append("STRNOTEAMBITOPROF='");
			stm.append(noteAmbitoProf);
			stm.append("' ,");
		}

		if (noteImpegni != null) {
			if (noteImpegni.length() >= 4000) {
				noteImpegni = noteImpegni.substring(0, 4000);
			}

			stm.append("STRNOTEIMPEGNI='");
			stm.append(noteImpegni);
			stm.append("' ,");
		}

		if (noteAzioni != null) {
			if (noteAzioni.length() >= 4000) {
				noteAzioni = noteAzioni.substring(0, 4000);
			}

			stm.append("STRNOTEAZIONI='");
			stm.append(noteAzioni);
			stm.append("' ,");
		}
		if (numKloPatto != null) {
			// dovrei lanciare una eccezione il NUMKLOPATTOLAVORATORE deve
			// essere presente perche un update sia possibile
			stm.append("NUMKLOPATTOLAVORATORE=");
			stm.append(numKloPatto);
			stm.append(",");
		}

		if (noteSituazioneAmm != null) {
			if (noteSituazioneAmm.length() >= 4000) {
				noteSituazioneAmm = noteSituazioneAmm.substring(0, 4000);
			}

			stm.append("STRNOTESITUAZIONEAMM='");
			stm.append(noteSituazioneAmm);
			stm.append("'");
		}

		if (stm.charAt(stm.length() - 1) == ',') {
			stm.setLength(stm.length() - 1);
		}

		stm.append(tail);
		stm.append(prgPattoLavoratore);

		return stm.toString();
	}

	public static void main(String[] a) {
		DynamicUpdateNotePatto d = new DynamicUpdateNotePatto();
		String stm = d.getStatement(null, null);
		System.out.println(stm);
	}

	private SourceBean getRequestTest() {
		try {
			SourceBean sb = new SourceBean("TEST");
			sb.setAttribute("STRNOTEAZIONI", "azioniiiiiiiiiiiiiiiA");
			sb.setAttribute("STRNOTEIMPEGNI", "fg sd sds ss sd sdsgdgs");
			sb.setAttribute("PRGPATTOLAVORATORE", "1");
			return sb;
		} catch (Exception e) {
			return null;
		}
	}
}
