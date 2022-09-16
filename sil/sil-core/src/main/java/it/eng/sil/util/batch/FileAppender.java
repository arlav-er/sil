package it.eng.sil.util.batch;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileAppender {
	private File out = null;
	private String workPath = null;
	private FileOutputStream fos = null;
	private String fileName = null;
	private DataOutputStream dout = null;
	private String ext = null;

	public FileAppender(String fileName, String workPath, String ext) throws FileNotFoundException, IOException {
		File dir = new File(workPath);
		dir.mkdirs();

		this.ext = ext;
		this.fileName = fileName;
		this.workPath = workPath;
		out = new File(this.workPath + File.separator + this.fileName);
		fos = new FileOutputStream(out);
		this.dout = new DataOutputStream(this.fos);
	}

	public void changeWorkFile(String newFileName, String newWorkPath) throws IOException {
		File dir = new File(newWorkPath);
		dir.mkdirs();

		this.fileName = newFileName;
		this.workPath = newWorkPath;

		this.fos.close();
		this.out = new File(this.workPath + File.separator + this.fileName);
		this.fos = new FileOutputStream(out);
		this.dout = new DataOutputStream(this.fos);
	}

	public void doneAppendCurrentFile(String finalFileName, String finalWorkPath) throws IOException {
		this.fos.close();
		File newFile = new File(finalWorkPath + File.separator + finalFileName + "." + ext);
		this.out.renameTo(newFile);
		this.out = null;
		dout.close();
	}

	public void appendTextToCurrentFile(String text) throws IOException {
		String message = text + "\r\n";
		this.dout.writeBytes(message);
	}
}
