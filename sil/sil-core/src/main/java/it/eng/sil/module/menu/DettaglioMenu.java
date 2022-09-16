package it.eng.sil.module.menu;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.sil.bean.menu.Menu;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class DettaglioMenu extends AbstractModule {
	private String className = this.getClass().getName();

	public DettaglioMenu() {
	}

	public void service(SourceBean request, SourceBean response) {
		Menu menu = null;

		SessionContainer session = getRequestContainer().getSessionContainer();

		String cdnMenuStr = (String) request.getAttribute("cdnmenu");

		String isNew = (String) request.getAttribute("NUOVO");
		if (isNew != null) {
			// ///////////////////////////////////////////////////
			// NUOVO MENU ///////////////////////////////////////
			// ///////////////////////////////////////////////////

			menu = new Menu();
			menu.loadNew();

			session.setAttribute("menu", menu);
			// return;

		} else {
			// ///////////////////////////////////////////////////
			// MENU già ESISTENTE //////////////////////////////
			// ///////////////////////////////////////////////////

			if (cdnMenuStr != null && (!cdnMenuStr.equals(""))) {
				menu = new Menu();
				menu.setCdnMenu(Integer.valueOf(cdnMenuStr).intValue());
				menu.load();
				session.setAttribute("menu", menu);
				return;
			}

			menu = (Menu) session.getAttribute("menu");

			String strAzione = (String) request.getAttribute("azione");
			String strHashCode = (String) request.getAttribute("hashCode");

			int intHashCode = new Integer(strHashCode).intValue();

			if (strAzione != null && (!strAzione.equals(""))) {
				switch (strAzione.charAt(0)) {

				case 'S':
					// Ordinamento su

					menu.su(intHashCode);

					break;

				case 'G':
					// Ordinamento giu
					menu.giu(intHashCode);
					break;

				case 'U':
					// Su di un livello
					menu.suLivello(intHashCode);
					break;

				case 'D':
					// Giu' di un livello
					menu.giuLivello(intHashCode);
					break;

				case 'X':
					// Cancella un livello (e tutti i nodi sotto)
					menu.cancellaLivello(intHashCode);
					break;

				case 'A':
					// Cancella un livello (e tutti i nodi sotto)
					String cartella = (String) request.getAttribute("cartella");
					if ((cartella != null) && !cartella.equals(""))
						menu.addCartella(intHashCode, cartella);
					break;

				case 'Z':
					// Cancella un livello (e tutti i nodi sotto)
					String strCdnFunz = (String) request.getAttribute("funzione");
					int cdnFunzione = new Integer(strCdnFunz).intValue();

					String descFunzione = (String) request.getAttribute("descFunzione");
					if ((descFunzione != null) && !descFunzione.equals(""))
						menu.addFunzione(intHashCode, cdnFunzione, descFunzione);
					break;

				case 'R':
					// Cancella un livello (e tutti i nodi sotto)
					String nome = (String) request.getAttribute("nome");
					if ((nome != null) && !nome.equals(""))
						menu.rinomina(intHashCode, nome);
					break;

				case 'M':
					// Cancella un livello (e tutti i nodi sotto)
					String descrizioneMenu = (String) request.getAttribute("descrizioneMenu");
					if ((descrizioneMenu != null) && !descrizioneMenu.equals(""))
						menu.setDescrizione(descrizioneMenu);
					break;

				default:

					break;

				}
			}

		}
	}
}
