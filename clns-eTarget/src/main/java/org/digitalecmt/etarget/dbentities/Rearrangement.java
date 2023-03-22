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

public class Rearrangement {
	Integer measurement_gene_variant_id;
	String rearr_description;
	String rearr_in_frame;
	String rearr_gene_2_name;
	String rearr_pos1; 
	String rearr_pos2;
	String status;
	Integer rearr_number_of_reads;
	String rearr_gene_1_name;
	Integer measurement_gene_panel_id;
	String variant_type;
	Integer baseline;
	String rearr_type;
	Float variant_allele_frequency;
	
	public Float getVariant_allele_frequency() {
		return variant_allele_frequency;
	}
	public void setVariant_allele_frequency(Float variant_allele_frequency) {
		this.variant_allele_frequency = variant_allele_frequency;
	}
	public String getRearr_type() {
		return rearr_type;
	}
	public void setRearr_type(String rearr_type) {
		this.rearr_type = rearr_type;
	}
	public Integer getBaseline() {
		return baseline;
	}
	public void setBaseline(Integer baseline) {
		this.baseline = baseline;
	}
	public String getVariant_type() {
		if(variant_type==null) return "rearrangement";
		return variant_type;
	}
	public void setVariant_type(String variant_type) {
		this.variant_type = variant_type;
	}
	public Integer getMeasurement_gene_variant_id() {
		return measurement_gene_variant_id;
	}
	public void setMeasurement_gene_variant_id(Integer measurement_gene_variant_id) {
		this.measurement_gene_variant_id = measurement_gene_variant_id;
	}
	public Integer getMeasurement_gene_panel_id() {
		return measurement_gene_panel_id;
	}
	public void setMeasurement_gene_panel_id(Integer measurement_gene_panel_id) {
		this.measurement_gene_panel_id = measurement_gene_panel_id;
	}
	public String getRearr_description() {
		return rearr_description;
	}
	public void setRearr_description(String rearr_description) {
		this.rearr_description = rearr_description;
	}
	public String getRearr_in_frame() {
		return rearr_in_frame;
	}
	public void setRearr_in_frame(String rearr_in_frame) {
		this.rearr_in_frame = rearr_in_frame;
	}
	public String getRearr_gene_2_name() {
		return rearr_gene_2_name;
	}
	public void setRearr_gene_2_name(String rearr_gene_2_name) {
		this.rearr_gene_2_name = rearr_gene_2_name;
	}
	public String getRearr_pos1() {
		return rearr_pos1;
	}
	public void setRearr_pos1(String rearr_pos1) {
		this.rearr_pos1 = rearr_pos1;
	}
	public String getRearr_pos2() {
		return rearr_pos2;
	}
	public void setRearr_pos2(String rearr_pos2) {
		this.rearr_pos2 = rearr_pos2;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getRearr_number_of_reads() {
		return rearr_number_of_reads;
	}
	public void setRearr_number_of_reads(Integer rearr_number_of_reads) {
		this.rearr_number_of_reads = rearr_number_of_reads;
	}
	public String getRearr_gene_1_name() {
		return rearr_gene_1_name;
	}
	public void setRearr_gene_1_name(String rearr_gene_1_name) {
		this.rearr_gene_1_name = rearr_gene_1_name;
	}
	
	public Boolean isSignificant() {
		if(this.getStatus()==null) {
			return true;
		}
		return !(this.getStatus().toLowerCase().equals("unknown") || this.getStatus().toLowerCase().equals("ambiguous"));
	}
}
