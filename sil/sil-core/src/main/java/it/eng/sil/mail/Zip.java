package it.eng.sil.mail;

/*
 * Creato il 18-nov-04
 * Author: vuoto
 * 
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {

	List files = null;
	File zipFile = null;
	private static int BUFFER = 1024;

	public Zip() {
	}

	public File zip() throws IOException {

		if (files == null)
			return null;

		BufferedInputStream origin = null;
		if (zipFile == null) {
			zipFile = File.createTempFile("TMP", ".zip");
		}
		FileOutputStream dest = new FileOutputStream(zipFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

		// out.setMethod(ZipOutputStream.DEFLATED);
		byte data[] = new byte[BUFFER];

		for (int i = 0; i < files.size(); i++) {
			File file = (File) files.get(i);
			// System.out.println("Adding: " + file);
			FileInputStream fi = new FileInputStream(file);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(file.getName());
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
		}
		out.close();

		return zipFile;
	}

	/*
	 * public static void main(String argv[]) {
	 * 
	 * Zip zip = new Zip();
	 * 
	 * List fileDaZippare = new ArrayList(); fileDaZippare.add(new File("C:\\tmp\\menu.xml"));
	 * zip.setFiles(fileDaZippare); zip.setZipFile(new File("C:\\tmp\\zip.zip")); try { zip.zip(); } catch (IOException
	 * e) { e.printStackTrace(); } }
	 */

	/**
	 * @return
	 */
	public List getFiles() {
		return files;
	}

	/**
	 * @return
	 */
	public File getZipFile() {
		return zipFile;
	}

	/**
	 * @param list
	 */
	public void setFiles(List list) {
		files = list;
	}

	/**
	 * @param file
	 */
	public void setZipFile(File file) {
		zipFile = file;
	}

}