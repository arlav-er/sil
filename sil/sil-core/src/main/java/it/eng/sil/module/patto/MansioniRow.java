/*
 * Creato il Jan 25, 2005
 * 
 */
package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.util.Utils;

/**
 * @author savino
 * 
 *         Classe che formatta un SourceBean a partire dai dati ottenuti dal db con una query formata da union.
 *         (GET_MANSIONI) Per formattazione si intende riorganizzazione delle informazioni estratte dal db. Il formato
 *         del SourceBean risultato (riga del vettore ritornato) e':
 * 
 *         <pre>
 * 		attributo            :   tipo
 * 
 * 		&quot;contratti&quot;          : ArrayList
 * 	&quot;orari&quot;              : ArrayList
 * 	&quot;turni&quot;              : ArrayList
 * 	&quot;comuni&quot;             : ArrayList
 * 	&quot;provincie&quot;          : ArrayList
 * 	&quot;regioni&quot;            : ArrayList
 * 	&quot;stati&quot;              : ArrayList
 * 	&quot;mobilitaGeo&quot;        : MobilitaGeoBean
 * 	&quot;mansioneATempo&quot;     : String
 * 	&quot;esperienza&quot;         : String
 * 	&quot;mansione&quot;           : String
 * 	&quot;prgLavPattoScelta&quot;  : String
 * 	&quot;prgMansione&quot;        : String
 * 	&quot;misura&quot;             : ArrayList
 *         </pre>
 */
public class MansioniRow {
	/**
	 * @author savino
	 * 
	 *         Classe che contiene le informazioni sulla mobilita' geografica.
	 */
	public static class MobilitaGeoBean {
		private String utilizzoAuto;
		private String pendGiornaliero;
		private String mobSett;
		private String tipoTrasferta;
		private String utilizzoMoto;
		private String percentuale;

		public String getMobSett() {
			return mobSett;
		}

		public String getPendGiornaliero() {
			return pendGiornaliero;
		}

		public String getPercentuale() {
			return percentuale;
		}

		public String getTipoTrasferta() {
			return tipoTrasferta;
		}

		public String getUtilizzoAuto() {
			return utilizzoAuto;
		}

		public String getUtilizzoMoto() {
			return utilizzoMoto;
		}
	}

	public static Vector getMansioni(Vector mansioniRows) throws Exception {

		//
		SourceBean mansioneRow = null;
		String mansione = null;
		String esperienza = null;
		String mansioneATempo = null;
		String disponibile = null;
		String formazione = null;
		String inserimentoProfessionale = null;
		// variabili per la mobilita' geografica
		String utilizzoAuto = null;
		String utilizzoMoto = null;
		String pendolarismoGiornaliero = null;
		String mobilitaSett = null;
		String tipoTrasferta = null;
		String durataPercorrenzaMax = null;
		String mansionePrec = null;
		//

		boolean nuovaMansione = false;
		// il risultato del metodo
		Vector mansioni = new Vector();
		// il risultato della formattazione di ogni riga
		SourceBean rowFormatted = new SourceBean("ROW");
		// variabili contenitrici delle informazioni multiple
		ArrayList contratti = new ArrayList();
		ArrayList orari = new ArrayList();
		ArrayList turni = new ArrayList();
		ArrayList comuni = new ArrayList();
		ArrayList province = new ArrayList();
		ArrayList regioni = new ArrayList();
		ArrayList stati = new ArrayList();
		MobilitaGeoBean geo = new MobilitaGeoBean();
		boolean stampa = true;
		// inizializzazione della variabile che permette di capire quando si
		// cambia mansione
		if (mansioniRows.size() > 0) {
			mansionePrec = (String) ((SourceBean) mansioniRows.elementAt(0)).getAttribute("MANSIONE");
		}
		for (int n = 0; n < mansioniRows.size(); n++) {
			mansioneRow = (SourceBean) mansioniRows.elementAt(n);
			int ordine = ((BigDecimal) mansioneRow.getAttribute("ORDINE")).intValue();
			switch (ordine) {
			case 2:
				contratti.add(mansioneRow.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {
					stampa = false;
					mansioneRow.updAttribute("ORDINE", new BigDecimal("1"));
					mansioneRow.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 3:
				orari.add(mansioneRow.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {
					stampa = false;
					mansioneRow.updAttribute("ORDINE", new BigDecimal("1"));
					mansioneRow.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 4:
				turni.add(mansioneRow.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {
					stampa = false;
					mansioneRow.updAttribute("ORDINE", new BigDecimal("1"));
					mansioneRow.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 5:
				comuni.add(mansioneRow.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {
					stampa = false;
					mansioneRow.updAttribute("ORDINE", new BigDecimal("1"));
					mansioneRow.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 6:
				province.add(mansioneRow.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {
					stampa = false;
					mansioneRow.updAttribute("ORDINE", new BigDecimal("1"));
					mansioneRow.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 7:
				regioni.add(mansioneRow.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {
					stampa = false;
					mansioneRow.updAttribute("ORDINE", new BigDecimal("1"));
					mansioneRow.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 8:
				stati.add(mansioneRow.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {
					stampa = false;
					mansioneRow.updAttribute("ORDINE", new BigDecimal("1"));
					mansioneRow.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 9:
				//
				utilizzoAuto = (String) mansioneRow.getAttribute("FLGDISPAUTO");
				utilizzoAuto = (utilizzoAuto == null) ? "" : utilizzoAuto;
				utilizzoMoto = (String) mansioneRow.getAttribute("flgDispMoto");
				utilizzoMoto = (utilizzoMoto == null) ? "" : utilizzoMoto;
				pendolarismoGiornaliero = (String) mansioneRow.getAttribute("flgPendolarismo");
				pendolarismoGiornaliero = (pendolarismoGiornaliero == null) ? "" : pendolarismoGiornaliero;
				mobilitaSett = (String) mansioneRow.getAttribute("FLGMOBSETT");
				mobilitaSett = (mobilitaSett == null) ? "" : mobilitaSett;
				tipoTrasferta = (String) mansioneRow.getAttribute("VINCOLO");
				durataPercorrenzaMax = Utils.notNull(mansioneRow.getAttribute("NUMOREPERC"));
				durataPercorrenzaMax = (durataPercorrenzaMax == null) ? "" : durataPercorrenzaMax;
				//
				geo.pendGiornaliero = pendolarismoGiornaliero;
				geo.percentuale = durataPercorrenzaMax;
				geo.tipoTrasferta = tipoTrasferta;
				geo.utilizzoAuto = utilizzoAuto;
				geo.utilizzoMoto = utilizzoMoto;
				geo.mobSett = mobilitaSett;
				if (mansioniRows.size() != n + 1)
					break;
				else {
					mansionePrec = "";
					ordine = 1;
					stampa = false;
				}
				// se sono alla fine del ciclo debbo registrare le info
				// accumulate
				// otterro' che la mansione letta e quella precedente saranno
				// diverse
			case 1:
				mansione = (String) mansioneRow.getAttribute("MANSIONE");
				if (!mansione.equals(mansionePrec)) {
					rowFormatted.setAttribute("contratti", contratti);
					rowFormatted.setAttribute("orari", orari);
					rowFormatted.setAttribute("turni", turni);
					rowFormatted.setAttribute("comuni", comuni);
					rowFormatted.setAttribute("provincie", province);
					rowFormatted.setAttribute("regioni", regioni);
					rowFormatted.setAttribute("stati", stati);
					rowFormatted.setAttribute("mobilitaGeo", geo);

					mansioni.add(rowFormatted);
					//
					mansionePrec = mansione;

				}
				mansioneATempo = (String) mansioneRow.getAttribute("TEMPO");
				if (mansioneATempo != null) {
					switch (mansioneATempo.charAt(0)) {
					case 'D':
						mansioneATempo = "Tempo determinato";
						break;
					case 'I':
						mansioneATempo = "Tempo indeterminato";
						break;
					case 'E':
						mansioneATempo = "Tempo det/indet";
						break;
					}
				}
				esperienza = Utils.notNull(mansioneRow.getAttribute("ESPERIENZA"));
				disponibile = (String) mansioneRow.getAttribute("FLGDISPONIBILE");
				inserimentoProfessionale = (String) mansioneRow.getAttribute("FLGPIP");
				formazione = (String) mansioneRow.getAttribute("FLGDISPFORMAZIONE");
				String prgMansione = mansioneRow.getAttribute("prgMansione").toString();
				String prgLavPattoScelta = mansioneRow.getAttribute("prgLavPattoScelta").toString();
				ArrayList misura = new ArrayList(3);
				if (inserimentoProfessionale != null)
					misura.add("Progetti di inserimento");
				if (formazione != null)
					misura.add("Percorsi formativi");
				if (disponibile != null)
					misura.add("Attività lavorativa");
				rowFormatted = new SourceBean("ROW");
				rowFormatted.setAttribute("mansioneATempo", Utils.notNull(mansioneATempo));
				rowFormatted.setAttribute("esperienza", esperienza);
				rowFormatted.setAttribute("mansione", mansione);
				rowFormatted.setAttribute("prgLavPattoScelta", prgLavPattoScelta);
				rowFormatted.setAttribute("prgMansione", prgMansione);
				rowFormatted.setAttribute("misura", misura);
				// inizializza arrays
				contratti = new ArrayList();
				orari = new ArrayList();
				turni = new ArrayList();
				comuni = new ArrayList();
				province = new ArrayList();
				regioni = new ArrayList();
				stati = new ArrayList();
				geo = new MobilitaGeoBean();
				// caso particolare in cui siamo nell' ultima mansione ed un
				// solo ordine=1
				if (mansioniRows.size() == n + 1 && stampa) {
					rowFormatted.setAttribute("contratti", contratti);
					rowFormatted.setAttribute("orari", orari);
					rowFormatted.setAttribute("turni", turni);
					rowFormatted.setAttribute("comuni", comuni);
					rowFormatted.setAttribute("provincie", province);
					rowFormatted.setAttribute("regioni", regioni);
					rowFormatted.setAttribute("stati", stati);
					rowFormatted.setAttribute("mobilitaGeo", geo);
					mansioni.add(rowFormatted);
				}
				break;
			default: // per il momento non fare niente. Una situazione del
						// genere non deve verificarsi
			} // switch
		}
		return mansioni;
	}
}
