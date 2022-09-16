package com.engiweb.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Serializer {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Serializer.class.getName());

	private Serializer() {
		super();
	} // private Serializer()

	public static byte[] serialize(Object obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			byteArrayOutputStream.toByteArray();
		} // try
		catch (Exception ex) {
			_logger.debug("Serializer::serialize: errore serializzazione oggetto [" + obj + "]");

			_logger.debug("Serializer::serialize: " + ex.getMessage());

		} // catch (Exception ex)
		return byteArrayOutputStream.toByteArray();
	} // public static byte[] serialize(Object obj)
} // public class Serializer
