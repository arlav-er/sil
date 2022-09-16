set define off
set pagesize 0
set linesize 10000
set sqlblanklines on




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ag_calendario.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AG_CALENDARIO
(CODCPI, DATDATA, CODTIPOVISTA, NUMGIORNO, NUMMESE, 
 NUMANNO, DATINIZIOVAL, DATFINEVAL)
AS 
SELECT
 DISTINCT AG.codCpi codCPI,
 to_date(to_char(dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy') datData,
  1 codTipoVista, to_number(to_char(dtmDataOra, 'dd')) numGiorno,
   to_number(to_char(dtmDataOra, 'mm')) numMese,
    to_number(to_char(dtmDataOra, 'yyyy')) numAnno,
	sysdate as datInizioVal,
	sysdate  as datFineVal
FROM AG_AGENDA AG
-- GIORNI CON SLOT PRENOTABILI - CODTIPOVISTA=3
union
SELECT distinct
slot.codCpi as codCPI,
to_date(to_char(slot.dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy') datData ,
3 as codTipoVista,
to_number(to_char(slot.dtmDataOra, 'dd')) numGiorno,
to_number(to_char(slot.dtmDataOra, 'mm')) numMese,
to_number(to_char(slot.dtmDataOra, 'yyyy')) numAnno,
sysdate as datInizioVal,
sysdate  as datFineVal
FROM AG_slot slot inner join DE_STATO_SLOT
on (slot.codstatoslot = de_stato_slot.CODSTATOSLOT)
WHERE  ( ( (nvl(slot.NUMAZIENDE,0)+nvl(slot.NUMLAVORATORI,0)) - ( nvl(slot.NUMAZIENDEPRENOTATE,0) + nvl(slot.NUMLAVPRENOTATI,0)) )  >0)
       and flgprenotabile ='S'
-- GIORNI NON LAVORATIVI - CODTIPOVISTA=0
union
SELECT DISTINCT
NL.codCpi as codCPI,
null as datData,
0 as codTipoVista,
numGg as numGiorno,
numMm as numMese,
numAaaa as numAnno,
datInizioVal as datInizioVal,
datFineVal  as datFineVal
FROM AG_GIORNONL NL
-- GIORNI CON INCONGRUENZE - CODTIPOVISTA=2
union
-- Incongruenze per stesso operatore
select DISTINCT
 ag1.codCpi as codCpi,
to_date(to_char(ag1.dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy') datData,
2 codTipoVista,
to_number(to_char(ag1.dtmDataOra, 'dd')) numGiorno,
to_number(to_char(ag1.dtmDataOra, 'mm')) numMese,
to_number(to_char(ag1.dtmDataOra, 'yyyy')) numAnno,
sysdate as datInizioVal,
sysdate  as datFineVal
from ag_agenda ag1
	 left outer join DE_STATO_APPUNTAMENTO sa1 on (ag1.CODSTATOAPPUNTAMENTO=sa1.CODSTATOAPPUNTAMENTO)
where sa1.FLGATTIVO='S'
	  AND exists
(select * from ag_agenda ag2 left outer join de_stato_appuntamento sa2 on (ag2.CODSTATOAPPUNTAMENTO=sa2.CODSTATOAPPUNTAMENTO)
 where  (sa2.FLGATTIVO='S') and
 		trunc(AG1.DTMDATAORA) = trunc(AG2.DTMDATAORA)
  		and AG1.codCpi = AG2.codCpi
		and AG1.prgspi = AG2.prgspi
		and AG1.prgAppuntamento <> AG2.prgAppuntamento
		and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
		)
-- Incongruenze per Stesso Lavoratore
union
select distinct
ag1.codCpi as codCpi,
to_date(to_char(ag1.dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy') datData,
2 codTipoVista,
to_number(to_char(ag1.dtmDataOra, 'dd')) numGiorno,
to_number(to_char(ag1.dtmDataOra, 'mm')) numMese,
to_number(to_char(ag1.dtmDataOra, 'yyyy')) numAnno,
sysdate as datInizioVal,
sysdate  as datFineVal
from ag_agenda ag1, ag_lavoratore la1, DE_STATO_APPUNTAMENTO sa1
where
	(ag1.CODSTATOAPPUNTAMENTO=sa1.CODSTATOAPPUNTAMENTO and sa1.FLGATTIVO='S') and
	 		   ag1.codCpi = la1.codcpi
	and		   ag1.PRGAPPUNTAMENTO = la1.PRGAPPUNTAMENTO
	and
    exists
(select * from ag_agenda ag2 , AG_LAVORATORE LA2, DE_STATO_APPUNTAMENTO sa2
where  	(ag2.CODSTATOAPPUNTAMENTO=sa2.CODSTATOAPPUNTAMENTO and sa2.FLGATTIVO='S') and
		ag2.codCpi = la2.codcpi
		and	ag2.PRGAPPUNTAMENTO = la2.PRGAPPUNTAMENTO
		and
 		trunc(AG1.DTMDATAORA) = trunc(AG2.DTMDATAORA)
  		and aG1.codCpi = AG2.codCpi
		and la1.cdnlavoratore = la2.cdnlavoratore
		and AG1.prgAppuntamento <> AG2.prgAppuntamento
		and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
		)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_agenda_errd.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AGENDA_ERRD(CODCPI, PRGAPPUNTAMENTO)
AS 
select ag1.CODCPI as CODCPI, ag1.PRGAPPUNTAMENTO as PRGAPPUNTAMENTO
from ag_agenda ag1
	 left outer join de_stato_appuntamento sa1 on (ag1.CODSTATOAPPUNTAMENTO=sa1.CODSTATOAPPUNTAMENTO)
where (sa1.FLGATTIVO='S')
	  and exists
(select * from ag_agenda ag2, de_stato_appuntamento sa2
 where  (ag2.CODSTATOAPPUNTAMENTO=sa2.CODSTATOAPPUNTAMENTO and sa2.FLGATTIVO='S')
 		and trunc(AG2.DTMDATAORA) = trunc(AG1.DTMDATAORA)
  		and AG2.codCpi = AG1.codCpi
		and AG2.prgspi = AG1.prgspi
		and AG2.prgAppuntamento <> AG1.prgAppuntamento
		and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
		)
union
select ag1.CODCPI as CODCPI, ag1.PRGAPPUNTAMENTO as PRGAPPUNTAMENTO
from ag_agenda ag1, ag_lavoratore la1, de_stato_appuntamento sa1
where
	 (ag1.CODSTATOAPPUNTAMENTO=sa1.CODSTATOAPPUNTAMENTO and sa1.FLGATTIVO='S') and
	 		   ag1.codCpi = la1.codcpi
	and		   ag1.PRGAPPUNTAMENTO = la1.PRGAPPUNTAMENTO
	and
   exists
(select * from ag_agenda ag2 , AG_LAVORATORE LA2, de_stato_appuntamento sa2
where  	(ag2.CODSTATOAPPUNTAMENTO=sa2.CODSTATOAPPUNTAMENTO and sa2.FLGATTIVO='S')
		and ag2.codCpi = la2.codcpi
		and	ag2.PRGAPPUNTAMENTO = la2.PRGAPPUNTAMENTO
		and
 		trunc(AG1.DTMDATAORA) = trunc(AG2.DTMDATAORA)
  		and aG1.codCpi = AG2.codCpi
		and la1.cdnlavoratore = la2.cdnlavoratore
		and AG1.prgAppuntamento <> AG2.prgAppuntamento
		and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
		)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_agenda_err.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AGENDA_ERR (CODCPI, PRGAPPUNTAMENTO, CDNTIPOERR, STRTIPOERR)
AS 
select ag1.CODCPI as CODCPI, ag1.PRGAPPUNTAMENTO as PRGAPPUNTAMENTO, 1 as CdnTipoErr, 'Stesso operatore' as strTipoErr
from ag_agenda ag1, de_stato_appuntamento sa1
where (ag1.CODSTATOAPPUNTAMENTO=sa1.CODSTATOAPPUNTAMENTO and sa1.FLGATTIVO='S')
	  and exists
(select * from ag_agenda ag2, de_stato_appuntamento sa2
 where  (ag2.CODSTATOAPPUNTAMENTO=sa2.CODSTATOAPPUNTAMENTO and sa2.FLGATTIVO='S') and
 		trunc(AG2.DTMDATAORA) = trunc(AG1.DTMDATAORA)
  		and AG2.codCpi = AG1.codCpi
		and AG2.prgspi = AG1.prgspi
		and AG2.prgAppuntamento <> AG1.prgAppuntamento
		and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
		)
union all
select ag1.CODCPI as CODCPI, ag1.PRGAPPUNTAMENTO as PRGAPPUNTAMENTO, 2 as CdnTipoErr, 'Stesso lavoratore' as strTipoErr
from ag_agenda ag1, ag_lavoratore la1, de_stato_appuntamento sa1
where
	 (ag1.CODSTATOAPPUNTAMENTO=sa1.CODSTATOAPPUNTAMENTO and sa1.FLGATTIVO='S')
	 and	   ag1.codCpi = la1.codcpi
	and		   ag1.PRGAPPUNTAMENTO = la1.PRGAPPUNTAMENTO
	and
   exists
(select * from ag_agenda ag2 , AG_LAVORATORE LA2, de_stato_appuntamento sa2
where  	(ag2.CODSTATOAPPUNTAMENTO=sa2.CODSTATOAPPUNTAMENTO and sa2.FLGATTIVO='S')
		and ag2.codCpi = la2.codcpi
		and	ag2.PRGAPPUNTAMENTO = la2.PRGAPPUNTAMENTO
		and
 		trunc(AG1.DTMDATAORA) = trunc(AG2.DTMDATAORA)
  		and aG1.codCpi = AG2.codCpi
		and la1.cdnlavoratore = la2.cdnlavoratore
		and AG1.prgAppuntamento <> AG2.prgAppuntamento
		and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
		)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ag_orario_appunt.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AG_ORARIO_APPUNT AS
SELECT CODCPI, PRGAPPUNTAMENTO,
TO_CHAR(AG_AGENDA.DTMDATAORA,'hh24:mi') ORASTR,
to_number(TO_CHAR(DTMDATAORA,'HH24')) ORA,
to_number(TO_CHAR(DTMDATAORA,'mi')) MIN,
DTMDATAORA
FROM AG_AGENDA
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ag_slot.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AG_SLOT
(CODCPI, DATDATA, CODTIPOVISTA, NUMGIORNO, NUMMESE, 
 NUMANNO, DATINIZIOVAL, DATFINEVAL)
AS 
SELECT
	   AG.codCpi codCPI,
	   to_date(to_char(dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy') datData,
  	   1000 codTipoVista,
	   to_number(to_char(dtmDataOra, 'dd')) numGiorno,
   	   to_number(to_char(dtmDataOra, 'mm')) numMese,
       to_number(to_char(dtmDataOra, 'yyyy')) numAnno,
	   sysdate as datInizioVal,
	   sysdate  as datFineVal
FROM AG_slot AG
left outer join de_stato_slot on (ag.CODSTATOSLOT=de_stato_slot.CODSTATOSLOT)
where de_stato_slot.FLGATTIVO='S'
-- GIORNI CON SLOT INCONGRUENTI per Operatore
union
select
	   ag1.codCpi as codCpi,
	   to_date(to_char(ag1.dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy') datData,
	   1001 codTipoVista,
	   to_number(to_char(ag1.dtmDataOra, 'dd')) numGiorno,
	   to_number(to_char(ag1.dtmDataOra, 'mm')) numMese,
	   to_number(to_char(ag1.dtmDataOra, 'yyyy')) numAnno,
	   sysdate as datInizioVal,
	   sysdate  as datFineVal
from ag_slot ag1, ag_spi_slot spi1, de_stato_slot ss1
where
	  (ag1.CODSTATOSLOT=ss1.CODSTATOSLOT and ss1.FLGATTIVO='S')
	  and (ag1.PRGSLOT=spi1.PRGSLOT and ag1.CODCPI=spi1.CODCPI)
	  and exists
	  (select * from ag_slot ag2, ag_spi_slot spi2, de_stato_slot ss2
	   where (ag2.CODSTATOSLOT=ss2.CODSTATOSLOT and ss2.FLGATTIVO='S')
	    and (ag2.PRGSLOT=spi2.PRGSLOT and ag2.CODCPI=spi2.CODCPI)
	    and trunc(AG1.DTMDATAORA) = trunc(AG2.DTMDATAORA)
  		and AG1.codCpi = AG2.codCpi
		and spi1.prgspi = spi2.prgspi
		and AG1.prgSlot <> AG2.prgSlot
		and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
	)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ag_slot_tipo_err.sql
************************************************************************************** */


create or replace view vw_ag_slot_tipo_err as
select 1 AS NUMTIPOERR, 'Incompleto' AS STRDESCTIPOERR,AG_SLOT_TIPO.CODCPI, AG_SLOT_TIPO.PRGSETTIPO, AG_SLOT_TIPO.NUMGIORNOSETT, AG_SLOT_TIPO.PRGGIORNO
from AG_SLOT_TIPO
where
AG_SLOT_TIPO.PRGSPI is null
or
AG_SLOT_TIPO.CODSERVIZIO is null
or
AG_SLOT_TIPO.STRORADALLE is null
or
AG_SLOT_TIPO.STRORAALLE is null
or
AG_SLOT_TIPO.NUMMINUTI is null
or
AG_SLOT_TIPO.NUMQTA is null
OR
AG_SLOT_TIPO.CODDESTINATARIO IS NULL
UNION
select 2 AS NUMTIPOERR, 'Inconsistente' AS STRDESCTIPOERR, AG_SLOT_TIPO.CODCPI, AG_SLOT_TIPO.PRGSETTIPO, AG_SLOT_TIPO.NUMGIORNOSETT, AG_SLOT_TIPO.PRGGIORNO
from AG_SLOT_TIPO
where
AG_SLOT_TIPO.PRGSPI is not null
and
AG_SLOT_TIPO.CODSERVIZIO is not null
and
AG_SLOT_TIPO.STRORADALLE is not null
and
AG_SLOT_TIPO.STRORAALLE is not null
and
AG_SLOT_TIPO.NUMMINUTI is not null
and
AG_SLOT_TIPO.NUMQTA is not null
and
(
nvl(To_number(replace(ag_slot_tipo.STRORAALLE,':')) - To_number(replace(ag_slot_tipo.STRORADALLE,':')), 0) < 0
or
not
(
(ag_slot_tipo.NUMQTA*ag_slot_tipo.NUMMINUTI - 
round(to_number(to_date(ag_slot_tipo.STRORAALLE,'hh24:mi') - to_date(ag_slot_tipo.STRORADALLE,'hh24:mi'))*1440))/ag_slot_tipo.NUMMINUTI
 between -1 and 0
)
)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ag_ultimo_contatto.sql
************************************************************************************** */


create or replace view vw_ag_ultimo_contatto as
select nvl(CDNLAVORATORE, 0) as CDNLAVORATORE, CODCPICONTATTO, max(ag_contatto.DATCONTATTO) as DataUltimoContatto
from ag_contatto
group by CDNLAVORATORE, CODCPICONTATTO
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_AM_LAV_DID.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_LAV_DID
(CDNLAVORATORE, DATDICHIARAZIONE, FLGCONDID)
AS 
SELECT AM_ELENCO_ANAGRAFICO.CDNLAVORATORE,AM_DICH_DISPONIBILITA.DATDICHIARAZIONE,DECODE(AM_DICH_DISPONIBILITA.DATDICHIARAZIONE,NULL,0,1) FLGCONDID
FROM AM_ELENCO_ANAGRAFICO,AM_DICH_DISPONIBILITA
WHERE AM_ELENCO_ANAGRAFICO.PRGELENCOANAGRAFICO=AM_DICH_DISPONIBILITA.PRGELENCOANAGRAFICO
AND AM_DICH_DISPONIBILITA.DATFINE IS NULL 
AND AM_DICH_DISPONIBILITA.CODSTATOATTO='PR'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_lav_situaz_ammin.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_LAV_SITUAZ_AMMIN
(CDNLAVORATORE, FLGCM, FLGMOBILITA, FLGINOCCUPATO, FLGDISOCCUPATO, 
 FLG40790, FLGAPPRENDISTATO, FLG297, FLGDISOCC150, FLGOCC150, MESIANZIANITA, 
 GIORNIANZIANITA, DONNAREINSERIMENTOLAV, DISINOCCLUNGADURATA, CAT181, STATODID)
AS 
SELECT DISTINCT lav.cdnLavoratore,
  -- vedo se il lavoratore è in collocamento mirato	  
  ' '  as flgCM,
  -- vedo se il lavoratore è in mobilità
  nvl((SELECT DISTINCT 'S'
         FROM AM_MOBILITA_ISCR sa
        WHERE lav.cdnlavoratore           = sa.cdnLavoratore
		  and nvl(sa.datfine,SYSDATE) >= SYSDATE
		  and sa.CODTIPOMOB like 'L%'), ' ')      as flgMobilita,
  -- vedo se il lavoratore è inoccupato	ai sensi 297 (e non ha mai lavorato)
  nvl((SELECT DISTINCT 'S'
          FROM AM_STATO_OCCUPAZ sa,
		       DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore          = sa.cdnLavoratore
		   and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and desa.codStatoOccupazRagg   = 'I'), ' ')        as flgInoccupato,
  -- vedo se il lavoratore è disoccupato  si sensi 297 (ed ha già lavorato)
  nvl((SELECT DISTINCT 'S'
          FROM AM_STATO_OCCUPAZ sa,
		       DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore          = sa.cdnLavoratore
		   and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and desa.codStatoOccupazRagg   = 'D'), ' ')         as flgDisoccupato,
  -- vedo se il lavoratore può usufruire della 407/90 (può farlo solo se è inoccupato o disoccupato)
  nvl( 
		(CASE
			When (nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
							instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)), 0)) >= 24
			   Then 'S'
			When (nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
							instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)), 0)) < 24
			   Then ' '
			   
		END), ' ')   as flg40790, -- solo i soggetti Discccupati/Inoccupati possono
  -- vedo se il lavoratore è in apprendistato
  /*nvl((SELECT DISTINCT CASE
					   	   WHEN sa.codContratto = 'A'
                            THEN 'S'
						   WHEN sa.codContratto <> 'A'
                            THEN ' '
					   END
          FROM AM_MOVIMENTO sa
	     WHERE lav.cdnlavoratore          = sa.cdnLavoratore
		   and nvl(sa.datFineMov, sysdate)>= sysdate), ' ')     */
	' ' as flgApprendistato,
	  nvl((SELECT DISTINCT 'S'
          FROM AM_STATO_OCCUPAZ sa,
		       DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore          = sa.cdnLavoratore
		   and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and desa.codStatoOccupazRagg   in ('I', 'D')), ' ')        as flg297,
		   
	  nvl((SELECT DISTINCT 'S'
          FROM AM_STATO_OCCUPAZ sa,
		       DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore          = sa.cdnLavoratore
		   and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and desa.codStatoOccupazRagg   in ('I', 'D') and sa.codStatoOccupaz <> 'B1'), ' ')        as flgDisocc150,
		   
	  nvl((SELECT DISTINCT 'S'
          FROM AM_STATO_OCCUPAZ sa,
		       DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore          = sa.cdnLavoratore
		   and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and (desa.codStatoOccupazRagg   = 'O' or sa.codStatoOccupaz = 'B1')), ' ')        as flgOcc150,		   


	  --Restiruisce i mesi di anzianita del lavoratore
	 nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
					   instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)), 0) as mesiAnzianita,

	  --Restiruisce i giorni di anzianita del lavoratore
	 nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 
					   instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)+1)), 0) as giorniAnzianita,
					   
		 --Donna in reinserimento lavorativo
		 nvl((Select DISTINCT case
							 	  when
								  	  	 (
										 SELECT TRUNC(MONTHS_BETWEEN(SYSDATE,nvl(T1.datfinemovEffettiva,sysdate)))/12 as mesiInattivita
								                 FROM AM_MOVIMENTO T1, AN_unita_AZIENDA AN
								                WHERE T1.CDNLAVORATORE  = lav.cdnlavoratore
								                  AND T1.CODSTATOATTO NOT IN ('AN','AR','AU')
								                  AND T1.PRGAZIENDA     = AN.PRGAZIENDA
												  and t1.prgunita       = an.prgunita
								                       AND
													    ( (   (DECODE(T1.DATFINEMOVEFFETTIVA,NULL,'S','N')='S'
								                              AND T1.datInizioMov = (SELECT MAX(T4.datInizioMov)
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE DECODE(T4.DATFINEMOVEFFETTIVA,NULL,'S','N')='S'
								                                                     AND T4.CDNLAVORATORE = T1.CDNLAVORATORE
																					 AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  <> 'CES')
								                                                 )
								                           OR (NOT exists (SELECT T4.datInizioMov
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE DECODE(T4.DATFINEMOVEFFETTIVA,NULL,'S','N')='S'
								                                                     AND T4.CDNLAVORATORE = T1.CDNLAVORATORE
																					 AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  <> 'CES')
								                              AND T1.DATFINEMOVEFFETTIVA = (SELECT MAX(T4.DATFINEMOVEFFETTIVA)
								                                                                          FROM AM_MOVIMENTO T4
								                                                                         WHERE DECODE(T4.DATFINEMOVEFFETTIVA,NULL,'S','N')='N'
								                                                                           AND T4.CDNLAVORATORE = T1.CDNLAVORATORE
																										   AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																										   AND T4.CODTIPOMOV  <> 'CES')
								                                                 )
								                            ) AND T1.CODTIPOMOV  <> 'CES'
								                      OR ( NOT exists (SELECT T4.datInizioMov
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE T4.CDNLAVORATORE = T1.CDNLAVORATORE
																				   AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  <> 'CES')
													       AND T1.datInizioMov = (SELECT MAX(T4.datInizioMov)
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE T4.CDNLAVORATORE = T1.CDNLAVORATORE
																				   AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  = 'CES')
															AND T1.CODTIPOMOV  = 'CES')
													 ) and rownum = 1
										 )>= 2
								  then 'Donna in reinserimento lavorativo'
							 	  when
								  	  	 (
										 SELECT TRUNC(MONTHS_BETWEEN(SYSDATE,nvl(T1.datfinemovEffettiva,sysdate)))/12 as mesiInattivita
								                 FROM AM_MOVIMENTO T1, AN_unita_AZIENDA AN
								                WHERE T1.CDNLAVORATORE  = lav.cdnlavoratore
								                  AND T1.CODSTATOATTO NOT IN ('AN','AR','AU')
								                  AND T1.PRGAZIENDA     = AN.PRGAZIENDA
												  and t1.prgunita       = an.prgunita
								                       AND
													    ( (   (DECODE(T1.DATFINEMOVEFFETTIVA,NULL,'S','N')='S'
								                              AND T1.datInizioMov = (SELECT MAX(T4.datInizioMov)
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE DECODE(T4.DATFINEMOVEFFETTIVA,NULL,'S','N')='S'
								                                                     AND T4.CDNLAVORATORE = T1.CDNLAVORATORE
																					 AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  <> 'CES')
								                                                 )
								                           OR (NOT exists (SELECT T4.datInizioMov
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE DECODE(T4.DATFINEMOVEFFETTIVA,NULL,'S','N')='S'
								                                                     AND T4.CDNLAVORATORE = T1.CDNLAVORATORE
																					 AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  <> 'CES')
								                              AND T1.DATFINEMOVEFFETTIVA = (SELECT MAX(T4.DATFINEMOVEFFETTIVA)
								                                                                          FROM AM_MOVIMENTO T4
								                                                                         WHERE DECODE(T4.DATFINEMOVEFFETTIVA,NULL,'S','N')='N'
								                                                                           AND T4.CDNLAVORATORE = T1.CDNLAVORATORE
																										   AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																										   AND T4.CODTIPOMOV  <> 'CES')
								                                                 )
								                            ) AND T1.CODTIPOMOV  <> 'CES'
								                      OR ( NOT exists (SELECT T4.datInizioMov
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE T4.CDNLAVORATORE = T1.CDNLAVORATORE
																					 AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  <> 'CES')
													       AND T1.datInizioMov = (SELECT MAX(T4.datInizioMov)
								                                                    FROM AM_MOVIMENTO T4
								                                                   WHERE T4.CDNLAVORATORE = T1.CDNLAVORATORE
																					 AND T4.CODSTATOATTO NOT IN ('AN','AR','AU')
																					 AND T4.CODTIPOMOV  = 'CES')
															AND T1.CODTIPOMOV  = 'CES')
													 ) and rownum = 1
										 ) < 2
								  then ''
							  end
		     FROM AM_STATO_OCCUPAZ sa,
		  	 	  DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore = sa.cdnLavoratore
		   and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and lav.strsesso = 'F'
		   and desa.codStatoOccupazRagg   in ('D')),' ') as DONNAREINSERIMENTOLAV,
		 --DISOCCUPATO/INOCCUPATO DI LUNGA DURATA
		 nvl((SELECT DISTINCT
		 			 		 CASE
							 	 WHEN
								 	 (nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
										instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)), 0)) > 12
									 AND desa.codStatoOccupazRagg = 'D'
								 THEN 'Disoccupato di lunga durata' -- giovane o meno
							 	 WHEN
								 	 (nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
										instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)), 0)) > 6
									 AND desa.codStatoOccupazRagg = 'D'
									--ed è giovane
									 AND (
									 	  	case
												when (
													  SELECT DISTINCT obf.FLGOBBLIGOSCOLASTICO
 									                  FROM an_lavoratore anl, AM_OBBLIGO_FORMATIVO obf
									                  WHERE anl.cdnlavoratore = obf.cdnlavoratore(+)
									                        AND anl.cdnlavoratore = lav.cdnlavoratore
													 ) = 'S'
													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) > 15
 													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) < 18
											    then 'A'
												--********* giovane
												when (
													  trunc((sysdate - lav.DATNASC)/365,0)
													 ) > 18
 													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) < 26
											    then 'G'
												when (
													  SELECT DISTINCT tit2.flglaurea
										              FROM pr_studio prs, de_titolo tit2
										              WHERE prs.codtipotitolo = tit2.codtitolo(+)
										                  AND UPPER(prs.CODMONOSTATO) = 'C'
										                  AND UPPER(tit2.flglaurea) LIKE ('S')
										                  AND prs.cdnlavoratore = lav.cdnlavoratore
													 ) = 'S'
													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) > 18
 													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) < 30
											    then 'G'
																				end
									 	 ) = 'G'
								 THEN 'Disoccupato di lunga durata' --giovane
								 --********* inoccupato di lunga durata
								 WHEN
								 	 (nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
										instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)), 0)) > 12
									 AND desa.codStatoOccupazRagg = 'I'
								 THEN 'Inoccupato di lunga durata' -- giovane o meno
								 WHEN
								 	 (nvl(to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
										instr(PG_MOVIMENTI.CalcolaAnzianita(lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)), 0)) > 6
									 AND desa.codStatoOccupazRagg = 'I'
									 --ed è giovane
									 AND (
									 	  	case
												when (
													  SELECT DISTINCT obf.FLGOBBLIGOSCOLASTICO
 									                  FROM an_lavoratore anl, AM_OBBLIGO_FORMATIVO obf
									                  WHERE anl.cdnlavoratore = obf.cdnlavoratore(+)
									                        AND anl.cdnlavoratore = lav.cdnlavoratore
													 ) = 'S'
													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) > 15
 													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) < 18
											    then 'A'
												--********* giovane
												when (
													  trunc((sysdate - lav.DATNASC)/365,0)
													 ) > 18
 													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) < 26
											    then 'G'
												when (
													  SELECT DISTINCT tit2.flglaurea
										              FROM pr_studio prs, de_titolo tit2
										              WHERE prs.codtipotitolo = tit2.codtitolo(+)
										                  AND UPPER(prs.CODMONOSTATO) = 'C'
										                  AND UPPER(tit2.flglaurea) LIKE ('S')
										                  AND prs.cdnlavoratore = lav.cdnlavoratore
													 ) = 'S'
													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) > 18
 													 AND (
													 	  trunc((sysdate - lav.DATNASC)/365,0)
													 	 ) < 30
											    then 'G'
																				end
									 	 ) = 'G'
								 THEN 'Inoccupato di lunga durata' -- giovane
							 END
             FROM AM_STATO_OCCUPAZ sa,
		  	 	  DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore = sa.cdnLavoratore
		  and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and desa.codStatoOccupazRagg   in ('D', 'I') ), ' ')  as DISINOCCLUNGADURATA,
		 --CATEGORIA 181
		 nvl((SELECT DISTINCT
						 	  	case
									when (
										  SELECT DISTINCT obf.FLGOBBLIGOSCOLASTICO
							                  FROM an_lavoratore anl, AM_OBBLIGO_FORMATIVO obf
						                  WHERE anl.cdnlavoratore = obf.cdnlavoratore(+)
						                        AND anl.cdnlavoratore = lav.cdnlavoratore
										 ) = 'S'
										 AND (
										 	  trunc((sysdate - lav.DATNASC)/365,0)
										 	 ) > 15
											 AND (
										 	  trunc((sysdate - lav.DATNASC)/365,0)
										 	 ) < 18
								    then 'Adolescente'
									--*********
									when (
										  SELECT DISTINCT tit2.flglaurea
							              FROM pr_studio prs, de_titolo tit2
							              WHERE prs.codtipotitolo = tit2.codtitolo(+)
							                  AND UPPER(prs.CODMONOSTATO) = 'C'
							                  AND UPPER(tit2.flglaurea) LIKE ('S')
							                  AND prs.cdnlavoratore = lav.cdnlavoratore
										 ) = 'S'
										 AND (
										 	  trunc((sysdate - lav.DATNASC)/365,0)
										 	 ) > 18
											 AND (
										 	  trunc((sysdate - lav.DATNASC)/365,0)
										 	 ) < 30
								    then 'Giovane'
									when (
										  trunc((sysdate - lav.DATNASC)/365,0)
										 ) > 18
											 AND (
										 	  trunc((sysdate - lav.DATNASC)/365,0)
										 	 ) < 26
								    then 'Giovane'
								end
             FROM AM_STATO_OCCUPAZ sa,
		  	 	  DE_STATO_OCCUPAZ desa
	     WHERE lav.cdnlavoratore = sa.cdnLavoratore
		   and NVL(sa.DATFINE,TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss'))= TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
		   and sa.codStatoOccupaz         = desa.codStatoOccupaz
		   and desa.codStatoOccupazRagg   in ('D', 'I') ), ' ')  as CAT181,

		 -- vedo lo stato della did
  nvl(
  (select distinct decode(nvl(tab.datFine, ''),'','Did valida',tab.datFine, substr(demfa.STRDESCRIZIONE, 1, 15) || '...')
              from am_dich_disponibilita tab,
			   		am_elenco_anagrafico tab1,
					de_motivo_fine_atto demfa
              WHERE tab1.CDNLAVORATORE = lav.cdnlavoratore
				AND tab.prgElencoAnagrafico = tab1.prgElencoAnagrafico
				AND tab.CODMOTIVOFINEATTO   = demfa.CODMOTIVOFINEATTO (+)
                AND tab.codStatoAtto = 'PR'
				AND tab.datDichiarazione    = (select max(tab2.datDichiarazione)
					                             from am_dich_disponibilita tab2, am_elenco_anagrafico tab3
												where tab1.CDNLAVORATORE      = tab3.cdnLavoratore
					                              AND tab2.prgElencoAnagrafico = tab3.prgElencoAnagrafico
												  AND tab2.codStatoAtto = 'PR')
				AND ((trunc(nvl(tab.datfine,to_date('31/12/9999','dd/mm/yyyy'))) =
											  (select max (trunc(nvl(tab4.datfine,to_date('31/12/9999','dd/mm/yyyy'))))
				 		 				    	 from am_dich_disponibilita tab4, am_elenco_anagrafico tab5
												where tab1.CDNLAVORATORE      = tab5.cdnLavoratore
												  and tab4.prgElencoAnagrafico = tab5.prgElencoAnagrafico
												  and tab4.codStatoAtto = 'PR')
					        				      and rownum =1)
					))
		  , ' ')      as STATODID
FROM AN_LAVORATORE lav
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_patto_ag_agenda.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_PATTO_AG_AGENDA
(PRGAPPUNTAMENTO, CODCPI)
AS 
select prgAppuntamento, codCpi
from ag_agenda
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_patto_or_per.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_PATTO_OR_PER
(PRGPERCORSO, PRGCOLLOQUIO)
AS 
select prgPercorso, prgColloquio
from or_percorso_concordato
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_patto_pr_mansione.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_PATTO_PR_MANSIONE
(CDNLAVORATORE, PRGMANSIONE)
AS 
select cdnLavoratore, prgMansione
from pr_mansione
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_appuntamenti.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_APPUNTAMENTI AS
SELECT to_char(ag_.DTMDATAORA,'dd/mm/yyyy') as data,
                        to_char(ag_.DTMDATAORA, 'hh24:mi') as orario,
                        ag_.numminuti as Durata,
                        de_.STRDESCRIZIONE AS DesServizio,
                        ( nvl(esito.STRDESCRIZIONE,' ') ) AS DesEsito,
						lavoratore.cdnLavoratore
                    FROM ag_agenda ag_,
                         de_servizio de_,
                         ag_lavoratore lavoratore,
                         de_esito_appunt esito,
                         am_lav_patto_scelta ps_
                  WHERE (    (lavoratore.codcpi = ag_.codcpi)
                         AND (lavoratore.prgappuntamento = ag_.prgappuntamento)
                         AND (ag_.codservizio = de_.codservizio)
                         AND (ag_.CODESITOAPPUNT = esito.CODESITOAPPUNT (+))
                         AND (ag_.prgappuntamento = ps_.strchiavetabella)
                         AND (ps_.codlsttab = 'AGENDA')
                         AND (ag_.codcpi = ps_.strchiavetabella2))
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_azioni.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_AZIONI AS
SELECT de_.STRDESCRIZIONE Descrizione,
                      to_char(de_.datFineVal, 'dd/mm/yyyy') as  EntroIl,
                      des.strdescrizione as Esito,
					  or_.cdnLavoratore
                 FROM --ev_lavoratore ev_l,
                      or_colloquio or_,
                      or_percorso_concordato percorso,
                      de_azione de_,
                      am_lav_patto_scelta ps_,de_esito des
              WHERE ( --(ev_l.prgcontatto = or_.prgcontatto)
                  --AND
				  (or_.prgcolloquio = percorso.prgcolloquio)
                  AND (de_.prgazioni = percorso.prgazioni)
                  AND (percorso.prgpercorso = ps_.strchiavetabella)
                  AND (ps_.codlsttab = 'OR_PER')
                  AND (percorso.prgcolloquio = ps_.strchiavetabella2)
                  AND (percorso.CODESITO  = des.codesito (+) )
                 )
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_citt_ex_co.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_CITT_EX_CO AS
SELECT
	to_char(datRichiesta,'dd/mm/yyyy') AS datRichiesta,
	to_char(datScadenza ,'dd/mm/yyyy') AS datScadenza,
	DE_EX_MOTIVO_RIL.STRDESCRIZIONE AS DESCRIZIONEMOT,
	DE_STATO_ATTO.STRDESCRIZIONE AS DESCRIZIONERICH,
	aps.prgpattolavoratore
FROM  AM_EX_PERM_SOGG,DE_EX_MOTIVO_RIL, DE_STATO_ATTO, am_lav_patto_scelta aps
WHERE
	aps.CODLSTTAB='AM_EX_PS'
	and aps.STRCHIAVETABELLA=am_ex_perm_sogg.PRGPERMSOGG
	and	AM_EX_PERM_SOGG.CODMOTIVORIL = DE_EX_MOTIVO_RIL.CODMOTIVORIL
	AND	AM_EX_PERM_SOGG.CODSTATORICHIESTA = DE_STATO_ATTO.CODSTATOATTO
	ORDER BY datScadenza DESC
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_coll_mir.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_COLL_MIR AS
SELECT
       DE_CM_TIPO_ISCR.STRDESCRIZIONE AS DESCRIZIONEISCR,
       to_char(I.DATDATAINIZIO,'DD/MM/YYYY') DATINIZIO,
       to_char(I.DATDATAFINE,'DD/MM/YYYY') DATFINE,
       DE_CM_TIPO_INVALIDITA.STRDESCRIZIONE AS DESCRIZIONEINV,
       I.NUMPERCINVALIDITA,
	   aps.PRGPATTOLAVORATORE
FROM
    AM_CM_ISCR I,
    DE_CM_TIPO_ISCR,
    DE_CM_TIPO_INVALIDITA,
    am_lav_patto_scelta aps
WHERE
	aps.STRCHIAVETABELLA=i.PRGCMISCR
    and aps.CODLSTTAB='AM_CM_IS'
    AND I.CODCMTIPOISCR     = DE_CM_TIPO_ISCR.CODCMTIPOISCR(+)
    AND I.CODTIPOINVALIDITA = DE_CM_TIPO_INVALIDITA.CODTIPOINVALIDITA(+)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_corsi_form_prof.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_CORSI_FORM_PROF AS
SELECT DISTINCT de_corso.strdescrizione AS corso, pr_corso.numanno,
                pr_corso.flgcompletato, aps.prgpattolavoratore
           FROM pr_corso, de_corso, am_lav_patto_scelta aps
          WHERE pr_corso.codcorso = de_corso.codcorso
            AND aps.strchiavetabella = pr_corso.prgcorso
            AND aps.codlsttab = 'PR_COR'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_esp_prof.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_ESP_PROF AS
SELECT t1.nummeseinizio, t1.numannoinizio,
       t3.strdescrizione AS descrizionemans,
       t2.strdescrizione AS descrizionecontr,
       (t1.numstipendio * 12) AS retribannua, t1.nummesefine, t1.numannofine,
       aps.prgpattolavoratore
  FROM pr_esp_lavoro t1,
       de_contratto t2,
       de_mansione t3,
       am_lav_patto_scelta aps,
       pr_mansione prm
 WHERE aps.codlsttab = 'PR_ESP_L'
   AND aps.strchiavetabella = t1.prgesplavoro
   AND t1.codcontratto = t2.codcontratto
   AND t1.prgmansione = prm.prgmansione
   AND prm.codmansione = t3.codmansione
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_indisp_temp.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_INDISP_TEMP AS
SELECT   am_indisp_temp.codindisptemp,
         de_indisp_temp.strdescrizione AS descrizione,
         TO_CHAR (am_indisp_temp.datinizio, 'DD/MM/YYYY') AS datinizio,
         TO_CHAR (am_indisp_temp.datfine, 'DD/MM/YYYY') AS datfine,
         aps.prgpattolavoratore
    FROM am_indisp_temp, de_indisp_temp, am_lav_patto_scelta aps
   WHERE (am_indisp_temp.codindisptemp = de_indisp_temp.codindisptemp)
     AND aps.strchiavetabella = am_indisp_temp.prgindisptemp
     AND aps.codlsttab = 'AM_IND_T'
ORDER BY am_indisp_temp.datinizio DESC
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_lst_mob.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_LST_MOB AS
SELECT DISTINCT de_mb_tipo.strdescrizione AS descrizione,
                TO_CHAR (am_mobilita_iscr.datinizio,
                         'DD/MM/YYYY'
                        ) AS datinizio,
                am_mobilita_iscr.flgindennita, aps.prgpattolavoratore
           FROM am_mobilita_iscr, de_mb_tipo, am_lav_patto_scelta aps
          WHERE am_mobilita_iscr.codtipomob = de_mb_tipo.codmbtipo(+)
            AND aps.strchiavetabella = am_mobilita_iscr.prgmobilitaiscr
            AND aps.codlsttab = 'AM_MB_IS'
            AND NVL (am_mobilita_iscr.datfine, SYSDATE) >= SYSDATE
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_patto.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_PATTO AS
SELECT DISTINCT am_.prgpattolavoratore, am_.cdnlavoratore,
                am_.prgdichdisponibilita,
                TO_CHAR (am_.datstipula, 'dd/mm/yyyy') AS datstipula,
                am_.codstatoatto, am_.prgstatooccupaz, am_.flgcomunicazesiti,
                am_.codmotivofineatto,
                TO_CHAR (am_.datscadconferma,
                         'dd/mm/yyyy') AS datscadconferma,
                TO_CHAR (am_.datfine, 'dd/mm/yyyy') AS datfine, am_.strnote,
                am_.cdnutins, TO_CHAR (am_.dtmins, 'dd/mm/yyyy') AS dtmins,
                am_.cdnutmod, TO_CHAR (am_.dtmmod, 'dd/mm/yyyy') AS dtmmod,
                am_.numklopattolavoratore, am_.flgpatto297,
                ts_.strcognome AS cognins, ts_.strnome nomins,
                ts_utentemod.strcognome cognmod, ts_utentemod.strnome nommod,
                an.strcognome, an.strnome, an.strcodicefiscale,
                TO_CHAR (dispo.datdichiarazione,
                         'dd/mm/yyyy'
                        ) AS datdichiarazione,
                TO_CHAR (am.datinizio, 'dd/mm/yyyy') AS datinizio,
                   dsor_.strdescrizione
                || ': '
                || dso_.strdescrizione AS descrizionestato,
                p.strdircoordinatore, p.strresponsabileuo,
                cpi_.strdescrizione, cpi_.strindirizzo, cpi_.strtel,
                cpi_.strfax, cpi_.stremail, p.strdenominazione
           FROM am_patto_lavoratore am_,
                an_lavoratore an,
                am_elenco_anagrafico am,
                am_dich_disponibilita dispo,
                an_lav_storia_inf_coll sc_,
                an_lav_storia_inf st_,
                de_cpi cpi_,
                ts_utente ts_,
                de_provincia p,
                de_comune c,
                ts_utente ts_utentemod,
                am_stato_occupaz so_,
                de_stato_occupaz dso_,
                de_stato_occupaz_ragg dsor_
          WHERE (    (am_.datfine IS NULL)
                 AND (an.cdnlavoratore = am_.cdnlavoratore)
                 AND (am.prgelencoanagrafico(+) = dispo.prgelencoanagrafico)
                 AND (am.prgelencoanagrafico = sc_.strchiavetabella(+))
                 AND (sc_.codlsttab IS NULL OR sc_.codlsttab = 'EA')
                 AND (sc_.prglavstoriainf = st_.prglavstoriainf(+))
                 AND (cpi_.codcpi = am_.codcpi)
                 AND (am_.cdnutins = ts_.cdnut)
                 AND (am_.cdnutmod = ts_utentemod.cdnut)
                 AND (dispo.prgdichdisponibilita(+) = am_.prgdichdisponibilita)
                 AND (am_.datfine IS NULL)
                 AND (am_.prgstatooccupaz = so_.prgstatooccupaz)
                 AND (so_.codstatooccupaz = dso_.codstatooccupaz)
                 AND (dso_.codstatooccupazragg = dsor_.codstatooccupazragg)
                 AND (c.codcpi = cpi_.codcpi)
                 AND (c.codprovincia = p.codprovincia)
                )
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_altreinf.sql
************************************************************************************** */


create or replace view vw_am_rpt_schedaap_altreinf as
select distinct 'Centro per l''impiego di ' || initCap(cpi.strdescrizione) strdenominazione,
lsi.dtmModSchedaAnagProf, lsi.cdnLavoratore
from an_lav_storia_inf lsi, de_cpi cpi
where lsi.datfine   is null
and lsi.codcpitit = cpi.codcpi
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_discontr.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_RPT_SCHEDAAP_DISCONTR
(CDNLAVORATORE, COCOCO, FORMAZIONE, APPRENDISTATO, INTERINALE, 
 TIROCINIO, LSUPIP,LAVORODOMICILIO, LAVORODOMESTICO, TELELAVORO, BORSALAVORO, 
 LAVOROAUTONOMOPIVA, LAVOROOCCASIONALE, SOCIOLAVCOOP, STAGIONALE)
AS 
SELECT DISTINCT ma.cdnLavoratore,
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto = 'CO' and ma1.prgMansione = dco.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as CoCoCo, -- join con il pr_mansione solo con il lavoratore o prodotto cartesiano
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto in ('FL1', 'FL2', 'FL3') and ma1.prgMansione = dco.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as Formazione, -- rivedere: legge 30
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto = 'AP' and ma1.prgMansione = dco.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as Apprendistato,
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto = 'IN' and ma1.prgMansione = dco.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as Interinale,
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto = 'TI' and ma1.prgMansione = dco.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as Tirocinio,
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto in ('RP1', 'RP2') and ma1.prgMansione = dco.prgMansione and  ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as LsuPip,
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto = 'LD' and ma1.prgMansione = dco.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as LavoroDomicilio, -- a domicilio
  ' ' as LavoroDomestico, 
  nvl((SELECT DISTINCT 'Sì' FROM PR_MANSIONE ma1, pr_dis_contratto dco WHERE dco.codContratto = 'TL' and ma1.prgMansione = dco.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as TeleLavoro,
  -- altre forme di rapporto (la descrizione ora serve: estrazione in ordine alfabetico)
  nvl((SELECT DISTINCT deco.strDescrizione FROM PR_MANSIONE ma1, pr_dis_contratto dco, de_contratto deco WHERE dco.codContratto = 'RP3'and ma1.prgMansione = dco.prgMansione and dco.CodContratto = deco.codContratto and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as BorsaLavoro,
  nvl((SELECT DISTINCT deco.strDescrizione FROM PR_MANSIONE ma1, pr_dis_contratto dco, de_contratto deco WHERE dco.codContratto = 'PI' and ma1.prgMansione = dco.prgMansione and dco.CodContratto = deco.codContratto and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as LavoroAutonomoPIVA,
  nvl((SELECT DISTINCT deco.strDescrizione FROM PR_MANSIONE ma1, pr_dis_contratto dco, de_contratto deco WHERE dco.codContratto = 'LO' and ma1.prgMansione = dco.prgMansione and dco.CodContratto = deco.codContratto and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as LavoroOccasionale,
  nvl((SELECT DISTINCT deco.strDescrizione FROM PR_MANSIONE ma1, pr_dis_contratto dco, de_contratto deco WHERE dco.codContratto = 'SO' and ma1.prgMansione = dco.prgMansione and dco.CodContratto = deco.codContratto and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as SocioLavCoop,
  nvl((SELECT DISTINCT deco.strDescrizione FROM PR_MANSIONE ma1, pr_dis_contratto dco, de_contratto deco WHERE dco.codContratto = 'SG' and ma1.prgMansione = dco.prgMansione and dco.CodContratto = deco.codContratto and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as Stagionale
FROM PR_MANSIONE ma
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_disterr.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAP_DISTERR AS
SELECT DISTINCT ma.cdnLavoratore,
  nvl((SELECT DISTINCT 'Sì'   FROM PR_MANSIONE ma1, pr_dis_comune  dis, an_lavoratore lav WHERE dis.codCom = lav.CodComDom and lav.cdnLavoratore = ma1.cdnLavoratore and ma1.prgMansione = dis.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as DisNelComune, -- join con il pr_mansione solo con il lavoratore o prodotto cartesiano
  nvl((SELECT DISTINCT 'Sì'   FROM PR_MANSIONE ma1, pr_dis_regione dis, an_lavoratore lav, de_provincia depr, de_comune deco WHERE dis.codRegione = depr.CodRegione and depr.CodRegione = depr.codRegione and depr.codProvincia = deco.codProvincia and deco.codCom = lav.codComDom and lav.cdnLavoratore = ma1.cdnLavoratore and ma1.prgMansione = dis.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as DisNellaRegione,
  nvl((SELECT DISTINCT 'Sì'   FROM PR_MANSIONE ma1, pr_dis_stato   dis, de_comune deco WHERE dis.codCom = deco.CodCom and deco.codProvincia = '255' and ma1.prgMansione = dis.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as DisNeiPaesiExtraUE,
  nvl((SELECT DISTINCT 'Sì'   FROM PR_MANSIONE ma1, pr_dis_comune  dis, de_cpi decp, de_comune deco, de_comune deco1, an_lavoratore lav, de_cpi deci WHERE dis.codCom = deci.codCom and deci.codCpi = deco.codCpi and deco.codCpi = deco1.codCpi and deco.codCom = deco1.codCom and deco1.codCom = lav.codComDom and lav.cdnLavoratore = ma.cdnLavoratore and ma1.prgMansione = dis.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as DisComuniLimitrofi,
  nvl((SELECT DISTINCT 'Sì'   FROM PR_MANSIONE ma1, pr_dis_stato   dis WHERE dis.codCom = 'Z000'  and ma1.prgMansione = dis.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as DisInItalia,
  nvl((SELECT DISTINCT 'Sì'   FROM PR_MANSIONE ma1, pr_dis_provincia  dis, an_lavoratore lav, de_provincia depr, de_comune deco WHERE dis.codProvincia = depr.codProvincia and depr.codProvincia = deco.codProvincia and deco.codCom = lav.codComDom and lav.cdnLavoratore = ma1.cdnLavoratore and ma1.prgMansione = dis.prgMansione and  ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as DisNellaProvincia,
  nvl((SELECT DISTINCT 'Sì'   FROM PR_MANSIONE ma1, pr_dis_stato   dis, de_comune deco WHERE dis.codCom = deco.CodCom and deco.codProvincia = '254' and ma1.prgMansione = dis.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as DisNeiPaesiUE
FROM PR_MANSIONE ma
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_expermsogg.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_RPT_SCHEDAAP_EXPERMSOGG
(DATRICHIESTA, DATSCADENZA, DESCRIZIONEMOT, CDNLAVORATORE, CODSTATUS)
AS 
SELECT  to_char(EX.DATRICHIESTA,'dd/mm/yyyy') DATRICHIESTA,
        to_char(EX.DATSCADENZA ,'dd/mm/yyyy') DATSCADENZA,
        DE_EX_MOTIVO_RIL.STRDESCRIZIONE      DESCRIZIONEMOT,
        EX.CDNLAVORATORE, EX.CODSTATUS
  FROM  AM_EX_PERM_SOGG EX,DE_EX_MOTIVO_RIL, DE_STATO_ATTO
 WHERE  (EX.CODMOTIVORIL = DE_EX_MOTIVO_RIL.CODMOTIVORIL(+)) AND
        (EX.CODSTATORICHIESTA = DE_STATO_ATTO.CODSTATOATTO(+)) AND
		(   NVL (EX.datscadenza, TO_DATE ('31/12/2100', 'dd/mm/yyyy')) > SYSDATE
            and NVL (EX.datfine, TO_DATE ('31/12/2100', 'dd/mm/yyyy')) > SYSDATE
        )

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_lavdisaltro.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_RPT_SCHEDAAP_LAVDISALTRO
(CDNLAVORATORE, LAVAUTOMUNITO, TRASFERTE)
AS 
SELECT DISTINCT man.cdnlavoratore,
                   NVL ((SELECT DISTINCT 'Sì'
                                   FROM pr_mobil_geogr, pr_mansione
                                   WHERE pr_mobil_geogr.prgMansione = pr_mansione.prgMansione
								   	   and pr_mansione.cdnlavoratore = man.cdnlavoratore
								       and pr_mobil_geogr.FLGDISPAUTO = 'S'
								   ),
                        ' '
                       ) AS lavautomunito,
                   NVL ((SELECT DISTINCT 'Sì'
                                   FROM pr_mobil_geogr, pr_mansione
                                   WHERE NVL (pr_mobil_geogr.codtrasferta, '1') <> '1'
                                     AND pr_mansione.cdnlavoratore = man.cdnlavoratore
									 and pr_mobil_geogr.prgMansione = pr_mansione.prgMansione
								),
                        ' '
                       ) AS trasferte -- join con il pr_mansione solo con il lavoratore o prodotto cartesiano
              FROM pr_mansione man
			  where man.flgDisponibile='S'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_listespec.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAP_LISTESPEC AS
SELECT distinct
    mo.CDNLAVORATORE,
    mo.DATINIZIO,
    mo.DATFINE,
    demo.STRDESCRIZIONE,
    mo.CODTIPOMOB as TipoIscr,
 depr.strTarga as Provincia,
  '' as CDNLAVORATORE_CRYP
  FROM AM_MOBILITA_ISCR mo, DE_MB_TIPO demo, AM_MOVIMENTO mv, AN_UNITA_AZIENDA ua, DE_COMUNE deco, DE_PROVINCIA depr
 WHERE mo.CODTIPOMOB            = demo.CODMBTIPO
   and nvl(mo.DATFINE, sysdate) >= sysdate
   and mo.prgMovimento          = mv.prgMovimento
   and mv.prgAzienda            = ua.prgAzienda
   and mv.prgUnita              = ua.prgUnita
   and ua.CodCom                = deco.codCom
   and deco.codProvincia        = depr.CodProvincia
UNION
SELECT distinct
    0 as CDNLAVORATORE,
    cm.DATDATAINIZIO as DatInizio,
    cm.DATDATAFINE as DatFine,
    'Lista ai sensi della L. 68/99' as STRDESCRIZIONE,
    cm.CODCMTIPOISCR as TipoIscr,
    '' as Provincia,
    cm.CDNLAVORATORE as CDNLAVORATORE_CRYP
FROM AM_CM_ISCR cm
INNER JOIN AM_DOCUMENTO_COLL COLL ON (CM.PRGCMISCR = COLL.STRCHIAVETABELLA)
INNER JOIN AM_DOCUMENTO DOC ON (COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO AND DOC.CODTIPODOCUMENTO = 'L68')
WHERE DOC.CODSTATOATTO = 'PR'
and COLL.CDNCOMPONENTE = (SELECT CDNCOMPONENTE from TS_COMPONENTE WHERE UPPER(STRPAGE) = 'CMISCRIZIONILAVORATOREPAGE')
and cm.DATDATAFINE  is null 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_ls_cm.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAP_LS_CM
(CDNLAVORATORE, DATINIZIO, DATFINE, STRDESCRIZIONE, TIPOISCR, 
 PROVINCIA, CDNLAVORATORE_CRYP)
AS 
SELECT distinct
    0 as CDNLAVORATORE,
    cm.DATDATAINIZIO as DatInizio,
    cm.DATDATAFINE as DatFine,
    'Lista ai sensi della L. 68/99' as STRDESCRIZIONE,
    cm.CODCMTIPOISCR as TipoIscr,
    '' as Provincia,
    cm.CDNLAVORATORE as CDNLAVORATORE_CRYP
FROM AM_CM_ISCR cm
INNER JOIN AM_DOCUMENTO_COLL COLL ON (CM.PRGCMISCR = COLL.STRCHIAVETABELLA)
INNER JOIN AM_DOCUMENTO DOC ON (COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO AND DOC.CODTIPODOCUMENTO = 'L68')
WHERE DOC.CODSTATOATTO = 'PR'
and COLL.CDNCOMPONENTE = (SELECT CDNCOMPONENTE from TS_COMPONENTE WHERE UPPER(STRPAGE) = 'CMISCRIZIONILAVORATOREPAGE')
and cm.DATDATAFINE  is null
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_ls_mob.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAP_LS_MOB
(CDNLAVORATORE, DATINIZIO, DATFINE, STRDESCRIZIONE, TIPOISCR, 
 PROVINCIA, CDNLAVORATORE_CRYP)
AS 
SELECT distinct
    mo.CDNLAVORATORE,
    mo.DATINIZIO,
    mo.DATFINE,
    demo.STRDESCRIZIONE,
    mo.CODTIPOMOB as TipoIscr,
 depr.strTarga as Provincia,
  '' as CDNLAVORATORE_CRYP
  FROM AM_MOBILITA_ISCR mo 
  join DE_MB_TIPO demo on mo.CODTIPOMOB = demo.CODMBTIPO
  left join AN_UNITA_AZIENDA ua on mo.prgAzienda = ua.prgAzienda and mo.prgUnita = ua.prgUnita
  left join DE_COMUNE deco on ua.CodCom = deco.codCom
  left join DE_PROVINCIA depr on deco.codProvincia = depr.CodProvincia
 WHERE nvl(mo.DATFINE, sysdate) >= sysdate 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_mantempo.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_AM_RPT_SCHEDAAP_MANTEMPO
(CDNLAVORATORE, TEMPOINDET, TEMPODET)
AS
SELECT DISTINCT ma.cdnlavoratore,
                NVL ((SELECT DISTINCT 'I'
                                 FROM pr_mansione ma1,
                                      pr_dis_contratto dco,
                                      de_contratto deco
                                WHERE dco.codcontratto = 'LP'
                                  AND ma1.prgmansione = dco.prgmansione
                                  AND dco.codcontratto = deco.codcontratto
                                  AND ma1.cdnlavoratore = ma.cdnlavoratore),
                     ' '
                    ) AS tempoindet,
                NVL ((SELECT DISTINCT 'D'
                                 FROM pr_mansione ma1,
                                      pr_dis_contratto dco,
                                      de_contratto deco
                                WHERE dco.codcontratto = 'LT'
                                  AND ma1.prgmansione = dco.prgmansione
                                  AND dco.codcontratto = deco.codcontratto
                                  AND ma1.cdnlavoratore = ma.cdnlavoratore),
                     ' '
                    ) AS tempodet
           FROM pr_mansione ma
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaapmin_isee.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAPMIN_ISEE
(CDNLAVORATORE, NUMVALOREISEE)
AS 
SELECT 	ISEE.CDNLAVORATORE,
		ISEE.NUMVALOREISEE
FROM 	AS_VALORE_ISEE ISEE 
WHERE   nvl(ISEE.DATFINEVAL,to_date('01/01/2100','dd/mm/yyyy')) > sysdate
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaapmin_ls_cm.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAPMIN_LS_CM
(CDNLAVORATORE, DATINIZIO, DATFINE, STRDESCRIZIONE, TIPOISCR, 
 PROVINCIA, CDNLAVORATORE_CRYP)
AS 
SELECT DISTINCT
	0 as CDNLAVORATORE,
    cm.DATDATAINIZIO as DatInizio,
    cm.DATDATAFINE as DatFine,
    mnlsspec.DES_LISTESPEC as STRDESCRIZIONE,
    '3' as TipoIscr,
    '' as Provincia,
    cm.CDNLAVORATORE as CDNLAVORATORE_CRYP
FROM AM_CM_ISCR cm
INNER JOIN AM_DOCUMENTO_COLL COLL ON (CM.PRGCMISCR = COLL.STRCHIAVETABELLA)
INNER JOIN AM_DOCUMENTO DOC ON (COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO AND DOC.CODTIPODOCUMENTO = 'L68')
INNER JOIN MN_LISTESPECIALI mnlsspec on mnlsspec.cod_listespec = '3'
WHERE DOC.CODSTATOATTO = 'PR' 
AND COLL.CDNCOMPONENTE = (SELECT CDNCOMPONENTE from TS_COMPONENTE WHERE UPPER(STRPAGE) = 'CMISCRIZIONILAVORATOREPAGE')
AND cm.DATDATAFINE  is null
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaapmin_ls_mob.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAPMIN_LS_MOB
(CDNLAVORATORE, DATINIZIO, DATFINE, DATAMAXDIFF, CODLISTESPEC, 
 STRDESCRIZIONE, TIPOISCR, PRCODMIN, PROVINCIA)
AS 
SELECT distinct
       mo.CDNLAVORATORE,
       mo.DATINIZIO,
       mo.DATFINE,
       mo.DATMAXDIFF,
       malsspec.CODLISTESPEC,
       mnlsspec.DES_LISTESPEC as STRDESCRIZIONE, 
       mo.CODTIPOMOB as TipoIscr,
	   depr.CODMIN PRCODMIN,
       depr.STRDENOMINAZIONE as Provincia
  FROM AM_MOBILITA_ISCR mo
      left join an_lav_storia_inf l on mo.CDNLAVORATORE = l.CDNLAVORATORE 
      left join DE_CPI cpi on l.CODCPITIT = cpi.CODCPI
   	  left join DE_PROVINCIA depr on cpi.CODPROVINCIA = depr.CODPROVINCIA
      join DE_MB_TIPO demo on mo.CODTIPOMOB = demo.CODMBTIPO
      join MA_LISTESPECIALI malsspec on demo.CODMBTIPO = malsspec.CODMBTIPO
      left join MN_LISTESPECIALI mnlsspec on malsspec.codlistespec = mnlsspec.cod_listespec
 WHERE nvl(mo.DATFINE, sysdate) >= sysdate
   and nvl(l.DATFINE,to_date('01/01/2100','dd/mm/yyyy')) > sysdate
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaapmin_patenti.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAPMIN_PATENTI
(CDNLAVORATORE, CODABILITAZIONEGEN)
AS 
SELECT ab.cdnLavoratore,
	   ab.codAbilitazioneGen
FROM PR_ABILITAZIONE ab
WHERE ab.CODABILITAZIONEGEN in ('PGA','PGA1','PGB','PGC','PGD','PGE','PGT','PGADR','PGN','PGO')
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_orarioturno.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAP_ORARIOTURNO AS
SELECT DISTINCT ma.cdnLavoratore,
  nvl((SELECT DISTINCT 'Sì' FROM PR_DIS_TURNO dt, PR_MANSIONE ma1 WHERE nvl(dt.codTurno, ' ') = '5' and dt.prgMansione = ma1.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ') as LavoroNotturno, -- join con il pr_mansione solo con il lavoratore o prodotto cartesiano
  nvl((SELECT DISTINCT 'Sì' FROM PR_DIS_TURNO dt, PR_MANSIONE ma1 WHERE nvl(dt.codTurno, ' ') in ('1', '2', '3', '4', '5', '7') and dt.prgMansione = ma1.prgMansione and ma1.cdnlavoratore = ma.cdnLavoratore), ' ')  as TurniFestivo,
  nvl((SELECT DISTINCT 'Sì' FROM PR_DIS_ORARIO do, PR_MANSIONE ma1 WHERE nvl(do.codOrario, ' ') in ('PTV', 'PTO', 'PTM', 'PTVM', 'PTVS', 'M') and do.prgMansione = ma1.prgMansione  and ma1.cdnlavoratore = ma.cdnLavoratore), ' ')  as PartTime
FROM PR_MANSIONE ma
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_schedaap_patenti.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_SCHEDAAP_PATENTI
(CDNLAVORATORE, PATENTEA, PATENTEA1, PATENTEB, PATENTEC,PATENTECQC,PATENTED, 
 PATENTEE, PATENTETRASPPUB, PATENTEADR, PATENTEN, PATENTEO)
AS 
SELECT DISTINCT ab.cdnLavoratore,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGA' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteA,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGA1' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteA1,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGB' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteB,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGC' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteC,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'CQC' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteCQC,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGD' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteD,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGE' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteE,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGT' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteTraspPub,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGADR' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteADR,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGN' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteN,
nvl((SELECT DISTINCT 'Sì' FROM PR_ABILITAZIONE ab1 WHERE ab1.codAbilitazioneGen = 'PGO' and ab1.cdnlavoratore = ab.cdnLavoratore), ' ') as PatenteO
FROM PR_ABILITAZIONE ab
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_stato_occ.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_STATO_OCC AS
SELECT    sor.strdescrizione
       || ': '
       || de_stato_occupaz.strdescrizione AS descrizionestato,
       am_stato_occupaz.codcategoria181,
       de_181_categoria.strdescrizione AS descrizione181,
       TO_CHAR (am_stato_occupaz.datinizio, 'DD/MM/YYYY') AS datinizio,
       TO_CHAR (am_stato_occupaz.datfine, 'DD/MM/YYYY') AS datfine,
       am_stato_occupaz.flgindennizzato, am_stato_occupaz.flgpensionato,
       am_stato_occupaz.nummesisosp,
       TO_CHAR (am_stato_occupaz.datanzianitadisoc,
                'DD/MM/YYYY'
               ) AS datanzianitadisoc,
       am_stato_occupaz.numreddito, am_stato_occupaz.strnumatto,
       TO_CHAR (am_stato_occupaz.datatto, 'DD/MM/YYYY') AS datatto,
       TO_CHAR (am_stato_occupaz.datrichrevisione,
                'DD/MM/YYYY'
               ) AS datrichrevisione,
       TO_CHAR (am_stato_occupaz.datricorsogiurisdiz,
                'DD/MM/YYYY'
               ) AS datricorsogiurisdiz,
       TRUNC (MONTHS_BETWEEN (SYSDATE, am_stato_occupaz.datanzianitadisoc)
             ) AS mesi_anz,
       amp.prgpattolavoratore
  FROM am_stato_occupaz,
       de_stato_occupaz,
       de_181_categoria,
       de_stato_atto,
       am_patto_lavoratore amp,
       de_stato_occupaz_ragg sor
 WHERE am_stato_occupaz.codcategoria181 = de_181_categoria.codcategoria181(+)
   AND am_stato_occupaz.codstatoatto = de_stato_atto.codstatoatto(+)
   AND am_stato_occupaz.codstatooccupaz = de_stato_occupaz.codstatooccupaz(+)
   AND amp.prgstatooccupaz = am_stato_occupaz.prgstatooccupaz
   AND sor.codstatooccupazragg = de_stato_occupaz.codstatooccupazragg
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_titoli_stu.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_TITOLI_STU AS
SELECT   tit.strdescrizione AS destitolo,
         tit2.strdescrizione AS destipotitolo, prs.numanno numanno,
         aps.prgpattolavoratore
    FROM pr_studio prs, am_lav_patto_scelta aps, de_titolo tit,
         de_titolo tit2
   WHERE aps.codlsttab = 'PR_STU'
     AND aps.strchiavetabella = prs.prgstudio
     AND prs.codtitolo = tit.codtitolo
     AND prs.codtipotitolo = tit2.codtitolo
ORDER BY NVL (prs.flgprincipale, ' ') DESC
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_vincoli_compatto.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_VINCOLI_COMPATTO AS
SELECT distinct PR_.PRGMANSIONE,
	   		0 AS RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            1 as Ordine,
            '' as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
			amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            )
UNION
SELECT distinct PR_.PRGMANSIONE,
	   		1 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            2 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_contratto dis_,
            de_contratto dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codcontratto = dis_.codcontratto)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			1 as RAGRUPPAMENTO,
			de_.strdescrizione as Mansione,
            3 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_orario dis_,
            de_orario dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
			AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codorario = dis_.codorario)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			1 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            4 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile, pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_turno dis_,
            de_turno dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codturno = dis_.codturno)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            5 as Ordine,
            pg_utils.concatena (amp.cdnlavoratore,pr_.PRGMANSIONE, 5) as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_comune dis_,
            de_comune dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codcom = dis_.codcom)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            6 as Ordine,
            pg_utils.concatena (amp.cdnlavoratore,pr_.PRGMANSIONE, 6) as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione, pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_provincia dis_,
            de_provincia dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codprovincia = dis_.codprovincia)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            7 as Ordine,
            pg_utils.concatena (amp.cdnlavoratore,pr_.PRGMANSIONE, 7) as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_regione dis_,
            de_regione dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codregione = dis_.codregione)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            8 as Ordine,
            pg_utils.concatena (amp.cdnlavoratore,pr_.PRGMANSIONE, 8) as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.cdnLavoratore
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_stato dis_,
            de_comune dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codcom = dis_.codcom)
            )
            union
            SELECT distinct PR_.PRGMANSIONE,
			3 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            9 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            dis_.FLGDISPAUTO, dis_.flgDispMoto, dis_.flgPendolarismo, dis_.NUMOREPERC, dis_.FLGMOBSETT,
            amp.cdnLavoratore
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_mobil_geogr dis_,
            de_trasferta dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codtrasferta = dis_.codtrasferta)
            )
            order by 1, 2, 3, 4, 5
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_am_rpt_vincoli.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_RPT_VINCOLI AS
SELECT distinct PR_.PRGMANSIONE,
	   		0 AS RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            1 as Ordine,
            '' as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
			amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            )
UNION
SELECT distinct PR_.PRGMANSIONE,
	   		1 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            2 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_contratto dis_,
            de_contratto dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codcontratto = dis_.codcontratto)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			1 as RAGRUPPAMENTO,
			de_.strdescrizione as Mansione,
            3 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_orario dis_,
            de_orario dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
			AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codorario = dis_.codorario)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			1 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            4 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile, pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_turno dis_,
            de_turno dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codturno = dis_.codturno)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            5 as Ordine,
            dec_.strdenominazione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_comune dis_,
            de_comune dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codcom = dis_.codcom)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            6 as Ordine,
            dec_.strdenominazione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione, pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_provincia dis_,
            de_provincia dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codprovincia = dis_.codprovincia)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            7 as Ordine,
            dec_.strdenominazione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.CDNLAVORATORE
            FROM am_lav_patto_scelta ps_,pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_regione dis_,
            de_regione dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codregione = dis_.codregione)
            )
            UNION
            SELECT distinct PR_.PRGMANSIONE,
			2 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            8 as Ordine,
            dec_.strdenominazione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            null as FLGDISPAUTO, null as flgDispMoto, null as flgPendolarismo, null as NUMOREPERC, null as FLGMOBSETT,
            amp.cdnLavoratore
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_dis_stato dis_,
            de_comune dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codcom = dis_.codcom)
            )
            union
            SELECT distinct PR_.PRGMANSIONE,
			3 as RAGRUPPAMENTO,
            de_.strdescrizione as Mansione,
            9 as Ordine,
            dec_.strdescrizione as Vincolo,
            pr_.flgesperienza as Esperienza,
            pr_.flgDisponibile,
            pr_.flgDispFormazione,
            pr_.flgPIP,
            pr_.CODMONOTEMPO as Tempo,
            dis_.FLGDISPAUTO, dis_.flgDispMoto, dis_.flgPendolarismo, dis_.NUMOREPERC, dis_.FLGMOBSETT,
            amp.cdnLavoratore
            FROM am_lav_patto_scelta ps_,
            pr_mansione pr_,
            de_mansione de_,
            am_patto_lavoratore amp,
            pr_mobil_geogr dis_,
            de_trasferta dec_
            WHERE ( (pr_.prgmansione = ps_.strchiavetabella)
            AND (ps_.codlsttab = 'PR_MAN')
            AND (ps_.prgpattolavoratore = amp.prgPattoLavoratore)
            AND (amp.datfine is null)
            AND (de_.codmansione = pr_.codmansione)
            AND (pr_.prgmansione = dis_.prgmansione)
            AND (dec_.codtrasferta = dis_.codtrasferta)
            )
            order by 1, 2, 3, 4, 5
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_de_mansione_flgfrequente.sql
************************************************************************************** */


create or replace view vw_de_mansione_flgfrequente as
select codmansione, strdescrizione,codpadre,cdnlivello,flgfrequente,datinizioval, datfineval, flgido, flgmov
from de_mansione
where flgfrequente = 'S'
union
select codmansione, strdescrizione,codpadre,cdnlivello,flgfrequente,datinizioval, datfineval, flgido, flgmov
from de_mansione
where cdnlivello = 1
and codmansione in (select codpadre from de_mansione m1 where flgfrequente = 'S')
union
select codmansione, strdescrizione,codpadre,cdnlivello,flgfrequente,datinizioval, datfineval, flgido, flgmov
from de_mansione
where cdnlivello = 0
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_do_elenco_rosa_def.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_DO_ELENCO_ROSA_DEF AS
SELECT do_nominativo.cdnlavoratore, an_lavoratore.strcognome,
          an_lavoratore.strnome, do_richiesta_az.numrichiesta, do_richiesta_az.numrichiestaorig,
          do_richiesta_az.numanno
     FROM do_richiesta_az, do_incrocio, do_rosa, do_nominativo, an_lavoratore
    WHERE do_richiesta_az.prgrichiestaaz = do_incrocio.prgrichiestaaz
      AND do_incrocio.prgincrocio = do_rosa.prgincrocio
      AND do_rosa.prgrosa = do_nominativo.prgrosa
      AND do_nominativo.cdnlavoratore = an_lavoratore.cdnlavoratore
      AND do_rosa.prgtiporosa = 3
      AND do_nominativo.codtipocanc IS NULL
   MINUS
   (SELECT do_disponibilita.cdnlavoratore, an_lavoratore.strcognome,
           an_lavoratore.strnome, do_richiesta_az.numrichiesta, do_richiesta_az.numrichiestaorig,
           do_richiesta_az.numanno
      FROM do_richiesta_az, do_disponibilita, an_lavoratore
     WHERE do_richiesta_az.prgrichiestaaz = do_disponibilita.prgrichiestaaz
       AND do_disponibilita.cdnlavoratore = an_lavoratore.cdnlavoratore
       AND do_disponibilita.coddisponibilitarosa <> 'A')
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_do_rpt_pubbl_patenti.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_DO_RPT_PUBBL_PATENTI
(prgrichiestaaz, patentea, patenteb, patentec, patented, patentee, patentef)
AS
SELECT DISTINCT ab.prgRichiestaAz,
nvl((SELECT DISTINCT 'Sì' FROM DO_ABILITAZIONE_GEN ab1 WHERE ab1.codAbilitazioneGen = 'PGA' and ab1.prgRichiestaAz = ab.prgRichiestaAz), ' ') as PatenteA,
nvl((SELECT DISTINCT 'Sì' FROM DO_ABILITAZIONE_GEN ab1 WHERE ab1.codAbilitazioneGen = 'PGB' and ab1.prgRichiestaAz = ab.prgRichiestaAz), ' ') as PatenteB,
nvl((SELECT DISTINCT 'Sì' FROM DO_ABILITAZIONE_GEN ab1 WHERE ab1.codAbilitazioneGen = 'PGC' and ab1.prgRichiestaAz = ab.prgRichiestaAz), ' ') as PatenteC,
nvl((SELECT DISTINCT 'Sì' FROM DO_ABILITAZIONE_GEN ab1 WHERE ab1.codAbilitazioneGen = 'PGD' and ab1.prgRichiestaAz = ab.prgRichiestaAz), ' ') as PatenteD,
nvl((SELECT DISTINCT 'Sì' FROM DO_ABILITAZIONE_GEN ab1 WHERE ab1.codAbilitazioneGen = 'PGE' and ab1.prgRichiestaAz = ab.prgRichiestaAz), ' ') as PatenteE,
nvl((SELECT DISTINCT 'Sì' FROM DO_ABILITAZIONE_GEN ab1 WHERE ab1.codAbilitazioneGen = 'PGF' and ab1.prgRichiestaAz = ab.prgRichiestaAz), ' ') as PatenteE
FROM DO_ABILITAZIONE_GEN ab
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_do_storia_rosa.sql
************************************************************************************** */


create or replace view vw_do_storia_rosa as
select st.CDNLAVORATORE, max(st.DATINVIO) as dtmUltimaSegn,
	   (select count(*) from do_lav_storia_rosa strosa
	   	where strosa.CDNLAVORATORE=st.cdnLavoratore and strosa.DATINVIO is not null and strosa.PRGTIPOROSA=3
		) as nroSegnalazioni
from do_lav_storia_rosa st
where DATINVIO is not null and PRGTIPOROSA=3
GROUP BY CDNLAVORATORE
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_gettipoinf_o_sq.SQL
************************************************************************************** */


--VISTA UGUALE A VW_GETTIPOINF_O CON L'AGGIUNTA DELLA DESCRIZIONE QUALFICA (MANSIONE).
CREATE or REPLACE VIEW VW_GETTIPOINF_O_SQ AS SELECT am_movimento.cdnlavoratore AS cdnlavoratore,
          am_movimento.datiniziomov AS datdatainizio,
          am_movimento.datfinemoveffettiva AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'O' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno,
          am_movimento.prgazienda AS prgazienda,
          am_movimento.prgunita AS prgunitaaz, NULL AS descobiettivoazione,
          NULL AS descesito, NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             INITCAP (de_mv_tipo_mov.strdescrizione)
          || ' "'
          || de_tipo_contratto.strdescrizione
          || '"'
          || DECODE (am_movimento.codmonotempo, 'I', ' a TI', 'D', ' a TD')
		  || DECODE (am_movimento.codmansione,
                     NULL, '',
                     ' nella Mansione di '
                    )
          || de_mansione.strdescrizione
          || DECODE (an_azienda.strragionesociale, NULL, '', ' presso ')
          || an_azienda.strragionesociale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ' (C.F.: ')
          || an_azienda.strcodicefiscale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ')')
          || CASE
                WHEN am_movimento.codmvcessazione IS NOT NULL
                AND am_movimento.codtipomov = 'CES'
                   THEN ' per ' || de_mv_cessazione.strdescrizione
                ELSE ''
             END
          || DECODE (am_movimento.codtipodich, NULL, '', '; Sanato')
          || DECODE (de_orario.strdescrizione, NULL, '', ' Orario ')
          || de_orario.strdescrizione
          || CASE
                WHEN am_movimento.numggprevistiagr > 0
                AND am_movimento.codtipomov = 'AVV'
                   THEN    '  Giorni presunti in agric.: '
                        || am_movimento.numggprevistiagr
                ELSE ''
             END
          || CASE
                WHEN am_movimento.numggeffettuatiagr > 0
                AND am_movimento.codtipomov = 'CES'
                   THEN    '  Giorni effettivi in agric.: '
                        || am_movimento.numggeffettuatiagr
                ELSE ''
             END 
          || CASE
                WHEN  de_tipo_contratto.codmonotipo = 'A' 
                AND am_movimento.datfinepf IS NOT NULL
                   THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy') 
                ELSE ''
              END
          AS descrizionepercorso,
          am_movimento.prgmovimento AS chiavedettaglio,
          am_movimento.codstatoatto AS descstato, 'MOVIMENTI' AS strposdata,
          DECODE (am_movimento.datfinemoveffettiva,
                  NULL, SYSDATE,
                  am_movimento.datfinemoveffettiva
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_movimento,
          de_mv_tipo_mov,
          de_mv_cessazione,
          de_tipo_contratto,
          de_mansione,
          an_azienda,
          de_orario
    WHERE am_movimento.codtipomov = de_mv_tipo_mov.codtipomov
      AND am_movimento.codstatoatto = 'PR'
      AND am_movimento.codmonomovdich IN ('C', 'D')
      AND am_movimento.codmvcessazione = de_mv_cessazione.codmvcessazione(+)
      AND am_movimento.codtipocontratto = de_tipo_contratto.codtipocontratto(+)
      AND am_movimento.codmansione = de_mansione.codmansione(+)
      AND am_movimento.prgazienda = an_azienda.prgazienda(+)
      AND am_movimento.codorario = de_orario.codorario(+)
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_GETTIPO_INF_R.SQL
************************************************************************************** */


--VW_GETTIPO_INF_E_ENC    DIVENTE VW_GETTIPO_INF_R
CREATE OR REPLACE VIEW VW_GETTIPOINF_R AS SELECT am_documento.cdnlavoratore, am_cm_iscr.datdatainizio AS datdatainizio,
          am_cm_iscr.datdatafine AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'R' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          0 AS prgrichiestaaz, NULL AS numanno, NULL AS prgazienda,
          NULL AS prgunitaaz, NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'COLLOCAMENTO MIRATO: Data Inizio '
          || TO_CHAR (datdatainizio, 'dd/mm/yyyy')
          || ' Data Anzianità '
          || TO_CHAR (am_cm_iscr.datanzianita68, 'dd/mm/yyyy')
          || ' Tipo: '
          || DECODE (de_cm_tipo_iscr.codmonotiporagg,
                     'D', 'Disabili',
                     'A', 'Altre categorie protette'
                    )
          || '  Categoria: '
          || de_cm_tipo_iscr.strdescrizione AS descrizionepercorso,
          am_cm_iscr.prgcmiscr AS chiavedettaglio, NULL AS descstato,
          'STATUS' AS strposdata, sysdate as dataSort2,
          am_cm_iscr.cdnlavoratore AS cdnlavoratore_crypt
     FROM am_cm_iscr, de_cm_tipo_iscr, am_documento, am_documento_coll
    WHERE am_cm_iscr.codcmtipoiscr = de_cm_tipo_iscr.codcmtipoiscr
      AND am_cm_iscr.prgcmiscr = am_documento_coll.strchiavetabella
      AND am_documento.prgdocumento = am_documento_coll.prgdocumento
      AND am_documento.codtipodocumento = 'L68'
      AND am_documento.codstatoatto = 'PR' AND datdatafine IS NULL
	  AND am_documento_coll.CDNCOMPONENTE = (SELECT CDNCOMPONENTE from TS_COMPONENTE WHERE UPPER(STRPAGE) = 'CMISCRIZIONILAVORATOREPAGE')     
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_GETTIPO_INF_S.SQL
************************************************************************************** */


CREATE OR REPLACE VIEW VW_GETTIPOINF_S AS
SELECT am_documento.cdnlavoratore, cm_iscr_art1.datiscrlistaprov AS datdatainizio,
          cm_iscr_art1.datfine AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'S' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          0 AS prgrichiestaaz, NULL AS numanno, NULL AS prgazienda,
          NULL AS prgunitaaz, NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'LISTE SPECIALI: Data Iscrizione lista provinciale: '
          || TO_CHAR (cm_iscr_art1.datiscrlistaprov, 'dd/mm/yyyy') || ' Albo: ' || cm_iscr_art1.codtipolista
          AS descrizionepercorso,
          cm_iscr_art1.prgiscrart1 AS chiavedettaglio, NULL AS descstato,
          'STATUS' AS strposdata, sysdate as dataSort2,
          null AS cdnlavoratore_crypt
     FROM cm_iscr_art1, am_documento, am_documento_coll
    WHERE cm_iscr_art1.prgiscrart1 = am_documento_coll.strchiavetabella
      AND am_documento.prgdocumento = am_documento_coll.prgdocumento
      AND am_documento.codtipodocumento = 'ILS'
      AND am_documento.codstatoatto = 'PR'
      AND cm_iscr_art1.datfine IS NULL
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_gettipoing_b_nq.sql
************************************************************************************** */


--NON VISUALIZZA LA MANSIONE
CREATE  OR REPLACE VIEW VW_GETTIPOINF_B_NQ AS SELECT am_movimento.cdnlavoratore AS cdnlavoratore,
          am_movimento.datiniziomov AS datdatainizio,
          am_movimento.datfinemoveffettiva AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'B' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno,
          am_movimento.prgazienda AS prgazienda,
          am_movimento.prgunita AS prgunitaaz, NULL AS descobiettivoazione,
          NULL AS descesito, NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             INITCAP (de_mv_tipo_mov.strdescrizione)
          || ' "'
          || de_tipo_contratto.strdescrizione
          || '"'
          || DECODE (am_movimento.codmonotempo, 'I', ' a TI', 'D', ' a TD')
          || DECODE (an_azienda.strragionesociale, NULL, '', ' presso ')
          || an_azienda.strragionesociale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ' (C.F.: ')
          || an_azienda.strcodicefiscale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ')')
          || CASE
                WHEN am_movimento.codmvcessazione IS NOT NULL
                AND am_movimento.codtipomov = 'CES'
                   THEN ' per ' || de_mv_cessazione.strdescrizione
                ELSE ''
             END
          || DECODE (am_movimento.codtipodich, NULL, '', '; Sanato')
          || DECODE (de_orario.strdescrizione, NULL, '', ' Orario ')
          || de_orario.strdescrizione
          || CASE
                WHEN am_movimento.numggprevistiagr > 0
                AND am_movimento.codtipomov = 'AVV'
                   THEN    '  Giorni presunti in agric.: '
                        || am_movimento.numggprevistiagr
                ELSE ''
             END
          || CASE
                WHEN am_movimento.numggeffettuatiagr > 0
                AND am_movimento.codtipomov = 'CES'
                   THEN    '  Giorni effettivi in agric.: '
                        || am_movimento.numggeffettuatiagr
                ELSE ''
             END             
           || CASE
                WHEN  de_tipo_contratto.codmonotipo = 'A' 
                AND am_movimento.datfinepf IS NOT NULL
                   THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy') 
                ELSE ''
              END
           AS descrizionepercorso,
          am_movimento.prgmovimento AS chiavedettaglio,
          am_movimento.codstatoatto AS descstato, 'MOVIMENTI' AS strposdata,
          DECODE (am_movimento.datfinemoveffettiva,
                  NULL, SYSDATE,
                  am_movimento.datfinemoveffettiva
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_movimento,
          de_mv_tipo_mov,
          de_mv_cessazione,
          de_tipo_contratto,
          de_mansione,
          an_azienda,
          de_orario
    WHERE am_movimento.codtipomov = de_mv_tipo_mov.codtipomov
      AND am_movimento.codstatoatto = 'PR'
      AND am_movimento.codmonomovdich = 'O'
      AND am_movimento.codmvcessazione = de_mv_cessazione.codmvcessazione(+)
      AND am_movimento.codtipocontratto = de_tipo_contratto.codtipocontratto(+)
      AND am_movimento.codmansione = de_mansione.codmansione(+)
      AND am_movimento.prgazienda = an_azienda.prgazienda(+)
      AND am_movimento.codorario = de_orario.codorario(+)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_indirizzi_com_prov.sql
************************************************************************************** */


create or replace view vw_indirizzi_com_prov
(codcom, denominazionecomune, codcpi, strcapcom, codprovincia, denominazioneprov)
as
select DE_COMUNE.CODCOM, trim(DE_COMUNE.STRDENOMINAZIONE) , DE_COMUNE.CODCPI, DE_COMUNE.STRCAP, DE_PROVINCIA.CODPROVINCIA, trim(DE_PROVINCIA.STRISTAT)
from DE_COMUNE
left join de_PROVINCIA on (DE_COMUNE.CODPROVINCIA = de_PROVINCIA.CODPROVINCIA)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_last_am_cm_iscr.sql
************************************************************************************** */


create or replace view vw_last_am_cm_iscr
(prgcmiscr, cdnlavoratore, datdatainizio, datdatafine, codaccertsanitario, codtipoinvalidita)
as
select aa.PRGCMISCR,ai.CDNLAVORATORE, ai.datinizio, aa.DATDATAFINE, aa.CODACCERTSANITARIO, aa.CODTIPOINVALIDITA
from am_cm_iscr aa, 
(select max(DATDATAINIZIO) as datinizio, CDNLAVORATORE from am_cm_iscr group by CDNLAVORATORE) ai
where aa.CDNLAVORATORE=ai.CDNLAVORATORE and aa.DATDATAINIZIO=ai.datinizio
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_MATCH_NRO_FLAG_MAN_CM.sql
************************************************************************************** */


create or replace view vw_match_nro_flag_man_cm as
select man.cdnlavoratore, count(distinct t1.prgMansione) as nroFormazione , count(distinct t2.prgMansione) as nroEsperienze
from (
		select pr_mansione.prgMansione, pr_mansione.cdnlavoratore
		from pr_mansione
		where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='L') and pr_mansione.flgEspForm='S'
		) t1,
		(
		select pr_mansione.prgMansione, pr_mansione.cdnlavoratore
		from pr_mansione
		where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='L') and pr_mansione.flgEsperienza='S'
	    ) t2,
		pr_mansione man
where
	man.cdnLavoratore = t1.cdnLavoratore(+)
	and man.cdnLavoratore = t2.cdnLavoratore(+)
	group by man.cdnlavoratore     
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_MATCH_NRO_FLAG_MAN.sql
************************************************************************************** */


create or replace view vw_match_nro_flag_man as
select man.cdnlavoratore, to_char(count(distinct t1.prgMansione)) as nroFormazione , to_char(count(distinct t2.prgMansione)) as nroEsperienze
from (
		select pr_mansione.prgMansione, pr_mansione.cdnlavoratore
		from pr_mansione
		where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='P') and pr_mansione.flgEspForm='S'
		) t1,
		(
		select pr_mansione.prgMansione, pr_mansione.cdnlavoratore
		from pr_mansione
		where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='P') and pr_mansione.flgEsperienza='S'
	    ) t2,
		pr_mansione man
where
	man.cdnLavoratore = t1.cdnLavoratore(+)
	and man.cdnLavoratore = t2.cdnLavoratore(+)
	group by man.cdnlavoratore
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_match_nro_x_mansione_cm.sql
************************************************************************************** */


create or replace view vw_match_nro_x_mansione_CM as
select prm.cdnLavoratore, prm.prgMansione, prm.codMansione,
       (select count(1)
	   from pr_mansione a inner join pr_dis_turno b on (a.PRGMANSIONE=b.PRGMANSIONE)
	   where a.CDNLAVORATORE=prm.cdnlavoratore
	   		 and a.prgMansione=prm.prgMansione
			 and (a.FLGDISPONIBILE='S' OR a.FLGDISPONIBILE='L')
	   ) as nroTurni,
	   (select count(1)
	   from pr_mansione a inner join pr_dis_orario b on (a.PRGMANSIONE=b.PRGMANSIONE)
	   where a.CDNLAVORATORE=prm.cdnlavoratore
	   		 and a.prgMansione=prm.prgMansione
			 and (a.FLGDISPONIBILE='S' OR a.FLGDISPONIBILE='L')
	   ) as nroOrari,
	   (select count(1)
	   from pr_mansione a inner join pr_dis_contratto b on (a.PRGMANSIONE=b.PRGMANSIONE)
	   where a.CDNLAVORATORE=prm.cdnlavoratore
	   		 and a.prgMansione=prm.prgMansione
			 and (a.FLGDISPONIBILE='S' OR a.FLGDISPONIBILE='L')
	   ) as nroContratti
from pr_mansione prm
where
      (prm.FLGDISPONIBILE='S' OR prm.FLGDISPONIBILE='L')
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_match_nro_x_mansione.sql
************************************************************************************** */


create or replace view vw_match_nro_x_mansione as
select prm.cdnLavoratore, prm.prgMansione, prm.codMansione,
       (select count(1)
	   from pr_mansione a inner join pr_dis_turno b on (a.PRGMANSIONE=b.PRGMANSIONE)
	   where a.CDNLAVORATORE=prm.cdnlavoratore
	   		 and a.prgMansione=prm.prgMansione
			 and (a.FLGDISPONIBILE='S' OR a.FLGDISPONIBILE='P')
	   ) as nroTurni,
	   (select count(1)
	   from pr_mansione a inner join pr_dis_orario b on (a.PRGMANSIONE=b.PRGMANSIONE)
	   where a.CDNLAVORATORE=prm.cdnlavoratore
	   		 and a.prgMansione=prm.prgMansione
			 and (a.FLGDISPONIBILE='S' OR a.FLGDISPONIBILE='P')
	   ) as nroOrari,
	   (select count(1)
	   from pr_mansione a inner join pr_dis_contratto b on (a.PRGMANSIONE=b.PRGMANSIONE)
	   where a.CDNLAVORATORE=prm.cdnlavoratore
	   		 and a.prgMansione=prm.prgMansione
			 and (a.FLGDISPONIBILE='S' OR a.FLGDISPONIBILE='P')
	   ) as nroContratti
from pr_mansione prm
where
      (prm.FLGDISPONIBILE='S' OR prm.FLGDISPONIBILE='P')
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_mobilita_competenza.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_AM_MOBILITA_ISCR_COMPETENZA AS
select am_mobilita_iscr.prgmobilitaiscr, 
CASE
    WHEN (min(trunc(an_lav_storia_inf.datinizio)) <= trunc(am_mobilita_iscr.datinizio))
        THEN to_char(trunc(am_mobilita_iscr.datinizio), 'dd/mm/yyyy')
    ELSE to_char(min(trunc(an_lav_storia_inf.datinizio)), 'dd/mm/yyyy')
    END
dataInizioCompetenza,
CASE
    WHEN (max(trunc(nvl(an_lav_storia_inf.datfine, am_mobilita_iscr.datfine))) >= trunc(am_mobilita_iscr.datfine))
        THEN to_char(trunc(am_mobilita_iscr.datfine), 'dd/mm/yyyy')
    ELSE to_char(max(trunc(an_lav_storia_inf.datfine)), 'dd/mm/yyyy')
    END
dataFineCompetenza,
CASE
  WHEN (max(trunc(nvl(an_lav_storia_inf.datfine, am_mobilita_iscr.datfine))) < trunc(am_mobilita_iscr.datfine))
     THEN to_char(max(trunc(an_lav_storia_inf.dtmmod)), 'dd/mm/yyyy')
 END
dtmModCompetenza,
to_char(am_mobilita_iscr.datinizio, 'dd/mm/yyyy') dataInizioMobilita,
to_char(am_mobilita_iscr.datfine, 'dd/mm/yyyy') dataFineMobilita
from am_mobilita_iscr, an_lav_storia_inf, de_comune
where am_mobilita_iscr.cdnlavoratore = an_lav_storia_inf.cdnlavoratore
and an_lav_storia_inf.codcomdom = de_comune.codcom
and an_lav_storia_inf.codmonotipocpi = 'C'
and de_comune.codprovincia = (select codprovinciasil from ts_generale)
and (
(trunc(an_lav_storia_inf.datinizio) >= trunc(am_mobilita_iscr.datinizio) and 
trunc(an_lav_storia_inf.datinizio) <= trunc(am_mobilita_iscr.datfine)
)
or (trunc(an_lav_storia_inf.datinizio) < trunc(am_mobilita_iscr.datinizio) and
trunc(nvl(an_lav_storia_inf.datfine, am_mobilita_iscr.datinizio)) >= trunc(am_mobilita_iscr.datinizio)
)
)
and trunc(nvl(an_lav_storia_inf.datfine, am_mobilita_iscr.datinizio)) >= trunc(am_mobilita_iscr.datinizio)
group by am_mobilita_iscr.prgmobilitaiscr, am_mobilita_iscr.datinizio, am_mobilita_iscr.datfine
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_PERC_LAV_FULL_ICE2.SQL
************************************************************************************** */


--VW_PERC_LAV_FULL_ICE2
CREATE OR REPLACE VIEW VW_PERC_LAV_FULL_ICE2 AS
SELECT CASE
          WHEN lav.strposdata = 'STATUS'
             THEN    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
                  || DECODE (lav.datdatafine,
                             NULL, '',
                                '-'||CHR(38)||'gt;'
                             || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                            )
          WHEN lav.strposdata <> 'STATUS'
             THEN NULL
       END AS status,
       CASE
          WHEN lav.strposdata = 'SERVIZI'
             THEN    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
                  || DECODE (lav.datdatafine,
                             NULL, '',
                                '-'||CHR(38)||'gt;'
                             || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                            )
          WHEN lav.strposdata <> 'SERVIZI'
             THEN NULL
       END AS servizi,
       CASE
          WHEN lav.strposdata = 'MOVIMENTI'
             THEN    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
                  || DECODE (lav.datdatafine,
                             NULL, '',
                                '-'||CHR(38)||'gt;'
                             || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                            )
          WHEN lav.strposdata = 'MISSIONI'
             THEN    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
                  || DECODE (lav.datdatafine,
                             NULL, '',
                                '-'||CHR(38)||'gt;'
                             || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                            )
          ELSE NULL
       END AS movimenti,
       CASE
          WHEN lav.strposdata = 'MISSIONI'
             THEN    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
                  || DECODE (lav.datdatafine,
                             NULL, '',
                                '-'||CHR(38)||'gt;'
                             || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                            )
          WHEN lav.strposdata <> 'MISSIONI'
             THEN NULL
       END AS missioni,
       CASE
          WHEN lav.codmonotipoinf = 'A'
             THEN    'PAGE=PercorsoDispoLavDettaglioInformazioniStorichePage'||CHR(38)||'PRGDICHDISPONIBILITA='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'B'
             THEN    'PAGE=PercorsoMovimentiCollegatiPage'||CHR(38)||'PrgMovimentoColl='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'C'
             THEN    'PAGE=PercorsoStatoOccInfoStorDettPage'||CHR(38)||'prgStatoOccupaz='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'D'
             THEN    'PAGE=PercorsoTrasferimentiStoricoDettaglioPage'||CHR(38)||'PRGLAVSTORIAINF='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'E'
             THEN 	'PAGE=PercorsoMobilitaInfoStorDettPage'||CHR(38)||'prgMobilitaIscr='
                            || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'R'          
			 THEN	'PAGE=PercorsoCollMiratoInfStorDettPage'||CHR(38)||'prgCMIscr='
                            || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'F'
             THEN    'PAGE=PercorsoPattoLavDettaglioInformazioniStorichePage'||CHR(38)||'PRGPATTOLAVORATORE='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'G'
             THEN CASE
                    WHEN lav.prgrichiestaaz = 0
                       THEN    'PAGE=PercorsoDettaglioAppuntamentoPage'||CHR(38)||'PRGAPPUNTAMENTO='
                            || lav.chiavedettaglio
                    WHEN lav.prgrichiestaaz = 1
                       THEN    'PAGE=PercorsoDettaglioContattoPage'||CHR(38)||'PRGCONTATTO='
                            || lav.chiavedettaglio
                            || ''||CHR(38)||'codCpiContatto='
                            || lav.codcpi
                 END
          WHEN lav.codmonotipoinf = 'H'
             THEN    'PAGE=PercorsoColloquioPage'||CHR(38)||'prgColloquio='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'I'
             THEN    'PAGE=PercorsoPercorsiConcordatiPage'||CHR(38)||'prgPercorso='
                  || lav.chiavedettaglio
                  || DECODE (lav.prgrichiestaaz,
                             NULL, '',
                             ''||CHR(38)||'prgPattoLavoratore='
                            )
                  || lav.prgrichiestaaz
          WHEN lav.codmonotipoinf = 'L'
             THEN    'PAGE=IdoDettaglioSinteticoPage'||CHR(38)||'POPUP=1'||CHR(38)||'PRGRICHIESTAAZ='
                  || lav.prgrichiestaaz
          WHEN lav.codmonotipoinf = 'M'
             THEN    'PAGE=DichRedDettaglioPage'||CHR(38)||'POPUP=1'||CHR(38)||'PRGDICHLAV='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'N'
             THEN    'PAGE=IdoDettaglioSinteticoPage'||CHR(38)||'POPUP=1'||CHR(38)||'PRGRICHIESTAAZ='
                  || lav.prgrichiestaaz
          WHEN lav.codmonotipoinf = 'O'
             THEN    'PAGE=PercorsoMovimentiCollegatiPage'||CHR(38)||'PrgMovimentoColl='
                  || lav.chiavedettaglio
          WHEN lav.codmonotipoinf = 'P'
             THEN    'PAGE=IdoDettaglioSinteticoPage'||CHR(38)||'POPUP=1'||CHR(38)||'PRGRICHIESTAAZ='
                  || lav.prgrichiestaaz
          WHEN lav.codmonotipoinf = 'Q'
             THEN    'PAGE=ConsultaDatiMissionePage'||CHR(38)||'POPUP=1'||CHR(38)||'PRGMISSIONE='
                  || lav.chiavedettaglio
       END AS url,
       lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
       lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif, datdatainizio,
       datasort2, lav.cdnlavoratore_crypt
  FROM vw_percorso_lav lav, de_cpi
 WHERE lav.codcpi = de_cpi.codcpi(+)
   AND lav.codmonotipoinf IN
          ('A', 'B', 'O', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'L', 'N', 'M',
           'P', 'Q','R')
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_percorso_lav_modular.sql
************************************************************************************** */


--set define off;
--Prompt drop View VW_GETTIPOINF_A;
DROP VIEW VW_GETTIPOINF_A
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_A;
--
-- VW_GETTIPOINF_A  (View) 
--
--  Dependencies: 
--   AM_DICH_DISPONIBILITA (Table)
--   AM_ELENCO_ANAGRAFICO (Table)
--   DE_MOTIVO_FINE_ATTO (Table)
--   DE_STATO_ATTO (Table)
--   DE_TIPO_DICH_DISP (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_a (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT am_elenco_anagrafico.cdnlavoratore AS cdnlavoratore,
          am_dich_disponibilita.datdichiarazione AS datdatainizio,
          am_dich_disponibilita.datfine AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'A' AS codmonotipoinf,
          'Tipo Disponibilità' AS ldescvarie,
          de_tipo_dich_disp.strdescrizione AS descvarie,
          'Motivo fine' AS ldescvarie2,
          de_motivo_fine_atto.strdescrizione AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno, NULL AS prgazienda,
          NULL AS prgunitaaz, NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'Rilascio della DID'
          || DECODE (de_motivo_fine_atto.strdescrizione,
                     NULL, '',
                     '-> Motivo Fine: ' || de_motivo_fine_atto.strdescrizione
                    ) AS descrizionepercorso,
          am_dich_disponibilita.prgdichdisponibilita AS chiavedettaglio,
          de_stato_atto.strdescrizione AS descstato, 'STATUS' AS strposdata,
          DECODE (am_dich_disponibilita.datfine,
                  NULL, SYSDATE,
                  am_dich_disponibilita.datfine
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_dich_disponibilita,
          am_elenco_anagrafico,
          de_stato_atto,
          de_motivo_fine_atto,
          de_tipo_dich_disp
    WHERE am_dich_disponibilita.codstatoatto = 'PR'
      AND am_dich_disponibilita.prgelencoanagrafico =
                                      am_elenco_anagrafico.prgelencoanagrafico
      AND am_dich_disponibilita.codstatoatto = de_stato_atto.codstatoatto
      AND am_dich_disponibilita.codmotivofineatto = de_motivo_fine_atto.codmotivofineatto(+)
      AND am_dich_disponibilita.codtipodichdisp =
                                             de_tipo_dich_disp.codtipodichdisp
/

--Prompt drop View VW_GETTIPOINF_B;
DROP VIEW VW_GETTIPOINF_B
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_B;
--
-- VW_GETTIPOINF_B  (View) 
--
--  Dependencies: 
--   AM_MOVIMENTO (Table)
--   AN_AZIENDA (Table)
--   DE_MANSIONE (Table)
--   DE_MV_CESSAZIONE (Table)
--   DE_MV_TIPO_MOV (Table)
--   DE_ORARIO (Table)
--   DE_TIPO_CONTRATTO (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_b (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT am_movimento.cdnlavoratore AS cdnlavoratore,
          am_movimento.datiniziomov AS datdatainizio,
          am_movimento.datfinemoveffettiva AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'B' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno,
          am_movimento.prgazienda AS prgazienda,
          am_movimento.prgunita AS prgunitaaz, NULL AS descobiettivoazione,
          NULL AS descesito, NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             INITCAP (de_mv_tipo_mov.strdescrizione)
          || ' "'
          || de_tipo_contratto.strdescrizione
          || '"'
          || DECODE (am_movimento.codmonotempo, 'I', ' a TI', 'D', ' a TD')
          || DECODE (am_movimento.codmansione,
                     NULL, '',
                     ' nella Mansione di '
                    )
          || de_mansione.strdescrizione
          || DECODE (an_azienda.strragionesociale, NULL, '', ' presso ')
          || an_azienda.strragionesociale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ' (C.F.: ')
          || an_azienda.strcodicefiscale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ')')
          || CASE
                WHEN am_movimento.codmvcessazione IS NOT NULL
                AND am_movimento.codtipomov = 'CES'
                   THEN ' per ' || de_mv_cessazione.strdescrizione
                ELSE ''
             END
          || DECODE (am_movimento.codtipodich, NULL, '', '; Sanato')
          || DECODE (de_orario.strdescrizione, NULL, '', ' Orario ')
          || de_orario.strdescrizione
          || CASE
                WHEN am_movimento.numggprevistiagr > 0
                AND am_movimento.codtipomov = 'AVV'
                   THEN    '  Giorni presunti in agric.: '
                        || am_movimento.numggprevistiagr
                ELSE ''
             END
          || CASE
                WHEN am_movimento.numggeffettuatiagr > 0
                AND am_movimento.codtipomov = 'CES'
                   THEN    '  Giorni effettivi in agric.: '
                        || am_movimento.numggeffettuatiagr
                ELSE ''
             END 
          || CASE
                WHEN  de_tipo_contratto.codmonotipo = 'A' 
                AND am_movimento.datfinepf IS NOT NULL
                   THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy') 
                ELSE ''
              END
            AS descrizionepercorso,   
          am_movimento.prgmovimento AS chiavedettaglio,
          am_movimento.codstatoatto AS descstato, 'MOVIMENTI' AS strposdata,
          DECODE (am_movimento.datfinemoveffettiva,
                  NULL, SYSDATE,
                  am_movimento.datfinemoveffettiva
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_movimento,
          de_mv_tipo_mov,
          de_mv_cessazione,
          de_tipo_contratto,
          de_mansione,
          an_azienda,
          de_orario
    WHERE am_movimento.codtipomov = de_mv_tipo_mov.codtipomov
      AND am_movimento.codstatoatto = 'PR'
      AND am_movimento.codmonomovdich = 'O'
      AND am_movimento.codmvcessazione = de_mv_cessazione.codmvcessazione(+)
      AND am_movimento.codtipocontratto = de_tipo_contratto.codtipocontratto(+)
      AND am_movimento.codmansione = de_mansione.codmansione(+)
      AND am_movimento.prgazienda = an_azienda.prgazienda(+)
      AND am_movimento.codorario = de_orario.codorario(+)
/

--Prompt drop View VW_GETTIPOINF_C;
DROP VIEW VW_GETTIPOINF_C
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_C;
--
-- VW_GETTIPOINF_C  (View) 
--
--  Dependencies: 
--   AM_STATO_OCCUPAZ (Table)
--   DE_STATO_OCCUPAZ (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_c (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT am_stato_occupaz.cdnlavoratore AS cdnlavoratore,
          am_stato_occupaz.datinizio AS datdatainizio,
          am_stato_occupaz.datfine AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'C' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno, NULL AS prgazienda,
          NULL AS prgunitaaz, NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          de_stato_occupaz.strdescrizione AS descstatooccupaz,
          NULL AS descstatooccupazorig,
             'Acquisizione stato occupazionale '
          || de_stato_occupaz.strdescrizione
          || ' proveniente da '
          || DECODE (am_stato_occupaz.codmonoprovenienza,
                     'A', 'REG. ANAG.',
                     'D', 'D.I.D.',
                     'M', 'MOVIMENTI',
                     'T', 'TRASFERIMENTO COMP.',
                     'P', 'PORTING',
                     'G', 'Agg. manuale',
                     'O', 'Reg./Agg. manuale',
                     'N', 'INS. MANUALE',
                     'B', 'MOBILITA'''
                    ) AS descrizionepercorso,
          am_stato_occupaz.prgstatooccupaz AS chiavedettaglio,
          NULL AS descstato, 'STATUS' AS strposdata,
          DECODE (am_stato_occupaz.datfine,
                  NULL, SYSDATE,
                  am_stato_occupaz.datfine
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_stato_occupaz, de_stato_occupaz
    WHERE am_stato_occupaz.codstatooccupaz = de_stato_occupaz.codstatooccupaz
/

--Prompt drop View VW_GETTIPOINF_D;
DROP VIEW VW_GETTIPOINF_D
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_D;
--
-- VW_GETTIPOINF_D  (View) 
--
--  Dependencies: 
--   AN_LAV_STORIA_INF (Table)
--   DE_CPI (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_d (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT an_lav_storia_inf.cdnlavoratore AS cdnlavoratore,
          an_lav_storia_inf.dattrasferimento AS datdatainizio,
          an_lav_storia_inf.datfine AS datdatafine,
          an_lav_storia_inf.codcpiorig AS codcpi,
          an_lav_storia_inf.codcpitit AS codcpidest, 'D' AS codmonotipoinf,
          NULL AS ldescvarie, NULL AS descvarie, NULL AS ldescvarie2,
          NULL AS descvarie2, NULL AS prgrichiestaaz, NULL AS numanno,
          NULL AS prgazienda, NULL AS prgunitaaz, NULL AS descobiettivoazione,
          NULL AS descesito, NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'Trasferimento dal CPI di '
          || CASE
                WHEN an_lav_storia_inf.codmonotipoorig = 'V'
                   THEN cpidaval.strdescrizione
                ELSE DECODE (an_lav_storia_inf.codmonotipocpi,
                             'C', INITCAP (cpiorig.strdescrizione),
                             'E', INITCAP (cpiorig.strdescrizione),
                             'T', INITCAP (cpitit.strdescrizione),
                             'CPI TITOLOARE NON PERVENUTO'
                            )
             END
          || ' al CPI di '
          || DECODE (an_lav_storia_inf.codmonotipocpi,
                     'C', INITCAP (cpitit.strdescrizione),
                     'E', INITCAP (cpitit.strdescrizione),
                     'T', INITCAP (cpiorig.strdescrizione),
                     'CPI COMPETENTE NON PERVENUTO'
                    ) AS descrizionepercorso,
          an_lav_storia_inf.prglavstoriainf AS chiavedettaglio,
          NULL AS descstato, 'STATUS' AS strposdata,
          DECODE (an_lav_storia_inf.datfine,
                  NULL, SYSDATE,
                  an_lav_storia_inf.datfine
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM an_lav_storia_inf, de_cpi cpiorig, de_cpi cpitit, de_cpi cpidaval
    WHERE an_lav_storia_inf.dattrasferimento IS NOT NULL
      AND an_lav_storia_inf.codcpiorig = cpiorig.codcpi(+)
      AND an_lav_storia_inf.codcpitit = cpitit.codcpi(+)
      AND an_lav_storia_inf.codcpiorigprec = cpidaval.codcpi(+)
/

--Prompt drop View VW_GETTIPOINF_E_ENC;
DROP VIEW VW_GETTIPOINF_E_ENC
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_E_ENC;
--
-- VW_GETTIPOINF_E_ENC  (View) 
--
--  Dependencies: 
--   AM_CM_ISCR (Table)
--   AM_DOCUMENTO (Table)
--   AM_DOCUMENTO_COLL (Table)
--   DE_CM_TIPO_ISCR (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_e_enc (cdnlavoratore,
                                                  datdatainizio,
                                                  datdatafine,
                                                  codcpi,
                                                  codcpidest,
                                                  codmonotipoinf,
                                                  ldescvarie,
                                                  descvarie,
                                                  ldescvarie2,
                                                  descvarie2,
                                                  prgrichiestaaz,
                                                  numanno,
                                                  prgazienda,
                                                  prgunitaaz,
                                                  descobiettivoazione,
                                                  descesito,
                                                  descservizio,
                                                  desctipopatto,
                                                  descstatooccupaz,
                                                  descstatooccupazorig,
                                                  descrizionepercorso,
                                                  chiavedettaglio,
                                                  descstato,
                                                  strposdata,
                                                  datasort2,
                                                  cdnlavoratore_crypt
                                                 )
AS
   SELECT 0 AS cdnlavoratore, am_cm_iscr.datdatainizio AS datdatainizio,
          am_cm_iscr.datdatafine AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'E' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          0 AS prgrichiestaaz, NULL AS numanno, NULL AS prgazienda,
          NULL AS prgunitaaz, NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'COLLOCAMENTO MIRATO: Data Inizio '
          || TO_CHAR (datdatainizio, 'dd/mm/yyyy')
          || ' Data Anzianità '
          || TO_CHAR (am_cm_iscr.datanzianita68, 'dd/mm/yyyy')
          || ' Tipo: '
          || DECODE (de_cm_tipo_iscr.codmonotiporagg,
                     'D', 'Disabili',
                     'A', 'Altre categorie protette'
                    )
          || '  Categoria: '
          || de_cm_tipo_iscr.strdescrizione AS descrizionepercorso,
          am_cm_iscr.prgcmiscr AS chiavedettaglio, NULL AS descstato,
          'STATUS' AS strposdata, sysdate as dataSort2,
          am_cm_iscr.cdnlavoratore AS cdnlavoratore_crypt
     FROM am_cm_iscr, de_cm_tipo_iscr, am_documento, am_documento_coll
    WHERE am_cm_iscr.codcmtipoiscr = de_cm_tipo_iscr.codcmtipoiscr
      AND am_cm_iscr.prgcmiscr = am_documento_coll.strchiavetabella
      AND am_documento.prgdocumento = am_documento_coll.prgdocumento
      AND am_documento.codtipodocumento = 'L68'
      AND am_documento.codstatoatto = 'PR'
	  AND am_documento_coll.CDNCOMPONENTE = (SELECT CDNCOMPONENTE from TS_COMPONENTE WHERE UPPER(STRPAGE) = 'CMISCRIZIONILAVORATOREPAGE')
      AND datdatafine IS NULL
/

--Prompt drop View VW_GETTIPOINF_E_UNI;
DROP VIEW VW_GETTIPOINF_E_UNI
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_E_UNI;
--
-- VW_GETTIPOINF_E_UNI  (View) 
--
--  Dependencies: 
--   AM_MOBILITA_ISCR (Table)
--   DE_MB_TIPO (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_e_uni (cdnlavoratore,
                                                  datdatainizio,
                                                  datdatafine,
                                                  codcpi,
                                                  codcpidest,
                                                  codmonotipoinf,
                                                  ldescvarie,
                                                  descvarie,
                                                  ldescvarie2,
                                                  descvarie2,
                                                  prgrichiestaaz,
                                                  numanno,
                                                  prgazienda,
                                                  prgunitaaz,
                                                  descobiettivoazione,
                                                  descesito,
                                                  descservizio,
                                                  desctipopatto,
                                                  descstatooccupaz,
                                                  descstatooccupazorig,
                                                  descrizionepercorso,
                                                  chiavedettaglio,
                                                  descstato,
                                                  strposdata,
                                                  datasort2,
                                                  cdnlavoratore_crypt
                                                 )
AS
   SELECT am_mobilita_iscr.cdnlavoratore AS cdnlavoratore,
          am_mobilita_iscr.datinizio AS datdatainizio,
          am_mobilita_iscr.datfine AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'E' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          1 AS prgrichiestaaz, NULL AS numanno, NULL AS prgazienda,
          NULL AS prgunitaaz, NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
          'MOBILITA''' AS descrizionepercorso,
          am_mobilita_iscr.prgmobilitaiscr AS chiavedettaglio,
          NULL AS descstato, 'STATUS' AS strposdata,
          DECODE (am_mobilita_iscr.datfine,
                  NULL, SYSDATE,
                  am_mobilita_iscr.datfine
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_mobilita_iscr, de_mb_tipo
    WHERE am_mobilita_iscr.codtipomob = de_mb_tipo.codmbtipo
/

--Prompt drop View VW_GETTIPOINF_F;
DROP VIEW VW_GETTIPOINF_F
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_F;
--
-- VW_GETTIPOINF_F  (View) 
--
--  Dependencies: 
--   AM_PATTO_LAVORATORE (Table)
--   DE_SERVIZIO (Table)
--   DE_TIPO_PATTO (Table)
--
-- Modificata 06/11/2019 Riccardi
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_f (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT am_patto_lavoratore.cdnlavoratore AS cdnlavoratore,
          am_patto_lavoratore.datstipula AS datdatainizio,
          am_patto_lavoratore.datfine AS datdatafine,
          am_patto_lavoratore.codcpi AS codcpi, NULL AS codcpidest,
          'F' AS codmonotipoinf, NULL AS ldescvarie, NULL AS descvarie,
          NULL AS ldescvarie2, NULL AS descvarie2, NULL AS prgrichiestaaz,
          NULL AS numanno, NULL AS prgazienda, NULL AS prgunitaaz,
          NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, de_tipo_patto.strdescrizione AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
          (case
          when am_patto_lavoratore.flgpatto297 = 'S' then 'Stipula del patto di servizio 150'
                else 'Accordo generico'
          end)
              || (case
          when am_patto_lavoratore.codtipopatto = 'ANP' then 
			' con i programmi: ' || getProgrammiPattoPercorsoLav (am_patto_lavoratore.prgpattolavoratore)
		  else ' con la misura ' || DE_TIPO_PATTO.strdescrizione
          end) AS descrizionepercorso,		  
          am_patto_lavoratore.prgpattolavoratore AS chiavedettaglio,
          NULL AS descstato, 'SERVIZI' AS strposdata,
          DECODE (am_patto_lavoratore.datfine,
                  NULL, SYSDATE,
                  am_patto_lavoratore.datfine
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_patto_lavoratore
     INNER JOIN de_tipo_patto on am_patto_lavoratore.codtipopatto = de_tipo_patto.codtipopatto
    WHERE am_patto_lavoratore.codstatoatto = 'PR'
/

--Prompt drop View VW_GETTIPOINF_G;
DROP VIEW VW_GETTIPOINF_G
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_G;
--
-- VW_GETTIPOINF_G  (View) 
--
--  Dependencies: 
--   AG_AGENDA (Table)
--   AG_CONTATTO (Table)
--   AG_LAVORATORE (Table)
--   AN_SPI (Table)
--   DE_CPI (Table)
--   DE_DISPONIBILITA_ROSA (Table)
--   DE_EFFETTO_CONTATTO (Table)
--   DE_ESITO_APPUNT (Table)
--   DE_MOTIVO_CONTATTOAG (Table)
--   DE_SERVIZIO (Table)
--   DE_STATO_APPUNTAMENTO (Table)
--   DE_TIPO_CONTATTOAG (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_g (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT ag_lavoratore.cdnlavoratore AS cdnlavoratore,
          TRUNC (ag_agenda.dtmdataora) AS datdatainizio, NULL AS datdatafine,
          ag_agenda.codcpi AS codcpi, NULL AS codcpidest,
          'G' AS codmonotipoinf, NULL AS ldescvarie, NULL AS descvarie,
          NULL AS ldescvarie2, NULL AS descvarie2, 0 AS prgrichiestaaz,
          NULL AS numanno, NULL AS prgazienda, NULL AS prgunitaaz,
          NULL AS descobiettivoazione,
          de_esito_appunt.strdescrizione AS descesito,
          de_servizio.strdescrizione AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'L''appuntamento col Servizio '
          || de_servizio.strdescrizione
          || ' del CPI di '
          || de_cpi.strdescrizione
          || DECODE (de_stato_appuntamento.strdescrizione, NULL, '', ' e'' ')
          || LOWER (de_stato_appuntamento.strdescrizione)
                                                       AS descrizionepercorso,
          ag_agenda.prgappuntamento AS chiavedettaglio,
          de_stato_appuntamento.strdescrizione AS descstato,
          'SERVIZI' AS strposdata, sysdate as dataSort2,
          NULL AS cdnlavoratore_crypt
     FROM ag_agenda,
          de_servizio,
          ag_lavoratore,
          de_esito_appunt,
          de_stato_appuntamento,
          de_cpi
    WHERE ag_agenda.codcpi = ag_lavoratore.codcpi
      AND ag_agenda.prgappuntamento = ag_lavoratore.prgappuntamento
      AND ag_agenda.codservizio = de_servizio.codservizio(+)
      AND ag_agenda.codesitoappunt = de_esito_appunt.codesitoappunt(+)
      AND ag_agenda.codstatoappuntamento = de_stato_appuntamento.codstatoappuntamento(+)
      AND ag_agenda.codcpi = de_cpi.codcpi
   UNION
   SELECT ag_contatto.cdnlavoratore AS cdnlavoratore,
          ag_contatto.datcontatto AS datdatainizio, NULL AS datdatafine,
          ag_contatto.codcpicontatto AS codcpi, NULL AS codcpidest,
          'G' AS codmonotipoinf, NULL AS ldescvarie, NULL AS descvarie,
          NULL AS ldescvarie2, NULL AS descvarie2, 1 AS prgrichiestaaz,
          NULL AS numanno, NULL AS prgazienda, NULL AS prgunitaaz,
          NULL AS descobiettivoazione,
          de_effetto_contatto.strdescrizione AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'CONTATTO'
          || DECODE (de_tipo_contattoag.strdescrizione, NULL, '', '; Tipo: ')
          || de_tipo_contattoag.strdescrizione
          || '-'
          || DECODE (ag_contatto.strio, 'I', 'Al CpI', 'O', 'Dal CpI')
          || DECODE (de_motivo_contattoag.strdescrizione,
                     NULL, '',
                     '; Motivo: '
                    )
          || de_motivo_contattoag.strdescrizione
          || DECODE (de_effetto_contatto.strdescrizione,
                     NULL, '',
                     '; Effetto: '
                    )
          || de_effetto_contatto.strdescrizione
          || DECODE (de_disponibilita_rosa.strdescrizione,
                     NULL, '',
                     '; Disp. Rosa: '
                    )
          || de_disponibilita_rosa.strdescrizione
          || DECODE (ag_contatto.txtcontatto, NULL, '', '; Note: ')
          || SUBSTR (ag_contatto.txtcontatto, 1, 25)
          || DECODE (ag_contatto.txtcontatto, NULL, '', '...')
                                                       AS descrizionepercorso,
          ag_contatto.prgcontatto AS chiavedettaglio, NULL AS descstato,
          'SERVIZI' AS strposdata, sysdate as dataSort2,
          NULL AS cdnlavoratore_crypt
     FROM ag_contatto,
          an_spi,
          de_disponibilita_rosa,
          de_effetto_contatto,
          de_tipo_contattoag,
          de_motivo_contattoag
    WHERE ag_contatto.prgspicontatto = an_spi.prgspi(+)
      AND ag_contatto.coddisponibilitarosa = de_disponibilita_rosa.coddisponibilitarosa(+)
      AND ag_contatto.prgeffettocontatto = de_effetto_contatto.prgeffettocontatto(+)
      AND ag_contatto.prgtipocontatto = de_tipo_contattoag.prgtipocontatto(+)
      AND ag_contatto.prgmotcontatto = de_motivo_contattoag.prgmotcontatto(+)
/

--Prompt drop View VW_GETTIPOINF_H;
DROP VIEW VW_GETTIPOINF_H
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_H;
--
-- VW_GETTIPOINF_H  (View) 
--
--  Dependencies: 
--   DE_SERVIZIO (Table)
--   OR_COLLOQUIO (Table)
--   OR_PERCORSO_CONCORDATO (Table)
--   OR_SCHEDA_COLLOQUIO (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_h (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT or_colloquio.cdnlavoratore AS cdnlavoratore,
          or_colloquio.datcolloquio AS datdatainizio, NULL AS datdatafine,
          or_colloquio.codcpi AS codcpi, NULL AS codcpidest,
          'H' AS codmonotipoinf, NULL AS ldescvarie, NULL AS descvarie,
          NULL AS ldescvarie2, NULL AS descvarie2, NULL AS prgrichiestaaz,
          NULL AS numanno, NULL AS prgazienda, NULL AS prgunitaaz,
          NULL AS descobiettivoazione, NULL AS descesito,
          de_servizio.strdescrizione AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'Effettua colloquio con il Servizio di '
          || de_servizio.strdescrizione
          || DECODE (or_percorso_concordato.prgpercorso,
                     NULL, ' non concordando azioni',
                     ' concordando azioni'
                    ) AS descrizionepercorso,
          or_colloquio.prgcolloquio AS chiavedettaglio, NULL AS descstato,
          'SERVIZI' AS strposdata, sysdate as dataSort2,
          NULL AS cdnlavoratore_crypt
     FROM or_colloquio,
		  OR_PERCORSO_CONCORDATO,
		  de_servizio, or_scheda_colloquio
    WHERE or_colloquio.prgcolloquio = or_percorso_concordato.prgcolloquio(+) and
		  or_colloquio.codservizio = de_servizio.codservizio (+) and
		  or_colloquio.prgcolloquio = or_scheda_colloquio.prgcolloquio(+)
/

--Prompt drop View VW_GETTIPOINF_I;
DROP VIEW VW_GETTIPOINF_I
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_I;
--
-- VW_GETTIPOINF_I  (View) 
--
--  Dependencies: 
--   AM_LAV_PATTO_SCELTA (Table)
--   AM_PATTO_LAVORATORE (Table)
--   DE_AZIONE (Table)
--   DE_AZIONE_RAGG (Table)
--   DE_ESITO (Table)
--   OR_COLLOQUIO (Table)
--   OR_PERCORSO_CONCORDATO (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_i (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT or_colloquio.cdnlavoratore AS cdnlavoratore,
          or_percorso_concordato.datstimata AS datdatainizio,
          NULL AS datdatafine, or_colloquio.codcpi AS codcpi,
          NULL AS codcpidest, 'I' AS codmonotipoinf,
          'Data effettiva' AS ldescvarie,
          TO_CHAR (or_percorso_concordato.dateffettiva,
                   'dd/mm/yyyy'
                  ) AS descvarie,
          NULL AS ldescvarie2, NULL AS descvarie2,
          am_patto_lavoratore.prgpattolavoratore AS prgrichiestaaz, NULL AS numanno,
          NULL AS prgazienda, NULL AS prgunitaaz,
          de_azione_ragg.strdescrizione AS descobiettivoazione,
          de_esito.strdescrizione AS descesito, NULL AS descservizio,
          NULL AS desctipopatto, NULL AS descstatooccupaz,
          NULL AS descstatooccupazorig,
             'Concorda '
          || de_azione.strdescrizione
          || ' con Obiettivo di '
          || de_azione_ragg.strdescrizione
          || ', effettuata il '
          || NVL (TO_CHAR (or_percorso_concordato.dateffettiva, 'dd/mm/yyyy'),
                  'Non Tracciata'
                 )
          || DECODE (or_percorso_concordato.codesito, NULL, '', '. Azione ')
          || de_esito.strdescrizione AS descrizionepercorso,
          or_percorso_concordato.prgpercorso AS chiavedettaglio,
          NULL AS descstato, 'SERVIZI' AS strposdata, sysdate as dataSort2,
          NULL AS cdnlavoratore_crypt
     FROM or_percorso_concordato,
          de_azione,
          or_colloquio,
          de_esito,
          de_azione_ragg, am_patto_lavoratore, am_lav_patto_scelta
    WHERE am_patto_lavoratore.CDNLAVORATORE = or_colloquio.CDNLAVORATORE
		 AND am_patto_lavoratore.PRGPATTOLAVORATORE =  am_lav_patto_scelta.PRGPATTOLAVORATORE
		 AND am_patto_lavoratore.CODSTATOATTO = 'PR'
		 AND am_lav_patto_scelta.codlsttab = 'OR_PER'
		 AND or_percorso_concordato.PRGAZIONI = de_azione.PRGAZIONI
		 AND or_colloquio.PRGCOLLOQUIO = or_percorso_concordato.PRGCOLLOQUIO
		 AND (or_percorso_concordato.PRGPERCORSO (+)= to_number(am_lav_patto_scelta.strchiavetabella))
		 AND or_percorso_concordato.CODESITO = de_esito.CODESITO (+)
		 AND de_azione.PRGAZIONERAGG = de_azione_ragg.PRGAZIONIRAGG
/

--Prompt drop View VW_GETTIPOINF_L;
DROP VIEW VW_GETTIPOINF_L
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_L;
--
-- VW_GETTIPOINF_L  (View) 
--
--  Dependencies: 
--   AN_LAVORATORE (Table)
--   AN_UNITA_AZIENDA (Table)
--   DE_MANSIONE (Table)
--   DE_TIPO_INCROCIO (Table)
--   DO_ALTERNATIVA (Table)
--   DO_ESITO_CANDIDATO (Table)
--   DO_INCROCIO (Table)
--   DO_LAV_STORIA_ROSA (Table)
--   DO_MANSIONE (Table)
--   DO_RICHIESTA_AZ (Table)
--   DO_ROSA (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_l (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT an_lavoratore.cdnlavoratore AS cdnlavoratore,
		  NVL(do_lav_storia_rosa.datinvio, TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')) as datdatainizio,
		  NULL AS datdatafine,
          do_richiesta_az.codcpi AS codcpi, NULL AS codcpidest,
          'L' AS codmonotipoinf, NULL AS ldescvarie, NULL AS descvarie,
          NULL AS ldescvarie2, NULL AS descvarie2,
          do_incrocio.prgrichiestaaz AS prgrichiestaaz,
             TO_CHAR (do_richiesta_az.numrichiesta)
          || '/'
          || TO_CHAR (do_richiesta_az.numanno) AS numanno,
          do_richiesta_az.prgazienda AS prgazienda,
          do_richiesta_az.prgunita AS prgunitaaz, NULL AS descobiettivoazione,
          do_esito_candidato.codesitodaazienda AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'Segnalazione nella rosa n. '
          || TO_CHAR (do_richiesta_az.numrichiesta)
          || '/'
          || TO_CHAR (do_richiesta_az.numanno)
          || ' (p. '
          || TO_CHAR (do_rosa.prgrosa)
          || ')'
          || ' per la Mansione di '
          || de_mansione.strdescrizione
          || '; Tipo Incrocio: '
          || NVL (tincrocio.strdescrizione, 'Non Tracciato')
                                                       AS descrizionepercorso,
          do_rosa.prgrosa AS chiavedettaglio,
          'MANSIONE:' || de_mansione.strdescrizione AS descstato,
          'SERVIZI' AS strposdata, sysdate as dataSort2,
          NULL AS cdnlavoratore_crypt
     FROM do_lav_storia_rosa,
          an_lavoratore,
          an_unita_azienda,
          do_rosa,
          do_incrocio,
          do_alternativa,
          do_mansione,
          de_mansione,
          de_tipo_incrocio tincrocio,
          do_richiesta_az,
          do_esito_candidato
    WHERE do_lav_storia_rosa.cdnlavoratore = an_lavoratore.cdnlavoratore
      AND do_lav_storia_rosa.prgazienda = an_unita_azienda.prgazienda
      AND do_lav_storia_rosa.prgunita = an_unita_azienda.prgunita
      AND do_lav_storia_rosa.prgrosa = do_rosa.prgrosa
      AND do_rosa.prgincrocio = do_incrocio.prgincrocio
      AND (    do_incrocio.prgrichiestaaz = do_alternativa.prgrichiestaaz
           AND NVL (do_incrocio.prgalternativa, do_alternativa.prgalternativa) =
                                                 do_alternativa.prgalternativa
          )
      AND do_alternativa.prgrichiestaaz = do_mansione.prgrichiestaaz
      AND do_alternativa.prgalternativa = do_mansione.prgalternativa
      AND do_mansione.codmansione = de_mansione.codmansione
      AND do_lav_storia_rosa.datinvio IS NOT NULL
      AND do_rosa.prgrosafiglia IS NULL
      AND do_lav_storia_rosa.prgrichiesta = do_richiesta_az.prgrichiestaaz
      AND do_incrocio.prgtipoincrocio = tincrocio.prgtipoincrocio
      AND do_esito_candidato.prgrichiestaaz(+) = do_lav_storia_rosa.prgrichiesta
      AND do_esito_candidato.cdnlavoratore(+) = do_lav_storia_rosa.cdnlavoratore
/

--Prompt drop View VW_GETTIPOINF_M;
DROP VIEW VW_GETTIPOINF_M
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_M;
--
-- VW_GETTIPOINF_M  (View) 
--
--  Dependencies: 
--   AM_DICH_LAV (Table)
--   DE_STATO_ATTO (Table)
--   DE_TIPO_DICH (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_m (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT am_dich_lav.cdnlavoratore AS cdnlavoratore,
          am_dich_lav.datinizio AS datdatainizio,
          am_dich_lav.datfine AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'M' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno, NULL AS prgazienda,
          NULL AS prgunitaaz, NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'DICH. REDDITO; Tipo dich.:'
          || de_tipo_dich.strdescrizione
          || '; Stato Atto: '
          || de_stato_atto.strdescrizione AS descrizionepercorso,
          am_dich_lav.prgdichlav AS chiavedettaglio, NULL AS descstato,
          'SERVIZI' AS strposdata,
          DECODE (am_dich_lav.datfine,
                  NULL, SYSDATE,
                  am_dich_lav.datfine
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_dich_lav, de_tipo_dich, de_stato_atto
    WHERE am_dich_lav.codtipodich = de_tipo_dich.codtipodich
      AND am_dich_lav.codstatoatto = de_stato_atto.codstatoatto
      AND (   de_stato_atto.codstatoatto = 'PR'
           OR (    de_stato_atto.codstatoatto = 'AN'
               AND am_dich_lav.codmotivo <> 'ERR'
              )
          )
/

--Prompt drop View VW_GETTIPOINF_N;
DROP VIEW VW_GETTIPOINF_N
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_N;
--
-- VW_GETTIPOINF_N  (View) 
--
--  Dependencies: 
--   AN_LAVORATORE (Table)
--   AN_UNITA_AZIENDA (Table)
--   DE_DISPONIBILITA_ROSA (Table)
--   DE_MANSIONE (Table)
--   DE_TIPO_INCROCIO (Table)
--   DO_ALTERNATIVA (Table)
--   DO_ESITO_CANDIDATO (Table)
--   DO_INCROCIO (Table)
--   DO_LAV_STORIA_ROSA (Table)
--   DO_MANSIONE (Table)
--   DO_NOMINATIVO (Table)
--   DO_RICHIESTA_AZ (Table)
--   DO_ROSA (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_n (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT an_lavoratore.cdnlavoratore AS cdnlavoratore,
          TRUNC (NVL (do_lav_storia_rosa.dtmcanc,
                      do_lav_storia_rosa.datdisponibilita
                     )
                ) AS datdatainizio,
          NULL AS datdatafine, do_richiesta_az.codcpi AS codcpi,
          NULL AS codcpidest, 'N' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          do_incrocio.prgrichiestaaz AS prgrichiestaaz,
             TO_CHAR (do_richiesta_az.numrichiesta)
          || '/'
          || TO_CHAR (do_richiesta_az.numanno) AS numanno,
          do_richiesta_az.prgazienda AS prgazienda,
          do_richiesta_az.prgunita AS prgunitaaz, NULL AS descobiettivoazione,
          NULL AS descesito, NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'ESCLUSIONE DA ROSE; '
          || 'RICH. N.: '
          || TO_CHAR (do_richiesta_az.numrichiesta)
          || '/'
          || TO_CHAR (do_richiesta_az.numanno)
          || '; P. rosa: '
          || TO_CHAR (do_rosa.prgrosa)
          || '; MANSIONE:'
          || de_mansione.strdescrizione
          || '; Tipo Incrocio: '
          || NVL (tincrocio.strdescrizione, 'Non Tracciato')
          || '; Motivo: '
          || NVL (do_nominativo.strmotivocanc,
                  de_disponibilita_rosa.strdescrizione
                 ) AS descrizionepercorso,
          do_rosa.prgrosa AS chiavedettaglio,
          'MANSIONE:' || de_mansione.strdescrizione AS descstato,
          'SERVIZI' AS strposdata, sysdate as dataSort2,
          NULL AS cdnlavoratore_crypt
     FROM do_lav_storia_rosa,
          an_lavoratore,
          an_unita_azienda,
          do_rosa,
          do_incrocio,
          do_alternativa,
          do_mansione,
          de_mansione,
          de_tipo_incrocio tincrocio,
          do_richiesta_az,
          de_disponibilita_rosa,
          do_nominativo
    WHERE do_lav_storia_rosa.cdnlavoratore = an_lavoratore.cdnlavoratore
      AND do_lav_storia_rosa.prgazienda = an_unita_azienda.prgazienda
      AND do_lav_storia_rosa.prgunita = an_unita_azienda.prgunita
      AND do_lav_storia_rosa.prgrosa = do_rosa.prgrosa
      AND do_rosa.prgincrocio = do_incrocio.prgincrocio
      AND (    do_incrocio.prgrichiestaaz = do_alternativa.prgrichiestaaz
           AND NVL (do_incrocio.prgalternativa, do_alternativa.prgalternativa) =
                                                 do_alternativa.prgalternativa
          )
      AND do_alternativa.prgrichiestaaz = do_mansione.prgrichiestaaz
      AND do_alternativa.prgalternativa = do_mansione.prgalternativa
      AND do_mansione.codmansione = de_mansione.codmansione
      AND do_rosa.prgrosafiglia IS NULL
      AND do_lav_storia_rosa.prgrichiesta = do_richiesta_az.prgrichiestaaz
      AND do_incrocio.prgtipoincrocio = tincrocio.prgtipoincrocio
	  AND do_lav_storia_rosa.coddisponibilitarosa=de_disponibilita_rosa.coddisponibilitarosa (+)
	  AND (do_rosa.Prgrosa = do_nominativo.prgrosa and do_nominativo.cdnlavoratore=do_lav_storia_rosa.cdnlavoratore)
	  AND  do_lav_storia_rosa.PRGTIPOROSA=2
	  and exists (select 1
			from do_rosa rosa1
			where rosa1.prgrosa = do_lav_storia_rosa.prgrosa and rosa1.prgtiporosa=3
			and nvl(rosa1.DATINVIO, TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')) <> TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
			)
	  and (select count(1)
					  from do_lav_storia_rosa lav_storia_rosa
					  where
			  lav_storia_rosa.prgrichiesta = DO_LAV_STORIA_ROSA.PRGRICHIESTA
			  and lav_storia_rosa.prgtiporosa = 3
					  and lav_storia_rosa.cdnlavoratore = do_lav_storia_rosa.CDNLAVORATORE
			  and lav_storia_rosa.prgrosa = do_lav_storia_rosa.prgrosa
					  and nvl(lav_storia_rosa.DATINVIO, TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')) <> TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
					  and (select nvl(prgrosafiglia,0) from do_rosa r where r.prgrosa=lav_storia_rosa.prgrosa)=0
			  ) = 0
/

--Prompt drop View VW_GETTIPOINF_O;
DROP VIEW VW_GETTIPOINF_O
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_O;
--
-- VW_GETTIPOINF_O  (View) 
--
--  Dependencies: 
--   AM_MOVIMENTO (Table)
--   AN_AZIENDA (Table)
--   DE_MANSIONE (Table)
--   DE_MV_CESSAZIONE (Table)
--   DE_MV_TIPO_MOV (Table)
--   DE_ORARIO (Table)
--   DE_TIPO_CONTRATTO (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_o (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT am_movimento.cdnlavoratore AS cdnlavoratore,
          am_movimento.datiniziomov AS datdatainizio,
          am_movimento.datfinemoveffettiva AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'O' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno,
          am_movimento.prgazienda AS prgazienda,
          am_movimento.prgunita AS prgunitaaz, NULL AS descobiettivoazione,
          NULL AS descesito, NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             INITCAP (de_mv_tipo_mov.strdescrizione)
          || ' "'
          || de_tipo_contratto.strdescrizione
          || '"'
          || DECODE (am_movimento.codmonotempo, 'I', ' a TI', 'D', ' a TD')
          || DECODE (an_azienda.strragionesociale, NULL, '', ' presso ')
          || an_azienda.strragionesociale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ' (C.F.: ')
          || an_azienda.strcodicefiscale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ')')
          || CASE
                WHEN am_movimento.codmvcessazione IS NOT NULL
                AND am_movimento.codtipomov = 'CES'
                   THEN ' per ' || de_mv_cessazione.strdescrizione
                ELSE ''
             END
          || DECODE (am_movimento.codtipodich, NULL, '', '; Sanato')
          || DECODE (de_orario.strdescrizione, NULL, '', ' Orario ')
          || de_orario.strdescrizione
          || CASE
                WHEN am_movimento.numggprevistiagr > 0
                AND am_movimento.codtipomov = 'AVV'
                   THEN    '  Giorni presunti in agric.: '
                        || am_movimento.numggprevistiagr
                ELSE ''
             END
          || CASE
                WHEN am_movimento.numggeffettuatiagr > 0
                AND am_movimento.codtipomov = 'CES'
                   THEN    '  Giorni effettivi in agric.: '
                        || am_movimento.numggeffettuatiagr
                ELSE ''
             END 
          || CASE
                WHEN  de_tipo_contratto.codmonotipo = 'A' 
                AND am_movimento.datfinepf IS NOT NULL
                   THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy') 
                ELSE ''
              END
            AS descrizionepercorso,      
          am_movimento.prgmovimento AS chiavedettaglio,
          am_movimento.codstatoatto AS descstato, 'MOVIMENTI' AS strposdata,
          DECODE (am_movimento.datfinemoveffettiva,
                  NULL, SYSDATE,
                  am_movimento.datfinemoveffettiva
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_movimento,
          de_mv_tipo_mov,
          de_mv_cessazione,
          de_tipo_contratto,
          de_mansione,
          an_azienda,
          de_orario
    WHERE am_movimento.codtipomov = de_mv_tipo_mov.codtipomov
      AND am_movimento.codstatoatto = 'PR'
      AND am_movimento.codmonomovdich IN ('C', 'D')
      AND am_movimento.codmvcessazione = de_mv_cessazione.codmvcessazione(+)
      AND am_movimento.codtipocontratto = de_tipo_contratto.codtipocontratto(+)
      AND am_movimento.codmansione = de_mansione.codmansione(+)
      AND am_movimento.prgazienda = an_azienda.prgazienda(+)
      AND am_movimento.codorario = de_orario.codorario(+)
/

--Prompt drop View VW_GETTIPOINF_P;
DROP VIEW VW_GETTIPOINF_P
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_P;
--
-- VW_GETTIPOINF_P  (View) 
--
--  Dependencies: 
--   AN_LAVORATORE (Table)
--   CM_CANC_GRAD (Table)
--   DE_MOT_CANC_GRAD (Table)
--   DE_TIPO_INCROCIO (Table)
--   DO_INCROCIO (Table)
--   DO_RICHIESTA_AZ (Table)
--   DO_ROSA (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_p (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT an_lavoratore.cdnlavoratore AS cdnlavoratore,
          TRUNC (ccg.dtmcanc) AS datdatainizio, NULL AS datdatafine,
          do_richiesta_az.codcpi AS codcpi, NULL AS codcpidest,
          'P' AS codmonotipoinf, NULL AS ldescvarie, NULL AS descvarie,
          NULL AS ldescvarie2, NULL AS descvarie2,
          do_incrocio.prgrichiestaaz AS prgrichiestaaz,
             TO_CHAR (do_richiesta_az.numrichiesta)
          || '/'
          || TO_CHAR (do_richiesta_az.numanno) AS numanno,
          do_richiesta_az.prgazienda AS prgazienda,
          do_richiesta_az.prgunita AS prgunitaaz, NULL AS descobiettivoazione,
          NULL AS descesito, NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'Cancellazione da Graduatorie CM/art.1; Rich. n. '
          || TO_CHAR (nvl(do_richiesta_az.numrichiestaorig, do_richiesta_az.numrichiesta))
          || '/'
          || TO_CHAR (do_richiesta_az.numanno)
          || ';'
          || ' p. graduatoria:'
          || TO_CHAR (do_rosa.prgrosa)
          || ';'
          || ' Tipo graduatoria: '
          || NVL (tincrocio.strdescrizione, 'Non Tracciato')
          || ';'
          || ' Motivo: '
          || dmcg.strdescrizione
          || ''
          || DECODE (ccg.strspecifica, NULL, '', ' - ' || ccg.strspecifica)
                                                       AS descrizionepercorso,
          do_rosa.prgrosa AS chiavedettaglio, NULL AS descstato,
          'SERVIZI' AS strposdata, sysdate as dataSort2,
          NULL AS cdnlavoratore_crypt
     FROM an_lavoratore,
          do_rosa,
          do_incrocio,
          de_tipo_incrocio tincrocio,
          do_richiesta_az,
          cm_canc_grad ccg,
          de_mot_canc_grad dmcg
    WHERE do_rosa.prgincrocio = do_incrocio.prgincrocio
      AND do_incrocio.prgrichiestaaz = do_richiesta_az.prgrichiestaaz
      AND do_incrocio.prgtipoincrocio = tincrocio.prgtipoincrocio
      AND ccg.prgrosa = do_rosa.prgrosa
      AND ccg.cdnlavoratore = an_lavoratore.cdnlavoratore
      AND ccg.codmotcancgrad = dmcg.codmotcancgrad
/

--Prompt drop View VW_GETTIPOINF_Q;
DROP VIEW VW_GETTIPOINF_Q
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_GETTIPOINF_Q;
--
-- VW_GETTIPOINF_Q  (View) 
--
--  Dependencies: 
--   AM_MOVIMENTO (Table)
--   AM_MOVIMENTO_MISSIONE (Table)
--   AN_AZIENDA (Table)
--   DE_MANSIONE (Table)
--   DE_MV_CESSAZIONE (Table)
--   DE_MV_TIPO_MIS (Table)
--   DE_ORARIO (Table)
--
CREATE OR REPLACE FORCE VIEW vw_gettipoinf_q (cdnlavoratore,
                                              datdatainizio,
                                              datdatafine,
                                              codcpi,
                                              codcpidest,
                                              codmonotipoinf,
                                              ldescvarie,
                                              descvarie,
                                              ldescvarie2,
                                              descvarie2,
                                              prgrichiestaaz,
                                              numanno,
                                              prgazienda,
                                              prgunitaaz,
                                              descobiettivoazione,
                                              descesito,
                                              descservizio,
                                              desctipopatto,
                                              descstatooccupaz,
                                              descstatooccupazorig,
                                              descrizionepercorso,
                                              chiavedettaglio,
                                              descstato,
                                              strposdata,
                                              datasort2,
                                              cdnlavoratore_crypt
                                             )
AS
   SELECT am_movimento.cdnlavoratore AS cdnlavoratore,
          am_movimento_missione.datiniziomis AS datdatainizio,
          am_movimento_missione.datfinemis AS datdatafine, NULL AS codcpi,
          NULL AS codcpidest, 'Q' AS codmonotipoinf, NULL AS ldescvarie,
          NULL AS descvarie, NULL AS ldescvarie2, NULL AS descvarie2,
          NULL AS prgrichiestaaz, NULL AS numanno,
          am_movimento_missione.prgaziendautiliz AS prgazienda,
          am_movimento_missione.prgunitautiliz AS prgunitaaz,
          NULL AS descobiettivoazione, NULL AS descesito,
          NULL AS descservizio, NULL AS desctipopatto,
          NULL AS descstatooccupaz, NULL AS descstatooccupazorig,
             'Missione : '
          || INITCAP (de_mv_tipo_mis.strdescrizione)
           --  || ' "' || DE_TIPO_CONTRATTO.STRDESCRIZIONE || '"'
          --|| decode(AM_MOVIMENTO.CODMONOTEMPO, 'I', ' a TI', 'D', ' a TD')
          || DECODE (an_azienda.strragionesociale, NULL, '', ' presso ')
          || an_azienda.strragionesociale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ' (C.F.: ')
          || an_azienda.strcodicefiscale
          || DECODE (an_azienda.strcodicefiscale, NULL, '', ')')
          || CASE
                WHEN am_movimento_missione.codmvcessazione IS NOT NULL
                AND am_movimento_missione.codtipomis = 'CES'
                   THEN ' per ' || de_mv_cessazione.strdescrizione
                ELSE ''
             END
          --|| decode(AM_MOVIMENTO.CODTIPODICH , null, '', '; Sanato')
          || DECODE (de_orario.strdescrizione, NULL, '', ' Orario ')
          || de_orario.strdescrizione
          || CASE
                WHEN am_movimento_missione.numggprevistiagr > 0
                AND am_movimento_missione.codtipomis = 'AVV'
                   THEN    '  Giorni presunti in agric.: '
                        || am_movimento_missione.numggprevistiagr
                ELSE ''
             END
          || CASE
                WHEN am_movimento_missione.numggprevistiagr > 0
                AND am_movimento_missione.codtipomis = 'CES'
                   THEN    '  Giorni effettivi in agric.: '
                        || am_movimento_missione.numggprevistiagr
                ELSE ''
             END AS descrizionepercorso,
          am_movimento_missione.prgmissione AS chiavedettaglio,
          '' AS descstato, 
                           --AM_MOVIMENTO_MISSIONE.CODSTATOATTO as descStato,
          'MISSIONI' AS strposdata,
          DECODE (am_movimento_missione.datfinemis,
                  NULL, SYSDATE,
                  am_movimento_missione.datfinemis
                 ) AS datasort2,
          NULL AS cdnlavoratore_crypt
     FROM am_movimento,
          am_movimento_missione,
          de_mv_tipo_mis,
          de_mv_cessazione,
          de_mansione,
          an_azienda,
          de_orario
    WHERE am_movimento_missione.codtipomis = de_mv_tipo_mis.codtipomis
      --and AM_MOVIMENTO.CODSTATOATTO = 'PR'
      --and AM_MOVIMENTO.CODMONOMOVDICH in ( 'C', 'D' )
      AND am_movimento_missione.codmvcessazione = de_mv_cessazione.codmvcessazione(+)
      --and AM_MOVIMENTO_MISSIONE. CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+)
      AND am_movimento_missione.codmansione = de_mansione.codmansione(+)
      AND am_movimento_missione.prgaziendautiliz = an_azienda.prgazienda(+)
      AND am_movimento_missione.codorario = de_orario.codorario(+)
      AND am_movimento_missione.prgmovimento = am_movimento.prgmovimento
/

--Prompt drop View VW_RIC_PERC_LAV_A;
DROP VIEW VW_RIC_PERC_LAV_A
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_A;
--
-- VW_RIC_PERC_LAV_A  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_A (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_a (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS status,
          NULL AS servizi, NULL AS movimenti, NULL AS missioni,
             'PAGE=PercorsoDispoLavDettaglioInformazioniStorichePage&PRGDICHDISPONIBILITA='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_a lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_B;
DROP VIEW VW_RIC_PERC_LAV_B
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_B;
--
-- VW_RIC_PERC_LAV_B  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_B (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_b (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL status, NULL AS servizi,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS movimenti,
          NULL AS missioni,
             'PAGE=PercorsoMovimentiCollegatiPage&PrgMovimentoColl='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_b lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_C;
DROP VIEW VW_RIC_PERC_LAV_C
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_C;
--
-- VW_RIC_PERC_LAV_C  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_C (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_c (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) status,
          NULL AS servizi, NULL AS movimenti, NULL AS missioni,
             'PAGE=PercorsoStatoOccInfoStorDettPage&prgStatoOccupaz='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_c lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_D;
DROP VIEW VW_RIC_PERC_LAV_D
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_D;
--
-- VW_RIC_PERC_LAV_D  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_D (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_d (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) status,
          NULL AS servizi, NULL AS movimenti, NULL AS missioni,
             'PAGE=PercorsoTrasferimentiStoricoDettaglioPage&PRGLAVSTORIAINF='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_d lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_E_ENC;
DROP VIEW VW_RIC_PERC_LAV_E_ENC
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_E_ENC;
--
-- VW_RIC_PERC_LAV_E_ENC  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_E_ENC (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_e_enc (status,
                                                    servizi,
                                                    movimenti,
                                                    missioni,
                                                    url,
                                                    cdnlavoratore,
                                                    descrizionepercorso,
                                                    chiavedettaglio,
                                                    codmonotipoinf,
                                                    cpirif,
                                                    datdatainizio,
                                                    datasort2,
                                                    cdnlavoratore_crypt
                                                   )
AS
   SELECT    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) status,
          NULL AS servizi, NULL AS movimenti, NULL AS missioni,
          CASE
             WHEN lav.prgrichiestaaz = 0
                THEN    'PAGE=PercorsoCollMiratoInfStorDettPage&prgCMIscr='
                     || lav.chiavedettaglio
             WHEN lav.prgrichiestaaz = 1
                THEN    'PAGE=PercorsoMobilitaInfoStorDettPage&prgMobilitaIscr='
                     || lav.chiavedettaglio
          END AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2, lav.cdnlavoratore_crypt
     FROM vw_gettipoinf_e_enc lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_E_UNI;
DROP VIEW VW_RIC_PERC_LAV_E_UNI
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_E_UNI;
--
-- VW_RIC_PERC_LAV_E_UNI  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_E_UNI (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_e_uni (status,
                                                    servizi,
                                                    movimenti,
                                                    missioni,
                                                    url,
                                                    cdnlavoratore,
                                                    descrizionepercorso,
                                                    chiavedettaglio,
                                                    codmonotipoinf,
                                                    cpirif,
                                                    datdatainizio,
                                                    datasort2
                                                   )
AS
   SELECT    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) status,
          NULL AS servizi, NULL AS movimenti, NULL AS missioni,
          CASE
             WHEN lav.prgrichiestaaz = 0
                THEN    'PAGE=PercorsoCollMiratoInfStorDettPage&prgCMIscr='
                     || lav.chiavedettaglio
             WHEN lav.prgrichiestaaz = 1
                THEN    'PAGE=PercorsoMobilitaInfoStorDettPage&prgMobilitaIscr='
                     || lav.chiavedettaglio
          END AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_e_uni lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_F;
DROP VIEW VW_RIC_PERC_LAV_F
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_F;
--
-- VW_RIC_PERC_LAV_F  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_F (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_f (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
             'PAGE=PercorsoPattoLavDettaglioInformazioniStorichePage&PRGPATTOLAVORATORE='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_f lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_G;
DROP VIEW VW_RIC_PERC_LAV_G
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_G;
--
-- VW_RIC_PERC_LAV_G  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_G (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_g (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
          CASE
             WHEN lav.prgrichiestaaz = 0
                THEN    'PAGE=PercorsoDettaglioAppuntamentoPage&PRGAPPUNTAMENTO='
                     || lav.chiavedettaglio
             WHEN lav.prgrichiestaaz = 1
                THEN    'PAGE=PercorsoDettaglioContattoPage&PRGCONTATTO='
                     || lav.chiavedettaglio
                     || '&codCpiContatto='
                     || lav.codcpi
          END AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_g lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_H;
DROP VIEW VW_RIC_PERC_LAV_H
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_H;
--
-- VW_RIC_PERC_LAV_H  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_H (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_h (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
             'PAGE=PercorsoColloquioPage&prgColloquio='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_h lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_I;
DROP VIEW VW_RIC_PERC_LAV_I
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_I;
--
-- VW_RIC_PERC_LAV_I  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_I (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_i (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
             'PAGE=PercorsoPercorsiConcordatiPage&prgPercorso='
          || lav.chiavedettaglio
          || DECODE (lav.prgrichiestaaz, NULL, '', '&prgPattoLavoratore=')
          || lav.prgrichiestaaz AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_i lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_L;
DROP VIEW VW_RIC_PERC_LAV_L
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_L;
--
-- VW_RIC_PERC_LAV_L  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_L (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_l (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
             'PAGE=IdoDettaglioSinteticoPage&POPUP=1&PRGRICHIESTAAZ='
          || lav.prgrichiestaaz AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_l lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_M;
DROP VIEW VW_RIC_PERC_LAV_M
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_M;
--
-- VW_RIC_PERC_LAV_M  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_M (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_m (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
             'PAGE=DichRedDettaglioPage&POPUP=1&PRGDICHLAV='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_m lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_N;
DROP VIEW VW_RIC_PERC_LAV_N
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_N;
--
-- VW_RIC_PERC_LAV_N  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_N (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_n (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
             'PAGE=IdoDettaglioSinteticoPage&POPUP=1&PRGRICHIESTAAZ='
          || lav.prgrichiestaaz AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_n lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_O;
DROP VIEW VW_RIC_PERC_LAV_O
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_O;
--
-- VW_RIC_PERC_LAV_O  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_O (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_o (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status, NULL AS servizi,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS movimenti,
          NULL AS missioni,
             'PAGE=PercorsoMovimentiCollegatiPage&PrgMovimentoColl='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_o lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_P;
DROP VIEW VW_RIC_PERC_LAV_P
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_P;
--
-- VW_RIC_PERC_LAV_P  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_P (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_p (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS servizi,
          NULL AS movimenti, NULL AS missioni,
             'PAGE=IdoDettaglioSinteticoPage&POPUP=1&PRGRICHIESTAAZ='
          || lav.prgrichiestaaz AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_p lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/

--Prompt drop View VW_RIC_PERC_LAV_Q;
DROP VIEW VW_RIC_PERC_LAV_Q
/
/* Formatted on 2009/07/14 15:19 (Formatter Plus v4.8.8) */
--Prompt View VW_RIC_PERC_LAV_Q;
--
-- VW_RIC_PERC_LAV_Q  (View) 
--
--  Dependencies: 
--   DE_CPI (Table)
--   VW_GETTIPOINF_Q (View)
--
CREATE OR REPLACE FORCE VIEW vw_ric_perc_lav_q (status,
                                                servizi,
                                                movimenti,
                                                missioni,
                                                url,
                                                cdnlavoratore,
                                                descrizionepercorso,
                                                chiavedettaglio,
                                                codmonotipoinf,
                                                cpirif,
                                                datdatainizio,
                                                datasort2
                                               )
AS
   SELECT NULL AS status, NULL AS servizi,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS movimenti,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-&gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS missioni,
             'PAGE=ConsultaDatiMissionePage&POPUP=1&PRGMISSIONE='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_q lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_percorso_lav.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_PERCORSO_LAV AS
Select
 AM_ELENCO_ANAGRAFICO.CDNLAVORATORE as CdnLavoratore,
 AM_DICH_DISPONIBILITA.datDichiarazione  as DatDataInizio,
 AM_DICH_DISPONIBILITA.datFine as DatDataFine,
 null as CodCpi,
 null as CodCpiDest,
 'A'  as CodMonoTipoInf,
 'Tipo Disponibilità' as LDescVarie,
 DE_TIPO_DICH_DISP.STRDESCRIZIONE as DescVarie,
 'Motivo fine' as LdescVarie2,
 DE_MOTIVO_FINE_ATTO.STRDESCRIZIONE as DescVarie2,
 null as prgRichiestaAz,
 null as numAnno,
 null as prgAzienda,
 null as prgUnitaAz,
 null as DescObiettivoAzione,
 null as DescEsito,
 null as DescServizio,
 null as DescTipoPatto,
 null as DescStatoOccupaz,
 null as DescStatoOccupazOrig,
 'Rilascio della DID' || decode(DE_MOTIVO_FINE_ATTO.STRDESCRIZIONE ,NULL,'','-> Motivo Fine: '|| DE_MOTIVO_FINE_ATTO.STRDESCRIZIONE )
 as DescrizionePercorso,
 '' as descrizionePercorsoAlternativa,
 AM_DICH_DISPONIBILITA.prgDichDisponibilita  as chiaveDettaglio,
 DE_STATO_ATTO.STRDESCRIZIONE as descStato,
 'STATUS' as strPosData,
 decode(AM_DICH_DISPONIBILITA.datFine, null, sysdate, AM_DICH_DISPONIBILITA.datFine) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_ELENCO_ANAGRAFICO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_ELENCO_ANAGRAFICO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 AM_DICH_DISPONIBILITA, AM_ELENCO_ANAGRAFICO,
 DE_STATO_ATTO,
 DE_MOTIVO_FINE_ATTO,
 DE_TIPO_DICH_DISP
where
 AM_DICH_DISPONIBILITA.CODSTATOATTO ='PR'
 and AM_DICH_DISPONIBILITA.PRGELENCOANAGRAFICO = AM_ELENCO_ANAGRAFICO.PRGELENCOANAGRAFICO
 and AM_DICH_DISPONIBILITA.codstatoatto = DE_STATO_ATTO.CODSTATOATTO
 and AM_DICH_DISPONIBILITA.codMotivoFineAtto = DE_MOTIVO_FINE_ATTO.CODMOTIVOFINEATTO(+)
 and AM_DICH_DISPONIBILITA.CODTIPODICHDISP = DE_TIPO_DICH_DISP.CODTIPODICHDISP
------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------
Select
 AM_MOVIMENTO.CDNLAVORATORE as CdnLavoratore,
 AM_MOVIMENTO.DATINIZIOMOV as DatDataInizio,
 AM_MOVIMENTO.DATFINEMOVEFFETTIVA as DatDataFine,
    null as codcpi,
    null as CodCpiDest,
    'B'  as CodMonoTipoInf,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as numAnno,
 AM_MOVIMENTO.PRGAZIENDA as prgAzienda,
 AM_MOVIMENTO.PRGUNITA as prgUnitaAz,
 null as DescObiettivoAzione,
 null as DescEsito,
 null as DescServizio,
 null as DescTipoPatto,
 null as DescStatoOccupaz,
 null as DescStatoOccupazOrig,
    initcap(DE_MV_TIPO_MOV.STRDESCRIZIONE)
    || ' "' || DE_TIPO_CONTRATTO.STRDESCRIZIONE || '"'
 || decode(AM_MOVIMENTO.CODMONOTEMPO, 'I', ' a TI', 'D', ' a TD')
 || decode(AM_MOVIMENTO.CODMANSIONE, null, '', ' nella mansione di ') || DE_MANSIONE.STRDESCRIZIONE
 || decode(AN_AZIENDA.STRRAGIONESOCIALE, null, '', ' presso ') || AN_AZIENDA.STRRAGIONESOCIALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ' (C.F.: ') || AN_AZIENDA.STRCODICEFISCALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ')')
 || case
      when AM_MOVIMENTO.CODMVCESSAZIONE is not null and am_movimento.CODTIPOMOV = 'CES'
          then  ' per ' || DE_MV_CESSAZIONE.STRDESCRIZIONE
       else  ''
    end
 || decode(AM_MOVIMENTO.CODTIPODICH , null, '', '; Sanato')
 || decode(DE_ORARIO.STRDESCRIZIONE, null, '', ' Orario ') || DE_ORARIO.STRDESCRIZIONE
 || case
      when AM_MOVIMENTO.NUMGGPREVISTIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'AVV'
          then  '  Giorni presunti in agric.: ' || AM_MOVIMENTO.NUMGGPREVISTIAGR
   else ''
    end
  || case
      when AM_MOVIMENTO.NUMGGEFFETTUATIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'CES'
          then  '  Giorni effettivi in agric.: ' || AM_MOVIMENTO.NUMGGEFFETTUATIAGR
   else ''
    end
  ---
  || CASE
    WHEN  de_tipo_contratto.codmonotipo = 'A'
    AND am_movimento.datfinepf IS NOT NULL
       THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy')
    ELSE ''
  END
  ---
 as DescrizionePercorso,
 initcap(DE_MV_TIPO_MOV.STRDESCRIZIONE)
    || ' "' || DE_TIPO_CONTRATTO.STRDESCRIZIONE || '"'
 || decode(AM_MOVIMENTO.CODMONOTEMPO, 'I', ' a TI', 'D', ' a TD')
 || decode(AN_AZIENDA.STRRAGIONESOCIALE, null, '', ' presso ') || AN_AZIENDA.STRRAGIONESOCIALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ' (C.F.: ') || AN_AZIENDA.STRCODICEFISCALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ')')
 || case
      when AM_MOVIMENTO.CODMVCESSAZIONE is not null and am_movimento.CODTIPOMOV = 'CES'
          then  ' per ' || DE_MV_CESSAZIONE.STRDESCRIZIONE
       else  ''
    end
 || decode(AM_MOVIMENTO.CODTIPODICH , null, '', '; Sanato')
 || decode(DE_ORARIO.STRDESCRIZIONE, null, '', ' Orario ') || DE_ORARIO.STRDESCRIZIONE
 || case
      when AM_MOVIMENTO.NUMGGPREVISTIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'AVV'
          then  '  Giorni presunti in agric.: ' || AM_MOVIMENTO.NUMGGPREVISTIAGR
   else ''
    end
  || case
      when AM_MOVIMENTO.NUMGGEFFETTUATIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'CES'
          then  '  Giorni effettivi in agric.: ' || AM_MOVIMENTO.NUMGGEFFETTUATIAGR
   else ''
    end
  ---
  || CASE
    WHEN  de_tipo_contratto.codmonotipo = 'A'
    AND am_movimento.datfinepf IS NOT NULL
       THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy')
    ELSE ''
  END
  ---
    as descrizionePercorsoAlternativa,
 AM_MOVIMENTO.PRGMOVIMENTO as chiaveDettaglio,
 AM_MOVIMENTO.CODSTATOATTO as descStato,
 'MOVIMENTI' as strPosData,
 decode(AM_MOVIMENTO.DATFINEMOVEFFETTIVA, null, sysdate, AM_MOVIMENTO.DATFINEMOVEFFETTIVA) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOVIMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOVIMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 AM_MOVIMENTO, DE_MV_TIPO_MOV, DE_MV_CESSAZIONE, DE_TIPO_CONTRATTO, DE_MANSIONE, AN_AZIENDA, DE_ORARIO
where
 AM_MOVIMENTO.CODTIPOMOV = DE_MV_TIPO_MOV.CODTIPOMOV
 and AM_MOVIMENTO.CODSTATOATTO = 'PR'
 and AM_MOVIMENTO.CODMONOMOVDICH = 'O'
 and AM_MOVIMENTO.CODMVCESSAZIONE = DE_MV_CESSAZIONE.CODMVCESSAZIONE (+)
 and AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+)
 and AM_MOVIMENTO.CODMANSIONE = DE_MANSIONE.CODMANSIONE (+)
 and AM_MOVIMENTO.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA (+)
 and AM_MOVIMENTO.CODORARIO = DE_ORARIO.CODORARIO (+)
-----------------------------------------------------------------------------------------------------------
union
-----------------------------------------------------------------------------------------------------------
Select
 AM_MOVIMENTO.CDNLAVORATORE as CdnLavoratore,
 AM_MOVIMENTO.DATINIZIOMOV as DatDataInizio,
 AM_MOVIMENTO.DATFINEMOVEFFETTIVA as DatDataFine,
    null as codcpi,
 null as CodCpiDest,
    'O'  as CodMonoTipoInf,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as numAnno,
 AM_MOVIMENTO.PRGAZIENDA as prgAzienda,
 AM_MOVIMENTO.PRGUNITA as prgUnitaAz,
 null as DescObiettivoAzione,
 null as DescEsito,
 null as DescServizio,
 null as DescTipoPatto,
 null as DescStatoOccupaz,
 null as DescStatoOccupazOrig,
    initcap(DE_MV_TIPO_MOV.STRDESCRIZIONE)
    || ' "' || DE_TIPO_CONTRATTO.STRDESCRIZIONE || '"'
 || decode(AM_MOVIMENTO.CODMONOTEMPO, 'I', ' a TI', 'D', ' a TD')
 || decode(AN_AZIENDA.STRRAGIONESOCIALE, null, '', ' presso ') || AN_AZIENDA.STRRAGIONESOCIALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ' (C.F.: ') || AN_AZIENDA.STRCODICEFISCALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ')')
 || case
      when AM_MOVIMENTO.CODMVCESSAZIONE is not null and am_movimento.CODTIPOMOV = 'CES'
          then  ' per ' || DE_MV_CESSAZIONE.STRDESCRIZIONE
       else  ''
    end
 || decode(AM_MOVIMENTO.CODTIPODICH , null, '', '; Sanato')
 || decode(DE_ORARIO.STRDESCRIZIONE, null, '', ' Orario ') || DE_ORARIO.STRDESCRIZIONE
 || case
      when AM_MOVIMENTO.NUMGGPREVISTIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'AVV'
          then  '  Giorni presunti in agric.: ' || AM_MOVIMENTO.NUMGGPREVISTIAGR
   else ''
    end
  || case
      when AM_MOVIMENTO.NUMGGEFFETTUATIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'CES'
          then  '  Giorni effettivi in agric.: ' || AM_MOVIMENTO.NUMGGEFFETTUATIAGR
   else ''
    end
  ---
  || CASE
    WHEN  de_tipo_contratto.codmonotipo = 'A'
    AND am_movimento.datfinepf IS NOT NULL
       THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy')
    ELSE ''
  END
  ---
  as DescrizionePercorso,
   initcap(DE_MV_TIPO_MOV.STRDESCRIZIONE)
    || ' "' || DE_TIPO_CONTRATTO.STRDESCRIZIONE || '"'
 || decode(AM_MOVIMENTO.CODMONOTEMPO, 'I', ' a TI', 'D', ' a TD')
 || decode(AM_MOVIMENTO.CODMANSIONE, null, '', ' nella mansione di ') || DE_MANSIONE.STRDESCRIZIONE
 || decode(AN_AZIENDA.STRRAGIONESOCIALE, null, '', ' presso ') || AN_AZIENDA.STRRAGIONESOCIALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ' (C.F.: ') || AN_AZIENDA.STRCODICEFISCALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ')')
 || case
      when AM_MOVIMENTO.CODMVCESSAZIONE is not null and am_movimento.CODTIPOMOV = 'CES'
          then  ' per ' || DE_MV_CESSAZIONE.STRDESCRIZIONE
       else  ''
    end
 || decode(AM_MOVIMENTO.CODTIPODICH , null, '', '; Sanato')
 || decode(DE_ORARIO.STRDESCRIZIONE, null, '', ' Orario ') || DE_ORARIO.STRDESCRIZIONE
 || case
      when AM_MOVIMENTO.NUMGGPREVISTIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'AVV'
          then  '  Giorni presunti in agric.: ' || AM_MOVIMENTO.NUMGGPREVISTIAGR
   else ''
    end
  || case
      when AM_MOVIMENTO.NUMGGEFFETTUATIAGR > 0 and AM_MOVIMENTO.CODTIPOMOV = 'CES'
          then  '  Giorni effettivi in agric.: ' || AM_MOVIMENTO.NUMGGEFFETTUATIAGR
   else ''
    end
  ---
  || CASE
    WHEN  de_tipo_contratto.codmonotipo = 'A'
    AND am_movimento.datfinepf IS NOT NULL
       THEN ' Fine periodo formativo ' || TO_CHAR (am_movimento.datfinepf,'dd/mm/yyyy')
    ELSE ''
  END
  ---
  as DescrizionePercorsoAlternativa,
 AM_MOVIMENTO.PRGMOVIMENTO as chiaveDettaglio,
 AM_MOVIMENTO.CODSTATOATTO as descStato,
 'MOVIMENTI' as strPosData,
 decode(AM_MOVIMENTO.DATFINEMOVEFFETTIVA, null, sysdate, AM_MOVIMENTO.DATFINEMOVEFFETTIVA) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOVIMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOVIMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 AM_MOVIMENTO, DE_MV_TIPO_MOV, DE_MV_CESSAZIONE, DE_TIPO_CONTRATTO, DE_MANSIONE, AN_AZIENDA, DE_ORARIO
where
 AM_MOVIMENTO.CODTIPOMOV = DE_MV_TIPO_MOV.CODTIPOMOV
 and AM_MOVIMENTO.CODSTATOATTO = 'PR'
 and AM_MOVIMENTO.CODMONOMOVDICH in ( 'C', 'D' )
 and AM_MOVIMENTO.CODMVCESSAZIONE = DE_MV_CESSAZIONE.CODMVCESSAZIONE (+)
 and AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+)
 and AM_MOVIMENTO.CODMANSIONE = DE_MANSIONE.CODMANSIONE (+)
 and AM_MOVIMENTO.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA (+)
 and AM_MOVIMENTO.CODORARIO = DE_ORARIO.CODORARIO (+)
-----------------------------------------------------------------------------------------------------------
union
-----------------------------------------------------------------------------------------------------------
--MISSIONI
Select
 AM_MOVIMENTO.CDNLAVORATORE as CdnLavoratore,
 AM_MOVIMENTO_MISSIONE.DATINIZIOMIS as DatDataInizio,
 AM_MOVIMENTO_MISSIONE.DATFINEMIS as DatDataFine,
    null as codcpi,
 null as CodCpiDest,
    'Q'  as CodMonoTipoInf,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as numAnno,
 AM_MOVIMENTO_MISSIONE.PRGAZIENDAUTILIZ as prgAzienda,
 AM_MOVIMENTO_MISSIONE.PRGUNITAUTILIZ as prgUnitaAz,
 null as DescObiettivoAzione,
 null as DescEsito,
 null as DescServizio,
 null as DescTipoPatto,
 null as DescStatoOccupaz,
 null as DescStatoOccupazOrig,
    'Missione : ' || initcap(DE_MV_TIPO_MIS.STRDESCRIZIONE)
  --  || ' "' || DE_TIPO_CONTRATTO.STRDESCRIZIONE || '"'
 --|| decode(AM_MOVIMENTO.CODMONOTEMPO, 'I', ' a TI', 'D', ' a TD')

 || decode(AN_AZIENDA.STRRAGIONESOCIALE, null, '', ' presso ') || AN_AZIENDA.STRRAGIONESOCIALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ' (C.F.: ') || AN_AZIENDA.STRCODICEFISCALE
 || decode(AN_AZIENDA.STRCODICEFISCALE, null, '', ')')
 || case
      when AM_MOVIMENTO_MISSIONE.CODMVCESSAZIONE is not null and am_movimento_MISSIONE.CODTIPOMIS= 'CES'
          then  ' per ' || DE_MV_CESSAZIONE.STRDESCRIZIONE
       else  ''
    end
 --|| decode(AM_MOVIMENTO.CODTIPODICH , null, '', '; Sanato')
 || decode(DE_ORARIO.STRDESCRIZIONE, null, '', ' Orario ') || DE_ORARIO.STRDESCRIZIONE
 || case
      when AM_MOVIMENTO_MISSIONE.NUMGGPREVISTIAGR > 0 and AM_MOVIMENTO_MISSIONE.CODTIPOMIS = 'AVV'
          then  '  Giorni presunti in agric.: ' || AM_MOVIMENTO_MISSIONE.NUMGGPREVISTIAGR
   else ''
    end
  || case
      when AM_MOVIMENTO_MISSIONE.NUMGGPREVISTIAGR > 0 and AM_MOVIMENTO_MISSIONE.CODTIPOMIS = 'CES'
          then  '  Giorni effettivi in agric.: ' || AM_MOVIMENTO_MISSIONE.NUMGGPREVISTIAGR
   else ''
    end
  as DescrizionePercorso,
  '' as DescrizionePercorsoAlternativa,
 AM_MOVIMENTO_MISSIONE.PRGMISSIONE as chiaveDettaglio,
 '' as descStato, --AM_MOVIMENTO_MISSIONE.CODSTATOATTO as descStato,
 'MISSIONI' as strPosData,
 decode(AM_MOVIMENTO_MISSIONE.DATFINEMIS, null, sysdate, AM_MOVIMENTO_MISSIONE.DATFINEMIS) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOVIMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOVIMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 AM_MOVIMENTO, AM_MOVIMENTO_MISSIONE, DE_MV_TIPO_MIS, DE_MV_CESSAZIONE, DE_MANSIONE, AN_AZIENDA, DE_ORARIO
where
 AM_MOVIMENTO_MISSIONE.CODTIPOMIS = DE_MV_TIPO_MIS.CODTIPOMIS
 --and AM_MOVIMENTO.CODSTATOATTO = 'PR'
 --and AM_MOVIMENTO.CODMONOMOVDICH in ( 'C', 'D' )
 and AM_MOVIMENTO_MISSIONE.CODMVCESSAZIONE = DE_MV_CESSAZIONE.CODMVCESSAZIONE (+)
 --and AM_MOVIMENTO_MISSIONE. CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+)
 and AM_MOVIMENTO_MISSIONE.CODMANSIONE = DE_MANSIONE.CODMANSIONE (+)
 and AM_MOVIMENTO_MISSIONE.PRGAZIENDAUTILIZ = AN_AZIENDA.PRGAZIENDA (+)
 and AM_MOVIMENTO_MISSIONE.CODORARIO = DE_ORARIO.CODORARIO (+)
 and AM_MOVIMENTO_MISSIONE.PRGMOVIMENTO = AM_MOVIMENTO.PRGMOVIMENTO
------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------
--FINE MISSIONI
Select
 AM_STATO_OCCUPAZ.CDNLAVORATORE as CdnLavoratore,
 AM_STATO_OCCUPAZ.DATINIZIO as DatDataInizio,
 AM_STATO_OCCUPAZ.DATFINE as DatDataFine,
 null as codcpi,
 null as CodCpiDest,
 'C'  as CodMonoTipoInf,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as numAnno,
 null as prgAzienda,
 null as prgUnitaAz,
 null as DescObiettivoAzione,
 null as DescEsito,
 null as DescServizio,
 null as DescTipoPatto,
 DE_STATO_OCCUPAZ.STRDESCRIZIONE  as DescStatoOccupaz,
 null as DescStatoOccupazOrig,
 'Acquisizione stato occupazionale ' || DE_STATO_OCCUPAZ.STRDESCRIZIONE ||' proveniente da ' ||
      decode(AM_STATO_OCCUPAZ.CODMONOPROVENIENZA, 'A', 'REG. ANAG.', 'D', 'D.I.D.','M','MOVIMENTI','T','TRASFERIMENTO COMP.','P','PORTING', 'G','Agg. manuale', 'O','Reg./Agg. manuale', 'N','INS. MANUALE', 'B', 'MOBILITA''')
   as DescrizionePercorso,
   '' as DescrizionePercorsoAlternativa,
 AM_STATO_OCCUPAZ.PRGSTATOOCCUPAZ as chiaveDettaglio,
 null as descStato,
 'STATUS' as strPosData,
 decode(AM_STATO_OCCUPAZ.DATFINE, null, sysdate, AM_STATO_OCCUPAZ.DATFINE) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_STATO_OCCUPAZ.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_STATO_OCCUPAZ.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 AM_STATO_OCCUPAZ, DE_STATO_OCCUPAZ
where
 AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ = DE_STATO_OCCUPAZ.CODSTATOOCCUPAZ
------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------
Select
 AN_LAV_STORIA_INF.CDNLAVORATORE    as CdnLavoratore,
 AN_LAV_STORIA_INF.DATTRASFERIMENTO as DatDataInizio,
 AN_LAV_STORIA_INF.DATFINE          as DatDataFine,
 AN_LAV_STORIA_INF.CODCPIORIG       as CodCpi,
 AN_LAV_STORIA_INF.CODCPITIT        as CodCpiDest,
 'D'  as CodMonoTipoInf,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as numAnno,
 null as prgAzienda,
 null as prgUnitaAz,
 null as DescObiettivoAzione,
 null as DescEsito,
 null as DescServizio,
 null as DescTipoPatto,
 null as DescStatoOccupaz,
 null as DescStatoOccupazOrig,
 'Trasferimento dal CPI di '||
           CASE
       WHEN AN_LAV_STORIA_INF.CODMONOTIPOORIG = 'V'
                THEN CPIDAVAL.STRDESCRIZIONE
           ELSE
           decode(AN_LAV_STORIA_INF.CODMONOTIPOCPI,
        'C',initcap(CPIORIG.STRDESCRIZIONE),
        'E',initcap(CPIORIG.STRDESCRIZIONE),
                  'T', initcap(CPITIT.STRDESCRIZIONE),
          'CPI TITOLOARE NON PERVENUTO'
         )
        END
        || ' al CPI di '
        || decode(AN_LAV_STORIA_INF.CODMONOTIPOCPI,
               'C',initcap(CPITIT.STRDESCRIZIONE),
               'E',initcap(CPITIT.STRDESCRIZIONE),
                              'T', initcap(CPIORIG.STRDESCRIZIONE),
         'CPI COMPETENTE NON PERVENUTO'
        )
 as DescrizionePercorso,
 '' as DescrizionePercorsoAlternativa,
 AN_LAV_STORIA_INF.PRGLAVSTORIAINF as chiaveDettaglio,
 null as descStato,
 'STATUS' as strPosData,
 decode(AN_LAV_STORIA_INF.DATFINE, null, sysdate, AN_LAV_STORIA_INF.DATFINE) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAV_STORIA_INF.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAV_STORIA_INF.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 AN_LAV_STORIA_INF, DE_CPI CPIORIG, DE_CPI CPITIT, DE_CPI CPIDAVAL
where
 AN_LAV_STORIA_INF.DATTRASFERIMENTO is not null
 and AN_LAV_STORIA_INF.CODCPIORIG = CPIORIG.CODCPI (+)
 and AN_LAV_STORIA_INF.CODCPITIT = CPITIT.CODCPI (+)
 and AN_LAV_STORIA_INF.CODCPIORIGPREC = CPIDAVAL.CODCPI(+)
------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------
select
 0 as CDNLAVORATORE,
 AM_CM_ISCR.DATDATAINIZIO as DATDATAINIZIO,
 AM_CM_ISCR.DATDATAFINE   as DATDATAFINE,
 null as CodCpi,
 null as CODCPIDEST,
 'R'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 0    as prgRichiestaAz,
 null as NUMANNO,
 null as PRGAZIENDA,
 null as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 null as DESCESITO,
 null as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'COLLOCAMENTO MIRATO: Data Inizio ' || to_char(DATDATAINIZIO,'dd/mm/yyyy') || ' Data Anzianità ' || to_char(AM_CM_ISCR.DATANZIANITA68,'dd/mm/yyyy') ||
 ' Tipo: ' || decode(DE_CM_TIPO_ISCR.CODMONOTIPORAGG,'D','Disabili','A','Altre categorie protette') || '  Categoria: ' || DE_CM_TIPO_ISCR.STRDESCRIZIONE
 as DESCRIZIONEPERCORSO,
 '' as DescrizionePercorsoAlternativa,
 AM_CM_ISCR.PRGCMISCR as CHIAVEDETTAGLIO,
 null as DESCSTATO,
 'STATUS' as STRPOSDATA,
 sysdate as dataSort2,
 AM_CM_ISCR.CDNLAVORATORE as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_DOCUMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_DOCUMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
  AM_CM_ISCR, DE_CM_TIPO_ISCR, AM_DOCUMENTO, AM_DOCUMENTO_COLL
WHERE
  AM_CM_ISCR.CODCMTIPOISCR = DE_CM_TIPO_ISCR.CODCMTIPOISCR AND
  AM_CM_ISCR.PRGCMISCR = AM_DOCUMENTO_COLL.STRCHIAVETABELLA AND
  AM_DOCUMENTO.PRGDOCUMENTO = AM_DOCUMENTO_COLL.PRGDOCUMENTO AND
  AM_DOCUMENTO.CODTIPODOCUMENTO = 'L68' AND AM_DOCUMENTO.CODSTATOATTO = 'PR' AND DATDATAFINE is null AND
  AM_DOCUMENTO_COLL.CDNCOMPONENTE = (SELECT CDNCOMPONENTE from TS_COMPONENTE WHERE UPPER(STRPAGE) = 'CMISCRIZIONILAVORATOREPAGE')
------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------
SELECT
 am_documento.cdnlavoratore,
 cm_iscr_art1.datiscrlistaprov AS datdatainizio,
 cm_iscr_art1.datfine AS datdatafine,
 NULL AS codcpi,
 NULL AS codcpidest,
 'S' AS codmonotipoinf,
 NULL AS ldescvarie,
 NULL AS descvarie,
 NULL AS ldescvarie2,
 NULL AS descvarie2,
 0 AS prgrichiestaaz,
 NULL AS numanno,
 NULL AS prgazienda,
 NULL AS prgunitaaz,
 NULL AS descobiettivoazione,
 NULL AS descesito,
 NULL AS descservizio,
 NULL AS desctipopatto,
 NULL AS descstatooccupaz,
 NULL AS descstatooccupazorig,
 'LISTE SPECIALI: Data Iscrizione lista provinciale: '
          || TO_CHAR (cm_iscr_art1.datiscrlistaprov, 'dd/mm/yyyy') || ' Albo: ' || cm_iscr_art1.codtipolista
          AS descrizionepercorso,
 '' as descrizionePercorsoAlternativa,
 cm_iscr_art1.prgiscrart1 AS chiavedettaglio,
 NULL AS descstato,
 'STATUS' AS strposdata,
 sysdate as dataSort2,
 null AS cdnlavoratore_crypt,
to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_DOCUMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_DOCUMENTO.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
     FROM cm_iscr_art1, am_documento, am_documento_coll
    WHERE cm_iscr_art1.prgiscrart1 = am_documento_coll.strchiavetabella
      AND am_documento.prgdocumento = am_documento_coll.prgdocumento
      AND am_documento.codtipodocumento = 'ILS'
      AND am_documento.codstatoatto = 'PR'
      AND cm_iscr_art1.datfine IS NULL
-----------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 AM_MOBILITA_ISCR.CDNLAVORATORE as CDNLAVORATORE,
 AM_MOBILITA_ISCR.DATINIZIO     as DATDATAINIZIO,
 AM_MOBILITA_ISCR.DATFINE       as DATDATAFINE,
 null as CodCpi,
 null as CODCPIDEST,
 'E'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 1    as prgRichiestaAz,
 null as NUMANNO,
 null as PRGAZIENDA,
 null as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 null as DESCESITO,
 null as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'MOBILITA''' as DESCRIZIONEPERCORSO,
 '' as DescrizionePercorsoAlternativa,
 AM_MOBILITA_ISCR.PRGMOBILITAISCR as CHIAVEDETTAGLIO,
 null      as DESCSTATO,
 'STATUS' as STRPOSDATA,
 decode(AM_MOBILITA_ISCR.DATFINE, null, sysdate, AM_MOBILITA_ISCR.DATFINE) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOBILITA_ISCR.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_MOBILITA_ISCR.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
  AM_MOBILITA_ISCR, DE_MB_TIPO
WHERE
  AM_MOBILITA_ISCR.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 AM_PATTO_LAVORATORE.CDNLAVORATORE as CDNLAVORATORE,
 AM_PATTO_LAVORATORE.DATSTIPULA    as DATDATAINIZIO,
 AM_PATTO_LAVORATORE.DATFINE       as DATDATAFINE,
 AM_PATTO_LAVORATORE.CODCPI as CODCPI,
 null as CODCPIDEST,
 'F'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as NUMANNO,
 null as PRGAZIENDA,
 null as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 null as DESCESITO,
 null as DESCSERVIZIO,
 DE_TIPO_PATTO.STRDESCRIZIONE as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 (case
  when am_patto_lavoratore.flgpatto297 = 'S' then 'Stipula del patto di servizio 150'
		else 'Accordo generico'
  end)
	  || (case
  when am_patto_lavoratore.codtipopatto = 'ANP' then 
    ' con i programmi: ' || getProgrammiPattoPercorsoLav (am_patto_lavoratore.prgpattolavoratore)
  else ' con la misura ' || DE_TIPO_PATTO.strdescrizione
  end) as DESCRIZIONEPERCORSO,  
  '' as DescrizionePercorsoAlternativa,
 AM_PATTO_LAVORATORE.PRGPATTOLAVORATORE as CHIAVEDETTAGLIO,
 null as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 decode(AM_PATTO_LAVORATORE.DATFINE, null, sysdate, AM_PATTO_LAVORATORE.DATFINE) as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AM_PATTO_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AM_PATTO_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
 FROM AM_PATTO_LAVORATORE
 INNER JOIN DE_TIPO_PATTO on AM_PATTO_LAVORATORE.codtipopatto = DE_TIPO_PATTO.codtipopatto
WHERE AM_PATTO_LAVORATORE.codstatoatto = 'PR'
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 ag_lavoratore.CDNLAVORATORE as CDNLAVORATORE,
 trunc(ag_agenda.DTMDATAORA) as DATDATAINIZIO, -- viene tolta l'ora
 null as DATDATAFINE,
 ag_agenda.CODCPI as CODCPI,
 null as CODCPIDEST,
 'G'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 0    as prgRichiestaAz,
 null as NUMANNO,
 null as PRGAZIENDA,
 null as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 de_esito_appunt.STRDESCRIZIONE as DESCESITO,
 de_servizio.STRDESCRIZIONE as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'L''appuntamento col Servizio ' || DE_SERVIZIO.STRDESCRIZIONE
 || ' del CPI di '|| de_cpi.STRDESCRIZIONE
 || decode(de_stato_appuntamento.STRDESCRIZIONE, null, '', ' e'' ')|| lower(de_stato_appuntamento.STRDESCRIZIONE)
    as DESCRIZIONEPERCORSO,
  '' as DescrizionePercorsoAlternativa,
 ag_agenda.PRGAPPUNTAMENTO as CHIAVEDETTAGLIO,
 de_stato_appuntamento.STRDESCRIZIONE as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 sysdate as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(ag_lavoratore.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(ag_lavoratore.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 ag_agenda, de_servizio, ag_lavoratore, de_esito_appunt, de_stato_appuntamento, de_cpi
where
 ag_agenda.CODCPI = ag_lavoratore.CODCPI
 and ag_agenda.PRGAPPUNTAMENTO = ag_lavoratore.PRGAPPUNTAMENTO
 and ag_agenda.CODSERVIZIO = de_servizio.CODSERVIZIO (+)
 and ag_agenda.CODESITOAPPUNT = de_esito_appunt.CODESITOAPPUNT(+)
 and ag_agenda.CODSTATOAPPUNTAMENTO = de_stato_appuntamento.CODSTATOAPPUNTAMENTO (+)
 and ag_agenda.codcpi = de_cpi.CODCPI
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 ag_contatto.CDNLAVORATORE as CDNLAVORATORE,
 ag_contatto.DATCONTATTO    as DATDATAINIZIO,
 null as DATDATAFINE,
 ag_contatto.CODCPICONTATTO as CODCPI,
 null as CODCPIDEST,
 'G'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 1    as prgRichiestaAz,
 null as NUMANNO,
 null as PRGAZIENDA,
 null as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 de_effetto_contatto.STRDESCRIZIONE as DESCESITO,
 null as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'CONTATTO'|| decode(DE_TIPO_CONTATTOAG.STRDESCRIZIONE, null,  '', '; Tipo: ')|| DE_TIPO_CONTATTOAG.STRDESCRIZIONE ||
 '-'|| decode(AG_CONTATTO.STRIO, 'I', 'Al CpI', 'O', 'Dal CpI') ||
 decode(DE_MOTIVO_CONTATTOAG.STRDESCRIZIONE, null, '', '; Motivo: ') || DE_MOTIVO_CONTATTOAG.STRDESCRIZIONE ||
 decode(DE_EFFETTO_CONTATTO.STRDESCRIZIONE, null, '', '; Effetto: ') || DE_EFFETTO_CONTATTO.STRDESCRIZIONE||
 decode(DE_DISPONIBILITA_ROSA.STRDESCRIZIONE, null, '', '; Disp. Rosa: ') || DE_DISPONIBILITA_ROSA.STRDESCRIZIONE||
 decode(AG_CONTATTO.TXTCONTATTO, null, '', '; Note: ') || substr(AG_CONTATTO.TXTCONTATTO,1,25)||
 decode(AG_CONTATTO.TXTCONTATTO, null, '', '...')
 as DESCRIZIONEPERCORSO,
 '' as DescrizionePercorsoAlternativa,
 ag_contatto.PRGCONTATTO as CHIAVEDETTAGLIO,
 null as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 sysdate as dataSort2,
 null as CDNLAVORATORE_CRYPT,
  to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(ag_contatto.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(ag_contatto.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
  ag_contatto, an_spi, de_disponibilita_rosa, de_effetto_contatto, DE_TIPO_CONTATTOAG, DE_MOTIVO_CONTATTOAG
where
  ag_contatto.prgspicontatto = an_spi.prgspi(+)
  and ag_contatto.CODDISPONIBILITAROSA = de_disponibilita_rosa.CODDISPONIBILITAROSA (+)
  and ag_contatto.PRGEFFETTOCONTATTO = de_effetto_contatto.PRGEFFETTOCONTATTO (+)
  and AG_CONTATTO.PRGTIPOCONTATTO = DE_TIPO_CONTATTOAG.PRGTIPOCONTATTO (+)
  and AG_CONTATTO.PRGMOTCONTATTO = DE_MOTIVO_CONTATTOAG.PRGMOTCONTATTO (+)
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 or_colloquio.CDNLAVORATORE as CDNLAVORATORE,
 or_colloquio.DATCOLLOQUIO as DATDATAINIZIO,
 null as DATDATAFINE,
 OR_COLLOQUIO.CODCPI as CODCPI,
 null as CODCPIDEST,
 'H'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as NUMANNO,
 null as PRGAZIENDA,
 null as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 null as DESCESITO,
 de_servizio.STRDESCRIZIONE as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'Effettua colloquio con il Servizio di ' || de_servizio.STRDESCRIZIONE
     || decode(OR_PERCORSO_CONCORDATO.PRGPERCORSO, null,' non concordando azioni',' concordando azioni')
   as DESCRIZIONEPERCORSO,
   '' as DescrizionePercorsoAlternativa,
 or_colloquio.PRGCOLLOQUIO as CHIAVEDETTAGLIO,
 null as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 sysdate as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(or_colloquio.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(or_colloquio.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
  or_colloquio,
  OR_PERCORSO_CONCORDATO,
  de_servizio, or_scheda_colloquio
WHERE
 or_colloquio.prgcolloquio = or_percorso_concordato.prgcolloquio(+) and
 or_colloquio.codservizio = de_servizio.codservizio (+) and
 or_colloquio.prgcolloquio = or_scheda_colloquio.prgcolloquio(+)


------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 OR_COLLOQUIO.CDNLAVORATORE as CDNLAVORATORE,
 OR_PERCORSO_CONCORDATO.DATSTIMATA as DATDATAINIZIO,
 null as DATDATAFINE,
 OR_COLLOQUIO.CODCPI as CODCPI,
 null as CODCPIDEST,
 'I'  as CODMONOTIPOINF,
 'Data effettiva' as LDescVarie,
 to_char(OR_PERCORSO_CONCORDATO.DATEFFETTIVA,'dd/mm/yyyy') as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 am_patto_lavoratore.PRGPATTOLAVORATORE as prgRichiestaAz,
 null as NUMANNO,
 null as PRGAZIENDA,
 null as PRGUNITAAZ,
 DE_AZIONE_RAGG.STRDESCRIZIONE as DESCOBIETTIVOAZIONE,
 DE_ESITO.STRDESCRIZIONE as DESCESITO,
 null as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'Concorda ' || de_azione.STRDESCRIZIONE
 ||' con Obiettivo di '
 ||DE_AZIONE_RAGG.STRDESCRIZIONE
 || ', effettuata il '
 ||nvl(to_char(OR_PERCORSO_CONCORDATO.DATEFFETTIVA,'dd/mm/yyyy'),'Non Tracciata')
 ||decode(OR_PERCORSO_CONCORDATO.CODESITO, null, '', '. Azione ')
 ||DE_ESITO.STRDESCRIZIONE
 as DESCRIZIONEPERCORSO,
 '' as DescrizionePercorsoAlternativa,
 OR_PERCORSO_CONCORDATO.PRGPERCORSO as CHIAVEDETTAGLIO,
 null as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 sysdate as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(or_colloquio.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(or_colloquio.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
  OR_PERCORSO_CONCORDATO, DE_AZIONE, OR_COLLOQUIO,
  DE_ESITO, DE_AZIONE_RAGG, am_patto_lavoratore, am_lav_patto_scelta
WHERE AM_PATTO_LAVORATORE.CDNLAVORATORE = OR_COLLOQUIO.CDNLAVORATORE
 AND AM_PATTO_LAVORATORE.PRGPATTOLAVORATORE =  AM_LAV_PATTO_SCELTA.PRGPATTOLAVORATORE
 AND AM_PATTO_LAVORATORE.CODSTATOATTO = 'PR'
 AND AM_LAV_PATTO_SCELTA.codlsttab = 'OR_PER'
 AND OR_PERCORSO_CONCORDATO.PRGAZIONI = DE_AZIONE.PRGAZIONI
 AND OR_COLLOQUIO.PRGCOLLOQUIO = OR_PERCORSO_CONCORDATO.PRGCOLLOQUIO
 AND (OR_PERCORSO_CONCORDATO.PRGPERCORSO (+)= to_number(am_lav_patto_scelta.strchiavetabella))
 AND OR_PERCORSO_CONCORDATO.CODESITO = DE_ESITO.CODESITO (+)
 AND DE_AZIONE.PRGAZIONERAGG = DE_AZIONE_RAGG.PRGAZIONIRAGG
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 AN_LAVORATORE.CDNLAVORATORE as CDNLAVORATORE,
 NVL(DO_LAV_STORIA_ROSA.DATINVIO, TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')) as DATDATAINIZIO,
 null as DATDATAFINE,
 do_richiesta_az.CODCPI as CODCPI,
 null as CODCPIDEST,
 'L'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 DO_INCROCIO.PRGRICHIESTAAZ as prgRichiestaAz,
 TO_CHAR(DO_RICHIESTA_AZ.NUMRICHIESTA) || '/' || TO_CHAR(DO_RICHIESTA_AZ.NUMANNO)  as NUMANNO,
 DO_RICHIESTA_AZ.PRGAZIENDA as PRGAZIENDA,
 DO_RICHIESTA_AZ.PRGUNITA   as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 DO_ESITO_CANDIDATO.CODESITODAAZIENDA as DESCESITO,
 null as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'Segnalazione nella rosa n. '
  ||TO_CHAR(DO_RICHIESTA_AZ.NUMRICHIESTA) || '/' || TO_CHAR(DO_RICHIESTA_AZ.NUMANNO)
  ||' (p. '  ||TO_CHAR(DO_ROSA.PRGROSA) || ')'
  ||' per la Mansione di '||DE_MANSIONE.STRDESCRIZIONE
  ||'; Tipo Incrocio: ' || NVL(TINCROCIO.STRDESCRIZIONE, 'Non Tracciato')
    as DESCRIZIONEPERCORSO,
  '' as DescrizionePercorsoAlternativa,
 DO_ROSA.PRGROSA as CHIAVEDETTAGLIO,
 'MANSIONE:'||DE_MANSIONE.STRDESCRIZIONE as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 sysdate as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 DO_LAV_STORIA_ROSA,
 AN_LAVORATORE,
 AN_UNITA_AZIENDA,
 DO_ROSA,
 DO_INCROCIO,
 DO_ALTERNATIVA,
 DO_MANSIONE,
 DE_MANSIONE,
 DE_TIPO_INCROCIO TINCROCIO,
 DO_RICHIESTA_AZ,
 DO_ESITO_CANDIDATO
where
 DO_LAV_STORIA_ROSA.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE
 and   DO_LAV_STORIA_ROSA.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA
 and   DO_LAV_STORIA_ROSA.PRGUNITA = AN_UNITA_AZIENDA.PRGUNITA
 and   DO_LAV_STORIA_ROSA.PRGROSA = DO_ROSA.PRGROSA
 and   DO_ROSA.PRGINCROCIO = DO_INCROCIO.PRGINCROCIO
 and   (DO_INCROCIO.PRGRICHIESTAAZ = DO_ALTERNATIVA.PRGRICHIESTAAZ
 and nvl(DO_INCROCIO.PRGALTERNATIVA,do_alternativa.PRGALTERNATIVA)=do_alternativa.PRGALTERNATIVA)
 and   DO_ALTERNATIVA.PRGRICHIESTAAZ=DO_MANSIONE.PRGRICHIESTAAZ
 and   DO_ALTERNATIVA.PRGALTERNATIVA =  DO_MANSIONE.PRGALTERNATIVA
 and   DO_MANSIONE.CODMANSIONE = DE_MANSIONE.CODMANSIONE
 and   DO_LAV_STORIA_ROSA.DATINVIO is not null
 and   DO_ROSA.PRGROSAFIGLIA is null
 and   DO_LAV_STORIA_ROSA.PRGRICHIESTA = DO_RICHIESTA_AZ.PRGRICHIESTAAZ
 and   DO_INCROCIO.PRGTIPOINCROCIO= TINCROCIO.PRGTIPOINCROCIO
 and   DO_ESITO_CANDIDATO.PRGRICHIESTAAZ (+)= DO_LAV_STORIA_ROSA.PRGRICHIESTA
 and   DO_ESITO_CANDIDATO.CDNLAVORATORE (+) = DO_LAV_STORIA_ROSA.CDNLAVORATORE
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
select
 AN_LAVORATORE.CDNLAVORATORE as CDNLAVORATORE,
 trunc(NVL(DO_LAV_STORIA_ROSA.dtmcanc,DO_LAV_STORIA_ROSA.DATDISPONIBILITA)) as DATDATAINIZIO,
 null as DATDATAFINE,
 do_richiesta_az.CODCPI as CODCPI,
 null as CODCPIDEST,
 'N'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 DO_LAV_STORIA_ROSA.PRGRICHIESTA AS prgRichiestaAz,
 TO_CHAR(DO_RICHIESTA_AZ.NUMRICHIESTA) || '/' || TO_CHAR(DO_RICHIESTA_AZ.NUMANNO)  as NUMANNO,
 DO_RICHIESTA_AZ.PRGAZIENDA as PRGAZIENDA,
 DO_RICHIESTA_AZ.PRGUNITA   as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 null as DESCESITO,
 null as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'ESCLUSIONE DA ROSE; '
 ||'RICH. N.: ' ||TO_CHAR(NVL(DO_RICHIESTA_AZ.NUMRICHIESTAORIG, DO_RICHIESTA_AZ.NUMRICHIESTA)) || '/' || TO_CHAR(DO_RICHIESTA_AZ.NUMANNO)
 ||'; P. rosa: '  ||TO_CHAR(DO_ROSA.PRGROSA)
 ||'; MANSIONE:'||DE_MANSIONE.STRDESCRIZIONE
 || '; Tipo Incrocio: ' || NVL(TINCROCIO.STRDESCRIZIONE, 'Non Tracciato')
 || '; Motivo: '|| NVL(DO_NOMINATIVO.STRMOTIVOCANC,DE_DISPONIBILITA_ROSA.STRDESCRIZIONE)
 as DESCRIZIONEPERCORSO,
 '' as DescrizionePercorsoAlternativa,
 DO_ROSA.PRGROSA as CHIAVEDETTAGLIO,
 'MANSIONE:'||DE_MANSIONE.STRDESCRIZIONE as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 sysdate as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 DO_LAV_STORIA_ROSA,
 AN_LAVORATORE,
 AN_UNITA_AZIENDA,
 DO_ROSA,
 DO_INCROCIO,
 DO_ALTERNATIVA,
 DO_MANSIONE,
 DE_MANSIONE,
 DE_TIPO_INCROCIO TINCROCIO,
 DO_RICHIESTA_AZ,
 DE_DISPONIBILITA_ROSA
 , DO_NOMINATIVO
where
 DO_LAV_STORIA_ROSA.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE
 and   (DO_LAV_STORIA_ROSA.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA and   DO_LAV_STORIA_ROSA.PRGUNITA = AN_UNITA_AZIENDA.PRGUNITA)
 and   DO_LAV_STORIA_ROSA.PRGROSA = DO_ROSA.PRGROSA
 and   DO_ROSA.PRGROSAFIGLIA is null
 and   DO_ROSA.PRGINCROCIO = DO_INCROCIO.PRGINCROCIO
 and (DO_INCROCIO.PRGRICHIESTAAZ = DO_ALTERNATIVA.PRGRICHIESTAAZ and DO_INCROCIO.PRGALTERNATIVA =do_alternativa.PRGALTERNATIVA)
 and (DO_ALTERNATIVA.PRGRICHIESTAAZ=DO_MANSIONE.PRGRICHIESTAAZ and DO_ALTERNATIVA.PRGALTERNATIVA =  DO_MANSIONE.PRGALTERNATIVA)
 and   DO_MANSIONE.CODMANSIONE = DE_MANSIONE.CODMANSIONE
 and   DO_LAV_STORIA_ROSA.PRGRICHIESTA = DO_RICHIESTA_AZ.PRGRICHIESTAAZ
 and   DO_INCROCIO.PRGTIPOINCROCIO= TINCROCIO.PRGTIPOINCROCIO
 and DO_LAV_STORIA_ROSA.coddisponibilitarosa=DE_DISPONIBILITA_ROSA.coddisponibilitarosa (+)
 and (DO_ROSA.Prgrosa=DO_NOMINATIVO.prgrosa and DO_NOMINATIVO.Cdnlavoratore=DO_LAV_STORIA_ROSA.Cdnlavoratore)
 and   DO_LAV_STORIA_ROSA.PRGTIPOROSA=2
 and exists (select 1
			from do_rosa rosa1
			where rosa1.prgrosa = do_lav_storia_rosa.prgrosa and rosa1.prgtiporosa=3
			and nvl(rosa1.DATINVIO, TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')) <> TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
			)
  and (select count(1)
				  from do_lav_storia_rosa lav_storia_rosa
				  where
          lav_storia_rosa.prgrichiesta = DO_LAV_STORIA_ROSA.PRGRICHIESTA
          and lav_storia_rosa.prgtiporosa = 3
				  and lav_storia_rosa.cdnlavoratore = do_lav_storia_rosa.CDNLAVORATORE
          and lav_storia_rosa.prgrosa = do_lav_storia_rosa.prgrosa
				  and nvl(lav_storia_rosa.DATINVIO, TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')) <> TO_DATE('1900-01-01 00:00:00', 'yyyy-mm-dd hh24:mi:ss')
				  and (select nvl(prgrosafiglia,0) from do_rosa r where r.prgrosa=lav_storia_rosa.prgrosa)=0
          ) = 0
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
Select
 am_dich_lav.CDNLAVORATORE as CdnLavoratore,
 am_dich_lav.DATINIZIO as DatDataInizio,
 am_dich_lav.datFine as DatDataFine,
 null as CodCpi,
 null as CodCpiDest,
 'M'  as CodMonoTipoInf,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 null as prgRichiestaAz,
 null as numAnno,
 null as prgAzienda,
 null as prgUnitaAz,
 null as DescObiettivoAzione,
 null as DescEsito,
 null as DescServizio,
 null as DescTipoPatto,
 null as DescStatoOccupaz,
 null as DescStatoOccupazOrig,
 'DICH. REDDITO; Tipo dich.:' || DE_TIPO_DICH.STRDESCRIZIONE
 || '; Stato Atto: ' || de_stato_atto.STRDESCRIZIONE
  as DescrizionePercorso,
  '' as DescrizionePercorsoAlternativa,
 am_dich_lav.PRGDICHLAV  as chiaveDettaglio,
 null as descStato,
 'SERVIZI' as strPosData,
 decode(am_dich_lav.DATFINE, null, sysdate, am_dich_lav.DATFINE)  as dataSort2,
 null as CDNLAVORATORE_CRYPT,
 to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(am_dich_lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(am_dich_lav.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 am_dich_lav, de_tipo_dich, de_stato_atto
where
 am_dich_lav.CODTIPODICH = de_tipo_dich.CODTIPODICH
 and am_dich_lav.CODSTATOATTO = de_stato_atto.codStatoAtto
    and (de_stato_atto.codStatoAtto = 'PR' or
     (de_stato_atto.codStatoAtto = 'AN' and am_dich_lav.CODMOTIVO <> 'ERR')
 )
------------------------------------------------------------------------------------------------------------------
union
------------------------------------------------------------------------------------------------------------------
  select
 AN_LAVORATORE.CDNLAVORATORE as CDNLAVORATORE,
 trunc(ccg.dtmcanc) as DATDATAINIZIO,
 null as DATDATAFINE,
 do_richiesta_az.CODCPI as CODCPI,
 null as CODCPIDEST,
 'P'  as CODMONOTIPOINF,
 null as LDescVarie,
 null as DescVarie,
 null as LdescVarie2,
 null as DescVarie2,
 DO_INCROCIO.PRGRICHIESTAAZ as prgRichiestaAz,
 TO_CHAR(DO_RICHIESTA_AZ.NUMRICHIESTA) || '/' || TO_CHAR(DO_RICHIESTA_AZ.NUMANNO)  as NUMANNO,
 DO_RICHIESTA_AZ.PRGAZIENDA as PRGAZIENDA,
 DO_RICHIESTA_AZ.PRGUNITA   as PRGUNITAAZ,
 null as DESCOBIETTIVOAZIONE,
 null as DESCESITO,
 null as DESCSERVIZIO,
 null as DESCTIPOPATTO,
 null as DESCSTATOOCCUPAZ,
 null as DESCSTATOOCCUPAZORIG,
 'Cancellazione da Graduatorie CM/art.1; Rich. n. '
  ||TO_CHAR(DO_RICHIESTA_AZ.NUMRICHIESTA) || '/' || TO_CHAR(DO_RICHIESTA_AZ.NUMANNO) ||';'
  ||' p. graduatoria:'  ||TO_CHAR(DO_ROSA.PRGROSA) || ';'
  ||' Tipo graduatoria: ' || NVL(TINCROCIO.STRDESCRIZIONE, 'Non Tracciato') ||';'
    ||' Motivo: '|| dmcg.strdescrizione || '' || decode(ccg.strspecifica, null , '', ' - '||ccg.strspecifica)
    as DESCRIZIONEPERCORSO,
  '' as DescrizionePercorsoAlternativa,
 DO_ROSA.PRGROSA as CHIAVEDETTAGLIO,
 null as DESCSTATO,
 'SERVIZI' as STRPOSDATA,
 sysdate as dataSort2,
  null as CDNLAVORATORE_CRYPT,
  to_number(substr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), 1,
            instr(PG_MOVIMENTI.CalcolaAnzianita(AN_LAVORATORE.cdnLavoratore, to_char(sysdate, 'dd/mm/yyyy')), '-', 1)-1)) as mesiAnz
from
 AN_LAVORATORE,
 DO_ROSA,
 DO_INCROCIO,
 DE_TIPO_INCROCIO TINCROCIO,
 DO_RICHIESTA_AZ,
  Cm_Canc_Grad ccg,
  de_mot_canc_grad dmcg
where
 DO_ROSA.PRGINCROCIO = DO_INCROCIO.PRGINCROCIO
  and   do_incrocio.prgrichiestaaz = DO_RICHIESTA_AZ.PRGRICHIESTAAZ
 and   DO_INCROCIO.PRGTIPOINCROCIO= TINCROCIO.PRGTIPOINCROCIO
  and   ccg.prgrosa = do_rosa.prgrosa
  and   ccg.cdnlavoratore = an_lavoratore.cdnlavoratore
  and   ccg.codmotcancgrad = dmcg.codmotcancgrad
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_pr_dis_provincia_cm.sql
************************************************************************************** */


create or replace view vw_pr_dis_provincia_cm as
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, PR_DIS_PROVINCIA.CODPROVINCIA
from PR_DIS_PROVINCIA
  inner join PR_MANSIONE on (PR_DIS_PROVINCIA.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join pr_dispo_l68 on (PR_MANSIONE.CDNLAVORATORE=pr_dispo_l68.CDNLAVORATORE)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='L') and pr_dispo_l68.codmonodispol8 = 'S'
--
union
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, DE_PROVINCIA.CODPROVINCIA
from PR_DIS_REGIONE
  inner join PR_MANSIONE on (PR_DIS_REGIONE.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join pr_dispo_l68 on (PR_MANSIONE.CDNLAVORATORE=pr_dispo_l68.CDNLAVORATORE)
  inner join DE_PROVINCIA on (PR_DIS_REGIONE.CODREGIONE=DE_PROVINCIA.CODREGIONE)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='L') and pr_dispo_l68.codmonodispol8 = 'S'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_pr_dis_provincia.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_PR_DIS_PROVINCIA ( CDNLAVORATORE, CODMANSIONE, CODPROVINCIA ) AS
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, PR_DIS_PROVINCIA.CODPROVINCIA
from PR_DIS_PROVINCIA
  inner join PR_MANSIONE on (PR_DIS_PROVINCIA.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join PR_VALIDITA on (PR_MANSIONE.CDNLAVORATORE=PR_VALIDITA.CDNLAVORATORE)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='P') and PR_VALIDITA.CODTIPOVALIDITA = 'DL'
--
union
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, DE_PROVINCIA.CODPROVINCIA
from PR_DIS_REGIONE
  inner join PR_MANSIONE on (PR_DIS_REGIONE.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join PR_VALIDITA on (PR_MANSIONE.CDNLAVORATORE=PR_VALIDITA.CDNLAVORATORE)
  inner join DE_PROVINCIA on (PR_DIS_REGIONE.CODREGIONE=DE_PROVINCIA.CODREGIONE)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='P') and PR_VALIDITA.CODTIPOVALIDITA = 'DL'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_pr_dis_territorio_cm.sql
************************************************************************************** */


create or replace view vw_pr_dis_territorio_cm as
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, PR_DIS_COMUNE.CODCOM
from PR_DIS_COMUNE, PR_MANSIONE, PR_DISPO_L68
where (PR_DIS_COMUNE.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
	  AND (pr_mansione.CDNLAVORATORE = PR_DISPO_L68.CDNLAVORATORE)
	  and pr_dispo_l68.codmonodispol8 = 'S'
	  AND (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='L')
--
union
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, DE_COMUNE.CODCOM
from PR_DIS_PROVINCIA
  inner join PR_MANSIONE on (PR_DIS_PROVINCIA.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join PR_DISPO_L68 on (PR_MANSIONE.CDNLAVORATORE=PR_DISPO_L68.CDNLAVORATORE)
  inner join DE_COMUNE on (PR_DIS_PROVINCIA.CODPROVINCIA=DE_COMUNE.CODPROVINCIA)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='L') and pr_dispo_l68.codmonodispol8 = 'S'
--
union
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, DE_COMUNE.CODCOM
from PR_DIS_REGIONE
  inner join PR_MANSIONE on (PR_DIS_REGIONE.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join PR_DISPO_L68 on (PR_MANSIONE.CDNLAVORATORE=PR_DISPO_L68.CDNLAVORATORE)
  inner join DE_PROVINCIA on (PR_DIS_REGIONE.CODREGIONE=DE_PROVINCIA.CODREGIONE)
  inner join DE_COMUNE on (DE_PROVINCIA.CODPROVINCIA=DE_COMUNE.CODPROVINCIA)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='L') and pr_dispo_l68.codmonodispol8 = 'S'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_pr_dis_territorio.sql
************************************************************************************** */


create or replace view vw_pr_dis_territorio as
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, PR_DIS_COMUNE.CODCOM
from PR_DIS_COMUNE, PR_MANSIONE, PR_VALIDITA 
where (PR_DIS_COMUNE.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
	  AND (pr_mansione.CDNLAVORATORE = pr_validita.CDNLAVORATORE)
	  and pr_validita.CODTIPOVALIDITA = 'DL'
	  AND (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='P')
--
union
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, DE_COMUNE.CODCOM
from PR_DIS_PROVINCIA
  inner join PR_MANSIONE on (PR_DIS_PROVINCIA.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join PR_VALIDITA on (PR_MANSIONE.CDNLAVORATORE=PR_VALIDITA.CDNLAVORATORE)
  inner join DE_COMUNE on (PR_DIS_PROVINCIA.CODPROVINCIA=DE_COMUNE.CODPROVINCIA)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='P') and PR_VALIDITA.CODTIPOVALIDITA = 'DL'
--
union
select PR_MANSIONE.CDNLAVORATORE, PR_MANSIONE.CODMANSIONE, DE_COMUNE.CODCOM
from PR_DIS_REGIONE
  inner join PR_MANSIONE on (PR_DIS_REGIONE.PRGMANSIONE=PR_MANSIONE.PRGMANSIONE)
  inner join PR_VALIDITA on (PR_MANSIONE.CDNLAVORATORE=PR_VALIDITA.CDNLAVORATORE)
  inner join DE_PROVINCIA on (PR_DIS_REGIONE.CODREGIONE=DE_PROVINCIA.CODREGIONE)
  inner join DE_COMUNE on (DE_PROVINCIA.CODPROVINCIA=DE_COMUNE.CODPROVINCIA)
where (PR_MANSIONE.FLGDISPONIBILE='S' OR PR_MANSIONE.FLGDISPONIBILE='P') and PR_VALIDITA.CODTIPOVALIDITA = 'DL'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_prof_dispo.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_PROF_DISPO
(ORDINE, ETICHETTA, CDNLAVORATORE, PRGMANSIONE, STRDESCRIZIONE, FLGDISPONIBILE)
AS 
SELECT   "ORDINE"        ,
         "ETICHETTA"     ,
         "CDNLAVORATORE" ,
         "PRGMANSIONE"   ,
         "STRDESCRIZIONE",
         "FLGDISPONIBILE"
FROM
         (SELECT 1          AS ordine      ,
                 'Mansione' AS etichetta   ,
                 pr_mansione.cdnlavoratore ,
                 pr_mansione.prgmansione   ,
                 de_mansione.strdescrizione,
                 pr_mansione.flgdisponibile
         FROM    pr_mansione,
                 de_mansione
         WHERE   pr_mansione.codmansione = de_mansione.codmansione
         UNION
         SELECT 2        AS ordine        ,
                'Comuni' AS etichetta     ,
                pr_mansione.cdnlavoratore ,
                pr_mansione.prgmansione   ,
                de_comune.strdenominazione,
                pr_mansione.flgdisponibile
         FROM   pr_mansione  ,
                pr_dis_comune,
                de_comune
         WHERE  pr_mansione.prgmansione  = pr_dis_comune.prgmansione(+)
                AND pr_dis_comune.codcom = de_comune.codcom(+)
         UNION
         SELECT 3           AS ordine        ,
                'Provincie' AS etichetta     ,
                pr_mansione.cdnlavoratore    ,
                pr_mansione.prgmansione      ,
                de_provincia.strdenominazione,
                pr_mansione.flgdisponibile
         FROM   pr_mansione     ,
                pr_dis_provincia,
                de_provincia
         WHERE  pr_mansione.prgmansione           = pr_dis_provincia.prgmansione
                AND pr_dis_provincia.codprovincia = de_provincia.codprovincia
         UNION
         SELECT 4         AS ordine        ,
                'Regioni' AS etichetta     ,
                pr_mansione.cdnlavoratore  ,
                pr_mansione.prgmansione    ,
                de_regione.strdenominazione,
                pr_mansione.flgdisponibile
         FROM   pr_mansione   ,
                pr_dis_regione,
                de_regione
         WHERE  pr_mansione.prgmansione       = pr_dis_regione.prgmansione
                AND pr_dis_regione.codregione = de_regione.codregione
         UNION
         SELECT 5           AS ordine      ,
                'Contratto' AS etichetta   ,
                pr_mansione.cdnlavoratore  ,
                pr_mansione.prgmansione    ,
                de_contratto.strdescrizione,
                pr_mansione.flgdisponibile
         FROM   pr_mansione     ,
                pr_dis_contratto,
                de_contratto
         WHERE  pr_mansione.prgmansione           = pr_dis_contratto.prgmansione
                AND pr_dis_contratto.codcontratto = de_contratto.codcontratto
                AND de_contratto.flgdisponibilita = 'S'
         UNION
         SELECT 6        AS ordine       ,
                'Orario' AS etichetta    ,
                pr_mansione.cdnlavoratore,
                pr_mansione.prgmansione  ,
                de_orario.strdescrizione ,
                pr_mansione.flgdisponibile
         FROM   de_orario    ,
                pr_dis_orario,
                pr_mansione
         WHERE  de_orario.codorario           = pr_dis_orario.codorario
                AND pr_dis_orario.prgmansione = pr_mansione.prgmansione
         UNION
         SELECT 7       AS ordine        ,
                'Turni' AS etichetta     ,
                pr_mansione.cdnlavoratore,
                pr_mansione.prgmansione  ,
                de_turno.strdescrizione  ,
                pr_mansione.flgdisponibile
         FROM   pr_mansione ,
                pr_dis_turno,
                de_turno
         WHERE  pr_dis_turno.codturno        = de_turno.codturno
                AND pr_dis_turno.prgmansione = pr_mansione.prgmansione
         UNION
         SELECT 8       AS ordine         ,
                'Stato' AS etichetta      ,
                pr_mansione.cdnlavoratore ,
                pr_mansione.prgmansione   ,
                de_comune.strdenominazione,
                pr_mansione.flgdisponibile
         FROM   pr_mansione ,
                pr_dis_stato,
                de_comune
         WHERE  pr_mansione.prgmansione = pr_dis_stato.prgmansione
                AND pr_dis_stato.codcom = de_comune.codcom
         )
ORDER BY cdnlavoratore,
         prgmansione  ,
         ordine
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_report_settima_slot.sql
************************************************************************************** */


create or replace view vw_report_settima_slot as
select ag_slot.codcpi, 2 as numTipo , 'Prenotabili' as strDescTipo, 1 as fascia, '0->8' as
strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_slot
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 0 and 759
and de_stato_slot.FLGPRENOTABILE = 'S'
group by ag_slot.codcpi, 2 , 1, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 2 as numTipo , 'Prenotabili' as strDescTipo, 2 as fascia,
'8->10' as strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 800 and 959
and de_stato_slot.FLGPRENOTABILE = 'S'
group by ag_SLOT.codcpi, 0 , 2, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_slot.codcpi, 2 as numTipo , 'Prenotabili' as strDescTipo, 3 as fascia,
'10->12' as strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1000 and 1159
and de_stato_slot.FLGPRENOTABILE = 'S'
group by ag_SLOT.codcpi, 2 , 3, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 2 as numTipo, 'Prenotabili' as strDescTipo, 4 as fascia,
'12-->14' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data , count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1200 and 1359
and de_stato_slot.FLGPRENOTABILE = 'S'
group by ag_SLOT.codcpi, 2 , 4, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 2 as numTipo, 'Prenotabili' as strDescTipo, 5 as fascia,
'14-->16' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data, count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1400 and 1559
and de_stato_slot.FLGPRENOTABILE = 'S'
group by ag_SLOT.codcpi, 2 , 5, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 2 as numTipo, 'Prenotabili' as strDescTipo, 6 as fascia,
'Dalle 16' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data, count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1600 and 2359
and de_stato_slot.FLGPRENOTABILE = 'S'
group by ag_SLOT.codcpi, 2 , 6, trunc(ag_SLOT.DTMDATAORA)
UNION all
select ag_slot.codcpi, 3 as numTipo , 'Attivi' as strDescTipo, 1 as fascia, '0->8' as
strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_slot
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 0 and 759
and de_stato_slot.FLGATTIVO = 'S'
group by ag_slot.codcpi, 3 , 1, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 3 as numTipo , 'Attivi' as strDescTipo, 2 as fascia,
'8->10' as strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 800 and 959
and de_stato_slot.FLGATTIVO = 'S'
group by ag_SLOT.codcpi, 0 , 2, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_slot.codcpi, 3 as numTipo , 'Attivi' as strDescTipo, 3 as fascia,
'10->12' as strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1000 and 1159
and de_stato_slot.FLGATTIVO = 'S'
group by ag_SLOT.codcpi, 3 , 3, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 3 as numTipo, 'Attivi' as strDescTipo, 4 as fascia,
'12-->14' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data , count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1200 and 1359
and de_stato_slot.FLGATTIVO = 'S'
group by ag_SLOT.codcpi, 3 , 4, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 3 as numTipo, 'Attivi' as strDescTipo, 5 as fascia,
'14-->16' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data, count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1400 and 1559
and de_stato_slot.FLGATTIVO = 'S'
group by ag_SLOT.codcpi, 3 , 5, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 3 as numTipo, 'Attivi' as strDescTipo, 6 as fascia,
'Dalle 16' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data, count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1600 and 2359
and de_stato_slot.FLGATTIVO = 'S'
group by ag_SLOT.codcpi, 3 , 6, trunc(ag_SLOT.DTMDATAORA)
UNION all
select ag_slot.codcpi, 4 as numTipo, 'Incongruenti' as strDescTipo, 1 as fascia,
'0-->8' as strDescFascia,
 trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_slot where PG_AGENDA.pdSlotErr(ag_slot.CODCPI,ag_slot.PRGSLOT) is not null
and to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 0 and 759
group by ag_slot.codcpi, 4 , 1, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 4 as numTipo, 'Incongruenti' as strDescTipo, 2 as fascia,
'8-->10' as strDescFascia,
 trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_slot where PG_AGENDA.pdSlotErr(ag_slot.CODCPI,ag_slot.PRGSLOT) is not null
and to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 800 and 959
group by ag_slot.codcpi, 4 , 2, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 4 as numTipo, 'Incongruenti' as strDescTipo, 3 as fascia,
'10-->12' as strDescFascia,
 trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGslot) as numero
from ag_slot where PG_AGENDA.pdSlotErr(ag_slot.CODCPI,ag_slot.PRGSLOT) is not null
and to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 1000 and 1159
group by ag_slot.codcpi, 4 , 3, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 4 as numTipo, 'Incongruenti' as strDescTipo, 4 as fascia,
'12-->14' as strDescFascia,
 trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGslot) as numero
from ag_slot where PG_AGENDA.pdSlotErr(ag_slot.CODCPI,ag_slot.PRGSLOT) is not null
and to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 1200 and 1359
group by ag_slot.codcpi, 4 , 4, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 4 as numTipo, 'Incongruenti' as strDescTipo, 5 as fascia,
'14-->16' as strDescFascia,
 trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGslot) as numero
from ag_slot where PG_AGENDA.pdSlotErr(ag_slot.CODCPI,ag_slot.PRGSLOT) is not null
and to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 1400 and 1559
group by ag_slot.codcpi, 4 , 5, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 4 as numTipo, 'Incongruenti' as strDescTipo, 6 as fascia,
'Dalle 16' as strDescFascia,
 trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGslot) as numero
from ag_slot where PG_AGENDA.pdSlotErr(ag_slot.CODCPI,ag_slot.PRGSLOT) is not null
and to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 1600 and 2359
group by ag_slot.codcpi, 4 , 6, trunc(ag_slot.DTMDATAORA)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_report_settima.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_REPORT_SETTIMA ( CODCPI, 
NUMTIPO, STRDESCTIPO, FASCIA, STRDESCFASCIA, 
DATA, NUMERO ) AS select ag_agenda.codcpi, 0 as numTipo , 'Totale' as strDescTipo, 1 as fascia, '0->8' as
strDescFascia,
trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 0 and 759
group by ag_agenda.codcpi, 0 , 1, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 0 as numTipo , 'Totale' as strDescTipo, 2 as fascia,
'8->10' as strDescFascia,
trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 800 and 959
group by ag_agenda.codcpi, 0 , 2, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 0 as numTipo , 'Totale' as strDescTipo, 3 as fascia,
'10->12' as strDescFascia,
trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1000 and 1159
group by ag_agenda.codcpi, 0 , 3, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 0 as numTipo, 'Totale' as strDescTipo, 4 as fascia,
'12-->14' as strDescFascia,
trunc(ag_agenda.DTMDATAORA) as data , count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1200 and 1359
group by ag_agenda.codcpi, 0 , 4, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 0 as numTipo, 'Totale' as strDescTipo, 5 as fascia,
'14-->16' as strDescFascia,
trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1400 and 1559
group by ag_agenda.codcpi, 0 , 5, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 0 as numTipo, 'Totale' as strDescTipo, 6 as fascia,
'Dalle 16' as strDescFascia,
trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1600 and 2359
group by ag_agenda.codcpi, 0 , 6, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 1 as numTipo, 'Incongruenti' as strDescTipo, 1 as fascia,
'0-->8' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda where PG_AGENDA.PDAPPERR(ag_agenda.CODCPI,ag_agenda.PRGAPPUNTAMENTO) is not null
and to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 0 and 759
group by ag_agenda.codcpi, 1 , 1, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 1 as numTipo, 'Incongruenti' as strDescTipo, 2 as fascia,
'8-->10' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda where PG_AGENDA.PDAPPERR(ag_agenda.CODCPI,ag_agenda.PRGAPPUNTAMENTO) is not null
and to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 800 and 959
group by ag_agenda.codcpi, 1 , 2, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 1 as numTipo, 'Incongruenti' as strDescTipo, 3 as fascia,
'10-->12' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda where PG_AGENDA.PDAPPERR(ag_agenda.CODCPI,ag_agenda.PRGAPPUNTAMENTO) is not null
and to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1000 and 1159
group by ag_agenda.codcpi, 1 , 3, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 1 as numTipo, 'Incongruenti' as strDescTipo, 4 as fascia,
'12-->14' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda where PG_AGENDA.PDAPPERR(ag_agenda.CODCPI,ag_agenda.PRGAPPUNTAMENTO) is not null
and to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1200 and 1359
group by ag_agenda.codcpi, 1 , 4, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 1 as numTipo, 'Incongruenti' as strDescTipo, 5 as fascia,
'14-->16' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda where PG_AGENDA.PDAPPERR(ag_agenda.CODCPI,ag_agenda.PRGAPPUNTAMENTO) is not null
and to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1400 and 1559
group by ag_agenda.codcpi, 1 , 5, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 1 as numTipo, 'Incongruenti' as strDescTipo, 6 as fascia,
'Dalle 16' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda where PG_AGENDA.PDAPPERR(ag_agenda.CODCPI,ag_agenda.PRGAPPUNTAMENTO) is not null
and to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1600 and 2359
group by ag_agenda.codcpi, 1 , 6, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 2 as numTipo, 'Non completi' as strDescTipo, 1 as fascia,
'0-->8' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 0 and 759
and
(ag_agenda.CODSERVIZIO is null
or ag_agenda.NUMMINUTI is null
or ag_agenda.PRGSPI is null
or (ag_agenda.PRGAZIENDA is null and not exists (select * from AG_LAVORATORE where
ag_agenda.codcpi = ag_lavoratore.CODCPI and ag_agenda.PRGAPPUNTAMENTO =
ag_lavoratore.PRGAPPUNTAMENTO)))
group by ag_agenda.codcpi, 2 , 1, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 2 as numTipo, 'Non completi' as strDescTipo, 2 as fascia,
'8-->10' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 800 and 959
and
(ag_agenda.CODSERVIZIO is null
or ag_agenda.NUMMINUTI is null
or ag_agenda.PRGSPI is null
or (ag_agenda.PRGAZIENDA is null and not exists (select * from AG_LAVORATORE where
ag_agenda.codcpi = ag_lavoratore.CODCPI and ag_agenda.PRGAPPUNTAMENTO =
ag_lavoratore.PRGAPPUNTAMENTO)))
group by ag_agenda.codcpi, 2 , 2, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 2 as numTipo, 'Non completi' as strDescTipo, 3 as fascia,
'10-->12' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1000 and 1159
and
(ag_agenda.CODSERVIZIO is null
or ag_agenda.NUMMINUTI is null
or ag_agenda.PRGSPI is null
or (ag_agenda.PRGAZIENDA is null and not exists (select * from AG_LAVORATORE where
ag_agenda.codcpi = ag_lavoratore.CODCPI and ag_agenda.PRGAPPUNTAMENTO =
ag_lavoratore.PRGAPPUNTAMENTO)))
group by ag_agenda.codcpi, 2 , 3, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 2 as numTipo, 'Non completi' as strDescTipo, 4 as fascia,
'12-->14' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1200 and 1359
and
(ag_agenda.CODSERVIZIO is null
or ag_agenda.NUMMINUTI is null
or ag_agenda.PRGSPI is null
or (ag_agenda.PRGAZIENDA is null and not exists (select * from AG_LAVORATORE where
ag_agenda.codcpi = ag_lavoratore.CODCPI and ag_agenda.PRGAPPUNTAMENTO =
ag_lavoratore.PRGAPPUNTAMENTO)))
group by ag_agenda.codcpi, 2 , 4, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 2 as numTipo, 'Non completi' as strDescTipo, 5 as fascia,
'14-->>16' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1400 and 1559
and
(ag_agenda.CODSERVIZIO is null
or ag_agenda.NUMMINUTI is null
or ag_agenda.PRGSPI is null
or (ag_agenda.PRGAZIENDA is null and not exists (select * from AG_LAVORATORE where
ag_agenda.codcpi = ag_lavoratore.CODCPI and ag_agenda.PRGAPPUNTAMENTO =
ag_lavoratore.PRGAPPUNTAMENTO)))
group by ag_agenda.codcpi, 2 , 5, trunc(ag_agenda.DTMDATAORA)
union all
select ag_agenda.codcpi, 2 as numTipo, 'Non completi' as strDescTipo, 6 as fascia,
'Dalle 16' as strDescFascia,
 trunc(ag_agenda.DTMDATAORA) as data, count(ag_agenda.PRGAPPUNTAMENTO) as numero
from ag_agenda
where to_number(to_char(ag_agenda.DTMDATAORA, 'hh24mi')) between 1600 and 2359
and
(ag_agenda.CODSERVIZIO is null
or ag_agenda.NUMMINUTI is null
or ag_agenda.PRGSPI is null
or (ag_agenda.PRGAZIENDA is null and not exists (select * from AG_LAVORATORE where
ag_agenda.codcpi = ag_lavoratore.CODCPI and ag_agenda.PRGAPPUNTAMENTO =
ag_lavoratore.PRGAPPUNTAMENTO)))
group by ag_agenda.codcpi, 2 , 6, trunc(ag_agenda.DTMDATAORA)
union all
 	  	   	 -- inizio
select ag_slot.codcpi, 3 as numTipo , 'Slot' as strDescTipo, 1 as fascia, '0->8' as
strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_slot
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_slot.DTMDATAORA, 'hh24mi')) between 0 and 759
and de_stato_slot.FLGPRENOTABILE = 'S'
and ( ( (nvl(ag_slot.NUMAZIENDE,0)+nvl(ag_slot.NUMLAVORATORI,0)) - ( nvl(ag_slot.NUMAZIENDEPRENOTATE,0) + nvl(ag_slot.NUMLAVPRENOTATI,0)) )  >0)  
group by ag_slot.codcpi, 3 , 1, trunc(ag_slot.DTMDATAORA)
union all
select ag_slot.codcpi, 3 as numTipo , 'Slot' as strDescTipo, 2 as fascia,
'8->10' as strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 800 and 959
and de_stato_slot.FLGPRENOTABILE = 'S'
and ( ( (nvl(ag_slot.NUMAZIENDE,0)+nvl(ag_slot.NUMLAVORATORI,0)) - ( nvl(ag_slot.NUMAZIENDEPRENOTATE,0) + nvl(ag_slot.NUMLAVPRENOTATI,0)) )  >0)  
group by ag_SLOT.codcpi, 0 , 2, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_slot.codcpi, 3 as numTipo , 'Slot' as strDescTipo, 3 as fascia,
'10->12' as strDescFascia,
trunc(ag_slot.DTMDATAORA) as data, count(ag_slot.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1000 and 1159
and de_stato_slot.FLGPRENOTABILE = 'S'
and ( ( (nvl(ag_slot.NUMAZIENDE,0)+nvl(ag_slot.NUMLAVORATORI,0)) - ( nvl(ag_slot.NUMAZIENDEPRENOTATE,0) + nvl(ag_slot.NUMLAVPRENOTATI,0)) )  >0)  
group by ag_SLOT.codcpi, 3 , 3, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 3 as numTipo, 'Slot' as strDescTipo, 4 as fascia,
'12-->14' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data , count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1200 and 1359
and de_stato_slot.FLGPRENOTABILE = 'S'
and ( ( (nvl(ag_slot.NUMAZIENDE,0)+nvl(ag_slot.NUMLAVORATORI,0)) - ( nvl(ag_slot.NUMAZIENDEPRENOTATE,0) + nvl(ag_slot.NUMLAVPRENOTATI,0)) )  >0)  
group by ag_SLOT.codcpi, 3 , 4, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 3 as numTipo, 'Slot' as strDescTipo, 5 as fascia,
'14-->16' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data, count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1400 and 1559
and de_stato_slot.FLGPRENOTABILE = 'S'
and ( ( (nvl(ag_slot.NUMAZIENDE,0)+nvl(ag_slot.NUMLAVORATORI,0)) - ( nvl(ag_slot.NUMAZIENDEPRENOTATE,0) + nvl(ag_slot.NUMLAVPRENOTATI,0)) )  >0)  
group by ag_SLOT.codcpi, 3 , 5, trunc(ag_SLOT.DTMDATAORA)
union all
select ag_SLOT.codcpi, 3 as numTipo, 'Slot' as strDescTipo, 6 as fascia,
'Dalle 16' as strDescFascia,
trunc(ag_SLOT.DTMDATAORA) as data, count(ag_SLOT.PRGSLOT) as numero
from ag_SLOT
inner join DE_STATO_SLOT on (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT)
where to_number(to_char(ag_SLOT.DTMDATAORA, 'hh24mi')) between 1600 and 2359
and de_stato_slot.FLGPRENOTABILE = 'S'
and ( ( (nvl(ag_slot.NUMAZIENDE,0)+nvl(ag_slot.NUMLAVORATORI,0)) - ( nvl(ag_slot.NUMAZIENDEPRENOTATE,0) + nvl(ag_slot.NUMLAVPRENOTATI,0)) )  >0)  
group by ag_SLOT.codcpi, 3 , 6, trunc(ag_SLOT.DTMDATAORA)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_RIC_PERC_LAV_E_UNI.sql
************************************************************************************** */


--VW_RIC_PERC_LAV_E_UNI
CREATE OR REPLACE VIEW VW_RIC_PERC_LAV_E_UNI AS SELECT    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-'||CHR(38)||'gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) status,
          NULL AS servizi, NULL AS movimenti, NULL AS missioni,
          'PAGE=PercorsoMobilitaInfoStorDettPage'||CHR(38)||'prgMobilitaIscr='
                     || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_e_uni lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ric_perc_lav_o_sq.sql
************************************************************************************** */


CREATE  OR REPLACE VIEW VW_RIC_PERC_LAV_O_SQ AS SELECT NULL AS status, NULL AS servizi,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-'||CHR(38)||'gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS movimenti,
          NULL AS missioni,
             'PAGE=PercorsoMovimentiCollegatiPage'||CHR(38)||'PrgMovimentoColl='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_o_sq lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_RIC_PERC_LAV_R.SQL
************************************************************************************** */


--VW_RIC_PERC_LAV_E_ENC DIVENTA VW_RIC_PERC_LAV_R
CREATE OR REPLACE VIEW VW_RIC_PERC_LAV_R AS SELECT    TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-'||CHR(38)||'gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) status,
          NULL AS servizi, NULL AS movimenti, NULL AS missioni,
             'PAGE=PercorsoCollMiratoInfStorDettPage'||CHR(38)||'prgCMIscr='
                     || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2, lav.cdnlavoratore_crypt
     FROM vw_gettipoinf_r lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_RIC_PERC_LAV_S.SQL
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_RIC_PERC_LAV_S 
AS
  SELECT TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
    || DECODE (lav.datdatafine, NULL, '', '-'
    ||CHR(38)
    ||'gt;'
    || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy') ) status,
    NULL AS servizi,
    NULL AS movimenti,
    NULL AS missioni,
    'PAGE=PercorsoCMIscrLSDettPage'
    ||CHR(38)
    ||'PRGISCRART1='
    || lav.chiavedettaglio AS url,
    lav.cdnlavoratore,
    lav.descrizionepercorso,
    lav.chiavedettaglio,
    lav.codmonotipoinf,
    de_cpi.strdescrizione AS cpirif,
    lav.datdatainizio,
    lav.datasort2
  FROM vw_gettipoinf_s lav,
    de_cpi
  WHERE lav.codcpi = de_cpi.codcpi(+) 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ric_per_lav_b_nq.sql
************************************************************************************** */


--usa la vista _NQ per non visualizzare la qualifica.
CREATE OR REPLACE VIEW VW_RIC_PERC_LAV_B_NQ AS SELECT NULL status, NULL AS servizi,
             TO_CHAR (lav.datdatainizio, 'dd/mm/yyyy')
          || DECODE (lav.datdatafine,
                     NULL, '',
                     '-'||CHR(38)||'gt;' || TO_CHAR (lav.datdatafine, 'dd/mm/yyyy')
                    ) AS movimenti,
          NULL AS missioni,
             'PAGE=PercorsoMovimentiCollegatiPage'||CHR(38)||'PrgMovimentoColl='
          || lav.chiavedettaglio AS url,
          lav.cdnlavoratore, lav.descrizionepercorso, lav.chiavedettaglio,
          lav.codmonotipoinf, de_cpi.strdescrizione AS cpirif,
          lav.datdatainizio, lav.datasort2
     FROM vw_gettipoinf_b_nq lav, de_cpi
    WHERE lav.codcpi = de_cpi.codcpi(+)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_rpt_an_lav_storia_inf_coll.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_RPT_AN_LAV_STORIA_INF_COLL AS
SELECT al.prglavstoriainfcoll, al.prglavstoriainf, al.codlsttab,
       CAST (al.strchiavetabella2 AS NUMBER (38)) AS strchiavetabella2,
       CAST (al.strchiavetabella AS NUMBER (38)) AS strchiavetabella
  FROM an_lav_storia_inf_coll al
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_RPT_MOVIMENTO.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_RPT_MOVIMENTO
(CDNLAVORATORE, STRDESATTIVITA, DESATTIVITAMISS, STRRAGIONESOCIALE, PRGUTILIZ, 
 RAGSOCMISS, CODTIPOAZIENDA, FLGINTERASSPROPRIA, MANSIONE_DESC, DESCMANSMISS, 
 DATINIZIOMIS, DATFINEMIS, CONTRATTO_DESC, TIROCINIO_DESC, ATTIVITA_DESC, 
 ATTIVITA_MISS_DESC, DATINIZIOMOV, STRINDIRIZZO, STRLOCALITA, STRCAP, 
 STRDENOMINAZIONE, STRTARGA, STRINDIRIZZOMISS, STRLOCALITAMISS, STRCAPMISS, 
 STRDENOMINAZIONEMISS, STRTARGAMISS, AZ_STATO_DESC, DATFINEMOV, ISMISSIONE)
AS 
SELECT AM_MOVIMENTO.CDNLAVORATORE,
    AM_MOVIMENTO.STRDESATTIVITA,
	MISS.STRDESATTIVITA AS DESATTIVITAMISS,
    AN_AZIENDA.STRRAGIONESOCIALE,
	AZMISS.PRGAZIENDA AS PRGUTILIZ,
	AZMISS.STRRAGIONESOCIALE AS RAGSOCMISS,
	AN_AZIENDA.CODTIPOAZIENDA,
	NVL(AM_MOVIMENTO.FLGINTERASSPROPRIA,'N'),
    DE_MANSIONE.STRDESCRIZIONE,
	DEMANSMISS.STRDESCRIZIONE AS DESCMANSMISS,
	MISS.DATINIZIOMIS,
	MISS.DATFINEMIS,	
     decode(PG_ANAGRAFICA_PROFESSIONALE_RP.GETCODCONTRATTOFORRP( AM_MOVIMENTO.PRGMOVIMENTO ),
    'LT',decode (PG_ANAGRAFICA_PROFESSIONALE_RP.GETCODMONOTEMPOFORRP( AM_MOVIMENTO.PRGMOVIMENTO ),
                  'D', 'Lavoro dipendente TD',
         'I', 'Lavoro dipendente TI'),
    'LP',decode (PG_ANAGRAFICA_PROFESSIONALE_RP.GETCODMONOTEMPOFORRP(AM_MOVIMENTO.PRGMOVIMENTO ),
                  'D', 'Lavoro dipendente TD',
         'I', 'Lavoro dipendente TI'),
		
	'LTP',decode (PG_ANAGRAFICA_PROFESSIONALE_RP.GETCODMONOTEMPOFORRP(AM_MOVIMENTO.PRGMOVIMENTO ),
                  'D', 'Lavoro dipendente TD',
         'I', 'Lavoro dipendente TI'),
    DE_CONTRATTO.STRDESCRIZIONE)as strdescrizione,
    DE_CONTRATTO.CODCONTRATTO,
    DE_ATTIVITA.STRDESCRIZIONE,
	DE_ATT_MISS.STRDESCRIZIONE,
    AM_MOVIMENTO.DATINIZIOMOV,
    AN_UNITA_AZIENDA.STRINDIRIZZO,
    AN_UNITA_AZIENDA.STRLOCALITA,
    DE_COMUNE.STRCAP,
    DE_COMUNE.STRDENOMINAZIONE,
    DE_PROVINCIA.STRTARGA,
    UNMISS.STRINDIRIZZO,
    UNMISS.STRLOCALITA,
    COMUNEMISS.STRCAP ,
    COMUNEMISS.STRDENOMINAZIONE, 
    PROVINCIAMISS.STRTARGA,
	
    DE_AZ_STATO.STRDESCRIZIONE,
    PG_ANAGRAFICA_PROFESSIONALE_RP.GetDataFineMovEffForRp(AM_MOVIMENTO.PRGMOVIMENTO),
	DECODE(AN_AZIENDA.CODTIPOAZIENDA,'INT',
		DECODE(AM_MOVIMENTO.FLGINTERASSPROPRIA,'S','0',
			   DECODE(AM_MOVIMENTO.PRGAZIENDAUTILIZ,NULL,'0',
			   		  DECODE(AM_MOVIMENTO.DATINIZIORAPLAV,NULL,'0','1'))),'0')
  FROM AM_MOVIMENTO,
  	AM_MOVIMENTO_MISSIONE MISS,
    AN_UNITA_AZIENDA,
    AN_AZIENDA,
	AN_AZIENDA AZMISS,
	AN_UNITA_AZIENDA UNMISS,
    DE_MANSIONE,
	DE_MANSIONE DEMANSMISS,
    DE_CONTRATTO,
    DE_COMUNE,
	DE_COMUNE COMUNEMISS,
    DE_ATTIVITA,
	DE_ATTIVITA DE_ATT_MISS,
    DE_AZ_STATO,
    DE_PROVINCIA,
	DE_PROVINCIA PROVINCIAMISS,
    DE_TIPO_CONTRATTO
 WHERE AM_MOVIMENTO.CODTIPOMOV <> 'CES'
   AND AM_MOVIMENTO.CODMONOMOVDICH <> 'C'
   AND AM_MOVIMENTO.CODSTATOATTO = 'PR'
   AND (AM_MOVIMENTO.PRGMOVIMENTOPREC IS NULL OR AM_MOVIMENTO.CODTIPOMOV = 'PRO')
   AND AM_MOVIMENTO.PRGAZIENDA=AN_UNITA_AZIENDA.PRGAZIENDA
   AND AM_MOVIMENTO.PRGUNITA=AN_UNITA_AZIENDA.PRGUNITA
   AND AM_MOVIMENTO.CODMANSIONE=DE_MANSIONE.CODMANSIONE(+)
   AND AM_MOVIMENTO.PRGMOVIMENTO = MISS.PRGMOVIMENTO (+)
   AND MISS.CODMANSIONE = DEMANSMISS.CODMANSIONE (+) 
   AND AM_MOVIMENTO.CODTIPOCONTRATTO=DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+)
   AND DE_TIPO_CONTRATTO.CODCONTRATTO=DE_CONTRATTO.CODCONTRATTO(+)
   AND AN_UNITA_AZIENDA.PRGAZIENDA=AN_AZIENDA.PRGAZIENDA
   AND AM_MOVIMENTO.PRGAZIENDAUTILIZ = AZMISS.PRGAZIENDA (+)
   AND AM_MOVIMENTO.PRGAZIENDAUTILIZ = UNMISS.PRGAZIENDA (+)
   AND AM_MOVIMENTO.PRGUNITAUTILIZ = UNMISS.PRGUNITA (+)
   AND AN_UNITA_AZIENDA.CODCOM=DE_COMUNE.CODCOM
   AND UNMISS.CODCOM = COMUNEMISS.CODCOM (+)
   AND AN_UNITA_AZIENDA.CODAZSTATO=DE_AZ_STATO.CODAZSTATO(+)
   AND AN_UNITA_AZIENDA.CODATECO=DE_ATTIVITA.CODATECO(+)
   AND UNMISS.CODATECO=DE_ATT_MISS.CODATECO(+)
   AND DE_COMUNE.CODPROVINCIA=DE_PROVINCIA.CODPROVINCIA(+)
   AND COMUNEMISS.CODPROVINCIA = PROVINCIAMISS.CODPROVINCIA(+) 
  ORDER BY DATINIZIOMOV
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_rpt_obbligo_formativo.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_RPT_OBBLIGO_FORMATIVO AS
SELECT cdnlavoratore,
                      flgobbligoscolastico,
                      flgobbligoformativo,
                      AM_OBBLIGO_FORMATIVO.codmodalitaassolv,
                      DE_FO_MODALITA_ASSOLV.STRDESCRIZIONE as DESCRIZIONE,
                      strnote,
                      cdnutins,
                      to_char(dtmins,'dd/mm/yyyy') dtmins,
                      cdnutmod,
                      to_char(dtmmod,'dd/mm/yyyy') dtmmod,
                      numkloobbligoform ,
					  aps.prgpattolavoratore
                FROM  AM_OBBLIGO_FORMATIVO, DE_FO_MODALITA_ASSOLV, am_lav_patto_scelta aps
                WHERE 				(aps.CODLSTTAB='AM_OBBFO')
				and (aps.STRCHIAVETABELLA=am_obbligo_formativo.CDNLAVORATORE)
				and
                (AM_OBBLIGO_FORMATIVO.CODMODALITAASSOLV = DE_FO_MODALITA_ASSOLV.CODMODALITAASSOLV)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_rpt_rosadef.sql
************************************************************************************** */


create or replace view VW_RPT_ROSADEF as
(
	select NOM.PRGNOMINATIVO,NOM.CDNLAVORATORE,RIC.PRGRICHIESTAAZ,(	select DIS.CODDISPONIBILITAROSA 
																	from do_disponibilita dis 
																	where DIS.CDNLAVORATORE = NOM.CDNLAVORATORE
																		and DIS.PRGRICHIESTAAZ = RIC2.PRGRICHIESTAAZ
																  ) flgdisp
	from do_nominativo nom
	inner join do_rosa ro on RO.PRGROSA = NOM.PRGROSA
	inner join do_incrocio inc on INC.PRGINCROCIO = RO.PRGINCROCIO
	inner join DO_RICHIESTA_AZ ric on RIC.PRGRICHIESTAAZ = INC.PRGRICHIESTAAZ
	inner join DO_RICHIESTA_AZ ric2 on RIC2.NUMANNO = RIC.numanno and
										RIC2.NUMRICHIESTA = RIC.numrichiesta and
										RIC2.NUMSTORICO = 0
	where nom.cdnutcanc IS NULL
)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_rpt_schedaanagmin_titstudio.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_RPT_SCHEDAANAGMIN_TITSTUDIO
(MAXTIT, CDNLAVORATORE, CODTIT1, DESC1, PD1, 
 LIV1, LEVSCOL, LEVSCOLDESC, CODTITOLO, STRSPECIFICA, CODCOM, 
 STRDENOMINAZIONE, CODMONOSTATO, NUMANNO, STRVOTO, STRESIMI, 
 FLGLODE, NUMANNIFREQ, NUMANNIPREV, CODMONOSTATOTIT)
AS 
SELECT PG_SIL_MONIT.pdMaxTitoloStudio(ST.CDNLAVORATORE) AS MAXTIT,
    ST.CDNLAVORATORE,
    DET.CODTITOLO AS CODTIT1,
    DET.STRDESCRIZIONE AS DESC1,
    DET.CODPADRE AS PD1,
    DET.CDNLIVELLO AS LIV1,
    DECODE(SUBSTR(ST.CODTITOLO,0,2), '81','81','83', '83', SUBSTR(ST.CODTITOLO,0,1)||'0'  ) AS LEVSCOL,
    MNL1.DESCRIZIONE AS LEVSCOLDESC,
    ST.CODTITOLO,
    ST.STRSPECIFICA,
    ST.CODCOM,
    DECOM.STRDENOMINAZIONE,
    ST.CODMONOSTATO,
    ST.NUMANNO,
    ST.STRVOTO,
    ST.STRESIMI,
    ST.FLGLODE,
	ST.NUMANNIFREQ,
	ST.NUMANNIPREV, ST.CODMONOSTATOTIT
  FROM PR_STUDIO ST
LEFT JOIN DE_TITOLO DET ON ST.CODTITOLO = DET.CODTITOLO
LEFT JOIN MN_TITOLO_L1 mnl1 ON MNL1.CODICE =  DECODE(SUBSTR(ST.CODTITOLO,0,2), '81','81','83', '83', SUBSTR(ST.CODTITOLO,0,1)||'0' )
LEFT JOIN DE_COMUNE DECOM ON ST.CODCOM = DECOM.CODCOM
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_rpt_schedaapmin_movimenti_batch.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_RPT_SCHEDAAPMIN_MOV_BATCH
(CDNLAVORATORE, ISMISSIONE, AZUTICF, AZUTIDENOM, AZUTIIND, AZUTICOM, 
 CODAZUTIATECO, AZUTIATECO, AZUTICFMISS, AZUTIDENOMMISS, AZUTIINDMISS,
 AZUTICOMMISS, CODAZUTIATECOMISS, AZUTIATECOMISS,
 STRDESATTIVITA, DESATTIVITAMISS, STRRAGIONESOCIALE, 
 STRCODICEFISCALE, MANSIONE_DESC, CODMANSIONE_MIN, CONTRATTO_DESC, CODCONTRATTO, CODAZATECO, 
 AZATECO, DATINIZIOMOV, STRINDIRIZZO, STRLOCALITA, AZCOM, 
 STRCAP, STRDENOMINAZIONE, STRTARGA, AZ_STATO_DESC, DATFINEMOV, 
 CODORARIO, NUMORESETT, CODTIPOMOV, FLGINTERASSPROPRIA, CODTIPOAZIENDA, CODTIPOTRASF, 
 INDDIST, COMDIST, DATINIZIOMIS, DATFINEMIS, MANS_DESC_MISS, DATFINEPF,
 FLGLEGGE68, FLGLAVOROINMOBILITA, FLGLAVOROSTAGIONALE, FLGLAVOROAGR, DATCONVENZIONE, NUMCONVENZIONE, CODTIPOASS, NUMGGPREVISTIAGR, CODCOMUNICAZIONE, PRGMOVIMENTO, PRGMOVIMENTORETT,
 CODSOGGPROMOTOREMIN, datcomunicaz, CODQUALIFICASRQ)
AS 
SELECT AM_MOVIMENTO.CDNLAVORATORE,
 DECODE(AZ.CODTIPOAZIENDA,'INT',
		DECODE(AM_MOVIMENTO.FLGINTERASSPROPRIA,'S','0',
			   DECODE(AM_MOVIMENTO.PRGAZIENDAUTILIZ,NULL,'0',
			   		  DECODE(AM_MOVIMENTO.DATINIZIORAPLAV,NULL,'0','1'))),'0') as ISMISSIONE,
 (CASE 
	WHEN AZUT.STRCODICEFISCALE IS NOT NULL THEN
		AZUT.STRCODICEFISCALE
	ELSE APPRENDIST.STRCODFISCPROMOTORETIR
 END) AS AZUTICF,
 (CASE 
	WHEN AZUT.STRRAGIONESOCIALE IS NOT NULL THEN
		AZUT.STRRAGIONESOCIALE
	ELSE APPRENDIST.STRDENOMINAZIONETIR
 END) AS AZUTIDENOM,
 (CASE 
	WHEN UAZUT.STRINDIRIZZO IS NOT NULL THEN
		UAZUT.STRINDIRIZZO || ' (' || DECOMUT.STRDENOMINAZIONE || ', ' || DECOMUT.STRCAP || ')' 
	ELSE UAZUT.STRINDIRIZZO
 END) AS AZUTIIND,
 UAZUT.CODCOM as AZUTICOM,
 DEATTUT.CODATECODOT as CODAZUTIATECO,
 DEATTUT.STRDESCRIZIONE as AZUTIATECO,
 AZUTMISS.STRCODICEFISCALE as AZUTICFMISS,
 AZUTMISS.STRRAGIONESOCIALE as AZUTIDENOMMISS,
 (CASE 
	WHEN UAZUTMISS.STRINDIRIZZO IS NOT NULL THEN
		UAZUTMISS.STRINDIRIZZO || ' (' || DECOMUTMISS.STRDENOMINAZIONE || ', ' || DECOMUTMISS.STRCAP || ')' 
	ELSE UAZUTMISS.STRINDIRIZZO
 END) AS AZUTIINDMISS,
 UAZUTMISS.CODCOM as AZUTICOMMISS,
 DEATTUTMISS.CODATECODOT as CODAZUTIATECOMISS,
 DEATTUTMISS.STRDESCRIZIONE as AZUTIATECOMISS,
 AM_MOVIMENTO.STRDESATTIVITA,
 MISS.STRDESATTIVITA as DESATTIVITAMISS,
 AZ.STRRAGIONESOCIALE,
 AZ.STRCODICEFISCALE,
 DE_MANSIONE.STRDESCRIZIONE as MANSIONE_DESC,
 DE_MANSIONE.CODMANSIONEDOT as CODMANSIONE_MIN,
 DE_TIPO_CONTRATTO.STRDESCRIZIONE as CONTRATTO_DESC,
 PG_MOVIMENTI.GetCodContrattoForRp(AM_MOVIMENTO.PRGMOVIMENTO) AS CODCONTRATTO,
 DEATT.CODATECODOT as CODAZATECO,
 DEATT.STRDESCRIZIONE as AZATECO,
 AM_MOVIMENTO.DATINIZIOMOV,
 (CASE 
	WHEN UAZ.STRINDIRIZZO IS NOT NULL THEN
		UAZ.STRINDIRIZZO || ' (' || DECOM.STRDENOMINAZIONE || ', ' || DECOM.STRCAP || ')' 
	ELSE UAZ.STRINDIRIZZO
 END) AS STRINDIRIZZO,
 UAZ.STRLOCALITA,
 UAZ.CODCOM as AZCOM,
 DECOM.STRCAP,
 DECOM.STRDENOMINAZIONE,
 DEPROV.STRTARGA,
 DEAZST.STRDESCRIZIONE as AZ_STATO_DESC,
 PG_ANAGRAFICA_PROFESSIONALE_RP.GetDataFineMovEffForRp(AM_MOVIMENTO.PRGMOVIMENTO) as DATFINEMOV,
 DE_ORARIO.CODMONOORARIO as CODORARIO,
 AM_MOVIMENTO.NUMORESETT,
 AM_MOVIMENTO.CODTIPOMOV,
 AM_MOVIMENTO.FLGINTERASSPROPRIA,
 AZ.CODTIPOAZIENDA,
 AM_MOVIMENTO.CODTIPOTRASF,
 (CASE 
	WHEN UAZDIST.STRINDIRIZZO IS NOT NULL THEN
		UAZDIST.STRINDIRIZZO || ' (' || DECOMDIST.STRDENOMINAZIONE || ', ' || DECOMDIST.STRCAP || ')' 
	ELSE UAZDIST.STRINDIRIZZO
 END) AS INDDIST,
 UAZDIST.CODCOM AS COMDIST,
 MISS.DATINIZIOMIS,
 MISS.DATFINEMIS,
 MANSMISS.STRDESCRIZIONE AS MANS_DESC_MISS,
 AM_MOVIMENTO.DATFINEPF,
 AM_MOVIMENTO.FLGLEGGE68,
 AM_MOVIMENTO.FLGLAVOROINMOBILITA,
 AM_MOVIMENTO.FLGLAVOROSTAGIONALE,
 AM_MOVIMENTO.FLGLAVOROAGR,
 AM_MOVIMENTO.DATCONVENZIONE, AM_MOVIMENTO.NUMCONVENZIONE, AM_MOVIMENTO.CODTIPOASS, AM_MOVIMENTO.NUMGGPREVISTIAGR, AM_MOVIMENTO.CODCOMUNICAZIONE, AM_MOVIMENTO.PRGMOVIMENTO,
 movInizialeRettificato(AM_MOVIMENTO.PRGMOVIMENTO) AS PRGMOVIMENTORETT, AM_MOVIMENTO.CODSOGGPROMOTOREMIN, AM_MOVIMENTO.datcomunicaz, APPRENDISTSRQ.CODQUALIFICASRQ
FROM AM_MOVIMENTO,
 AM_MOVIMENTO_MISSIONE MISS,
 AM_MOVIMENTO_APPRENDIST APPRENDIST,
 AM_MOVIMENTO_APPRENDIST APPRENDISTSRQ,
 AN_UNITA_AZIENDA UAZ,
 AN_AZIENDA AZ,
 AN_UNITA_AZIENDA UAZUT,
 AN_AZIENDA AZUT,
 AN_UNITA_AZIENDA UAZUTMISS,
 AN_AZIENDA AZUTMISS,
 AN_UNITA_AZIENDA UAZDIST,
 AN_AZIENDA AZDIST,
 DE_MANSIONE,
 DE_MANSIONE MANSMISS,
 DE_COMUNE DECOM,
 DE_ATTIVITA DEATT,
 DE_AZ_STATO DEAZST,
 DE_PROVINCIA DEPROV,
 DE_COMUNE DECOMUT,
 DE_ATTIVITA DEATTUT,
 DE_AZ_STATO DEAZSTUT,
 DE_PROVINCIA DEPROVUT,
 DE_COMUNE DECOMUTMISS,
 DE_ATTIVITA DEATTUTMISS,
 DE_AZ_STATO DEAZSTUTMISS,
 DE_PROVINCIA DEPROVUTMISS,
 DE_COMUNE DECOMDIST,
 DE_ATTIVITA DEATTDIST,
 DE_AZ_STATO DEAZSTDIST,
 DE_PROVINCIA DEPROVDIST,
 DE_TIPO_CONTRATTO,
 DE_ORARIO
WHERE AM_MOVIMENTO.CODTIPOMOV <> 'CES'
  AND AM_MOVIMENTO.CODMONOMOVDICH <> 'C'
  AND AM_MOVIMENTO.CODSTATOATTO = 'PR'
  AND AM_MOVIMENTO.PRGMOVIMENTOPREC IS NULL
  AND AM_MOVIMENTO.PRGAZIENDA=UAZ.PRGAZIENDA
  AND AM_MOVIMENTO.PRGUNITA=UAZ.PRGUNITA
  AND UAZUT.PRGAZIENDA (+) = AM_MOVIMENTO.PRGAZIENDAUTILIZ 
  AND UAZUT.PRGUNITA (+) = AM_MOVIMENTO.PRGUNITAUTILIZ 
  AND MISS.PRGMOVIMENTO (+) = AM_MOVIMENTO.PRGMOVIMENTO 
  AND APPRENDIST.PRGMOVIMENTO (+) = AM_MOVIMENTO.PRGMOVIMENTO
  AND APPRENDISTSRQ.PRGMOVIMENTO (+) = movInizialeRettificato(AM_MOVIMENTO.PRGMOVIMENTO)
  AND UAZUTMISS.PRGAZIENDA (+) = MISS.PRGAZIENDAUTILIZ 
  AND UAZUTMISS.PRGUNITA (+) = MISS.PRGUNITAUTILIZ 
  AND UAZDIST.PRGAZIENDA (+) = AM_MOVIMENTO.PRGAZIENDADIST
  AND UAZDIST.PRGUNITA (+) = AM_MOVIMENTO.PRGUNITADIST
  AND MISS.CODMANSIONE=MANSMISS.CODMANSIONE (+)
  AND AM_MOVIMENTO.CODMANSIONE=DE_MANSIONE.CODMANSIONE (+)
  AND PG_MOVIMENTI.GetCodContrattoForRp(AM_MOVIMENTO.PRGMOVIMENTO)=DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+)
  AND DE_TIPO_CONTRATTO.FLGMIN = 'S'
  AND NVL(DE_TIPO_CONTRATTO.DATFINEVAL,sysdate + 1) > sysdate
  AND UAZ.PRGAZIENDA=AZ.PRGAZIENDA
  AND UAZ.CODCOM=DECOM.CODCOM
  AND UAZ.CODAZSTATO=DEAZST.CODAZSTATO
  AND UAZ.CODATECO=DEATT.CODATECO(+)
  AND DECOM.CODPROVINCIA=DEPROV.CODPROVINCIA
  AND UAZUT.PRGAZIENDA=AZUT.PRGAZIENDA(+)
  AND UAZUT.CODCOM=DECOMUT.CODCOM(+)
  AND UAZUT.CODAZSTATO=DEAZSTUT.CODAZSTATO(+)
  AND UAZUT.CODATECO=DEATTUT.CODATECO(+)
  AND DECOMUT.CODPROVINCIA=DEPROVUT.CODPROVINCIA(+)
  AND UAZUTMISS.PRGAZIENDA=AZUTMISS.PRGAZIENDA(+)
  AND UAZUTMISS.CODCOM=DECOMUTMISS.CODCOM(+)
  AND UAZUTMISS.CODAZSTATO=DEAZSTUTMISS.CODAZSTATO(+)
  AND UAZUTMISS.CODATECO=DEATTUTMISS.CODATECO(+)
  AND DECOMUTMISS.CODPROVINCIA=DEPROVUTMISS.CODPROVINCIA(+)
  AND UAZDIST.PRGAZIENDA=AZDIST.PRGAZIENDA(+)
  AND UAZDIST.CODCOM=DECOMDIST.CODCOM(+)
  AND UAZDIST.CODAZSTATO=DEAZSTDIST.CODAZSTATO(+)
  AND UAZDIST.CODATECO=DEATTDIST.CODATECO(+)
  AND DECOMDIST.CODPROVINCIA=DEPROVDIST.CODPROVINCIA(+)
  AND AM_MOVIMENTO.CODORARIO = DE_ORARIO.CODORARIO (+)
ORDER BY DATINIZIOMOV
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_rpt_schedaapmin_movimenti.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_RPT_SCHEDAAPMIN_MOVIMENTI
(CDNLAVORATORE, ISMISSIONE, AZUTICF, AZUTIDENOM, AZUTIIND, AZUTICOM, 
 CODAZUTIATECO, AZUTIATECO, AZUTICFMISS, AZUTIDENOMMISS, AZUTIINDMISS,
 AZUTICOMMISS, CODAZUTIATECOMISS, AZUTIATECOMISS,
 STRDESATTIVITA, DESATTIVITAMISS, STRRAGIONESOCIALE, 
 STRCODICEFISCALE, MANSIONE_DESC, CODMANSIONE_MIN, CONTRATTO_DESC, CODCONTRATTO, CODAZATECO, 
 AZATECO, DATINIZIOMOV, STRINDIRIZZO, STRLOCALITA, AZCOM, 
 STRCAP, STRDENOMINAZIONE, STRTARGA, AZ_STATO_DESC, DATFINEMOV, 
 CODORARIO, NUMORESETT, CODTIPOMOV, FLGINTERASSPROPRIA, CODTIPOAZIENDA, CODTIPOTRASF, 
 INDDIST, COMDIST, DATINIZIOMIS, DATFINEMIS, MANS_DESC_MISS, DATFINEPF,
 FLGLEGGE68, FLGLAVOROINMOBILITA, FLGLAVOROSTAGIONALE, FLGLAVOROAGR, DATCONVENZIONE, NUMCONVENZIONE, CODTIPOASS, NUMGGPREVISTIAGR, CODCOMUNICAZIONE, PRGMOVIMENTO, PRGMOVIMENTORETT,
 CODSOGGPROMOTOREMIN, datcomunicaz, CODQUALIFICASRQ)
AS 
SELECT AM_MOVIMENTO.CDNLAVORATORE,
 DECODE(AZ.CODTIPOAZIENDA,'INT',
		DECODE(AM_MOVIMENTO.FLGINTERASSPROPRIA,'S','0',
			   DECODE(AM_MOVIMENTO.PRGAZIENDAUTILIZ,NULL,'0',
			   		  DECODE(AM_MOVIMENTO.DATINIZIORAPLAV,NULL,'0','1'))),'0') as ISMISSIONE,
 (CASE 
	WHEN AZUT.STRCODICEFISCALE IS NOT NULL THEN
		AZUT.STRCODICEFISCALE
	ELSE APPRENDIST.STRCODFISCPROMOTORETIR
 END) AS AZUTICF,
 (CASE 
	WHEN AZUT.STRRAGIONESOCIALE IS NOT NULL THEN
		AZUT.STRRAGIONESOCIALE
	ELSE APPRENDIST.STRDENOMINAZIONETIR
 END) AS AZUTIDENOM,
 (CASE 
	WHEN UAZUT.STRINDIRIZZO IS NOT NULL THEN
		UAZUT.STRINDIRIZZO || ' (' || DECOMUT.STRDENOMINAZIONE || ', ' || DECOMUT.STRCAP || ')' 
	ELSE UAZUT.STRINDIRIZZO
 END) AS AZUTIIND,
 UAZUT.CODCOM as AZUTICOM,
 DEATTUT.CODATECODOT as CODAZUTIATECO,
 DEATTUT.STRDESCRIZIONE as AZUTIATECO,
 AZUTMISS.STRCODICEFISCALE as AZUTICFMISS,
 AZUTMISS.STRRAGIONESOCIALE as AZUTIDENOMMISS,
 (CASE 
	WHEN UAZUTMISS.STRINDIRIZZO IS NOT NULL THEN
		UAZUTMISS.STRINDIRIZZO || ' (' || DECOMUTMISS.STRDENOMINAZIONE || ', ' || DECOMUTMISS.STRCAP || ')' 
	ELSE UAZUTMISS.STRINDIRIZZO
 END) AS AZUTIINDMISS,
 UAZUTMISS.CODCOM as AZUTICOMMISS,
 DEATTUTMISS.CODATECODOT as CODAZUTIATECOMISS,
 DEATTUTMISS.STRDESCRIZIONE as AZUTIATECOMISS,
 AM_MOVIMENTO.STRDESATTIVITA,
 MISS.STRDESATTIVITA as DESATTIVITAMISS,
 AZ.STRRAGIONESOCIALE,
 AZ.STRCODICEFISCALE,
 DE_MANSIONE.STRDESCRIZIONE as MANSIONE_DESC,
 DE_MANSIONE.CODMANSIONEDOT as CODMANSIONE_MIN,
 DE_TIPO_CONTRATTO.STRDESCRIZIONE as CONTRATTO_DESC,
 PG_MOVIMENTI.GetCodContrattoForRp(AM_MOVIMENTO.PRGMOVIMENTO) AS CODCONTRATTO,
 DEATT.CODATECODOT as CODAZATECO,
 DEATT.STRDESCRIZIONE as AZATECO,
 AM_MOVIMENTO.DATINIZIOMOV,
 (CASE 
	WHEN UAZ.STRINDIRIZZO IS NOT NULL THEN
		UAZ.STRINDIRIZZO || ' (' || DECOM.STRDENOMINAZIONE || ', ' || DECOM.STRCAP || ')' 
	ELSE UAZ.STRINDIRIZZO
 END) AS STRINDIRIZZO,
 UAZ.STRLOCALITA,
 UAZ.CODCOM as AZCOM,
 DECOM.STRCAP,
 DECOM.STRDENOMINAZIONE,
 DEPROV.STRTARGA,
 DEAZST.STRDESCRIZIONE as AZ_STATO_DESC,
 PG_ANAGRAFICA_PROFESSIONALE_RP.GetDataFineMovEffForRp(AM_MOVIMENTO.PRGMOVIMENTO) as DATFINEMOV,
 DE_ORARIO.CODMONOORARIO as CODORARIO,
 AM_MOVIMENTO.NUMORESETT,
 AM_MOVIMENTO.CODTIPOMOV,
 AM_MOVIMENTO.FLGINTERASSPROPRIA,
 AZ.CODTIPOAZIENDA,
 AM_MOVIMENTO.CODTIPOTRASF,
 (CASE 
	WHEN UAZDIST.STRINDIRIZZO IS NOT NULL THEN
		UAZDIST.STRINDIRIZZO || ' (' || DECOMDIST.STRDENOMINAZIONE || ', ' || DECOMDIST.STRCAP || ')' 
	ELSE UAZDIST.STRINDIRIZZO
 END) AS INDDIST,
 UAZDIST.CODCOM AS COMDIST,
 MISS.DATINIZIOMIS,
 MISS.DATFINEMIS,
 MANSMISS.STRDESCRIZIONE AS MANS_DESC_MISS,
 AM_MOVIMENTO.DATFINEPF,
 AM_MOVIMENTO.FLGLEGGE68,
 AM_MOVIMENTO.FLGLAVOROINMOBILITA,
 AM_MOVIMENTO.FLGLAVOROSTAGIONALE,
 AM_MOVIMENTO.FLGLAVOROAGR,
 AM_MOVIMENTO.DATCONVENZIONE, AM_MOVIMENTO.NUMCONVENZIONE, AM_MOVIMENTO.CODTIPOASS, AM_MOVIMENTO.NUMGGPREVISTIAGR, AM_MOVIMENTO.CODCOMUNICAZIONE, AM_MOVIMENTO.PRGMOVIMENTO,
 movInizialeRettificato(AM_MOVIMENTO.PRGMOVIMENTO) AS PRGMOVIMENTORETT, AM_MOVIMENTO.CODSOGGPROMOTOREMIN, AM_MOVIMENTO.datcomunicaz, APPRENDISTSRQ.CODQUALIFICASRQ
FROM AM_MOVIMENTO,
 AM_MOVIMENTO_MISSIONE MISS,
 AM_MOVIMENTO_APPRENDIST APPRENDIST,
 AM_MOVIMENTO_APPRENDIST APPRENDISTSRQ,
 AN_UNITA_AZIENDA UAZ,
 AN_AZIENDA AZ,
 AN_UNITA_AZIENDA UAZUT,
 AN_AZIENDA AZUT,
 AN_UNITA_AZIENDA UAZUTMISS,
 AN_AZIENDA AZUTMISS,
 AN_UNITA_AZIENDA UAZDIST,
 AN_AZIENDA AZDIST,
 DE_MANSIONE,
 DE_MANSIONE MANSMISS,
 DE_COMUNE DECOM,
 DE_ATTIVITA DEATT,
 DE_AZ_STATO DEAZST,
 DE_PROVINCIA DEPROV,
 DE_COMUNE DECOMUT,
 DE_ATTIVITA DEATTUT,
 DE_AZ_STATO DEAZSTUT,
 DE_PROVINCIA DEPROVUT,
 DE_COMUNE DECOMUTMISS,
 DE_ATTIVITA DEATTUTMISS,
 DE_AZ_STATO DEAZSTUTMISS,
 DE_PROVINCIA DEPROVUTMISS,
 DE_COMUNE DECOMDIST,
 DE_ATTIVITA DEATTDIST,
 DE_AZ_STATO DEAZSTDIST,
 DE_PROVINCIA DEPROVDIST,
 DE_TIPO_CONTRATTO,
 DE_ORARIO
WHERE AM_MOVIMENTO.CODTIPOMOV <> 'CES'
  AND AM_MOVIMENTO.CODMONOMOVDICH <> 'C'
  AND AM_MOVIMENTO.CODSTATOATTO = 'PR'
  AND AM_MOVIMENTO.PRGMOVIMENTOPREC IS NULL
  AND AM_MOVIMENTO.PRGAZIENDA=UAZ.PRGAZIENDA
  AND AM_MOVIMENTO.PRGUNITA=UAZ.PRGUNITA
  AND UAZUT.PRGAZIENDA (+) = AM_MOVIMENTO.PRGAZIENDAUTILIZ 
  AND UAZUT.PRGUNITA (+) = AM_MOVIMENTO.PRGUNITAUTILIZ 
  AND MISS.PRGMOVIMENTO (+) = AM_MOVIMENTO.PRGMOVIMENTO 
  AND APPRENDIST.PRGMOVIMENTO (+) = AM_MOVIMENTO.PRGMOVIMENTO
  AND APPRENDISTSRQ.PRGMOVIMENTO (+) = movInizialeRettificato(AM_MOVIMENTO.PRGMOVIMENTO)
  AND UAZUTMISS.PRGAZIENDA (+) = MISS.PRGAZIENDAUTILIZ 
  AND UAZUTMISS.PRGUNITA (+) = MISS.PRGUNITAUTILIZ 
  AND UAZDIST.PRGAZIENDA (+) = AM_MOVIMENTO.PRGAZIENDADIST
  AND UAZDIST.PRGUNITA (+) = AM_MOVIMENTO.PRGUNITADIST
  AND MISS.CODMANSIONE=MANSMISS.CODMANSIONE (+)
  AND AM_MOVIMENTO.CODMANSIONE=DE_MANSIONE.CODMANSIONE (+)
  AND PG_MOVIMENTI.GetCodContrattoForRp(AM_MOVIMENTO.PRGMOVIMENTO)=DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+)
  AND DE_TIPO_CONTRATTO.FLGMIN = 'S'
  AND UAZ.PRGAZIENDA=AZ.PRGAZIENDA
  AND UAZ.CODCOM=DECOM.CODCOM
  AND UAZ.CODAZSTATO=DEAZST.CODAZSTATO
  AND UAZ.CODATECO=DEATT.CODATECO(+)
  AND DEATT.CODATECODOT IS NOT NULL
  AND DECOM.CODPROVINCIA=DEPROV.CODPROVINCIA
  AND UAZUT.PRGAZIENDA=AZUT.PRGAZIENDA(+)
  AND UAZUT.CODCOM=DECOMUT.CODCOM(+)
  AND UAZUT.CODAZSTATO=DEAZSTUT.CODAZSTATO(+)
  AND UAZUT.CODATECO=DEATTUT.CODATECO(+)
  AND (UAZUT.PRGAZIENDA IS NULL OR DEATTUT.CODATECODOT IS NOT NULL)
  AND DECOMUT.CODPROVINCIA=DEPROVUT.CODPROVINCIA(+)
  AND UAZUTMISS.PRGAZIENDA=AZUTMISS.PRGAZIENDA(+)
  AND UAZUTMISS.CODCOM=DECOMUTMISS.CODCOM(+)
  AND UAZUTMISS.CODAZSTATO=DEAZSTUTMISS.CODAZSTATO(+)
  AND UAZUTMISS.CODATECO=DEATTUTMISS.CODATECO(+)
  AND DECOMUTMISS.CODPROVINCIA=DEPROVUTMISS.CODPROVINCIA(+)
  AND UAZDIST.PRGAZIENDA=AZDIST.PRGAZIENDA(+)
  AND UAZDIST.CODCOM=DECOMDIST.CODCOM(+)
  AND UAZDIST.CODAZSTATO=DEAZSTDIST.CODAZSTATO(+)
  AND UAZDIST.CODATECO=DEATTDIST.CODATECO(+)
  AND (UAZDIST.PRGAZIENDA IS NULL OR DEATTDIST.CODATECODOT IS NOT NULL)
  AND DECOMDIST.CODPROVINCIA=DEPROVDIST.CODPROVINCIA(+)
  AND AM_MOVIMENTO.CODORARIO = DE_ORARIO.CODORARIO (+)
ORDER BY DATINIZIOMOV
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_slot_errd.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_SLOT_ERRD (CODCPI, PRGSLOT)
AS 
select ag1.CODCPI as CODCPI, ag1.PRGslot as PRGSLOT
from ag_slot ag1, ag_spi_slot la1, de_stato_slot sa1
where (ag1.CODSTATOSLOT=sa1.CODSTATOSLOT and sa1.FLGATTIVO='S')
	   and ag1.codCpi = la1.codcpi
 	   and ag1.PRGSLOT = la1.PRGSLOT
	   and exists (
		    select 1
		    from ag_slot ag2 , AG_SPI_SLOT LA2, de_stato_slot sa2
			where (ag2.CODSTATOSLOT=sa2.CODSTATOSLOT and sa2.FLGATTIVO='S')
		       and ag2.codCpi = la2.codcpi
			   and ag2.PRGSLOT = la2.PRGSLOT
			   and trunc(AG1.DTMDATAORA) = trunc(AG2.DTMDATAORA)
		       and aG1.codCpi = AG2.codCpi
		  	   and la1.prgspi = la2.prgspi
		  	   and AG1.prgslot <> AG2.prgslot
			   and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
		   )
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_slot_err.sql
************************************************************************************** */


CREATE OR REPLACE FORCE VIEW VW_SLOT_ERR (CODCPI, PRGSLOT, CDNTIPOERR, STRTIPOERR)
AS 
select ag1.CODCPI as CODCPI, ag1.PRGslot as PRGSLOT, 2 as CdnTipoErr, 'Stesso operatore' as strTipoErr
from ag_slot ag1, ag_spi_slot la1, de_stato_slot sa1
where (ag1.CODSTATOSLOT=sa1.CODSTATOSLOT and sa1.FLGATTIVO='S') 
	   and ag1.codCpi = la1.codcpi 
	   and ag1.PRGSLOT = la1.PRGSLOT
       and exists (
	   	   select 1 
		   from ag_slot ag2 , AG_SPI_SLOT LA2, de_stato_slot sa2
		   where   (ag2.CODSTATOSLOT=sa2.CODSTATOSLOT and sa2.FLGATTIVO='S')
  		   	  and ag2.codCpi = la2.codcpi
  			  and ag2.PRGSLOT = la2.PRGSLOT
			  and trunc(AG1.DTMDATAORA) = trunc(AG2.DTMDATAORA)
    		  and aG1.codCpi = AG2.codCpi
  			  and la1.prgspi = la2.prgspi
			  and AG1.prgslot <> AG2.prgslot
			  and ( 
			 	     (to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-to_char(ag1.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 (to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-to_char(ag2.dtmdataora, 'SSSSS')>0 
					 and 
					 (to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))<0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))-(to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))>=0 
					 and 
					 to_char(ag1.dtmdataora, 'SSSSS')-to_char(ag2.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
				or ( 
				   	 ((to_char(ag2.dtmdataora, 'SSSSS')+(ag2.numminuti*60))-(to_char(ag1.dtmdataora, 'SSSSS')+(ag1.numminuti*60))>=0 
					 and 
					 to_char(ag2.dtmdataora, 'SSSSS')-to_char(ag1.dtmdataora, 'SSSSS')<=0 
					 ) 
				) 
			 ) 
  		)
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_test_patto.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_TEST_PATTO AS
SELECT aps.prglavpattoscelta, aps.prgpattolavoratore,
       CAST (aps.strchiavetabella2 AS NUMBER (38)) AS strchiavetabella2,
       CAST (aps.strchiavetabella AS NUMBER (38)) AS strchiavetabella
  FROM am_lav_patto_scelta aps
 WHERE aps.codlsttab = 'PR_MAN'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ts_menu_funz_abil.sql
************************************************************************************** */


create or replace view vw_ts_menu_funz_abil as
select
       F.cdnfunzione cdnfunzione,
       F.strdescrizione strdescrizionefunzione,
       F.STRTIPOFUNZIONE strtipofunzione,
       COMP.cdncomponente cdncomponente,
       COMP.strdenominazione strcompdenominazione,
       COMP.strpage strpage,
       F_C.STRCODPRIMOCOMP STRCODPRIMOCOMP,
       ap.cdnprofilo
  from ts_funzione F,
       ts_funz_comp F_C,
       ts_componente COMP,
       ts_abi_profilo AP
 where
        F.cdnfunzione = F_C.cdnfunzione
        and F_C.cdncomponente = COMP.cdncomponente
        and COMP.CDNCOMPONENTE = AP.CDNCOMPONENTE
        --and comp.flgvisibile<>'N'
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ts_menu.sql
************************************************************************************** */


create or replace view vw_ts_menu as
select
    V.PRGPROFILOUTENTE,
    V.CDNMENU,
    V.STRMENUDESCRIZIONE,
    V.CDNVOCEMENU,
    V.PRGVOCEMENU,
    V.STRVOCEMENU,
    V.CDNVOCEMENUPADRE,
    C.CDNFUNZIONE,
    C.STRDESCRIZIONEFUNZIONE,
    C.STRTIPOFUNZIONE,
    C.CDNCOMPONENTE,
    C.STRCOMPDENOMINAZIONE,
    C.STRPAGE,
    C.STRCODPRIMOCOMP
  from
       vw_ts_menu_funz_abil c,
       vw_ts_menu_voci v
 where
       c.cdnfunzione (+)= v.cdnfunzione
       AND C.CDNPROFILO (+)= V.CDNPROFILO
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/vw_ts_menu_voci.sql
************************************************************************************** */


create or replace view vw_ts_menu_voci as
select
       PU.prgprofiloutente prgprofiloutente,
       pu.cdnprofilo cdnprofilo,
       M.cdnmenu cdnmenu,
       MH.strdescrizione strmenudescrizione,
       M.cdnvocemenu cdnvocemenu,
       M.prgvocemenu prgvocemenu,
       VM.strvocemenu strvocemenu,
       M.cdnvocemenupadre cdnvocemenupadre,
       vm.cdnfunzione
  from ts_menu M,
       ts_voce_menu VM,
       ts_menu_home MH,
       ts_profilo_gruppo PG,
       ts_profilatura_utente PU
 where VM.cdnvocemenu = M.cdnvocemenu
       and MH.cdnmenu = M.cdnmenu
       and MH.cdnmenu = PG.cdnmenu
       and PG.cdngruppo = PU.cdngruppo
       and PG.cdnprofilo = PU.cdnprofilo
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/VW_WEB_DE_MANSIONE.sql
************************************************************************************** */


CREATE OR REPLACE VIEW VW_WEB_DE_MANSIONE ( CODMANSIONE, 
STRDESCRIZIONE, CODPADRE, CDNLIVELLO, FLGFREQUENTE, 
DATINIZIOVAL, DATFINEVAL ) AS select codmansione, strdescrizione,codpadre,cdnlivello,flgfrequente,datinizioval, datfineval
from de_mansione
where codmansione in (
	  select codMansione
	  from do_mansione, do_richiesta_az
	  where (do_mansione.PRGRICHIESTAAZ=do_richiesta_az.PRGRICHIESTAAZ)
	  		and (do_richiesta_az.FLGPUBBLICATA='S' and do_richiesta_az.NUMSTORICO=0)
			and (do_richiesta_az.DATPUBBLICAZIONE <= SYSDATE AND SYSDATE <=do_richiesta_az.DATSCADENZAPUBBLICAZIONE)
	  )
union
select codmansione, strdescrizione,codpadre,cdnlivello,flgfrequente,datinizioval, datfineval
from de_mansione
where cdnlivello = 1
and codmansione
	in (select codpadre from de_mansione m1
	where
	codmansione in (
	  select codMansione
	  from do_mansione, do_richiesta_az
	  where (do_mansione.PRGRICHIESTAAZ=do_richiesta_az.PRGRICHIESTAAZ)
	  		and (do_richiesta_az.FLGPUBBLICATA='S' and do_richiesta_az.NUMSTORICO=0)
			and (do_richiesta_az.DATPUBBLICAZIONE <= SYSDATE AND SYSDATE <=do_richiesta_az.DATSCADENZAPUBBLICAZIONE)
	  )
	)
union
select codmansione, strdescrizione,codpadre,cdnlivello,flgfrequente,datinizioval, datfineval
from de_mansione
where cdnlivello = 0
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Views/wv_pr_rpt_indisp.sql
************************************************************************************** */


CREATE OR REPLACE VIEW WV_PR_RPT_INDISP AS
SELECT ind.strragsocialeazienda, apl.prgpattolavoratore, apl.cdnlavoratore
  FROM pr_indisponibilita ind,
       am_lav_patto_scelta aps,
       am_patto_lavoratore apl
 WHERE aps.strchiavetabella = ind.prgindisponibilita
   AND apl.prgpattolavoratore = aps.prgpattolavoratore
   AND aps.codlsttab = 'PR_IND'
   AND apl.datfine IS NULL
/


