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

public class ShortVariant {
	Integer measurement_gene_variant_id;
	Integer measurement_gene_panel_id;
	String gene_name;
	String cdna_change;
	Integer read_depth;
	Float variant_allele_frequency;
	String position;
	String amino_acid_change;
	String transcript;
	String status;
	String variant_type;
	Integer baseline;
	String functional_effect;
	String subclonal;
	
	public String getSubclonal() {
		return subclonal;
	}
	public void setSubclonal(String subclonal) {
		this.subclonal = subclonal;
	}
	public String getFunctional_effect() {
		return functional_effect;
	}
	public void setFunctional_effect(String functional_effect) {
		this.functional_effect = functional_effect;
	}
	public Integer getBaseline() {
		return baseline;
	}
	public void setBaseline(Integer baseline) {
		this.baseline = baseline;
	}
	public String getVariant_concept_id() {
		if(variant_type==null) return "short_variant";
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGene_name() {
		return gene_name;
	}
	public void setGene_name(String gene) {
		this.gene_name = gene;
	}
	public String getCdna_change() {
		return cdna_change;
	}
	public void setCdna_change(String cdna_change) {
		this.cdna_change = cdna_change;
	}
	public Integer getRead_depth() {
		return read_depth;
	}
	public void setRead_depth(Integer read_depth) {
		this.read_depth = read_depth;
	}
	public Float getVariant_allele_frequency() {
		return variant_allele_frequency;
	}
	public void setVariant_allele_frequency(Float variant_allele_frequency) {
		this.variant_allele_frequency = variant_allele_frequency;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getAmino_acid_change() {
		return amino_acid_change;
	}
	public void setAmino_acid_change(String amino_acid_change) {
		this.amino_acid_change = amino_acid_change;
	}
	public String getTranscript() {
		if(transcript==null) 
			return "-";
		return transcript;
	}
	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}
	public Integer getMeasurement_gene_panel_id() {
		return measurement_gene_panel_id;
	}
	public void setMeasurement_gene_panel_id(Integer measurement_gene_panel_id) {
		this.measurement_gene_panel_id = measurement_gene_panel_id;
	}
	
	public Boolean isSignificant() {
		if(this.getStatus()==null) {
			return true;
		}
		return !(this.getStatus().toLowerCase().equals("unknown") || this.getStatus().toLowerCase().equals("ambiguous"));
	}
	
	public String getResult() {
		return "c."+this.getCdna_change() + " p.("+this.getAmino_acid_change()+")";
	}
}
