package it.eng.myportal.beans.amministrazione;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import it.eng.myportal.dtos.StNotiziaDTO;
import it.eng.myportal.entity.home.StNotiziaHome;

public class LazyStNotiziaHomeModel
                   extends LazyDataModel<StNotiziaDTO> 
{
	private static final long serialVersionUID = 1L;
	private List<StNotiziaDTO> listaNotizie;
	@EJB
	private StNotiziaHome stNote;
	
	
	public LazyStNotiziaHomeModel(List<StNotiziaDTO> listaNotizie) {
		this.listaNotizie = listaNotizie;
	}


	@Override
	public List<StNotiziaDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters)	{
		int rowsize = listaNotizie.size();
		this.setRowCount(rowsize);
		
        //paginate
        if(rowsize > pageSize) {
            try {
                return listaNotizie.subList(first, first + pageSize);
            }
            catch(IndexOutOfBoundsException e) {
                return listaNotizie.subList(first, first + (rowsize % pageSize));
            }
        }
        else {
            return listaNotizie;
        }
	}

	
	/*public DTO toDTO(StNotizia entity) {
		throw new UnsupportedOperationException("Metodo toDTO() per la classe " + entity.getClass().getSimpleName()
				+ " non implementato. Devi creare i due metodi toDTO() e fromDTO() nella classe "
				+ entity.getClass().getSimpleName() + "Home cretino!");
	}*/
}
