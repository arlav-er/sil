package it.eng.afExt.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

/**
 * 
 * @author Corrado Vaccari
 */
public class RowsFlatIterator implements Iterator {

	ArrayList listRows = new ArrayList();
	int current = 0;

	/**
	 * Costruttore.
	 * 
	 * @param beanSource
	 */
	public RowsFlatIterator(SourceBean beanSource) {

		extractRows(beanSource);
	}

	/**
	 * 
	 */
	private void extractRows(SourceBean beanCurrent) {

		if (beanCurrent == null)
			return;

		Vector vectRows = beanCurrent.getAttributeAsVector("ROWS");
		if ((vectRows == null) || (vectRows.size() == 0))
			vectRows = beanCurrent.getAttributeAsVector("ROW");
		if ((vectRows == null) || (vectRows.size() == 0))
			vectRows = beanCurrent.getAttributeAsVector("ROWS.ROW");

		if ((vectRows == null) || (vectRows.size() == 0))
			return;

		for (Iterator iter = vectRows.iterator(); iter.hasNext();) {

			SourceBean beanRow = (SourceBean) iter.next();

			if (beanRow.getName().equals("ROW"))
				listRows.add(beanRow);
			else if (beanRow.getName().equals("ROWS"))
				extractRows(beanRow);
		}
	}

	/**
	 * 
	 */
	public boolean hasNext() {

		return current < listRows.size();
	}

	/**
	 * 
	 */
	public Object next() {

		if (current >= listRows.size())
			throw new NoSuchElementException();

		return listRows.get(current++);
	}

	/**
	 * 
	 */
	public void remove() {

		throw new UnsupportedOperationException();
	}

}