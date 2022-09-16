package it.eng.myportal.helpers;

import it.eng.myportal.dtos.YgDichiarazioneNeetDTO;
import it.eng.myportal.dtos.YgDichiarazioneNeetFilterDTO;
import it.eng.myportal.entity.home.YgDichiarazioneNeetHome;

import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyYgDichiarazioneNeetModel extends LazyDataModel<YgDichiarazioneNeetDTO> {
	private static final long serialVersionUID = -7618224596291910030L;
	protected final Log log = LogFactory.getLog(this.getClass());

	YgDichiarazioneNeetHome ygDichiarazioneNeetHome;
	private YgDichiarazioneNeetFilterDTO searchParams;
	private Integer findAllCount;

	public LazyYgDichiarazioneNeetModel(YgDichiarazioneNeetFilterDTO searchParams) {
		this.searchParams = searchParams;
		findAllCount = null;
		injectEjb();
	}

	@Override
	public List<YgDichiarazioneNeetDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		searchParams.setFirst(first);
		searchParams.setPageSize(pageSize);
		List<YgDichiarazioneNeetDTO> data = ygDichiarazioneNeetHome.findDTOByFilter(searchParams);

		// rowCount
		if (findAllCount == null) {
			findAllCount = ygDichiarazioneNeetHome.findDTOByFilterCount(searchParams).intValue();
			this.setRowCount(findAllCount);
		}

		return data;
	}

	private void injectEjb() {
		try {
			InitialContext ic = new InitialContext();
			ygDichiarazioneNeetHome = (YgDichiarazioneNeetHome) ic.lookup("java:app/MyPortal/YgDichiarazioneNeetHome");
		} catch (NamingException e) {
			log.error("Lookup Failed");
			e.printStackTrace();
		}
	}
}
