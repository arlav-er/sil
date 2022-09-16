package it.eng.sil.util.xml;

import java.util.Arrays;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class SilNamespaceContext implements NamespaceContext {

	private String prefix;
	private String namespaceURI;

	public SilNamespaceContext(String prefix, String namespaceURI) {
		this.prefix = prefix;
		this.namespaceURI = namespaceURI;
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return this.namespaceURI;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return this.prefix;
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		String[] tmp = { this.prefix };

		return Arrays.stream(tmp).iterator();
	}

}
