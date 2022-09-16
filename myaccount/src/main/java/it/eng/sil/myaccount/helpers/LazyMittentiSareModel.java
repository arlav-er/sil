package it.eng.sil.myaccount.helpers;

import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloGruppoMyAccountEJB;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Questo LazyModel viene usato nella pagina di amministrazione del 'Superuser SARE'. Raccoglie tutte le profilature
 * (oggetti GpRuoloGruppo) che abbiano ruolo 'Gestione CO', filtrandole in base a vari parametri inseriti da front-end.
 *
 */
public class LazyMittentiSareModel extends LazyDataModel<Object[]> {
	private static final long serialVersionUID = 5231019539176427667L;

	private GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;
	private Integer findAllCount;
	private String username;
	private String codiceFiscale;

	public LazyMittentiSareModel(String username, String codiceFiscale, GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB) {
		this.username = username;
		this.codiceFiscale = codiceFiscale;
		this.gpRuoloGruppoEJB = gpRuoloGruppoEJB;
	}

	@Override
	public List<Object[]> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		List<Object[]> data = gpRuoloGruppoEJB.findMittentiSare(username, codiceFiscale, first, pageSize);

		// rowCount
		if (findAllCount == null) {
			findAllCount = gpRuoloGruppoEJB.findMittentiSareCount(username, codiceFiscale);
			this.setRowCount(findAllCount);
		}

		return data;
	}
}
