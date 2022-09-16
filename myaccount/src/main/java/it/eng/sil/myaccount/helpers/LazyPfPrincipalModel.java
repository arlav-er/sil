package it.eng.sil.myaccount.helpers;

import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyPfPrincipalModel extends LazyDataModel<PfPrincipal> {
	private static final long serialVersionUID = 7697596762635787164L;

	private PfPrincipalMyAccountEJB pfPrincipalEJB;
	private PfPrincipalFilter searchParams;
	private Integer findAllCount;
	private boolean nameOnly;

	public LazyPfPrincipalModel(PfPrincipalMyAccountEJB pfPrincipalEJB, PfPrincipalFilter searchParams) {
		this.pfPrincipalEJB = pfPrincipalEJB;
		this.searchParams = searchParams;
	}

	public LazyPfPrincipalModel(PfPrincipalMyAccountEJB pfPrincipalEJB, PfPrincipalFilter searchParams, boolean nameOnly) {
		this(pfPrincipalEJB, searchParams);
		this.nameOnly = nameOnly;
	}

	@Override
	public List<PfPrincipal> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		List<PfPrincipal> data = new ArrayList<PfPrincipal>();
		if (nameOnly) {
			data = pfPrincipalEJB.findUsernameNomeCognomeRagioneSocialeByFilter(searchParams, first, pageSize);
		} else {
			data = pfPrincipalEJB.findByFilter(searchParams, first, pageSize);
		}

		// rowCount
		if (findAllCount == null) {
			findAllCount = pfPrincipalEJB.findCountByFilter(searchParams);
			this.setRowCount(findAllCount);
		}

		return data;
	}
}
