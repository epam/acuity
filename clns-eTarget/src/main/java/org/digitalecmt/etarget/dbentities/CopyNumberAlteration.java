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

public class CopyNumberAlteration {
	Integer measurement_gene_variant_id;
	String gene_name;
	String exons;
	String position;
	Float cna_ratio;
	String status;
	String cna_type;
	Integer measurement_gene_panel_id;
	Integer copy_number;
	String variant_type;
	Integer baseline;
	String equivocal;
	
	public String getEquivocal() {
		return equivocal;
	}
	public void setEquivocal(String equivocal) {
		this.equivocal = equivocal;
	}
	public Integer getBaseline() {
		return baseline;
	}
	public void setBaseline(Integer baseline) {
		this.baseline = baseline;
	}
	public String getVariant_concept_id() {
		if(variant_type==null) return "copy_number_alteration";
		return variant_type;
	}
	public void setVariant_concept_id(String variant_type) {
		this.variant_type = variant_type;
	}
	public Integer getMeasurement_gene_variant_id() {
		return measurement_gene_variant_id;
	}
	public void setMeasurement_gene_variant_id(Integer measurement_gene_variant_id) {
		this.measurement_gene_variant_id = measurement_gene_variant_id;
	}
	public Integer getCopy_number() {
		if(copy_number==null) return -1;
		return copy_number;
	}
	public void setCopy_number(Integer copy_number) {
		this.copy_number = copy_number;
	}
	public Integer getMeasurement_gene_panel_id() {
		return measurement_gene_panel_id;
	}
	public void setMeasurement_gene_panel_id(Integer measurement_gene_panel_id) {
		this.measurement_gene_panel_id = measurement_gene_panel_id;
	}
	public String getGene_name() {
		return gene_name;
	}
	public void setGene_name(String gene_name) {
		this.gene_name = gene_name;
	}
	public String getExons() {
		return exons;
	}
	public void setExons(String exons) {
		this.exons = exons;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Float getCna_ratio() {
		return cna_ratio;
	}
	public void setCna_ratio(Float cna_ratio) {
		this.cna_ratio = cna_ratio;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCna_type() {
		return cna_type;
	}
	public void setCna_type(String cna_type) {
		this.cna_type = cna_type;
	}
	public Boolean isSignificant() {
		if(this.getStatus()==null) {
			return true;
		}
		return !(this.getStatus().toLowerCase().equals("unknown") || this.getStatus().toLowerCase().equals("ambiguous"));
	}
}
