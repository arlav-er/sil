/*
 * Created on Mar 7, 2007
 */
package it.eng.sil.bean.protocollo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author savino
 */
public class ByteArrayDataSource implements javax.activation.DataSource {
	byte data[];
	String name;
	String type;

	public ByteArrayDataSource(byte[] data, String type, String name) {
		this.data = data;
		this.type = type;
		this.name = name;
	}

	public String getContentType() {
		return type;
	}

	public InputStream getInputStream() throws IOException {
		InputStream is = new ByteArrayInputStream(data);
		return is;
	}

	public String getName() {

		return name;
	}

	public OutputStream getOutputStream() throws IOException {
		return null;
	}
}
