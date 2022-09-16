package it.eng.sil.module.ido;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.StringUtils;

/*
 * Clesse di utilità - Viene utilizzata nell'avvio a selezione dei candidati
 * vi sono metodi che permettono l'inserimento e la cancellazione 
 * dei candidati dal SourceBean oppure dal Vector 
 * presente in sessione
 * 
 * @author coticone
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ASCandidatiGraduatoriaUtil {

	/*
	 * metodo che permette l'inserimento nel sourcebean di ogni cdnLavoratore (attributo passato al metodo) per i
	 * candidati checkati sulla lista
	 * 
	 */
	public static void insertNominativoSB(Object cdnLavoratore, SourceBean nominativiSB) {

		try {
			boolean checkCdnLavoratore = false;
			SourceBean rowPrgNom = new SourceBean("ROW");
			// verifica se il prgnominativo è presente
			Vector rows = nominativiSB.getAttributeAsVector("ROW");
			for (int j = 0; j < rows.size(); j++) {
				SourceBean row = (SourceBean) rows.get(j);
				String cdnLavoratoreIesimo = (String) row.getAttribute("CDNLAVORATORE");
				if ((cdnLavoratore.toString()).equalsIgnoreCase(cdnLavoratoreIesimo)) {
					checkCdnLavoratore = true;
					break;
				}
			}

			if (!checkCdnLavoratore) {
				rowPrgNom.setAttribute("CDNLAVORATORE", cdnLavoratore);
				nominativiSB.setAttribute(rowPrgNom);
			}

		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * metodo che permette la cancellazione dal sourcebean in sessione di ogni cdnLavoratore che è stato decheckato
	 * dalla lista e quindi presente nella request nell'attributo con label ARRAY_CDNLAVORATORE
	 * 
	 * il metodo ritorna un Vector contenente tutte le righe del SB
	 * 
	 */
	public static Vector deleteNominativoSB(SourceBean request, SourceBean nominativiSB) {

		try {
			String arrayDelNominativi = (String) request.getAttribute("ARRAY_CDNLAVORATORE");
			Vector listanominativi = StringUtils.split(arrayDelNominativi, ";");
			SourceBean newNominativiSB = new SourceBean("CANDIDATI_SEL");
			Vector rows = nominativiSB.getAttributeAsVector("ROW");
			// per ogni prgnominativo presente devo controllare se vi è un
			// riscontro nel SB e se si eliminarlo
			for (int k = 0; k < listanominativi.size(); k++) {
				String cdnLavoratoreDel = (String) listanominativi.get(k);

				for (int j = 0; j < rows.size(); j++) {
					SourceBean row = (SourceBean) rows.get(j);
					String cdnLavoratoreIesimo = (String) row.getAttribute("CDNLAVORATORE");
					if ((cdnLavoratoreDel).equalsIgnoreCase(cdnLavoratoreIesimo)) {
						rows.remove(j);
					}
				}
			}

			return rows;

		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * metodo che permette l'inserimento nel Vector di ogni cdnLavoratore (attributo passato al metodo) per i candidati
	 * checkati sulla lista
	 * 
	 */
	public static void insertNominativoVector(Object cdnLavoratore, Vector nominativiVector) {

		boolean checkCdnLavoratore = false;
		// verifica se il prgnominativo è presente
		Vector rows = nominativiVector;
		for (int j = 0; j < rows.size(); j++) {
			String cdnLavoratoreIesimo = (String) rows.get(j);
			if ((cdnLavoratore.toString()).equalsIgnoreCase(cdnLavoratoreIesimo)) {
				checkCdnLavoratore = true;
				break;
			}
		}

		if (!checkCdnLavoratore) {
			nominativiVector.add(cdnLavoratore);
		}
	}

	/*
	 * metodo che permette la cancellazione dal vettore in sessione di ogni cdnLavoratore che è stato decheckato dalla
	 * lista e quindi presente nella request nell'attributo con label ARRAY_CDNLAVORATORE
	 * 
	 * il metodo ritorna il Vector stesso
	 * 
	 */
	public static Vector deleteNominativoVector(SourceBean request, Vector nominativiVector) {

		String arrayDelNominativi = (String) request.getAttribute("ARRAY_CDNLAVORATORE");
		Vector listanominativi = StringUtils.split(arrayDelNominativi, ";");
		Vector rows = nominativiVector;
		// per ogni prgnominativo presente devo controllare se vi è un riscontro
		// nel SB e se si eliminarlo
		for (int k = 0; k < listanominativi.size(); k++) {
			String cdnLavoratoreDel = (String) listanominativi.get(k);

			for (int j = 0; j < rows.size(); j++) {
				String cdnLavoratoreIesimo = (String) rows.get(j);
				if ((cdnLavoratoreDel).equalsIgnoreCase(cdnLavoratoreIesimo)) {
					rows.remove(j);
				}
			}
		}

		return rows;
	}
}