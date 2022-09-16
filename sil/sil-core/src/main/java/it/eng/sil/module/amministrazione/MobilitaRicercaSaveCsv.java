package it.eng.sil.module.amministrazione;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;

import it.eng.sil.module.AbstractSimpleModule;

public class MobilitaRicercaSaveCsv extends AbstractSimpleModule {

	private static final long serialVersionUID = 1942356153039421730L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MobilitaRicercaSaveCsv.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		SourceBean listaMobilita = doDynamicSelect(request, response);

		Vector vecMobilita = new Vector();
		try {
			vecMobilita = listaMobilita.getAttributeAsVector("ROW");
		} catch (Exception e) {
			// nothing to do
		}

		File outputfile = File.createTempFile("tmpiscrizioni", "csv");

		try {

			if (outputfile.exists()) {
				outputfile.delete();
			}

			outputfile.createNewFile();

			FileWriter fw = new FileWriter(outputfile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			boolean isFirstRow = true;
			for (int i = 0; i < vecMobilita.size(); i++) {

				SourceBean row = (SourceBean) vecMobilita.get(i);

				Vector attributes = row.getContainedAttributes();

				if (isFirstRow) {
					for (int j = 0; j < attributes.size(); j++) {
						String columnName = ((SourceBeanAttribute) attributes.get(j)).getKey();
						columnName = columnName.replace("__", ".");
						columnName = columnName.replace("_", " ");
						bw.write(columnName);
						bw.write("|");
					}
					isFirstRow = false;
				}

				bw.write("\n");

				for (int j = 0; j < attributes.size(); j++) {
					String columnValue = ((SourceBeanAttribute) attributes.get(j)).getValue().toString().trim();
					bw.write(columnValue);
					bw.write("|");
				}

			}
			bw.close();

		} catch (IOException e) {
			_logger.debug(e.getMessage());
		}

		response.setAttribute("fileExportIscrMobCsv", outputfile.getAbsolutePath());

	}

}
