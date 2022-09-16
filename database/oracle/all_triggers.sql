set define off
set pagesize 0
set linesize 10000
set sqlblanklines on




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_am_elenco_anagrafico.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AM_ELENCO_ANAGRAFICO
AFTER INSERT
ON AM_ELENCO_ANAGRAFICO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdCollegaTab('EA', :new.cdnlavoratore, :new.prgElencoAnagrafico, null, null);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_am_ex_perm_sogg.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AM_EX_PERM_SOGG
 AFTER INSERT
 ON AM_EX_PERM_SOGG
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_am_mobilita_iscr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AM_MOBILITA_ISCR
 AFTER INSERT
 ON AM_MOBILITA_ISCR
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_am_movimento.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AM_MOVIMENTO
 AFTER INSERT
 ON AM_MOVIMENTO
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
  PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_am_obbligo_formativo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AM_OBBLIGO_FORMATIVO
 AFTER INSERT
 ON AM_OBBLIGO_FORMATIVO
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_am_stato_occupaz.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AM_STATO_OCCUPAZ
 AFTER INSERT
 ON AM_STATO_OCCUPAZ
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
  PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_an_azienda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AN_AZIENDA
 AFTER INSERT
 ON AN_AZIENDA
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
  PG_IDO.pdStorico_AZ(:new.prgazienda, :new.strcodicefiscale, :new.strpartitaiva, :new.strragionesociale, :new.codnatgiuridica, :new.codtipoazienda, :new.cdnUtIns);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_an_lavoratore.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AN_LAVORATORE
 AFTER INSERT
 ON AN_LAVORATORE
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
  PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
  PG_GESTAMM.pdCollegaTab('AN_LAV', :new.cdnlavoratore,  :new.cdnLavoratore, :new.cdnUtIns, :new.codComDom);
   if (:OLD.codcomdom is null  and :NEW.codcomdom is not null) then
           PG_GESTAMM.pdCollegaTab('AN_LAV1', :new.cdnlavoratore,  :new.cdnLavoratore, :new.cdnUtIns,
                                   :new.codComDom);
  end IF;
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_an_unita_azienda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_AN_UNITA_AZIENDA
AFTER INSERT
ON AN_UNITA_AZIENDA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
  PG_IDO.pdStorico_UA(:new.prgazienda, :new.prgunita, :new.codcom, :new.codazstato, :new.codateco, :new.cdnUtIns);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_do_disponibilita.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_DO_DISPONIBILITA
	AFTER INSERT ON DO_DISPONIBILITA
	FOR EACH ROW
BEGIN
	if(:new.codDisponibilitaRosa <> 'A') then
		PG_STORIA_ROSA.PDAGGSTORIAROSAXDISPO(:new.prgRichiestaAz, :new.cdnLavoratore, :new.codDisponibilitaRosa, :new.datDisponibilita);
	end if;
END;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_do_esito_candidato.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_DO_ESITO_CANDIDATO
	AFTER INSERT ON DO_ESITO_CANDIDATO
	FOR EACH ROW
BEGIN
	 PG_STORIA_ROSA.PDAGGSTORIAROSAXESITO(:new.prgRichiestaAz, :new.cdnLavoratore, :new.codEsitoDaAzienda, :new.codEsitoDaCandidato);
END;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_do_richiesta_az.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_DO_RICHIESTA_AZ
AFTER INSERT
ON DO_RICHIESTA_AZ
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
PG_IDO.pdInsRichiesta_az(:new.prgazienda, :new.prgunita, :new.prgrichiestaaz, :new.cdnUtIns, :new.numstorico, :new.flgFuoriSede);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_abilitazione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_ABILITAZIONE
AFTER INSERT
ON LG_PR_ABILITAZIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_competenza.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_COMPETENZA
AFTER INSERT
ON LG_PR_COMPETENZA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_corso.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_CORSO
AFTER INSERT
ON LG_PR_CORSO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_credito.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_CREDITO
AFTER INSERT
ON LG_PR_CREDITO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_dis_comune.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_DIS_COMUNE
AFTER INSERT
ON LG_PR_DIS_COMUNE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_COM');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_dis_contratto.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_DIS_CONTRATTO
AFTER INSERT
ON LG_PR_DIS_CONTRATTO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_CON');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_dis_orario.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_DIS_ORARIO
AFTER INSERT
ON LG_PR_DIS_ORARIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_ORA');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_dis_provincia.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_DIS_PROVINCIA
AFTER INSERT
ON LG_PR_DIS_PROVINCIA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_PRO');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_dis_regione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_DIS_REGIONE
AFTER INSERT
ON LG_PR_DIS_REGIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_REG');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_dis_stato.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_DIS_STATO
AFTER INSERT
ON LG_PR_DIS_STATO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_STA');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_dis_turno.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_DIS_TURNO
AFTER INSERT
ON LG_PR_DIS_TURNO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_TUR');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_esp_lavoro.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_ESP_LAVORO
AFTER INSERT
ON LG_PR_ESP_LAVORO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_ESP_L');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_indisponibilita.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_INDISPONIBILITA
AFTER INSERT
ON LG_PR_INDISPONIBILITA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_info.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_INFO
AFTER INSERT
ON LG_PR_INFO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_lingua.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_LINGUA
AFTER INSERT
ON LG_PR_LINGUA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_mansione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_MANSIONE
AFTER INSERT
ON LG_PR_MANSIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_mobil_geogr.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_MOBIL_GEOGR
AFTER INSERT
ON LG_PR_MOBIL_GEOGR
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_MO_GE');
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_lg_pr_studio.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_LG_PR_STUDIO
AFTER INSERT
ON LG_PR_STUDIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
if(:new.strtipoop='D') then
   PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtLog,  :new.dtmModLog);
end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_abilitazione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_ABILITAZIONE
AFTER INSERT
ON PR_ABILITAZIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_competenza.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_COMPETENZA
AFTER INSERT
ON PR_COMPETENZA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_corso.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_PR_CORSO
 AFTER INSERT
 ON PR_CORSO
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_credito.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_PR_CREDITO
 AFTER INSERT
 ON PR_CREDITO
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_dis_ccontratto.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_DIS_CCONTRATTO
AFTER INSERT
ON PR_DIS_CONTRATTO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_CON');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_CON');
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_dis_comune.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_DIS_COMUNE
AFTER INSERT
ON PR_DIS_COMUNE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_COM');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_COM');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_dis_orario.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_DIS_ORARIO
AFTER INSERT
ON PR_DIS_ORARIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_ORA');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_ORA');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_dis_provincia.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_DIS_PROVINCIA
AFTER INSERT
ON PR_DIS_PROVINCIA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_PRO');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_PRO');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_dis_regione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_DIS_REGIONE
AFTER INSERT
ON PR_DIS_REGIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_REG');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_REG');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_dis_stato.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_DIS_STATO
AFTER INSERT
ON PR_DIS_STATO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_STA');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_STA');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_dis_turno.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_DIS_TURNO
AFTER INSERT
ON PR_DIS_TURNO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_TUR');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_TUR');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_esp_lavoro.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_ESP_LAVORO
AFTER INSERT
ON PR_ESP_LAVORO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_ESP_L');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_indisponibilita.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_INDISPONIBILITA
AFTER INSERT 
ON PR_INDISPONIBILITA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_info.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_PR_INFO
 AFTER INSERT
 ON PR_INFO
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_lingua.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_PR_LINGUA
 AFTER INSERT
 ON PR_LINGUA
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_mansione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_MANSIONE
AFTER INSERT
ON PR_MANSIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_mobil_geogr.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_MOBIL_GEOGR
AFTER INSERT
ON PR_MOBIL_GEOGR
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_MO_GE');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_MO_GE');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_nota_lav.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AIR_PR_NOTA_LAV
AFTER INSERT
ON PR_NOTA_LAV
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_air_pr_studio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AIR_PR_STUDIO
AFTER INSERT
ON PR_STUDIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
 PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_am_ex_perm_sogg.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_AM_EX_PERM_SOGG
AFTER UPDATE
ON AM_EX_PERM_SOGG
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_am_mobilita_iscr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_AM_MOBILITA_ISCR
AFTER UPDATE
ON AM_MOBILITA_ISCR
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_am_movimento.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_AM_MOVIMENTO
AFTER UPDATE
ON AM_MOVIMENTO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_am_obbligo_formativo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_AM_OBBLIGO_FORMATIVO
AFTER UPDATE
ON AM_OBBLIGO_FORMATIVO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_am_stato_occupaz.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_AM_STATO_OCCUPAZ
AFTER UPDATE
ON AM_STATO_OCCUPAZ
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
  PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_an_lavoratore.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_AN_LAVORATORE
AFTER UPDATE
ON AN_LAVORATORE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_do_disponibilita.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_DO_DISPONIBILITA
	AFTER UPDATE ON DO_DISPONIBILITA
	FOR EACH ROW
BEGIN
	if(:new.codDisponibilitaRosa <> 'A') then
		PG_STORIA_ROSA.PDAGGSTORIAROSAXDISPO(:new.prgRichiestaAz, :new.cdnLavoratore, :new.codDisponibilitaRosa, :new.datDisponibilita);
	end if;
END;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_do_esito_candidato.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_DO_ESITO_CANDIDATO
	AFTER UPDATE ON DO_ESITO_CANDIDATO
	FOR EACH ROW
BEGIN
	 PG_STORIA_ROSA.PDAGGSTORIAROSAXESITO(:new.prgRichiestaAz, :new.cdnLavoratore, :new.codEsitoDaAzienda, :new.codEsitoDaCandidato);
END;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_do_nominativo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_DO_NOMINATIVO
	AFTER UPDATE on DO_NOMINATIVO
	REFERENCING OLD AS old NEW AS new
	FOR EACH ROW
begin
	if(:old.codTipoCanc is null and :new.codTipoCanc is not null) then
		PG_STORIA_ROSA.pdAggStoriaRosaXCanc(:new.cdnLavoratore, :new.prgRosa, :new.codTipoCanc, :new.dtmCanc);
	end if;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_abilitazione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_ABILITAZIONE
AFTER UPDATE
ON PR_ABILITAZIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_competenza.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_COMPETENZA
AFTER UPDATE
ON PR_COMPETENZA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_corso.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_PR_CORSO
AFTER UPDATE
ON PR_CORSO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
 PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_credito.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_PR_CREDITO
AFTER UPDATE
ON PR_CREDITO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
 PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_dis_comune.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_DIS_COMUNE
AFTER UPDATE
ON PR_DIS_COMUNE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_COM');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_COM');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_dis_contratto.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_DIS_CONTRATTO
AFTER UPDATE
ON PR_DIS_CONTRATTO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_CON');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_CON');
end;
-- END PL/SQL BLOCK (do not remove this line) ------------
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_dis_orario.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_DIS_ORARIO
AFTER UPDATE
ON PR_DIS_ORARIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_ORA');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_ORA');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_dis_provincia.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_DIS_PROVINCIA
AFTER UPDATE
ON PR_DIS_PROVINCIA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_PRO');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_PRO');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_dis_regione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_DIS_REGIONE
AFTER UPDATE
ON PR_DIS_REGIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_REG');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_REG');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_dis_stato.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_DIS_STATO
AFTER UPDATE
ON PR_DIS_STATO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_STA');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_STA');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_dis_turno.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_DIS_TURNO
AFTER UPDATE
ON PR_DIS_TURNO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_TUR');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_D_TUR');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_esp_lavoro.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_ESP_LAVORO
AFTER UPDATE
ON PR_ESP_LAVORO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_ESP_L');
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_indisponibilita.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_INDISPONIBILITA
AFTER UPDATE
ON PR_INDISPONIBILITA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_info.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_PR_INFO
AFTER UPDATE
ON PR_INFO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_lingua.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_PR_LINGUA
AFTER UPDATE
ON PR_LINGUA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
 PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_mansione.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_MANSIONE
AFTER UPDATE
ON PR_MANSIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_mobil_geogr.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_MOBIL_GEOGR
AFTER UPDATE
ON PR_MOBIL_GEOGR
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_MO_GE');
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.prgMansione, :new.cdnUtMod,  :new.dtmMod, 'PR_MO_GE');	
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_nota_lav.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_AMR_PR_NOTA_LAV
AFTER UPDATE
ON PR_NOTA_LAV
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
	PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
	PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_amr_pr_studio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_AMR_PR_STUDIO
AFTER UPDATE
ON PR_STUDIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 PG_GESTAMM.pdModSchedaAnagProf (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
 PG_SOGG_ESTERNI.pdModValiditaCurriculum (:new.cdnLavoratore, :new.cdnUtMod,  :new.dtmMod);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ac_indirizzo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AC_INDIRIZZO
	before insert on AC_INDIRIZZO for each row
	begin
	:new.numKloIndirizzo := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ac_rilevazione_fabbisog.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AC_RILEVAZIONE_FABBISOG
	before insert on AC_RILEVAZIONE_FABBISOGNO for each row
	begin
	:new.numKloRilFab := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ac_scrivania.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AC_SCRIVANIA
	before insert on AC_SCRIVANIA for each row
	begin
	:new.numKloScrivania := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ag_agenda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AG_AGENDA
	before insert on AG_AGENDA for each row
	begin
	:new.numKloAgenda := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ag_assegnazione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AG_ASSEGNAZIONE
	before insert on AG_ASSEGNAZIONE for each row
	begin
	:new.numKloAssegnazione := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ag_contatto.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AG_CONTATTO
	before insert on AG_CONTATTO for each row
	begin
	:new.numKloContatto := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ag_evento.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AG_EVENTO
	before insert on AG_EVENTO for each row
	begin
	:new.numKloEvento := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ag_giornonl.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AG_GIORNONL
	before insert on AG_GIORNONL for each row
	begin
	:new.numKloGiornonl := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ag_slot.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AG_SLOT
	before insert on AG_SLOT for each row
	begin
	:new.numKloSlot := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_art16.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_ART16
	before insert on AM_ART16 for each row
	begin
	:new.numKloArt16 := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_conv.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_CONV
	before insert on AM_CM_CONV for each row
	begin
	:new.numKloConv := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_graduatoria.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_GRADUATORIA
	before insert on AM_CM_GRADUATORIA for each row
	begin
	:new.numKloGraduatoria := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_iscr.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_ISCR
BEFORE INSERT
ON AM_CM_ISCR
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
:new.numKloCMIscr := PG_SISTEMA.pdConcorrenzaIns;
PG_GESTAMM.checkIntersezioneDateCM(:new.cdnlavoratore, :new.datDataInizio,'AM_CM_IS',:new.codCmTipoIscr);
PG_GESTAMM.pdChiudiRecordPrecCM('AM_CM_IS', :new.cdnlavoratore, :new.prgCMIscr, :new.datDataInizio, :new.codCmTipoIscr);
END;
/






/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_prospetto_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_PROSPETTO_INF
	before insert on AM_CM_PROSPETTO_INF for each row
	begin
	:new.numKloProspettoInf := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_rich_comp_terr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_RICH_COMP_TERR
	before insert on AM_CM_RICH_COMP_TERR for each row
	begin
	:new.numKloCompTerr := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_rich_esonero.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_RICH_ESONERO
	before insert on AM_CM_RICH_ESONERO for each row
	begin
	:new.numKloEsonero := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_rich_gradualita.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_RICH_GRADUALITA
	before insert on AM_CM_RICH_GRADUALITA for each row
	begin
	:new.numKloGradualita := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_cm_rich_sospensione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_CM_RICH_SOSPENSIONE
	before insert on AM_CM_RICH_SOSPENSIONE for each row
	begin
	:new.numKloSospensione := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_condizionalita.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_CONDIZIONALITA
 before insert on AM_CONDIZIONALITA for each row
begin
 :new.NUMKLOCOND := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_dich_disponibilita.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_DICH_DISPONIBILITA
BEFORE INSERT
on am_dich_disponibilita
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
  :new.numKloDichDisp := PG_SISTEMA.pdConcorrenzaIns;
  PG_GESTAMM.checkIntersezioneDate (:new.prgElencoAnagrafico, :new.datDichiarazione,'AM_DIC_D');
  PG_GESTAMM.pdChiudiRecordPrec('AM_DIC_D', :new.prgElencoAnagrafico, :new.prgDichDisponibilita, :new.datDichiarazione);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_dich_lav_dettaglio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_DICH_LAV_DETTAGLIO
	before insert on AM_DICH_LAV_DETTAGLIO for each row
	begin
	:new.numKloDichLavDettaglio := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_dich_lav.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_DICH_LAV
	before insert on AM_DICH_LAV for each row
	begin
	:new.numKloDichLav := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_documento.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_DOCUMENTO BEFORE INSERT ON AM_DOCUMENTO REFERENCING OLD AS OLD NEW AS NEW FOR EACH ROW
begin
 :new.numKloDocumento := PG_SISTEMA.pdConcorrenzaIns;
 PG_GESTAMM.CTRL_DOC_DOPPI( :new.numannoprot, :new.NUMPROTOCOLLO, :new.CDNUTINS );
 IF :new.cdnlavoratore IS NOT NULL THEN
   PG_GESTAMM.checkIntersezioneDate (:new.cdnLavoratore, :new.datInizio, 'AM_DOC', :new.codTipoDocumento, :new.prgDocumento);
   PG_GESTAMM.pdChiudiRecordPrec('AM_DOC', :new.cdnlavoratore, :new.prgDocumento, :new.datInizio, :new.codTipoDocumento);
   PG_GESTAMM.pckProtocollazione (:new.cdnlavoratore, :new.CODTIPODOCUMENTO, :new.codStatoAtto) ;
 END IF;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_domanda_registro_com.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_DOMANDA_REGISTRO_COM
	before insert on AM_DOMANDA_REGISTRO_COM for each row
	begin
	:new.numKloDomandaRegComm := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_elenco_anagrafico.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_ELENCO_ANAGRAFICO
BEFORE INSERT
ON AM_ELENCO_ANAGRAFICO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 :new.numKloElencoAnag := PG_SISTEMA.pdConcorrenzaIns;
 PG_GESTAMM.checkIntersezioneDate (:new.cdnlavoratore, :new.datInizio,'EA');
 PG_GESTAMM.pdChiudiRecordPrec('EA', :new.cdnlavoratore, :new.prgElencoAnagrafico,  :new.datInizio);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_ex_perm_sogg.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_EX_PERM_SOGG
	before insert on AM_EX_PERM_SOGG for each row
begin
	:new.numKloPermSogg := PG_SISTEMA.pdConcorrenzaIns;
	PG_GESTAMM.pdChiudiRecordPrec('AM_EX_PS', :new.cdnLavoratore, :new.prgPermSogg, :new.datRichiesta);
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_famiglia.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_FAMIGLIA
	before insert on AM_FAMIGLIA for each row
	begin
	:new.numKloFamiglia := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AM_FORZA_MOV.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_FORZA_MOV
 before insert on AM_FORZA_MOV for each row
begin
 :new.NUMKLOFORZAMOV := PG_SISTEMA.pdConcorrenzaIns;
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_indisp_temp.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_INDISP_TEMP
	before insert on AM_INDISP_TEMP for each row
	begin
	:new.numKloIndispTemp := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_lavoratore_neet_risposte.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_LAVORATORE_NEET_RIS
	before insert on AM_LAVORATORE_NEET_RISPOSTE for each row
	begin
	:new.numklolavneetrisposte := PG_SISTEMA.pdConcorrenzaIns;
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_lavoratore_neet.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_LAVORATORE_NEET
	before insert on AM_LAVORATORE_NEET for each row
	begin
	:new.numklolavneet := PG_SISTEMA.pdConcorrenzaIns;
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_lav_patto_scelta.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_LAV_PATTO_SCELTA
BEFORE INSERT
ON AM_LAV_PATTO_SCELTA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
  --
  -- Controllo integrita' refenziale della tabella am_lav_patto_scelta
  --
  PG_GestAmm.pdIntegRefPattoScelta (:new.codLstTab,  :new.prgPattoLavoratore, :new.strChiaveTabella, :new.strChiaveTabella2,  :new.strChiaveTabella3);
end;
-- END PL/SQL BLOCK (do not remove this line) ----------------------------------

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_lav_profilo.sql
************************************************************************************** */



create or replace TRIGGER TG_BIR_AM_LAV_PROFILO
 before insert on AM_LAVORATORE_PROFILO for each row
begin
 :new.Numklolavprofilo := PG_SISTEMA.pdConcorrenzaIns;
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_lav_prof_ris.sql
************************************************************************************** */



create or replace TRIGGER TG_BIR_AM_LAV_PROF_RIS
 before insert on AM_LAVORATORE_PROFILO_RISPOSTE for each row
begin
 :new.Numklolavprofrisposte := PG_SISTEMA.pdConcorrenzaIns;
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_lista_spett_iscr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_LISTA_SPETT_ISCR
	before insert on AM_LISTA_SPETT_ISCR for each row
	begin
	:new.numKloLSIscr := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_mb_lsu.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_MB_LSU
	before insert on AM_MB_LSU for each row
	begin
	:new.numKloLSU := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_mobilita_iscr_app.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_MOBILITA_ISCR_APP
 BEFORE INSERT ON AM_MOBILITA_ISCR_APP FOR EACH ROW
begin
 :new.NUMKLOMOBISCRAPP := PG_SISTEMA.pdConcorrenzaIns;
 end;
/








/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AM_MOBILITA_ISCR_MANS.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_MOBILITA_ISCR_MANS
	before insert on AM_MOBILITA_ISCR_MANS for each row
begin
	:new.NUMKLOMOBILITAISCRMANS := PG_SISTEMA.pdConcorrenzaIns;
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_mobilita_iscr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_MOBILITA_ISCR
	before insert on AM_MOBILITA_ISCR for each row
begin
	:new.numKloMobIscr := PG_SISTEMA.pdConcorrenzaIns;
	PG_GESTAMM.checkIntersezioneDateNew (:new.cdnlavoratore, :new.datInizio, :new.datFine, 'AM_MB_IS');
    PG_GESTAMM.pdChiudiRecordPrec('AM_MB_IS', :new.cdnlavoratore, :new.prgMobilitaIscr, :new.datInizio);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AM_MOBILITA_RIS_DETT.SQL
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_MOBILITA_RIS_DETT

 BEFORE INSERT ON  AM_MOBILITA_RIS_DETT FOR EACH ROW
begin

 :new.NUMKLOMOBILITArisDETT := PG_SISTEMA.pdConcorrenzaIns;

 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AM_MOBILITA_RIS.SQL
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_MOBILITA_RIS

 BEFORE INSERT ON  AM_MOBILITA_RIS FOR EACH ROW
begin

 :new.NUMKLOMOBILITAris := PG_SISTEMA.pdConcorrenzaIns;

 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AM_MOB_ISCR_DAENTE_BK.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_MOB_ISCR_DAENTE_BK
 BEFORE INSERT ON  AM_MOB_ISCR_DAENTE_BK FOR EACH ROW
begin
 :new.NUMKLOMOBISCRdaENTEBK := PG_SISTEMA.pdConcorrenzaIns;
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AM_MOV_APP_ARCHIVIO.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_MOV_APP_ARCHIVIO
BEFORE INSERT 
ON AM_MOV_APP_ARCHIVIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin 
	:new.numKloMovApp := PG_SISTEMA.pdConcorrenzaIns; 
	end; 

/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AM_MOV_APP_RICICLAGGIO.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_MOV_APP_RICICLAGGIO
BEFORE INSERT 
ON AM_MOV_APP_RICICLAGGIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin 
	:new.numKloMovApp := PG_SISTEMA.pdConcorrenzaIns; 
	end; 

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_movimento_appoggio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_MOVIMENTO_APPOGGIO
 before insert on AM_MOVIMENTO_APPOGGIO for each row
begin
 :new.numKloMovApp := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_movimento_apprendist.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_MOVIMENTO_APPRENDIST
BEFORE INSERT
ON AM_MOVIMENTO_APPRENDIST
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 :new.numKloMovApprendist := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_movimento_missione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_MOVIMENTO_MISSIONE
BEFORE INSERT 
ON AM_MOVIMENTO_MISSIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin 
	:new.numKloMis := PG_SISTEMA.pdConcorrenzaIns; 
end; 
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_movimento.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_MOVIMENTO
 before insert on AM_MOVIMENTO for each row
begin
 :new.numKloMov := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_obbligo_formativo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_OBBLIGO_FORMATIVO
	before insert on AM_OBBLIGO_FORMATIVO for each row
	begin
	:new.numKloObbligoForm := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_patto_lavoratore.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_PATTO_LAVORATORE
BEFORE INSERT
ON  AM_PATTO_LAVORATORE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
  :new.numKloPattoLavoratore := PG_SISTEMA.pdConcorrenzaIns;
  PG_GESTAMM.checkIntersezioneDate (:new.cdnlavoratore, :new.datStipula,'AM_PAT_L');
  PG_GESTAMM.pdChiudiRecordPrec('AM_PAT_L', :new.cdnLavoratore, :new.prgPattoLavoratore,  :new.datStipula);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_periodo_lav.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_PERIODO_LAV
 before insert on AM_PERIODO_LAVORATIVO for each row
begin
 :new.NUMKLOPERIODOLAV := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_privacy.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_PRIVACY
	before insert on AM_PRIVACY for each row
	begin
	:new.numKloPrivacy := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_programma_ente.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_PROGRAMMA_ENTE
 before insert on AM_PROGRAMMA_ENTE for each row
begin
 :new.NUMKLOPROGRAMMA := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_protocollo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_PROTOCOLLO
	before insert on AM_PROTOCOLLO for each row
	begin
	:new.numKloProtocollo := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_rdc.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AM_RDC
 before insert on AM_RDC for each row
begin
 :new.NUMKLORDC := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_reddito_attivazione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_REDDITO_ATTIVAZIONE
 before insert on AM_REDDITO_ATTIVAZIONE for each row
begin
 :new.numKloRa := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_stato_occupaz.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_stato_occupaz
 BEFORE INSERT
 oN AM_stato_occupaz
 rEFERENCING OLD AS OLD NEW AS NEW
 fOR EACH ROW
begin
  :new.numKloStatoOccupaz := PG_SISTEMA.pdConcorrenzaIns;
  PG_GESTAMM.checkIntersezioneDate (:new.cdnlavoratore, :new.datInizio,'AM_S_OCC');
  PG_GESTAMM.pdChiudiRecordPrec('AM_S_OCC', :new.cdnLavoratore, :new.prgstatooccupaz, :new.datINizio);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_ua_autorizzazione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_UA_AUTORIZZAZIONE
	before insert on AM_UA_AUTORIZZAZIONE for each row
	begin
	:new.numKloAutoriz := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_am_ua_patto.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AM_UA_PATTO
	before insert on AM_UA_PATTO for each row
	begin
	:new.numKloPattoUnitaAziendale := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_azi_autorizzazione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AN_AZI_AUTORIZZAZIONE
	before insert on AN_AZI_AUTORIZZAZIONE for each row
	begin
	:new.numKlo := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_azienda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AN_AZIENDA
 before insert on AN_AZIENDA for each row
begin
 :new.numKloAzienda := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_az_storia_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AN_AZ_STORIA_INF
	before insert on AN_AZ_STORIA_INF for each row
	begin
	:new.numKloAzStoriaInf := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_evidenza.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AN_EVIDENZA
	before insert on AN_EVIDENZA for each row
begin
	:new.numKloEvidenza := PG_SISTEMA.pdConcorrenzaIns;
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_lavoratore.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AN_LAVORATORE
	before insert on AN_LAVORATORE for each row
	begin
	:new.numkloLavoratore := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_lav_storia_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AN_LAV_STORIA_INF
 before insert on AN_LAV_STORIA_INF for each row
begin
 :new.numKloLavStoriaInf := PG_SISTEMA.pdConcorrenzaIns;
  PG_GESTAMM.pdChiudiRecordPrec('AN_LAV_S', :new.cdnLavoratore, :new.prgLavStoriaInf, :new.datINizio);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_ua_storia_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AN_UA_STORIA_INF
	before insert on AN_UA_STORIA_INF for each row
begin
	:new.numKloUAStoriaInf := PG_SISTEMA.pdConcorrenzaIns;
end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_an_unita_azienda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AN_UNITA_AZIENDA
BEFORE INSERT
ON AN_UNITA_AZIENDA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 :new.numKloUnitaAzienda := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AS_AVVIO.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AS_AVVIO
BEFORE INSERT 
ON AS_AVVIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin 
	:new.numKloAvvio := PG_SISTEMA.pdConcorrenzaIns; 
	end; 

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AS_AVV_SELEZIONE.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_AS_AVV_SELEZIONE
BEFORE INSERT 
ON AS_AVV_SELEZIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin 
	:new.numKloAvvSelezione := PG_SISTEMA.pdConcorrenzaIns; 
	end; 

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_AS_STORIA_STATO_OCC.sql
************************************************************************************** */


create or replace trigger TG_BIR_AS_STORIA_STATO_OCC 
	before insert on AS_STORIA_STATO_OCC for each row 
	begin 
	:new.numKloStoriaStatoOcc := PG_SISTEMA.pdConcorrenzaIns; 
	end; 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_as_valore_isee.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_AS_VALORE_ISEE
BEFORE INSERT
ON AS_VALORE_ISEE
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  :new.numKloValoreIsee := PG_SISTEMA.pdConcorrenzaIns;
  
  PG_GESTAMM.checkIntersezionePeriodi ('AS_ISEE', :new.cdnlavoratore, :new.datinizioval, :new.datfineval);
  
  PG_GESTAMM.pdChiudiRecordPeriodoPrec('AS_ISEE', :new.cdnLavoratore, :new.datinizioval);
  
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_bd_adesione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_BD_ADESIONE
 before insert on BD_ADESIONE for each row
begin
 :new.numkloadesione := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_ca_presa_atto.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_CA_PRESA_ATTO
BEFORE INSERT 
ON CA_PRESA_ATTO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin 
	:new.numKloPresaAtto := PG_SISTEMA.pdConcorrenzaIns; 
	end; 

/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_ambiente.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_AMBIENTE
	before insert on DE_AMBIENTE for each row
	begin
	:new.numKloAmbiente := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_comune.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_COMUNE
 before insert on DE_COMUNE for each row
begin
 :new.numKloComune := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_cpi.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_CPI
 before insert on DE_CPI for each row
begin
 :new.numKloCpi := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_impegno.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_IMPEGNO
	before insert on DE_IMPEGNO for each row
	begin
	:new.numKloImpegno := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_mv_grado.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_MV_GRADO
	before insert on DE_MV_GRADO for each row
	begin
	:new.numKloGrado := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_mv_tipo_ass.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_MV_TIPO_ASS
	before insert on DE_MV_TIPO_ASS for each row
	begin
	:new.numKloTipoAss := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_normativa.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_NORMATIVA
	before insert on DE_NORMATIVA for each row
	begin
	:new.numKloNormativa := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_orario.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_ORARIO
	before insert on DE_ORARIO for each row
	begin
	:new.numKloOrario := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_provincia.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_PROVINCIA
	before insert on DE_PROVINCIA for each row
	begin
	:new.numKloProvincia := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_servizio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_SERVIZIO
	before insert on DE_SERVIZIO for each row
	begin
	:new.numKloServizio := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_settimana_tipo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_SETTIMANA_TIPO
	before insert on DE_SETTIMANA_TIPO for each row
	begin
	:new.numKlo := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_stato_slot.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_STATO_SLOT
	before insert on DE_STATO_SLOT for each row
	begin
	:new.numKloStatoSlot := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_tipo_dich.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_TIPO_DICH
	before insert on DE_TIPO_DICH for each row
	begin
	:new.numKloTipoDich := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_de_tipo_patto.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DE_TIPO_PATTO
	before insert on DE_TIPO_PATTO for each row
	begin
	:new.numKloTipoPatto := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_do_incrocio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DO_INCROCIO
	before insert on DO_INCROCIO for each row
	begin
	:new.numKloIncrocio := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_do_nominativo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DO_NOMINATIVO
	before insert on DO_NOMINATIVO for each row
begin
	:new.numKloNominativo := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_do_richiesta_az.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DO_RICHIESTA_AZ
	before insert on DO_RICHIESTA_AZ for each row
	begin
	:new.numKloRichiestaAz := PG_SISTEMA.pdConcorrenzaIns;
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_do_rosa.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_DO_ROSA
	before insert on DO_ROSA for each row
	begin
	:new.numKloRosa := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_nt_lav_storia_notifiche.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_NT_LAV_STORIA_NOTIFICHE
	before insert on NT_LAV_STORIA_NOTIFICHE for each row
	begin
	:new.numKloLavStoriaModifiche := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_or_colloquio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_OR_COLLOQUIO
	before insert on OR_COLLOQUIO for each row
	begin
	:new.numKloColloquio := PG_SISTEMA.pdConcorrenzaIns;
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_or_scheda_partecipante.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BIR_OR_SCHEDA_PARTECIPANTE
 before insert on OR_SCHEDA_PARTECIPANTE for each row
begin
 :new.NUMKLOPARTECIPANTE := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_OR_VCH_ATTIVITA.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_OR_VCH_ATTIVITA
 before insert on OR_VCH_ATTIVITA for each row
begin
 :new.NUMKLOATTIVITA := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_OR_VCH_EVENTO.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_OR_VCH_EVENTO
 before insert on OR_VCH_EVENTO for each row
begin
 :new.NUMKLOEVENTO := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_OR_VCH_MODALITA_09032016.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_OR_VCH_MODALITA
 before insert on OR_VCH_MODALITA for each row
begin
 :new.NUMKLOVCHMODALITA := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_OR_VCH_Voucher.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_OR_VCH_Voucher
 before insert on OR_VCH_Voucher for each row
begin
 :new.NUMKLOVOUCHER := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_pi_dett_pt_disabile.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_PI_DETT_PT_DISABILE
  before insert on PI_DETT_PT_DISABILE for each row
  begin
  :new.numKloPIDettPT := PG_SISTEMA.pdConcorrenzaIns;
  end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_PR_VALIDITA.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_PR_VALIDITA
 BEFORE INSERT ON PR_VALIDITA FOR EACH ROW
begin
 :new.numKloValidita := PG_SISTEMA.pdConcorrenzaIns;
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_sp_lavoratore.sql
************************************************************************************** */



create or replace trigger TG_BIR_SP_LAVORATORE
 before insert on SP_LAVORATORE for each row
begin
	:new.numKloSap := PG_SISTEMA.pdConcorrenzaIns;
 end;

/
 



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_sp_notifica_pn.sql
************************************************************************************** */


create or replace trigger TG_BIR_SP_NOTIFICA_PN
	before insert on SP_NOTIFICA_PN for each row
begin
	:new.NUMKLOSPNOTIFICAPN := PG_SISTEMA.pdConcorrenzaIns;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bir_sp_politica_nazionale.sql
************************************************************************************** */


create or replace trigger TG_BIR_SP_POLITICA_NAZIONALE
	before insert on SP_POLITICA_NAZIONALE for each row
begin
	:new.NUMKLOSPPOLITICANAZ := PG_SISTEMA.pdConcorrenzaIns;
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_TS_ERR_AMB_TIPO.SQL
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_TS_ERR_AMB_TIPO
 BEFORE INSERT ON  TS_ERR_AMB_TIPO FOR EACH ROW
begin
 :new.NUMKLOERRAMBTIPO := PG_SISTEMA.pdConcorrenzaIns;
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_VCH_BUDGET_CPI.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_VCH_BUDGET_CPI
 before insert on VCH_BUDGET_CPI for each row
begin
 :new.NUMKLOBUDGET := PG_SISTEMA.pdConcorrenzaIns;
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_VCH_COSTI_RISULTATO.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_VCH_COSTI_RISULTATO
 before insert on VCH_COSTI_RISULTATO for each row
begin
 :new.NUMKLOCCOSTIRIS := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_VCH_DEF_DOTE.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_VCH_DEF_DOTE
 before insert on VCH_DEF_DOTE for each row
begin
 :new.NUMKLODOTE := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_VCH_ENTE_ACCREDITATO.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_VCH_ENTE_ACCREDITATO
 before insert on VCH_ENTE_ACCREDITATO for each row
begin
 :new.NUMKLOENTEACCREDITATO := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_VCH_MODELLO_VOUCHER_14032016.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_VCH_MODELLO_VOUCHER
 before insert on VCH_MODELLO_VOUCHER for each row
begin
 :new.NUMKLOMODVOUCHER := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BIR_VCH_OPERAZIONI_BUDGET.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BIR_VCH_OPERAZIONI_BUDGET
 before insert on VCH_OPERAZIONI_BUDGET for each row
begin
 :new.NUMKLOOPERAZIONIBUDGET := PG_SISTEMA.pdConcorrenzaIns;
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ac_indirizzo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AC_INDIRIZZO
	before update on AC_INDIRIZZO for each row
	begin
	:new.numKloIndirizzo := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloIndirizzo, :new.numKloIndirizzo);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ac_rilevazione_fabbisog.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AC_RILEVAZIONE_FABBISOG
	before update on AC_RILEVAZIONE_FABBISOGNO for each row
	begin
	:new.numKloRilFab := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloRilFab, :new.numKloRilFab);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ac_scrivania.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AC_SCRIVANIA
	before update on AC_SCRIVANIA for each row
	begin
	:new.numKloScrivania := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloScrivania, :new.numKloScrivania);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ag_agenda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AG_AGENDA
	before update on AG_AGENDA for each row
	begin
	:new.numKloAgenda := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAgenda, :new.numKloAgenda);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ag_assegnazione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AG_ASSEGNAZIONE
	before update on AG_ASSEGNAZIONE for each row
	begin
	:new.numKloAssegnazione := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAssegnazione, :new.numKloAssegnazione);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ag_contatto.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AG_CONTATTO
	before update on AG_CONTATTO for each row
	begin
	:new.numKloContatto := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloContatto, :new.numKloContatto);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ag_evento.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AG_EVENTO
	before update on AG_EVENTO for each row
	begin
	:new.numKloEvento := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloEvento, :new.numKloEvento);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ag_giornonl.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AG_GIORNONL
	before update on AG_GIORNONL for each row
	begin
	:new.numKloGiornonl := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloGiornonl, :new.numKloGiornonl);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ag_slot.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AG_SLOT
	before update on AG_SLOT for each row
	begin
	:new.numKloSlot := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloSlot, :new.numKloSlot);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_art16.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_ART16
	before update on AM_ART16 for each row
	begin
	:new.numKloArt16 := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloArt16, :new.numKloArt16);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_conv.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_CONV
	before update on AM_CM_CONV for each row
	begin
	:new.numKloConv := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloConv, :new.numKloConv);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_graduatoria.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_GRADUATORIA
	before update on AM_CM_GRADUATORIA for each row
	begin
	:new.numKloGraduatoria := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloGraduatoria, :new.numKloGraduatoria);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_iscr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_ISCR
	before update on AM_CM_ISCR for each row
	begin
	:new.numKloCMIscr := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloCMIscr, :new.numKloCMIscr);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_prospetto_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_PROSPETTO_INF
	before update on AM_CM_PROSPETTO_INF for each row
	begin
	:new.numKloProspettoInf := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloProspettoInf, :new.numKloProspettoInf);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_rich_comp_terr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_RICH_COMP_TERR
	before update on AM_CM_RICH_COMP_TERR for each row
	begin
	:new.numKloCompTerr := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloCompTerr, :new.numKloCompTerr);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_rich_esonero.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_RICH_ESONERO
	before update on AM_CM_RICH_ESONERO for each row
	begin
	:new.numKloEsonero := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloEsonero, :new.numKloEsonero);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_rich_gradualita.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_RICH_GRADUALITA
	before update on AM_CM_RICH_GRADUALITA for each row
	begin
	:new.numKloGradualita := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloGradualita, :new.numKloGradualita);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_cm_rich_sospensione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_CM_RICH_SOSPENSIONE
	before update on AM_CM_RICH_SOSPENSIONE for each row
	begin
	:new.numKloSospensione := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloSospensione, :new.numKloSospensione);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_condizionalita.sql
************************************************************************************** */


create or replace trigger TG_BMR_AM_CONDIZIONALITA
 before update 
 on AM_CONDIZIONALITA for each row
begin
	:new.NUMKLOCOND := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOCOND, :new.NUMKLOCOND);
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_dich_disponibilita.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_DICH_DISPONIBILITa
before update on
am_dich_disponibilita for each row
begin
 :new.numKloDichDisp := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloDichDisp, :new.numKloDichDisp);

 if (:old.datFine != :new.datFine) or (:new.datFine IS NULL and :old.datFine IS NOT NULL) or (:old.datFine IS NULL and :new.datFine IS NOT NULL)
 then
 	  PG_GESTAMM.pdApriChiudiInfColl ('AM_DOC','AM_DIC_D', :old.prgDichDisponibilita, :new.datFine);
 end if;

 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_dich_lav_dettaglio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_DICH_LAV_DETTAGLIO
	before update on AM_DICH_LAV_DETTAGLIO for each row
	begin
	:new.numKloDichLavDettaglio := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloDichLavDettaglio, :new.numKloDichLavDettaglio);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_dich_lav.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_DICH_LAV
	before update on AM_DICH_LAV for each row
	begin
	:new.numKloDichLav := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloDichLav, :new.numKloDichLav);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_did_inps.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_DID_INPS
    before update on AM_DID_INPS for each row
begin
    :new.numKloDidInps := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloDidInps, :new.numKloDidInps);
    end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_documento.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_DOCUMENTO
	before update on AM_DOCUMENTO for each row
	begin
	:new.numKloDocumento := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloDocumento, :new.numKloDocumento);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_domanda_registro_com.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_DOMANDA_REGISTRO_COM
	before update on AM_DOMANDA_REGISTRO_COM for each row
	begin
	:new.numKloDomandaRegComm := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloDomandaRegComm, :new.numKloDomandaRegComm);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_elenco_anagrafico.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_ELENCO_ANAGRAFICO
	before update on AM_ELENCO_ANAGRAFICO for each row
	begin
	:new.numKloElencoAnag := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloElencoAnag, :new.numKloElencoAnag);
    PG_GESTAMM.CHECKINTERVALLODATE(:new.datInizio, :new.datCan);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_ex_perm_sogg.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_EX_PERM_SOGG
	before update on AM_EX_PERM_SOGG for each row
	begin
	:new.numKloPermSogg := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloPermSogg, :new.numKloPermSogg);
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_famiglia.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_FAMIGLIA
	before update on AM_FAMIGLIA for each row
	begin
	:new.numKloFamiglia := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloFamiglia, :new.numKloFamiglia);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AM_FORZA_MOV.sql
************************************************************************************** */


create or replace trigger TG_BMR_AM_FORZA_MOV
 before update 
 on AM_FORZA_MOV for each row
begin
  :new.NUMKLOFORZAMOV := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOFORZAMOV, :new.NUMKLOFORZAMOV);
 end;
/






/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_indisp_temp.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_INDISP_TEMP
	before update on AM_INDISP_TEMP for each row
	begin
	:new.numKloIndispTemp := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloIndispTemp, :new.numKloIndispTemp);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_lavoratore_neet_risposte.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_LAVORATORE_NEET_RIS
BEFORE UPDATE 
ON AM_LAVORATORE_NEET_RISPOSTE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numklolavneetrisposte := PG_SISTEMA.pdConcorrenzaUpd(:old.numklolavneetrisposte, :new.numklolavneetrisposte); 
	end; 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_lavoratore_neet.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_LAVORATORE_NEET
BEFORE UPDATE 
ON AM_LAVORATORE_NEET
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numklolavneet := PG_SISTEMA.pdConcorrenzaUpd(:old.numklolavneet, :new.numklolavneet); 
	end; 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_lav_patto_scelta.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_LAV_PATTO_SCELTA
 before update on AM_LAV_PATTO_SCELTA for each row
begin
  --
  -- Controllo integrita' refenziale della tabella am_lav_patto_scelta
  --
  --PG_GestAmm.pdIntegRefPattoScelta (:new.codLstTab,  :new.prgPattoLavoratore, :new.strChiaveTabella, :new.strChiaveTabella2,  :new.strChiaveTabella3);
  null; -- 08/09/2004 Problemi di mutating con la registrazione modifiche da patto
end;
-- END PL/SQL BLOCK (do not remove this line) ----------------------------------
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_lav_profilo.sql
************************************************************************************** */



create or replace trigger TG_BMR_AM_LAV_PROFILO
 before update 
 on AM_LAVORATORE_PROFILO for each row
begin
	:new.Numklolavprofilo := PG_SISTEMA.pdConcorrenzaUpd(:old.Numklolavprofilo, :new.Numklolavprofilo);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_lav_prof_ris.sql
************************************************************************************** */



create or replace trigger TG_BMR_AM_LAV_PROF_RIS
 before update 
 on AM_LAVORATORE_PROFILO_RISPOSTE for each row
begin
	:new.Numklolavprofrisposte := PG_SISTEMA.pdConcorrenzaUpd(:old.Numklolavprofrisposte, :new.Numklolavprofrisposte);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_lista_spett_iscr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_LISTA_SPETT_ISCR
	before update on AM_LISTA_SPETT_ISCR for each row
	begin
	:new.numKloLSIscr := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloLSIscr, :new.numKloLSIscr);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_mb_lsu.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_MB_LSU
	before update on AM_MB_LSU for each row
	begin
	:new.numKloLSU := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloLSU, :new.numKloLSU);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_mobilita_iscr_app.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_MOBILITA_ISCR_APP
 BEFORE UPDATE ON AM_MOBILITA_ISCR_APP FOR EACH ROW
begin
 :new.NUMKLOMOBISCRAPP := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOMOBISCRAPP, :new.NUMKLOMOBISCRAPP);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AM_MOBILITA_ISCR_MANS.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_MOBILITA_ISCR_MANS
	before update on AM_MOBILITA_ISCR_MANS for each row
begin
	:new.NUMKLOMOBILITAISCRMANS := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOMOBILITAISCRMANS, :new.NUMKLOMOBILITAISCRMANS);
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_mobilita_iscr.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_MOBILITA_ISCR
	before update on AM_MOBILITA_ISCR for each row
	begin
	:new.numKloMobIscr := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloMobIscr, :new.numKloMobIscr);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AM_MOBILITA_RIS_DETT.SQL
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_MOBILITA_RIS_DETT

 BEFORE UPDATE ON  AM_MOBILITA_RIS_DETT FOR EACH ROW
begin

 :new.NUMKLOMOBILITArisDETT := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOMOBILITArisDETT, :new.NUMKLOMOBILITArisDETT);

 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AM_MOBILITA_RIS.SQL
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_MOBILITA_RIS

 BEFORE UPDATE ON  AM_MOBILITA_RIS FOR EACH ROW
begin

 :new.NUMKLOMOBILITAris := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOMOBILITAris, :new.NUMKLOMOBILITAris);

 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AM_MOB_ISCR_DAENTE_BK.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_MOB_ISCR_DAENTE_BK
 BEFORE UPDATE ON  AM_MOB_ISCR_DAENTE_BK FOR EACH ROW
begin
 :new.NUMKLOMOBISCRDAENTEBK := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOMOBISCRDAENTEBK, :new.NUMKLOMOBISCRDAENTEBK);
 end;
/






/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AM_MOV_APP_ARCHIVIO.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_MOV_APP_ARCHIVIO
BEFORE UPDATE 
ON AM_MOV_APP_ARCHIVIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numKloMovApp := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloMovApp, :new.numKloMovApp); 
	end; 

/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AM_MOV_APP_RICICLAGGIO.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_MOV_APP_RICICLAGGIO
BEFORE UPDATE 
ON AM_MOV_APP_RICICLAGGIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numKloMovApp := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloMovApp, :new.numKloMovApp); 
	end; 

/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_movimento_appoggio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_MOVIMENTO_APPOGGIO
 before update on AM_MOVIMENTO_APPOGGIO for each row
begin
 :new.numKloMovApp := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloMovApp, :new.numKloMovApp);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_movimento_apprendist.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_MOVIMENTO_APPRENDIST
BEFORE UPDATE
ON AM_MOVIMENTO_APPRENDIST
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 :new.numKloMovApprendist := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloMovApprendist, :new.numKloMovApprendist);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_movimento_missione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_MOVIMENTO_MISSIONE
BEFORE UPDATE 
ON AM_MOVIMENTO_MISSIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numKloMis := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloMis, :new.numKloMis); 
end; 
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_movimento.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_MOVIMENTO
 before update on AM_MOVIMENTO for each row
begin
 :new.numKloMov := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloMov, :new.numKloMov);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_obbligo_formativo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_OBBLIGO_FORMATIVO
	before update on AM_OBBLIGO_FORMATIVO for each row
	begin
	:new.numKloObbligoForm := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloObbligoForm, :new.numKloObbligoForm);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_patto_lavoratore.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AM_PATTO_LAVORATORE
 before update on AM_PATTO_LAVORATORE for each row
begin
 :new.numKloPattoLavoratore := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloPattoLavoratore, :new.numKloPattoLavoratore);


  if (:old.datFine != :new.datFine) or
  	 (:new.datFine IS NULL and :old.datFine IS NOT NULL) or
	 (:old.datFine IS NULL and :new.datFine IS NOT NULL)
  then
  	  PG_GESTAMM.pdApriChiudiInfColl ('AM_DOC','AM_PAT_L', :new.prgPattoLavoratore, :new.datFine);
  end if;

 end;

/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_periodo_lav.sql
************************************************************************************** */



create or replace trigger TG_BMR_AM_PERIODO_LAV
 before update 
 on AM_PERIODO_LAVORATIVO for each row
begin
	:new.NUMKLOPERIODOLAV := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOPERIODOLAV, :new.NUMKLOPERIODOLAV);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_privacy.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_PRIVACY
	before update on AM_PRIVACY for each row
	begin
	:new.numKloPrivacy := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloPrivacy, :new.numKloPrivacy);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_programma_ente.sql
************************************************************************************** */



create or replace trigger TG_BMR_AM_PROGRAMMA_ENTE
 before update 
 on AM_PROGRAMMA_ENTE for each row
begin
	:new.NUMKLOPROGRAMMA := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOPROGRAMMA, :new.NUMKLOPROGRAMMA);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_protocollo.sql
************************************************************************************** */



create or replace trigger TG_BMR_AM_PROTOCOLLO
 before update on AM_PROTOCOLLO for each row 
 begin  
 :new.numKloProtocollo := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloProtocollo, :new.numKloProtocollo); 
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_rdc.sql
************************************************************************************** */


create or replace trigger TG_BMR_AM_RDC
 before update 
 on AM_RDC for each row
begin
	:new.NUMKLORDC := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLORDC, :new.NUMKLORDC);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_reddito_attivazione.sql
************************************************************************************** */



create or replace trigger TG_BMR_AM_REDDITO_ATTIVAZIONE
 before update 
 on AM_REDDITO_ATTIVAZIONE for each row
begin
	:new.numKloRa := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloRa, :new.numKloRa);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_stato_occupaz.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_STATO_OCCUPAZ
 before update on AM_STATO_OCCUPAZ for each row
begin
 :new.numKloStatoOccupaz := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloStatoOccupaz, :new.numKloStatoOccupaz);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_ua_autorizzazione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_UA_AUTORIZZAZIONE
	before update on AM_UA_AUTORIZZAZIONE for each row
	begin
	:new.numKloAutoriz := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAutoriz, :new.numKloAutoriz);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_am_ua_patto.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AM_UA_PATTO
	before update on AM_UA_PATTO for each row
	begin
	:new.numKloPattoUnitaAziendale := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloPattoUnitaAziendale, :new.numKloPattoUnitaAziendale);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_azi_autorizzazione.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AN_AZI_AUTORIZZAZIONE
	before update on AN_AZI_AUTORIZZAZIONE for each row
	begin
	:new.numKlo := PG_SISTEMA.pdConcorrenzaUpd(:old.numKlo, :new.numKlo);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_azienda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AN_AZIENDA
 before update on AN_AZIENDA for each row
begin
 :new.numKloAzienda := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAzienda, :new.numKloAzienda);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_az_storia_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AN_AZ_STORIA_INF
	before update on AN_AZ_STORIA_INF for each row
	begin
	:new.numKloAzStoriaInf := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAzStoriaInf, :new.numKloAzStoriaInf);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_evidenza.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AN_EVIDENZA
	before update on AN_EVIDENZA for each row
begin
	:new.numKloEvidenza := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloEvidenza, :new.numKloEvidenza);
end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_lavoratore.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AN_LAVORATORE
	before update on AN_LAVORATORE for each row
	begin
	:new.numkloLavoratore := PG_SISTEMA.pdConcorrenzaUpd(:old.numkloLavoratore, :new.numkloLavoratore);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_lav_storia_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AN_LAV_STORIA_INF
 before update on AN_LAV_STORIA_INF for each row
begin
 :new.numKloLavStoriaInf := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloLavStoriaInf, :new.numKloLavStoriaInf);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_ua_storia_inf.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AN_UA_STORIA_INF
	before update on AN_UA_STORIA_INF for each row
	begin
	:new.numKloUAStoriaInf := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloUAStoriaInf, :new.numKloUAStoriaInf);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_an_unita_azienda.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_AN_UNITA_AZIENDA
BEFORE UPDATE
ON AN_UNITA_AZIENDA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
 :new.numKloUnitaAzienda := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloUnitaAzienda, :new.numKloUnitaAzienda);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AS_AVVIO.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AS_AVVIO
BEFORE UPDATE 
ON AS_AVVIO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numKloAvvio := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAvvio, :new.numKloAvvio); 
	end; 

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AS_AVV_SELEZIONE.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AS_AVV_SELEZIONE
BEFORE UPDATE 
ON AS_AVV_SELEZIONE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numKloAvvSelezione := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAvvSelezione, :new.numKloAvvSelezione); 
	end; 

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_AS_STORIA_STATO_OCC.sql
************************************************************************************** */


create or replace trigger  TG_BMR_AS_STORIA_STATO_OCC 
	before update on AS_STORIA_STATO_OCC for each row 
	begin  
	:new.numKloStoriaStatoOcc := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloStoriaStatoOcc, :new.numKloStoriaStatoOcc); 
	end; 
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_as_valore_isee.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_AS_VALORE_ISEE
BEFORE UPDATE 
ON AS_VALORE_ISEE
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin
	:new.numKloValoreIsee := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloValoreIsee, :new.numKloValoreIsee); 
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_bd_adesione.sql
************************************************************************************** */



create or replace trigger TG_BMR_BD_ADESIONE
 before update 
 on BD_ADESIONE for each row
begin
	:new.numkloadesione := PG_SISTEMA.pdConcorrenzaUpd(:old.numkloadesione, :new.numkloadesione);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_ca_presa_atto.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_CA_PRESA_ATTO
BEFORE UPDATE 
ON CA_PRESA_ATTO
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW 
begin  
	:new.numKloPresaAtto := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloPresaAtto, :new.numKloPresaAtto); 
	end; 

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_ambiente.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_AMBIENTE
	before update on DE_AMBIENTE for each row
	begin
	:new.numKloAmbiente := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloAmbiente, :new.numKloAmbiente);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_comune.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_COMUNE
 before update on DE_COMUNE for each row
begin
 :new.numKloComune := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloComune, :new.numKloComune);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_cpi.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_CPI
 before update on DE_CPI for each row
begin
 :new.numKloCpi := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloCpi, :new.numKloCpi);
 end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_impegno.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_IMPEGNO
	before update on DE_IMPEGNO for each row
	begin
	:new.numKloImpegno := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloImpegno, :new.numKloImpegno);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_mv_grado.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_MV_GRADO
	before update on DE_MV_GRADO for each row
	begin
	:new.numKloGrado := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloGrado, :new.numKloGrado);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_mv_tipo_ass.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_MV_TIPO_ASS
	before update on DE_MV_TIPO_ASS for each row
	begin
	:new.numKloTipoAss := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloTipoAss, :new.numKloTipoAss);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_normativa.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_NORMATIVA
	before update on DE_NORMATIVA for each row
	begin
	:new.numKloNormativa := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloNormativa, :new.numKloNormativa);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_orario.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_ORARIO
	before update on DE_ORARIO for each row
	begin
	:new.numKloOrario := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloOrario, :new.numKloOrario);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_provincia.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_PROVINCIA
	before update on DE_PROVINCIA for each row
	begin
	:new.numKloProvincia := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloProvincia, :new.numKloProvincia);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_servizio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_SERVIZIO
	before update on DE_SERVIZIO for each row
	begin
	:new.numKloServizio := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloServizio, :new.numKloServizio);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_settimana_tipo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_SETTIMANA_TIPO
	before update on DE_SETTIMANA_TIPO for each row
	begin
	:new.numKlo := PG_SISTEMA.pdConcorrenzaUpd(:old.numKlo, :new.numKlo);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_stato_slot.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_STATO_SLOT
	before update on DE_STATO_SLOT for each row
	begin
	:new.numKloStatoSlot := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloStatoSlot, :new.numKloStatoSlot);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_tipo_dich.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_TIPO_DICH
	before update on DE_TIPO_DICH for each row
	begin
	:new.numKloTipoDich := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloTipoDich, :new.numKloTipoDich);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_de_tipo_patto.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DE_TIPO_PATTO
	before update on DE_TIPO_PATTO for each row
	begin
	:new.numKloTipoPatto := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloTipoPatto, :new.numKloTipoPatto);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_do_incrocio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DO_INCROCIO
	before update on DO_INCROCIO for each row
	begin
	:new.numKloIncrocio := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloIncrocio, :new.numKloIncrocio);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_do_nominativo.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DO_NOMINATIVO
	before update on DO_NOMINATIVO for each row
begin
	:new.numKloNominativo := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloNominativo, :new.numKloNominativo);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_do_richiesta_az.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DO_RICHIESTA_AZ
	before update on DO_RICHIESTA_AZ for each row
	begin
	:new.numKloRichiestaAz := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloRichiestaAz, :new.numKloRichiestaAz);
	end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_do_rosa.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_DO_ROSA
	before update on DO_ROSA for each row
	begin
	:new.numKloRosa := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloRosa, :new.numKloRosa);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_nt_lav_storia_notifiche.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_NT_LAV_STORIA_NOTIFICHE
	before update on NT_LAV_STORIA_NOTIFICHE for each row
	begin
	:new.numKloLavStoriaModifiche := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloLavStoriaModifiche, :new.numKloLavStoriaModifiche);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_or_colloquio.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_OR_COLLOQUIO
	before update on OR_COLLOQUIO for each row
	begin
	:new.numKloColloquio := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloColloquio, :new.numKloColloquio);
	end;

/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_or_scheda_partecipante.sql
************************************************************************************** */



create or replace trigger TG_BMR_OR_SCHEDA_PARTECIPANTE
 before update 
 on OR_SCHEDA_PARTECIPANTE for each row
begin
	:new.NUMKLOPARTECIPANTE := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOPARTECIPANTE, :new.NUMKLOPARTECIPANTE);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_OR_VCH_ATTIVITA.sql
************************************************************************************** */


create or replace trigger TG_BMR_OR_VCH_ATTIVITA
 before update 
 on OR_VCH_ATTIVITA for each row
begin
	:new.NUMKLOATTIVITA := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOATTIVITA, :new.NUMKLOATTIVITA);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_OR_VCH_EVENTO.sql
************************************************************************************** */


create or replace trigger TG_BMR_OR_VCH_EVENTO
 before update
 on OR_VCH_EVENTO for each row
begin
	:new.NUMKLOEVENTO := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOEVENTO, :new.NUMKLOEVENTO);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_OR_VCH_MODALITA_09032016.sql
************************************************************************************** */


create or replace trigger TG_BMR_OR_VCH_MODALITA
 before update
 on OR_VCH_MODALITA for each row
begin
	:new.NUMKLOVCHMODALITA := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOVCHMODALITA, :new.NUMKLOVCHMODALITA);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_OR_VCH_Voucher.sql
************************************************************************************** */


create or replace trigger TG_BMR_OR_VCH_Voucher
 before update 
 on OR_VCH_Voucher for each row
begin
	:new.NUMKLOVOUCHER := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOVOUCHER, :new.NUMKLOVOUCHER);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_pi_dett_pt_disabile.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_BMR_PI_DETT_PT_DISABILE
  before update on PI_DETT_PT_DISABILE for each row
  begin
  :new.numKloPIDettPT := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloPIDettPT, :new.numKloPIDettPT);
  end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_PR_VALIDITA.sql
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_PR_VALIDITA
 BEFORE UPDATE ON PR_VALIDITA FOR EACH ROW
begin
 :new.numKloValidita := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloValidita, :new.numKloValidita);
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_sp_lavoratore.sql
************************************************************************************** */



create or replace trigger TG_BMR_SP_LAVORATORE
 before update 
 on SP_LAVORATORE for each row
begin
	:new.numKloSap := PG_SISTEMA.pdConcorrenzaUpd(:old.numKloSap, :new.numKloSap);
 end;
 
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_sp_notifica_pn.sql
************************************************************************************** */


create or replace trigger TG_BMR_SP_NOTIFICA_PN
	before update on SP_NOTIFICA_PN for each row
begin
	:new.NUMKLOSPNOTIFICAPN := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOSPNOTIFICAPN, :new.NUMKLOSPNOTIFICAPN);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/tg_bmr_sp_politica_nazionale.sql
************************************************************************************** */


create or replace trigger TG_BMR_SP_POLITICA_NAZIONALE
	before update on SP_POLITICA_NAZIONALE for each row
begin
	:new.NUMKLOSPPOLITICANAZ := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOSPPOLITICANAZ, :new.NUMKLOSPPOLITICANAZ);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_TS_ERR_AMB_TIPO.SQL
************************************************************************************** */



CREATE OR REPLACE TRIGGER TG_BMR_TS_ERR_AMB_TIPO
 BEFORE UPDATE ON  TS_ERR_AMB_TIPO FOR EACH ROW
begin
 :new.NUMKLOERRAMBTIPO := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOERRAMBTIPO, :new.NUMKLOERRAMBTIPO);
 end;
/





/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_VCH_BUDGET_CPI.sql
************************************************************************************** */


create or replace trigger TG_BMR_VCH_BUDGET_CPI
 before update 
 on VCH_BUDGET_CPI for each row
begin
	:new.NUMKLOBUDGET := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOBUDGET, :new.NUMKLOBUDGET);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_VCH_COSTI_RISULTATO.sql
************************************************************************************** */


create or replace trigger TG_BMR_VCH_COSTI_RISULTATO
 before update 
 on VCH_COSTI_RISULTATO for each row
begin
	:new.NUMKLOCCOSTIRIS := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOCCOSTIRIS, :new.NUMKLOCCOSTIRIS);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_VCH_DEF_DOTE.sql
************************************************************************************** */


create or replace trigger TG_BMR_VCH_DEF_DOTE
 before update 
 on VCH_DEF_DOTE for each row
begin
	:new.NUMKLODOTE := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLODOTE, :new.NUMKLODOTE);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_VCH_ENTE_ACCREDITATO.sql
************************************************************************************** */


create or replace trigger TG_BMR_VCH_ENTE_ACCREDITATO
 before update 
 on VCH_ENTE_ACCREDITATO for each row
begin
	:new.NUMKLOENTEACCREDITATO := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOENTEACCREDITATO, :new.NUMKLOENTEACCREDITATO);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_VCH_MODELLO_MODALITA_09032016.sql
************************************************************************************** */


create or replace trigger TG_BMR_VCH_MODELLO_MODALITA
 before update
 on VCH_MODELLO_MODALITA for each row
begin
	:new.NUMKLOMODMODALITA := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOMODMODALITA, :new.NUMKLOMODMODALITA);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_VCH_MODELLO_VOUCHER_09032016.sql
************************************************************************************** */


create or replace trigger TG_BMR_VCH_MODELLO_VOUCHER
 before update
 on VCH_MODELLO_VOUCHER for each row
begin
	:new.NUMKLOMODVOUCHER := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOMODVOUCHER, :new.NUMKLOMODVOUCHER);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_BMR_VCH_OPERAZIONI_BUDGET.sql
************************************************************************************** */


create or replace trigger TG_BMR_VCH_OPERAZIONI_BUDGET
 before update 
 on VCH_OPERAZIONI_BUDGET for each row
begin
	:new.NUMKLOOPERAZIONIBUDGET := PG_SISTEMA.pdConcorrenzaUpd(:old.NUMKLOOPERAZIONIBUDGET, :new.NUMKLOOPERAZIONIBUDGET);
 end;
/



/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_MD_BIR_AM_PATTO_LAVORATORE.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_MD_BIR_AM_PATTO_LAVORATORE
BEFORE INSERT
ON  AM_LAV_PATTO_SCELTA
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
begin
  PG_GESTAMM.tracciaModifichePatto(:new.codLstTab, :new.prgPattoLavoratore, :new.strChiaveTabella, :new.strChiaveTabella2,:new.strChiaveTabella3);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_MD_BMR_AG_AGENDA.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_MD_BMR_AG_AGENDA
	before update on AG_AGENDA for each row
begin
	PG_GESTAMM.tracciaModAgendaAssPattoLav(:new.CodServizio ,
											:new.DtmDataOra ,
											:new.CodCpi ,
											:new.PrgAppuntamento,
											:old.CODSERVIZIO,
											:old.DTMDATAORA ,
											:old.CODCPI ,
											:old.PRGAPPUNTAMENTO,
											:old.CODESITOAPPUNT ,
											:old.NUMKLOAGENDA ,
											:old.PRGAZIENDA ,
											:old.PRGUNITA ,
											:old.NUMMINUTI,
											:old.PRGSPI ,
											:old.TXTNOTE,
											:old.PRGTIPOPRENOTAZIONE ,
											:old.STRTELRIF,
											:old.STREMAILRIF,
											:old.STRTELMOBILERIF ,
											:old.CODEFFETTOAPPUNT,
											:old.CODSTATOAPPUNTAMENTO,
											:old.PRGAMBIENTE,
											:old.PRGSPIEFF ,
											:old.NUMORAFINEEFF ,
											:old.NUMORAINIZIOEFF,
											:new.cdnUtMod ,
											:new.dtmMod
											);
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_MD_BMR_OR_PERCORSO_CONC.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_MD_BMR_OR_PERCORSO_CONC
before update on OR_PERCORSO_CONCORDATO for each row
begin
	PG_GESTAMM.tracciaModAzioniAssPattoLav(:new.PrgColloquio,
											:new.PrgPercorso,
											:new.DatStimata,
											:new.PrgAzioni,
											:old.PrgColloquio,
											:old.PrgPercorso,
											:old.DatStimata,
											:old.DatEffettiva,
											:old.PrgAzioni,
											:old.CodEsito ,
											:old.StrNote,
											:old.StrNotePropostaOp ,
											:new.cdnUtMod ,
											:new.dtmMod );
end;
/




/* *************************************************************************************
**** CONTENUTO DEL FILE ./Database/db_src/Triggers/TG_MD_BMR_PR_MANSIONE.sql
************************************************************************************** */


CREATE OR REPLACE TRIGGER TG_MD_BMR_PR_MANSIONE
	before update on PR_MANSIONE for each row
begin
	PG_GESTAMM.tracciaModMansioniAssPattoLav(:new.CdnLavoratore,
											:new.FlgDisponibile ,
											:new.FlgDispFormazione,
											:new.PrgMansione,
											:old.PrgMansione ,
											:old.CodMansione ,
											:old.FlgEsperienza,
											:old.FlgEspForm ,
											:old.FlgDisponibile,
											:old.FlgDispFormazione,
											:old.FlgPip ,
											:old.CodMonoTempo,
											:new.cdnUtMod,
											:new.dtmMod );
end;
/


