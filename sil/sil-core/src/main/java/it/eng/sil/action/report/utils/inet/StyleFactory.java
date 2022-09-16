package it.eng.sil.action.report.utils.inet;

import com.inet.report.GeneralProperties;
import com.inet.report.RDC;

/**
 * Metodi di factories per gli Stili.
 * 
 * @author Andrea Savino
 */
public class StyleFactory {
	private Style etichetta = new Etichetta();
	private Style etichetta2 = new Etichetta2();
	private Style etichetta3 = new Etichetta3();
	private Style etichetta4 = new Etichetta4();
	private Style campo = new Campo();
	private Style campo1 = new Campo1();
	private Style campo2 = new Campo2();
	private Style campo4 = new Campo4();
	private Style titoloSezione = new TitoloSezione();
	private Style titolo2 = new Titolo2();
	private Style titolo3 = new Titolo3();
	private Style titolo2Centrato = new Titolo2Centrato();
	private Style titolo3Centrato = new Titolo2Centrato();

	static class Etichetta extends StyleImpl {
		public Etichetta() {
			super(11, RDC.COLOR_BLACK, false, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(1870),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, false, true);
		}

	}

	static class Etichetta4 extends StyleImpl {
		public Etichetta4() {
			super(11, RDC.COLOR_BLACK, false, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(1870),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, true);
		}

	}

	public static class Campo extends StyleImpl {
		public Campo() {
			super(11, RDC.COLOR_BLACK, true, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(1870),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

	}

	public static class Campo1 extends StyleImpl {
		public Campo1() {
			super(10, RDC.COLOR_BLACK, false, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(1870),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, false, false);
		}

	}

	public static class Campo2 extends StyleImpl {
		public Campo2() {
			super(10, RDC.COLOR_BLACK, false, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(1870),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

	}

	public static class Campo4 extends StyleImpl {
		public Campo4() {
			super(11, RDC.COLOR_BLACK, false, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(1870),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

	}

	public static class TitoloSezione extends StyleImpl {
		public TitoloSezione() {
			super(12, RDC.COLOR_BLACK, false, "Arial", StyleUtils.toTwips(53), StyleUtils.toTwips(1200),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

	}

	public static class Titolo2 extends StyleImpl {
		public Titolo2() {
			super(12, RDC.COLOR_BLACK, true, "Times New Roman", StyleUtils.toTwips(53), StyleUtils.toTwips(1500),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

	}

	public static class Titolo2Centrato extends StyleImpl {
		public Titolo2Centrato() {
			super(12, RDC.COLOR_BLACK, false, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(2000),
					GeneralProperties.ALIGN_HORIZONTAL_CENTER, true, false);
		}

	}

	public static class Titolo3Centrato extends StyleImpl {
		public Titolo3Centrato() {
			super(7, RDC.COLOR_BLACK, false, "Times New Roman", StyleUtils.toTwips(42), StyleUtils.toTwips(1880),
					GeneralProperties.ALIGN_HORIZONTAL_CENTER, true, false);
		}

	}

	public static class Titolo3 extends StyleImpl {
		public Titolo3() {
			super(10, RDC.COLOR_BLACK, false, "Arial", StyleUtils.toTwips(50), StyleUtils.toTwips(1200),
					GeneralProperties.ALIGN_HORIZONTAL_LEFT, true, false);
		}

	}

	public static class Etichetta2 extends Etichetta {
		public Etichetta2() {
			setWidth(StyleUtils.toTwips(600));
		}
	}

	public static class Etichetta3 extends Etichetta {
		public Etichetta3() {
			setWidth(StyleUtils.toTwips(600));
			setAlign(GeneralProperties.ALIGN_HORIZONTAL_LEFT);
		}
	}

	public StyleFactory() {
	}

	/**
	 * Viene restituito il riferimento all'istanza condivisa dello stile richiesto
	 * 
	 * @param style
	 *            il codice dello stile richiesto. Es.: StyleImpl.ETICHETTA
	 * @return l'istanza dello stile richiesto.
	 */
	public Style getStyle(int style) {
		switch (style) {
		case CAMPO: {
			if (campo == null)
				campo = new Campo();
			return campo;
		}
		case CAMPO1: {
			if (campo1 == null)
				campo1 = new Campo1();
			return campo1;
		}
		case CAMPO2: {
			if (campo2 == null)
				campo2 = new Campo2();
			return campo2;
		}
		case CAMPO4: {
			if (campo4 == null)
				campo4 = new Campo4();
			return campo4;
		}
		case ETICHETTA: {
			if (etichetta == null)
				etichetta = new Etichetta();
			return etichetta;
		}
		case TITOLO_SEZIONE:
			return titoloSezione;
		case ETICHETTA2:
			return etichetta2;
		case ETICHETTA3:
			return etichetta3;
		case ETICHETTA4:
			return etichetta4;
		case TITOLO2: {
			if (titolo2 == null)
				titolo2 = new Titolo2();
			return titolo2;
		}
		case TITOLO3:
			return titolo3;
		case TITOLO2CENTRATO:
			return titolo2Centrato;

		case TITOLO3CENTRATO:
			return titolo3Centrato;
		default:
			return null;
		}

	}

	/**
	 * Viene restituira una nuova instanza dello style associato al codice intero <strong>style</strong>
	 * 
	 * @param style
	 *            il codice dello stile richiesto. Es.: StyleImpl.ETICHETTA
	 * @return l'istanza dello stile richiesto.
	 */
	public Style makeStyle(int style) {
		Style s = null;
		switch (style) {
		case CAMPO:
			s = new Campo();
			break;
		case ETICHETTA:
			s = new Etichetta();
			break;
		case TITOLO_SEZIONE:
			s = new TitoloSezione();
			break;
		case ETICHETTA2:
			s = new Etichetta2();
			break;
		case ETICHETTA3:
			s = new Etichetta3();
			break;
		case ETICHETTA4:
			return etichetta4;
		case TITOLO2:
			s = new Titolo2();
			break;
		case TITOLO3:
			s = new Titolo3();
			break;
		case TITOLO2CENTRATO:
			s = new Titolo2Centrato();
			break;
		case TITOLO3CENTRATO:
			s = new Titolo3Centrato();
			break;
		case CAMPO1:
			s = new Campo1();
			break;
		case CAMPO2:
			s = new Campo2();
			break;
		default:
			return null;
		}
		return s;
	}

	/**
	 * Codice stile
	 */
	public static final int ETICHETTA = 0;
	/**
	 * Codice stile
	 */
	public static final int CAMPO = 1;
	/**
	 * Codice stile
	 */
	public static final int TITOLO_SEZIONE = 2;
	/**
	 * Codice stile
	 */
	public static final int ETICHETTA2 = 3;
	/**
	 * Codice stile
	 */
	public static final int ETICHETTA3 = 4;
	/**
	 * Codice stile
	 */
	public static final int TITOLO2 = 5;
	/**
	 * Codice stile
	 */
	public static final int TITOLO2CENTRATO = 6;
	/**
	 * Codice stile
	 */
	public static final int TITOLO3 = 7;
	/**
	 * Codice stile
	 */
	public static final int CAMPO1 = 8;
	/**
	 * Codice stile
	 */
	public static final int CAMPO2 = 9;

	public static final int ETICHETTA4 = 10;
	public static final int TITOLO3CENTRATO = 11;

	public static final int CAMPO4 = 12;
}
