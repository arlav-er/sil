set define off
set pagesize 0
set linesize 10000
set sqlblanklines on




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/Aggiorna_DataFinePatto.sql
************************************************************************************** */


-- /*Funzione che imposta la data fine patto, utile nel caso di errata digitazione 
-- o problemi applicativi che generano più patti senza data fine.
-- 
-- Non occorre impostare la data del documento perche' eseite già un triggher che lo fa automaticamente.
-- 
-- Passando null in p_codmotivofineatto viene mantenuto quello preesistente.
-- 
-- La funzionme si occupa anche di loggare la modifica sulla tabella di log.
-- */

create or replace function Aggiorna_DataFinePatto(p_prgPattoLavoratore am_patto_lavoratore.prgpattolavoratore%type,
                                                  p_dataFne            am_patto_lavoratore.datfine%type,
                                                  p_codmotivofineatto  am_patto_lavoratore.codmotivofineatto%type)
  return number is
  utente      NUMBER(38) := 100;
  nomeTabella varchar2(20) := 'AM_PATTO_LAVORATORE';
begin

  PG_LOG.doLog('U',
               nomeTabella,
               utente,
               'where prgPattoLavoratore  = ' || p_prgPattoLavoratore);
 
  update AM_PATTO_LAVORATORE
     set CDNUTMOD              = utente,
         DTMMOD                = sysdate,
         DATFINE               = p_dataFne,
         codmotivofineatto     = nvl(p_codmotivofineatto, codmotivofineatto),
         NUMKLOPATTOLAVORATORE = NUMKLOPATTOLAVORATORE + 1
   where prgPattoLavoratore = p_prgPattoLavoratore;

  commit;

  return 0;
exception
  when others then
    dbms_output.put_line('Errore= ' || sqlcode || ' ' || sqlerrm);
    return - 1;
end Aggiorna_DataFinePatto;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/AGGIORNA_SCADENZE_UTENTI.sql
************************************************************************************** */


-- Funzione per aggiornare massivamente le date fine degli utenti
-- Data una data vecchia e una nuova imposta la nuova data nuova  per tutti gli utenti 
-- che corrispondono alla vecchia data, e logga la modifica,
create or replace function AGGIORNA_SCADENZE_UTENTI(p_old_data_fine date, p_new_data_fine date) return number is

  result number := 0;       

  CURSOR curUtenti IS
  select u.cdnut 
  from TS_UTENTE u
  where TRUNC(u.Datfineval) = TRUNC(p_old_data_fine);
  
  utente      NUMBER(38) := 100;
  nomeTabella varchar2(20) := 'TS_UTENTE';
  queryWhere VARCHAR2(255);
begin

  FOR recUtente in curUtenti LOOP
/*tracciare ogni operazione nella rispettiva tabella di log*/
     queryWhere :=  'where TS_UTENTE.CDNUT = '|| recUtente.CDNUT;
     PG_LOG.doLog('U',nomeTabella, utente, queryWhere);

    UPDATE TS_UTENTE 
    SET TS_UTENTE.datfineval = p_new_data_fine,
        TS_UTENTE.cdnutmod = utente,
        TS_UTENTE.dtmmod = sysdate
    WHERE  TS_UTENTE.CDNUT =  recUtente.CDNUT;

    result :=   result + 1;

    END LOOP;
    return result;
    exception
        when others then
           dbms_output.put_line('Errore= ' || sqlcode || ' ' || sqlerrm);
           return -1;

end AGGIORNA_SCADENZE_UTENTI;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/BLOB_TO_CLOB.sql
************************************************************************************** */


CREATE OR REPLACE FUNCTION BLOB_TO_CLOB (B BLOB) return clob is
  c clob;
  n number;
begin
  if (b is null) then
    return null;
  end if;
  
  if (dbms_lob.GETLENGTH(b)=0) then
    return empty_clob();
  end if;
  dbms_lob.createtemporary(c,true);
  n:=1;
  while (n+32767<=dbms_lob.GETLENGTH(b)) loop
    dbms_lob.writeappend(c,32767,utl_raw.cast_to_varchar2(dbms_lob.substr(b,32767,n)));
    n:=n+32767;
  end loop;
  dbms_lob.writeappend(c,dbms_lob.GETLENGTH(b)-n+1,utl_raw.cast_to_varchar2(dbms_lob.substr(b,dbms_lob.GETLENGTH(b)-n+1,n)));
  return c;
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/checkAdesionePatto.sql
************************************************************************************** */


create or replace
function checkAdesionePatto(prgPattoLav in am_patto_lavoratore.prgpattolavoratore%type, dataAdesione out varchar2) return varchar2
is
	cursor azioniGG IS
          select to_char(perAdesione.datadesionegg, 'dd/mm/yyyy') datadesionegg
		  from am_patto_lavoratore patto
		  inner join am_lav_patto_scelta on (patto.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore and am_lav_patto_scelta.codlsttab = 'OR_PER')
		  inner join or_percorso_concordato on (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso)
		  inner join or_colloquio on (or_percorso_concordato.prgcolloquio = or_colloquio.prgcolloquio)
		  inner join de_azione on (or_percorso_concordato.prgazioni = de_azione.prgazioni)
		  inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg)
		  inner join ma_azione_tipoattivita on (de_azione.prgazioni = ma_azione_tipoattivita.prgazioni)
		  left join or_percorso_concordato perAdesione on (or_percorso_concordato.prgpercorsoadesione = perAdesione.prgpercorso and
														   or_percorso_concordato.prgcolloquioadesione = perAdesione.prgcolloquio)
		  where (patto.prgpattolavoratore = prgPattoLav) and (patto.codtipopatto in ('MGG', 'MGGU'))
		  and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU')
		  and (trunc(or_colloquio.datcolloquio) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval));
		  
	p_datdataadesione varchar2(10);
	p_datdataadesionePrec varchar2(10) := '''';
	errCodeOut varchar2(2) := '00';
	
	numCountAdesioniErrate integer := 0;
	p_misurapatto am_patto_lavoratore.codtipopatto%type;
	
begin
	
	select codtipopatto into p_misurapatto
	from am_patto_lavoratore
	where prgpattolavoratore = prgPattoLav;
	
	if (p_misurapatto = 'ANP') then
		
		select count(*) into numCountAdesioniErrate
		from am_patto_lavoratore patto
		inner join am_lav_patto_scelta on (patto.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore and am_lav_patto_scelta.codlsttab = 'OR_PER')
		inner join or_percorso_concordato on (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso)
		inner join or_colloquio on (or_percorso_concordato.prgcolloquio = or_colloquio.prgcolloquio)
		inner join de_servizio on (or_colloquio.codservizio = de_servizio.codservizio)
		inner join de_azione on (or_percorso_concordato.prgazioni = de_azione.prgazioni)
		inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg)
		inner join ma_azione_tipoattivita on (de_azione.prgazioni = ma_azione_tipoattivita.prgazioni)
		inner join or_percorso_concordato perAdesione on (or_percorso_concordato.prgpercorsoadesione = perAdesione.prgpercorso and
														  or_percorso_concordato.prgcolloquioadesione = perAdesione.prgcolloquio)
		where (patto.prgpattolavoratore = prgPattoLav)
		and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU')
		and (trunc(or_colloquio.datcolloquio) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval))
		and exists (select 1 from am_patto_lavoratore patto1
			inner join am_lav_patto_scelta scelta1 on (patto1.prgpattolavoratore = scelta1.prgpattolavoratore and scelta1.codlsttab = 'OR_PER')
			inner join or_percorso_concordato orper on (to_number(scelta1.strchiavetabella) = orper.prgpercorso)
			inner join de_azione az on (orper.prgazioni = az.prgazioni)
			inner join de_azione_ragg ragg on (az.prgazioneragg = ragg.prgazioniragg)
			inner join or_percorso_concordato perAdesione1 on (orper.prgpercorsoadesione = perAdesione1.prgpercorso and
															   orper.prgcolloquioadesione = perAdesione1.prgcolloquio)
			where patto1.prgpattolavoratore = patto.prgpattolavoratore and orper.prgcolloquio = or_percorso_concordato.prgcolloquio
			and orper.prgpercorso != or_percorso_concordato.prgpercorso and 
				(ragg.flg_misurayei = 'S' or ragg.codmonopacchetto = 'GU') and trunc(perAdesione1.datadesionegg) != trunc(perAdesione.datadesionegg));
		
		if (numCountAdesioniErrate > 0) then
			return '01';
		else
			begin
				select to_char(max(perAdesione.datadesionegg), 'dd/mm/yyyy')
				into p_datdataadesionePrec
				from am_patto_lavoratore patto
				inner join am_lav_patto_scelta on (patto.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore and am_lav_patto_scelta.codlsttab = 'OR_PER')
				inner join or_percorso_concordato on (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso)
				inner join or_colloquio on (or_percorso_concordato.prgcolloquio = or_colloquio.prgcolloquio)
				inner join de_servizio on (or_colloquio.codservizio = de_servizio.codservizio)
				inner join de_azione on (or_percorso_concordato.prgazioni = de_azione.prgazioni)
				inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg)
				inner join ma_azione_tipoattivita on (de_azione.prgazioni = ma_azione_tipoattivita.prgazioni)
				inner join or_percorso_concordato perAdesione on (or_percorso_concordato.prgpercorsoadesione = perAdesione.prgpercorso and
																  or_percorso_concordato.prgcolloquioadesione = perAdesione.prgcolloquio)
				where (patto.prgpattolavoratore = prgPattoLav)
				and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU')
				and (trunc(or_colloquio.datcolloquio) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval));
				
				dataAdesione := p_datdataadesionePrec;
				return '00';
				
			exception
				WHEN OTHERS THEN
					return '02';
			end;
		
		end if;
	
	else
	
		for azione in azioniGG loop
			p_datdataadesione := azione.datadesionegg;
			if (p_datdataadesionePrec = '''') then
				if (p_datdataadesione is not null and p_datdataadesione != '''') then
					p_datdataadesionePrec := p_datdataadesione;
				end if;
			else
				if (p_datdataadesione is not null and p_datdataadesione != '''') then
					if (p_datdataadesionePrec != p_datdataadesione) then
						errCodeOut := '01';
					end if;
				end if;
			end if;
		end loop;
		
		if (p_datdataadesionePrec = '''') then
			return '02';
		else
			if (errCodeOut = '01') then
				return '01';
			else
				dataAdesione := p_datdataadesionePrec;
				return '00';
			end if;
		end if;
		
	end if;

exception
   when others then
      return '99';
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/checkAdesioneProgrammaPatto.sql
************************************************************************************** */


create or replace
function checkAdesioneProgrammaPatto(prgPattoLav in am_patto_lavoratore.prgpattolavoratore%type, prgColl in or_colloquio.prgcolloquio%type,
	dataAdesione out varchar2) return varchar2
is
	
	p_datdataadesionePrec varchar2(10) := '''';
	errCodeOut varchar2(2) := '00';
	numCountAdesioniErrate integer := 0;
	
begin
		
	select count(*) into numCountAdesioniErrate
	from am_patto_lavoratore patto
	inner join am_lav_patto_scelta on (patto.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore and am_lav_patto_scelta.codlsttab = 'OR_PER')
	inner join or_percorso_concordato on (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso)
	inner join or_colloquio on (or_percorso_concordato.prgcolloquio = or_colloquio.prgcolloquio)
	inner join de_servizio on (or_colloquio.codservizio = de_servizio.codservizio)
	inner join de_azione on (or_percorso_concordato.prgazioni = de_azione.prgazioni)
	inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg)
	inner join ma_azione_tipoattivita on (de_azione.prgazioni = ma_azione_tipoattivita.prgazioni)
	inner join or_percorso_concordato perAdesione on (or_percorso_concordato.prgpercorsoadesione = perAdesione.prgpercorso and
													  or_percorso_concordato.prgcolloquioadesione = perAdesione.prgcolloquio)
	where (patto.prgpattolavoratore = prgPattoLav)
	and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU')
	and (or_colloquio.prgcolloquio = prgColl)
	and (trunc(or_colloquio.datcolloquio) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval))
	and exists (select 1 from am_patto_lavoratore patto1
		inner join am_lav_patto_scelta scelta1 on (patto1.prgpattolavoratore = scelta1.prgpattolavoratore and scelta1.codlsttab = 'OR_PER')
		inner join or_percorso_concordato orper on (to_number(scelta1.strchiavetabella) = orper.prgpercorso)
		inner join de_azione az on (orper.prgazioni = az.prgazioni)
		inner join de_azione_ragg ragg on (az.prgazioneragg = ragg.prgazioniragg)
		inner join or_percorso_concordato perAdesione1 on (orper.prgpercorsoadesione = perAdesione1.prgpercorso and
														   orper.prgcolloquioadesione = perAdesione1.prgcolloquio)
		where patto1.prgpattolavoratore = patto.prgpattolavoratore and orper.prgcolloquio = or_colloquio.prgcolloquio
		and orper.prgpercorso != or_percorso_concordato.prgpercorso and 
			(ragg.flg_misurayei = 'S' or ragg.codmonopacchetto = 'GU') and trunc(perAdesione1.datadesionegg) != trunc(perAdesione.datadesionegg));
	
	if (numCountAdesioniErrate > 0) then
		return '01';
	else
		begin
			select to_char(max(perAdesione.datadesionegg), 'dd/mm/yyyy')
			into p_datdataadesionePrec
			from am_patto_lavoratore patto
			inner join am_lav_patto_scelta on (patto.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore and am_lav_patto_scelta.codlsttab = 'OR_PER')
			inner join or_percorso_concordato on (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso)
			inner join or_colloquio on (or_percorso_concordato.prgcolloquio = or_colloquio.prgcolloquio)
			inner join de_servizio on (or_colloquio.codservizio = de_servizio.codservizio)
			inner join de_azione on (or_percorso_concordato.prgazioni = de_azione.prgazioni)
			inner join de_azione_ragg on (de_azione.prgazioneragg = de_azione_ragg.prgazioniragg)
			inner join ma_azione_tipoattivita on (de_azione.prgazioni = ma_azione_tipoattivita.prgazioni)
			inner join or_percorso_concordato perAdesione on (or_percorso_concordato.prgpercorsoadesione = perAdesione.prgpercorso and
															  or_percorso_concordato.prgcolloquioadesione = perAdesione.prgcolloquio)
			where (patto.prgpattolavoratore = prgPattoLav)
			and (de_azione_ragg.flg_misurayei = 'S' or de_azione_ragg.codmonopacchetto = 'GU')
			and (or_colloquio.prgcolloquio = prgColl)
			and (trunc(or_colloquio.datcolloquio) between trunc(ma_azione_tipoattivita.datinizioval) and trunc(ma_azione_tipoattivita.datfineval));
			
			dataAdesione := p_datdataadesionePrec;
			return '00';
			
		exception
			WHEN OTHERS THEN
				return '02';
		end;
	
	end if;

exception
   when others then
      return '99';
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/checkExProgConAzSiferAllaData.sql
************************************************************************************** */


create or replace function checkExProgConAzSiferAllaData (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type, listaAzioniSifer varchar2, dataIn date) return int
is
	
	CURSOR programmiConAzioniSifer IS
      select distinct az.codazionesifer
			from am_patto_lavoratore 
			inner join or_colloquio coll on (am_patto_lavoratore.cdnlavoratore = coll.cdnlavoratore)
			inner join or_percorso_concordato perc on (coll.prgcolloquio = perc.prgcolloquio)
			inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE
				and to_number(am_lav_patto_scelta.strchiavetabella) = perc.prgpercorso and am_lav_patto_scelta.CODLSTTAB = 'OR_PER')
			inner join de_azione az ON (az.prgazioni = perc.prgazioni) 
			where (am_patto_lavoratore.PRGPATTOLAVORATORE = prgPattoPar)
			and (trunc(dataIn) >= trunc(coll.datcolloquio)) 
			and (coll.datfineprogramma is null or trunc(dataIn) <= trunc(coll.datfineprogramma));
	
	p_codazionesifer varchar2(12);
			
begin

	FOR programmaConAzioniSifer IN programmiConAzioniSifer loop
		p_codazionesifer := '''' || programmaConAzioniSifer.codazionesifer || '''';
		if (instr(listaAzioniSifer, p_codazionesifer) > 0) then
			return 1;
		end if;
		
	end loop;
	
	return 0;
exception
	when others then
		return 0;
	
end checkExProgConAzSiferAllaData;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/DENCRYPT.sql
************************************************************************************** */


create or replace function decrypt
(input_string varchar2, key_string varchar2) 
return varchar2
IS

 decrypted_string    VARCHAR2(2048);
-- l_units int;
 pad_input_string varchar2(2048);
BEGIN
    
    if(input_string is null) then
        return '';
    else    
    
     
    pad_input_string := UTL_RAW.Cast_To_Varchar2( hextoraw(input_string));                          
    
    dbms_obfuscation_toolkit.DESDecrypt(
                     input_string => pad_input_string, 
                     key_string => key_string, 
                     decrypted_string => decrypted_string);
               
      
    return trim(decrypted_string);
  
    end if; 
                
END;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/DISINOCLUNGADURATA.sql
************************************************************************************** */


CREATE OR REPLACE function disInocLungaDurata (cdnParLav an_lavoratore.CDNLAVORATORE%type,
	   	  		  		   					   dataVarNascita an_lavoratore.DATNASC%type,
											   dataParCalcolo an_lavoratore.DATNASC%type,
											   flgVarObbScol am_obbligo_formativo.FLGOBBLIGOSCOLASTICO%type,
											   flgVarLaurea de_titolo.FLGLAUREA%type) return varchar2

is
resultVar varchar2(1000) := '';
codVarStatoOccupaz am_stato_occupaz.codstatooccupaz%type;
codVarStatoOccupazRagg de_stato_occupaz_ragg.codstatooccupazragg%type;
codVarCat181 am_stato_occupaz.codcategoria181%type;
catVar181 varchar2(1) := '';
datVarInizio varchar2(12);
datVarFine   varchar2(12);
datVarAnzDisocc varchar2(12);
numVarMesiSosp int := 0;
numVarMesiSospFornero int := 0;
numVarMesiAnz int := 0;
numVarMesiRischioDisocc int := 0;
numVarMesiRischioDisoccComp varchar2(50);
mesiSospFornero2014Comp varchar2(50);
numVarMesiAnzPrec int := 0;
numVarMesiSospPrec int := 0;
datVarCalcoloAnzianita varchar2(12);
datVarCalcoloSosp varchar2(12);
numVarMesiTotAnz int;
numGGRestantiSosp number := 0;
numVarCont int;
mesiAggiuntivi number := 0;
meseDiffAnzianitaGiorni int;
ggResiduiAnzianita int := 0;
dataFornero ts_generale.datFornero%type;
data150 ts_generale.dat150%type;
dataVarCalcolo ts_generale.datFornero%type;

begin
	 select count(*)
	 into numVarCont
	 from am_stato_occupaz, an_lav_storia_inf
	 where am_stato_occupaz.CDNLAVORATORE = an_lav_storia_inf.CDNLAVORATORE
	 and am_stato_occupaz.CDNLAVORATORE = cdnParLav
	 and am_stato_occupaz.datfine IS NULL
	 and an_lav_storia_inf.datfine IS NULL;

	 if (numVarCont > 0) then
		
		select trunc(datFornero) 
		into dataFornero
		from ts_generale;
		
		select trunc(dat150) 
		into data150
		from ts_generale;
	 
		if (trunc(dataParCalcolo) >= dataFornero) then
			dataVarCalcolo := dataFornero - 1; 
		else
			dataVarCalcolo := dataParCalcolo;
		end if;

		SELECT am_stato_occupaz.codstatooccupaz,
	         	de_stato_occupaz_ragg.codstatooccupazragg,
	         	am_stato_occupaz.codcategoria181,
	         	TO_CHAR (am_stato_occupaz.datinizio, 'DD/MM/YYYY') datinizio,
	         	TO_CHAR (am_stato_occupaz.datfine, 'DD/MM/YYYY') datfine,
	         	TO_CHAR (am_stato_occupaz.datanzianitadisoc,'DD/MM/YYYY') datanzianitadisoc,
				to_number(substr(PG_MOVIMENTI.MesiSospFornero2014AllaData(cdnParLav, dataParCalcolo, am_stato_occupaz.datcalcolomesisosp), 1,
				   instr(PG_MOVIMENTI.MesiSospFornero2014AllaData(cdnParLav, dataParCalcolo, am_stato_occupaz.datcalcolomesisosp), '-', 1)-1)) mesiSospFornero2014,
			    PG_MOVIMENTI.MesiSospFornero2014AllaData(cdnParLav, dataParCalcolo, am_stato_occupaz.datcalcolomesisosp) mesiSospFornero2014Comp,
	         	case
	              when de_stato_occupaz_ragg.CODSTATOOCCUPAZRAGG in ('D','I')
	              then
	              	(SELECT SUM (
				          (to_number(
							(to_char(CASE
						 				WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
                                          THEN 
											(CASE
                                          		WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
                                          		THEN am_stato_occupaz.datcalcolomesisosp
                                          		ELSE (dataFornero - 1)
                                        	END)
										WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
                                          THEN dataVarCalcolo
                                        WHEN occ.datfine < dataVarCalcolo
                                          THEN occ.datfine
                                     END,'yyyy')
                             - to_char(CASE
 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
                                              THEN  
	                                          	(CASE
	                                          		WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
	                                          		THEN am_stato_occupaz.datcalcolomesisosp
	                                          		ELSE (dataFornero - 1)
	                                        	END)
											WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
                                              THEN occ.datInizio
                                       END,'yyyy')) * 12
	  						 + to_char(CASE
							 				WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
		                                      THEN
												(CASE
													WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
													THEN am_stato_occupaz.datcalcolomesisosp
													ELSE (dataFornero - 1)
												END)
											WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
		                                      THEN dataVarCalcolo
		                                    WHEN occ.datfine < dataVarCalcolo
		                                      THEN occ.datfine
		                               END,'mm')
		                    - to_char(CASE
 						                 	WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
                                              THEN
												(CASE
	                                          		WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
	                                          		THEN am_stato_occupaz.datcalcolomesisosp
	                                          		ELSE (dataFornero - 1)
	                                        	END)
											WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
                                              THEN occ.datInizio
                                      END,'mm') + 1
						+ (case 
	   	 	  				   when ((to_char(CASE
								 				WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
	                                              THEN
													(CASE
														WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
														THEN am_stato_occupaz.datcalcolomesisosp
														ELSE (dataFornero - 1)
													END)
												WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
	                                              THEN dataVarCalcolo
	                                            WHEN occ.datfine < dataVarCalcolo
	                                              THEN occ.datfine
	                                          END,'yyyy') 
	                                  - to_char(CASE
		 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
		                                              THEN
														(CASE
															WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
															THEN am_stato_occupaz.datcalcolomesisosp
															ELSE (dataFornero - 1)
														END)
													WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
		                                              THEN occ.datInizio
		                                         END,'yyyy')) * 12
	   		  	   			   	      + to_char(CASE
									 				WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
		                                              THEN
														(CASE
															WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
															THEN am_stato_occupaz.datcalcolomesisosp
															ELSE (dataFornero - 1)
														END)
													WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
		                                              THEN dataVarCalcolo
		                                            WHEN occ.datfine < dataVarCalcolo
		                                              THEN occ.datfine
		                                        END,'mm') 
		                              - to_char(CASE
		 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
		                                              THEN 
														(CASE
															WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
															THEN am_stato_occupaz.datcalcolomesisosp
															ELSE (dataFornero - 1)
														END)
													WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
		                                              THEN occ.datInizio
		                                          END,'mm') + 1) > 1
			  
			  				   then	  
	   						   		   -(case when
				   	 	  	  		  	 	 (30 - to_char(CASE
					 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
					                                              THEN
																	(CASE
																		WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
																		THEN am_stato_occupaz.datcalcolomesisosp
																		ELSE (dataFornero - 1)
																	END)
																WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
					                                              THEN occ.datInizio
					                                          END,'dd') + 1) < 16
						  				   	 then 1
						  				   	 when
				   	 	  	  			   	 (30 - to_char(CASE
					 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
					                                              THEN
																	(CASE
																		WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
																		THEN am_stato_occupaz.datcalcolomesisosp
																		ELSE (dataFornero - 1)
																	END)
																WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
					                                              THEN occ.datInizio
					                                          END,'dd') + 1) >= 15
						  				   	 then 0
					  				   end)
					  				   -(case when
				   	 	  	  			 	  to_char(CASE
										                WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
					                                      THEN
															(CASE
																WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
																THEN am_stato_occupaz.datcalcolomesisosp
																ELSE (dataFornero - 1)
															END)
														WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
					                                      THEN dataVarCalcolo
					                                    WHEN occ.datfine < dataVarCalcolo
					                                      THEN occ.datfine
					                                  END,'dd') < 16
						  					  then 1
						  					  when
				   	 	  	  				  to_char(CASE
										                WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
			                                              THEN
															(CASE
																WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
																THEN am_stato_occupaz.datcalcolomesisosp
																ELSE (dataFornero - 1)
															END)
														WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
			                                              THEN dataVarCalcolo
			                                            WHEN occ.datfine < dataVarCalcolo
			                                              THEN occ.datfine
			                                          END,'dd') >= 15
						  					  then 0
					  					end)
							  when
			 	  			  	  ((to_char(CASE
								 				WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
	                                              THEN
													(CASE
														WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
														THEN am_stato_occupaz.datcalcolomesisosp
														ELSE (dataFornero - 1)
													END)
												WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
	                                              THEN dataVarCalcolo
	                                            WHEN occ.datfine < dataVarCalcolo
	                                              THEN occ.datfine
	                                          END,'yyyy') 
	                                - to_char(CASE
	 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
	                                              THEN
													(CASE
														WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
														THEN am_stato_occupaz.datcalcolomesisosp
														ELSE (dataFornero - 1)
													END)
												WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
	                                              THEN occ.datInizio
	                                          END,'yyyy')) * 12
	   		  	  			  	  	+ to_char(CASE
								 				WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
	                                              THEN
													(CASE
														WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
														THEN am_stato_occupaz.datcalcolomesisosp
														ELSE (dataFornero - 1)
													END)
												WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
	                                              THEN dataVarCalcolo
	                                            WHEN occ.datfine < dataVarCalcolo
	                                              THEN occ.datfine
	                                          END,'mm') 
	                                - to_char(CASE
	 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
	                                              THEN
													(CASE
														WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
														THEN am_stato_occupaz.datcalcolomesisosp
														ELSE (dataFornero - 1)
													END)
												WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
	                                              THEN occ.datInizio
	                                          END,'mm') + 1) = 1
			 				  then
			 	 			  	  -(case when
			   	 	  	  		  		 (to_char(CASE
									                WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
		                                              THEN
														(CASE
															WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
															THEN am_stato_occupaz.datcalcolomesisosp
															ELSE (dataFornero - 1)
														END)
													WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
		                                              THEN dataVarCalcolo
		                                            WHEN occ.datfine < dataVarCalcolo
		                                              THEN occ.datfine
		                                          END,'dd') 
		                                 - to_char(CASE
			 						                  WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
			                                             THEN
															(CASE
																WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
																THEN am_stato_occupaz.datcalcolomesisosp
																ELSE (dataFornero - 1)
															END)
													  WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
			                                             THEN occ.datInizio
			                                       END,'dd') + 1) < 16 
					  					 then 1 
					    		         when
			   	 	  	  	 			 (to_char(CASE
									                WHEN NVL (occ.datfine, dataVarCalcolo) <= am_stato_occupaz.datcalcolomesisosp
		                                              THEN
														(CASE
															WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
															THEN am_stato_occupaz.datcalcolomesisosp
															ELSE (dataFornero - 1)
														END)
													WHEN NVL (occ.datfine, dataVarCalcolo) >= dataVarCalcolo
		                                              THEN dataVarCalcolo
		                                            WHEN occ.datfine < dataVarCalcolo
		                                              THEN occ.datfine
		                                          END,'dd') 
		                                 - to_char(CASE
		 						                     WHEN occ.datInizio <= am_stato_occupaz.datcalcolomesisosp
		                                                THEN
														  (CASE
															WHEN am_stato_occupaz.datcalcolomesisosp <= (dataFornero - 1)
															THEN am_stato_occupaz.datcalcolomesisosp
															ELSE (dataFornero - 1)
														  END)
													 WHEN occ.datInizio > am_stato_occupaz.datcalcolomesisosp
		                                                THEN occ.datInizio
		                                           END,'dd') + 1) >= 15
					    				 then 0
				  				    end)
			 				  end))))
				          	   NUMMESISOSP
	                FROM am_stato_occupaz occ
		                 WHERE occ.cdnlavoratore = cdnParLav
		                   AND occ.codstatooccupaz = 'B1'
		                   AND (occ.datinizio >= occ.datcalcolomesisosp or
		                        (occ.datcalcolomesisosp >= occ.datinizio and
		                         occ.datcalcolomesisosp <= nvl(occ.datfine,dataVarCalcolo)))
						   AND trunc(occ.datinizio) < dataFornero
		            )
	              when de_stato_occupaz_ragg.CODSTATOOCCUPAZRAGG not in ('D','I')
	               then
	                  0
	         end
	         NUMMESISOSP,
	         case
	    		 	   when de_stato_occupaz_ragg.CODSTATOOCCUPAZRAGG in ('D','I')
	        	   then
	               (to_number(
								(to_char(dataParCalcolo,'yyyy')
	                             - to_char(am_stato_occupaz.datcalcoloanzianita,'yyyy')) * 12
		  						 + to_char(dataParCalcolo,'mm')
			                    - to_char(am_stato_occupaz.datcalcoloanzianita,'mm') + 1
							+ (case
		   	 	  				   when ((to_char(dataParCalcolo,'yyyy')
		                                  - to_char(am_stato_occupaz.datcalcoloanzianita,'yyyy')) * 12
		   		  	   			   	      + to_char(dataParCalcolo,'mm')
			                              - to_char(am_stato_occupaz.datcalcoloanzianita,'mm') + 1) > 1
								   then
										   -(case when
												 trunc(am_stato_occupaz.datcalcoloanzianita) >= data150 
													then 2 - (trunc( (
														(case 
														  when to_number(to_char(LAST_DAY(to_date('01/' || to_char(dataParCalcolo, 'mm') || '/' || to_char(dataParCalcolo, 'yyyy'), 'dd/mm/yyyy')), 'dd')) =
															   to_number(to_char(dataParCalcolo, 'dd'))
														  then 30
														  else to_number(to_char(dataParCalcolo, 'dd'))
														end) + (30 - 
																(case
																  when to_number(to_char(am_stato_occupaz.datcalcoloanzianita, 'dd')) < 31 
																  then to_number(to_char(am_stato_occupaz.datcalcoloanzianita, 'dd'))
																  else 30
																end) + 1
															  )) /30))
												 when
												 (30 - to_char(am_stato_occupaz.datcalcoloanzianita,'dd') + 1) < 16
												 then 1
												 when
												 (30 - to_char(am_stato_occupaz.datcalcoloanzianita,'dd') + 1) >= 15
												 then 0
										   end)
										   -(case when
												  trunc(am_stato_occupaz.datcalcoloanzianita) < data150 and to_char(dataParCalcolo,'dd') < 16
												  then 1
												  when
												  trunc(am_stato_occupaz.datcalcoloanzianita) < data150 and to_char(dataParCalcolo,'dd') >= 15
												  then 0
												  else 0
										   end)

				 				  when
				 	  			  	  ((to_char(dataParCalcolo,'yyyy')
		                                - to_char(am_stato_occupaz.datcalcoloanzianita,'yyyy')) * 12
		   		  	  			  	  	+ to_char(dataParCalcolo,'mm')
		                                - to_char(am_stato_occupaz.datcalcoloanzianita,'mm') + 1) = 1
				 				  then
				 	 			  	  -(case when
											trunc(am_stato_occupaz.datcalcoloanzianita) >= data150 and 
											 (to_number(to_char(dataParCalcolo,'dd')) - to_number(to_char(am_stato_occupaz.datcalcoloanzianita,'dd')) + 1) =
											 to_number(to_char(LAST_DAY(to_date('01/' || to_char(dataParCalcolo, 'mm') || '/' || to_char(dataParCalcolo, 'yyyy'), 'dd/mm/yyyy')), 'dd'))
											 then 0
											 when
											 trunc(am_stato_occupaz.datcalcoloanzianita) >= data150 and 
											 (to_number(to_char(dataParCalcolo,'dd')) - to_number(to_char(am_stato_occupaz.datcalcoloanzianita,'dd')) + 1) <>
											 to_number(to_char(LAST_DAY(to_date('01/' || to_char(dataParCalcolo, 'mm') || '/' || to_char(dataParCalcolo, 'yyyy'), 'dd/mm/yyyy')), 'dd'))
											 then 1
											 when trunc(am_stato_occupaz.datcalcoloanzianita) < data150 and
											 (to_number(to_char(dataParCalcolo,'dd')) - to_number(to_char(am_stato_occupaz.datcalcoloanzianita,'dd')) + 1) < 16 
											 then 1 
											 when trunc(am_stato_occupaz.datcalcoloanzianita) < data150 and
											 (to_number(to_char(dataParCalcolo,'dd')) - to_number(to_char(am_stato_occupaz.datcalcoloanzianita,'dd')) + 1) >= 15
											 then 0
								     end)
				 				  end)))
	             when de_stato_occupaz_ragg.CODSTATOOCCUPAZRAGG not in ('D','I')
	             then
	                  0
	         end
	         mesi_anz,
			 (case 
				when de_stato_occupaz_ragg.CODSTATOOCCUPAZRAGG in ('D','I')
					then
						(case 
							when ((to_char(dataParCalcolo,'yyyy') 
								  - to_char(am_stato_occupaz.datcalcoloanzianita,'yyyy')) * 12
								  + to_char(dataParCalcolo,'mm') 
								  - to_char(am_stato_occupaz.datcalcoloanzianita,'mm') + 1) > 1
							then	  
								   (case when
									 trunc(am_stato_occupaz.datcalcoloanzianita) >= (select trunc(dat150) from ts_generale) 
										then mod(
										((case 
											  when to_number(to_char(LAST_DAY(to_date('01/' || to_char(dataParCalcolo, 'mm') || '/' || to_char(dataParCalcolo, 'yyyy'), 'dd/mm/yyyy')), 'dd')) =
												   to_number(to_char(dataParCalcolo, 'dd'))
											  then 30
											  else to_number(to_char(dataParCalcolo, 'dd'))
											end) + 
											(30 - 
												(case
												  when to_number(to_char(am_stato_occupaz.datcalcoloanzianita, 'dd')) < 31 
												  then to_number(to_char(am_stato_occupaz.datcalcoloanzianita, 'dd'))
												  else 30
												end) + 1
											  )
										), 30)
									  else 0
									end)
							when
							  ((to_char(dataParCalcolo,'yyyy') 
								- to_char(am_stato_occupaz.datcalcoloanzianita,'yyyy')) * 12
								+ to_char(dataParCalcolo,'mm') 
								- to_char(am_stato_occupaz.datcalcoloanzianita,'mm') + 1) = 1
								then
									(case when
										trunc(am_stato_occupaz.datcalcoloanzianita) >= (select trunc(dat150) from ts_generale) 
										  and (to_number(to_char(dataParCalcolo,'dd')) - to_number(to_char(am_stato_occupaz.datcalcoloanzianita,'dd')) + 1) =
											   to_number(to_char(LAST_DAY(to_date('01/' || to_char(dataParCalcolo, 'mm') || '/' || to_char(dataParCalcolo, 'yyyy'), 'dd/mm/yyyy')), 'dd'))
										then 0
										when
										  trunc(am_stato_occupaz.datcalcoloanzianita) >= (select trunc(dat150) from ts_generale) 
										  and (to_number(to_char(dataParCalcolo,'dd')) - to_number(to_char(am_stato_occupaz.datcalcoloanzianita,'dd')) + 1) <>
											   to_number(to_char(LAST_DAY(to_date('01/' || to_char(dataParCalcolo, 'mm') || '/' || to_char(dataParCalcolo, 'yyyy'), 'dd/mm/yyyy')), 'dd'))
										then (to_number(to_char(dataParCalcolo,'dd')) - to_number(to_char(am_stato_occupaz.datcalcoloanzianita,'dd')) + 1)
										else 0
									end)
							else 0
						end)			  	
				 when de_stato_occupaz_ragg.CODSTATOOCCUPAZRAGG not in ('D','I')
				 then 0
			end)
			giorni_anz,
			  to_number(substr(PG_MOVIMENTI.MesiRischioDisoccupazione(cdnParLav, am_stato_occupaz.datcalcoloanzianita), 1,
				   instr(PG_MOVIMENTI.MesiRischioDisoccupazione(cdnParLav, am_stato_occupaz.datcalcoloanzianita), '-', 1)-1)) mesi_rischio_disocc,
			 PG_MOVIMENTI.MesiRischioDisoccupazione(cdnParLav, am_stato_occupaz.datcalcoloanzianita) mesi_rischio_disocc_completo,
	         to_number(am_stato_occupaz.NUMANZIANITAPREC297) mesi_anz_prec,
	         to_number(am_stato_occupaz.NUMMESISOSP) NUMMESISOSPPREC,
	         to_char(am_stato_occupaz.datcalcoloanzianita,'DD/MM/YYYY') datcalcoloanzianita,
	         to_char(am_stato_occupaz.datcalcolomesisosp,'DD/MM/YYYY') datcalcolomesisosp
		into codVarStatoOccupaz, codVarStatoOccupazRagg, codVarCat181, datVarInizio, datVarFine, datVarAnzDisocc, numVarMesiSospFornero, mesiSospFornero2014Comp,
			 numVarMesiSosp, numVarMesiAnz, ggResiduiAnzianita, numVarMesiRischioDisocc, numVarMesiRischioDisoccComp, numVarMesiAnzPrec, numVarMesiSospPrec, 
			 datVarCalcoloAnzianita, datVarCalcoloSosp
	    FROM am_stato_occupaz
	         LEFT JOIN de_stato_atto ON (am_stato_occupaz.codstatoatto = de_stato_atto.codstatoatto)
	         INNER JOIN de_stato_occupaz ON (am_stato_occupaz.codstatooccupaz = de_stato_occupaz.codstatooccupaz)
	         INNER JOIN de_stato_occupaz_ragg ON (de_stato_occupaz.codstatooccupazragg = de_stato_occupaz_ragg.codstatooccupazragg)
	         INNER JOIN an_lav_storia_inf inf ON (am_stato_occupaz.cdnlavoratore = inf.cdnlavoratore)
	    		 left JOIN de_cpi ON (de_cpi.codcpi = inf.codcpitit)
	   WHERE trunc(am_stato_occupaz.DATINIZIO) <= trunc(dataParCalcolo) and
	   		 (am_stato_occupaz.datfine IS NULL or trunc(am_stato_occupaz.datfine) >= trunc(dataParCalcolo))
	         AND am_stato_occupaz.cdnlavoratore = cdnParLav
	         AND inf.datfine IS NULL;

		if (numVarMesiAnz is null) then
		   numVarMesiAnz := 0;
		end if;
		
		if (ggResiduiAnzianita is null) then
		   ggResiduiAnzianita := 0;
		end if;

		if (numVarMesiAnzPrec is null) then
		   numVarMesiAnzPrec := 0;
		end if;

		if (numVarMesiSosp is null) then
		   numVarMesiSosp := 0;
		end if;
		
		if (numVarMesiSospFornero is null) then
		   numVarMesiSospFornero := 0;
		end if;

		if (numVarMesiSospPrec is null) then
		   numVarMesiSospPrec := 0;
		end if;
		
		if (numVarMesiRischioDisocc is null) then
		   numVarMesiRischioDisocc := 0;
		end if;
		
		if (numVarMesiRischioDisoccComp is not null) then
			numGGRestantiSosp := to_number(substr(numVarMesiRischioDisoccComp, instr(numVarMesiRischioDisoccComp, '-', 1, 1)+1));
		end if;
		
		if (mesiSospFornero2014Comp is not null) then
			numGGRestantiSosp := numGGRestantiSosp + to_number(substr(mesiSospFornero2014Comp, instr(mesiSospFornero2014Comp, '-', 1, 1)+1));
		
		end if;
		
		mesiAggiuntivi := trunc(numGGRestantiSosp/30);
		meseDiffAnzianitaGiorni := 0;
		if (ggResiduiAnzianita >= mod(numGGRestantiSosp,30)) then
			ggResiduiAnzianita := ggResiduiAnzianita - mod(numGGRestantiSosp,30);
		else
			if (mod(numGGRestantiSosp,30) > 0) then
				ggResiduiAnzianita := ggResiduiAnzianita + (30 - (mod(numGGRestantiSosp,30)));
				meseDiffAnzianitaGiorni := 1;
			end if;
		end if;
		
		numVarMesiTotAnz := numVarMesiAnzPrec + numVarMesiAnz;
		numVarMesiTotAnz := numVarMesiTotAnz - (numVarMesiSosp + numVarMesiSospFornero + numVarMesiSospPrec + numVarMesiRischioDisocc + mesiAggiuntivi + meseDiffAnzianitaGiorni);

		if (maggioreDiUno(dataVarNascita, 15, dataParCalcolo) and minoreDiUno(dataVarNascita, 18, dataParCalcolo) and flgVarObbScol = 'S') then
			catVar181:= 'A';

		else
			if (maggioreDiUno(dataVarNascita, 18, dataParCalcolo) and ((minoreDiUno(dataVarNascita, 30, dataParCalcolo) and flgVarLaurea = 'S') or minoreDiUno(dataVarNascita, 26, dataParCalcolo))) then
			   catVar181:= 'G';
			end if;
		end if;

		if (codVarStatoOccupazRagg is not null and
		   ((numVarMesiTotAnz > 12) or (numVarMesiTotAnz > 6 and catVar181 is not null and catVar181 = 'G'))) then
			if (codVarStatoOccupazRagg = 'D') then
				resultVar := 'Disoccupato di lunga durata';
			else
				if (codVarStatoOccupazRagg = 'I') then
				 	resultVar := 'Inoccupato di lunga durata';
				end if;
			end if;
		end if;
	end if;
	return resultVar;
end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/DONNAINREINSERIMENTO.sql
************************************************************************************** */


CREATE OR REPLACE function donnaInReinserimento (mesiInattivita int, sessoParLav an_lavoratore.STRSESSO%type) return boolean
is
  anni int;
  res boolean;
begin
	 anni := trunc(mesiInattivita/12);
	 res := (anni >= 2 and sessoParLav = 'F');
	 return res;
end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/ENCRYPT.sql
************************************************************************************** */


create or replace function encrypt
(input_string varchar2, key_string varchar2)
return varchar2
IS

 encrypted_string    VARCHAR2(2048);
 l_units int;
 pad_input_string varchar2(2048);
BEGIN
 
  if(input_string is null) then
        return '';
  else
 
      IF LENGTH(input_string ) MOD 8 > 0 THEN
          l_units := TRUNC(LENGTH(input_string )/8) + 1;
          pad_input_string   := RPAD(input_string , l_units * 8);
		ELSE
			pad_input_string   := input_string;
        END IF;
        
        
     
     
        dbms_obfuscation_toolkit.DESEncrypt(
                       input_string => pad_input_string,
                       key_string => key_string,
                       encrypted_string => encrypted_string);
    
                       
          
      return rawtohex(UTL_RAW.CAST_TO_RAW(encrypted_string));
  END IF;
      
                
END;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/ESISTE_CATENA_APERTA.sql
************************************************************************************** */


CREATE OR REPLACE FUNCTION "ESISTE_CATENA_APERTA"
(
 in_PRG_MOVIMENTO IN am_movimento.prgmovimento%TYPE,
 in_PRG_MOVIMENTO_SUC IN am_movimento.prgmovimento%TYPE

) Return number As

  esiste_catena  number := 0;
  prgsucc  am_movimento.prgmovimento%TYPE;
  prgmov am_movimento.prgmovimento%TYPE;
  ultimo_mov am_movimento%rowtype;
Begin
  prgmov := in_PRG_MOVIMENTO;
  prgsucc := in_PRG_MOVIMENTO_SUC;

  --trovo l'ultimo anello della catena associato al prg in ingresso
  while prgsucc is not null
  loop
   select movsucc.prgmovimento,  movsucc.prgmovimentosucc
   into prgmov, prgsucc
   from am_movimento mov,  am_movimento movsucc
   where mov.prgmovimentosucc = movsucc.prgmovimento
   and mov.prgmovimento = prgmov;

  -- dbms_output.put_line('prgmov:'||prgmov || ' - ' ||'prgsucc:'||prgsucc );
  end loop;
  
  --verifico se l'ultimo movimento della catena  un movimento aperto alla data
  select mov.* 
  into ultimo_mov
  from am_movimento mov where mov.prgmovimento = prgmov;
   
  
  if ((ultimo_mov.Datfinemoveffettiva IS NULL OR ultimo_mov.Datfinemoveffettiva >= Sysdate)
      and ultimo_mov.datiniziomov <= Sysdate
      and ultimo_mov.codtipomov in ('AVV','TRA','PRO')
      and ultimo_mov.codstatoatto = 'PR') 
  then esiste_catena:=1;
  end if;       
  
  



  RETURN esiste_catena;
  
     
  EXCEPTION
    when NO_DATA_FOUND then
       --dbms_output.put_line('NO_DATA_FOUND');
       return -1;
  END ESISTE_CATENA_APERTA;
 
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/esiste_conigurazione.sql
************************************************************************************** */


CREATE OR REPLACE FUNCTION "ESISTE_CONFIGURAZIONE" 
(
  FUNZIONE IN NUMBER
, COMPONENTE  IN NUMBER
, ATTRIBUTO   in NUMBER
) Return Varchar2 As

  Valore  int := 0;
  Codice number(38);
Begin
If (funzione is not null and componente is null and attributo is null)
  then   select 1 into valore from dual
    where exists (select * from ts_mappa_config where cdnfunzione = funzione);
end if;
If (funzione is null and componente is not null and attributo is null)
  then   select 1 into valore from dual
    where exists (select * from ts_mappa_config where cdncomponente = componente);
end if;
If (funzione is null and componente is null and attributo is not null)
  then   select 1 into valore from dual
    where exists (select * from ts_mappa_config where prgattributo = attributo);
end if;
  RETURN Valore;
END ESISTE_CONFIGURAZIONE;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/function_MovimentoIniziale.sql
************************************************************************************** */


create or replace
function RECUPERA_PRGMOV_INIZIALE(prgMov IN AM_MOVIMENTO.PRGMOVIMENTO%TYPE) return AM_MOVIMENTO.Prgmovimento%TYPE
IS
 rowMov AM_MOVIMENTO%ROWTYPE;
 prgMovCiclo AM_MOVIMENTO.PRGMOVIMENTO%TYPE;
BEGIN
 -- Continuo a recuperare il record del movimento precedente fino ad arrivare al primo della catena
 prgMovCiclo := prgMov;
 LOOP

  IF prgMovCiclo IS NULL THEN
     EXIT;
  END IF;

  -- Recupero il record relativo al prgMovimento in ingresso
  SELECT MOV.*
  INTO rowMov
  FROM AM_MOVIMENTO MOV
  WHERE MOV.PRGMOVIMENTO = prgMovCiclo;

  -- Se non trovato, esco dal ciclo
  EXIT WHEN SQL%NOTFOUND;

  IF rowMov.PRGMOVIMENTOPREC is null THEN
   RETURN rowMov.Prgmovimento;
  END IF;

  -- Continuo a ciclare (passo al movimento precedente)
  prgMovCiclo := rowMov.PRGMOVIMENTOPREC;

 END LOOP;

 RETURN NULL;

END;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getAzioniPatto.sql
************************************************************************************** */


CREATE OR REPLACE function getAzioniPatto (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type) return varchar2
is
	
	CURSOR azioniPatto IS
          SELECT AZ.STRDESCRIZIONE, DE_ESITO.STRDESCRIZIONE ESITO, TO_CHAR(PER.DATADESIONEGG, 'DD/MM/YYYY') DATADESIONEGG
		  FROM OR_COLLOQUIO COLL 
		  INNER JOIN OR_PERCORSO_CONCORDATO PER ON (COLL.PRGCOLLOQUIO = PER.PRGCOLLOQUIO)
		  INNER JOIN DE_AZIONE AZ ON (PER.PRGAZIONI = AZ.PRGAZIONI)
		  INNER JOIN AM_LAV_PATTO_SCELTA SCELTA ON (PER.PRGPERCORSO = TO_NUMBER(SCELTA.STRCHIAVETABELLA) AND SCELTA.CODLSTTAB = 'OR_PER')
		  INNER JOIN DE_ESITO ON (DE_ESITO.CODESITO = PER.CODESITO)
		  WHERE SCELTA.PRGPATTOLAVORATORE = prgPattoPar;
		  
		  resultVar varchar2(32767) := '';

begin

	FOR azione IN azioniPatto loop
		
		resultVar:= resultVar || azione.STRDESCRIZIONE || ' - ' || azione.ESITO;
		if (azione.DATADESIONEGG is not null) then
			resultVar:= resultVar || ' - ' || azione.DATADESIONEGG;
		end if;
		resultVar:= resultVar || '<br>';
	end loop;
	
	return resultVar;
exception
	when others then
		return null;
	
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getCat181PerStampa.sql
************************************************************************************** */


CREATE OR REPLACE function getCat181perStampa (cdnParLav an_lavoratore.CDNLAVORATORE%type, dataParCalcolo an_lavoratore.DATNASC%type) return varchar2
is
flgVarLaurea de_titolo.FLGLAUREA%type;
flgVarObbScol am_obbligo_formativo.FLGOBBLIGOSCOLASTICO%type;
dataVarNascita an_lavoratore.DATNASC%type;
resultVar varchar2(1000) := '';
sessoVarLav an_lavoratore.STRSESSO%type;
mesiVarInattivita int;

begin

 SELECT DISTINCT nvl(obbfo.flgobbligoscolastico,'N') as flgobbligoscolastico,
 nvl(lau.flgLaurea, 'N') AS flgLaurea,
 lav.datnasc,
 lav.STRSESSO
 into flgVarObbScol, flgVarLaurea, dataVarNascita, sessoVarLav
 FROM an_lavoratore lav,
 am_obbligo_formativo obbfo, (SELECT DISTINCT detit.flglaurea,
         detit.strdescrizione AS destipotitolo,
         stu.CDNLAVORATORE
         FROM pr_studio stu, de_titolo detit
         WHERE stu.codtipotitolo      = detit.codtitolo(+)
         AND UPPER (stu.codmonostato) = 'C'
         AND UPPER (detit.flglaurea)  LIKE ('S')
         AND stu.cdnlavoratore        = cdnParLav
         AND rownum = 1) lau
  WHERE lav.cdnlavoratore    = obbfo.cdnlavoratore(+)
   and lav.cdnLavoratore    = lau.cdnLavoratore(+)
   and lav.cdnlavoratore    = cdnParLav;

 if (maggioreDiUno(dataVarNascita, 15, dataParCalcolo) and minoreDiUno(dataVarNascita, 18, dataParCalcolo) and flgVarObbScol = 'S') then
  resultVar:= resultVar || '1 - Adolescenti';

 else
  if (maggioreDiUno(dataVarNascita, 18, dataParCalcolo) and ((minoreDiUno(dataVarNascita, 30, dataParCalcolo) and flgVarLaurea = 'S') or minoreDiUno(dataVarNascita, 26, dataParCalcolo))) then
     resultVar:= resultVar || '2 - Giovani';
  else
   if ((maggioreDiUno(dataVarNascita, 30, dataParCalcolo) and flgVarLaurea = 'S' ) or (maggioreDiUno(dataVarNascita, 26, dataParCalcolo) and flgVarLaurea = 'N')) then
     resultVar:= resultVar || '3 - Adulti';
   end if;
  end if;
 end if;

 mesiVarInattivita := mesiInattivita(cdnParLav);
 if (donnaInReinserimento(mesiVarInattivita,sessoVarLav)) then
    if (resultVar is null) or (resultVar is not null and (resultVar = '3 - Adulti')) then
       resultVar := '4 - Donna in reinserimento';
	end if;
 end if;

 return resultVar;
 
 exception
         when others then
    return '';
 
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getCat181.sql
************************************************************************************** */


CREATE OR REPLACE function getCat181 (cdnParLav an_lavoratore.CDNLAVORATORE%type, dataParCalcolo an_lavoratore.DATNASC%type) return varchar2
is
flgVarLaurea de_titolo.FLGLAUREA%type;
flgVarObbScol am_obbligo_formativo.FLGOBBLIGOSCOLASTICO%type;
dataVarNascita an_lavoratore.DATNASC%type;
resultVar varchar2(1000) := '';
sessoVarLav an_lavoratore.STRSESSO%type;
mesiVarInattivita int;
infoVarDisOccDurata varchar2(1000) := '';

begin

	SELECT DISTINCT nvl(obbfo.flgobbligoscolastico,'N') as flgobbligoscolastico,
	nvl(lau.flgLaurea, 'N') AS flgLaurea,
	lav.datnasc,
	lav.STRSESSO
	into flgVarObbScol, flgVarLaurea, dataVarNascita, sessoVarLav
	FROM an_lavoratore lav,
	am_obbligo_formativo obbfo, (SELECT DISTINCT detit.flglaurea,
									detit.strdescrizione AS destipotitolo,
									stu.CDNLAVORATORE
									FROM pr_studio stu, de_titolo detit
									WHERE stu.codtipotitolo      = detit.codtitolo(+)
									AND UPPER (stu.codmonostato) = 'C'
									AND UPPER (detit.flglaurea)  LIKE ('S')
									AND stu.cdnlavoratore        = cdnParLav
									AND rownum = 1) lau
		WHERE lav.cdnlavoratore    = obbfo.cdnlavoratore(+)
			and lav.cdnLavoratore    = lau.cdnLavoratore(+)
			and lav.cdnlavoratore    = cdnParLav;

	if (maggioreDiUno(dataVarNascita, 15, dataParCalcolo) and minoreDiUno(dataVarNascita, 18, dataParCalcolo) and flgVarObbScol = 'S') then
		resultVar:= resultVar || 'Adolescente';

	else
		if (maggioreDiUno(dataVarNascita, 18, dataParCalcolo) and ((minoreDiUno(dataVarNascita, 30, dataParCalcolo) and flgVarLaurea = 'S') or minoreDiUno(dataVarNascita, 26, dataParCalcolo))) then
		   resultVar:= resultVar || 'Giovane';
		end if;
	end if;

	mesiVarInattivita := mesiInattivita(cdnParLav);
	if (donnaInReinserimento(mesiVarInattivita,sessoVarLav)) then
	   if (resultVar is null) then
	   	  resultVar := resultVar || 'Donna in reinserimento lavorativo';
	   else
	      resultVar := resultVar || '/Donna in reinserimento lavorativo';
	   end if;
	end if;

	infoVarDisOccDurata := disInocLungaDurata(cdnParLav, dataVarNascita, dataParCalcolo, flgVarObbScol, flgVarLaurea);
	if (infoVarDisOccDurata is not null) then
	   if (resultVar is null) then
	   	  resultVar := resultVar || infoVarDisOccDurata;
	   else
	      resultVar := resultVar || '/' || infoVarDisOccDurata;
	   end if;
	end if;

	return resultVar;
	
	exception
         when others then
			 return 'condizione non calcolabile';
	
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getEsisteProgrammaAllaData.sql
************************************************************************************** */


CREATE OR REPLACE function getEsisteProgrammaAllaData (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type, listaProgramma varchar2, dataIn date) return int
is
	
	CURSOR programmi IS
          select distinct de_servizio.codmonoprogramma
			from am_patto_lavoratore 
			inner join or_colloquio coll on (am_patto_lavoratore.cdnlavoratore = coll.cdnlavoratore)
			inner join or_percorso_concordato on (coll.prgcolloquio = or_percorso_concordato.prgcolloquio)
			inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE
				and to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso and am_lav_patto_scelta.CODLSTTAB = 'OR_PER')
			inner join de_servizio on (coll.codservizio = de_servizio.codservizio)
			where (am_patto_lavoratore.PRGPATTOLAVORATORE = prgPattoPar)
			and (trunc(dataIn) >= trunc(coll.datcolloquio)) 
			and (coll.datfineprogramma is null or trunc(dataIn) <= trunc(coll.datfineprogramma));
	
	p_codprogramma varchar2(10);
			
begin

	FOR programma IN programmi loop
		p_codprogramma := '''' || programma.codmonoprogramma || '''';
		if (instr(listaProgramma, p_codprogramma) > 0) then
			return 1;
		end if;
		
	end loop;
	
	return 0;
exception
	when others then
		return 0;
	
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getEsisteProgrammaPattoAperti.sql
************************************************************************************** */


CREATE OR REPLACE function getEsisteProgrammaPattoAperti (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type, listaProgramma varchar2) return int
is
	
	CURSOR programmi IS
          select distinct de_servizio.codmonoprogramma
			from am_patto_lavoratore 
			inner join or_colloquio coll on (am_patto_lavoratore.cdnlavoratore = coll.cdnlavoratore)
			inner join or_percorso_concordato on (coll.prgcolloquio = or_percorso_concordato.prgcolloquio)
			inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE
				and to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso and am_lav_patto_scelta.CODLSTTAB = 'OR_PER')
			inner join de_servizio on (coll.codservizio = de_servizio.codservizio)
			where am_patto_lavoratore.PRGPATTOLAVORATORE = prgPattoPar and coll.datfineprogramma is null;
	
	p_codprogramma varchar2(10);
			
begin

	FOR programma IN programmi loop
		p_codprogramma := '''' || programma.codmonoprogramma || '''';
		if (instr(listaProgramma, p_codprogramma) > 0) then
			return 1;
		end if;
		
	end loop;
	
	return 0;
exception
	when others then
		return 0;
	
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getEsisteProgrammaPatto.sql
************************************************************************************** */


CREATE OR REPLACE function getEsisteProgrammaPatto (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type, listaProgramma varchar2) return int
is
	
	CURSOR programmi IS
          select distinct de_servizio.codmonoprogramma
			from am_patto_lavoratore 
			inner join or_colloquio coll on (am_patto_lavoratore.cdnlavoratore = coll.cdnlavoratore)
			inner join or_percorso_concordato on (coll.prgcolloquio = or_percorso_concordato.prgcolloquio)
			inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE
				and to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso and am_lav_patto_scelta.CODLSTTAB = 'OR_PER')
			inner join de_servizio on (coll.codservizio = de_servizio.codservizio)
			where am_patto_lavoratore.PRGPATTOLAVORATORE = prgPattoPar;
	
	p_codprogramma varchar2(10);
			
begin

	FOR programma IN programmi loop
		p_codprogramma := '''' || programma.codmonoprogramma || '''';
		if (instr(listaProgramma, p_codprogramma) > 0) then
			return 1;
		end if;
		
	end loop;
	
	return 0;
exception
	when others then
		return 0;
	
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/GETINFODOCUMENTIPROT.sql
************************************************************************************** */


CREATE OR REPLACE 
FUNCTION getinfodocumentiprot (datacalcolo IN VARCHAR2)
   RETURN NUMBER
IS
BEGIN
   DELETE FROM am_info_documenti_tmp;

   INSERT INTO am_info_documenti_tmp
               (numprotocollo, dataprotocollazione, dataatto, lavoratore,
                azienda, tipodocumento, ingresso_uscita, riferimento,
                enteacqril)
      SELECT am_documento.numprotocollo, am_documento.datprotocollo,
             am_documento.datinizio,
             DECODE (NVL (am_documento.cdnlavoratore, 0),
                     0, NULL,
                        an_lavoratore.strcognome
                     || '-'
                     || an_lavoratore.strnome
                     || '-'
                     || an_lavoratore.strcodicefiscale
                    ),
             DECODE (NVL (am_documento.prgazienda, 0),
                     0, NULL,
                        an_azienda.strragionesociale
                     || '-'
                     || an_azienda.strcodicefiscale
                    ),
             de_doc_tipo.strdescrizione,
             DECODE (am_documento.codmonoio,
                     'I', 'DOCUMENTO DI INPUT',
                     'O', 'DOCUMENTO DI OUTPUT',
                     NULL
                    ),
             de_cpi.strdescrizione, am_documento.strenterilascio
        FROM am_documento,
             de_doc_tipo,
             de_cpi,
             an_lavoratore,
             an_azienda,
             an_unita_azienda
       WHERE am_documento.codcpi = de_cpi.codcpi(+)
         AND am_documento.cdnlavoratore = an_lavoratore.cdnlavoratore(+)
         AND am_documento.prgazienda = an_azienda.prgazienda(+)
         AND am_documento.prgunita = an_unita_azienda.prgunita(+)
         AND am_documento.prgazienda = an_unita_azienda.prgazienda(+)
         AND am_documento.codtipodocumento = de_doc_tipo.codtipodocumento
         AND am_documento.codstatoatto = 'PR'
         AND am_documento.numprotocollo IS NOT NULL
         AND TRUNC (am_documento.datprotocollo) =
                                           TO_DATE (datacalcolo, 'DD/MM/YYYY');

   COMMIT;
   RETURN 0;
EXCEPTION
   WHEN OTHERS
   THEN
      ROLLBACK;
      RETURN -1;
END;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getNumConfig.sql
************************************************************************************** */


-----
--- Funzione che ottiene il num configurazione dato in input il codtipoconfig
-----

CREATE OR REPLACE function getNumConfig(p_codtipoconfig in de_tipo_config.codtipoconfig%type) return number is

numconfig number;

begin
  SELECT
    ts_config_loc.num as NUMVALORECONFIG
    into numconfig
  FROM
    ts_config_loc
    INNER JOIN de_tipo_config ON ( ts_config_loc.codtipoconfig = de_tipo_config.codtipoconfig )
  WHERE
    ts_config_loc.strcodrif = (
        SELECT
            ts_generale.codprovinciasil
        FROM
            ts_generale
        WHERE
            prggenerale = 1
    )
    AND de_tipo_config.codtipoconfig = p_codtipoconfig
    AND trunc(sysdate) BETWEEN trunc(de_tipo_config.datinizioval) AND trunc(de_tipo_config.datfineval);
	return numconfig;
exception
  when others then
    numconfig := 0;
    return numconfig;
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getProgrammiPattoAperti.sql
************************************************************************************** */


CREATE OR REPLACE function getProgrammiPattoAperti (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type) return varchar2
is
	
	CURSOR programmi IS
          select distinct de_servizio.codmonoprogramma
			from am_patto_lavoratore 
			inner join or_colloquio coll on (am_patto_lavoratore.cdnlavoratore = coll.cdnlavoratore)
			inner join or_percorso_concordato on (coll.prgcolloquio = or_percorso_concordato.prgcolloquio)
			inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE
				and to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso and am_lav_patto_scelta.CODLSTTAB = 'OR_PER')
			inner join de_servizio on (coll.codservizio = de_servizio.codservizio)
			where am_patto_lavoratore.PRGPATTOLAVORATORE = prgPattoPar and coll.datfineprogramma is null;
		  
		  resultVar varchar2(32767) := '';
		  indice int := 1;

begin
	resultVar := '(''';

	FOR programma IN programmi loop
		
		if (indice = 1) then
			resultVar:= resultVar || programma.codmonoprogramma || '''';
		else
			resultVar:= resultVar || ', ''' || programma.codmonoprogramma || '''';
		end if;
		
		indice := indice + 1;
	end loop;
	
	resultVar := resultVar || ')';
	
	return resultVar;
exception
	when others then
		return null;
	
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getProgrammiPattoPercorsoLav.sql
************************************************************************************** */


create or replace function getProgrammiPattoPercorsoLav (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type) return varchar2
is
	
	CURSOR programmi IS
      with prog as (
        select distinct de_servizio.strDescrizione, or_colloquio.datcolloquio, or_colloquio.datFineProgramma
        FROM or_colloquio 
        INNER JOIN de_servizio on or_colloquio.codservizio = de_servizio.codservizio
        INNER JOIN or_percorso_concordato on or_percorso_concordato.prgColloquio = or_colloquio.prgColloquio
        INNER JOIN am_lav_patto_scelta on 
          (to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso 
          and am_lav_patto_scelta.codlsttab = 'OR_PER')
        WHERE de_servizio.codMonoProgramma is not null
        AND am_lav_patto_scelta.prgpattolavoratore = prgPattoPar
      )
      SELECT prog.strDescrizione || ' con data inizio ' || 
      to_char(prog.datcolloquio,'DD/MM/YYYY') || decode(prog.datFineProgramma, null, '', ' e data fine ' || 
      to_char(prog.datFineProgramma,'DD/MM/YYYY')) as progDesc
      from prog;      
		  
		  resultVar varchar2(32767) := '';
		  indice int := 1;

begin
	resultVar := '';

	FOR programma IN programmi loop
		
		if (indice = 1) then
			resultVar:= resultVar || programma.progDesc;
		else
			resultVar:= resultVar || ', ' || programma.progDesc;
		end if;
		
		indice := indice + 1;
	end loop;
	
	return resultVar;
exception
	when others then
		return null;
	
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/getProgrammiPatto.sql
************************************************************************************** */


CREATE OR REPLACE function getProgrammiPatto (prgPattoPar am_patto_lavoratore.prgpattolavoratore%type) return varchar2
is
	
	CURSOR programmi IS
          select distinct de_servizio.codmonoprogramma
			from am_patto_lavoratore 
			inner join or_colloquio coll on (am_patto_lavoratore.cdnlavoratore = coll.cdnlavoratore)
			inner join or_percorso_concordato on (coll.prgcolloquio = or_percorso_concordato.prgcolloquio)
			inner join am_lav_patto_scelta on (am_patto_lavoratore.PRGPATTOLAVORATORE = am_lav_patto_scelta.PRGPATTOLAVORATORE
				and to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso and am_lav_patto_scelta.CODLSTTAB = 'OR_PER')
			inner join de_servizio on (coll.codservizio = de_servizio.codservizio)
			where am_patto_lavoratore.PRGPATTOLAVORATORE = prgPattoPar;
		  
		  resultVar varchar2(32767) := '';
		  indice int := 1;

begin
	resultVar := '(''';

	FOR programma IN programmi loop
		
		if (indice = 1) then
			resultVar:= resultVar || programma.codmonoprogramma || '''';
		else
			resultVar:= resultVar || ', ''' || programma.codmonoprogramma || '''';
		end if;
		
		indice := indice + 1;
	end loop;
	
	resultVar := resultVar || ')';
	
	return resultVar;
exception
	when others then
		return null;
	
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/MAGGIOREDIUNO.sql
************************************************************************************** */


CREATE OR REPLACE function maggioreDiUno (dataVarNascita an_lavoratore.DATNASC%type, years int, dataParCalcolo an_lavoratore.DATNASC%type) return boolean
is
		ggVarNascita int;
		mmVarNascita int;
		aaVarNascita int;
		ggVarCalcolo int;
		mmVarCalcolo int;
		aaVarCalcolo int;

begin
	ggVarNascita := to_char(dataParCalcolo,'dd');
	mmVarNascita := to_char(dataParCalcolo,'mm');
	aaVarNascita := to_char(dataParCalcolo,'yyyy');

	ggVarCalcolo := to_char(dataVarNascita,'dd');
	mmVarCalcolo := to_char(dataVarNascita,'mm');
	aaVarCalcolo := to_char(dataVarNascita,'yyyy');

	if ((aaVarNascita - aaVarCalcolo) >= years) then
	    if ((aaVarNascita - aaVarCalcolo) = years) then
   	    	if ((mmVarNascita - mmVarCalcolo) > 0) then
				return true;
			else
				if ((mmVarNascita - mmVarCalcolo) = 0) then
					 	if ((ggVarNascita - ggVarCalcolo) >= 0) then
						   return true;
						end if;
				end if;
			end if;
		else
			return true;
		end if;
	end if;
	return false;
end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/MESIINATTIVITA.sql
************************************************************************************** */


CREATE OR REPLACE function mesiInattivita (cdnParLav an_lavoratore.CDNLAVORATORE%type) return int
is
  mesiVar int:=0;
  numVarMovimenti int;
begin
	 select count(*)
	 into numVarMovimenti
	 from am_movimento mov
	 WHERE mov.cdnlavoratore = cdnParLav and mov.CODTIPOMOV <> 'CES'
	 and mov.CODSTATOATTO = 'PR';

	 if (numVarMovimenti > 0) then
		 SELECT MIN(TRUNC (MONTHS_BETWEEN (SYSDATE,nvl(mov.datfinemovEffettiva,sysdate)))) as mesiInattivita
		 into mesiVar
	     FROM am_movimento mov
	     WHERE mov.cdnlavoratore = cdnParLav and mov.CODTIPOMOV <> 'CES'
		 and mov.CODSTATOATTO = 'PR';
	 end if;
	 return mesiVar;
end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/MESISOSPPRECFORNEROALLADATA.sql
************************************************************************************** */


CREATE OR REPLACE function MESISOSPPRECFORNEROALLADATA (cndLavPar an_lavoratore.CDNLAVORATORE%type,
														dataCalcoloMesiSosp am_stato_occupaz.datcalcolomesisosp%type,
														dataCalcolo am_stato_occupaz.datinizio%type) return number

is

dataFornero ts_generale.datFornero%type;
dataPrecFornero ts_generale.datFornero%type;
numMesiSospPrecFornero number := 0;

begin

	select trunc(datFornero) 
	into dataFornero
	from ts_generale;
	
	dataPrecFornero := dataFornero - 1;

	SELECT SUM (
		 --differenza mesi
		 (to_char(CASE
						WHEN (CASE 
								WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
									THEN dataCalcolo
								ELSE NVL(occ.datfine, dataPrecFornero)
							  END) <= dataCalcoloMesiSosp
							 THEN 
							 (CASE
								WHEN dataCalcoloMesiSosp <= dataPrecFornero
								THEN dataCalcoloMesiSosp
								ELSE dataPrecFornero
							  END)
						WHEN (CASE 
								WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
									THEN dataCalcolo
								ELSE NVL(occ.datfine, dataPrecFornero)
							  END) >= dataPrecFornero
								THEN dataPrecFornero
						   WHEN NVL(occ.datfine, dataPrecFornero-1) 
									< dataPrecFornero
							 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
					END
				 ,'yyyy')
		 - to_char(CASE
						  WHEN occ.datInizio <= dataCalcoloMesiSosp
							 THEN 
								(CASE
									WHEN dataCalcoloMesiSosp <= dataPrecFornero
									THEN dataCalcoloMesiSosp
									ELSE dataPrecFornero
								  END)
						WHEN occ.datInizio > dataCalcoloMesiSosp
							 THEN occ.datInizio
					  END
				   ,'yyyy')) * 12
		 +
		 to_char(CASE
						WHEN (CASE 
								WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
									THEN dataCalcolo
								ELSE NVL(occ.datfine, dataPrecFornero)
							  END) <= dataCalcoloMesiSosp
							 THEN 
							 (CASE
								WHEN dataCalcoloMesiSosp <= dataPrecFornero
								THEN dataCalcoloMesiSosp
								ELSE dataPrecFornero
							  END)
						WHEN (CASE 
								WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
									THEN dataCalcolo
								ELSE NVL(occ.datfine, dataPrecFornero)
							  END) >= dataPrecFornero
								THEN dataPrecFornero
						   WHEN NVL(occ.datfine, dataPrecFornero-1) 
									< dataPrecFornero
							 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
					END
				 ,'mm')
		 -to_char(CASE
						  WHEN occ.datInizio <= dataCalcoloMesiSosp
							 THEN
								(CASE
									WHEN dataCalcoloMesiSosp <= dataPrecFornero
									THEN dataCalcoloMesiSosp
									ELSE dataPrecFornero
								 END)
						WHEN occ.datInizio > dataCalcoloMesiSosp
							 THEN occ.datInizio
					  END
				  ,'mm') + 1

			 --togliere eventuali mesi
			 + (case
					  when ((to_char(CASE
									WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) <= dataCalcoloMesiSosp
									 THEN 
									 (CASE
										WHEN dataCalcoloMesiSosp <= dataPrecFornero
										THEN dataCalcoloMesiSosp
										ELSE dataPrecFornero
									  END)
								WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) >= dataPrecFornero
										THEN dataPrecFornero
								   WHEN NVL(occ.datfine, dataPrecFornero-1) 
											< dataPrecFornero
									 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
								  END
								 ,'yyyy')
						   - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									 ,'yyyy')) * 12
						   +
						   to_char(CASE
									WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) <= dataCalcoloMesiSosp
									 THEN 
									 (CASE
										WHEN dataCalcoloMesiSosp <= dataPrecFornero
										THEN dataCalcoloMesiSosp
										ELSE dataPrecFornero
									  END)
								WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) >= dataPrecFornero
										THEN dataPrecFornero
								   WHEN NVL(occ.datfine, dataPrecFornero-1) 
											< dataPrecFornero
									 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
								  END
								  ,'mm')
						   - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									 ,'mm') + 1) > 1

					  then

						   -(case when
									  (30 - to_char(CASE
													  WHEN occ.datInizio <= dataCalcoloMesiSosp
														  THEN
															(CASE
																WHEN dataCalcoloMesiSosp <= dataPrecFornero
																THEN dataCalcoloMesiSosp
																ELSE dataPrecFornero
															 END)
														WHEN occ.datInizio > dataCalcoloMesiSosp
														  THEN occ.datInizio
													  END
													,'dd') + 1) < 16
								  then 1
								  when
									  (30 - to_char(CASE
													  WHEN occ.datInizio <= dataCalcoloMesiSosp
														  THEN
															(CASE
																WHEN dataCalcoloMesiSosp <= dataPrecFornero
																THEN dataCalcoloMesiSosp
																ELSE dataPrecFornero
															 END)
														WHEN occ.datInizio > dataCalcoloMesiSosp
														  THEN occ.datInizio
													  END
													,'dd') + 1) >= 15
								  then 0
							  end)

							-(case when
									  to_char(CASE
												WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) <= dataCalcoloMesiSosp
												 THEN 
												 (CASE
													WHEN dataCalcoloMesiSosp <= dataPrecFornero
													THEN dataCalcoloMesiSosp
													ELSE dataPrecFornero
												  END)
											WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) >= dataPrecFornero
													THEN dataPrecFornero
											   WHEN NVL(occ.datfine, dataPrecFornero-1) 
														< dataPrecFornero
												 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
											  END
											  ,'dd') < 16
								  then 1
								  when
									  to_char(CASE
												WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) <= dataCalcoloMesiSosp
												 THEN 
												 (CASE
													WHEN dataCalcoloMesiSosp <= dataPrecFornero
													THEN dataCalcoloMesiSosp
													ELSE dataPrecFornero
												  END)
											WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) >= dataPrecFornero
													THEN dataPrecFornero
											   WHEN NVL(occ.datfine, dataPrecFornero-1) 
														< dataPrecFornero
												 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
											  END
											  ,'dd') >= 15
								  then 0
							  end)

					 when
						  ((to_char(CASE
									WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) <= dataCalcoloMesiSosp
									 THEN 
									 (CASE
										WHEN dataCalcoloMesiSosp <= dataPrecFornero
										THEN dataCalcoloMesiSosp
										ELSE dataPrecFornero
									  END)
								WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) >= dataPrecFornero
										THEN dataPrecFornero
								   WHEN NVL(occ.datfine, dataPrecFornero-1) 
											< dataPrecFornero
									 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
								  END
								,'yyyy')
						  - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									,'yyyy')) * 12
						  +
						  to_char(CASE
									WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) <= dataCalcoloMesiSosp
									 THEN 
									 (CASE
										WHEN dataCalcoloMesiSosp <= dataPrecFornero
										THEN dataCalcoloMesiSosp
										ELSE dataPrecFornero
									  END)
								WHEN (CASE 
										WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
											THEN dataCalcolo
										ELSE NVL(occ.datfine, dataPrecFornero)
									  END) >= dataPrecFornero
										THEN dataPrecFornero
								   WHEN NVL(occ.datfine, dataPrecFornero-1) 
											< dataPrecFornero
									 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
								  END
								  ,'mm')
						  - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									,'mm') + 1) = 1
					 then
						 -(case when
									(to_char(CASE
												WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) <= dataCalcoloMesiSosp
												 THEN 
												 (CASE
													WHEN dataCalcoloMesiSosp <= dataPrecFornero
													THEN dataCalcoloMesiSosp
													ELSE dataPrecFornero
												  END)
											WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) >= dataPrecFornero
													THEN dataPrecFornero
											   WHEN NVL(occ.datfine, dataPrecFornero-1) 
														< dataPrecFornero
												 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
											  END
											 ,'dd')
									 - to_char(CASE
											  WHEN occ.datInizio <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN occ.datInizio > dataCalcoloMesiSosp
												  THEN occ.datInizio
											  END
											   ,'dd') + 1) < 16
								then 1
								when
									 (to_char(CASE
												WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) <= dataCalcoloMesiSosp
												 THEN 
												 (CASE
													WHEN dataCalcoloMesiSosp <= dataPrecFornero
													THEN dataCalcoloMesiSosp
													ELSE dataPrecFornero
												  END)
											WHEN (CASE 
													WHEN NVL(occ.datfine, dataPrecFornero) > TRUNC(dataCalcolo) 
														THEN dataCalcolo
													ELSE NVL(occ.datfine, dataPrecFornero)
												  END) >= dataPrecFornero
													THEN dataPrecFornero
											   WHEN NVL(occ.datfine, dataPrecFornero-1) 
														< dataPrecFornero
												 THEN (case WHEN NVL(occ.datfine, dataPrecFornero-1) > TRUNC(dataCalcolo) then dataCalcolo else NVL(occ.datfine, dataPrecFornero-1) end)
											  END
											  ,'dd')
									 - to_char(CASE
											  WHEN occ.datInizio <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN occ.datInizio > dataCalcoloMesiSosp
												  THEN occ.datInizio
											  END
											   ,'dd') + 1) >= 15
								then 0
						  end)
				end))
				into numMesiSospPrecFornero
						   FROM am_stato_occupaz occ
						   WHERE occ.cdnlavoratore = cndLavPar
								 AND occ.codstatooccupaz = 'B1'
								 AND (occ.datinizio >= occ.datcalcolomesisosp or
									  (occ.datcalcolomesisosp >= occ.datinizio AND
									   occ.datcalcolomesisosp <= nvl (occ.datfine,dataPrecFornero))
									  )
								 AND trunc(occ.datinizio) < dataFornero
								 AND (dataCalcolo is null or trunc(occ.datinizio) <= trunc(dataCalcolo));
	
	return numMesiSospPrecFornero;
													 
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/MESISOSPPRECFORNERO.sql
************************************************************************************** */


CREATE OR REPLACE function MESISOSPPRECFORNERO (cndLavPar an_lavoratore.CDNLAVORATORE%type,
											    dataCalcoloMesiSosp am_stato_occupaz.datcalcolomesisosp%type,
											    dataInizioStatoOcc am_stato_occupaz.datinizio%type) return number

is

dataFornero ts_generale.datFornero%type;
dataPrecFornero ts_generale.datFornero%type;
numMesiSospPrecFornero number := 0;

begin

	select trunc(datFornero) 
	into dataFornero
	from ts_generale;
	
	dataPrecFornero := dataFornero - 1;

	SELECT SUM (
		 --differenza mesi
		 (to_char(CASE
						WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
							 THEN 
							 (CASE
								WHEN dataCalcoloMesiSosp <= dataPrecFornero
								THEN dataCalcoloMesiSosp
								ELSE dataPrecFornero
							  END)
						WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
							 THEN dataPrecFornero
						   WHEN occ.datfine < dataPrecFornero
							 THEN occ.datfine
					END
				 ,'yyyy')
		 - to_char(CASE
						  WHEN occ.datInizio <= dataCalcoloMesiSosp
							 THEN 
								(CASE
									WHEN dataCalcoloMesiSosp <= dataPrecFornero
									THEN dataCalcoloMesiSosp
									ELSE dataPrecFornero
								  END)
						WHEN occ.datInizio > dataCalcoloMesiSosp
							 THEN occ.datInizio
					  END
				   ,'yyyy')) * 12
		 +
		 to_char(CASE
						WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
							 THEN
								(CASE
									WHEN dataCalcoloMesiSosp <= dataPrecFornero
									THEN dataCalcoloMesiSosp
									ELSE dataPrecFornero
								 END)
						WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
							 THEN dataPrecFornero
						   WHEN occ.datfine < dataPrecFornero
							 THEN occ.datfine
					END
				 ,'mm')
		 -to_char(CASE
						  WHEN occ.datInizio <= dataCalcoloMesiSosp
							 THEN
								(CASE
									WHEN dataCalcoloMesiSosp <= dataPrecFornero
									THEN dataCalcoloMesiSosp
									ELSE dataPrecFornero
								 END)
						WHEN occ.datInizio > dataCalcoloMesiSosp
							 THEN occ.datInizio
					  END
				  ,'mm') + 1

			 --togliere eventuali mesi
			 + (case
					  when ((to_char(CASE
									WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
									  THEN
										(CASE
											WHEN dataCalcoloMesiSosp <= dataPrecFornero
											THEN dataCalcoloMesiSosp
											ELSE dataPrecFornero
										 END)
									WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
									  THEN dataPrecFornero
									WHEN occ.datfine < dataPrecFornero
									  THEN occ.datfine
								  END
								 ,'yyyy')
						   - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									 ,'yyyy')) * 12
						   +
						   to_char(CASE
									WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
									  THEN
										(CASE
											WHEN dataCalcoloMesiSosp <= dataPrecFornero
											THEN dataCalcoloMesiSosp
											ELSE dataPrecFornero
										 END)
									WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
									  THEN dataPrecFornero
									WHEN occ.datfine < dataPrecFornero
									  THEN occ.datfine
								  END
								  ,'mm')
						   - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									 ,'mm') + 1) > 1

					  then

						   -(case when
									  (30 - to_char(CASE
													  WHEN occ.datInizio <= dataCalcoloMesiSosp
														  THEN
															(CASE
																WHEN dataCalcoloMesiSosp <= dataPrecFornero
																THEN dataCalcoloMesiSosp
																ELSE dataPrecFornero
															 END)
														WHEN occ.datInizio > dataCalcoloMesiSosp
														  THEN occ.datInizio
													  END
													,'dd') + 1) < 16
								  then 1
								  when
									  (30 - to_char(CASE
													  WHEN occ.datInizio <= dataCalcoloMesiSosp
														  THEN
															(CASE
																WHEN dataCalcoloMesiSosp <= dataPrecFornero
																THEN dataCalcoloMesiSosp
																ELSE dataPrecFornero
															 END)
														WHEN occ.datInizio > dataCalcoloMesiSosp
														  THEN occ.datInizio
													  END
													,'dd') + 1) >= 15
								  then 0
							  end)

							-(case when
									  to_char(CASE
												WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
												  THEN dataPrecFornero
												WHEN occ.datfine < dataPrecFornero
												  THEN occ.datfine
											  END
											  ,'dd') < 16
								  then 1
								  when
									  to_char(CASE
												WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
												  THEN dataPrecFornero
												WHEN occ.datfine < dataPrecFornero
												  THEN occ.datfine
											  END
											  ,'dd') >= 15
								  then 0
							  end)

					 when
						  ((to_char(CASE
									WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
									  THEN
										(CASE
											WHEN dataCalcoloMesiSosp <= dataPrecFornero
											THEN dataCalcoloMesiSosp
											ELSE dataPrecFornero
										 END)
									WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
									  THEN dataPrecFornero
									WHEN occ.datfine < dataPrecFornero
									  THEN occ.datfine
								  END
								,'yyyy')
						  - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									,'yyyy')) * 12
						  +
						  to_char(CASE
									WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
									  THEN
										(CASE
											WHEN dataCalcoloMesiSosp <= dataPrecFornero
											THEN dataCalcoloMesiSosp
											ELSE dataPrecFornero
										 END)
									WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
									  THEN dataPrecFornero
									WHEN occ.datfine < dataPrecFornero
									  THEN occ.datfine
								  END
								  ,'mm')
						  - to_char(CASE
									  WHEN occ.datInizio <= dataCalcoloMesiSosp
										  THEN
											(CASE
												WHEN dataCalcoloMesiSosp <= dataPrecFornero
												THEN dataCalcoloMesiSosp
												ELSE dataPrecFornero
											 END)
										WHEN occ.datInizio > dataCalcoloMesiSosp
										  THEN occ.datInizio
									  END
									,'mm') + 1) = 1
					 then
						 -(case when
									(to_char(CASE
												WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
												  THEN dataPrecFornero
												WHEN occ.datfine < dataPrecFornero
												  THEN occ.datfine
											  END
											 ,'dd')
									 - to_char(CASE
											  WHEN occ.datInizio <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN occ.datInizio > dataCalcoloMesiSosp
												  THEN occ.datInizio
											  END
											   ,'dd') + 1) < 16
								then 1
								when
									 (to_char(CASE
												WHEN NVL (occ.datfine, dataPrecFornero) <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN NVL (occ.datfine, dataPrecFornero) >= dataPrecFornero
												  THEN dataPrecFornero
												WHEN occ.datfine < dataPrecFornero
												  THEN occ.datfine
											  END
											  ,'dd')
									 - to_char(CASE
											  WHEN occ.datInizio <= dataCalcoloMesiSosp
												  THEN
													(CASE
														WHEN dataCalcoloMesiSosp <= dataPrecFornero
														THEN dataCalcoloMesiSosp
														ELSE dataPrecFornero
													 END)
												WHEN occ.datInizio > dataCalcoloMesiSosp
												  THEN occ.datInizio
											  END
											   ,'dd') + 1) >= 15
								then 0
						  end)
				end))
				into numMesiSospPrecFornero
						   FROM am_stato_occupaz occ
						   WHERE occ.cdnlavoratore = cndLavPar
								 AND occ.codstatooccupaz = 'B1'
								 AND (occ.datinizio >= occ.datcalcolomesisosp or
									  (occ.datcalcolomesisosp >= occ.datinizio AND
									   occ.datcalcolomesisosp <= nvl (occ.datfine,dataPrecFornero))
									  )
								 AND trunc(occ.datinizio) < dataFornero
								 AND (dataInizioStatoOcc is null or trunc(occ.datinizio) <= trunc(dataInizioStatoOcc));
	
	return numMesiSospPrecFornero;
													 
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/MINOREDIUNO.sql
************************************************************************************** */


CREATE OR REPLACE function minoreDiUno (dataVarNascita an_lavoratore.DATNASC%type, years int, dataParCalcolo an_lavoratore.DATNASC%type) return boolean
is
		ggVarNascita int;
		mmVarNascita int;
		aaVarNascita int;
		ggVarCalcolo int;
		mmVarCalcolo int;
		aaVarCalcolo int;

begin
	ggVarNascita := to_char(dataParCalcolo,'dd');
	mmVarNascita := to_char(dataParCalcolo,'mm');
	aaVarNascita := to_char(dataParCalcolo,'yyyy');

	ggVarCalcolo := to_char(dataVarNascita,'dd');
	mmVarCalcolo := to_char(dataVarNascita,'mm');
	aaVarCalcolo := to_char(dataVarNascita,'yyyy');

	if ((aaVarNascita - aaVarCalcolo) <= years) then
		if ((aaVarNascita - aaVarCalcolo) = years) then
			if ((mmVarNascita - mmVarCalcolo) < 0) then
				return true;
			else
				if ((mmVarNascita - mmVarCalcolo) = 0) then
					if ((ggVarNascita - ggVarCalcolo) < 0) then
						return true;
					end if;
				end if;
			end if;
		else
			return true;
		end if;
	end if;
	return false;
end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/movInizialeRettificato.sql
************************************************************************************** */


create or replace
function movInizialeRettificato(prgMov IN AM_MOVIMENTO.PRGMOVIMENTO%TYPE) return AM_MOVIMENTO.PRGMOVIMENTO%TYPE
IS
 rowMov AM_MOVIMENTO%ROWTYPE;
 prgMovCiclo AM_MOVIMENTO.PRGMOVIMENTO%TYPE;
BEGIN
 
 SELECT MOV.PRGMOVIMENTORETT
 INTO prgMovCiclo
 FROM AM_MOVIMENTO MOV
 WHERE MOV.PRGMOVIMENTO = prgMov;
 
 LOOP

  IF prgMovCiclo IS NULL THEN
     RETURN prgMov;
  END IF;

  -- Recupero il record relativo al prgMovimento rettificato
  SELECT MOV.*
  INTO rowMov
  FROM AM_MOVIMENTO MOV
  WHERE MOV.PRGMOVIMENTO = prgMovCiclo;

  -- Se non trovato, esco dal ciclo
  EXIT WHEN SQL%NOTFOUND;

  IF rowMov.PRGMOVIMENTORETT is null THEN
   RETURN rowMov.Prgmovimento;
  END IF;

  -- Continuo a ciclare (passo al movimento rettificato precedente della catena)
  prgMovCiclo := rowMov.PRGMOVIMENTORETT;

 END LOOP;

 RETURN NULL;

END;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/PROT_PROSPETTI_2_6_0.sql
************************************************************************************** */


create or replace procedure prot_prospetti_2_6_0 as


CURSOR richCursor IS
          select inf.prgprospettoinf, inf.prgazienda, inf.prgunita, inf.numannorifprospetto,
				 decode(inf.DATCONSEGNAPROSPETTO,NULL,trunc(sysdate),inf.DATCONSEGNAPROSPETTO) as datInizioVal
		  from cm_prospetto_inf inf
		  where inf.CODMONOPROV = 'S' and (inf.CODMONOSTATOPROSPETTO = 'S' or inf.CODMONOSTATOPROSPETTO = 'V')
	  			and inf.DATCONSEGNAPROSPETTO >= to_date('01/01/2009', 'dd/mm/yyyy');

nro number;
checkProsp number;
p_prgAzienda number;
p_prgunita number;
p_prg number;
p_datdatainizio varchar2(10);
parprgdoc number;
parprgdocblob number;
parprgdoccoll number;
p_numprotocollo number;
p_numannoprot number;
p_dataoraprot varchar2(100);
p_codCpi de_cpi.codcpi%type;
p_numAnnoRifProspetto cm_prospetto_inf.numannorifprospetto%type;
p_datInizioVal cm_prospetto_inf.datconsegnaprospetto%type;

      BEGIN
		  
   		  select de.codCpiCapoluogo codCpi
		  into p_codCpi
		  from ts_generale ts
		  inner join de_provincia de on (de.codProvincia = ts.codProvinciaSil);
     
           FOR rich IN richCursor
             LOOP
               p_prgAzienda := rich.prgazienda;
               p_prgunita := rich.prgunita;
               p_prg := rich.prgprospettoinf;
			   p_numAnnoRifProspetto := rich.numannorifprospetto;
			   p_datInizioVal := rich.datInizioVal;

               select(am_protocollo.numprotocollo + 1) numprotocollo,
               am_protocollo.numannoprot,
               TO_CHAR (SYSDATE, 'dd/mm/yyyy hh24:mi') AS dataoraprot
               into p_numprotocollo, p_numannoprot, p_dataoraprot
               from am_protocollo
               where numannoprot = 2009
               for update;

               checkProsp := pg_gestamm.insertdocumento(parprgdoc => parprgdoc,
                            parcodcpi => p_codCpi,
                            parcdnlav => '',
                            parprgazienda => p_prgAzienda,
                            parprgunita => p_prgunita,
                            parcodtipodoc => 'PINF',
                            parflgautocertif => '',
                            parstrdesc => 'Prospetto informativo ' || p_numAnnoRifProspetto,
                            parflgdocamm => '',
                            parflgdocidentifp => '',
                            pardatainizio => to_char(p_datInizioVal, 'dd/mm/yyyy'),
                            parstrnumdoc => '',
                            parenterilascio => '',
                            parcodmonoio => 'I',
                            pardataacqril => to_char(sysdate, 'dd/mm/yyyy'),
                            parcodmodalitaacqri => '',
                            parcodtipofile => '',
                            parstrnomedoc => '',
                            pardatafine => '',
                            parnumannoprot => p_numannoprot,
                            parnumprot => p_numprotocollo,
                            parstrnote => '',
                            parcdnutins => 200,
                            parcdnutmod => 200,
                            pardataprotocollazione => p_dataoraprot,
                            parcodstatoatto => 'PR',
                            parprgdocblob => parprgdocblob,
                            parpagina => 'CMProspDettPage',
                            parprgdoccoll => parprgdoccoll,
                            parstrchiavetabella => p_prg);

                 if (checkProsp = 0) then

                    update am_protocollo set
                    numprotocollo = p_numprotocollo,
                    numkloprotocollo = numkloprotocollo+1
                    where numannoprot = 2009;

                 end if;

             END LOOP;

       DECLARE
                CURSOR richCursorNP IS
                	select inf.prgprospettoinf, inf.prgazienda, inf.prgunita, inf.numannorifprospetto,
						   decode(inf.DATCONSEGNAPROSPETTO,NULL,trunc(sysdate),inf.DATCONSEGNAPROSPETTO) as datInizioVal
					from cm_prospetto_inf inf
					where inf.CODMONOSTATOPROSPETTO <> 'N' and 
	  					  ( inf.DATCONSEGNAPROSPETTO is null 
							or trunc(inf.DATCONSEGNAPROSPETTO) < to_date('01/01/2009', 'dd/mm/yyyy') 
							or ( trunc(inf.DATCONSEGNAPROSPETTO) >= to_date('01/01/2009', 'dd/mm/yyyy')
							and ( inf.CODMONOSTATOPROSPETTO = 'A' 
							or ( inf.CODMONOPROV = 'M' 
							and (inf.CODMONOSTATOPROSPETTO = 'S' or inf.CODMONOSTATOPROSPETTO = 'V') ) ) ) );

           BEGIN
           			FOR richNP IN richCursorNP
                     LOOP
                          p_prgAzienda := richNP.prgazienda;
                          p_prgunita := richNP.prgunita;
                          p_prg := richNP.prgprospettoinf;
						  p_numAnnoRifProspetto := richNP.numannorifprospetto;
						  p_datInizioVal := richNP.datInizioVal;
						  
						  checkProsp := pg_gestamm.insertdocumento(parprgdoc => parprgdoc,
                          			parcodcpi => p_codCpi,
                                    parcdnlav => '',
                                    parprgazienda => p_prgAzienda,
                                    parprgunita => p_prgunita,
                                    parcodtipodoc => 'PINF',
                                    parflgautocertif => '',
                                    parstrdesc => 'Prospetto informativo ' || p_numAnnoRifProspetto,
                                    parflgdocamm => '',
                                    parflgdocidentifp => '',
                                    pardatainizio => to_char(p_datInizioVal, 'dd/mm/yyyy'),
                                    parstrnumdoc => '',
                                    parenterilascio => '',
                                    parcodmonoio => 'I',
                                    pardataacqril => to_char(sysdate, 'dd/mm/yyyy'),
                                    parcodmodalitaacqri => '',
                                    parcodtipofile => '',
                                    parstrnomedoc => '',
                                    pardatafine => '',
                                    parnumannoprot => '',
                                    parnumprot => '',
                                    parstrnote => '',
                                    parcdnutins => 200,
                                    parcdnutmod => 200,
                                    pardataprotocollazione => '',
                                    parcodstatoatto => 'NP',
                                    parprgdocblob => parprgdocblob,
                                    parpagina => 'CMProspDettPage',
                                    parprgdoccoll => parprgdoccoll,
                                    parstrchiavetabella => p_prg);
                	END LOOP;
			
			DECLARE
                CURSOR richCursorAN IS
                	select inf.prgprospettoinf, inf.prgazienda, inf.prgunita, inf.numannorifprospetto,
						   decode(inf.DATCONSEGNAPROSPETTO,NULL,trunc(sysdate),inf.DATCONSEGNAPROSPETTO) as datInizioVal
		  			from cm_prospetto_inf inf
		  			where inf.CODMONOSTATOPROSPETTO = 'N';
				
			BEGIN
           			FOR richAN IN richCursorAN
                     LOOP
                          p_prgAzienda := richAN.prgazienda;
                          p_prgunita := richAN.prgunita;
                          p_prg := richAN.prgprospettoinf;
						  p_numAnnoRifProspetto := richAN.numannorifprospetto;
						  p_datInizioVal := richAN.datInizioVal;
						  
						  checkProsp := pg_gestamm.insertdocumento(parprgdoc => parprgdoc,
                          			parcodcpi => p_codCpi,
                                    parcdnlav => '',
                                    parprgazienda => p_prgAzienda,
                                    parprgunita => p_prgunita,
                                    parcodtipodoc => 'PINF',
                                    parflgautocertif => '',
                                    parstrdesc => 'Prospetto informativo ' || p_numAnnoRifProspetto,
                                    parflgdocamm => '',
                                    parflgdocidentifp => '',
                                    pardatainizio => to_char(p_datInizioVal, 'dd/mm/yyyy'),
                                    parstrnumdoc => '',
                                    parenterilascio => '',
                                    parcodmonoio => 'I',
                                    pardataacqril => to_char(sysdate, 'dd/mm/yyyy'),
                                    parcodmodalitaacqri => '',
                                    parcodtipofile => '',
                                    parstrnomedoc => '',
                                    pardatafine => '',
                                    parnumannoprot => '',
                                    parnumprot => '',
                                    parstrnote => '',
                                    parcdnutins => 200,
                                    parcdnutmod => 200,
                                    pardataprotocollazione => '',
                                    parcodstatoatto => 'AN',
                                    parprgdocblob => parprgdocblob,
                                    parpagina => 'CMProspDettPage',
                                    parprgdoccoll => parprgdoccoll,
                                    parstrchiavetabella => p_prg);
                	END LOOP;
              END;
		END;
  
 commit;

 exception
   when others then
      dbms_output.PUT_LINE('Errore = ' || sqlcode);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/PROT_PROSPETTI_ALLA_DATA.sql
************************************************************************************** */


create or replace procedure prot_prospetti_alla_data(dataConsegna varchar2) as


CURSOR richCursor IS
          select inf.prgprospettoinf, inf.prgazienda, inf.prgunita, inf.numannorifprospetto,
         decode(inf.DATCONSEGNAPROSPETTO,NULL,trunc(sysdate),inf.DATCONSEGNAPROSPETTO) as datInizioVal
      from cm_prospetto_inf inf
      where inf.CODMONOPROV = 'S' and (inf.CODMONOSTATOPROSPETTO = 'S' or inf.CODMONOSTATOPROSPETTO = 'V')
          and inf.DATCONSEGNAPROSPETTO >= to_date(dataConsegna, 'dd/mm/yyyy');

nro number;
checkProsp number;
p_prgAzienda number;
p_prgunita number;
p_prg number;
p_datdatainizio varchar2(10);
parprgdoc number;
parprgdocblob number;
parprgdoccoll number;
p_numprotocollo number;
p_numannoprot number;
p_dataoraprot varchar2(100);
p_codCpi de_cpi.codcpi%type;
p_numAnnoRifProspetto cm_prospetto_inf.numannorifprospetto%type;
p_datInizioVal cm_prospetto_inf.datconsegnaprospetto%type;

      BEGIN

         select de.codCpiCapoluogo codCpi
      into p_codCpi
      from ts_generale ts
      inner join de_provincia de on (de.codProvincia = ts.codProvinciaSil);

           FOR rich IN richCursor
             LOOP
               p_prgAzienda := rich.prgazienda;
               p_prgunita := rich.prgunita;
               p_prg := rich.prgprospettoinf;
         p_numAnnoRifProspetto := rich.numannorifprospetto;
         p_datInizioVal := rich.datInizioVal;

               select(am_protocollo.numprotocollo + 1) numprotocollo,
               am_protocollo.numannoprot,
               TO_CHAR (SYSDATE, 'dd/mm/yyyy hh24:mi') AS dataoraprot
               into p_numprotocollo, p_numannoprot, p_dataoraprot
               from am_protocollo
               where numannoprot = 2009
               for update;

               checkProsp := pg_gestamm.insertdocumento(parprgdoc => parprgdoc,
                            parcodcpi => p_codCpi,
                            parcdnlav => '',
                            parprgazienda => p_prgAzienda,
                            parprgunita => p_prgunita,
                            parcodtipodoc => 'PINF',
                            parflgautocertif => '',
                            parstrdesc => 'Prospetto informativo ' || p_numAnnoRifProspetto,
                            parflgdocamm => '',
                            parflgdocidentifp => '',
                            pardatainizio => to_char(p_datInizioVal, 'dd/mm/yyyy'),
                            parstrnumdoc => '',
                            parenterilascio => '',
                            parcodmonoio => 'I',
                            pardataacqril => to_char(sysdate, 'dd/mm/yyyy'),
                            parcodmodalitaacqri => '',
                            parcodtipofile => '',
                            parstrnomedoc => '',
                            pardatafine => '',
                            parnumannoprot => p_numannoprot,
                            parnumprot => p_numprotocollo,
                            parstrnote => '',
                            parcdnutins => 200,
                            parcdnutmod => 200,
                            pardataprotocollazione => p_dataoraprot,
                            parcodstatoatto => 'PR',
                            parprgdocblob => parprgdocblob,
                            parpagina => 'CMProspDettPage',
                            parprgdoccoll => parprgdoccoll,
                            parstrchiavetabella => p_prg);

                 if (checkProsp = 0) then

                    update am_protocollo set
                    numprotocollo = p_numprotocollo,
                    numkloprotocollo = numkloprotocollo+1
                    where numannoprot = 2009;

                 end if;

             END LOOP;

       DECLARE
                CURSOR richCursorNP IS
                  select inf.prgprospettoinf, inf.prgazienda, inf.prgunita, inf.numannorifprospetto,
               decode(inf.DATCONSEGNAPROSPETTO,NULL,trunc(sysdate),inf.DATCONSEGNAPROSPETTO) as datInizioVal
          from cm_prospetto_inf inf
          where inf.CODMONOSTATOPROSPETTO <> 'N' and
                ( inf.DATCONSEGNAPROSPETTO is null
              or trunc(inf.DATCONSEGNAPROSPETTO) < to_date(dataConsegna, 'dd/mm/yyyy')
              or ( trunc(inf.DATCONSEGNAPROSPETTO) >= to_date(dataConsegna, 'dd/mm/yyyy')
              and ( inf.CODMONOSTATOPROSPETTO = 'A'
              or ( inf.CODMONOPROV = 'M'
              and (inf.CODMONOSTATOPROSPETTO = 'S' or inf.CODMONOSTATOPROSPETTO = 'V') ) ) ) );

           BEGIN
                 FOR richNP IN richCursorNP
                     LOOP
                          p_prgAzienda := richNP.prgazienda;
                          p_prgunita := richNP.prgunita;
                          p_prg := richNP.prgprospettoinf;
              p_numAnnoRifProspetto := richNP.numannorifprospetto;
              p_datInizioVal := richNP.datInizioVal;

              checkProsp := pg_gestamm.insertdocumento(parprgdoc => parprgdoc,
                                parcodcpi => p_codCpi,
                                    parcdnlav => '',
                                    parprgazienda => p_prgAzienda,
                                    parprgunita => p_prgunita,
                                    parcodtipodoc => 'PINF',
                                    parflgautocertif => '',
                                    parstrdesc => 'Prospetto informativo ' || p_numAnnoRifProspetto,
                                    parflgdocamm => '',
                                    parflgdocidentifp => '',
                                    pardatainizio => to_char(p_datInizioVal, 'dd/mm/yyyy'),
                                    parstrnumdoc => '',
                                    parenterilascio => '',
                                    parcodmonoio => 'I',
                                    pardataacqril => to_char(sysdate, 'dd/mm/yyyy'),
                                    parcodmodalitaacqri => '',
                                    parcodtipofile => '',
                                    parstrnomedoc => '',
                                    pardatafine => '',
                                    parnumannoprot => '',
                                    parnumprot => '',
                                    parstrnote => '',
                                    parcdnutins => 200,
                                    parcdnutmod => 200,
                                    pardataprotocollazione => '',
                                    parcodstatoatto => 'NP',
                                    parprgdocblob => parprgdocblob,
                                    parpagina => 'CMProspDettPage',
                                    parprgdoccoll => parprgdoccoll,
                                    parstrchiavetabella => p_prg);
                  END LOOP;

      DECLARE
                CURSOR richCursorAN IS
                  select inf.prgprospettoinf, inf.prgazienda, inf.prgunita, inf.numannorifprospetto,
               decode(inf.DATCONSEGNAPROSPETTO,NULL,trunc(sysdate),inf.DATCONSEGNAPROSPETTO) as datInizioVal
            from cm_prospetto_inf inf
            where inf.CODMONOSTATOPROSPETTO = 'N';

      BEGIN
                 FOR richAN IN richCursorAN
                     LOOP
                          p_prgAzienda := richAN.prgazienda;
                          p_prgunita := richAN.prgunita;
                          p_prg := richAN.prgprospettoinf;
              p_numAnnoRifProspetto := richAN.numannorifprospetto;
              p_datInizioVal := richAN.datInizioVal;

              checkProsp := pg_gestamm.insertdocumento(parprgdoc => parprgdoc,
                                parcodcpi => p_codCpi,
                                    parcdnlav => '',
                                    parprgazienda => p_prgAzienda,
                                    parprgunita => p_prgunita,
                                    parcodtipodoc => 'PINF',
                                    parflgautocertif => '',
                                    parstrdesc => 'Prospetto informativo ' || p_numAnnoRifProspetto,
                                    parflgdocamm => '',
                                    parflgdocidentifp => '',
                                    pardatainizio => to_char(p_datInizioVal, 'dd/mm/yyyy'),
                                    parstrnumdoc => '',
                                    parenterilascio => '',
                                    parcodmonoio => 'I',
                                    pardataacqril => to_char(sysdate, 'dd/mm/yyyy'),
                                    parcodmodalitaacqri => '',
                                    parcodtipofile => '',
                                    parstrnomedoc => '',
                                    pardatafine => '',
                                    parnumannoprot => '',
                                    parnumprot => '',
                                    parstrnote => '',
                                    parcdnutins => 200,
                                    parcdnutmod => 200,
                                    pardataprotocollazione => '',
                                    parcodstatoatto => 'AN',
                                    parprgdocblob => parprgdocblob,
                                    parpagina => 'CMProspDettPage',
                                    parprgdoccoll => parprgdoccoll,
                                    parstrchiavetabella => p_prg);
                  END LOOP;
              END;
    END;

 commit;

 exception
   when others then
      dbms_output.PUT_LINE('Errore = ' || sqlcode);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/SCRIPT_FUNCTION_InformativaPrivacyRER.sql
************************************************************************************** */



create or replace FUNCTION tuttiIProfiliRER(cdnComp in TS_COMPONENTE.CDNCOMPONENTE%type)  RETURN NUMBER AS

CURSOR cur_profili IS
      SELECT DISTINCT cdnprofilo FROM ts_profilo_gruppo WHERE cdngruppo IN
      (SELECT cdngruppo FROM ts_gruppo WHERE codprovinciasil IN
      (SELECT codprovinciasil FROM de_provincia WHERE codregione = 8));
     
      cur_rec cur_profili%ROWTYPE;
      profiloVar ts_abi_profilo.CDNPROFILO%type;
        
BEGIN

  FOR cur_rec IN cur_profili LOOP
	begin
    profiloVar := cur_rec.cdnprofilo;
    INSERT into ts_abi_profilo(CDNPROFILO,CDNCOMPONENTE,CDNUTINS,DTMINS,CDNUTMOD,DTMMOD)
    VALUES(profiloVar,cdnComp,1,sysdate,1,sysdate);
     dbms_output.put_line('inserito correttamente sul profilo '||profiloVar);
	exception
		when DUP_VAL_ON_INDEX then
		dbms_output.put_line('errore di inserimento su '||profiloVar||': valore gia presente');           
		--return -1;
  end;
  END LOOP;
  return 0;
 
  exception 
	when others then
		dbms_output.put_line('errore non gestito: intervenire');          
		return -1;
              
END tuttiIProfiliRER;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Functions/studio_main.sql
************************************************************************************** */


--Assegna per ogni lavoratore presente in PR_STUDIO il campo FLGPRINCIPALE sul titolo di studio principale
--per l'alberatura dei titoli ho che il titstudio principale è MAX(CODTITOLO)
create or replace procedure compute_main_titstu(chiavecm IN VARCHAR2, doupdate IN BOOLEAN := false) AS
cur_cnt NUMBER := 0;
max_tit PR_STUDIO.codtitolo%type;
tmp_tit PR_STUDIO.codtitolo%type;
cursor cm_cur is select distinct(decrypt(CM.CDNLAVORATORE, chiavecm))  cdnlav , LAV.STRCODICEFISCALE
                    from am_cm_iscr cm 
                    join an_lavoratore lav on lav.cdnlavoratore = decrypt(CM.CDNLAVORATORE, 'chiaveCM');
begin
 
 for lav in cm_cur loop
    
    --dbms_output.put_line('Titoli di '||LAV.STRCODICEFISCALE);
    max_tit := 0; --nessun titolo
    cur_cnt := 0;
   begin
    for rec in (select codtitolo from pr_studio where cdnlavoratore = lav.cdnlav) loop
        cur_cnt := cur_cnt + 1;
        if (rec.codtitolo > max_tit) then
            max_tit := rec.codtitolo;
        else 
            null;
        end if;
        --dbms_output.put_line('-'||rec.codtitolo);
    end loop;
    select DECODE(max_tit,'NT',0,max_tit) into tmp_tit from dual;
    if (tmp_tit <> 0) then
        --dbms_output.put_line('SCELTO:'||max_tit);
        if (doupdate = true) then
        UPDATE PR_STUDIO SET FLGPRINCIPALE = 'S' WHERE CDNLAVORATORE = lav.CDNLAV AND CODTITOLO = max_tit;
        end if;
        dbms_output.put_line('UPDATE PR_STUDIO SET FLGPRINCIPALE = ''S'' WHERE CDNLAVORATORE = '||lav.CDNLAV||' AND CODTITOLO = '''||max_tit||'''');
    end if;
    --dbms_output.put_line('--------------');
    EXCEPTION
    when others then
    dbms_output.put_line('UPDATE FAIL:'||sqlerrm||' ON '||lav.cdnlav||' CODTITOLO='||max_tit);
    end;
 end loop;
 

end;

/

