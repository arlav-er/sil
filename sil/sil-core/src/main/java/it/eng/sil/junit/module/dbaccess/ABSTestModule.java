/*
 * Created on Aug 25, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.junit.module.dbaccess;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

/**
 * @author savino
 * 
 *         test della query dinamiche con e senza transazioni. legge an_lavoratore, prende un lavoratore a caso,
 */
public class ABSTestModule extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		String testName = (String) request.getAttribute("test_name");
		try {
			if ("testBase".equals(testName))
				testBase(request, response);
			if ("test1".equals(testName))
				test1(request, response);
			if ("test2".equals(testName))
				test2(request, response);
			if ("test3".equals(testName))
				test3(request, response);
		} catch (Exception e) {
			response.setAttribute("ERROR", e);
		}
	}

	/**
	 * condizione di partenza: il lavoratore non ha record in pr_credito
	 * 
	 * Transazione: no Statement dinamici: select, insert, update Statement statici: select, delete
	 * 
	 * condizione finale: il lavoratore non ha record in pr_credito
	 */
	private void testBase(SourceBean request, SourceBean response) throws Exception {
		// leggiamo il lavoratore
		setSectionQuerySelect("GET_LAVORATORE");
		SourceBean row = doSelect(request, response, false);
		if (row == null)
			throw new Exception("impossibile leggere il lavoratore ");
		String cdnLavoratore = Utils.notNull(row.getAttribute("row.cdnLavoratore"));
		// leggiamo con la query dinamica i crediti
		setSectionQuerySelect("PR_CREDITO_DYN");
		row = doDynamicSelect(request, response, false);
		if (row == null)
			throw new Exception("impossibile leggere pr_credito ");
		// non sappiamo se ci siano a questo punto record, l'importante e' che
		// non si sia verificato errore
		// inseriamo con la query dinamica un record in pr_credito
		// prima prelevo il nextval
		setSectionQueryNextVal("PR_CREDITO_NEXTVAL");
		String prgCredito = Utils.notNull(doNextVal(request, response));
		request.setAttribute("prgCredito", prgCredito);
		setSectionQueryInsert("INS_PR_CREDITO_DYN");
		boolean ret = doDynamicInsert(request, response);
		// l'inserimento non deve fallire. Se succede il test e' fallito
		if (!ret)
			throw new Exception("impossibile inserire con query dinamica un record in pr_credito");
		// aggiorniamo con uno stm dinamico
		setSectionQueryUpdate("UPD_PR_CREDITO_DYN");
		ret = doDynamicUpdate(request, response);
		if (!ret)
			throw new Exception("impossibile aggiornare con query dinamica il record appena inserito in pr_credito");
		// ora proviamo a cancellare il record con una query semplice
		setSectionQueryDelete("DEL_PR_CREDITO");
		ret = doDelete(request, response);
		// la cancellazione non deve fallire. Se succede il test e' fallito
		if (!ret)
			throw new Exception("impossibile cancellare con query statica il record appena inserito in pr_credito");
		// se si arriva fin qui il test dovrebbe essere stato completato senza
		// failure
		// rileggiamo pr_credito
		setSectionQuerySelect("SEL_PR_CREDITO");
		row = doSelect(request, response);
		// non dovrebbero esserci errori, ma non si sa mai
		if (row == null)
			throw new Exception("impossibile leggere pr_credito alla fine del test");
		response.setAttribute("TEST_RESULT", row);
	}

	/**
	 * condizione di partenza: il lavoratore non ha record in pr_credito
	 * 
	 * Transazione: si Generazione errori durante la transazione: no Statement dinamici: select, insert, update
	 * Statement statici: select, delete
	 * 
	 * condizione finale: il lavoratore non ha record in pr_credito
	 */
	private void test1(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor txExec = new TransactionQueryExecutor(getPool());
		enableTransactions(txExec);
		try {
			txExec.initTransaction();
			// ///////////////////////////////////////
			// leggiamo il lavoratore
			setSectionQuerySelect("GET_LAVORATORE");
			SourceBean row = doSelect(request, response, false);
			if (row == null)
				throw new Exception("impossibile leggere il lavoratore ");
			String cdnLavoratore = Utils.notNull(row.getAttribute("row.cdnLavoratore"));
			// leggiamo con la query dinamica i crediti
			setSectionQuerySelect("PR_CREDITO_DYN");
			row = doDynamicSelect(request, response, false);
			if (row == null)
				throw new Exception("impossibile leggere pr_credito ");
			// non sappiamo se ci siano a questo punto record, l'importante e'
			// che non si sia verificato errore
			// inseriamo con la query dinamica un record in pr_credito
			// prima prelevo il nextval
			setSectionQueryNextVal("PR_CREDITO_NEXTVAL");
			String prgCredito = Utils.notNull(doNextVal(request, response));
			request.setAttribute("prgCredito", prgCredito);
			setSectionQueryInsert("INS_PR_CREDITO_DYN");
			boolean ret = doDynamicInsert(request, response);
			// l'inserimento non deve fallire. Se succede il test e' fallito
			if (!ret)
				throw new Exception("impossibile inserire con query dinamica un record in pr_credito");
			// aggiorniamo con uno stm dinamico
			setSectionQueryUpdate("UPD_PR_CREDITO_DYN");
			ret = doDynamicUpdate(request, response);
			if (!ret)
				throw new Exception(
						"impossibile aggiornare con query dinamica il record appena inserito in pr_credito");
			// ora proviamo a cancellare il record con una query semplice
			setSectionQueryDelete("DEL_PR_CREDITO");
			ret = doDelete(request, response);
			// la cancellazione non deve fallire. Se succede il test e' fallito
			if (!ret)
				throw new Exception("impossibile cancellare con query statica il record appena inserito in pr_credito");
			// ///////////////////////////////////////
			txExec.commitTransaction();
			// se si arriva fin qui il test dovrebbe essere stato completato
			// senza failure
			// riabilita la connessione semplice
			enableSimpleQuery();
			// rileggiamo pr_credito
			setSectionQuerySelect("SEL_PR_CREDITO");
			row = doSelect(request, response);
			// non dovrebbero esserci errori, ma non si sa mai
			if (row == null)
				throw new Exception("impossibile leggere pr_credito alla fine del test");
			response.setAttribute("TEST_RESULT", row);
		} catch (Exception e) {
			if (txExec != null)
				txExec.rollBackTransaction();
		}
	}

	/**
	 * condizione di partenza: il lavoratore non ha record in pr_credito
	 * 
	 * Transazione: si Generazione errori durante la transazione: no Statement dinamici: select, insert, update
	 * Statement statici: select, delete
	 * 
	 * condizione finale: il lavoratore non ha record in pr_credito
	 */
	private void test2(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor txExec = new TransactionQueryExecutor(getPool());
		try {

			// leggiamo il lavoratore
			setSectionQuerySelect("GET_LAVORATORE");
			SourceBean row = doSelect(request, response, false);
			if (row == null)
				throw new Exception("impossibile leggere il lavoratore ");
			String cdnLavoratore = Utils.notNull(row.getAttribute("row.cdnLavoratore"));
			// leggiamo con la query dinamica i crediti
			setSectionQuerySelect("PR_CREDITO_DYN");
			row = doDynamicSelect(request, response, false);
			if (row == null)
				throw new Exception("impossibile leggere pr_credito ");
			// non sappiamo se ci siano a questo punto record, l'importante e'
			// che non si sia verificato errore
			// inseriamo con la query dinamica un record in pr_credito
			// prima prelevo il nextval
			setSectionQueryNextVal("PR_CREDITO_NEXTVAL");
			String prgCredito = Utils.notNull(doNextVal(request, response));
			request.setAttribute("prgCredito", prgCredito);
			setSectionQueryInsert("INS_PR_CREDITO_DYN");
			boolean ret = doDynamicInsert(request, response);
			// l'inserimento non deve fallire. Se succede il test e' fallito
			if (!ret)
				throw new Exception("impossibile inserire con query dinamica un record in pr_credito");
			// ora iniziamo la transazione
			enableTransactions(txExec);
			txExec.initTransaction();
			// ///////////////////////////////////////
			// aggiorniamo con uno stm dinamico
			setSectionQueryUpdate("UPD_PR_CREDITO_DYN");
			ret = doDynamicUpdate(request, response);
			if (!ret)
				throw new Exception(
						"impossibile aggiornare con query dinamica il record appena inserito in pr_credito");
			// ora proviamo a cancellare il record con una query semplice
			setSectionQueryDelete("DEL_PR_CREDITO");
			ret = doDelete(request, response);
			// la cancellazione non deve fallire. Se succede il test e' fallito
			if (!ret)
				throw new Exception("impossibile cancellare con query statica il record appena inserito in pr_credito");
			// ///////////////////////////////////////
			txExec.commitTransaction();
			// se si arriva fin qui il test dovrebbe essere stato completato
			// senza failure
			// riabilita la connessione semplice
			enableSimpleQuery();
			// rileggiamo pr_credito
			setSectionQuerySelect("SEL_PR_CREDITO");
			row = doSelect(request, response);
			// non dovrebbero esserci errori, ma non si sa mai
			if (row == null)
				throw new Exception("impossibile leggere pr_credito alla fine del test");
			response.setAttribute("TEST_RESULT", row);
		} catch (Exception e) {
			if (txExec != null)
				txExec.rollBackTransaction();
		}
	}

	/**
	 * condizione di partenza: il lavoratore non ha record in pr_credito
	 * 
	 * Transazione: si Generazione errori durante la transazione: si Statement dinamici: select, insert Statement
	 * statici: select, update, delete
	 * 
	 * condizione finale: il lavoratore non ha record in pr_credito
	 */
	private void test3(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor txExec = new TransactionQueryExecutor(getPool());
		try {

			// leggiamo il lavoratore
			setSectionQuerySelect("GET_LAVORATORE");
			SourceBean row = doSelect(request, response, false);
			if (row == null)
				throw new Exception("impossibile leggere il lavoratore ");
			String cdnLavoratore = Utils.notNull(row.getAttribute("row.cdnLavoratore"));
			// leggiamo con la query dinamica i crediti
			setSectionQuerySelect("PR_CREDITO_DYN");
			row = doDynamicSelect(request, response, false);
			if (row == null)
				throw new Exception("impossibile leggere pr_credito ");
			// non sappiamo se ci siano a questo punto record, l'importante e'
			// che non si sia verificato errore
			// inseriamo con la query dinamica un record in pr_credito
			// prima prelevo il nextval
			setSectionQueryNextVal("PR_CREDITO_NEXTVAL");
			String prgCredito = Utils.notNull(doNextVal(request, response));
			request.setAttribute("prgCredito", prgCredito);
			setSectionQueryInsert("INS_PR_CREDITO_DYN");
			boolean ret = doDynamicInsert(request, response);
			// l'inserimento non deve fallire. Se succede il test e' fallito
			if (!ret)
				throw new Exception("impossibile inserire con query dinamica un record in pr_credito");
			// ora iniziamo la transazione
			enableTransactions(txExec);
			txExec.initTransaction();
			// ///////////////////////////////////////
			// aggiorniamo con uno stm
			setSectionQueryUpdate("UPD_PR_CREDITO");
			ret = doUpdate(request, response);
			if (!ret)
				throw new Exception("impossibile aggiornare con query statica il record appena inserito in pr_credito");
			// ora proviamo a cancellare il record con una query semplice
			setSectionQueryDelete("DEL_PR_CREDITO");
			ret = doDelete(request, response);
			// la cancellazione non deve fallire. Se succede il test e' fallito
			if (!ret)
				throw new Exception("impossibile cancellare con query statica il record appena inserito in pr_credito");
			// /////////////////////////////////////
			// a questo punto non committo ma eseguo la rollback
			txExec.rollBackTransaction();
			// se si arriva fin qui il test dovrebbe essere stato completato
			// senza failure
			// riabilita la connessione semplice
			enableSimpleQuery();
			// rileggiamo pr_credito
			setSectionQuerySelect("SEL_PR_CREDITO");
			row = doSelect(request, response);
			// non dovrebbero esserci errori, ma non si sa mai
			if (row == null)
				throw new Exception("impossibile leggere pr_credito alla fine del test");
			response.setAttribute("TEST_RESULT", row);
		} catch (Exception e) {
			if (txExec != null)
				txExec.rollBackTransaction();
		}
	}

}
