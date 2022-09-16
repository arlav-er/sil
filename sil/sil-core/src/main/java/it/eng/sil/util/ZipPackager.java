/*
 * Created on 25-set-07
 * @author vuoto
 *
 */
package it.eng.sil.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ZipPackager {

	public void zip(File outputFile, ZipEntryFile[] entryFiles) throws IOException {
		byte[] buf = new byte[1024];
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));

		// Compress the files
		for (int i = 0; i < entryFiles.length; i++) {
			FileInputStream in = new FileInputStream(entryFiles[i].getFileToZip());

			out.putNextEntry(entryFiles[i]);
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.closeEntry();
			in.close();
		}

		out.close();
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * ZipPackager zipPackager = new ZipPackager();
	 * 
	 * ZipEntryFile[] entryFiles = new ZipEntryFile[2]; int i = 0; entryFiles[i++] = new ZipEntryFile("ISCR.DBF",
	 * "C:\\tmp\\1.txt"); entryFiles[i++] = new ZipEntryFile("gfsdfsdfsd.txt", "C:\\tmp\\2.txt");
	 * 
	 * File outputfile; try { outputfile = File.createTempFile("~DBF", ".zip"); zipPackager.zip(outputfile, entryFiles);
	 * System.out.println(outputfile.getAbsolutePath()); } catch (IOException e) { // Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
}