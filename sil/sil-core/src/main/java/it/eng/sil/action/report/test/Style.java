/*
 * Style.java
 *
 * Created on 21 gennaio 2004, 7.11
 */

package it.eng.sil.action.report.test;

import com.inet.report.Paragraph;
import com.inet.report.Section;
import com.inet.report.Text;
import com.inet.report.TextPart;

/**
 * 
 * @author Administrator
 */
public interface Style {
	int ETICHETTA = 0;
	int CAMPO = 1;
	int TITOLO_SEZIONE = 2;
	int ETICHETTA2 = 3;
	int ETICHETTA3 = 4;
	int TITOLO2 = 5;
	int TITOLO2CENTRATO = 6;
	int TITOLO3 = 7;

	float INCH_TO_MM = 5.67f;

	public void setStyle(TextPart t);

	public void setStyle(Paragraph p);

	public void setStyle(Section s);

	public void setStyle(Text t);

	public int getFontSyze();

	/**
	 * Getter for property fontItalic.
	 * 
	 * @return Value of property fontItalic.
	 * 
	 */
	public boolean isItalic();

	/**
	 * Setter for property fontItalic.
	 * 
	 * @param fontItalic
	 *            New value of property fontItalic.
	 * 
	 */
	public void setItalic(boolean fontItalic);

	/**
	 * Getter for property fontSize.
	 * 
	 * @return Value of property fontSize.
	 * 
	 */
	public int getFontSize();

	/**
	 * Setter for property fontSize.
	 * 
	 * @param fontSize
	 *            New value of property fontSize.
	 * 
	 */
	public void setFontSize(int fontSize);

	/**
	 * Getter for property fontStile.
	 * 
	 * @return Value of property fontStile.
	 * 
	 */
	public java.lang.String getFontName();

	/**
	 * Setter for property fontStile.
	 * 
	 * @param fontStile
	 *            New value of property fontStile.
	 * 
	 */
	public void setFontName(java.lang.String fontName);

	/**
	 * Getter for property hight.
	 * 
	 * @return Value of property hight.
	 * 
	 */
	public int getHeight();

	/**
	 * Setter for property hight.
	 * 
	 * @param hight
	 *            New value of property hight.
	 * 
	 */
	public void setHeight(int hight);

	/**
	 * Getter for property width.
	 * 
	 * @return Value of property width.
	 * 
	 */
	public int getWidth();

	/**
	 * Setter for property width.
	 * 
	 * @param width
	 *            New value of property width.
	 * 
	 */
	public void setWidth(int width);

	/**
	 * Getter for property canGrow.
	 * 
	 * @return Value of property canGrow.
	 * 
	 */
	public boolean isCanGrow();

	/**
	 * Setter for property canGrow.
	 * 
	 * @param canGrow
	 *            New value of property canGrow.
	 * 
	 */
	public void setCanGrow(boolean canGrow);

}
