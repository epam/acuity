SET check_function_bodies = false;

CREATE FUNCTION calc_recist_week(p_first_dose timestamp without time zone, p_assessment timestamp without time zone, p_freq bigint) RETURNS bigint
    LANGUAGE plpgsql STABLE
    AS $$
DECLARE

  days bigint;
  x bigint := 1;
  x_min bigint := 1;
  x_max bigint := null;
  half bigint := null;

BEGIN
  if p_first_dose is null or p_assessment is null or p_freq is null
  then return null;
  end if;

  if p_first_dose > p_assessment then return null;
  end if;

  days := trunc(p_assessment) - trunc(p_first_dose);
  half := 1/2 * (7 * p_freq);
  x_max := 7 * p_freq + half;
  --dbms_output.put_line('First: x '||x||' freq '|| freq ||' min '||x_min||'  max '||x_max||' days '|| days|| ' half '||half);
  while days > x_max
  loop
    x := x + 1;
    x_min := x_max + 1;
    x_max := 7 * p_freq * x + half;
    --dbms_output.put_line('Next '||x||' freq '|| freq ||' min '||x_min||'  max '||x_max||' days '|| days|| ' half '||half);
  end loop;

  --dbms_output.put_line('result: Week'|| x*freq );
  return x * p_freq;
exception
  when others then
  --dbms_output.put_line(sqlerrm);
  --raise;
  return null;
end;
$$;

ALTER FUNCTION calc_recist_week(p_first_dose timestamp without time zone, p_assessment timestamp without time zone, p_freq bigint) OWNER TO acuity;

CREATE FUNCTION normalize_time(time_string text) RETURNS character varying
    LANGUAGE plpgsql STABLE
    AS $_$
DECLARE
 normalized varchar(50);

BEGIN
if time_string ~ '^\s*(\d|0\d|1\d|2[0-3]):[0-5]\d:[0-5]\d\s*$' then
  normalized := trim(both time_string);
elsif time_string ~ '^\s*(\d|0\d|1\d|2[0-3]):[0-5]\d\s*$' then
  normalized := trim(both time_string)  || ':00';
else
  return NULL;
end if;
if normalized ~ '^\d:.*$' then
  normalized := '0' || normalized;
end if;
return normalized;
end;
$_$;

ALTER FUNCTION normalize_time(time_string text) OWNER TO acuity;

CREATE FUNCTION nvl2(var1 bigint, var2 integer, var3 integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
begin
return case when var1 is not null then var2 else var3 end;
end;
$$;

ALTER FUNCTION nvl2(var1 bigint, var2 integer, var3 integer) OWNER TO acuity;

CREATE FUNCTION nvl2(var1 numeric, var2 integer, var3 integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
begin
return case when var1 is not null then var2 else var3 end;
end;
$$;

ALTER FUNCTION nvl2(var1 numeric, var2 integer, var3 integer) OWNER TO acuity;

CREATE FUNCTION nvl2(var1 timestamp without time zone, var2 character varying, var3 character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$

begin

return case when var1 is not null then var2 else var3 end;

end;

$$;

ALTER FUNCTION nvl2(var1 timestamp without time zone, var2 character varying, var3 character varying) OWNER TO acuity;

CREATE FUNCTION trigger_fct_a_d_study() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	delete FROM RESULT_PROJECT
	where prj_id in (SELECT prj_id
					 from RESULT_PROJECT left outer join RESULT_STUDY on RESULT_STUDY.std_prj_id=RESULT_PROJECT.prj_id
					 group by RESULT_PROJECT.prj_id
					 having count(std_id)=0);
RETURN OLD;
END
$$;

CREATE FUNCTION trigger_fct_acl_class_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    SELECT nextval('acl_class_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_acl_entry_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    SELECT nextval('acl_entry_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_acl_object_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    SELECT nextval('acl_object_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_acl_object_identity_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    SELECT nextval('acl_object_identity_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_acl_remote_identity_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    SELECT nextval('acl_remote_identity_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_acl_sid_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    SELECT nextval('acl_sid_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_group_members_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
SELECT nextval('group_members_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_groups_id_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
SELECT nextval('groups_sequence') INTO STRICT NEW.id;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_map_dynamic_field_seq_nextval() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
SELECT nextval('map_dynamic_field_seq')
INTO STRICT NEW.mdfi_id
;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_map_project_display() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
 if NEW.MPR_DRUG_DISPLAY_NAME is null then
  NEW.MPR_DRUG_DISPLAY_NAME := NEW.MPR_DRUG;
  end if;
RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_rename_custom_subject_grouping() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  BEGIN
    UPDATE map_subject_grouping
    SET msg_grouping_name = NEW.msgr_name
    WHERE msg_study_id = OLD.msgr_study_id AND msg_grouping_name = OLD.msgr_name;
  RETURN NEW;
END
$$;

CREATE FUNCTION trigger_fct_ua_tr() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  SELECT nextval('ua_seq')
  INTO STRICT   NEW.ua_id
;
RETURN NEW;
END
$$;

CREATE FUNCTION acuity_utils.clean_all_reports(depth_num bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE

    c1 CURSOR(DEPTH  bigint)
    FOR
      SELECT t3.execution_id
      FROM (SELECT
              batch_job_execution.job_execution_id AS execution_id,
              RANK() OVER (PARTITION BY project, study ORDER BY batch_job_execution.create_time DESC) AS repeat_number
            FROM batch_job_execution
              LEFT OUTER JOIN(SELECT batch_job_instance.job_instance_id AS inst_id, study, project
               FROM batch_job_instance
                 LEFT OUTER JOIN(SELECT
                    job_instance_id AS params_inst_id,
                    MAX(CASE WHEN key_name='etl.study' THEN  string_val END ) AS study,
                    MAX(CASE WHEN key_name='etl.project' THEN  string_val END ) AS project
                  FROM batch_job_params
                  GROUP BY job_instance_id
                 ) t1
                   ON batch_job_instance.job_instance_id = t1.params_inst_id
              ) t2
                ON job_instance_id = t2.inst_id
            WHERE batch_job_execution.status='COMPLETED'
           ) t3
      WHERE t3.repeat_number > DEPTH;

    execution_id batch_job_execution.job_execution_id%TYPE;

BEGIN
      OPEN c1(depth_num);
      LOOP
        FETCH c1 INTO execution_id;
        EXIT WHEN NOT FOUND; /* apply on c1 */
        DELETE FROM report_unique_violation WHERE rcv_je_id = execution_id;
        DELETE FROM report_fk_violation WHERE rfv_je_id = execution_id;
        DELETE FROM report_missed_cntlin WHERE rmc_je_id = execution_id;
        DELETE FROM report_unparsed_data WHERE rud_je_id = execution_id;
      END LOOP;
      CLOSE c1;
      COMMIT;
    END;

$$;

ALTER FUNCTION acuity_utils.clean_all_reports(depth_num bigint) OWNER TO acuity;

CREATE FUNCTION acuity_utils.clean_reports(depth_num bigint, project_name text, study_code text) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE

    c1 CURSOR(DEPTH  bigint, project_name  text, study_code  text)
    FOR
      SELECT t3.execution_id
      FROM (SELECT
             batch_job_execution.job_execution_id AS execution_id,
             RANK() OVER (PARTITION BY project, study ORDER BY batch_job_execution.create_time DESC) AS repeat_number
           FROM batch_job_execution
             LEFT OUTER JOIN(SELECT batch_job_instance.job_instance_id AS inst_id, study, project
              FROM batch_job_instance
                LEFT OUTER JOIN(SELECT
                   job_instance_id AS params_inst_id,
                   MAX(CASE WHEN key_name='etl.study' THEN  string_val END ) AS study,
                   MAX(CASE WHEN key_name='etl.project' THEN  string_val END ) AS project
                 FROM batch_job_params GROUP BY job_instance_id) t1
                  ON batch_job_instance.job_instance_id = t1.params_inst_id
             ) t2
               ON job_instance_id = t2.inst_id
           WHERE batch_job_execution.status = 'COMPLETED'
                 AND study = study_code
                 AND project = project_name
          ) t3
      WHERE t3.repeat_number > DEPTH;

    execution_id batch_job_execution.job_execution_id%TYPE;

BEGIN
      OPEN c1(depth_num, project_name, study_code);
      LOOP
        FETCH c1 INTO execution_id;
        EXIT WHEN NOT FOUND; /* apply on c1 */
        DELETE FROM report_unique_violation WHERE rcv_je_id=execution_id;
        DELETE FROM report_fk_violation WHERE rfv_je_id=execution_id;
        DELETE FROM report_missed_cntlin WHERE rmc_je_id=execution_id;
        DELETE FROM report_unparsed_data WHERE rud_je_id=execution_id;
      END LOOP;
      CLOSE c1;
    END;

$$;

ALTER FUNCTION acuity_utils.clean_reports(depth_num bigint, project_name text, study_code text) OWNER TO acuity;

CREATE FUNCTION acuity_utils.d_h_since_first_dose(p_time_of_measurement timestamp without time zone, p_first_dose_time timestamp without time zone) RETURNS character varying
    LANGUAGE plpgsql STABLE
    AS $$
BEGIN
      if p_time_of_measurement is null or p_first_dose_time is null then
        return null;
      else
        return	(date_trunc('day', p_time_of_measurement) - date_trunc('day', p_first_dose_time))::varchar
                ||'.'|| ((extract(HOUR from to_timestamp(p_time_of_measurement)))::numeric
                                + floor((extract(MINUTE from to_timestamp(p_time_of_measurement)))::numeric /30)
                )::varchar;
      end if;
      EXCEPTION
      when others then
      return null;
    END;

$$;

ALTER FUNCTION acuity_utils.d_h_since_first_dose(p_time_of_measurement timestamp without time zone, p_first_dose_time timestamp without time zone) OWNER TO acuity;

CREATE FUNCTION acuity_utils.is_number(p_string text) RETURNS numeric
    LANGUAGE plpgsql STABLE
    AS $$
DECLARE

    v_new_num bigint;

BEGIN
      v_new_num := (p_string)::numeric;
      RETURN 1;
      EXCEPTION
      WHEN data_exception THEN
      RETURN 0;
    END;

$$;

ALTER FUNCTION acuity_utils.is_number(p_string text) OWNER TO acuity;

CREATE FUNCTION acuity_utils.letter_azz(num bigint) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE

    num0 bigint;

BEGIN
      num0 := num - 1;
      IF num0 < 26
      THEN
        RETURN chr(ascii('A') + num0);
      ELSIF num0 < 702
        THEN
          RETURN chr(ascii('A') + FLOOR(num0 / 26) - 1) || chr(ascii('A') + MOD(num0, 26));
      ELSE
        RETURN '';
      END IF;
    END;

$$;

ALTER FUNCTION acuity_utils.letter_azz(num bigint) OWNER TO acuity;

CREATE FUNCTION acuity_utils.update_fk_to_lgr(dateupdated timestamp without time zone, defaultfk text, project_name text, study_code text) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE

    studyGUID   RESULT_STUDY.std_id%TYPE;

BEGIN
      SELECT   std_id
      INTO STRICT   studyGUID
      FROM   RESULT_STUDY, RESULT_PROJECT
      WHERE       RESULT_STUDY.std_prj_id = RESULT_PROJECT.prj_id
                  AND RESULT_PROJECT.prj_name = project_name
                  AND RESULT_STUDY.std_name = study_code;

      UPDATE   RESULT_LABORATORY a
      SET   lab_lgr_id =
      (SELECT   coalesce(
          (SELECT   lgr_id
           FROM   RESULT_LAB_GROUP b
           WHERE       b.lgr_std_id = studyGUID
                       AND b.lgr_lab_code = a.lab_code
                       AND lgr_date_updated>=dateUpdated),
          (SELECT   lgr_id
           FROM   RESULT_LAB_GROUP
           WHERE   lgr_std_id = studyGUID
                   AND RESULT_LAB_GROUP.lgr_lab_code = defaultfk)
      )
       )
      WHERE   lab_tst_id IN (SELECT   tst_id
               FROM   RESULT_TEST, RESULT_PATIENT
               WHERE   RESULT_TEST.tst_pat_id = RESULT_PATIENT.pat_id
                       AND RESULT_PATIENT.pat_std_id = studyGUID
                       AND tst_date_updated>=dateUpdated
                       AND pat_date_updated>=dateUpdated
              )
              AND lab_lgr_id IN (SELECT   lgr_id
                   FROM   RESULT_LAB_GROUP
                   WHERE   lgr_date_updated>=dateUpdated AND lgr_std_id = studyGUID);

      COMMIT;
    END;

$$;

ALTER FUNCTION acuity_utils.update_fk_to_lgr(dateupdated timestamp without time zone, defaultfk text, project_name text, study_code text) OWNER TO acuity;

SET check_function_bodies = true;
