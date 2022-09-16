package it.eng.sil.action.report.test;

//package report;

public class StyleFactory {
	static private Style etichetta = new StyleImpl.Etichetta();
	static private Style etichetta2 = new StyleImpl.Etichetta2();
	static private Style etichetta3 = new StyleImpl.Etichetta3();
	static private Style campo = new StyleImpl.Campo();
	static private Style titoloSezione = new StyleImpl.TitoloSezione();
	static private Style titolo2 = new StyleImpl.Titolo2();
	static private Style titolo3 = new StyleImpl.Titolo3();
	static private Style titolo2Centrato = new StyleImpl.Titolo2Centrato();

	public static Style getStyle(int style) {
		switch (style) {
		case Style.CAMPO: {
			if (campo == null)
				campo = new StyleImpl.Campo();
			return campo;
		}
		case Style.ETICHETTA: {
			if (etichetta == null)
				etichetta = new StyleImpl.Etichetta();
			return etichetta;
		}
		case Style.TITOLO_SEZIONE:
			return titoloSezione;
		case Style.ETICHETTA2:
			return etichetta2;
		case Style.ETICHETTA3:
			return etichetta3;
		case Style.TITOLO2: {
			if (titolo2 == null)
				titolo2 = new StyleImpl.Titolo2();
			return titolo2;
		}
		case Style.TITOLO3:
			return titolo3;
		case Style.TITOLO2CENTRATO:
			return titolo2Centrato;
		default:
			return null;
		}

	}

	public static void reset() {
		campo = null;
		etichetta = null;
	}

	public static Style makeStyle(int style) {
		Style s = null;
		switch (style) {
		case Style.CAMPO:
			s = new StyleImpl.Campo();
			break;
		case Style.ETICHETTA:
			s = new StyleImpl.Etichetta();
			break;
		case Style.TITOLO_SEZIONE:
			s = new StyleImpl.TitoloSezione();
			break;
		case Style.ETICHETTA2:
			s = new StyleImpl.Etichetta2();
			break;
		case Style.ETICHETTA3:
			s = new StyleImpl.Etichetta3();
			break;
		case Style.TITOLO2:
			s = new StyleImpl.Titolo2();
			break;
		case Style.TITOLO3:
			s = new StyleImpl.Titolo3();
			break;
		case Style.TITOLO2CENTRATO:
			s = new StyleImpl.Titolo2Centrato();
			break;
		default:
			return null;
		}
		return s;
	}

	public static void main(String[] a) {
		Style s = StyleFactory.getStyle(Style.ETICHETTA);
		System.out.println(s.getFontSyze());
	}
}
