package it.eng.sil.module.pi3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import it.eng.afExt.utils.StringUtils;

public class ProtocolloPi3Utility {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static boolean isMainDocumentFullSignature(CreaDocumentPi3Bean creaDocumentPi3Bean) {

		boolean isMainDocumentFullSignature = false;

		String codTipoProtocollazione = creaDocumentPi3Bean.getDocumentPi3().getDocumentType();
		String codTipoTrattamento = creaDocumentPi3Bean.getTipoDelTrattamento();

		if (StringUtils.isEmpty(codTipoTrattamento) || StringUtils.isEmpty(codTipoTrattamento)) {
			return false;
			// TODO: procedura da invalidare?
		}

		if ((codTipoProtocollazione.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_NON_PROTOCOLLATO)
				&& codTipoTrattamento.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_REPERTORIO))
				|| (codTipoProtocollazione.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_ENTRATA)
						&& codTipoTrattamento.equalsIgnoreCase(Pi3Constants.PI3_DOCUMENT_TYPE_PROTOCOLLATO))) {
			if (creaDocumentPi3Bean.isDocumentoFirmato() && creaDocumentPi3Bean.isDocumentoFirmabile()
					&& creaDocumentPi3Bean.isConsensoAttivo()) {
				isMainDocumentFullSignature = true;
			}
		}

		return isMainDocumentFullSignature;
	}

	public static File serializePi3Bean(CreaDocumentPi3Bean creaDocumentPi3Bean) throws Exception {

		File fileTemp = File.createTempFile("temp", "tmp");

		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;

		fileOutputStream = new FileOutputStream(fileTemp);
		objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(creaDocumentPi3Bean);
		objectOutputStream.close();
		fileOutputStream.close();

		return fileTemp;
	}

	public static CreaDocumentPi3Bean deserializePi3Bean(File fileTemp) throws Exception {

		CreaDocumentPi3Bean creaDocumentPi3Bean = null;

		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;

		fileInputStream = new FileInputStream(fileTemp);
		objectInputStream = new ObjectInputStream(fileInputStream);
		creaDocumentPi3Bean = (CreaDocumentPi3Bean) objectInputStream.readObject();
		objectInputStream.close();
		fileInputStream.close();

		return creaDocumentPi3Bean;
	}

	public static File putBlobDbFieldIntoGenericFile(InputStream input) throws IOException {

		File blob = File.createTempFile("temp", "tmp");
		FileOutputStream output = new FileOutputStream(blob);

		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}

		if (count > Integer.MAX_VALUE) {
			count = -1;
		}

		return blob;
	}

	public static byte[] fromFileToByteArray(File file) throws IOException {

		InputStream input = new FileInputStream(file);
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}

		if (count > Integer.MAX_VALUE) {
			count = -1;
		}

		return output.toByteArray();
	}

}
