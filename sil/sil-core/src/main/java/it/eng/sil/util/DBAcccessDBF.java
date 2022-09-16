/*
 * Creato il 9-feb-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * @author Landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DBAcccessDBF {
	private File file;
	private String tipoAccesso;
	private RandomAccessFile dbf;
	private int curRec = 1;
	private int dbfType;
	private int dbfLastUpdateYear;
	private int dbfLastUpdateMonth;
	private int dbfLastUpdateDay;
	private int dbfNumberRecs;
	private int dbfDataPosition;
	private int dbfDataLength;
	private int dbfNumberFields;
	private fieldHeader dbfFields[];
	private Vector fieldName = null;

	public DBAcccessDBF(File f, String rw) {
		this.file = f;
		this.tipoAccesso = rw;
	}

	public void open() throws IOException {
		dbf = new RandomAccessFile(this.file, tipoAccesso);
		init();
	}

	public void close() throws IOException {
		dbf.close();
	}

	public int recno() {
		return curRec;
	}

	public void gotop() {
		curRec = 1;
	}

	public void gobottom() {
		curRec = dbfNumberRecs;
	}

	public void skip() {
		curRec++;
		if (curRec > dbfNumberRecs + 1)
			curRec = dbfNumberRecs + 1;
	}

	public int getDbfNumberRecs() {
		return dbfNumberRecs;
	}

	public int getDbfNumberFields() {
		return dbfNumberFields;
	}

	public Vector getFieldName() {
		return fieldName;
	}

	public int readBackwardsInt() throws IOException {
		int ch4 = dbf.read();
		int ch3 = dbf.read();
		int ch2 = dbf.read();
		int ch1 = dbf.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	public int readBackwardsUnsignedShort() throws IOException {
		int ch2 = dbf.read();
		int ch1 = dbf.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();
		return (ch1 << 8) + (ch2 << 0);
	}

	public void init() throws IOException {
		// Informazioni dell'header
		dbfType = dbf.readUnsignedByte();
		dbfLastUpdateYear = (byte) dbf.read();
		dbfLastUpdateMonth = (byte) dbf.read();
		dbfLastUpdateDay = (byte) dbf.read();
		dbfNumberRecs = readBackwardsInt();
		dbfDataPosition = readBackwardsUnsignedShort();
		dbfDataLength = readBackwardsUnsignedShort();
		dbfNumberFields = (dbfDataPosition - 33) / 32;
		dbf.seek(32);
		// Struttura campi
		byte fieldNameBuffer[] = new byte[11];
		int locn = 0;
		// Il primo campo e' il campo di deleted. Non è in struttura, ma
		// esiste e se contiene '*' il record e' cancellato, se ' ' il record
		// e' valido
		dbfFields = new fieldHeader[dbfNumberFields + 1];
		dbfFields[0] = new fieldHeader();
		dbfFields[0].fieldName = "@DELETED@";
		dbfFields[0].dataType = 'C';
		dbfFields[0].displacement = 0;
		locn += (dbfFields[0].length = 1);
		dbfFields[0].decimal = 0;
		fieldName = new Vector();
		// Ciclo di lettura dei campi
		for (int i = 1; i <= dbfNumberFields; i++) {
			dbfFields[i] = new fieldHeader();
			// Nome
			dbf.read(fieldNameBuffer);
			dbfFields[i].fieldName = new String(fieldNameBuffer);
			int posZero = dbfFields[i].fieldName.indexOf(0);
			dbfFields[i].fieldName = dbfFields[i].fieldName.substring(0, posZero);
			// Tipo
			dbfFields[i].dataType = (char) dbf.read();
			// Lunghezza
			dbf.skipBytes(4);
			dbfFields[i].displacement = locn;
			locn += (dbfFields[i].length = dbf.read());
			// Decimali
			if (dbfFields[i].dataType == 'N') {
				dbfFields[i].decimal = (byte) dbf.read();
			} else {
				dbfFields[i].decimal = 0;
				int len = (byte) dbf.read();
				dbfFields[i].length += len * 256;
			}
			fieldName.add(dbfFields[i].fieldName);
			dbf.skipBytes(14);
		}
	}

	public String readValue(int i, int j) throws IOException {
		curRec = i;
		int posInizialeRec = dbfDataPosition + ((recno() - 1) * dbfDataLength);
		dbf.seek(posInizialeRec + dbfFields[j].displacement);
		byte dataBuffer[] = new byte[dbfFields[j].length];
		dbf.read(dataBuffer);
		if (dbfFields[j].dataType == 'D') {
			// il formato yyyy/mm/gg deve essere convertito in gg/mm/yyyy
			String dataValue = new String(dataBuffer);
			String dataResultValue = dataValue.substring(6, 8) + dataValue.substring(4, 6) + dataValue.substring(0, 4);
			return dataResultValue;
		} else {
			return new String(dataBuffer);
		}
	}

	class fieldHeader {
		public String fieldName;
		public char dataType;
		public int displacement;
		public int length;
		public byte decimal;
	}

}
