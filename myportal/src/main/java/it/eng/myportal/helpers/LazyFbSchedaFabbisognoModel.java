package it.eng.myportal.helpers;

import it.eng.myportal.dtos.FbSchedaFabbisognoDTO;
import it.eng.myportal.dtos.filter.FbSchedaFabbisognoFilterDTO;
import it.eng.myportal.entity.home.FbSchedaFabbisognoHome;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyFbSchedaFabbisognoModel extends LazyDataModel<FbSchedaFabbisognoDTO> {
	private static final long serialVersionUID = -5492032182380428737L;
	protected final Log log = LogFactory.getLog(this.getClass());

	FbSchedaFabbisognoHome fbSchedaFabbisognoHome;
	private FbSchedaFabbisognoFilterDTO searchParams;
	private Integer findAllCount;

	public LazyFbSchedaFabbisognoModel(FbSchedaFabbisognoHome fbSchedaFabbisognoHome,
			FbSchedaFabbisognoFilterDTO searchParams) {
		this.fbSchedaFabbisognoHome = fbSchedaFabbisognoHome;
		this.searchParams = searchParams;
		findAllCount = null;
	}

	@Override
	public List<FbSchedaFabbisognoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		List<FbSchedaFabbisognoDTO> data = fbSchedaFabbisognoHome.findDTOByFilter(searchParams, first, pageSize, sortField, sortOrder);

		if (findAllCount == null) {
			findAllCount = fbSchedaFabbisognoHome.findCountByFilter(searchParams);
			this.setRowCount(findAllCount);
		}
		
		return data;
	}
}
