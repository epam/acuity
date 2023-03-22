package org.digitalecmt.etarget.support;

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

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.FMSample;
import org.digitalecmt.etarget.dbentities.GeneName;
import org.digitalecmt.etarget.dbentities.GeneSubsetExt;
import org.digitalecmt.etarget.dbentities.GeneSubsetTumour;
import org.digitalecmt.etarget.dbentities.IHCReport;
import org.digitalecmt.etarget.dbentities.NgsLibExt;
import org.digitalecmt.etarget.dbentities.PDXCDXSearch;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.Search;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.digitalecmt.etarget.dbentities.TumourNgs;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;
import org.digitalecmt.etarget.rest.FileAccess;

public class Formater {
	private static final Logger log = Logger.getLogger(Formater.class.getName());
	static Map<String, String> convertAminoCode;
	static {
		convertAminoCode = new HashMap<String, String>();
		convertAminoCode.put("Gly", "G");
		convertAminoCode.put("Ala", "A");
		convertAminoCode.put("Leu", "L");
		convertAminoCode.put("Met", "M");
		convertAminoCode.put("Phe", "F");
		convertAminoCode.put("Trp", "W");
		convertAminoCode.put("Lys", "K");
		convertAminoCode.put("Gln", "Q");
		convertAminoCode.put("Glu", "E");
		convertAminoCode.put("Ser", "S");
		convertAminoCode.put("Pro", "P");
		convertAminoCode.put("Val", "V");
		convertAminoCode.put("Ile", "I");
		convertAminoCode.put("Cys", "C");
		convertAminoCode.put("Tyr", "Y");
		convertAminoCode.put("His", "H");
		convertAminoCode.put("Arg", "R");
		convertAminoCode.put("Asn", "N");
		convertAminoCode.put("Asp", "D");
		convertAminoCode.put("Thr", "T");
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNgsLibMap(List<NgsLibExt> nll){
		Map<String,Map<String, Object>> baselineMap;
		Map<String,Object> baselineInd;
		Map<String,Map<String,Map<String,Object>>> runMap;
		Map<String,Map<String,Object>> runInd;
		Map<String,Object> versInd;
		
		Map<String,Object> nlm = new HashMap<>();
		
		for(NgsLibExt nl : nll) {
			if(nlm.containsKey("baseline")) {
				baselineMap=(Map<String, Map<String, Object>>) nlm.get("baseline");
			} else {
				baselineMap=new HashMap<>();
				nlm.put("baseline", baselineMap);
			}
			if(baselineMap.containsKey(Integer.toString(nl.getBaseline_number()))) {
				baselineInd= baselineMap.get(Integer.toString(nl.getBaseline_number()));
			} else {
				baselineInd=new HashMap<>();
				Map<String,String> specimen=new HashMap<>();
				specimen.put("specimenDate", nl.getSpecimenDateFormatted1());
				specimen.put("specimenID", nl.getSpecimen_id());
				baselineInd.put("specimen", specimen);
				baselineMap.put(Integer.toString(nl.getBaseline_number()), baselineInd);
			}
			if(baselineInd.containsKey("runs")) {
				runMap=(Map<String, Map<String, Map<String,Object>>>) baselineInd.get("runs");
			} else {
				runMap=new HashMap<>();
				baselineInd.put("runs", runMap);
			}
			int version=1;
			if(nl.getRun_number()!=null) {
				if(runMap.containsKey(Integer.toString(nl.getRun_number()))) {
					runInd=runMap.get(Integer.toString(nl.getRun_number()));
				} else {
					runInd=new HashMap<>();
					runMap.put(Integer.toString(nl.getRun_number()), runInd);
				}
					
					
				//find the last version
				while(runInd.containsKey(Integer.toString(version))) {
					version++;
				}
				versInd=nl.getNgsLibMap();
				versInd.put("geneSubset", new ArrayList<String>());
				versInd.put("panelName", nl.getPanel_name());
				versInd.put("tumourPanelName", nl.getTumourGenePanel());
				runInd.put(Integer.toString(version), versInd);
			}
		}
		return nlm;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTumourMap(List<TumourNgsExt> nll){
		Map<String,Map<String, Object>> baselineMap;
		Map<String,Object> baselineInd;
		Map<String,Map<String,Map<String,Object>>> runMap;
		Map<String,Map<String,Object>> runInd;
		Map<String,Object> versInd;
		
		Map<String,Object> nlm = new HashMap<>();
		
		for(TumourNgsExt nl : nll) {
			if(nlm.containsKey("baseline")) {
				baselineMap=(Map<String, Map<String, Object>>) nlm.get("baseline");
			} else {
				baselineMap=new HashMap<>();
				nlm.put("baseline", baselineMap);
			}
			if(baselineMap.containsKey(Integer.toString(nl.getBaseline_number()))) {
				baselineInd= baselineMap.get(Integer.toString(nl.getBaseline_number()));
			} else {
				baselineInd=new HashMap<>();
				Map<String,String> specimen=new HashMap<>();
				specimen.put("specimenDate", nl.getSpecimentDateFormatted());
				specimen.put("specimenID", nl.getSpecimen_id().toString());
				specimen.put("tumourId", nl.getTumour_id());
				if(FileAccess.exists(nl.getTarget_id()+"_"+nl.getTumour_id()+".pdf")) {
					specimen.put("pdfReportOther", nl.getTarget_id()+"_"+nl.getTumour_id()+".pdf");
				}
				baselineInd.put("specimen", specimen);
				baselineMap.put(Integer.toString(nl.getBaseline_number()), baselineInd);
			}
			if(baselineInd.containsKey("runs")) {
				runMap=(Map<String, Map<String, Map<String,Object>>>) baselineInd.get("runs");
			} else {
				runMap=new HashMap<>();
				baselineInd.put("runs", runMap);
			}
			int version=1;
//			if(nl.getRun_number()!=null) {
				if(runMap.containsKey(Integer.toString(nl.getRun_number()==null?1:nl.getRun_number()))) {
					runInd=runMap.get(Integer.toString(nl.getRun_number()==null?1:nl.getRun_number()));
				} else {
					runInd=new HashMap<>();
					runMap.put(Integer.toString(nl.getRun_number()==null?1:nl.getRun_number()), runInd);
				}
					
					
				//find the last version
				while(runInd.containsKey(Integer.toString(version))) {
					version++;
				}
				versInd=nl.getTumourMap();
				runInd.put(Integer.toString(version), versInd);
//			}
		}
		return nlm;
	}
	
	public static List<String> getGeneNameList(List<GeneName> gnl){
		List<String> geneNames = new ArrayList<String>();
		for(GeneName gn : gnl) {
			geneNames.add(gn.getGeneName());
		}
		return geneNames;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> addTumourGeneSubset(List<GeneSubsetTumour> generesults, Map<String,Object> nlm){
		Map<String,Map<String, Object>> baselineMap;
		Map<String,Object> baselineInd;
		Map<String,Map<String,Object>> runMap;
		Map<String,Object> runInd=null;
		Map<String,Object>verInd=null;
		List<Map<String,Object>> geneSubset;
		if(nlm.containsKey("baseline")) {
			baselineMap=(Map<String, Map<String, Object>>) nlm.get("baseline");
		} else {
			baselineMap=new HashMap<>();
			nlm.put("baseline", baselineMap);
		}
		for(GeneSubsetTumour gse : generesults) {
			if(baselineMap.containsKey(Integer.toString(gse.getBaseline_number()))) {
				baselineInd= baselineMap.get(Integer.toString(gse.getBaseline_number()));
			} else {
				baselineInd=new HashMap<>();
				baselineMap.put(Integer.toString(gse.getBaseline_number()), baselineInd);
			}
			if(baselineInd.containsKey("runs")) {
				runMap=(Map<String, Map<String, Object>>) baselineInd.get("runs");
			} else {
				runMap=new HashMap<>();
				baselineInd.put("runs", runMap);
			}
			
			int version=1;
			
			if(gse.getRun_number()!=null) {
				if(runMap.containsKey(Integer.toString(gse.getRun_number()))) {
					runInd=runMap.get(Integer.toString(gse.getRun_number()));
					while(runInd.containsKey(Integer.toString(version))) {
						verInd = (Map<String,Object>) runInd.get(Integer.toString(version));
						if(verInd.containsKey("measurement_gene_panel_id")) {
							if(Integer.toString((Integer)verInd.get("measurement_gene_panel_id")).compareTo(Integer.toString(gse.getMeasurement_gene_panel_id()))==0) {
								//found correct version for this gene_penel
								break;
							}
						}
						version++;
						verInd=null;
					}
					
				} if(verInd==null) {
					verInd=new HashMap<>();
					runInd.put(Integer.toString(version),verInd);
				}
			} else {
				runInd=new HashMap<>();
				runMap.put("1", runInd); 
				verInd=new HashMap<>();
				runInd.put(Integer.toString(1),verInd);
				continue;
			}
			
			
			
			verInd.put("showNilResult", "false");
			if(verInd.containsKey("geneSubset")) {
				geneSubset = (List<Map<String, Object>>) verInd.get("geneSubset");
			} else {
				geneSubset = new ArrayList<>();
				verInd.put("geneSubset", geneSubset);
			}			
			geneSubset.add(gse.getGeneSubsetMap());
			verInd.replace("noResults", "");
			verInd.replace("noResultsReport", Boolean.FALSE);
			
		}	
		return nlm;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addGeneSubset(List<GeneSubsetExt> generesults, Map<String,Object> nlm){
		Map<String,Map<String, Object>> baselineMap;
		Map<String,Object> baselineInd;
		Map<String,Map<String,Object>> runMap;
		Map<String,Object> runInd=null;
		Map<String,Object>verInd=null;
		List<Map<String,Object>> geneSubset;
		if(nlm.containsKey("baseline")) {
			baselineMap=(Map<String, Map<String, Object>>) nlm.get("baseline");
		} else {
			baselineMap=new HashMap<>();
			nlm.put("baseline", baselineMap);
		}
		for(GeneSubsetExt gse : generesults) {
			if(baselineMap.containsKey(Integer.toString(gse.getBaseline_number()))) {
				baselineInd= baselineMap.get(Integer.toString(gse.getBaseline_number()));
			} else {
				baselineInd=new HashMap<>();
				baselineMap.put(Integer.toString(gse.getBaseline_number()), baselineInd);
			}
			if(baselineInd.containsKey("runs")) {
				runMap=(Map<String, Map<String, Object>>) baselineInd.get("runs");
			} else {
				runMap=new HashMap<>();
				baselineInd.put("runs", runMap);
			}
			
			int version=1;
			
			if(gse.getRun_number()!=null) {
				if(runMap.containsKey(Integer.toString(gse.getRun_number()))) {
					runInd=runMap.get(Integer.toString(gse.getRun_number()));
					while(runInd.containsKey(Integer.toString(version))) {
						verInd = (Map<String,Object>) runInd.get(Integer.toString(version));
						if(verInd.containsKey("ngsLib")) {
							Map<String,String> ngsLib=(Map<String,String>)verInd.get("ngsLib");
							if(ngsLib.containsKey("measurement_gene_panel_id")) {
								if(ngsLib.get("measurement_gene_panel_id").compareTo(Integer.toString(gse.getMeasurement_gene_panel_id()))==0) {
									//found correct version for this gene_penel
									break;
								}
							}
							
						}
						version++;
						verInd=null;
					}
					
				} 
				if(verInd==null) {
					if(runInd==null) {
						runInd=new HashMap<>();
						runMap.put("1", runInd);
					}
					verInd=new HashMap<>();
					runInd.put(Integer.toString(version),verInd);
				}
			} else {
				runInd=new HashMap<>();
				runMap.put("1", runInd); 
				verInd=new HashMap<>();
				runInd.put(Integer.toString(1),verInd);
				continue;
			}
			
			
			
			verInd.put("showNilResult", "false");
			if(verInd.containsKey("geneSubset")) {
				geneSubset = (List<Map<String, Object>>) verInd.get("geneSubset");
			} else {
				geneSubset = new ArrayList<>();
				verInd.put("geneSubset", geneSubset);
			}			
			geneSubset.add(gse.getGeneSubsetMap());
			
		}	
		return nlm;
	}
	
	public static Map<String,String> formatCTDNASummery(GeneSubsetExt gs){
		Map<String,String> result = new HashMap<>();
		result.put("source", "ctDNA");
		result.put("type", "Short Variant");
		result.put("gene", gs.getGene_name());
		result.put("description", gs.getGeneResultPure());
		result.put("newlySelected", Boolean.toString(gs.getIsNew()));
		result.put("geneVarientID", Integer.toString(gs.getGene_varient_id()));
		result.put("timepoint", "T"+gs.getBaseline_number());
		return result;
	}
	
	public static Map<String,String> formatTumourNgsSummery(TumourNgsExt tngs){
		Map<String,String> result = new HashMap<>();
		result.put("source", "Tumour NGS");
		result.put("type", "Short Variant");
		result.put("gene", tngs.getGene_name());
		result.put("description", tngs.getGeneResultPure());
		result.put("newlySelected", Boolean.toString(false));
		result.put("geneVarientID", Integer.toString(tngs.getGene_varient_id()));
		result.put("timepoint", "Bx"+tngs.getBaseline_number());
		return result;
	}
	
	public static Map<String,String> formatShortVariantSummery(ShortVariant shortvariant, String source){
		Map<String,String> result = new HashMap<>();
		result.put("source", source);
		result.put("type", "Short Variant");
		result.put("gene", shortvariant.getGene_name());
		result.put("description", shortvariant.getResult());
		result.put("newlySelected", Boolean.toString(false));
		result.put("geneVarientID", Integer.toString(shortvariant.getMeasurement_gene_variant_id()));
		if(source.toLowerCase().contains("tumour")) {
			result.put("timepoint", "Bx" +shortvariant.getBaseline());
		}
		else {
			result.put("timepoint","T"+shortvariant.getBaseline());
		}
		return result;
	}
	
	public static Map<String,String> formatCopyNumberAlterationSummery(CopyNumberAlteration cna, String source){
		Map<String,String> result = new HashMap<>();
		result.put("source", source);
		result.put("type", "Copy Number Alteration");
		result.put("gene", cna.getGene_name());
		result.put("description", cna.getCna_type());
		result.put("newlySelected", Boolean.toString(false));
		result.put("geneVarientID", Integer.toString(cna.getMeasurement_gene_variant_id()));
		if(source.toLowerCase().contains("tumour")) {
			result.put("timepoint", "Bx" +cna.getBaseline());
		}
		else {
			result.put("timepoint", "T"+cna.getBaseline());
		}
		return result;
	}
	
	public static Map<String,String> formatRearrangementSummery(Rearrangement rarr, String source){
		Map<String,String> result = new HashMap<>();
		result.put("source", source);
		result.put("type", "Rearrangement");
		result.put("rearrType", rarr.getRearr_type());
		result.put("gene", rarr.getRearr_gene_1_name()+" - " +rarr.getRearr_gene_2_name());
		result.put("description", rarr.getRearr_description());
		result.put("newlySelected", Boolean.toString(false));
		result.put("geneVarientID", Integer.toString(rarr.getMeasurement_gene_variant_id()));
		if(source.toLowerCase().contains("tumour")) {
			result.put("timepoint", "Bx" +rarr.getBaseline());
		}
		else {
			result.put("timepoint", "T"+rarr.getBaseline());
		}
		return result;
	}
	
		
	public static Map<String,Object> formatCTDNA(GeneSubsetExt gs){
	    
		Map<String, Object> result = new HashMap<String, Object>();
		String gsresult=gs.getGeneResult();
		if(gsresult!=null) gsresult=gsresult.replaceAll("<strong>", "").replaceAll("</strong>", "");
		result.put("geneVarientID", gs.getGene_varient_id());
		result.put("geneName", gs.getGene_name());
		result.put("result", gsresult);
		result.put("cfdnaFrequency", String.format("%.1f", gs.getVariant_allele_frequency()));
		result.put("cfdnaReads", gs.getRead_depth());
		result.put("germlineFrequency", String.format("%.1f", gs.getGermline_frequency()));
		result.put("mutationType", gs.getFunctional_effect());
		result.put("highConfidence", gs.getHigh_confidence());
		result.put("specimenDate", gs.getSpecimenDateFormatted1());
		result.put("newlySelected", gs.getIsNew());
		result.put("baseline", "ctDNA " +"T"+gs.getBaseline_number());
		return result;
	}

	public static Map<String, Object> formatTumourNgs(TumourNgsExt tngs) {
		Map<String,Object> result = new HashMap<String,Object>();
		String tngsresult=tngs.getGeneResult();
		if(tngsresult!=null) tngsresult=tngsresult.replaceAll("<strong>", "").replaceAll("</strong>", "");
		result.put("geneVarientID", tngs.getGene_varient_id());
		result.put("geneName", tngs.getGene_name());
		result.put("result", tngsresult);
		result.put("mutantAllele", tngs.getApproximate_mutatnt_allele_frequency());
		result.put("specimenDate", tngs.getSpecimentDateFormatted());
		result.put("baseline", "NGS Bx"+tngs.getBaseline_number());
		return result;
	}
	
	public static Map<String, String> formatShortVariant(ShortVariant shortvariant) {
		Map<String,String> sv = new HashMap<>();
	    sv.put("gene_variant_id", shortvariant.getMeasurement_gene_variant_id().toString());
		sv.put("geneName",	shortvariant.getGene_name());
		sv.put("variant_allele_frequency", String.format("%.2f", shortvariant.getVariant_allele_frequency()));
		sv.put("read_depth", shortvariant.getRead_depth().toString());
		sv.put("transcript", shortvariant.getTranscript()==null?"-":shortvariant.getTranscript());
		sv.put("result", shortvariant.getResult());
		sv.put("is_significant", shortvariant.isSignificant().toString());
		sv.put("variation_type", shortvariant.getVariant_concept_id());
		sv.put("status", shortvariant.getStatus()==null?"-":shortvariant.getStatus());
		sv.put("functional_effect", shortvariant.getFunctional_effect());
		sv.put("subclonal", shortvariant.getSubclonal()==null?"-":shortvariant.getSubclonal());
		sv.put("position", shortvariant.getPosition());
		return sv;
	}
	
	public static Map<String,String> formatCopyNumberAlteration(CopyNumberAlteration cna) {
		Map<String,String> cn = new HashMap<>();
		cn.put("gene_variant_id", cna.getMeasurement_gene_variant_id().toString());
		cn.put("is_significant", cna.isSignificant().toString());
		cn.put("geneName", cna.getGene_name());
		cn.put("copy_number", cna.getCopy_number().toString());
		cn.put("exons", cna.getExons()==null?"-":cna.getExons());
		cn.put("ratio", cna.getCna_ratio()==null?"-":String.format("%.2f", cna.getCna_ratio()));
		cn.put("type", cna.getCna_type());
		cn.put("variation_type", cna.getVariant_concept_id());
		cn.put("status", cna.getStatus()==null?"-":cna.getStatus());
		cn.put("equivocal", cna.getEquivocal()==null?"-":cna.getEquivocal());
		return cn;
	}
	
	public static Map<String,String> formatRearrangement(Rearrangement rarr) {
		Map<String,String> ra = new HashMap<>();
		ra.put("gene_variant_id", rarr.getMeasurement_gene_variant_id().toString());
		ra.put("is_significant", rarr.isSignificant().toString()); 
		ra.put("gene1", rarr.getRearr_gene_1_name());
		ra.put("gene2", rarr.getRearr_gene_2_name());
		ra.put("description", rarr.getRearr_description()==null?"-":rarr.getRearr_description());
		ra.put("inframe", rarr.getRearr_in_frame()==null?"-":rarr.getRearr_in_frame());
		ra.put("pos1", rarr.getRearr_pos1());
		ra.put("pos2", rarr.getRearr_pos2());
		ra.put("number_of_reads", rarr.getRearr_number_of_reads()==null?"-":rarr.getRearr_number_of_reads().toString());
		ra.put("variant_allele_frequency", rarr.getVariant_allele_frequency()==null?"-" : String.format("%.2f", rarr.getVariant_allele_frequency()));
		ra.put("variation_type", rarr.getVariant_type());
		ra.put("status", rarr.getStatus()==null?"-":rarr.getStatus());
		ra.put("rearrangement_type", rarr.getRearr_type());
		return ra;
	}
	
	public static Map<String, Map<String,Object>> formatFM(List<FMSample> samples, List<ShortVariant> shortvariants, 
			List<CopyNumberAlteration> copynumberalterations, List<Rearrangement> rearrangements, String type, String code) {
		Map<String,Map<String,Object>> result = new HashMap<>();
		Integer i=0;
		int run=1;
		Map<String, Integer> knownsamples= new HashMap<String,Integer>();
		for(FMSample sample : samples) {
			Map<String,Object> s = new HashMap<>();
			Map<String,Object> specimen = new HashMap<String,Object>();
			String key;
//			if(type.equals("tumour")) {
//				key=sample.getTumour_id();
//			} else {
				key=sample.getBaseline_number().toString();
//			}
			
			if(!knownsamples.containsKey(key)) {
				i++;
				knownsamples.put(key,i);
				specimen.put("specimenDate", sample.getSpecimentDateFormatted());
				specimen.put("tumourID", sample.getTumour_id());
				s.put("specimen", specimen);
//				log.info("gene_panel (2) key:" + key +" panel_id "+ sample.getMeasurement_gene_panel_id() + " specimen_id " + sample.getSpecimen_id() + " timepoint " + sample.getBaseline_number());;
				run=1;
			} else {
//				log.info("key " + key);
//				log.info("baseline " + knownsamples.get(key));
//				log.info("result size " + result.size());
//				log.info("contains key? " +result.containsKey(Integer.toString(knownsamples.get(key))));
//				log.info("keys " + result.keySet());
//				log.info("gene_panel " + sample.getMeasurement_gene_panel_id());
				s = result.get(Integer.toString(knownsamples.get(key)));
//				log.info("s " + s);
				run++;
			}
			Map<String,Object> genepanel = new HashMap<String,Object>();
			if(type.equals("tumour")) {
				String filename=sample.getPreclin_id()+code+".pdf";
				filename=filename.replaceAll("/","");
				if(FileAccess.exists(filename)) {
					genepanel.put("report", filename);
				}
				List<String> versions=FileAccess.getAllFiles(sample.getPreclin_id()+code,".pdf");
				if(versions!=null && versions.size()>0) {
					genepanel.put("versions", versions);
				}
			} else {
				String filename;
				filename=FileAccess.getFile(sample.getTarget_id(),"T"+sample.getBaseline_number()+code+".pdf");
				log.info("add blood report: " + sample.getBaseline_number() + " baselinelist " + "T"+sample.getBaseline_number()+ " " + code);
				log.info(filename);
				if(filename!=null && FileAccess.exists(filename)) {
					genepanel.put("report", filename);
				}
				if(filename!=null) {
					List<String> versions=FileAccess.getAllFiles(filename.substring(0, filename.indexOf('.')),".pdf");
					if(versions!=null && versions.size()>0) {
						genepanel.put("versions", versions);
					}
				}
			}
			if(sample.getTmb_status()==null) {
				genepanel.put("tmb_status", "Not reported");
			} else {
				genepanel.put("tmb_status", sample.getTmb_status());
			}
			if(sample.getTmb_score()==null) {
				genepanel.put("tmb_score", "");
			} else {
				genepanel.put("tmb_score", String.format("%.2f", sample.getTmb_score()));
			}
			genepanel.put("tmb_unit", sample.getTmb_unit());
			genepanel.put("microsatellite_score", String.format("%.2f", (sample.getMicrosatellite_instability_score()==null)?0f:sample.getMicrosatellite_instability_score()));
			if(sample.getMicrosatellite_instability_status()==null) {
				genepanel.put("microsatellite_status", "Not reported");
			} else {
				genepanel.put("microsatellite_status", sample.getMicrosatellite_instability_status());
			}
			genepanel.put("mean_exon_depth", sample.getMean_exon_depth()==null?"Not reported":sample.getMean_exon_depth_int());
			genepanel.put("panel_id", sample.getMeasurement_gene_panel_id());
			if(sample.getTumour_fraction_score()==null) {
				genepanel.put("tumour_fraction_score", "Not reported");
			} else {
				genepanel.put("tumour_fraction_score", sample.getTumour_fraction_score());
			}
			genepanel.put("tumour_fraction_unit", sample.getTumour_fraction_unit());
			genepanel.put("percent_exons_100x", String.format("%.2f", (sample.getPercent_exons_100x()==null)?0f:sample.getPercent_exons_100x()));
			log.info("key: " + key + " tmb_score " + sample.getTmb_score());
			log.info("s " + s);
			log.info("run " + run);
			log.info("genepanel " + genepanel);
			s.put(Integer.toString(run),genepanel);
			
			//short variants
			List<Map<String,String>> shortvariantlist = new ArrayList<>();
			if(sample.getMeasurement_gene_panel_id()!=null) {
				for(ShortVariant shortvariant : shortvariants) {
//					log.info("measurement_gene_panel_id " + shortvariant.getMeasurement_gene_panel_id() + " " + sample.getMeasurement_gene_panel_id());
					if(shortvariant.getMeasurement_gene_panel_id().equals(sample.getMeasurement_gene_panel_id())) {
//						if(type.equals("tumour")) {
//							shortvariant.setBaseline(i);
//						}
						Map<String,String> sv = Formater.formatShortVariant(shortvariant);
						shortvariantlist.add(sv);
					}
				}
			}
			genepanel.put("short_variants", shortvariantlist);
			
			//copy number alterations
			List<Map<String,String>> cnal = new ArrayList<>();
			if(sample.getMeasurement_gene_panel_id()!=null) {
				for(CopyNumberAlteration cna : copynumberalterations) {
					if(cna.getMeasurement_gene_panel_id().equals(sample.getMeasurement_gene_panel_id())) {
//						if(type.equals("tumour")) {
//							cna.setBaseline(i);
//						}
						Map<String,String> cn = Formater.formatCopyNumberAlteration(cna);
						cnal.add(cn);
					}
				}
			}
			genepanel.put("copy_number_alterations", cnal);
			
			// rearragements
			List<Map<String,String>> rarrl = new ArrayList<>();
			if(sample.getMeasurement_gene_panel_id()!=null) {
				for(Rearrangement rarr : rearrangements) {
					if(rarr.getMeasurement_gene_panel_id().equals(sample.getMeasurement_gene_panel_id())) {
//						if(type.equals("tumour")) {
//							rarr.setBaseline(i);
//						}
						Map<String,String> ra = Formater.formatRearrangement(rarr);
						rarrl.add(ra);
					}
				}				
			}
			genepanel.put("rearrangements", rarrl);
			log.info("add key to result " + i + " " + type);
//			if(type.equals("tumour")) {
//				result.put(Integer.toString(i), s);
//			} else {
				result.put(key, s);
//			}
		}
		return result;
	}

	public static List<Map<String, String>> getSearchResults(List<Search> search) {
		List<Map<String,String>> results = new ArrayList<>();
		for(Search s:search) {
			HashMap<String,String> item = new HashMap<>();
			item.put("conditionName", s.getCondition_name());
			item.put("conditionSubtype", s.getSubtype_name());
			item.put("geneName", s.getGene_name());
			item.put("unknownSignificant", s.getUnknown_significant()>0?"true":"false");
			if(s.getVariant_type().equals("short_variant")) {
				item.put("result", "SNV: "+getGeneResult(s.getAmino_acid_change(),s.getCdna_change()));
			} else if (s.getVariant_type().equals("rearrangement")) {
				item.put("rearr_gene1", s.getRearr_gene1());
				item.put("rearr_gene2", s.getRearr_gene2());
				item.put("result", "Rearr: "+s.getRearr_description()+" "+s.getRearr_gene1() + " "+s.getRearr_gene2());
				item.put("geneName", s.getRearr_gene1() + " "+s.getRearr_gene2());
			} else if (s.getVariant_type().equals("copy_number_alteration")) {
				item.put("result", "CNA: " + s.getCna_type());
			}
			item.put("targetID", s.getTarget_id());
			item.put("personID", String.valueOf(s.getPerson_id()));
			results.add(item);
		}
		return results;
	}
	

	private static String getGeneResult(String amino_acid_change, String cdna_change) {
		String geneResult=null;
		if (amino_acid_change != null) {
			if (amino_acid_change.contains("(")) {
				String[] am1 = amino_acid_change.split("\\(");
				String[] am2 = am1[1].split("\\)");
				geneResult = cdna_change + " " + am1[0] + "(<strong>" + am2[0] + "</strong>)";
			} else {
				geneResult = cdna_change + " " + amino_acid_change;
			}
		} else {
			geneResult = "";
		}
		geneResult = translateGene(geneResult);
		return geneResult;
	}
	
	public static String translateGene(String geneResult) {
		// Iterate over the Map and replace all instances in the string
		for (Map.Entry<String, String> entry : convertAminoCode.entrySet()) {
			String caseInsensitiveKey = "(?i)" + entry.getKey();
			geneResult = geneResult.replaceAll(caseInsensitiveKey, entry.getValue());
		}
		// Return the result
		return geneResult;
	}

	public static List<Map<String, String>> getPDXCDXResults(List<PDXCDXSearch> search) {
		List<Map<String,String>> results = new ArrayList<>();
		for(PDXCDXSearch s:search) {
			HashMap<String,String> item = new HashMap<>();
			item.put("targetID", s.getTarget_id());
			item.put("personID", String.valueOf(s.getPerson_id()));
			item.put("pdxcdx", s.getPDXCDX());
			results.add(item);
		}
		return results;
	}

	public static Map<String, Map<String, Map<String,String>>> formatIHCReport(List<IHCReport> ihcReport,
			List<TumourNgs> specimen) {
		Map<String,Map<String,Map<String,String>>> result = new HashMap<>();
		Map<String,Map<String,String>> versionMap = null;
		NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		formatter.setRoundingMode(RoundingMode.HALF_UP); 
		for(IHCReport r : ihcReport) {
			log.info("IHC " + r.getIhc_report_id());
			Integer specimen_id = r.getSpecimen_id();
			Map<String,String> report = new HashMap<>();
			String timepoint = null;
			Integer version = 1;
			for(TumourNgs s : specimen) {
				if(s.getSpecimen_id().equals(specimen_id)) {
					timepoint=""+s.getBaseline_number();
				}
			}
			if(timepoint == null) {
				timepoint ="1";
			}
			if(result.containsKey(timepoint)) {
				versionMap=result.get(timepoint);
			} else {
				versionMap = new HashMap<>();
			}
			
			log.info("IHC version map " + versionMap.size());
			if(versionMap.containsKey(version.toString())) {
				version++;
			}
			
			versionMap.put(version.toString(),report);
			result.put(timepoint, versionMap);
			report.put("personID", Integer.toString(r.getPerson_id()));
			report.put("sampleDate", r.getSpecimen_date_formated());
			report.put("sampleReceivedDate", r.getSample_received_date_formatted());
			if(r.getIhc_report_id()!=null) {
				report.put("reportDate", r.getReport_date_formatted());
				report.put("pdl1_tps", r.getPdl1_tps());
				report.put("estimated_results", r.getEstimated_results() + ((r.getEstimated_results().toLowerCase().startsWith("no result"))?"":" %"));
				report.put("cd3_total_tissue_area", r.getCd3_total_tissue_area()==null?"No result":formatter.format(r.getCd3_total_tissue_area()));
				report.put("cd3_instratumoural", r.getCd3_intratumoural()==null?"No result":formatter.format(r.getCd3_intratumoural()));
				report.put("cd3_instrastromal", r.getCd3_intrastromal()==null?"No result":formatter.format(r.getCd3_intrastromal()));
				report.put("cd8_total_tissue_area", r.getCd8_total_tissue_area()==null?"No result":formatter.format(r.getCd8_total_tissue_area()));
				report.put("cd8_instratumoural", r.getCd8_intratumoural()==null?"No result":formatter.format(r.getCd8_intratumoural()));
				report.put("cd8_instrastromal", r.getCd8_intrastromal()==null?"No result":formatter.format(r.getCd8_intrastromal()));
				report.put("comments", r.getComments());
				report.put("pre_clin_id", r.getPreclin_id());
				String filecd3=r.getPreclin_id()+"_CD3.jpg";
				String filecd8=r.getPreclin_id()+"_CD8.jpg";
				if(FileAccess.exists(filecd3)) {
					report.put("cd3_expression", filecd3);
				}
				if(FileAccess.exists(filecd8)) {
					report.put("cd8_expression", filecd8);
				}
			} else {
				report.put("message", "Results not available.");
			}
		}
		Set<String> keys = result.keySet();
		for(String key : keys) {
			Map<String,Map<String,String>> timepoint = result.get(key);
			log.info(timepoint.keySet().toString());
			Map<String,String> x = timepoint.get("1");
			if(x==null) {
				continue;
			}
			String prelcin_id= timepoint.get("1").get("pre_clin_id");
			List<String> pict_CD3=FileAccess.getAllFiles(prelcin_id+"_CD3", ".jpg");
			List<String> pict_CD8=FileAccess.getAllFiles(prelcin_id+"_CD8", ".jpg");
			List<String> pict_PDL1=FileAccess.getAllFiles(prelcin_id+"_PDL1", ".jpg");
			Set<String> versions = timepoint.keySet();
			for(String version : versions) {
				Map<String,String> data = timepoint.get(version);

				int v = Integer.parseInt(version);
				if(pict_CD3.size()>0) {
					data.put("cd3_expression", pict_CD3.get(v<pict_CD3.size()?v:pict_CD3.size()-1));
				} 
				if(pict_CD8.size()>0) {
					data.put("cd8_expression", pict_CD8.get(v<pict_CD8.size()?v:pict_CD8.size()-1));
				}
				if(pict_PDL1.size()>0) {
					data.put("pdl1_expression", pict_PDL1.get(v<pict_PDL1.size()?v:pict_PDL1.size()-1));
				}
			}
			
			
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public static void cleanTumourMap(Map<String, Object> tmap) {
		Map<String,Object> baselines = (Map<String, Object>) tmap.get("baseline");
		if(baselines==null) {
			return;
		}
		Integer baseline=2;
		Map<String,Object> runs=null;
		while((runs = (Map<String, Object>) baselines.get(baseline.toString()))!=null) {
			Map<String,Object> runs_intern=(Map<String, Object>)runs.get("runs");
			if(runs_intern==null) {
				baseline++;
				continue;
			}
			Map<String,Object> first=(Map<String, Object>)runs_intern.get("1");
			if(first==null) {
				baseline++;
				continue;
			}
			Map<String,Object> version=(Map<String, Object>)first.get("1");
			if(version==null) {
				baseline++;
				continue;
			}
			String noResults= (String)version.get("noResults");
			if(noResults.compareTo("Results not available yet.")==0) {
				log.info("delete Baseline " + baseline);
				baselines.remove(baseline.toString());
			}
			baseline++;
			
		}
	}

	public static void cleanFMTumourMap(Map<String, Map<String, Object>> tumourMap) {
		Integer baseline=2;
		Map<String,Object> runs=null;
		while((runs = tumourMap.get(baseline.toString()))!=null) {
			@SuppressWarnings("unchecked")
			Map<String,Object> run=(Map<String, Object>)runs.get("1");
			if(run==null) {
				baseline++;
				continue;
			}
			log.info(run.keySet().toString());
			Boolean hasReport = run.containsKey("versions");
			if(hasReport==Boolean.FALSE && run.get("panel_id")==null) {
				tumourMap.remove(baseline.toString());
			}
			baseline++;
					
		}
	}
	
	public static void cleanIHCTumour(Map<String,Map<String,Map<String,String>>> ihcReport) {
		Integer baseline=2;
		Map<String,Map<String,String>> report=null;
		while((report = ihcReport.get(baseline.toString()))!=null) {
			log.info(report.keySet().toString());
			Map<String,String> version=report.get("1");
			if(version==null) {
				baseline++;
				continue;
			}
			Boolean hasMessage = version.containsKey("message");
			if(hasMessage==Boolean.TRUE) {
				ihcReport.remove(baseline.toString());
			}
			baseline++;
					
		}
	}
}
