package it.eng.sil.util.batch;

/**
 * Batch per l'invio vacancy clicLavoro
 * 
 * NON VIENE PIU' UTILIZZATO LE RICHIESTE DI PERSONALE VENGONO SPEDITE VERSO MYPORTAL CON L'ALTRO SERVIZIO
 *
 */
public class BatchInvioVacancy implements BatchRunnable {

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	// private static Logger logger = Logger.getLogger(BatchInvioVacancy.class.getName());
	// private String[] parametri;
	//
	// private final String GET_VACANCY = "SELECT vac.prgvacancy, " +
	// " vac.prgrichiestaaz, " +
	// " vac.codcpi CODCPI, " +
	// " TO_CHAR(vac.datinvio, 'dd/MM/yyyy') DTINVIO " +
	// " FROM cl_vacancy vac " +
	// " WHERE vac.codstatoinviocl IN ('PA', 'VA')" +
	// " AND vac.codtipocomunicazionecl = '01'";
	//
	// private final String UPDATE_CL_VACANCY = " UPDATE cl_vacancy " +
	// " SET CODSTATOINVIOCL = ?, " +
	// " datinvio = sysdate, " +
	// " numklovacancy = numklovacancy + 1, " +
	// " cdnutmod = ?, " +
	// " dtmmod = sysdate " +
	// " WHERE prgvacancy = ? ";
	//
	// // valore di default - Donato docet
	// final String PRG_ALTERNATIVA_DEFAULT = "1";
	//
	// public static void main(String[] args) {
	// BatchInvioVacancy app = null;
	// try {
	// app = new BatchInvioVacancy();
	// app.setParametri(args);
	// app.start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e.getMessage());
	// } finally {
	// }
	// }
	//
	// public BatchInvioVacancy() {
	// }
	//
	//
	// @SuppressWarnings("unchecked")
	// public void start() {
	// logger.debug("=========== Avvio Batch ===========");
	// logger.info("INVIO VACANCY: Avvio Batch", null);
	// Context ctx;
	// QueryExecutorObject qExec = null;
	// DataConnection dc = null;
	// Connection conn = null;
	// try {
	// ctx = new InitialContext();
	// Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
	// if (objs instanceof DataSource) {
	// DataSource ds = (DataSource) objs;
	// conn = ds.getConnection();
	// dc = new DataConnection(conn, "2", new OracleSQLMapper());
	// dc.initTransaction();
	// qExec = BatchInvioVacancy.getQueryExecutorObject(dc);
	// }
	//
	// // RECUPERO VACANCY DA INVIARE
	// qExec.setStatement(this.GET_VACANCY);
	// qExec.setType(QueryExecutorObject.SELECT);
	// SourceBean vacancySB = (SourceBean) qExec.exec();
	// List<SourceBean> listaVacancy = vacancySB.getAttributeAsVector("ROW");
	//
	// int numTotVacancy = listaVacancy.size();
	// logger.info("INVIO VACANCY: totali da inviare: "+numTotVacancy, null);
	//
	// for (int i = 0; i < listaVacancy.size(); i++) {
	//
	// if (conn == null) {
	// dc.initTransaction();
	// }
	//
	// boolean skipInvio = false;
	// if (listaVacancy.get(i).getAttribute("PRGVACANCY") != null) {
	// String codStatoInvioUpdate = "";
	// BigDecimal prgVacancy = (BigDecimal) listaVacancy.get(i).getAttribute("PRGVACANCY");
	// BigDecimal prgRichiestaAz = (BigDecimal) listaVacancy.get(i).getAttribute("PRGRICHIESTAAZ");
	// String codCpi = (String) listaVacancy.get(i).getAttribute("CODCPI").toString();
	// String datInvio = (String) listaVacancy.get(i).getAttribute("DTINVIO").toString();
	// String codTipoInvio = (String) listaVacancy.get(i).getAttribute("CODSTATOINVIOCL");
	// if (("PA").equalsIgnoreCase(codTipoInvio)) {
	// codStatoInvioUpdate = "PI";
	// }
	// else {
	// codStatoInvioUpdate = "VI";
	// }
	//
	// String strDatiVacancy = " PRGVACANCY: "+prgVacancy.toString()+" PRGRICHIESTAAZ: "+prgRichiestaAz.toString()+"
	// CODCPI: "+codCpi+" DATINVIO: "+datInvio;
	//
	// int numVacIesima = i;
	// numVacIesima = numVacIesima+1;
	// logger.info("INVIO VACANCY: inizio numero "+numVacIesima+" di "+numTotVacancy , null);
	// logger.info("INVIO VACANCY: "+strDatiVacancy , null);
	//
	// // GENERO L'XML DELLA VACANCY DA INVIARE
	// String xmlGenerato = null;
	// try{
	// logger.debug("INVIO VACANCY: Creazione e validazione xml" , null);
	// xmlGenerato = buildRichiestaDiPersonale(prgRichiestaAz, PRG_ALTERNATIVA_DEFAULT, codCpi, datInvio, dc);
	// if (StringUtils.isFilledNoBlank(xmlGenerato)) {
	// xmlGenerato = xmlGenerato.replace("<cliclavoro", "\n<cliclavoro");
	// xmlGenerato = xmlGenerato.replace("></", ">\n</");
	// }
	// } catch (MandatoryFieldException e) {
	// logger.error("Errore MandatoryFieldException: "+e.getExceptionMessage());
	// Vector<String> param = new Vector<String>();
	// param.add(e.getMessageParameter());
	// logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_INPUT_ERRATO, param));
	// logger.error("ERRORE= "+strDatiVacancy);
	// skipInvio = true;
	// } catch (FieldFormatException e) {
	// logger.error("Errore FieldFormatException: "+e.getExceptionMessage());
	// Vector<String> param = new Vector<String>();
	// param.add(e.getMessageParameter());
	// logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_INPUT_ERRATO, param));
	// logger.error("ERRORE= "+strDatiVacancy);
	// skipInvio = true;
	// } catch (EMFUserError e) {
	// logger.error("Errore FieldFormatException: "+e);
	// Vector<String> param = new Vector<String>();
	// logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
	// logger.error("ERRORE= "+strDatiVacancy);
	// skipInvio = true;
	// }
	//
	// logger.debug("INVIO VACANCY: xml= "+xmlGenerato , null);
	//
	// if (!skipInvio) {
	//
	// logger.debug("INVIO VACANCY: Invio all'NCR" , null);
	//
	// CLSender sender = new CLSender();
	// try {
	// sender.popolaAddressByDs(Values.JDBC_JNDI_NAME);
	// } catch (Exception e) {
	// logger.error("popolaAddress", e);
	// Vector<String> param = new Vector<String>();
	// logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
	// logger.error("ERRORE= "+strDatiVacancy);
	// }
	//
	// // INVIO VACANCY
	// try {
	// // lwe
	// boolean wsResult = false; //sender.sendComunicazioneToNCR(xmlGenerato);
	//
	// logger.debug("INVIO VACANCY: Risposta NCR="+wsResult , null);
	//
	// // SE INVIO A BUON FINE, AGGIORNO VACANCY A INVIATO
	// if (wsResult) {
	// ArrayList<DataField> inputParameters = new ArrayList<DataField>();
	// inputParameters.add(dc.createDataField("CODSTATOINVIOCL", Types.VARCHAR, codStatoInvioUpdate));
	// inputParameters.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, parametri[0]));
	// inputParameters.add(dc.createDataField("PRGVACANCY", Types.NUMERIC, prgVacancy));
	// qExec.setStatement(this.UPDATE_CL_VACANCY);
	// qExec.setInputParameters(inputParameters);
	// qExec.setType(QueryExecutorObject.UPDATE);
	//
	// try {
	// boolean esitoUpdate = ((Boolean) qExec.exec());
	//
	// if (!esitoUpdate) {
	// dc.rollBackTransaction();
	// logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, null));
	// }
	// else {
	// dc.commitTransaction();
	// }
	// }
	// catch (EMFInternalError e) {
	// dc.rollBackTransaction();
	// logger.error("ERRORE NEL BATCH INVIO VACANCY: IMPOSSIBILE FARE COMMIT", e);
	// }
	// } else {
	// dc.rollBackTransaction();
	// logger.error("ERRORE NEL BATCH INVIO VACANCY: errore durante la comunicazione con l'NCR");
	// }
	//
	// } catch (RemoteException e) {
	// logger.error("INVIO VACANCY A NCR", e);
	// Vector<String> param = new Vector<String>();
	// logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
	// logger.error("ERRORE= "+strDatiVacancy);
	// } catch (ServiceException e) {
	// logger.error("INVIO VACANCY A NCR", e);
	// Vector<String> param = new Vector<String>();
	// logger.error(getMessageLog(MessageCodes.ClicLavoro.CODE_ERR_INTERNO, param));
	// logger.error("ERRORE= "+strDatiVacancy);
	// }
	// }
	//
	// logger.info("INVIO VANCACY: fine numero "+numVacIesima+" di "+numTotVacancy , null);
	// }
	// }
	//
	// } catch (Exception e1) {
	// logger.error("ERRORE NEL BATCH INVIO VACANCY", e1);
	// Utils.releaseResources(dc, null, null);
	// } finally {
	// Utils.releaseResources(dc, null, null);
	// }
	// logger.info("INVIO VACANCY: Fine Batch", null);
	// }
	//
	//
	// public void setParametri(String[] args) {
	// parametri = new String[1];
	// parametri[0] = args[0]; // user //Se avviati da .bat impostarlo di default
	//
	// logger.info("par 0=" + parametri[0]);
	// }
	//
	// /**
	// * Recupero del QueryExecutorObject: sono fuori dal contesto SIL,
	// * per cui mi tocca usare questo al posto del TransactionQueryExecutor
	// * @param dc DataConnection
	// * @return QueryExecutorObject
	// */
	// public static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {
	// logger.info("getQueryExecutorObject(DataConnection) - start");
	//
	// QueryExecutorObject qExec = new QueryExecutorObject();
	//
	// qExec.setRequestContainer(null);
	// qExec.setResponseContainer(null);
	// qExec.setDataConnection(dc);
	// qExec.setTransactional(true);
	// qExec.setDontForgetException(false);
	//
	// logger.info("getQueryExecutorObject(DataConnection) - end");
	// return qExec;
	// }
	//
	// public String getMessageLog(int messageCode, Vector<String> params){
	// EMFUserError errorItem;
	// if(params == null){
	// errorItem = new EMFUserError(EMFErrorSeverity.ERROR,messageCode);
	// }else{
	// errorItem = new EMFUserError(EMFErrorSeverity.ERROR,messageCode,params);
	// }
	// return errorItem.getDescription();
	// }
	//
	//
	// private String buildRichiestaDiPersonale(BigDecimal prgRichiesta,String prgAlternativa,
	// String codCPI, String codiceOfferta, DataConnection dc) throws EMFUserError, MandatoryFieldException,
	// FieldFormatException {
	// BigDecimal bdRichiesta;
	// BigDecimal bdAlternativa;
	// try {
	// bdRichiesta = prgRichiesta;
	// bdAlternativa = new BigDecimal(prgAlternativa);
	// } catch (NumberFormatException ex) {
	// throw new EMFUserError(EMFErrorSeverity.ERROR,MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,"I valori passati
	// prgRichiesta e prgAlternativa non sono numerici: prgRichiesta:"+prgRichiesta + ", prgAlternativa:" +
	// prgAlternativa);
	// }
	// CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(bdRichiesta,bdAlternativa,codCPI);
	// risposta.costruisci(dc);
	//
	// return risposta.generaXML();
	// }
}
