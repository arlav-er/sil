package it.eng.myportal.beans.offertelavoro;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import it.eng.myportal.entity.decodifiche.DeContratto;

public class DeContrattoDataModel extends ListDataModel<DeContratto> implements SelectableDataModel<DeContratto> {  

    public DeContrattoDataModel() {
    }

    public DeContrattoDataModel(List<DeContratto> data) {
        super(data);
    }
    
    @Override
    public DeContratto getRowData(String rowKey) {       
        List<DeContratto> contratti = (List<DeContratto>) getWrappedData();
        
        for(DeContratto contratto : contratti) {
            if(contratto.getCodContratto().equals(rowKey))
                return contratto;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(DeContratto contratto) {
        return contratto.getCodContratto();
    }
}