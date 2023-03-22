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
import java.util.logging.Logger;

import org.digitalecmt.etarget.rest.FileAccess;
import org.digitalecmt.etarget.support.Formater;

public class TumourNgsExt extends TumourNgs {
	
	private static final Logger log = Logger.getLogger(TumourNgsExt.class.getName());
	String geneResult = null;
	String geneResultPure = null;
	String specimentDateFormatted = null;
	String gdlRequestFormatted = null;
	Boolean analysisFailed = false;
	Boolean noMutationsFound = false;

	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	private TumourNgsExt(TumourNgs tngs) {
		super(tngs);
		if (this.getComments() != null) {
			if (this.getComments().equals("Analysis failed.")) {
				analysisFailed = true;
			}
			if (this.getComments().equals("No mutation identified.")) {
				noMutationsFound = true;
				this.setComments("No mutations identified.");
			}
		}
	}

	public static TumourNgsExt getTumourNgsExt(TumourNgs tngs) {
		return new TumourNgsExt(tngs);
	}

	public static List<TumourNgsExt> getTumourNgsExtList(List<TumourNgs> tngsl) {
		TumourNgsExt.adjustBaselines(tngsl);
		List<TumourNgsExt> results = new ArrayList<TumourNgsExt>();
		for (TumourNgs tngs : tngsl) {
			results.add(TumourNgsExt.getTumourNgsExt(tngs));
		}
		return results;
	}
	
	
	public static void adjustBaselines(List<TumourNgs> tngsl) {
//		Integer baselineNo=1;
//		HashMap<String, Integer> maping = new HashMap<>();
//		for(TumourNgs tngs : tngsl) {
//			if(maping.containsKey(tngs.getTumour_id())) {
//				tngs.setBaseline_number(maping.get(tngs.getTumour_id()));
//			} else {
//				tngs.setBaseline_number(baselineNo);
//				maping.put(tngs.getTumour_id(), baselineNo);
//				baselineNo++;
//			}
//		}
	}
	
	private void setGeneResult() {
		if (this.getAmino_acid_change() != null) {
			if (this.getAmino_acid_change().contains("(")) {
				String[] am1 = this.getAmino_acid_change().split("\\(");
				String[] am2 = am1[1].split("\\)");
				geneResult = this.getCdna_change() + " " + am1[0] + "(<strong>" + am2[0] + "</strong>)";
				geneResultPure = this.getCdna_change() + " " + am1[0] + "(" + am2[0] + ")";
			} else {
				geneResult = this.getCdna_change() + " " + this.getAmino_acid_change();
				geneResultPure = geneResult;
			}
		} else {
			geneResult = "";
			geneResultPure = "";
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

	public String getGdlRequestFormatted() {
		if (gdlRequestFormatted == null) {
			if (this.getDate_requested() != null) {
				gdlRequestFormatted = df.format(this.getDate_requested());
			} else {
				gdlRequestFormatted = "";
			}
		}
		return gdlRequestFormatted;
	}
	
	public Map<String,Object> getTumourMap(){
		Map<String,Object> results = new HashMap<>();
		results.put("coverage", this.getCoverage());
		boolean analysisFailed=false;
		boolean noMutationsFound=false;
		if(this.getComments()!=null) {
			if(this.getComments().equals("Analysis failed.")) {
				analysisFailed=true;
			} else if(this.getComments().equals("No mutations identified.")) {
				noMutationsFound=true;
			}
		}
		results.put("comments", this.getComments());
		results.put("analysisFailed", analysisFailed);
		results.put("noMutationsFound", noMutationsFound);
		results.put("unknownSignificance", this.getUnknown_significance());
		results.put("pathLabRef", this.getPath_lab_ref());
		if(this.getCoverage()==null || this.getCoverage()==0) {
			if(noMutationsFound) {
				results.put("coverage", "See PDF");
			} else if(analysisFailed) {
				results.put("coverage", "Not Reported");
			} else if(this.getFilename()!=null) {
				results.put("coverage", "See PDF");
			} else {
				results.put("coverage", "");
			}
		} else {
			results.put("coverage", this.getCoverage().toString()+" %");
		}
		results.put("pdfReport", this.getFilename());
		log.info("Tumour file " + this.getPreclin_id()+".pdf");
		if(FileAccess.exists(this.getPreclin_id()+".pdf")) {
			log.info("file exists");
			results.put("pdfReportOther", this.getPreclin_id()+".pdf");
		} else {
			log.info("file does not exist");
		}
		
		if(this.getDate_requested()==null) {
			results.put("gdlRequest", "Not requested yet.");
		}else {
			results.put("gdlRequest", this.getGdlRequestFormatted());
		}
		results.put("measurement_gene_panel_id", this.getMeasurement_gene_panel_id());
		if(this.getFilename()!=null && this.getFilename().length()>0) {
			results.put("noResults", "No results. "+this.getComments()+" Please see report for details.");
			results.put("noResultsReport", Boolean.TRUE);
		} else {
			results.put("noResults", "Results not available yet.");
			results.put("noResultsReport", Boolean.FALSE);
		}
		return results;
	}

}
