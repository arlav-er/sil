/*
 * Created on 25-set-07
 *
 */
package it.eng.sil.util;

import java.util.zip.ZipEntry;

/**
 * @author vuoto
 */

public class ZipEntryFile extends ZipEntry {

	public String fileToZip = null;

	public ZipEntryFile(String entryName) {
		super(entryName);
	}

	public ZipEntryFile(String entryName, String fileToZip) {
		super(entryName);

		this.fileToZip = fileToZip;
	}

	public String getFileToZip() {
		return fileToZip;
	}

	public void setFileToZip(String string) {
		fileToZip = string;
	}

}
