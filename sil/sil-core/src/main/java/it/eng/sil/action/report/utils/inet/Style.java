/*
 * Style.java
 *
 * Created on 21 gennaio 2004, 7.11
 */

package it.eng.sil.action.report.utils.inet;

import com.inet.report.FieldPart;
import com.inet.report.Paragraph;
import com.inet.report.Section;
import com.inet.report.Text;
import com.inet.report.TextPart;

/**
 * Interfaccia utilizzata dalle classi che implementano uno stile.
 * 
 * @author Andrea Savino
 */
public interface Style {
	/**
	 * Il sistema di riferimento per i report si chiama inch. Questa unita' di misura vale 5.67 millimetri.<br>
	 * Quindi 1 inch = 5.67 mm<br>
	 * Nella classe StyleUtils sono presenti due metodi statici che eseguono queste conversioni.
	 */
	public static final float TWIPS_TO_MM = 5.67f;

	public void setStyle(TextPart t);

	public void setStyle(Paragraph p);

	public void setStyle(Section s);

	public void setStyle(Text t);

	public void setStyle(FieldPart t);

	/**
	 * 
	 * @return
	 */
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
	 *            in formato <strong>inch</strong>. Vedere field INCH_TO_MM.
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
	public void setAlign(int align);

	public int getAlign();

	public boolean isCanGrow();

	/**
	 * Setter for property canGrow.
	 * 
	 * @param canGrow
	 *            New value of property canGrow.
	 * 
	 */
	public void setCanGrow(boolean canGrow);

	public Object clone();

}
