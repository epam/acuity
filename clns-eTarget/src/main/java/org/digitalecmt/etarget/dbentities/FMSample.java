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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;


public class FMSample {
	String target_id;
	Integer specimen_id;
	Integer measurement_gene_panel_id;
	Date specimen_date;  
	Integer specimen_concept_id;  
	Integer baseline_number;
	Integer run_number;
	String tumour_id;
	String preclin_id;
	Float tmb_score;
	String tmb_status;
	String tmb_unit;
	Float microsatellite_instability_score;
	String microsatellite_instability_status;
	Float mean_exon_depth;
	Float percent_exons_100x;
	Float tumour_fraction_score;
	String tumour_fraction_unit;

	public Float getTumour_fraction_score() {
		return tumour_fraction_score;
	}
	public void setTumour_fraction_score(Float tumour_fraction_score) {
		this.tumour_fraction_score = tumour_fraction_score;
	}
	public String getTumour_fraction_unit() {
		return tumour_fraction_unit;
	}
	public void setTumour_fraction_unit(String tumour_fraction_unit) {
		this.tumour_fraction_unit = tumour_fraction_unit;
	}
	String specimentDateFormatted=null;
	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public String getPreclin_id() {
		return preclin_id;
	}
	public void setPreclin_id(String preclin_id) {
		this.preclin_id = preclin_id;
	}
	public Integer getMean_exon_depth_int() {
		if(this.mean_exon_depth!=null) {
			return Math.round(this.mean_exon_depth);
		} 
		return null;
	}
	
	public Float getMean_exon_depth() {
		return this.mean_exon_depth;
	}
	public void setMean_exon_depth(Float mean_exon_depth) {
		this.mean_exon_depth = mean_exon_depth;
	}
	public Float getPercent_exons_100x() {
		return percent_exons_100x;
	}
	public void setPercent_exons_100x(Float percent_exons_100x) {
		this.percent_exons_100x = percent_exons_100x;
	}
	
	public Integer getSpecimen_id() {
		return specimen_id;
	}
	public void setSpecimen_id(Integer specimen_id) {
		this.specimen_id = specimen_id;
	}
	public Integer getMeasurement_gene_panel_id() {
		return measurement_gene_panel_id;
	}
	public void setMeasurement_gene_panel_id(Integer measurement_gene_panel_id) {
		this.measurement_gene_panel_id = measurement_gene_panel_id;
	}
	public Date getSpecimen_date() {
		return specimen_date;
	}
	public void setSpecimen_date(Date specimen_date) {
		this.specimen_date = specimen_date;
	}
	public Integer getSpecimen_concept_id() {
		return specimen_concept_id;
	}
	public void setSpecimen_concept_id(Integer specimen_concept_id) {
		this.specimen_concept_id = specimen_concept_id;
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
	public String getTumour_id() {
		if(tumour_id==null) return "unknown";
		return tumour_id;
	}
	public void setTumour_id(String tumour_id) {
		this.tumour_id = tumour_id;
	}
	public Float getTmb_score() {
		return tmb_score;
	}
	public void setTmb_score(Float tmb_score) {
		this.tmb_score = tmb_score;
	}
	public String getTmb_status() {
		return tmb_status;
	}
	public void setTmb_status(String tmb_status) {
		this.tmb_status = tmb_status;
	}
	public String getTmb_unit() {
		return tmb_unit;
	}
	public void setTmb_unit(String tmb_unit) {
		this.tmb_unit = tmb_unit;
	}
	public Float getMicrosatellite_instability_score() {
		return microsatellite_instability_score;
	}
	public void setMicrosatellite_instability_score(Float microsatellite_instability_score) {
		this.microsatellite_instability_score = microsatellite_instability_score;
	}
	public String getMicrosatellite_instability_status() {
		return microsatellite_instability_status;
	}
	public void setMicrosatellite_instability_status(String microsatellite_instability_status) {
		this.microsatellite_instability_status = microsatellite_instability_status;
	}
	public String getTarget_id() {
		return target_id;
	}
	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}
	public String getSpecimentDateFormatted() {
		if (specimentDateFormatted == null) {
			if (this.getSpecimen_date() != null) {
				specimentDateFormatted = df.format(this.getSpecimen_date());
			} else {
				specimentDateFormatted = "Not recorded";
			}
		}
		return specimentDateFormatted;
	}
	
}
