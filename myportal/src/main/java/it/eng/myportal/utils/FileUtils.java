package it.eng.myportal.utils;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileUtils {
	/**
	 * 
	 * Ridimensiona l'immagine passatagli. L'immagine viene ridimensionata
	 * rispettando il rapporto tra altezza e larghezza.
	 * 
	 * @param fileData
	 *            l'immagine da ridimensionare
	 * @param maxEdgeSize
	 *            la nuova dimensione del lato piu' lungo dell'immagine
	 * @param scalingAlgorithm
	 *            la qualita' dell'algoritmo da usare, regola il rapporto
	 *            qualita'/velocita'
	 * @return
	 * @throws IOException
	 */
	public static byte[] scale(byte[] fileData, int maxEdgeSize,
			int scalingAlgorithm) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(fileData);
		BufferedImage img;

		img = ImageIO.read(in);
		if (img == null) {
			throw new IOException();
		}
		if (maxEdgeSize >= img.getHeight() && maxEdgeSize >= img.getWidth()) {
			/*
			 * l'immagine e' piu' piccola della dimensione del resize, la lascio
			 * stare
			 */
			return fileData;
		}

		/*
		 * ridimensiono il lato piu' lungo dell'immagine a maxEdgeSize e l'altro
		 * lato mantenendo il rapporto
		 */
		float height = img.getHeight();
		float width = img.getWidth();
		float max = Math.max(width, height);
		if (max == width) {
			width = maxEdgeSize;
			height = img.getHeight() * ((float) width / (float) img.getWidth());
		} else {
			height = maxEdgeSize;
			width = img.getWidth() * ((float) height / (float) img.getHeight());
		}

		Image scaledImage = img.getScaledInstance((int) width, (int) height,
				scalingAlgorithm);
		BufferedImage imageBuff = new BufferedImage((int) width, (int) height,
				BufferedImage.TYPE_INT_RGB);
		imageBuff.getGraphics().drawImage(scaledImage, 0, 0,
				new Color(0, 0, 0), null);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		ImageIO.write(imageBuff, "jpg", buffer);

		return buffer.toByteArray();
	}
}
