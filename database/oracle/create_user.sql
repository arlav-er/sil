CREATE USER "SIL" PROFILE "DEFAULT" IDENTIFIED BY "SIL_PASSWORD" DEFAULT TABLESPACE "TBS_SIL" TEMPORARY TABLESPACE "TEMP" QUOTA UNLIMITED ON "TBS_SIL" ACCOUNT UNLOCK;
GRANT "RUOLO_SIL" TO "SIL";