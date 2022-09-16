package it.eng.myportal.rest.app.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import it.eng.myportal.rest.app.pojo.UploadedFile;

public class MultiPartHelper {

	private Map<String, List<InputPart>> uploadForm;

	public MultiPartHelper(Map<String, List<InputPart>> uploadForm) {
		this.uploadForm = uploadForm;
	}

	public UploadedFile extractFileFromMultiPart(String key) throws IOException {

		UploadedFile f = new UploadedFile();
		List<InputPart> inputParts = this.uploadForm.get(key);

		if (inputParts != null) {
			for (InputPart inputPart : inputParts) {
				MultivaluedMap<String, String> headers = inputPart.getHeaders();
				InputStream inputStream = inputPart.getBody(InputStream.class, null);
				byte[] bytes = IOUtils.toByteArray(inputStream);
				String filename = getFileName(headers);

				f.setData(bytes);
				f.setFilename(filename);

			}
		}

		return f;
	}

	public String extractStringFromMultiPart(String key) throws IOException {

		List<InputPart> inputParts = this.uploadForm.get(key);

		if (inputParts != null) {
			for (InputPart inputPart : inputParts) {
				InputStream inputStream = inputPart.getBody(InputStream.class, null);
				byte[] bytes = IOUtils.toByteArray(inputStream);

				return new String(bytes);
			}
		}

		throw new IOException(String.format("Parte %s non trovata", key));

	}

	private String getFileName(MultivaluedMap<String, String> headers) {
		String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = sanitizeFilename(name[1]);
				return finalFileName;
			}
		}
		return "unknown";
	}

	private String sanitizeFilename(String s) {
		return s.trim().replaceAll("\"", "");
	}
}
