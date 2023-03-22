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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.digitalecmt.etarget.support.Formater;

public class GeneSubsetTumour {
	private Integer baseline_number=1;
	private Integer run_number=1;
	private Integer measurement_gene_panel_id=-1;
	private String gene_name;
	private Date specimen_date;
	private Integer gene_varient_id;
	private String cdna_change;
	private String amino_acid_change;
	private Integer variant_allele_frequency;
	private String result=null;
	

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
	public String getGene_name() {
		return gene_name;
	}
	public void setGene_name(String gene_name) {
		this.gene_name = gene_name;
	}
	public Date getSpecimen_date() {
		return specimen_date;
	}
	public void setSpecimen_date(Date specimen_date) {
		this.specimen_date = specimen_date;
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
	public String getResult() {
		if (result==null) {
			if (this.getCdna_change() != null) {
				if (this.getAmino_acid_change().contains("(")) {
					String[] am1 = this.getAmino_acid_change().split("\\(");
					String[] am2 = am1[1].split("\\)");
					result = cdna_change + " " + am1[0] + "(<strong>" + am2[0] + "</strong>)";
				} else {
					result = cdna_change + " " + amino_acid_change;
	
				}
				result = Formater.translateGene(result);
			} else {
				result = "";
			}
		}
		return result;
	}

	public Map<String, Object> getGeneSubsetMap(){
		Map<String, Object> geneSubset = new HashMap<>();
		geneSubset.put("geneName", this.getGene_name());
		geneSubset.put("result", this.getResult());
		geneSubset.put("mutantAllele", this.getApproximate_mutatnt_allele_frequency());
		geneSubset.put("geneVariantId", this.getGene_varient_id());
		return geneSubset;
	}
	
	public static void adjustBaseline(List<GeneSubsetTumour> gstl, List<TumourNgs> tngsl) {
		Map<Integer,Integer> genepanelidmap = new HashMap<Integer,Integer>();
		for(TumourNgs  tngs: tngsl) {
			genepanelidmap.put(tngs.getMeasurement_gene_panel_id(), tngs.getBaseline_number());
		}
		for(GeneSubsetTumour gst : gstl) {
			gst.setBaseline_number(genepanelidmap.get(gst.getMeasurement_gene_panel_id()));
		}
		
	}
	
}
