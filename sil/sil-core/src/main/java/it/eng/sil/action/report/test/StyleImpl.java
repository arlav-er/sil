/*
 * StyleImpl.java
 *
 * Created on 21 gennaio 2004, 7.26
 */

package it.eng.sil.action.report.test;

import com.inet.report.GeneralProperties;
import com.inet.report.Paragraph;
import com.inet.report.RDC;
import com.inet.report.Section;
import com.inet.report.Text;
import com.inet.report.TextPart;

/**
 * 
 * @author Administrator
 */
public abstract class StyleImpl implements Style {

	private final static String CC_ERROR = "Errore nell'engine di CrystalClear";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StyleImpl.class.getName());

	public static class Etichetta extends StyleImpl {
		public Etichetta() {
			super(11, RDC.COLOR_BLACK, false, "Times New Roman", 42, 1870, GeneralProperties.ALIGN_HORIZONTAL_LEFT,
					false, true);
		}

		protected void postInit() {
		}
	}

	public static class Campo extends StyleImpl {
		public Campo() {
			super(11, RDC.COLOR_BLACK, true, "Times New Roman", 42, 1870, GeneralProperties.ALIGN_HORIZONTAL_LEFT, true,
					false);
		}

		protected void postInit() {
		}
	}

	public static class TitoloSezione extends StyleImpl {
		public TitoloSezione() {
			super(12, RDC.COLOR_BLACK, false, "Arial", 53, 1200, GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

		protected void postInit() {
		}
	}

	public static class Titolo2 extends StyleImpl {
		public Titolo2() {
			super(12, RDC.COLOR_BLACK, true, "Times New Roman", 53, 1500, GeneralProperties.ALIGN_HORIZONTAL_LEFT, true,
					false);
		}

		protected void postInit() {
		}
	}

	public static class Titolo2Centrato extends StyleImpl {
		public Titolo2Centrato() {
			super(12, RDC.COLOR_BLACK, false, "Arial", 53, 2000, GeneralProperties.ALIGN_HORIZONTAL_CENTER, true,
					false);
		}

		protected void postInit() {
		}
	}

	public static class Titolo3 extends StyleImpl {
		public Titolo3() {
			super(10, RDC.COLOR_BLACK, false, "Arial", 50, 1200, GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

		protected void postInit() {
		}
	}

	public static class Etichetta2 extends Etichetta {
		protected void postInit() {
			setWidth(StyleUtils.toInch(600));
		}
	}

	public static class Etichetta3 extends Etichetta {
		protected void postInit() {
			setWidth(StyleUtils.toInch(600));
			setAlign(GeneralProperties.ALIGN_HORIZONTAL_LEFT);
		}
	}

	/**
	 * 
	 */

	public StyleImpl(int fontSize, int fontColor, boolean fontItalic, String fontName, int hight, int width, int align,
			boolean bold, boolean canGrow) {
		this.fontSize = fontSize;
		this.fontColor = fontColor;
		this.fontItalic = fontItalic;
		this.hight = StyleUtils.toInch(hight);
		this.width = StyleUtils.toInch(width);
		this.align = align;
		this.bold = bold;
		this.fontName = fontName;
		this.canGrow = canGrow;
		postInit();
	}

	protected abstract void postInit();

	public void setStyle(TextPart t) {
		t.setFontSize(getFontSize());
		t.setItalic(isItalic());

		t.setFontColor(getFontColor());
		t.setHeight(getHeight());

		// t.setWidth(getWidth());
		t.setFontName(getFontName());
		t.setBold(isBold());
	}

	public void setStyle(Paragraph p) {
		p.setHorAlign(getAlign());

	}

	public void setStyle(Section s) {
		s.setHeight(getHeight());
	}

	public void setStyle(Text t) {
		t.setCanGrow(isCanGrow());
	}

	public int fontSize = 12;
	public int fontColor = RDC.COLOR_BLACK;
	public boolean fontItalic = false;
	public String fontName = "Arial";
	public int hight = 248;
	public int width = 6000;
	public int align = GeneralProperties.ALIGN_HORIZONTAL_RIGHT;
	public boolean bold;
	public boolean canGrow;

	public int getFontSyze() {
		return fontSize;
	}

	/**
	 * Getter for property align.
	 * 
	 * @return Value of property align.
	 * 
	 */
	public int getAlign() {
		return align;
	}

	/**
	 * Setter for property align.
	 * 
	 * @param align
	 *            New value of property align.
	 * 
	 */
	public void setAlign(int align) {
		this.align = align;
	}

	/**
	 * Getter for property bold.
	 * 
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
	 * Getter for property fontColor.
	 * 
	 * @return Value of property fontColor.
	 * 
	 */
	public int getFontColor() {
		return fontColor;
	}

	/**
	 * Setter for property fontColor.
	 * 
	 * @param fontColor
	 *            New value of property fontColor.
	 * 
	 */
	public void setFontColor(int fontColor) {
		this.fontColor = fontColor;
	}

	/**
	 * Getter for property fontItalic.
	 * 
	 * @return Value of property fontItalic.
	 * 
	 */
	public boolean isItalic() {
		return fontItalic;
	}

	/**
	 * Setter for property fontItalic.
	 * 
	 * @param fontItalic
	 *            New value of property fontItalic.
	 * 
	 */
	public void setItalic(boolean fontItalic) {
		this.fontItalic = fontItalic;
	}

	/**
	 * Getter for property fontSize.
	 * 
	 * @return Value of property fontSize.
	 * 
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * Setter for property fontSize.
	 * 
	 * @param fontSize
	 *            New value of property fontSize.
	 * 
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Getter for property fontStile.
	 * 
	 * @return Value of property fontStile.
	 * 
	 */
	public java.lang.String getFontName() {
		return fontName;
	}

	/**
	 * Setter for property fontStile.
	 * 
	 * @param fontStile
	 *            New value of property fontStile.
	 * 
	 */
	public void setFontName(java.lang.String fontName) {
		this.fontName = fontName;
	}

	/**
	 * Getter for property hight.
	 * 
	 * @return Value of property hight.
	 * 
	 */
	public int getHeight() {
		return hight;
	}

	/**
	 * Setter for property hight.
	 * 
	 * @param hight
	 *            New value of property hight.
	 * 
	 */
	public void setHeight(int hight) {
		this.hight = hight;
	}

	/**
	 * Getter for property width.
	 * 
	 * @return Value of property width.
	 * 
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Setter for property width.
	 * 
	 * @param width
	 *            New value of property width.
	 * 
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Getter for property canGrow.
	 * 
	 * @return Value of property canGrow.
	 * 
	 */
	public boolean isCanGrow() {
		return canGrow;
	}

	/**
	 * Setter for property canGrow.
	 * 
	 * @param canGrow
	 *            New value of property canGrow.
	 * 
	 */
	public void setCanGrow(boolean canGrow) {
		this.canGrow = canGrow;
	}
}
