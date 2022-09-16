package it.eng.sil.util.batch;

import com.engiweb.framework.base.ApplicationContainer;

public class BatchLauncher {
	private ApplicationContainer applicationContainer;
	private BatchRunnable classInstance;

	public BatchLauncher(String args[]) {
		init(args);
	}

	public void start() {
		classInstance.start();
	}

	public void init(String args[]) {
		if (args.length == 0)
			throw new RuntimeException("numero di argomenti non valido");
		try {
			classInstance = (BatchRunnable) Class.forName(args[0]).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("nome della classe " + args[0] + " non valido");
		}
		applicationContainer = ApplicationContainer.getInstance();
	}

	public void release() {
		if (applicationContainer != null)
			applicationContainer.release();
	}

	public static void main(String[] args) {
		BatchLauncher batchGiornaliero = null;
		try {
			batchGiornaliero = new BatchLauncher(args);
			batchGiornaliero.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (batchGiornaliero != null)
				batchGiornaliero.release();
		}
	}
}