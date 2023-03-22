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
import java.util.Calendar;

public class NgsLib {
	private Integer baseline_number=-1;
	private Integer run_number=0;
	private Integer measurement_gene_panel_id=-1;
	private String  ngs_library_cfdna_input=null;
	private String  cfdna_input_colour_key=null;
	private String  average_read_depth=null;
	private String  average_read_depth_colour_key=null;
	private String  bioinformatics_pipeline=null;
	private String  level_of_detection=null;
	private String  ngs_comment=null;
	private String  exploratory_comment=null;
	private Date    specimen_date=null;
	private Boolean analysis_failed=false;
	private Boolean no_mutations_found=false;
	private String  ngs_run=null;
	private String  specimen_id=null;
	private String  panel_name=null;
	private Timestamp ingestion_date;
	private String  target_id;
	private Date    date_issued;
	
	public NgsLib() {
		
	}
	
	protected NgsLib(NgsLib gs) {
		baseline_number=gs.baseline_number;
		run_number=gs.run_number;
		measurement_gene_panel_id=gs.measurement_gene_panel_id;
		ngs_comment=gs.ngs_comment;
		exploratory_comment=gs.exploratory_comment;
		cfdna_input_colour_key=gs.cfdna_input_colour_key;
		average_read_depth=gs.average_read_depth;
		average_read_depth_colour_key=gs.average_read_depth_colour_key;
		level_of_detection=gs.level_of_detection;
		bioinformatics_pipeline=gs.bioinformatics_pipeline;
		specimen_date=gs.specimen_date;
		ngs_library_cfdna_input=gs.ngs_library_cfdna_input;
		analysis_failed=gs.analysis_failed;
		no_mutations_found=gs.no_mutations_found;
		ngs_run=gs.ngs_run;
		specimen_id=gs.specimen_id;
		panel_name=gs.panel_name;
		ingestion_date=gs.ingestion_date;
		target_id=gs.target_id;
		date_issued=gs.date_issued;
	}
	
	public Integer getBaseline_number() {
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

	public String getCfdna_input_colour_key() {
		return cfdna_input_colour_key;
	}
	public void setCfdna_input_colour_key(String cfdna_input_colour_key) {
		this.cfdna_input_colour_key = cfdna_input_colour_key;
	}
	public String getAverage_read_depth() {
		return average_read_depth;
	}
	public void setAverage_read_depth(String average_read_depth) {
		this.average_read_depth = average_read_depth;
	}
	public String getAverage_read_depth_colour_key() {
		return average_read_depth_colour_key;
	}
	public void setAverage_read_depth_colour_key(String average_read_depth_colour_key) {
		this.average_read_depth_colour_key = average_read_depth_colour_key;
	}
	public String getBioinformatics_pipeline() {
		return bioinformatics_pipeline;
	}
	public void setBioinformatics_pipeline(String bioinformatics_pipeline) {
		this.bioinformatics_pipeline = bioinformatics_pipeline;
	}
	public String getLevel_of_detection() {
		return level_of_detection;
	}
	public void setLevel_of_detection(String level_of_detection) {
		this.level_of_detection = level_of_detection;
	}
	public String getExploratory_comment() {
		return (exploratory_comment==null || exploratory_comment.length()==0) ?"No comments.":this.exploratory_comment;
	}
	public void setExploratory_comment(String exploratory_comment) {
		this.exploratory_comment = exploratory_comment;
	}
	public String getNgs_comment() {
		return (ngs_comment==null || ngs_comment.length()==0) ?"No comments.":this.ngs_comment;
	}
	public void setNgs_comment(String ngs_comment) {
		this.ngs_comment = ngs_comment;
	}
	public String getNgs_library_cfdna_input() {
		return ngs_library_cfdna_input;
	}

	public void setNgs_library_cfdna_input(String ngs_library_cfdna_input) {
		this.ngs_library_cfdna_input = ngs_library_cfdna_input;
	}
	
	public Date getSpecimen_date() {
		return specimen_date;
	}
	public void setSpecimen_date(Date specimen_date) {
		this.specimen_date = specimen_date;
	}
	public String isAnalysis_failed() {
		return Boolean.toString(analysis_failed);//?"Y":"N";
	}
	public void setAnalysis_failed(Boolean analysis_failed) {
		this.analysis_failed = analysis_failed;
	}
	public String isNo_mutations_found() {
		return Boolean.toString(no_mutations_found);
	}
	public void setNo_mutations_found(Boolean no_mutations_found) {
		this.no_mutations_found = no_mutations_found;
	}
	public String getNgs_run() {
		return ngs_run;
	}
	public void setNgs_run(String ngs_run) {
		this.ngs_run = ngs_run;
	}
	public String getSpecimen_id() {
		return specimen_id;
	}
	public void setSpecimen_id(String specimen_id) {
		this.specimen_id = specimen_id;
	}
	public String getPanel_name() {
		return panel_name;
	}
	public void setPanel_name(String panel_name) {
		this.panel_name = panel_name;
	}
	public Calendar getIngestion_calendar() {
		if(ingestion_date==null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(ingestion_date);
		return cal;
	}
	
	public Timestamp getIngestion_date() {
		return ingestion_date;
	}
	
	public void setIngestion_date(Timestamp ingestion_date) {
		this.ingestion_date = ingestion_date;
	}

	public String getTarget_id() {
		return target_id;
	}

	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}

	public Date getDate_issued() {
		return date_issued;
	}

	public void setDate_issued(Date date_issued) {
		this.date_issued = date_issued;
	}
}
