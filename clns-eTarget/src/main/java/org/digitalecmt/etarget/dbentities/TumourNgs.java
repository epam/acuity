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

public class TumourNgs {
	private Integer person_id = -1;
	private Integer specimen_id = -1;
	private Date specimen_date = null;
	private String gene_name = null;
	private Integer gene_varient_id = -1;
	private String cdna_change = null;
	private String amino_acid_change = null;
	private Integer variant_allele_frequency = -1;
	private String average_read_depth = null;
	private Integer coverage = -1;
	private String path_lab_ref = null;
	private Date date_requested = null;
	private String filename = null;
	private String comments = null;
	private String unknown_significance = null;
	private Integer baseline_number = 1;
	private Integer run_number = 1;
	private Integer measurement_gene_panel_id =1;
	private String tumour_id = null;
	private String target_id =null;
	private String preclin_id = null;
	
	public TumourNgs() {
	
	}

	protected TumourNgs(TumourNgs tngs) {
		if(tngs==null) return;
		person_id=tngs.person_id;
		specimen_id=tngs.specimen_id;
		specimen_date=tngs.specimen_date;
		gene_name=tngs.gene_name;
		gene_varient_id=tngs.gene_varient_id;
		cdna_change=tngs.cdna_change;
		amino_acid_change=tngs.amino_acid_change;
		variant_allele_frequency=tngs.variant_allele_frequency;
		average_read_depth=tngs.average_read_depth;
		coverage=tngs.coverage;
		path_lab_ref=tngs.path_lab_ref;
		date_requested=tngs.date_requested;
		filename=tngs.filename;
		comments=tngs.comments;
		unknown_significance=tngs.unknown_significance;
		baseline_number=tngs.baseline_number;
		run_number=tngs.run_number;
		measurement_gene_panel_id=tngs.measurement_gene_panel_id;
		tumour_id = tngs.tumour_id;
		target_id = tngs.target_id;
		preclin_id = tngs.preclin_id;
	}
	
	public Integer getPerson_id() {
		return person_id;
	}

	public void setPerson_id(Integer person_id) {
		this.person_id = person_id;
	}

	public Integer getSpecimen_id() {
		return specimen_id;
	}

	public void setSpecimen_id(Integer specimen_id) {
		this.specimen_id = specimen_id;
	}

	public Date getSpecimen_date() {
		return specimen_date;
	}

	public void setSpecimen_date(Date specimen_date) {
		this.specimen_date = specimen_date;
	}

	public String getGene_name() {
		return gene_name;
	}

	public void setGene_name(String gene_name) {
		this.gene_name = gene_name;
	}

	public Integer getGene_varient_id() {
		return gene_varient_id;
	}

	public void setGene_varient_id(Integer gene_varient_id) {
		this.gene_varient_id = gene_varient_id;
	}

	public String getCdna_change() {
		return cdna_change;
	}

	public void setCdna_change(String cdna_change) {
		this.cdna_change = cdna_change;
	}

	public String getAmino_acid_change() {
		return amino_acid_change;
	}

	public void setAmino_acid_change(String amino_acid_change) {
		this.amino_acid_change = amino_acid_change;
	}

	public Integer getApproximate_mutatnt_allele_frequency() {
		return variant_allele_frequency;
	}

	public void setApproximate_mutatnt_allele_frequency(Integer variant_allele_frequency) {
		this.variant_allele_frequency = variant_allele_frequency;
	}

	public String getAverage_read_depth() {
		return average_read_depth;
	}

	public void setAverage_read_depth(String average_read_depth) {
		this.average_read_depth = average_read_depth;
	}

	public Integer getCoverage() {
		return coverage;
	}

	public void setCoverage(Integer coverage) {
		this.coverage = coverage;
	}

	public String getPath_lab_ref() {
		return path_lab_ref;
	}

	public void setPath_lab_ref(String path_lab_ref) {
		this.path_lab_ref = path_lab_ref;
	}

	public Date getDate_requested() {
		return date_requested;
	}

	public void setDate_requested(Date date_requested) {
		this.date_requested = date_requested;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getComments() {
		if(comments!=null && comments.equals("n/a")) {
			return "";
		}
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getUnknown_significance() {
		return unknown_significance;
	}

	public void setUnknown_significance(String unknown_significance) {
		this.unknown_significance = unknown_significance;
	}

	public Integer getBaseline_number() {
		if(baseline_number==null) return 1;
		return baseline_number;
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

	public Integer getMeasurement_gene_panel_id() {
		return measurement_gene_panel_id;
	}

	public void setMeasurement_gene_panel_id(Integer measurement_gene_panel_id) {
		this.measurement_gene_panel_id = measurement_gene_panel_id;
	}

	public String getTumour_id() {
		return tumour_id;
	}

	public void setTumour_id(String tumour_id) {
		this.tumour_id = tumour_id;
	}

	public String getTarget_id() {
		return target_id;
	}

	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}
	public String getPreclin_id() {
		return preclin_id;
	}

	public void setPreclin_id(String preclin_id) {
		this.preclin_id = preclin_id;
	}
}
