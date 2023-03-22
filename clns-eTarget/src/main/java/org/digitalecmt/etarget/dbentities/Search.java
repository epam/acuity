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

public class Search {
	private String condition_name=null;
	private String subtype_name=null;
	private String gene_name=null;
	private String cdna_change=null;
	private String amino_acid_change=null;
	private String target_id=null;
	private Integer person_id=-1;
	private String rearr_gene1=null;
	private String rearr_gene2=null;
	private String rearr_description=null;
	private String cna_type=null;
	private String variant_type=null;
	private Integer unknown_significant=-1;
		
	public String getSubtype_name() {
		return subtype_name;
	}
	public void setSubtype_name(String subtype_name) {
		this.subtype_name = subtype_name;
	}
	public String getRearr_gene1() {
		return rearr_gene1;
	}
	public void setRearr_gene1(String rearr_gene1) {
		this.rearr_gene1 = rearr_gene1;
	}
	public String getRearr_gene2() {
		return rearr_gene2;
	}
	public void setRearr_gene2(String rearr_gene2) {
		this.rearr_gene2 = rearr_gene2;
	}
	public String getRearr_description() {
		return rearr_description;
	}
	public void setRearr_description(String rearr_description) {
		this.rearr_description = rearr_description;
	}
	public String getCna_type() {
		return cna_type;
	}
	public void setCna_type(String cna_type) {
		this.cna_type = cna_type;
	}
	public String getVariant_type() {
		if(variant_type==null) return "short_variant";
		return variant_type;
	}
	public void setVariant_type(String variant_type) {
		this.variant_type = variant_type;
	}
	public String getCdna_change() {
		if(cdna_change==null) return "";
		return cdna_change;
	}
	public void setCdna_change(String cdna_change) {
		this.cdna_change = cdna_change;
	}
	public String getAmino_acid_change() {
		if(amino_acid_change==null) return "";
		return amino_acid_change;
	}
	public void setAmino_acid_change(String amino_acid_change) {
		this.amino_acid_change = amino_acid_change;
	}
	public String getCondition_name() {
		return condition_name;
	}
	public void setCondition_name(String condition_name) {
		this.condition_name = condition_name;
	}
	public String getGene_name() {
		return gene_name;
	}
	public void setGene_name(String gene_name) {
		this.gene_name = gene_name;
	}
	public String getTarget_id() {
		return target_id;
	}
	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}
	public Integer getPerson_id() {
		return person_id;
	}
	public void setPerson_id(Integer person_id) {
		this.person_id = person_id;
	}
	public Integer getUnknown_significant() {
		return unknown_significant;
	}
	public void setUnknown_significant(Integer unknown_significant) {
		this.unknown_significant = unknown_significant;
	}
}
