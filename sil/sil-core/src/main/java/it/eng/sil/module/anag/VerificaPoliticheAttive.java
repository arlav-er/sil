package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;

public class VerificaPoliticheAttive {

	private BigDecimal cdnLavoratore;
	private String dataAdesioneMin;
	private String dataAdesioneSil;
	private String statoAdesione;
	private String descrizioneEsistenzaAdesione;
	private String descrizionePolAttiveCollAdesione;
	private String descrizionePolAttiveStatoAdesione;

	private static final String STATO_ATTIVA = "A";
	private static final String STATO_ANNULLATA = "D";
	private static final String STATO_CANCELLATA_NO_REQUISITI = "C";
	private static final String STATO_CHIUSURA_MANCATO_APP = "U";
	private static final String STATO_PRESA_IN_CARICO_ALTROVE = "N";
	private static final String STATO_RIFIUTO_PRESA_IN_CARICO = "X";
	private static final String STATO_RIFIUTO_POL_ATTIVA = "R";
	private static final String STATO_PRESO_IN_CARICO = "P";
	private static final String STATO_TRATTATO = "T";
	private static final String STATO_FINE_PARTECIPAZIONE = "F";
	private static final String ESITO_ANNULLATO_UFFICIO = "NA";
	private static final String ESITO_RIFIUTATO = "RIF";
	private static final String ESITO_INTERROTTO = "INT";
	private static final String ESITO_CONCLUSO = "FC";
	private static final String ESITO_AVVIATO = "AVV";
	private static final String ESITO_PROPOSTO = "PRO";

	private static final String A01_COLLOQUI = "A01";
	private static final String A02_PATTO_DI_ATTIVAZIONE = "A02";

	public VerificaPoliticheAttive() {
	}

	public VerificaPoliticheAttive(BigDecimal cdnLav, String dataAdesMin, String dataAdesSil, String statoAdes) {
		this.cdnLavoratore = cdnLav;
		this.dataAdesioneMin = dataAdesMin;
		this.dataAdesioneSil = dataAdesSil;
		this.statoAdesione = statoAdes;
	}

	public BigDecimal getCdnLavoratore() {
		return this.cdnLavoratore;
	}

	public String getDataAdesioneMin() {
		return this.dataAdesioneMin;
	}

	public String getDataAdesioneSil() {
		return this.dataAdesioneSil;
	}

	public String getStatoAdesione() {
		return this.statoAdesione;
	}

	public String getDescrizioneEsistenzaAdesione() {
		return this.descrizioneEsistenzaAdesione;
	}

	public String getDescrizionePolAttiveCollAdesione() {
		return this.descrizionePolAttiveCollAdesione;
	}

	public String getDescrizionePolAttiveStatoAdesione() {
		return this.descrizionePolAttiveStatoAdesione;
	}

	public void setCdnLavoratore(BigDecimal value) {
		this.cdnLavoratore = value;
	}

	public void setDataAdesioneMin(String value) {
		this.dataAdesioneMin = value;
	}

	public void setDataAdesioneSil(String value) {
		this.dataAdesioneSil = value;
	}

	public void setStatoAdesione(String value) {
		this.statoAdesione = value;
	}

	public void setDescrizioneEsistenzaAdesione(String value) {
		this.descrizioneEsistenzaAdesione = value;
	}

	public void setDescrizionePolAttiveCollAdesione(String value) {
		this.descrizionePolAttiveCollAdesione = value;
	}

	public void setDescrizionePolAttiveStatoAdesione(String value) {
		this.descrizionePolAttiveStatoAdesione = value;
	}

	public int verificaEsistenzaAdesione() throws Exception {
		if (getDataAdesioneMin() == null || getDataAdesioneMin().equals("")) {
			String desc = "Non è stato possibile ricavare l'adesione e lo stato adesione dal Ministero.";
			setDescrizioneEsistenzaAdesione(desc);
			return -1;
		} else {
			Object params[] = new Object[2];
			params[0] = getCdnLavoratore();
			params[1] = getDataAdesioneMin();
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("EXISTS_ADESIONE_GG_VERIFICA_POL_ATTIVE_CRUSCOTTO",
					params, "SELECT", Values.DB_SIL_DATI);
			if (res == null) {
				throw new Exception("Errore Verifica Politiche Attive");
			} else {
				Vector adesioni = res.getAttributeAsVector("ROW");
				if (adesioni.size() > 0) {
					return 0;
				} else {
					String desc = "Manca a sistema l'adesione del giovane in data " + getDataAdesioneMin() + ".";
					setDescrizioneEsistenzaAdesione(desc);
					return -1;
				}
			}
		}
	}

	public int politicheAttiveCollegateAdesione() throws Exception {
		Object params[] = new Object[1];
		params[0] = getCdnLavoratore();
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("EXISTS_POL_ATTIVE_NON_COLLEGATA_ADESIONE_CRUSCOTTO",
				params, "SELECT", Values.DB_SIL_DATI);
		if (res == null) {
			throw new Exception("Errore Verifica Politiche Attive");
		} else {
			Vector adesioni = res.getAttributeAsVector("ROW");
			if (adesioni.size() > 0) {
				String desc = "Alcune delle politiche attive in Garanzia Giovani del Giovane sono prive del riferimento all'adesione: è necessario tracciare l'adesione in ciascuna di esse. ";
				if (getDataAdesioneSil() != null && !getDataAdesioneSil().equals("")) {
					desc = desc + "In questo momento le verifiche sono effettuate come se fossero legate all'adesione "
							+ getDataAdesioneSil() + ".";
				} else {
					desc = desc + "Fino a quel momento saranno considerate nelle verifiche.";
				}
				setDescrizionePolAttiveCollAdesione(desc);
				return -1;
			} else {
				return 0;
			}
		}
	}

	public int coerenzaPoliticheAttiveStatoAdesione() throws Exception {
		Object params[] = null;
		SourceBean res = null;
		// Se presente la data adesione ministero
		if (getDataAdesioneMin() != null && !getDataAdesioneMin().equals("")) {
			params = new Object[3];
			params[0] = getCdnLavoratore();
			params[1] = getDataAdesioneMin();
			params[2] = getCdnLavoratore();
			res = (SourceBean) QueryExecutor.executeQuery("GET_INSIEME_AZIONI_ADESIONE_CRUSCOTTO", params, "SELECT",
					Values.DB_SIL_DATI);
		} else {
			if (getDataAdesioneSil() != null && !getDataAdesioneSil().equals("")) {
				params = new Object[3];
				params[0] = getCdnLavoratore();
				params[1] = getDataAdesioneSil();
				params[2] = getCdnLavoratore();
				res = (SourceBean) QueryExecutor.executeQuery("GET_INSIEME_AZIONI_ADESIONE_CRUSCOTTO", params, "SELECT",
						Values.DB_SIL_DATI);
			} else {
				params = new Object[1];
				params[0] = getCdnLavoratore();
				res = (SourceBean) QueryExecutor.executeQuery("GET_INSIEME_AZIONI_NON_COLLEGATE_ADESIONE_CRUSCOTTO",
						params, "SELECT", Values.DB_SIL_DATI);
			}
		}

		if (res == null) {
			throw new Exception("Errore Verifica Politiche Attive");
		} else {
			Vector azioniGG = res.getAttributeAsVector("ROW");
			String stato = getStatoAdesione();
			if (stato.equalsIgnoreCase(STATO_ANNULLATA) || stato.equalsIgnoreCase(STATO_CANCELLATA_NO_REQUISITI)
					|| stato.equalsIgnoreCase(STATO_CHIUSURA_MANCATO_APP) || stato.equalsIgnoreCase(STATO_ATTIVA)
					|| stato.equalsIgnoreCase(STATO_PRESA_IN_CARICO_ALTROVE)) {
				for (int i = 0; i < azioniGG.size(); i++) {
					// Nell'insieme delle azioni al di fuori della A01,
					// non devono esistere politiche attive in GG, se ci sono devono essere tutte con esito ANNULLATA
					// D'UFFICIO.
					SourceBean azione = (SourceBean) azioniGG.get(i);
					String codAzioneSifer = azione.containsAttribute("codazionesifer")
							? azione.getAttribute("codazionesifer").toString()
							: "";
					String codEsito = azione.containsAttribute("codesito") ? azione.getAttribute("codesito").toString()
							: "";
					if (!codAzioneSifer.equalsIgnoreCase(A01_COLLOQUI)) {
						if (!codEsito.equalsIgnoreCase(ESITO_ANNULLATO_UFFICIO)) {
							String statoDesc = "";
							if (stato.equalsIgnoreCase(STATO_ANNULLATA)) {
								statoDesc = "ANNULLATA";
							} else {
								if (stato.equalsIgnoreCase(STATO_CANCELLATA_NO_REQUISITI)) {
									statoDesc = "CANCELLATA PER MANCANZA DI REQUISITI";
								} else {
									if (stato.equalsIgnoreCase(STATO_CHIUSURA_MANCATO_APP)) {
										statoDesc = "CHIUSURA PER MANCATO RISPETTO APPUNTAMENTO";
									} else {
										if (stato.equalsIgnoreCase(STATO_ATTIVA)) {
											statoDesc = "ATTIVA";
										} else {
											statoDesc = "PRESA IN CARICO ALTRA REGIONE";
										}
									}
								}
							}
							if (stato.equalsIgnoreCase(STATO_CANCELLATA_NO_REQUISITI)) {
								String desc = "Lo stato adesione " + statoDesc
										+ " prevede, al di fuori della A01 (COLLOQUI DI ORIENTAMENTO), assenza di politiche attive in GG.<br>"
										+ "Possono esistere delle politiche attive solo nel caso la cancellazione avvenga dopo una presa in carico (P) oppure dopo un trattamento (T), "
										+ "in questo caso si consiglia di chiudere le politiche attive in GG del lavoratore.<br>"
										+ "Nota: Le politiche attive con esito ANNULLATA D'UFFICIO non vengono prese in considerazione nelle verifiche.";
								setDescrizionePolAttiveStatoAdesione(desc);
								return -1;
							} else {
								String desc = "Lo stato adesione " + statoDesc
										+ " prevede, al di fuori della A01 (COLLOQUI DI ORIENTAMENTO), assenza di politiche attive in GG.<br>"
										+ "Nota: Le politiche attive con esito ANNULLATA D'UFFICIO non vengono prese in considerazione nelle verifiche.";
								setDescrizionePolAttiveStatoAdesione(desc);
								return -1;
							}

						}
					}
				}
			} else {
				if (stato.equalsIgnoreCase(STATO_RIFIUTO_PRESA_IN_CARICO)) {
					// Deve esistere l'azione A02 - PATTO DI ATTIVAZIONE con esito RIF (Rifiutato)
					// le altre politiche in GG, al di fuori della A01, non devono esistere, se ci sono devono essere
					// tutte con esito ANNULLATA D'UFFICIO
					String desc = "Lo stato adesione RIFIUTO PRESA IN CARICO prevede la presenza della politica A02 (PATTO DI ATTIVAZIONE) con esito RIFUTATO.<br>"
							+ "Non devono essere presenti altre politiche attive in GG successive alla A02.<br>"
							+ "Nota: Le politiche attive con esito ANNULLATA D'UFFICIO non vengono prese in considerazione nelle verifiche.";
					boolean esisteA02 = false;
					for (int i = 0; i < azioniGG.size(); i++) {
						SourceBean azione = (SourceBean) azioniGG.get(i);
						String codAzioneSifer = azione.containsAttribute("codazionesifer")
								? azione.getAttribute("codazionesifer").toString()
								: "";
						String codEsito = azione.containsAttribute("codesito")
								? azione.getAttribute("codesito").toString()
								: "";
						if (codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)
								&& codEsito.equalsIgnoreCase(ESITO_RIFIUTATO)) {
							esisteA02 = true;
						} else {
							if (!codAzioneSifer.equalsIgnoreCase(A01_COLLOQUI)) {
								if (!codEsito.equalsIgnoreCase(ESITO_ANNULLATO_UFFICIO)) {
									setDescrizionePolAttiveStatoAdesione(desc);
									return -1;
								}
							}
						}
					}
					if (!esisteA02) {
						setDescrizionePolAttiveStatoAdesione(desc);
						return -1;
					}
				} else {
					if (stato.equalsIgnoreCase(STATO_RIFIUTO_POL_ATTIVA)) {
						// Deve essere presente A02 - PATTO DI ATTIVAZIONE con l'esito 'CONCLUSO'
						// deve esistere almeno una politiche attiva, diversa da A01 e A02, con esito (RIFIUTATO o
						// INTERROTTO).
						// Non ci devono esserci politiche con esito AVVIATO
						boolean esisteA02 = false;
						boolean esistePoliticaRifInterrotto = false;
						boolean esistePoliticaAvviato = false;

						int checkCoerenzaPattoServizio = coerenzaPattoServizio();

						for (int i = 0; i < azioniGG.size(); i++) {
							SourceBean azione = (SourceBean) azioniGG.get(i);
							String codAzioneSifer = azione.containsAttribute("codazionesifer")
									? azione.getAttribute("codazionesifer").toString()
									: "";
							String codEsito = azione.containsAttribute("codesito")
									? azione.getAttribute("codesito").toString()
									: "";
							if (codEsito.equalsIgnoreCase(ESITO_AVVIATO)) {
								esistePoliticaAvviato = true;
							} else {
								if (codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)
										&& codEsito.equalsIgnoreCase(ESITO_CONCLUSO)) {
									esisteA02 = true;
								} else {
									if (!codAzioneSifer.equalsIgnoreCase(A01_COLLOQUI)
											&& !codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)) {
										if (codEsito.equalsIgnoreCase(ESITO_RIFIUTATO)
												|| codEsito.equalsIgnoreCase(ESITO_INTERROTTO)) {
											esistePoliticaRifInterrotto = true;
										}
									}
								}
							}
						}
						if (esistePoliticaAvviato || !esisteA02 || !esistePoliticaRifInterrotto) {
							String desc = "Lo stato adesione RIFIUTO O RITIRO DA POLITICA ATTIVA prevede la presenza della politica A02 (PATTO DI ATTIVAZIONE) con esito CONCLUSO.<br>"
									+ "Deve esserci almeno un'altra politica attiva successiva all'A02, con esito RIFIUTATO o INTERROTTO.<br>"
									+ "Non devono essere presenti ulteriori politiche attive in GG con esito AVVIATO successive alla A02.";
							if (checkCoerenzaPattoServizio == -1) {
								desc = desc
										+ "<br>Lo stato RIFIUTO O RITIRO DA POLITICA ATTIVA e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
							}
							if (checkCoerenzaPattoServizio == -2) {
								desc = desc
										+ "<br>Lo stato RIFIUTO O RITIRO DA POLITICA ATTIVA prevede la presenza Indice GG/Dlgs 150 per il profiling.";
							}
							desc = desc
									+ "<br>Nota: Le politiche attive con esito ANNULLATA D'UFFICIO non vengono prese in considerazione nelle verifiche.";
							setDescrizionePolAttiveStatoAdesione(desc);
							return -1;
						} else {
							if (checkCoerenzaPattoServizio == -1) {
								String desc = "Lo stato RIFIUTO O RITIRO DA POLITICA ATTIVA e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
								setDescrizionePolAttiveStatoAdesione(desc);
								return -1;
							}

							if (checkCoerenzaPattoServizio == -2) {
								String desc = "Lo stato RIFIUTO O RITIRO DA POLITICA ATTIVA prevede la presenza Indice GG/Dlgs 150 per il profiling.";
								setDescrizionePolAttiveStatoAdesione(desc);
								return -1;
							}
						}
					} else {
						if (stato.equalsIgnoreCase(STATO_PRESO_IN_CARICO)) {
							// Deve esistere l'azione A02 - PATTO DI ATTIVAZIONE con esito 'CONCLUSO'
							// al di fuori della A01 e dalla A02, le altre politiche attive possono essere solo PROPOSTE
							// o ANNULLATE D'UFFICIO
							String desc = "Lo stato adesione PRESO IN CARICO prevede la presenza della politica A02 (PATTO DI ATTIVAZIONE) con esito CONCLUSO.<br>"
									+ "Le politiche attive in GG successive alla A02 possono solo essere PROPOSTE.";
							int checkCoerenzaPattoServizio = coerenzaPattoServizio();
							if (checkCoerenzaPattoServizio == -1) {
								desc = desc
										+ "<br>Lo stato PRESO IN CARICO e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
							}

							if (checkCoerenzaPattoServizio == -2) {
								desc = desc
										+ "<br>Lo stato PRESO IN CARICO prevede la presenza Indice GG/Dlgs 150 per il profiling.";
							}

							desc = desc
									+ "<br>Nota: Le politiche attive con esito ANNULLATA D'UFFICIO non vengono prese in considerazione nelle verifiche.";

							boolean esisteA02 = false;
							for (int i = 0; i < azioniGG.size(); i++) {
								SourceBean azione = (SourceBean) azioniGG.get(i);
								String codAzioneSifer = azione.containsAttribute("codazionesifer")
										? azione.getAttribute("codazionesifer").toString()
										: "";
								String codEsito = azione.containsAttribute("codesito")
										? azione.getAttribute("codesito").toString()
										: "";
								if (codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)
										&& codEsito.equalsIgnoreCase(ESITO_CONCLUSO)) {
									esisteA02 = true;
								} else {
									if (!codAzioneSifer.equalsIgnoreCase(A01_COLLOQUI)
											&& !codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)) {
										if (!codEsito.equalsIgnoreCase(ESITO_PROPOSTO)
												&& !codEsito.equalsIgnoreCase(ESITO_ANNULLATO_UFFICIO)) {
											setDescrizionePolAttiveStatoAdesione(desc);
											return -1;
										}
									}
								}
							}
							if (!esisteA02) {
								setDescrizionePolAttiveStatoAdesione(desc);
								return -1;
							} else {
								if (checkCoerenzaPattoServizio == -1) {
									String descProfiling = "Lo stato PRESO IN CARICO e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
									setDescrizionePolAttiveStatoAdesione(descProfiling);
									return -1;
								}

								if (checkCoerenzaPattoServizio == -2) {
									String descProfiling = "Lo stato PRESO IN CARICO prevede la presenza Indice GG/Dlgs 150 per il profiling.";
									setDescrizionePolAttiveStatoAdesione(descProfiling);
									return -1;
								}
							}
						} else {
							if (stato.equalsIgnoreCase(STATO_TRATTATO)) {
								// Deve essere presente A02 - PATTO DI ATTIVAZIONE con l'esito 'CONCLUSO'
								// deve esistere almeno una politiche attive, diversa da A01 e A02, con esito (AVVIATO o
								// CONCLUSO).
								// Non ci devono esserci politiche con esito RIFIUTATO o INTERROTTO.
								boolean esisteA02 = false;
								boolean esistePoliticaAvvConcluso = false;
								boolean esistePoliticaRifInterrotto = false;

								int checkCoerenzaPattoServizio = coerenzaPattoServizio();

								for (int i = 0; i < azioniGG.size(); i++) {
									SourceBean azione = (SourceBean) azioniGG.get(i);
									String codAzioneSifer = azione.containsAttribute("codazionesifer")
											? azione.getAttribute("codazionesifer").toString()
											: "";
									String codEsito = azione.containsAttribute("codesito")
											? azione.getAttribute("codesito").toString()
											: "";
									if (codEsito.equalsIgnoreCase(ESITO_RIFIUTATO)
											|| codEsito.equalsIgnoreCase(ESITO_INTERROTTO)) {
										esistePoliticaRifInterrotto = true;
									} else {
										if (codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)
												&& codEsito.equalsIgnoreCase(ESITO_CONCLUSO)) {
											esisteA02 = true;
										} else {
											if (!codAzioneSifer.equalsIgnoreCase(A01_COLLOQUI)
													&& !codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)) {
												if (codEsito.equalsIgnoreCase(ESITO_AVVIATO)
														|| codEsito.equalsIgnoreCase(ESITO_CONCLUSO)) {
													esistePoliticaAvvConcluso = true;
												}
											}
										}
									}
								}
								if (esistePoliticaRifInterrotto || !esisteA02 || !esistePoliticaAvvConcluso) {
									String desc = "Lo stato adesione TRATTATO prevede la presenza della politica A02 (PATTO DI ATTIVAZIONE) con esito CONCLUSO e "
											+ "di almeno un'altra politica attiva, successiva alla A02, con esito AVVIATO o CONCLUSO.<br>"
											+ "Non devono essere presenti ulteriori politiche attive in GG con esito RIFIUTATO o INTERROTTO successive alla A02.";
									if (checkCoerenzaPattoServizio == -1) {
										desc = desc
												+ "<br>Lo stato TRATTATO e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
									}
									if (checkCoerenzaPattoServizio == -2) {
										desc = desc
												+ "<br>Lo stato TRATTATO prevede la presenza Indice GG/Dlgs 150 per il profiling.";
									}

									desc = desc
											+ "<br>Nota: Le politiche attive con esito ANNULLATA D'UFFICIO non vengono prese in considerazione nelle verifiche.";
									setDescrizionePolAttiveStatoAdesione(desc);
									return -1;
								} else {
									if (checkCoerenzaPattoServizio == -1) {
										String desc = "Lo stato TRATTATO e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
										setDescrizionePolAttiveStatoAdesione(desc);
										return -1;
									}

									if (checkCoerenzaPattoServizio == -2) {
										String desc = "Lo stato TRATTATO prevede la presenza Indice GG/Dlgs 150 per il profiling.";
										setDescrizionePolAttiveStatoAdesione(desc);
										return -1;
									}
								}
							} else {
								if (stato.equalsIgnoreCase(STATO_FINE_PARTECIPAZIONE)) {
									// Deve essere presente l'azione A02 - PATTO DI ATTIVAZIONE con l'esito 'CONCLUSO'
									// deve esistere almeno una politiche attive, diversa da A01 e A02, con esito
									// CONCLUSO.
									// Non ci devono esserci politche con stato RIFIUTATO o INTERROTTO o PROPOSTO
									boolean esisteA02 = false;
									boolean esistePoliticaConcluso = false;
									boolean esistePoliticaRifInterrottoProposto = false;
									int checkCoerenzaPattoServizio = coerenzaPattoServizio();

									for (int i = 0; i < azioniGG.size(); i++) {
										SourceBean azione = (SourceBean) azioniGG.get(i);
										String codAzioneSifer = azione.containsAttribute("codazionesifer")
												? azione.getAttribute("codazionesifer").toString()
												: "";
										String codEsito = azione.containsAttribute("codesito")
												? azione.getAttribute("codesito").toString()
												: "";
										if (codEsito.equalsIgnoreCase(ESITO_RIFIUTATO)
												|| codEsito.equalsIgnoreCase(ESITO_INTERROTTO)
												|| codEsito.equalsIgnoreCase(ESITO_PROPOSTO)) {
											esistePoliticaRifInterrottoProposto = true;
										} else {
											if (codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)
													&& codEsito.equalsIgnoreCase(ESITO_CONCLUSO)) {
												esisteA02 = true;
											} else {
												if (!codAzioneSifer.equalsIgnoreCase(A01_COLLOQUI)
														&& !codAzioneSifer.equalsIgnoreCase(A02_PATTO_DI_ATTIVAZIONE)) {
													if (codEsito.equalsIgnoreCase(ESITO_CONCLUSO)) {
														esistePoliticaConcluso = true;
													}
												}
											}
										}
									}
									if (esistePoliticaRifInterrottoProposto || !esisteA02 || !esistePoliticaConcluso) {
										String desc = "Lo stato adesione FINE PARTECIPAZIONE prevede la presenza della politica A02 (PATTO DI ATTIVAZIONE) con esito CONCLUSO e, di "
												+ "almeno un'altra politica attiva, successiva alla A02, con esito CONCLUSO.<br>"
												+ "Non devono essere presenti ulteriori politiche attive in GG con esito RIFIUTATO o INTERROTTO o PROPOSTO.";
										if (checkCoerenzaPattoServizio == -1) {
											desc = desc
													+ "<br>Lo stato FINE PARTECIPAZIONE e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
										}
										if (checkCoerenzaPattoServizio == -2) {
											desc = desc
													+ "<br>Lo stato FINE PARTECIPAZIONE prevede la presenza Indice GG/Dlgs 150 per il profiling.";
										}

										desc = desc
												+ "<br>Nota: Le politiche attive con esito ANNULLATA D'UFFICIO non vengono prese in considerazione nelle verifiche.";
										setDescrizionePolAttiveStatoAdesione(desc);
										return -1;
									} else {
										if (checkCoerenzaPattoServizio == -1) {
											String desc = "Lo stato FINE PARTECIPAZIONE e la data del patto corrente prevedono la presenza di entrambi gli indici di svantaggio per il profiling.";
											setDescrizionePolAttiveStatoAdesione(desc);
											return -1;
										}

										if (checkCoerenzaPattoServizio == -2) {
											String desc = "Lo stato FINE PARTECIPAZIONE prevede la presenza Indice GG/Dlgs 150 per il profiling.";
											setDescrizionePolAttiveStatoAdesione(desc);
											return -1;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return 0;
	}

	public int coerenzaPattoServizio() throws Exception {
		Object params[] = new Object[1];
		params[0] = getCdnLavoratore();
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("EXISTS_PROFILING_PATTO_SERVIZIO_ADESIONE_CRUSCOTTO",
				params, "SELECT", Values.DB_SIL_DATI);
		if (res == null) {
			throw new Exception("Errore Verifica Politiche Attive");
		} else {
			Vector patti = res.getAttributeAsVector("ROW");
			if (patti.size() > 0) {
				for (int i = 0; i < patti.size(); i++) {
					SourceBean patto = (SourceBean) patti.get(i);
					String datStipula = (String) patto.getAttribute("DATSTIPULA");
					Object numIndice = patto.getAttribute("NUMINDICESVANTAGGIO");
					Object numIndice2 = patto.getAttribute("NUMINDICESVANTAGGIO2");

					if (DateUtils.compare(datStipula, MessageCodes.General.DATA_PROFILING_2015) < 0) {
						if (numIndice == null || numIndice2 == null) {
							return -1;
						}
					} else {
						if (numIndice2 == null) {
							return -2;
						}
					}
				}
			}

			return 0;
		}
	}

}