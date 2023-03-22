package org.digitalecmt.etarget.dao;

/*-
 * #%L
 * eTarget Maven Webapp
 * %%
 * Copyright (C) 2017 - 2021 digital ECMT
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.digitalecmt.etarget.config.ConfigDataSources;
import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.GeneSubsetExt;
import org.digitalecmt.etarget.dbentities.MeetingOutcome;
import org.digitalecmt.etarget.dbentities.MeetingOutcomeGeneVariant;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MeetingOutcomeDAOImpl  extends JdbcDaoSupport implements MeetingOutcomeDAO {
	
	private static final Logger log = Logger.getLogger(MeetingOutcomeDAOImpl.class.getName());
	private List<ConfigDataSources> addSources;
	private String select="SELECT * FROM MEETING_OUTCOME where person_id = ? ORDER BY mo_id DESC";
	private String selectMeeting ="SELECT * from MEETING_OUTCOME where mo_id = ?";
	private String updatenote= "UPDATE MEETING_OUTCOME set notes = ?, updated_by = ?, last_updated = ? where mo_id=?";
	private String updateoutcome="UPDATE MEETING_OUTCOME set outcome = ?, updated_by = ?, last_updated = ? where mo_id=?";
	private String addMeeting = "INSERT INTO MEETING_OUTCOME (updated_by, person_id, outcome, notes, meeting_date, gene_tables) VALUES(?,?,?,?,?,?);";
	private String deleteMeeting = "DELETE FROM MEETING_OUTCOME where mo_id=?;";
	private String printMeeting = "UPDATE MEETING_OUTCOME set last_printed=? where mo_id=?";
	private String selectForPrinting = "select distinct person_id from MEETING_OUTCOME where last_printed<last_updated or last_printed is null";
	private String selectForPrintingPerson = "select mo_id from MEETING_OUTCOME where person_id= ? and (last_printed<last_updated or last_printed is null)";
	private String selectLastMeetingOutcome = "select top 1 created_on from MEETING_OUTCOME where person_id=? order by mo_id desc;";
	private String selectBloodDateWithIngestion ="SELECT top 1  SPECIMEN.specimen_date from SPECIMEN \n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL as mgp on SPECIMEN.specimen_id=mgp.specimen_id\n" + 
			"where specimen.specimen_concept_id=1 and person_id=? and mgp.ingestion_date<? order by specimen_date desc";
	private String selectTumourDateWithIngestion ="SELECT top 1 SPECIMEN.specimen_date from SPECIMEN \n" + 
			"LEFT JOIN MEASUREMENT_GENE_PANEL as mgp on SPECIMEN.specimen_id=mgp.specimen_id\n" + 
			"where (specimen.specimen_concept_id=2 or specimen.specimen_concept_id=3) and person_id=? and mgp.ingestion_date<? order by specimen_date desc";
	private String selectBloodDate="SELECT top 1 SPECIMEN.specimen_date from SPECIMEN \n" + 
			"where specimen.specimen_concept_id=1 and person_id=? and specimen_date<? order by specimen_date desc";
	private String selectTumourDate="SELECT top 1 SPECIMEN.specimen_date from SPECIMEN \n" + 
			"where (specimen.specimen_concept_id=2 or specimen.specimen_concept_id=3) and person_id=? and specimen_date<? order by specimen_date desc";
	
	
	
	public MeetingOutcomeDAOImpl(DataSource datasource) {
		setDataSource(datasource);
	}
	@Override
	public List<MeetingOutcome> getMeetingOutcomesByPersonID(int personID) throws DataAccessException {
		List<MeetingOutcome> values = null;
		JdbcTemplate template = getJdbcTemplate();
		values= template.query(select, new Object[] {personID}, 
				new BeanPropertyRowMapper<MeetingOutcome>(MeetingOutcome.class));
		MeetingOutcomeGeneVariantDAO genes= new MeetingOutcomeGeneVariantDAOImpl(this.getDataSource());
		GeneSubsetDAO ctDNA= new GeneSubsetDAOImpl(this.getDataSource());
		TumourNgsDAO tumourNGS = new TumourNgsDAOImpl(this.getDataSource());
		FoundationMedicineDAO fmDao = new FoundationMedicineDAOImpl(this.getDataSource());
		GenomicsDataDAO genomicsData = GenomicsDataDAOImpl.getInstance(this.getDataSource());
		for(MeetingOutcome outcome : values) {
			List<MeetingOutcomeGeneVariant> genevariants=genes.getGeneVariantsForMeetingOutcome(outcome.getMo_id());
			for(MeetingOutcomeGeneVariant gene : genevariants) {
				if(gene.getType().equals("CTDNA")) {
					outcome.addCtDNA(GeneSubsetExt.getGeneSubsetExt(
							ctDNA.findGeneSubsetByGeneVarientID(personID, gene.getMeasurement_gene_variant_id())));
				} else if(gene.getType().equals("NGS")) {
					outcome.addTumourNGS(TumourNgsExt.getTumourNgsExt(
							tumourNGS.findTumourNgsByGeneVarientID(personID, gene.getMeasurement_gene_variant_id())));
				} else if(gene.getType().equals("FMBloodCNA")) {
					outcome.addFMBloodCNA(fmDao.getCopyNumberAlterationByGeneVariantID(gene.getMeasurement_gene_variant_id()));
				} else if(gene.getType().equals("FMBloodSV")) {
					outcome.addFMBloodSV(fmDao.getShortVariantByGeneVariantID(gene.getMeasurement_gene_variant_id()));
				} else if(gene.getType().equals("FMBloodR")) {
					outcome.addFMBloodR(fmDao.getRearrangementByGeneVariantID(gene.getMeasurement_gene_variant_id()));
				} else if(gene.getType().equals("FMTumourCNA")) {
					CopyNumberAlteration cna = fmDao.getCopyNumberAlterationByGeneVariantID(gene.getMeasurement_gene_variant_id());
//					cna.setBaseline(fmDao.getTumourBaseline(cna.getMeasurement_gene_panel_id()));
					outcome.addFMTumourCNA(cna);
				} else if(gene.getType().equals("FMTumourSV")) {
					ShortVariant sv = fmDao.getShortVariantByGeneVariantID(gene.getMeasurement_gene_variant_id());
//					sv.setBaseline(fmDao.getTumourBaseline(sv.getMeasurement_gene_panel_id()));
					outcome.addFMTumourSV(sv);
				} else if(gene.getType().equals("FMTumourR")) {
					Rearrangement r = fmDao.getRearrangementByGeneVariantID(gene.getMeasurement_gene_variant_id());
//					r.setBaseline(fmDao.getTumourBaseline(r.getMeasurement_gene_panel_id()));
					outcome.addFMTumourR(r);
				} else if(gene.getType().endsWith("R")) {
					Rearrangement r = fmDao.getRearrangementByGeneVariantID(gene.getMeasurement_gene_variant_id());
//					if(gene.getType().toLowerCase().endsWith("tumourr")) {
//						r.setBaseline(fmDao.getTumourBaseline(r.getMeasurement_gene_panel_id()));
//					}
					outcome.addR(gene.getType(),r);
				} else if(gene.getType().endsWith("SV")) {
					ShortVariant sv = fmDao.getShortVariantByGeneVariantID(gene.getMeasurement_gene_variant_id());
//					if(gene.getType().toLowerCase().endsWith("tumoursv")) {
//						sv.setBaseline(fmDao.getTumourBaseline(sv.getMeasurement_gene_panel_id()));
//					}
					outcome.addSV(gene.getType(),sv);
				} else if(gene.getType().endsWith("CNA")) {
					CopyNumberAlteration cna = fmDao.getCopyNumberAlterationByGeneVariantID(gene.getMeasurement_gene_variant_id());
//					if(gene.getType().toLowerCase().endsWith("tumourcna")) {
//						cna.setBaseline(fmDao.getTumourBaseline(cna.getMeasurement_gene_panel_id()));
//					}
					outcome.addCNA(gene.getType(),cna);
				}
			}
		}
		return values;
	}

	@Override
	public boolean addMeetingOutcome(String loggedInUserID, String dbPersonID, String postedMeetingDate,
			String postedOutcome, String postedNotes, String ctDNA, String tumourNGS, String fmBlood, String fmTumour, String genericGenomic) {
		MeetingOutcomeGeneVariantDAO genes= new MeetingOutcomeGeneVariantDAOImpl(this.getDataSource());
		JdbcTemplate template = getJdbcTemplate();
		int resultcount=0;
		resultcount=template.update(addMeeting, loggedInUserID, dbPersonID, postedOutcome, postedNotes, postedMeetingDate, "");
		if(resultcount==1) {
			List<MeetingOutcome> meetings= getMeetingOutcomesByPersonID(Integer.parseInt(dbPersonID));
			//highest (newest) mo_id first
			int mo_id= meetings.get(0).getMo_id();
			//parse string to json
			
			log.info(ctDNA);
			log.info(tumourNGS);
			log.info(fmBlood);
			log.info(fmTumour);
			List<Double> ctDNAList = new Gson().fromJson(ctDNA, new TypeToken<List<Double>>() {}.getType());
			List<Double> tumourNGSList = new Gson().fromJson(tumourNGS, new TypeToken<List<Double>>() {}.getType());
			Map<String,List<String>> fmBloodMap = new Gson().fromJson(fmBlood, new TypeToken<Map<String,List<String>>>() {}.getType());
			Map<String,List<String>> fmTumourMap = new Gson().fromJson(fmTumour, new TypeToken<Map<String,List<String>>>() {}.getType());
			Map<String, Map<String,List<String>>> genericGen = new Gson().fromJson(genericGenomic, new TypeToken<Map<String, Map<String,List<String>>>>() {}.getType());
			
			for(int i=0;i<ctDNAList.size();i++) {
				log.info("type " +ctDNAList.get(i).intValue());
				genes.addGeneVariantToMeetingOutcome(mo_id, ctDNAList.get(i).intValue(), "CTDNA");
			}
			for(int i=0;i<tumourNGSList.size();i++) {
				genes.addGeneVariantToMeetingOutcome(mo_id, tumourNGSList.get(i).intValue(), "NGS");
			}
			List<String> cnal = fmBloodMap.get("copyNumberAlteration");
			if(cnal!=null) {
			for(int i=0; i<cnal.size();i++) {
				genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(cnal.get(i)), "FMBloodCNA");
			}}
			List<String> rl = fmBloodMap.get("rearrangement");
			if(rl!=null) {
			for(int i=0; i<rl.size();i++) {
				genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(rl.get(i)), "FMBloodR");
			}}
			List<String> svl = fmBloodMap.get("shortVariant");
			if(svl!=null) {
			for(int i=0; i<svl.size();i++) {
				genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(svl.get(i)), "FMBloodSV");
			}}
			cnal = fmTumourMap.get("copyNumberAlteration");
			if(cnal!=null) {
			for(int i=0; i<cnal.size();i++) {
				genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(cnal.get(i)), "FMTumourCNA");
			}} 
			rl = fmTumourMap.get("rearrangement");
			if(rl!=null) {
			for(int i=0; i<rl.size();i++) {
				genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(rl.get(i)), "FMTumourR");
			}}
			svl = fmTumourMap.get("shortVariant");
			if(svl!=null){
			for(int i=0; i<svl.size();i++) {
				genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(svl.get(i)), "FMTumourSV");
			}}
			for(String source : genericGen.keySet()) {
				cnal = genericGen.get(source).get("copyNumberAlteration");
				if(cnal!=null) {
					for(int i=0; i<cnal.size();i++) {
						genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(cnal.get(i)), source+"CNA");
					}
				}
				rl = genericGen.get(source).get("rearrangement");
				if(rl!=null) {
				for(int i=0; i<rl.size();i++) {
					genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(rl.get(i)), source+"R");
				}}
				svl = genericGen.get(source).get("shortVariant");
				if(svl!=null){
				for(int i=0; i<svl.size();i++) {
					genes.addGeneVariantToMeetingOutcome(mo_id, Integer.parseInt(svl.get(i)), source+"SV");
				}}
			}
			
			return true;
		}
		else return false;
	}

	@Override
	public boolean editMeetingOutcome(String loggedInuserId, String meetingID, String type, String value)  throws DataAccessException {
		JdbcTemplate template = getJdbcTemplate();
		int resultcount=0;
		if(type.compareTo("notes")==0) {
			Timestamp timestamp = new Timestamp(new Date().getTime());
			resultcount=template.update(updatenote, new Object[] {value, loggedInuserId, timestamp, meetingID});
		} else if (type.compareTo("outcome")==0) {
			Timestamp timestamp = new Timestamp(new Date().getTime());
			resultcount=template.update(updateoutcome, new Object[] {value, loggedInuserId, timestamp, meetingID});
		}
		if(resultcount==1) return true;
		else return false;

	}
	@Override
	public MeetingOutcome getMeetingOutcomeByMeetingID(int meetingOutcomeID) throws DataAccessException{
		MeetingOutcome values = null;
		JdbcTemplate template = getJdbcTemplate();
		values= (MeetingOutcome) template.queryForObject(selectMeeting, new Object[] {meetingOutcomeID}, 
				new BeanPropertyRowMapper<MeetingOutcome>(MeetingOutcome.class));
		MeetingOutcomeGeneVariantDAO genes= new MeetingOutcomeGeneVariantDAOImpl(this.getDataSource());
		GeneSubsetDAO ctDNA= new GeneSubsetDAOImpl(this.getDataSource());
		TumourNgsDAO tumourNGS = new TumourNgsDAOImpl(this.getDataSource());
		FoundationMedicineDAO fmDao = new FoundationMedicineDAOImpl(this.getDataSource());
		List<MeetingOutcomeGeneVariant> genevariants=genes.getGeneVariantsForMeetingOutcome(meetingOutcomeID);
		for(MeetingOutcomeGeneVariant gene : genevariants) {
			if(gene.getType().equals("CTDNA")) {
				values.addCtDNA(GeneSubsetExt.getGeneSubsetExt(
						ctDNA.findGeneSubsetByGeneVarientID(values.getPerson_id(), gene.getMeasurement_gene_variant_id())));
			} else if(gene.getType().equals("NGS")) {
				values.addTumourNGS(TumourNgsExt.getTumourNgsExt(
						tumourNGS.findTumourNgsByGeneVarientID(values.getPerson_id(), gene.getMeasurement_gene_variant_id())));
			} else if(gene.getType().equals("FMBloodCNA")) {
				values.addFMBloodCNA(fmDao.getCopyNumberAlterationByGeneVariantID(gene.getMeasurement_gene_variant_id()));
			} else if(gene.getType().equals("FMBloodSV")) {
				values.addFMBloodSV(fmDao.getShortVariantByGeneVariantID(gene.getMeasurement_gene_variant_id()));
			} else if(gene.getType().equals("FMBloodR")) {
				values.addFMBloodR(fmDao.getRearrangementByGeneVariantID(gene.getMeasurement_gene_variant_id()));
			} else if(gene.getType().equals("FMTumourCNA")) {
				CopyNumberAlteration cna = fmDao.getCopyNumberAlterationByGeneVariantID(gene.getMeasurement_gene_variant_id());
//				cna.setBaseline(fmDao.getTumourBaseline(cna.getMeasurement_gene_panel_id()));
				values.addFMTumourCNA(cna);
			} else if(gene.getType().equals("FMTumourSV")) {
				ShortVariant sv = fmDao.getShortVariantByGeneVariantID(gene.getMeasurement_gene_variant_id());
//				sv.setBaseline(fmDao.getTumourBaseline(sv.getMeasurement_gene_panel_id()));
				values.addFMTumourSV(sv);
			} else if(gene.getType().equals("FMTumourR")) {
				Rearrangement r = fmDao.getRearrangementByGeneVariantID(gene.getMeasurement_gene_variant_id());
//				r.setBaseline(fmDao.getTumourBaseline(r.getMeasurement_gene_panel_id()));
				values.addFMTumourR(r);
			}
		}
		return values;
	}
	@Override
	public boolean deleteMeetingOutcome(int mo_id) {
		JdbcTemplate template = getJdbcTemplate();
		MeetingOutcomeGeneVariantDAO genes= new MeetingOutcomeGeneVariantDAOImpl(this.getDataSource());
		//delete gene variants
		List<MeetingOutcomeGeneVariant> mogv=genes.getGeneVariantsForMeetingOutcome(mo_id);
		for(MeetingOutcomeGeneVariant geneVariante : mogv) {
			genes.removeGeneVariantFromMeetingOutcome(geneVariante.getMo_id(), geneVariante.getMeasurement_gene_variant_id(), geneVariante.getType());
		}
		int resultcount=0;
		resultcount=template.update(deleteMeeting, mo_id);
		if(resultcount==1) return true;
		else return false;
	}
	@Override
	public boolean printMeetingOutcome(int mo_id) {
		JdbcTemplate template = getJdbcTemplate();
		Timestamp timestamp = new Timestamp(new Date().getTime());
		int resultcount=0;
		resultcount=template.update(printMeeting, timestamp, mo_id);
		if(resultcount==1) return true;
		else return false;
	}
	@Override
	public List<Integer> getPersonIdsForPrinting() throws DataAccessException {
		List<Integer> values = null;
		JdbcTemplate template = getJdbcTemplate();
		values= template.queryForList(selectForPrinting, Integer.class);
		return values;
	}
	@Override
	public List<Integer> getMeetingOutcomesForPrinting(int personID) throws DataAccessException {
		List<Integer> values = null;
		JdbcTemplate template = getJdbcTemplate();
		values= template.queryForList(selectForPrintingPerson, new Object[] {personID}, Integer.class);
		return values;
	}
	@Override
	public Calendar getLastMeetingOutcomeDate(int personID) throws DataAccessException {
		java.sql.Timestamp meetingdate=null;
		JdbcTemplate template = getJdbcTemplate();
		try {
			meetingdate= template.queryForObject(selectLastMeetingOutcome, new Object[] {personID}, java.sql.Timestamp.class);
		} catch (IncorrectResultSizeDataAccessException err) {
			return null;
		}
		if(meetingdate!=null) {
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(meetingdate);
			return cal;
		}
		return null;
	}
	@Override
	public Calendar getSampleDateBlood(java.sql.Date meeting_date, Integer personID) {
		java.sql.Date specimendate = null;
		JdbcTemplate template = getJdbcTemplate();
		try {
			specimendate = template.queryForObject(selectBloodDateWithIngestion, new Object[] {personID, meeting_date}, java.sql.Date.class);
		} catch (IncorrectResultSizeDataAccessException err) {
			try {
				specimendate = template.queryForObject(selectBloodDate, new Object[] {personID, meeting_date}, java.sql.Date.class);
			}catch (IncorrectResultSizeDataAccessException err2) {
				return null;
			}
		}
		if(specimendate!=null) {
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(specimendate);
			return cal;
		}
		return null;
	}
	@Override
	public Calendar getSampleDateTumour(java.sql.Date meeting_date, Integer personID) {
		java.sql.Date specimendate = null;
		JdbcTemplate template = getJdbcTemplate();
		try {
			specimendate = template.queryForObject(selectTumourDateWithIngestion, new Object[] {personID, meeting_date}, java.sql.Date.class);
		} catch (IncorrectResultSizeDataAccessException err) {
			try {
				specimendate = template.queryForObject(selectTumourDate, new Object[] {personID, meeting_date}, java.sql.Date.class);
			}catch (IncorrectResultSizeDataAccessException err2) {
				return null;
			}
		}
		if(specimendate!=null) {
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(specimendate);
			return cal;
		}
		return null;
	}
	@Override
	public void setAdditionalSources(List<ConfigDataSources> addSources) {
		this.addSources=addSources;
	}

}
