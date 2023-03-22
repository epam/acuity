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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.logging.Logger;

import org.digitalecmt.etarget.support.Formater;

public class GeneSubsetExt extends GeneSubset implements Comparable<GeneSubsetExt>{
	
//	private static final Logger log = Logger.getLogger(GeneSubsetExt.class.getName());

	String geneResult = null;
	String geneResultPure = null;
	String specimenDateFormatted1 = null;
	Boolean isNew = false;

	private GeneSubsetExt(GeneSubset gs) {
		super(gs);
	}

	public static GeneSubsetExt getGeneSubsetExt(GeneSubset gs) {
		return new GeneSubsetExt(gs);
	}

	public static List<GeneSubsetExt> getGeneSubsetExtList(List<GeneSubset> gsl) {
		List<GeneSubsetExt> gensubsets = new ArrayList<GeneSubsetExt>();
		if(gsl == null) return gensubsets;
		for (GeneSubset gs : gsl) {
			gensubsets.add(GeneSubsetExt.getGeneSubsetExt(gs));
		}
		return gensubsets;
	}

	private void setGeneResult() {

		if (this.getAmino_acid_change() != null && this.getAmino_acid_change().contains("(")) {
			String[] am1 = this.getAmino_acid_change().split("\\(");
			String[] am2 = am1[1].split("\\)");
			geneResult = this.getCdna_change() + " " + am1[0] + "(<strong>" + am2[0] + "</strong>)";
			geneResultPure = this.getCdna_change() + " " + am1[0] + "(" + am2[0] + ")";
		} else {
			geneResult = this.getCdna_change() + " " + this.getAmino_acid_change();
			geneResultPure = geneResult;
		}
		geneResult = Formater.translateGene(geneResult);
		geneResultPure = Formater.translateGene(geneResultPure);
	}

	public String getGeneResult() {
		if (geneResult == null) {
			setGeneResult();
		}
		return geneResult;
	}

	public String getGeneResultPure() {
		if (geneResultPure == null) {
			setGeneResult();
		}
		return geneResultPure;
	}

	public String getSpecimenDateFormatted1() {
		if (specimenDateFormatted1 == null) {
			if (this.getSpecimen_date() != null) {
				DateFormat dfspecimenDate1 = new SimpleDateFormat("dd/MM/yyyy");
				specimenDateFormatted1 = dfspecimenDate1.format(this.getSpecimen_date());
			} else {
				specimenDateFormatted1 = "Sample Date unkown";
			}
		}
		return specimenDateFormatted1;
	}

	public Map<String, Object> getGeneSubsetMap() {
		Map<String, Object> geneSubset = new HashMap<>();
		geneSubset.put("geneName", this.getGene_name());
		geneSubset.put("specimenDate", this.getSpecimenDateFormatted1());
		geneSubset.put("geneVarientId", this.getGene_varient_id());
		geneSubset.put("result", this.getGeneResult());
		geneSubset.put("cfdnaFrequency", this.getVariant_allele_frequency());
		geneSubset.put("cfdnaReads", this.getRead_depth());
		geneSubset.put("germlineFrequency", this.getGermline_frequency());
		geneSubset.put("mutationType", this.getFunctional_effect());
		geneSubset.put("highConfidence", this.getHigh_confidence());
		geneSubset.put("isSpecificMutationInPanel", this.getIs_specific_mutation_in_panel());
		geneSubset.put("baseline", this.getBaseline_number());
		geneSubset.put("run", this.getRun_number());
		geneSubset.put("measurement_gene_panel_id", this.getMeasurement_gene_panel_id());
		geneSubset.put("cosmicURL", this.getCosmic_url());
//		geneSubset.put("newSelected", this.isNew);
		return geneSubset;
	}

	@Override
	public int compareTo(GeneSubsetExt o) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		if(this.getBaseline_number()< o.getBaseline_number()) return BEFORE;
		if(this.getBaseline_number()> o.getBaseline_number()) return AFTER;
		if(this.getRun_number()<o.getRun_number()) return BEFORE;
		if(this.getRun_number()>o.getRun_number()) return AFTER;
		if(this.getGene_name().compareTo(o.getGene_name())!=0) return this.getGene_name().compareTo(o.getGene_name());
		if(this.getGeneResult().compareTo(o.getGeneResult())!=0) return this.getGeneResult().compareTo(o.getGeneResult());
		if(this.getVariant_allele_frequency()<o.getVariant_allele_frequency()) return BEFORE;
		if(this.getVariant_allele_frequency()>o.getVariant_allele_frequency()) return AFTER;
		if(this.getRead_depth()<o.getRead_depth()) return BEFORE;
		if(this.getRead_depth()>o.getRead_depth()) return AFTER;
		if(this.getGermline_frequency()<o.getGermline_frequency()) return BEFORE;
		if(this.getGermline_frequency()>o.getGermline_frequency()) return AFTER;
		if(this.getFunctional_effect().compareTo(o.getFunctional_effect())!=0) return this.getFunctional_effect().compareTo(o.getFunctional_effect());
		if(this.getHigh_confidence().compareTo(o.getHigh_confidence())!=0) return this.getHigh_confidence().compareTo(o.getHigh_confidence());
		if(this.getIs_specific_mutation_in_panel().compareTo(o.getIs_specific_mutation_in_panel())!=0) return this.getIs_specific_mutation_in_panel().compareTo(o.getIs_specific_mutation_in_panel());
		return EQUAL;
	}
	
	public int compareToMin(GeneSubsetExt o) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		if(this.getBaseline_number()< o.getBaseline_number()) return BEFORE;
		if(this.getBaseline_number()> o.getBaseline_number()) return AFTER;
		if(this.getRun_number()<o.getRun_number()) return BEFORE;
		if(this.getRun_number()>o.getRun_number()) return AFTER;
		return EQUAL;
	}
	
	public int numberOfChanges(GeneSubsetExt o) {
		int changes=0;
		if(this.getGene_name().compareTo(o.getGene_name())!=0) {
			changes++;
		}
		if(this.getGeneResult().compareTo(o.getGeneResult())!=0) {
			changes++;
		}
		if(this.getVariant_allele_frequency().compareTo(o.getVariant_allele_frequency())!=0) {
			changes++;
		}
		if(this.getRead_depth().compareTo(o.getRead_depth())!=0) {
			changes++;
		}
		if(this.getGermline_frequency().compareTo(o.getGermline_frequency())!=0) {
			changes++;
		}
		if(this.getFunctional_effect().compareTo(o.getFunctional_effect())!=0) {
			changes++;
		}
		if(this.getHigh_confidence().compareTo(o.getHigh_confidence())!=0) {
			changes++;
		}
		if(this.getIs_specific_mutation_in_panel().compareTo(o.getIs_specific_mutation_in_panel())!=0) {
			changes++;
		}
//		if(this.getGene_name().compareTo("Gene24")==0) {
//			log.info(o.getGene_name() + " " + changes);
//		}
		return changes;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
}
