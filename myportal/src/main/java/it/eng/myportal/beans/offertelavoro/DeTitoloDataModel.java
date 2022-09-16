package it.eng.myportal.beans.offertelavoro;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import it.eng.myportal.entity.decodifiche.DeTitolo;

public class DeTitoloDataModel extends ListDataModel<DeTitolo> implements SelectableDataModel<DeTitolo> {  

    public DeTitoloDataModel() {
    }

    public DeTitoloDataModel(List<DeTitolo> data) {
        super(data);
    }
    
    @Override
    public DeTitolo getRowData(String rowKey) {       
        List<DeTitolo> titoli = (List<DeTitolo>) getWrappedData();
        
        for(DeTitolo titolo : titoli) {
            if(titolo.getCodTitolo().equals(rowKey))
                return titolo;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(DeTitolo titolo) {
        return titolo.getCodTitolo();
    }
}