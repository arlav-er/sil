package it.eng.sil.myaccount.helpers;

import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyValidaUtentiSareModel extends LazyDataModel<AziendaInfo> {

	private static final long serialVersionUID = 8323401798618277773L;
	private AziendaInfoEJB aziendaInfoEJB;
	private String user;
	private String stato;
	private String codProvincia;
	private Integer findAllCount;

	/**
	 * Initializes search parameters for this table.
	 */
	public LazyValidaUtentiSareModel(AziendaInfoEJB aziendaInfoEJB, String user, String stato, String codProvincia) {
		this.aziendaInfoEJB = aziendaInfoEJB;
		this.user = user;
		this.stato = stato;
		this.codProvincia = codProvincia;
	}

	/**
	 * Loads a page of the table.
	 */
	@Override
	public List<AziendaInfo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		List<AziendaInfo> data = new ArrayList<AziendaInfo>();
		data = aziendaInfoEJB.cercaUtentiSAREValida(user, stato, codProvincia, first, pageSize);

		// rowCount
		if (findAllCount == null) {
			findAllCount = aziendaInfoEJB.cercaUtentiSAREValidaCount(user, stato, codProvincia);
			this.setRowCount(findAllCount);
		}

		return data;
	}
}
