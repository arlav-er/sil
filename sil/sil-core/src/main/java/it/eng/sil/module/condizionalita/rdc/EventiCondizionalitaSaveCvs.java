package it.eng.sil.module.condizionalita.rdc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;

import it.eng.sil.module.AbstractSimpleModule;

public class EventiCondizionalitaSaveCvs extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 318555354987275105L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(EventiCondizionalitaSaveCvs.class.getName());

	@SuppressWarnings("rawtypes")
	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		SourceBean listaEventi = doDynamicSelect(serviceRequest, serviceResponse);

		Vector vectorEventi = new Vector();
		try {
			vectorEventi = listaEventi.getAttributeAsVector("ROW");
		} catch (Exception e) {
			// nothing to do
		}

		File outputfile = File.createTempFile("tmpeventicond", "csv");

		try {

			if (outputfile.exists()) {
				outputfile.delete();
			}

			outputfile.createNewFile();

			FileWriter fw = new FileWriter(outputfile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			boolean isFirstRow = true;
			for (int i = 0; i < vectorEventi.size(); i++) {

				SourceBean row = (SourceBean) vectorEventi.get(i);

				Vector attributes = row.getContainedAttributes();

				if (isFirstRow) {
					for (int j = 0; j < attributes.size(); j++) {
						String columnName = ((SourceBeanAttribute) attributes.get(j)).getKey();
						if (!columnName.toLowerCase().startsWith("ordine_")) {
							columnName = columnName.replace("__", ".");
							columnName = columnName.replace("_", " ");
							bw.write(columnName);
							bw.write(";");
						}
					}
					isFirstRow = false;
				}

				bw.write("\n");

				for (int j = 0; j < attributes.size(); j++) {
					String columnValue = ((SourceBeanAttribute) attributes.get(j)).getValue().toString().trim();
					String columnName = ((SourceBeanAttribute) attributes.get(j)).getKey();
					if (!columnName.toLowerCase().startsWith("ordine_")) {
						bw.write(columnValue);
						bw.write(";");
					}
				}

			}
			bw.close();

		} catch (IOException e) {
			_logger.debug(e.getMessage());
		}

		serviceResponse.setAttribute("fileExportEventiCsv", outputfile.getAbsolutePath());

	}

}
