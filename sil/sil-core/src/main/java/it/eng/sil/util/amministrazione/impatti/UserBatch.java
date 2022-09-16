package it.eng.sil.util.amministrazione.impatti;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.security.User;

/**
 * autore Landi Giovanni 29/12/2004 Utilizzata nei batch (CessazioniGiornaliere, MovimentiGiornalieri)
 */

public class UserBatch {
	private User user;

	/**
	 * costruttore
	 */
	public UserBatch() {
		user = null;
	}

	/**
	 * metodo che restituisce un oggetto User
	 * 
	 * @param cdnUt
	 *            (parametro cdnut dell'utente che lancia il batch)
	 * @param cdnProfilo
	 *            (profilo utente che lancia il batch)
	 * @return
	 * @throws Exception
	 */
	public User getUser(String cdnUt, String cdnProfilo, String cdnGruppo) throws Exception {
		Vector rowsUser = null;
		SourceBean rowUser = null;
		rowsUser = DBLoad.getUser(cdnUt, cdnProfilo, cdnGruppo);
		if (rowsUser.size() > 0) {
			rowUser = (SourceBean) rowsUser.get(0);
			Integer codiceUtente = Integer.valueOf(cdnUt);
			String strCognome = rowUser.getAttribute("strcognome").toString();
			String strNome = rowUser.getAttribute("strnome").toString();
			String strUsername = rowUser.getAttribute("strlogin").toString();
			String codRif = rowUser.getAttribute("COD_RIF").toString();
			Integer codiceGruppo = Integer.valueOf(rowUser.getAttribute("CDN_gruppo").toString());
			Integer codiceTipoGruppo = Integer.valueOf(rowUser.getAttribute("CDN_tipo_gruppo").toString());
			Integer codiceProfilo = Integer.valueOf(rowUser.getAttribute("CDN_profilo").toString());
			user = new User(codiceUtente.intValue(), strUsername, strNome, strCognome);
			user.setCdnGruppo(codiceGruppo.intValue());
			user.setCdnTipoGruppo(codiceTipoGruppo.intValue());
			user.setCodRif(codRif);
			user.setCdnProfilo(codiceProfilo.intValue());
		}
		return user;
	}
}
