package it.eng.myportal.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import it.eng.myportal.dtos.FbChecklistDTO;
import it.eng.myportal.dtos.FbSchedaFabbisognoDTO;
import it.eng.myportal.dtos.filter.FbChecklistFilterDTO;
import it.eng.myportal.entity.home.FbChecklistHome;
import it.eng.myportal.utils.ConstantsSingleton;

public class LazyFbChecklistModel extends LazyDataModel<FbChecklistDTO> {
	private static final long serialVersionUID = -846343831179074278L;
	protected final Log log = LogFactory.getLog(this.getClass());

	FbChecklistHome fbChecklistHome;
	private FbChecklistFilterDTO searchParams;
	private Integer findAllCount;
	private boolean regione;

	public LazyFbChecklistModel(FbChecklistHome fbChecklistHome, FbChecklistFilterDTO searchParams, boolean regione) {
		this.fbChecklistHome = fbChecklistHome;
		this.searchParams = searchParams;
		this.regione = regione;
		findAllCount = null;
	}

	@Override
	public List<FbChecklistDTO> load(int first, int pageSize, String sortField, SortOrder order,
			Map<String, Object> filters) {
		List<FbChecklistDTO> data = fbChecklistHome.findDTOByFilter(searchParams, first, pageSize, sortField, order);
		if (regione) {
			for (int i = 0; i < data.size(); i++) {
				List<FbSchedaFabbisognoDTO> scheList = new ArrayList<FbSchedaFabbisognoDTO>();
				scheList = filterSchedaFabbisogno(data.get(i).getFbSchedaFabbisognoList());
				data.get(i).setFbSchedaFabbisognoList(scheList);
			}
		}
		if (findAllCount == null) {
			findAllCount = fbChecklistHome.findCountByFilter(searchParams);
			this.setRowCount(findAllCount);
		}

		return data;
	}

	public List<FbSchedaFabbisognoDTO> filterSchedaFabbisogno(List<FbSchedaFabbisognoDTO> scheList) {
		List<FbSchedaFabbisognoDTO> dtoList = new ArrayList<FbSchedaFabbisognoDTO>();

		Iterator<FbSchedaFabbisognoDTO> itr = scheList.iterator();
		while (itr.hasNext()) {
			FbSchedaFabbisognoDTO dto = (FbSchedaFabbisognoDTO) itr.next();
			if (!dto.getDeStatoScheda().getId().equals(ConstantsSingleton.DeStatoFbScheda.IN_LAVORAZIONE))
				dtoList.add(dto);
		}
		return dtoList;
	}

}
