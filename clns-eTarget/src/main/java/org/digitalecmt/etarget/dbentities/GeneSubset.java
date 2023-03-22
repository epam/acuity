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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneSubset {
	private Integer baseline_number=-1;
	private Integer run_number=0;
	private String cdna_change=null;
	private String gene_name=null;
	private String amino_acid_change=null;
	private Float variant_allele_frequency=-1.0f;
	private Integer read_depth=-1;
	private Float germline_frequency=-1f;
	private String functional_effect=null;
	private String high_confidence=null;
	private String is_specific_mutation_in_panel=null;
	private String specimen_id=null;
	private Date specimen_date=null;
	
	private String panel_name=null;
	private String ngs_comment=null;
	private String ngs_run=null;
	private Integer specimen_concept_id=-1;
	private Integer person_id=-1;
	private Integer gene_varient_id=-1;
	private Integer measurement_gene_panel_id=-1;
	private String position=null;
	private String cosmic_url=null;
	
	public GeneSubset() {
		
	}
	
	protected GeneSubset(GeneSubset gs) {
		if(gs==null) return;
		baseline_number=gs.baseline_number;
		run_number=gs.run_number;
		cdna_change=gs.cdna_change;
		gene_name=gs.gene_name;
		amino_acid_change=gs.amino_acid_change;
		variant_allele_frequency=gs.variant_allele_frequency;
		read_depth=gs.read_depth;
		germline_frequency=gs.germline_frequency;
		functional_effect=gs.functional_effect;
		high_confidence=gs.high_confidence;
		is_specific_mutation_in_panel=gs.is_specific_mutation_in_panel;
		specimen_id=gs.specimen_id;
		specimen_date=gs.specimen_date;
		panel_name=gs.panel_name;
		ngs_comment=gs.ngs_comment;
		ngs_run=gs.ngs_run;
		specimen_concept_id=gs.specimen_concept_id;
		person_id=gs.person_id;
		gene_varient_id=gs.gene_varient_id;
		measurement_gene_panel_id=gs.measurement_gene_panel_id;
		position=gs.position;
		cosmic_url=gs.cosmic_url;

	}
	
	public Integer getBaseline_number() {
		return baseline_number==null?1:baseline_number;
	}
	public void setBaseline_number(Integer baseline_number) {
		this.baseline_number = baseline_number;
	}
	public Integer getRun_number() {
		return run_number;
	}
	public void setRun_number(Integer run_number) {
		this.run_number = run_number;
	}
	public String getCdna_change() {
		return cdna_change;
	}
	public void setCdna_change(String cdna_change) {
		if(cdna_change!=null) {
			this.cdna_change = cdna_change.trim();
		} else {
			this.cdna_change=null;
		}
	}
	public String getGene_name() {
		return gene_name;
	}
	public void setGene_name(String gene_name) {
		this.gene_name = gene_name;
	}
	public String getAmino_acid_change() {
		return amino_acid_change;
	}
	public void setAmino_acid_change(String amino_acid_change) {
		if(amino_acid_change!=null) {
			this.amino_acid_change = amino_acid_change.trim();
		} else {
			this.amino_acid_change=null;
		}
	}
	public Float getVariant_allele_frequency() {
		return variant_allele_frequency;
	}
	public void setVariant_allele_frequency(Float variant_allele_frequency) {
		this.variant_allele_frequency = variant_allele_frequency;
	}
	public Integer getRead_depth() {
		return read_depth;
	}
	public void setRead_depth(Integer read_depth) {
		this.read_depth = read_depth;
	}
	public Float getGermline_frequency() {
		return germline_frequency;
	}
	public void setGermline_frequency(Float germline_frequency) {
		this.germline_frequency = germline_frequency;
	}
	public String getFunctional_effect() {
		return functional_effect;
	}
	public void setFunctional_effect(String functional_effect) {
		this.functional_effect = functional_effect;
	}
	public String getHigh_confidence() {
		return high_confidence;
	}
	public void setHigh_confidence(String high_confidence) {
		this.high_confidence = high_confidence;
	}
	public String getIs_specific_mutation_in_panel() {
		return is_specific_mutation_in_panel;
	}
	public void setIs_specific_mutation_in_panel(String is_specific_mutation_in_panel) {
		this.is_specific_mutation_in_panel = is_specific_mutation_in_panel;
	}
	public String getSpecimen_id() {
		return specimen_id;
	}
	public void setSpecimen_id(String specimen_id) {
		this.specimen_id = specimen_id;
	}
	public Date getSpecimen_date() {
		return specimen_date;
	}
	public void setSpecimen_date(Date specimen_date) {
		this.specimen_date = specimen_date;
	}
	public String getPanel_name() {
		return panel_name;
	}
	public void setPanel_name(String panel_name) {
		this.panel_name = panel_name;
	}
	public String getNgs_comment() {
		return ngs_comment;
	}
	public void setNgs_comment(String ngs_comment) {
		this.ngs_comment = ngs_comment;
	}
	public String getNgs_run() {
		return ngs_run;
	}
	public void setNgs_run(String ngs_run) {
		this.ngs_run = ngs_run;
	}
	public int getSpecimen_concept_id() {
		return specimen_concept_id;
	}
	public void setSpecimen_concept_id(Integer specimen_concept_id) {
		this.specimen_concept_id = specimen_concept_id;
	}
	public Integer getPerson_id() {
		return person_id;
	}
	public void setPerson_id(Integer person_id) {
		this.person_id = person_id;
	}
	public Integer getGene_varient_id() {
		return gene_varient_id;
	}
	public void setGene_varient_id(Integer gene_varient_id) {
		this.gene_varient_id = gene_varient_id;
	}
	public Integer getMeasurement_gene_panel_id() {
		return measurement_gene_panel_id;
	}

	public void setMeasurement_gene_panel_id(Integer measurement_gene_panel_id) {
		this.measurement_gene_panel_id = measurement_gene_panel_id;
	}
	
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getCosmic_url() {
		return cosmic_url;
	}

	public void setCosmic_url(String cosmic_url) {
		Pattern p = Pattern.compile("\\d+");
		if(cosmic_url!=null && cosmic_url.startsWith("ID")) {
		    Matcher m = p.matcher(cosmic_url.subSequence(0, cosmic_url.indexOf(";")));
		    this.cosmic_url="";
		    while (m.find()) {
		    	String current=m.group();
		    	if(this.cosmic_url.length()>0) {
		    		this.cosmic_url=this.cosmic_url.concat(", ");
		    	}
		        this.cosmic_url=this.cosmic_url.concat("<a target=\"_blank\" rel=\"noreferrer\" href=\"http://cancer.sanger.ac.uk/cosmic/mutation/overview?id="+current+"\">"+current+"</a>");
		    } 
		} else if(cosmic_url!=null && cosmic_url.toLowerCase().startsWith("<a")) {
			this.cosmic_url = cosmic_url;
			this.cosmic_url=this.cosmic_url.replaceAll("a href", "a target=\"_blank\" href");
		} else if(cosmic_url!=null && cosmic_url.toLowerCase().startsWith("http")) {
			Matcher m = p.matcher(cosmic_url);
		    if (m.find()) {
		    	this.cosmic_url="<a target=\"_blank\" rel=\"noreferrer\" href=\""+cosmic_url+"\">"+m.group()+"</a>";
		    } else {
		    	this.cosmic_url="<a target=\"_blank\" rel=\"noreferrer\" href=\""+cosmic_url+"\">"+cosmic_url+"</a>";
		    }
			
		} else if(cosmic_url!=null && cosmic_url.toUpperCase().startsWith("COSM")) {
			 Matcher m = p.matcher(cosmic_url);
			 if(m.find()) {
				 String current=m.group();
				 this.cosmic_url="<a target=\"_blank\" rel=\"noreferrer\" href=\"http://cancer.sanger.ac.uk/cosmic/mutation/overview?id="+current+"\">"+current+"</a>";
			 }
		} else {
			this.cosmic_url=cosmic_url;
		}
	}

	public boolean sameAs(GeneSubset compare) {
		if(this.gene_name.equals(compare.gene_name) && 
				this.position.equals(compare.position)) {
			return true;
		}
		return false;
	}
}
