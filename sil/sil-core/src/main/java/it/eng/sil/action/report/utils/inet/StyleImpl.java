/*
 * StyleImpl.java
 *
 * Created on 21 gennaio 2004, 7.26
 */

package it.eng.sil.action.report.utils.inet;

import com.inet.report.FieldPart;
import com.inet.report.GeneralProperties;
import com.inet.report.Paragraph;
import com.inet.report.RDC;
import com.inet.report.Section;
import com.inet.report.Text;
import com.inet.report.TextPart;

/**
 * Questa classe rappresenta lo stile da usare per stampare una stringa di testo. Inoltre vengono implementate delle
 * inner class con uno stile predefinito, stile in genere usato nelle stampe del sil. Tutti i valori sono espressi nell'
 * unita' di misura usata dalle api di crystal clear. Quindi, per esempio, le dimensioni sono espresse in inch e non in
 * millimetri.
 * 
 * @author Andrea Savino
 */
public class StyleImpl implements Style {
	/**
	 * 
	 */
	private int fontSize = 12;
	private int fontColor = RDC.COLOR_BLACK;
	private boolean fontItalic = false;
	private String fontName = "Arial";
	private int height = 248;
	private int width = 6000;
	private int align = GeneralProperties.ALIGN_HORIZONTAL_RIGHT;
	private boolean bold;
	private boolean canGrow;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StyleImpl.class.getName());

	/**
	 * 
	 */
	public StyleImpl() {
	}

	/**
	 * 
	 * @param fontSize
	 * @param fontColor
	 * @param fontItalic
	 * @param fontName
	 * @param height
	 *            (inch)
	 * @param width
	 *            (inch)
	 * @param align
	 * @param bold
	 * @param canGrow
	 */
	public StyleImpl(int fontSize, int fontColor, boolean fontItalic, String fontName, int height, int width, int align,
			boolean bold, boolean canGrow) {
		this.fontSize = fontSize;
		this.fontColor = fontColor;
		this.fontItalic = fontItalic;
		this.height = height;
		this.width = width;
		this.align = align;
		this.bold = bold;
		this.fontName = fontName;
		this.canGrow = canGrow;
	}

	/**
	 * imposta lo stile dell'istanza alla TextPart <strong>t</storng>.
	 */
	public void setStyle(TextPart t) {
		t.setFontSize(getFontSize());
		t.setFontColor(getFontColor());
		t.setItalic(isItalic());
		t.setHeight(getHeight());
		t.setFontName(getFontName());
		t.setBold(isBold());
	}

	public void setStyle(FieldPart t) {
		t.setFontSize(getFontSize());
		t.setFontColor(getFontColor());
		t.setItalic(isItalic());
		t.setHeight(getHeight());
		t.setFontName(getFontName());
		t.setBold(isBold());
	}

	/**
	 * imposta lo stile dell'istanza al Praragraph <strong>p</storng>
	 */
	public void setStyle(Paragraph p) {
		p.setForeColor(RDC.COLOR_PURPLE);
		p.setHorAlign(getAlign());
	}

	/**
	 * imposta lo stile dell'istanza alla Section <strong>s</storng>
	 */
	public void setStyle(Section s) {
		s.setHeight(getHeight());
	}

	/**
	 * imposta lo stile dell'istanza al Text <strong>t</storng>
	 */
	public void setStyle(Text t) {
		t.setCanGrow(isCanGrow());
	}

	public int getFontSyze() {
		return fontSize;
	}

	/**
	 * @return Value of property align. (Es.: GeneralProperties.RIGHT_ALIGN)
	 * 
	 */
	public int getAlign() {
		return align;
	}

	/**
	 * @param align
	 *            New value of property align. (Es.: GeneralProperties.RIGHT_ALIGN)
	 * 
	 */
	public void setAlign(int align) {
		this.align = align;
	}

	/**
	 * @return Value of property bold.
	 * 
	 */
	public boolean isBold() {
		return bold;
	}

	/**
	 * Setter for property bold.
	 * 
	 * @param bold
	 *            New value of property bold.
	 * 
	 */
	public void setBold(boolean bold) {
		this.bold = bold;
	}

	/**
	 * @return Value of property fontColor. (Es.: RDC.COLOR_BLACK)
	 * 
	 */
	public int getFontColor() {
		return fontColor;
	}

	/**
	 * @param fontColor
	 *            New value of property fontColor.(Es.: RDC.COLOR_BLACK)
	 * 
	 */
	public void setFontColor(int fontColor) {
		this.fontColor = fontColor;
	}

	/**
	 * @return Value of property fontItalic.
	 * 
	 */
	public boolean isItalic() {
		return fontItalic;
	}

	/**
	 * @param fontItalic
	 *            New value of property fontItalic.
	 * 
	 */
	public void setItalic(boolean fontItalic) {
		this.fontItalic = fontItalic;
	}

	/**
	 * @return Value of property fontSize.
	 * 
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize
	 *            New value of property fontSize.
	 * 
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * @return Value of property fontStile. (Es.: "Arial")
	 * 
	 */
	public java.lang.String getFontName() {
		return fontName;
	}

	/**
	 * @param fontStile
	 *            New value of property fontStile. (Es.: "Arial")
	 * 
	 */
	public void setFontName(java.lang.String fontName) {
		this.fontName = fontName;
	}

	/**
	 * @return Value of property hight in <strong>inch</strong>.
	 * 
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param hight
	 *            New value of property hight in <strong>inch</strong>.
	 * 
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return Value of property width in <strong>inch</strong>.
	 * 
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            New value of property width in <strong>inch</strong>.
	 * 
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return Value of property canGrow.
	 * 
	 */
	public boolean isCanGrow() {
		return canGrow;
	}

	/**
	 * @param canGrow
	 *            New value of property canGrow.
	 * 
	 */
	public void setCanGrow(boolean canGrow) {
		this.canGrow = canGrow;
	}

	/**
	 * L'oggetto viene duplicato. E' utile clonare una istanza di uno stile quando la si vuole modificare in alcune
	 * caratteristiche partendo da uno stile predefinito.
	 */
	public Object clone() {
		Object o = new StyleImpl(getFontSize(), getFontColor(), isItalic(), getFontName(), getHeight(), getWidth(),
				getAlign(), isBold(), isCanGrow());
		return o;
	}

}
