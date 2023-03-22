package org.digitalecmt.etarget.dbentities;

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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digitalecmt.etarget.config.ConfigDataSources;
import org.digitalecmt.etarget.support.Formater;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MeetingOutcome {
	private Integer mo_id;
	private Integer person_id;
	private Date meeting_date;
	private String notes;
	private String updated_by;
	private Timestamp created_on;
	private String outcome;
	private String gene_tables;
	private Timestamp last_updated;
	private Timestamp last_printed;
	private List<Map<String,Object>> ctDNA;
	private List<Map<String,Object>> tumourNGS;
	private Map<String,List<Map<String,String>>> fmBlood;
	private Map<String,List<Map<String,String>>> fmTumour;
	private Map<String,Map<String,List<Map<String,String>>>> genericGenomic;
	private List<Map<String,String>> summary;
		
	public Integer getMo_id() {
		return mo_id;
	}
	public void setMo_id(Integer mo_id) {
		this.mo_id = mo_id;
	}
	public Integer getPerson_id() {
		return person_id;
	}
	public void setPerson_id(Integer person_id) {
		this.person_id = person_id;
	}
	public Date getMeeting_date() {
		return meeting_date;
	}
	public void setMeeting_date(Date meeting_date) {
		this.meeting_date = meeting_date;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public Timestamp getCreated_on() {
		return created_on;
	}
	public void setCreated_on(Timestamp created_on) {
		this.created_on = created_on;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	public String getGene_tables() {
		return gene_tables;
	}
	
	@SuppressWarnings("unchecked")
	public void setGene_tables(String gene_tables) {
		this.gene_tables = gene_tables;
		Gson gson = new Gson();
		Map<String,Object> gt=gson.fromJson(gene_tables, new TypeToken<HashMap<String,Object>>() {}.getType());
        if(gt!=null) {
	        this.ctDNA=(List<Map<String,Object>>)gt.get("ctDNA");
	        this.tumourNGS=(List<Map<String,Object>>)gt.get("tumourNGS");
	        this.summary = new ArrayList<Map<String,String>>();
	        this.summary.addAll(getSummeryCtDNA(this.ctDNA));
	        this.summary.addAll(getSummeryTumourNGS(this.tumourNGS));
        } else {
        	this.ctDNA = new ArrayList<Map<String,Object>>();
        	this.tumourNGS = new ArrayList<Map<String,Object>>();
        	this.summary = new ArrayList<Map<String,String>>();
        }
	}
	private Collection<? extends Map<String, String>> getSummeryTumourNGS(List<Map<String, Object>> tumourNGS2) {
		ArrayList<Map<String,String>> tumours= new ArrayList<>();
		for(Map<String,Object> tum: tumourNGS2) {
			HashMap<String,String> tumour = new HashMap<>();
			tumour.put("type", "Short Variant");
			tumour.put("timepoint", "Bx1");
			tumour.put("newlySelected", "false");
			tumour.put("source", "Tumour NGS");
			tumour.put("description", tum.get("result").toString());
			tumour.put("gene", tum.get("geneName").toString());
			tumours.add(tumour);
		}
		return tumours;
	}
	private Collection<? extends Map<String, String>> getSummeryCtDNA(List<Map<String, Object>> ctDNA2) {
		ArrayList<Map<String,String>> bloods= new ArrayList<>();
		for(Map<String,Object> bl: ctDNA2) {
			HashMap<String,String> blood = new HashMap<>();
			blood.put("type", "Short Variant");
			blood.put("timepoint", "B");
			blood.put("newlySelected", "false");
			blood.put("source", "ctDNA");
			blood.put("description", bl.get("result").toString());
			blood.put("gene", bl.get("geneName").toString());
			bloods.add(blood);
		}
		return bloods;
	}
	public Timestamp getLast_updated() {
		return last_updated;
	}
	public void setLast_updated(Timestamp last_updated) {
		this.last_updated = last_updated;
	}
	public Timestamp getLast_printed() {
		return last_printed;
	}
	public void setLast_printed(Timestamp last_printed) {
		this.last_printed = last_printed;
	}
	
	public List<Map<String,Object>> getCtDNA() {
		return this.ctDNA;
	}
	
	public List<Map<String,Object>> getTumourNGS() {
		return this.tumourNGS;
	}
	
	public Map<String,List<Map<String,String>>> getFMBlood() {
		return this.fmBlood;
	}
	
	public Map<String,List<Map<String,String>>> getFMTumour() {
		return this.fmTumour;
	}
	
	public Map<String, Map<String,List<Map<String,String>>>> getGenericGenomic(){
		return this.genericGenomic;
	}
	
	public void addTumourNGS(TumourNgsExt tumour) {
		if(this.tumourNGS==null) {
			this.tumourNGS=new ArrayList<Map<String,Object>>();
		}
		this.tumourNGS.add(Formater.formatTumourNgs(tumour));
		this.summary.add(Formater.formatTumourNgsSummery(tumour));
	}
	
	public void addCtDNA(GeneSubsetExt ctDNA) {
		if(this.ctDNA==null) {
			this.ctDNA=new ArrayList<Map<String,Object>>();
		}
		this.ctDNA.add(Formater.formatCTDNA(ctDNA));
		this.summary.add(Formater.formatCTDNASummery(ctDNA));
	}
	
	public void addFMBloodCNA(CopyNumberAlteration cna) {
		if(this.fmBlood==null) {
			this.fmBlood=new HashMap<>();
		}
		if(this.fmBlood.get("FMBloodCNA")==null) {
			this.fmBlood.put("FMBloodCNA", new ArrayList<>());
		}
		this.fmBlood.get("FMBloodCNA").add(Formater.formatCopyNumberAlteration(cna));
		this.summary.add(Formater.formatCopyNumberAlterationSummery(cna, "FM blood"));
	}
	
	public void addFMBloodSV(ShortVariant sv) {
		if(this.fmBlood==null) {
			this.fmBlood=new HashMap<>();
		}
		if(this.fmBlood.get("FMBloodSV")==null) {
			this.fmBlood.put("FMBloodSV", new ArrayList<>());
		}
		this.fmBlood.get("FMBloodSV").add(Formater.formatShortVariant(sv));
		this.summary.add(Formater.formatShortVariantSummery(sv, "FM blood"));
	}
	
	public void addFMBloodR(Rearrangement r) {
		if(this.fmBlood==null) {
			this.fmBlood=new HashMap<>();
		}
		if(this.fmBlood.get("FMBloodR")==null) {
			this.fmBlood.put("FMBloodR", new ArrayList<>());
		}
		this.fmBlood.get("FMBloodR").add(Formater.formatRearrangement(r));
		this.summary.add(Formater.formatRearrangementSummery(r, "FM blood"));
	}
	
	public void addFMTumourCNA(CopyNumberAlteration cna) {
		if(this.fmTumour==null) {
			this.fmTumour=new HashMap<>();
		}
		if(this.fmTumour.get("FMTumourCNA")==null) {
			this.fmTumour.put("FMTumourCNA", new ArrayList<>());
		}
		this.fmTumour.get("FMTumourCNA").add(Formater.formatCopyNumberAlteration(cna));
		this.summary.add(Formater.formatCopyNumberAlterationSummery(cna, "FM tumour"));
	}
	
	public void addFMTumourSV(ShortVariant sv) {
		if(this.fmTumour==null) {
			this.fmTumour=new HashMap<>();
		}
		if(this.fmTumour.get("FMTumourSV")==null) {
			this.fmTumour.put("FMTumourSV", new ArrayList<>());
		}
		this.fmTumour.get("FMTumourSV").add(Formater.formatShortVariant(sv));
		this.summary.add(Formater.formatShortVariantSummery(sv, "FM tumour"));
	}
	
	public void addFMTumourR(Rearrangement r) {
		if(this.fmTumour==null) {
			this.fmTumour=new HashMap<>();
		}
		if(this.fmTumour.get("FMTumourR")==null) {
			this.fmTumour.put("FMTumourR", new ArrayList<>());
		}
		this.fmTumour.get("FMTumourR").add(Formater.formatRearrangement(r));
		this.summary.add(Formater.formatRearrangementSummery(r, "FM tumour"));
	}
	
	public void addR(String source, Rearrangement r) {
		String name=source.substring(0, source.length()-1);
		if(this.genericGenomic==null) {
			this.genericGenomic=new HashMap<>();
		}
		if(!this.genericGenomic.containsKey(name)) {
			this.genericGenomic.put(name, new HashMap<>());
		}
		if(!this.genericGenomic.get(name).containsKey(source)) {
			this.genericGenomic.get(name).put(source, new ArrayList<>());
		}
		this.genericGenomic.get(name).get(source).add(Formater.formatRearrangement(r));
		this.summary.add(Formater.formatRearrangementSummery(r, name));
	}
	
	public void addSV(String source, ShortVariant sv) {
		String name=source.substring(0, source.length()-2);
		if(this.genericGenomic==null) {
			this.genericGenomic=new HashMap<>();
		}
		if(!this.genericGenomic.containsKey(name)) {
			this.genericGenomic.put(name, new HashMap<>());
		}
		if(!this.genericGenomic.get(name).containsKey(source)) {
			this.genericGenomic.get(name).put(source, new ArrayList<>());
		}
		this.genericGenomic.get(name).get(source).add(Formater.formatShortVariant(sv));
		this.summary.add(Formater.formatShortVariantSummery(sv, name));
	}
	
	public void addCNA(String source, CopyNumberAlteration cna) {
		String name=source.substring(0, source.length()-3);
		if(this.genericGenomic==null) {
			this.genericGenomic=new HashMap<>();
		}
		if(!this.genericGenomic.containsKey(name)) {
			this.genericGenomic.put(name, new HashMap<>());
		}
		if(!this.genericGenomic.get(name).containsKey(source)) {
			this.genericGenomic.get(name).put(source, new ArrayList<>());
		}
		this.genericGenomic.get(name).get(source).add(Formater.formatCopyNumberAlteration(cna));
		this.summary.add(Formater.formatCopyNumberAlterationSummery(cna, name));
	}
	
	public List<Map<String, String>> getSummary() {
		return summary;
	}
	
}
