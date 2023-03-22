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

public class IHCReport {
	Integer ihc_report_id;
	Integer person_id;
	Integer specimen_id;
	Date specimen_date;
	Date sample_received_date;
	Date report_date;
	Float cd3_total_tissue_area;
	Float cd3_intratumoural;
	Float cd3_intrastromal;
	Float cd8_total_tissue_area;
	Float cd8_intratumoural;
	Float cd8_intrastromal;
	String pdl1_tps;
	String estimated_results;
	String comments;
	String preclin_id;
	
	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	

	public String getPreclin_id() {
		return preclin_id;
	}

	public void setPreclin_id(String preclin_id) {
		this.preclin_id = preclin_id;
	}

	public String getSpecimen_date_formated() {
		if(specimen_date==null) return "";
		return df.format(specimen_date);
	}

	public Date getSpecimen_date() {
		return specimen_date;
	}

	public void setSpecimen_date(Date specimen_date) {
		this.specimen_date = specimen_date;
	}

	public Integer getIhc_report_id() {
		return ihc_report_id;
	}
	public void setIhc_report_id(Integer ihc_report_id) {
		this.ihc_report_id = ihc_report_id;
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
	public Date getSample_received_date() {
		return sample_received_date;
	}
	public String getSample_received_date_formatted() {
		if(sample_received_date==null) return "";
		return df.format(sample_received_date);
	}
	public void setSample_received_date(Date sample_received_date) {
		this.sample_received_date = sample_received_date;
	}
	public Date getReport_date() {
		return report_date;
	}
	public String getReport_date_formatted() {
		if(report_date==null) return "";
		return df.format(report_date);
	}
	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}
	public Float getCd3_total_tissue_area() {
		return cd3_total_tissue_area;
	}
	public void setCd3_total_tissue_area(Float cd3_total_tissue_area) {
		this.cd3_total_tissue_area = cd3_total_tissue_area;
	}
	public Float getCd3_intratumoural() {
		return cd3_intratumoural;
	}
	public void setCd3_intratumoural(Float cd3_intratumoural) {
		this.cd3_intratumoural = cd3_intratumoural;
	}
	public Float getCd3_intrastromal() {
		return cd3_intrastromal;
	}
	public void setCd3_intrastromal(Float cd3_intrastromal) {
		this.cd3_intrastromal = cd3_intrastromal;
	}
	public Float getCd8_total_tissue_area() {
		return cd8_total_tissue_area;
	}
	public void setCd8_total_tissue_area(Float cd8_total_tissue_area) {
		this.cd8_total_tissue_area = cd8_total_tissue_area;
	}
	public Float getCd8_intratumoural() {
		return cd8_intratumoural;
	}
	public void setCd8_intratumoural(Float cd8_intratumoural) {
		this.cd8_intratumoural = cd8_intratumoural;
	}
	public Float getCd8_intrastromal() {
		return cd8_intrastromal;
	}
	public void setCd8_intrastromal(Float cd8_intrastromal) {
		this.cd8_intrastromal = cd8_intrastromal;
	}
	public String getPdl1_tps() {
		return pdl1_tps;
	}
	public void setPdl1_tps(String pdl1_tps) {
		this.pdl1_tps = pdl1_tps;
	}
	public String getEstimated_results() {
		return estimated_results;
	}
	public void setEstimated_results(String estimated_results) {
		this.estimated_results = estimated_results;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
}
