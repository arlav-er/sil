/*
 * Creato il 8-nov-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.Controlli;

/**
 * @author savino
 * 
 *         Vengono estratte le informazioni sul collocamento mirato, mobilita', eta' del lavoratore. La inner class che
 *         esegue la costruzione del SourceBean della risposta viene anche chiamata da un processor che esegue gli
 *         stessi controlli in fase di inserimento di un movimento.
 */
public class ControlloMbCmEtaLav extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		if (request.getAttribute("cdnLavoratore") != null && !request.getAttribute("cdnLavoratore").equals("")) {
			disableMessageIdSuccess();
			ControlloDatiLav p = new ControlloDatiLav() {
				public SourceBean esegui(SourceBean request, SourceBean response, String stmName) {
					setSectionQuerySelect(stmName);
					return doSelect(request, response, false);
				}
			};
			p.perform(request, response);
		}
	}

	public abstract static class ControlloDatiLav {
		/**
		 * Legge alcuni dati del lavoratore: inserisce nella response il source bean con i dati del lavoratore.
		 */
		private static final String dataDaControllare = "01/09/2007";

		public void perform(SourceBean request, SourceBean response) throws Exception {

			SourceBean result = null;
			// Modifica : Tutti i lavoratori assunti dal 01/09/2007 (compreso),
			// per poter lavorare devono avere almeno 16 anni
			String datInizioMov = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(request, "datInizioMov");
			String codTipoMov = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(request, "CODTIPOMOV");

			if (request.getAttribute("cdnLavoratore") != null && !request.getAttribute("cdnLavoratore").equals("")) {
				result = new SourceBean("ROWS");
				SourceBean attr = new SourceBean("ROW");
				result.setAttribute(attr);
				SourceBean row = esegui(request, response, "GET_INFO_MB_CM_ETA_LAV");
				if (row != null) {
					if (row.getAttribute("row.CODCMTIPOISCR") != null) {
						attr.setAttribute("CODCMTIPOISCR", row.getAttribute("row.CODCMTIPOISCR"));
						attr.setAttribute("CODMONOTIPORAGG", row.getAttribute("row.CODMONOTIPORAGG"));
					}
					if (row.getAttribute("row.CODMOBILITA") != null)
						attr.setAttribute("CODMOBILITA", row.getAttribute("row.CODMOBILITA"));
					if (row.getAttribute("row.CODSOSPENSIONE") != null)
						attr.setAttribute("CODSOSPENSIONE", row.getAttribute("row.CODSOSPENSIONE"));
					if (row.getAttribute("row.datNasc") != null) {
						String datNasc = (String) row.getAttribute("row.datNasc");
						// Devono essere inibiti solo gli avviamenti successivi
						// al 01/09/2007
						if (codTipoMov.equals("AVV") && !datInizioMov.equals("")
								&& DateUtils.compare(datInizioMov, dataDaControllare) >= 0) {
							boolean risultato = Controlli.minoreDiUno(datNasc, 16, DateUtils.getNow());
							attr.setAttribute("etaMinoreDi16", String.valueOf(risultato));
						} else {
							boolean res = Controlli.minoreDiUno(datNasc, 15, DateUtils.getNow());
							attr.setAttribute("etaMinoreDi15", String.valueOf(res));
						}
					}
				}
				row = esegui(request, response, "GET_PROVINCIA_SIL");
				attr.setAttribute("codProvinciaSil", row.getAttribute("row.codProvinciaSil"));
				attr.setAttribute("provincia", row.getAttribute("row.strDenominazione"));

				String iscrizionePossibile = "false";
				row = esegui(request, response, "GET_LAV_PROVINCIA");
				if (row.containsAttribute("ROW"))
					iscrizionePossibile = "true"; /*
													 * il lavoratore è fuori provincia
													 */
				// il modulo GET_INFO_MB_CM_ETA_LAV estrae solo i lavoratori
				// appartenenti alla categoria disabili
				else {
					row = esegui(request, response, "GET_LAV_COLLMIRATO");
					if (row.containsAttribute("ROW"))
						iscrizionePossibile = "true"; /*
														 * il lavoratore è iscritto al collocamento mirato
														 */
				}

				attr.setAttribute("iscrizionePossibile", iscrizionePossibile);
				response.setAttribute(result);
			}
		}

		/**
		 * Secondo il contesto in cui l'inner class viene chiamata verra' eseguito un particolare accesso al db. E'
		 * necessario perche' nel processor viene utilizzata una transazione.
		 */
		public abstract SourceBean esegui(SourceBean request, SourceBean response, String stmName);
	}
}
