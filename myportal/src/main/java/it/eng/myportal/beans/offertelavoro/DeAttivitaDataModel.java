package it.eng.myportal.beans.offertelavoro;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import it.eng.myportal.entity.decodifiche.DeAttivita;

public class DeAttivitaDataModel extends ListDataModel<DeAttivita> implements SelectableDataModel<DeAttivita> {  

    public DeAttivitaDataModel() {
    }

    public DeAttivitaDataModel(List<DeAttivita> data) {
        super(data);
    }
    
    @Override
    public DeAttivita getRowData(String rowKey) {       
        List<DeAttivita> attivitas = (List<DeAttivita>) getWrappedData();
        
        for(DeAttivita attivita : attivitas) {
            if(attivita.getCodAteco().equals(rowKey))
                return attivita;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(DeAttivita attivita) {
        return attivita.getCodAteco();
    }
}