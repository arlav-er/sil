package it.eng.myportal.dtos;


/**
 * DTO per la entity PatrAbiProvincia.
 * @author gicozza
 */
public class PatrAbiProvinciaDTO extends AbstractUpdatablePkDTO{

	private static final long serialVersionUID = 9169052024788201792L;
	private PatronatoDTO patronato;
	private DeProvinciaDTO provincia;
	
	public DeProvinciaDTO getProvincia() {
		return provincia;
	}

	public void setProvincia(DeProvinciaDTO provincia) {
		this.provincia = provincia;
	}

	public PatronatoDTO getPatronato() {
		return patronato;
	}
	
	public void setPatronato(PatronatoDTO patronato) {
		this.patronato = patronato;
	}

}
