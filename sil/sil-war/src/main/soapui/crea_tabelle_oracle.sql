-- servizi SAP --------------------------------------------------------------------

CREATE TABLE ws_stub.sap
(
  identificativosap VARCHAR2(20),
  risposta CLOB,
  CONSTRAINT sap_pkey PRIMARY KEY (identificativosap) USING INDEX
);
/
CREATE TABLE ws_stub.did
(
  codicefiscale VARCHAR2(16),
  risposta CLOB,
  CONSTRAINT did_pkey PRIMARY KEY (codicefiscale) USING INDEX
);
/
-- insert di riferimento
insert into ws_stub.sap (identificativosap, risposta) values (
'AA01711608H',
to_clob('<![CDATA[<?xml version="1.0" encoding="utf-8"?>
        <lavoratore>
  <datiinvio>
    <dataultimoagg>2018-12-14</dataultimoagg>
	<identificativosap>AA28868621H</identificativosap>
    <codiceentetit>H501C000523</codiceentetit>
    <tipovariazione>02</tipovariazione>
    <datadinascita>2003-01-01</datadinascita>
  </datiinvio>
  <datianagrafici>
    <datipersonali>
      <codicefiscale>DLLSMN03A01H501E</codicefiscale>
      <cognome>DELL''ORTO</cognome>
      <nome>SIMONE</nome>
      <sesso>M</sesso>
      <datanascita>2003-01-01</datanascita>
      <codcomune>H501</codcomune>
      <codcittadinanza>000</codcittadinanza>
    </datipersonali>
    <residenza>
      <codcomune>H501</codcomune>
      <cap>41100</cap>
      <indirizzo>via Marconi, 46</indirizzo>
    </residenza>
    <domicilio>
      <codcomune>H501</codcomune>
      <cap>41100</cap>
      <indirizzo>via Marconi, 46</indirizzo>
    </domicilio>
    <recapiti>
      <telefono>0000000000</telefono>
    </recapiti>
  </datianagrafici>
  <datiamministrativi>
    <statoinanagrafe>
      <codstatooccupazionale>IN</codstatooccupazionale>
      <anzianita>7</anzianita>
      <disponibilita>2018-05-07</disponibilita>
    </statoinanagrafe>
    <periodidisoccupazione>
      <dataingresso>2018-05-07</dataingresso>
      <tipoingresso>D</tipoingresso>
    </periodidisoccupazione>
  </datiamministrativi>
  <allegato/>
  <politiche_attive_lst>
		<politiche_attive>
			<tipo_attivita>C07</tipo_attivita>
			<titolo_denominazione>FORMAZIONE NON GENERALISTA MIRATA ALL''INSERIMENTO LAVORATIVO</titolo_denominazione>
			<data_proposta>2018-02-16</data_proposta>
			<data>2018-03-01</data>
			<data_fine>2018-06-30</data_fine>
			<durata>2</durata>
			<tipologia_durata>G</tipologia_durata>
			<descrizione>Progetto di politica attiva regionale/provinciale (Parma)</descrizione>
			<titolo_progetto>01</titolo_progetto>
			<codice_ente_promotore>G337C000409</codice_ente_promotore>
			<identificativo_politica>0620080048E</identificativo_politica>
			<indice_profiling>0.79</indice_profiling>
			<ultimo_evento>
				<evento>10</evento>
				<data_evento>2018-06-30</data_evento>
			</ultimo_evento>
		</politiche_attive>
		<politiche_attive>
			<tipo_attivita>C05</tipo_attivita>
			<titolo_denominazione>FORMAZIONE PER ACQUISIZIONE QUALIFICA</titolo_denominazione>
			<data_proposta>2018-06-01</data_proposta>
			<data>2018-06-01</data>
			<data_fine>2018-09-30</data_fine>
			<durata>4</durata>
			<tipologia_durata>M</tipologia_durata>
			<descrizione>AGENZIA DEI CUOCHI S.R.L. (Ente Bologna)</descrizione>
			<titolo_progetto>02</titolo_progetto>
			<codice_ente_promotore>A944S003140</codice_ente_promotore>
		</politiche_attive>
		<politiche_attive>
			<tipo_attivita>C05</tipo_attivita>
			<titolo_denominazione>FORMAZIONE PER ACQUISIZIONE QUALIFICA</titolo_denominazione>
			<data_proposta>2018-06-01</data_proposta>
			<data>2018-06-01</data>
			<data_fine>2018-09-30</data_fine>
			<durata>4</durata>
			<tipologia_durata>M</tipologia_durata>
			<descrizione>CIFAP (Centro Inter.le Formazione Addestramento Professionale) (Ente RC)</descrizione>
			<titolo_progetto>02</titolo_progetto>
			<codice_ente_promotore>H224S015591</codice_ente_promotore>
		</politiche_attive>
		<politiche_attive>
			<tipo_attivita>A02</tipo_attivita>
			<titolo_denominazione>STEFY</titolo_denominazione>
			<data_proposta>2010-06-01</data_proposta>
			<data>2010-06-01</data>
			<data_fine>2010-09-30</data_fine>
			<durata>4</durata>
			<tipologia_durata>M</tipologia_durata>
			<descrizione>STEFY NON ESISTE</descrizione>
			<titolo_progetto>02</titolo_progetto>
			<codice_ente_promotore>00000000000</codice_ente_promotore>
		</politiche_attive>
	</politiche_attive_lst>
</lavoratore>]]>')
);
/
-- insert di riferimento
insert into ws_stub.did values (
'GRGGNN98L01H199A',
to_clob('<![CDATA[<ListaDID>
	<codicefiscale>GRGGNN98L01H199A</codicefiscale>
	<codiceentetit>H501C000523</codiceentetit>
	<dataultimoagg>2018-09-21</dataultimoagg>
	<indiceprofiling>0.644371661</indiceprofiling>
	<dataevento>2018-04-05</dataevento>
	<disponibilita>2018-04-05</disponibilita>
	<N00>
		<tipo_attivita>N00</tipo_attivita>
		<titolo_denominazione>0000010800G</titolo_denominazione>
		<data_proposta>2018-04-05</data_proposta>
		<data>2018-04-05</data>
		<data_fine>2018-04-05</data_fine>
		<descrizione>Convalida DID Online</descrizione>
		<titolo_progetto>05</titolo_progetto>
		<codice_ente_promotore>H501N000001</codice_ente_promotore>
		<identificativo_politica>0000010800G</identificativo_politica>
		<indice_profiling>0.644371661</indice_profiling>
		<identificativo_presa_in_carico>0670018e72E</identificativo_presa_in_carico>
		<ultimo_evento>
			<evento>02</evento>
			<data_evento>2018-04-05</data_evento>
		</ultimo_evento>
	</N00>
	<A02>
		<tipo_attivita>A02</tipo_attivita>
		<titolo_denominazione>PATTO DI ATTIVAZIONE ED EVENTUALE PROFILING</titolo_denominazione>
		<data_proposta>2018-04-05</data_proposta>
		<data>2018-04-05</data>
		<data_fine>2018-04-05</data_fine>
		<descrizione>Progetto di politica attiva regionale/provinciale</descrizione>
		<titolo_progetto>05</titolo_progetto>
		<codice_ente_promotore>F257C000424</codice_ente_promotore>
		<identificativo_politica>0670018e72E</identificativo_politica>
		<indice_profiling>2</indice_profiling>
		<ultimo_evento>
			<evento>02</evento>
			<data_evento>2018-04-05</data_evento>
		</ultimo_evento>
	</A02>
</ListaDID>]]>'));
/
-- servizi YG adesione --------------------------------------------------------------------
CREATE TABLE ws_stub.adesione_yg
(
  codicefiscale VARCHAR2(16),
  statoadesione VARCHAR2(3),
  risposta CLOB,
  CONSTRAINT adesione_yg_pkey PRIMARY KEY (codicefiscale) USING INDEX
);
/
-- insert di riferimento
insert into ws_stub.adesione_yg values (
'TSTGCM96D28H199F',
'A',
to_clob('<![CDATA[<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<sezione0>
    <utente>
        <CodiceFiscale>TSTGCM96D28H199F</CodiceFiscale>
        <dataadesione>2014-11-21</dataadesione>
        <IdentificativoSap>AA02096552D</IdentificativoSap>
        <Regione>08</Regione>
    </utente>
</sezione0>
]]>'));
/
-- servizi soggettiAccreditati del layer di cooperazione --------------------------------------------------------------------
CREATE TABLE ws_stub.soggetti_accreditati
(
  codice_identificativo VARCHAR2(20),
  denominazione VARCHAR2(255),
  misura VARCHAR2(255),
  provincia VARCHAR2(2),
  comune VARCHAR2(4),
  tipologia VARCHAR2(10),
  pagina VARCHAR2(5),
  risposta CLOB
);
/
insert into ws_stub.soggetti_accreditati values (
'H501C001019',
'CPI ROMA OSTIA',
'YG-A03,YG-A06,YG-B04,YG-B05,YG-B08',
'RM',
'H501',
'CPI',
'1',
to_clob('{
"record_totali": 1,
"pagina_corrente": 1,
"pagine_totali": 1,
  "soggetti_accreditati": [
      {
      "persona_giuridica":       {
         "ragione_sociale": "CPI ROMA OSTIA",
         "codice_fiscale": "02081750560",
         "partita_iva": "02081750560",
         "natura_giuridica": {"id": "1.03.20"},
         "email": "mail@mail.com",
         "telefono": "06 5678098",
         "province":          [
            {"id": "VT"},
            {"id": "RM"}
         ],
         "comuni":          [
            {"id": "M082"},
            {"id": "H501"}
         ]
      },
      "misure":       [
         {"id": "YG-A03"},
         {"id": "YG-A06"},
         {"id": "YG-B04"},
         {"id": "YG-B05"},
         {"id": "YG-B08"},
      ]
   }
]}'));
/



CREATE TABLE "WS_STUB"."PROFILING_YG" (
    "CODICEFISCALE" VARCHAR2(16 BYTE),
    "RISPOSTA_SELECT" CLOB,
    "RISPOSTA_INSERT" CLOB,
    CONSTRAINT "PROFILING_YG_PKEY" PRIMARY KEY ("CODICEFISCALE")
);
/

