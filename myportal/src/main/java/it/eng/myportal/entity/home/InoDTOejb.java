package it.eng.myportal.entity.home;

import javax.ejb.Local;

@Local
public interface InoDTOejb<T> {
	
	public T merge(T entity, Integer actingUser);
	
	public T persist(T entity, Integer actingUser);
	
	public void remove(T toRemove);

}
