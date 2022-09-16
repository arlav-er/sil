package it.eng.myportal.entity.enums;

import it.eng.myportal.entity.PfAbilitazione;
import it.eng.myportal.entity.PfAbilitazione_;

import javax.persistence.metamodel.SingularAttribute;


/**
 * Classe di enumerazione per determinare il tipo di permesso che si richiede
 * per una attività
 * 
 * @author Rodi A.
 * 
 */
public enum TipoAbilitazione {	
	VISIBILE(PfAbilitazione_.flagVisibile),
	INSERIMENTO(PfAbilitazione_.flagInserimento),
	MODIFICA(PfAbilitazione_.flagModifica), 
	LETTURA(PfAbilitazione_.flagLettura), 
	CANCELLAZIONE(PfAbilitazione_.flagCancellazione),
	AMMINISTRAZIONE(PfAbilitazione_.flagAmministrazione);

	private SingularAttribute<PfAbilitazione,Boolean> attribute;

	TipoAbilitazione(SingularAttribute<PfAbilitazione,Boolean> attribute) {
		this.attribute = attribute;
	}

	public SingularAttribute<PfAbilitazione,Boolean> getAttribute() {
		return attribute;
	}
}