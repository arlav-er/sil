package it.eng.myportal.dtos;

import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Classe astratta per DTO aggiornabili
 * 
 * @see AbstractHasPrimaryKeyDTO
 * @see IUpdatable
 * 
 * @author Rodi A.
 * 
 * 
 */
public abstract class AbstractUpdatablePkDTO extends AbstractUpdatableDTO implements IHasPrimaryKey<Integer> {
	private static final long serialVersionUID = 5233775294570511972L;
	protected Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (!o.getClass().isAssignableFrom(this.getClass()))
			return false;

		Integer otherId = ((AbstractUpdatablePkDTO) o).getId();
		if (otherId != null && this.getId() != null)
			return otherId.intValue() == this.getId().intValue();
		else
			return false;
	}

	@Override
	public int hashCode() {
		if (this.getId() != null)
			return this.getId() % ConstantsSingleton.DEFAULT_BUCKET_SIZE;
		else
			return 0;
	}

}
