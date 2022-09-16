/*
 * Created on 8-mag-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package it.eng.afExt.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtils {

	public static final int FMT_IT = 0;
	public static final int FMT_EN = 1;
	public static final String FORMATO_DATA = "dd/MM/yyyy";

	/**
	 * Controlla se una stringa è una data valida valida FMT è il formato italiano o inglese (DD/MM/YYYY oppure
	 * MM/DD/YYYY) i valori da passare sono: FMT_IT | FMT_IT
	 */
	public static boolean isValidDate(String cDateStr, int fmt) {
		if (cDateStr == null)
			return false;

		if (cDateStr.length() < 8)
			return false;

		String cDay = "0", cMonth = "0", cYear = "0", cChar;
		boolean bValid = true;
		int iDay, iMonth, iYear, iPart = 1;

		for (int i = 0; i < cDateStr.length(); i++) {
			cChar = cDateStr.substring(i, i + 1);

			if (chkDigitsOnly(cChar, false, false)) {
				if (iPart == 1) {
					if (fmt == FMT_EN) {
						cMonth += cChar;
					} else {
						cDay += cChar;
					}
				}

				if (iPart == 2) {
					if (fmt == FMT_EN) {
						cDay += cChar;
					} else {
						cMonth += cChar;
					}
				}
				if (iPart == 3)
					cYear += cChar;
			} else {
				if (cChar.equals("/") || cChar.equals("-"))
					iPart++;
			}
		}

		iDay = Integer.parseInt(cDay) - 0;
		iMonth = Integer.parseInt(cMonth) - 0;
		iYear = Integer.parseInt(cYear) - 0;

		if (iDay > 0 && iMonth > 0 && iYear > 0) {
			if (iYear < 100) {
				iYear += 1900;

				if (iYear < 1930)
					iYear += 100;
			}

			bValid &= (iMonth > 0 && iMonth < 13);
			bValid &= ((iDay > 0) && (iDay <= daysInMonth(iMonth, iYear)));

		} else {
			bValid = false;
		}

		return bValid;
	}

	/**
	 * Calcola il numero di giorni di un mese (utile per sapere i giorni di febbraio di un certo anno)
	 */
	public static int daysInMonth(int iMonth, int iYear) {
		int iAnswer = 0;
		boolean bLeap;

		if (iMonth == 1 || iMonth == 3 || iMonth == 5 || iMonth == 7 || iMonth == 8 || iMonth == 10 || iMonth == 12)
			iAnswer = 31;
		else {
			if (iMonth == 4 || iMonth == 6 || iMonth == 9 || iMonth == 11)
				iAnswer = 30;
			else if (iMonth == 2) {
				bLeap = isLeapYear(iYear);
				iAnswer = 28 + (bLeap ? 1 : 0);
			}
		}

		return iAnswer;
	}

	/**
	 * Controlla se l' anno e' bisestile
	 */
	public static boolean isLeapYear(int iYear) {
		return ((((iYear % 4) == 0) && ((iYear % 100) != 0)) || ((iYear % 400) == 0));
	}

	public static boolean sameDates(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null)
			return false;
		else
			return (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
					&& cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
					&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
	}

	/**
	 * Calcola i giorni di differenza tra due date (cal2 - cal): <b>ATTENZIONE: CALCOLA SOLO LA DIFFERENZA TRA ANNI
	 * UGUALI.</b>
	 * 
	 * @return int -100 se una delle due date e' null, 0 se sono uguali, cal2 - cal altrimenti
	 * 
	 * @deprecated
	 */
	public static int daysBetween(Calendar cal1, Calendar cal2) {
		int iReturn, iDOY1, iDOY2;

		if (cal1 == null || cal2 == null)
			iReturn = -100;
		else {
			if (sameDates(cal1, cal2))
				iReturn = 0;
			else {
				iDOY1 = cal1.get(Calendar.DAY_OF_YEAR);
				iDOY2 = cal2.get(Calendar.DAY_OF_YEAR);

				if (cal1.before(cal2)) {
					if (iDOY1 < iDOY2)
						iReturn = iDOY2 - iDOY1;
					else
						iReturn = ((isLeapYear(cal1.get(Calendar.YEAR)) ? 366 : 365) - iDOY1) + iDOY2;
				} else {
					if (iDOY1 > iDOY2)
						iReturn = iDOY1 - iDOY2;
					else
						iReturn = ((isLeapYear(cal2.get(Calendar.YEAR)) ? 366 : 365) - iDOY2) + iDOY1;
				}
			}
		}

		return iReturn;
	}

	/**
	 * Calcola i giorni di differenza tra due date (cal2 - cal)
	 * 
	 * @return int -100 se una delle due date e' null, 0 se sono uguali, cal2 - cal altrimenti
	 */
	public static int daysBetween(String date, String date2) {
		int n = -1;
		long SECOND_MILLIS = 1000;
		long MINUTE_MILLIS = SECOND_MILLIS * 60;
		long HOUR_MILLIS = MINUTE_MILLIS * 60;
		long DAY_MILLIS = HOUR_MILLIS * 24;
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date d1 = df.parse(date);
			Date d2 = df.parse(date2);
			n = (int) ((d2.getTime() / DAY_MILLIS) - (d1.getTime() / DAY_MILLIS));
		} catch (Exception e) {
		}
		return n;
	}

	public static int daysInRange(Calendar cal1, Calendar cal2) {
		return (daysBetween(cal1, cal2) + 1);
	}

	public static boolean chkTime(String cTime) {
		if (cTime == null)
			return false;

		cTime = cTime.trim();

		boolean bReturn = true, bColon = false;
		String cChar, cIntCheck = "";

		// Check for invalid characters
		for (int i = 0; i < cTime.length(); i++) {
			cChar = cTime.substring(i, i + 1);

			if (chkDigitsOnly(cChar, false, false))
				cIntCheck += cChar;
			else {
				if (cChar.equals(":")) {
					if (bColon)
						bReturn = false;
					else
						bColon = true;
				} else
					bReturn = false;
			}

			if (bReturn == false)
				break;
		}

		if (bReturn) {
			// Check length
			if (cTime.length() < (bColon ? 4 : 3) || cTime.length() > (bColon ? 5 : 4))
				bReturn = false;

			// Check for valid time range
			if (bReturn)
				bReturn = (Integer.parseInt(cIntCheck) <= 2400);
		}

		return bReturn;
	}

	public static int[] parseTime(String cTime) {
		int[] aReturn = { -1, -1 };

		if (cTime == null)
			return aReturn;

		cTime = cTime.trim();

		if (chkTime(cTime) == false)
			return aReturn;

		// Time is now definitely valid
		int iColonAt = cTime.indexOf(":");

		if (iColonAt == -1) {
			if (cTime.length() == 3) {
				aReturn[0] = Integer.parseInt(cTime.substring(0, 1));
				aReturn[1] = Integer.parseInt(cTime.substring(1));
			} else {
				// length = 4
				aReturn[0] = Integer.parseInt(cTime.substring(0, 2));
				aReturn[1] = Integer.parseInt(cTime.substring(2));
			}
		} else {
			// Colon used
			if (cTime.length() == 4) {
				aReturn[0] = Integer.parseInt(cTime.substring(0, 1));
				aReturn[1] = Integer.parseInt(cTime.substring(2));
			} else {
				// length = 5
				aReturn[0] = Integer.parseInt(cTime.substring(0, 2));
				aReturn[1] = Integer.parseInt(cTime.substring(3));
			}
		}

		return aReturn;
	}

	public static boolean chkDigitsOnly(String cValue) {
		return chkDigitsOnly(cValue, false, false);
	}

	public static boolean chkDigitsOnly(String cValue, boolean bAllowComma, boolean bAllowDecimal) {
		if (cValue == null)
			return false;

		if (cValue.length() == 0)
			return false;

		boolean bReturn = true, bCurrValid;
		int iNextChar;

		for (int a = 0; a < cValue.length(); a++) {
			iNextChar = (int) cValue.charAt(a);

			bCurrValid = (iNextChar >= 48 && iNextChar <= 57);

			if (bCurrValid == false && bAllowComma)
				bCurrValid = (iNextChar == 44);

			if (bCurrValid == false && bAllowDecimal)
				bCurrValid = (iNextChar == 46);

			if (bCurrValid == false) {
				bReturn = false;
				break;
			}
		}

		return bReturn;
	}

	public static String getNow() {
		Date dataNow = new Date();
		String str = "";
		if (dataNow != null) {
			// formattatore per trasformare stringhe in date
			// SimpleDateFormat fd = new SimpleDateFormat(FORMATO_DATA);
			// str = fd.format(dataNow);
			//
			// // ORA INVOCO IL GARBAGE-COLLECTOR PERCHE' LA "SimpleDateFormat" NON PULISCE BENE LA MEMORIA
			// // SE NON LO SI FA, LA MEMORIA NON VERRA' PIU' DEALLOCATA E CONTINUERA' A CRESCERE LO SPAZIO OCCUPATO!
			// System.gc();

			str = format(dataNow);
		}
		return str;
	}

	/**
	 * given a String dd/mm/yyyy or dd-mm-yyyy, return the java.sql.Date class
	 */
	public static java.sql.Date convertStringToDate(String st) {
		String temp = "";
		if (st == null || st.trim().equals("")) {
			return null;
		}
		temp = st.substring(st.length() - 4) + st.substring(2, 6) + st.substring(0, 2);
		temp = temp.replace('/', '-');
		return java.sql.Date.valueOf(temp);
	}

	/**
	 * Get the difference between two date in days
	 */
	public static long getDayDiff(java.sql.Date di, java.sql.Date df) {
		if (di == null || df == null) {
			return 0;
		}
		return (long) (df.getTime() - di.getTime()) / (1000 * 60 * 60 * 24);
	}

	/** metodo statico che riceve una data in formato stringa e restituisce un oggetto date */
	public static Date getDate(String str) {
		Date data = null;
		if (str != null && !str.equalsIgnoreCase("")) {
			// formattatore per trasformare stringhe in date
			SimpleDateFormat fd = new SimpleDateFormat(FORMATO_DATA);
			data = fd.parse(str, new ParsePosition(0));
		}
		return data;
	}

	/**
	 * Restituisce l' anno in formato numerico
	 * 
	 * @return int anno
	 */
	public static int getAnno() {
		String data = getNow();
		return Integer.parseInt(data.substring(data.lastIndexOf("/") + 1));
	}

	public static int getAnno(String date) throws Exception {
		String anno = date.substring(date.lastIndexOf("/") + 1);
		int n = Integer.parseInt(anno);
		if (n < 100)
			n += 1900;
		return n;
	}

	public static String giornoPrecedente(String date) throws Exception {
		String ret = "";
		int[] dateA = DateUtils.split(date);
		int dd = dateA[0];
		int mm = dateA[1];
		int yy = dateA[2];
		if (dd <= 1) {
			if (mm <= 1) {
				dd = 31;
				mm = 12;
				yy--;
			} else {
				mm--;
				dd = daysInMonth(mm, yy);
			}
		} else {
			dd--;
		}
		ret = (dd < 10 ? ("0" + dd) : "" + dd) + "/" + (mm < 10 ? "0" + mm : "" + mm) + "/" + yy;
		return ret;
	}

	public static String giornoSuccessivo(String date) throws Exception {
		String ret = "";
		int[] dateA = DateUtils.split(date);
		int dd = dateA[0];
		int mm = dateA[1];
		int yy = dateA[2];
		if (mm == 12 && dd == 31) {
			dd = 1;
			mm = 1;
			yy++;
		} else {
			if (daysInMonth(mm, yy) == dd) {
				dd = 1;
				mm++;
			} else {
				dd++;
			}
		}
		ret = (dd < 10 ? ("0" + dd) : "" + dd) + "/" + (mm < 10 ? "0" + mm : "" + mm) + "/" + yy;
		return ret;
	}

	/**
	 * @author riccardi --- @return la data in formato String aggiunta del numero di giorni indicati
	 */
	public static String aggiungiNumeroGiorni(String dateold, int numGiorniDaAgg) throws Exception {
		try {
			Date datetemp = DateUtils.getDate(dateold);
			Calendar date = GregorianCalendar.getInstance();
			date.setTime(datetemp);
			date.add(Calendar.DATE, numGiorniDaAgg);
			SimpleDateFormat fd = new SimpleDateFormat(FORMATO_DATA);
			return fd.format(date.getTime());
		} catch (Exception e) {
			throw new Exception("Il formato della data non rientra tra quelli standard");
		}
	}

	/**
	 * @return un vettore di 3 interi cosi' costituito: {gg, mm, aaaa}
	 */
	public static int[] split(String date) throws Exception {
		int a = date.indexOf("/");
		int b = date.lastIndexOf("/");
		String giorno = date.substring(0, a);
		String mese = date.substring(a + 1, b);
		String anno = date.substring(b + 1);
		return new int[] { Integer.parseInt(giorno), Integer.parseInt(mese), Integer.parseInt(anno) };
	}

	/**
	 * @return -1 se date1 < date2, 0 se date1 = date2, 1 se date1 > date2
	 */
	public static int compare(String date1, String date2) throws Exception {
		int ret = 0;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		// SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		/*
		 * Calendar cal1 = Calendar.getInstance(); Calendar cal2 = Calendar.getInstance();
		 */
		/*
		 * cal1.setTime(df.parse(date1)); cal2.setTime(df.parse(date2)); if ( cal1.before(cal2)) return -1; else if
		 * (cal1.after(cal2)) return 1; else return 0;
		 */
		Date d1 = df.parse(date1);
		Date d2 = df.parse(date2);
		if (d1.before(d2))
			ret = -1;
		else if (d1.after(d2))
			ret = 1;
		else
			ret = 0;
		d1 = null;
		d2 = null;
		df = null;
		return ret;

	}

	/**
	 * @return -1 se date1 < date2, 0 se date1 = date2, 1 se date1 > date2
	 */
	public static int compare(Date date1, Date date2) throws Exception {
		return date1.compareTo(date2);
	}
	/*
	 * public static void main(String[] args) {
	 * 
	 * //isValidDate("28/02/2003",DateUtils.FMT_IT);//OK //isValidDate("29/02/2003",DateUtils.FMT_IT);//KO
	 * isValidDate("1/02/2003",DateUtils.FMT_IT); isValidDate("1/02/abc",DateUtils.FMT_IT); }
	 */

	/**
	 * @return gli anni che intercorrono tra la data corrente e la data passata come parametro.
	 */
	public static int getEta(String data) {
		int eta = 0;
		;
		int yy;
		int dd;
		int mm;
		int yyOdierno;
		int ddOdierno;
		int mmOdierno;

		if ((data != null) && !data.equals("")) {
			String dataOdierna = getNow();

			yyOdierno = Integer.parseInt(dataOdierna.substring(6, 10));
			ddOdierno = Integer.parseInt(dataOdierna.substring(0, 2));
			mmOdierno = Integer.parseInt(dataOdierna.substring(3, 5));

			yy = Integer.parseInt(data.substring(6, 10));
			dd = Integer.parseInt(data.substring(0, 2));
			mm = Integer.parseInt(data.substring(3, 5));

			eta = yyOdierno - yy;
			if (mmOdierno < mm) {
				eta = eta - 1;
			} else {
				if (mmOdierno == mm) {
					if (ddOdierno < dd) {
						eta = eta - 1;
					}
				}
			}
		}
		return eta;
	}

	/**
	 * 
	 * @param dataNascita
	 * @param dataRif
	 * @return gli anni che intercorrono tra la data di nascita e la data passata (dataRif)
	 */
	public static int getEta(String dataNascita, String dataRif) throws Exception {
		int eta = 0;
		;
		int yy;
		int dd;
		int mm;
		int yyRif;
		int ddRif;
		int mmRif;

		if ((dataNascita == null) || (dataNascita.equals("")) || (dataRif == null) || (dataRif.equals(""))
				|| (DateUtils.compare(dataNascita, dataRif) > 0)) {
			return eta;
		}
		// Posso procedere al calcolo dell'età
		yyRif = Integer.parseInt(dataRif.substring(6, 10));
		ddRif = Integer.parseInt(dataRif.substring(0, 2));
		mmRif = Integer.parseInt(dataRif.substring(3, 5));

		yy = Integer.parseInt(dataNascita.substring(6, 10));
		dd = Integer.parseInt(dataNascita.substring(0, 2));
		mm = Integer.parseInt(dataNascita.substring(3, 5));

		eta = yyRif - yy;
		if (mmRif < mm) {
			eta = eta - 1;
		} else {
			if (mmRif == mm) {
				if (ddRif < dd) {
					eta = eta - 1;
				}
			}
		}
		return eta;
	}

	/**
	 * restituisce il numero di mesi tra due date compreso il mese iniziale e finale indipendentemente dai giorni dei
	 * mesi iniziale e finale
	 * 
	 * @param date
	 * @param date2
	 * @return
	 */
	public static int monthsBetween(String date, String date2) throws Exception {
		int yyInizio;
		int yyFine;
		int mmInizio;
		int mmFine;
		int mesi = 0;
		if (DateUtils.compare(date, date2) > 0)
			return 0;
		yyInizio = Integer.parseInt(date.substring(6, 10));
		yyFine = Integer.parseInt(date2.substring(6, 10));
		mmInizio = Integer.parseInt(date.substring(3, 5));
		mmFine = Integer.parseInt(date2.substring(3, 5));
		// controllare date > date2
		if (yyInizio == yyFine) {
			mesi = (mmFine - mmInizio) + 1;
		} else {
			mesi = (12 - mmInizio) + 1;
			mesi = mesi + ((yyFine - yyInizio) * 12) - (12 - mmFine);
		}
		return mesi;
	}

	public static int monthsBetweenFornero(String date, String date2) throws Exception {
		int yyInizio;
		int yyFine;
		int mmInizio;
		int mmFine;
		int ggInizio;
		int ggFine;
		int mesi = 0;
		if (DateUtils.compare(date, date2) > 0) {
			return 0;
		}
		yyInizio = Integer.parseInt(date.substring(6, 10));
		yyFine = Integer.parseInt(date2.substring(6, 10));
		mmInizio = Integer.parseInt(date.substring(3, 5));
		mmFine = Integer.parseInt(date2.substring(3, 5));
		// controllare date > date2
		if (yyInizio == yyFine) {
			mesi = (mmFine - mmInizio);
		} else {
			mesi = (12 - mmInizio);
			mesi = mesi + (((yyFine - yyInizio) - 1) * 12) + mmFine;
		}

		ggInizio = Integer.parseInt(date.substring(0, 2));
		ggFine = Integer.parseInt(date2.substring(0, 2));

		// dal 10/meseInizio al 11/meseFine oppure giorno x/meseinizio --> giorno x/mesefine
		if (ggInizio < ggFine) {
			mesi = mesi + 1;
		} else {
			// 31/08 --> 01/03
			if (ggInizio > ggFine) {
				if (ggInizio == 31 || ggInizio == 30) {
					if (ggFine == 1) {
						// se l'ultimo giorno del mese iniziale > ultimo giorno del mese finale (dal 31/mese iniziale al
						// 30/mese precedente quello finale)
						// bisogna togliere 1 (calcolo non fatto se il mmFine = gennaio)
						if ((mmFine - 2) >= 0) {
							Calendar c = Calendar.getInstance();
							c.set(Calendar.YEAR, yyFine);
							// mese (value between 0-11)
							c.set(Calendar.MONTH, (mmFine - 2));
							int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
							if (ggInizio > lastDay) {
								mesi = mesi - 1;
							}
						}
					}
				} else {
					if (ggInizio == 29) {
						if (mmFine == 3) {
							if (ggFine == 1) {
								// ultimo giorno del mese di febbraio
								Calendar c = Calendar.getInstance();
								c.set(Calendar.YEAR, yyFine);
								// mese (value between 0-11)
								c.set(Calendar.MONTH, mmFine - 2);
								int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
								// 29/08/2013 --> 01/03/2014
								if (lastDay < ggInizio) {
									mesi = mesi - 1;
								}
							}
						}
					}
				}
			}
		}

		if (mesi == 0) {
			// stesso x/meseinizio/annoinizio --> x/meseinizio/annoinizio
			mesi = 1;
		}

		return mesi;
	}

	/**
	 * 
	 * @param date
	 * @param date2
	 * @return
	 * @throws Exception
	 */
	public static int monthsBetween150(String date, String date2) throws Exception {
		int yyInizio;
		int yyFine;
		int mmInizio;
		int mmFine;
		int ggInizio;
		int ggFine;
		int mesi = 0;
		if (DateUtils.compare(date, date2) > 0) {
			return 0;
		}
		yyInizio = Integer.parseInt(date.substring(6, 10));
		yyFine = Integer.parseInt(date2.substring(6, 10));
		mmInizio = Integer.parseInt(date.substring(3, 5));
		mmFine = Integer.parseInt(date2.substring(3, 5));
		// controllare date > date2
		if (yyInizio == yyFine) {
			mesi = (mmFine - mmInizio);
		} else {
			mesi = (12 - mmInizio);
			mesi = mesi + (((yyFine - yyInizio) - 1) * 12) + mmFine;
		}

		ggInizio = Integer.parseInt(date.substring(0, 2));
		ggFine = Integer.parseInt(date2.substring(0, 2));

		if (ggInizio <= ggFine) {
			mesi = mesi + 1;
		}

		return mesi;
	}

	public static final SimpleDateFormat getSimpleDateFormatFixBugMem(String pattern) {

		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		if (dateFormat instanceof SimpleDateFormat) {
			((SimpleDateFormat) dateFormat).applyPattern(pattern);
		} else {
			dateFormat = new SimpleDateFormat(pattern); // MAH
		}
		return (SimpleDateFormat) dateFormat;
	}

	/**
	 * 
	 * @param dateold
	 * @param numMesi
	 * @return dateNew(formato String) = dateold + numMesi
	 * @throws Exception
	 */
	public static String aggiungiMesi(String dateold, int numMesi, int giorniDiff) throws Exception {
		try {
			Date datetemp = DateUtils.getDate(dateold);
			Calendar date = GregorianCalendar.getInstance();
			date.setTime(datetemp);
			date.add(Calendar.MONTH, numMesi);
			if (giorniDiff < 0) {
				date.add(Calendar.DATE, giorniDiff);
			}
			SimpleDateFormat fd = new SimpleDateFormat(FORMATO_DATA);
			return fd.format(date.getTime());
		} catch (Exception e) {
			throw new Exception("Il formato della data non rientra tra quelli standard");
		}
	}

	public static String format(Date d) {
		return getSimpleDateFormatFixBugMem(FORMATO_DATA).format(d);
	}

	/*
	 * Converts XMLGregorianCalendar to java.util.Date in Java
	 */
	private static Date xMLGregorianToDate(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	/*
	 * Converts XMLGregorianCalendar to String in Java
	 */
	public static String formatXMLGregorian(XMLGregorianCalendar calendar) {
		if (calendar == null)
			return "";
		return format(xMLGregorianToDate(calendar));
	}

	/*
	 * Converts XMLGregorianCalendar in Java to String
	 */
	public static XMLGregorianCalendar stringToGregorianDate(String date)
			throws DatatypeConfigurationException, ParseException {

		if (date == null) {
			return null;
		}

		DateFormat dfIn = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd");
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(dfOut.format(dfIn.parse(date)));

	}

	public static int monthsBetween(Calendar inizioCalendar, Calendar fineCalendar) {
		int diffMonth = -1;
		int yyInizio = inizioCalendar.get(Calendar.YEAR);
		int yyFine = fineCalendar.get(Calendar.YEAR);
		int mmInizio = inizioCalendar.get(Calendar.MONTH);
		int mmFine = fineCalendar.get(Calendar.MONTH);
		int ggInizio = inizioCalendar.get(Calendar.DAY_OF_MONTH);
		int ggFine = fineCalendar.get(Calendar.DAY_OF_MONTH);

		if (yyInizio == yyFine) {
			diffMonth = mmFine - mmInizio;
			if (ggFine < ggInizio) {
				diffMonth = diffMonth - 1;
			}
		} else {
			diffMonth = (12 - mmInizio);
			diffMonth = diffMonth + (((yyFine - yyInizio) - 1) * 12) + mmFine;
			if (ggFine < ggInizio) {
				diffMonth = diffMonth - 1;
			}
		}

		return diffMonth;
	}

	public static XMLGregorianCalendar toXMLGregorianCalendarDate(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}

	public static XMLGregorianCalendar datestringToXml(String dateFromDB)
			throws ParseException, DatatypeConfigurationException {
		try {
			XMLGregorianCalendar xmlDateDB = null;
			if (dateFromDB != null && !"".equals(dateFromDB)) {
				DateFormat dfIn = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				xmlDateDB = DatatypeFactory.newInstance().newXMLGregorianCalendar(dfOut.format(dfIn.parse(dateFromDB)));
			}
			return xmlDateDB;
		} catch (ParseException epe) {
			throw epe;
		} catch (DatatypeConfigurationException edte) {
			throw edte;
		}
	}
}
