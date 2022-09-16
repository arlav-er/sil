package it.eng.sil.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

public class AssemblaXml {
	final String defaultDirConf = "C:\\Progetti\\SIL\\sviluppo\\applicazioni\\SilWeb\\web\\WEB-INF\\conf\\";
	final String defaultOutputDir = "c:\\tmp";
	private String dirConf = defaultDirConf;
	private String outputDir = defaultOutputDir;

	public AssemblaXml() {
	}

	public static void main(String[] args) {
		AssemblaXml assemblaXml = new AssemblaXml();

		if (args.length > 0) {
			assemblaXml.setDirConf(args[0]);
		}
		if (args.length > 1) {
			assemblaXml.setOutputDir(args[1]);
		}

		assemblaXml.Scrivi("PAGES");
		assemblaXml.Scrivi("MODULES");
		assemblaXml.Scrivi("STATEMENTS");
		assemblaXml.Scrivi("PRESENTATION");
		assemblaXml.Scrivi("PUBLISHERS");
	}

	public void Scrivi(String rootTag) {
		String directory = dirConf + File.separator + rootTag;

		String nomeFileOut = rootTag.toLowerCase();
		byte[] buffer = new byte[1024];

		try {
			FileOutputStream out = new FileOutputStream(outputDir + File.separator + nomeFileOut + ".xml");

			StringBuffer testata = new StringBuffer();
			testata.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			testata.append("<!-- Generato il      :" + new Date() + "-->\r\n");
			testata.append("<!-- Directory di conf: " + dirConf + "-->\r\n");
			testata.append("<!-- Output in        : " + outputDir + "-->\r\n");

			testata.append("<" + rootTag + ">\r\n");

			out.write(testata.toString().getBytes());

			File dir = new File(directory);
			File[] fileXML = null;

			if (dir.isDirectory()) {
				fileXML = dir.listFiles();

				for (int i = 0; i < fileXML.length; i++) {
					File f = fileXML[i];

					if (f.isFile()) {
						String nomeFile = f.getName();
						boolean isXmlFile = nomeFile.toLowerCase().endsWith(".xml");

						if (isXmlFile) {
							BufferedInputStream bir = new BufferedInputStream(new FileInputStream(f.getAbsolutePath()));

							// inizia la scrittura su out...
							int len = bir.read(buffer, 0, 1024);

							while (len != -1) {
								out.write(buffer, 0, len);
								len = bir.read(buffer, 0, 1024);
							}

							bir.close();
						}
					}
				}
			}

			out.write(new String("</" + rootTag + ">").getBytes());
			out.close();
			System.out.println(nomeFileOut + ".xml generato in " + outputDir);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public String getDirConf() {
		return dirConf;
	}

	/**
	 * @return
	 */
	public String getOutputDir() {
		return outputDir;
	}

	/**
	 * @param string
	 */
	public void setDirConf(String string) {
		dirConf = string;
	}

	/**
	 * @param string
	 */
	public void setOutputDir(String string) {
		outputDir = string;
	}

}
