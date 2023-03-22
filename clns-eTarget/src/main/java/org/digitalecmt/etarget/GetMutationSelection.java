package org.digitalecmt.etarget;

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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.digitalecmt.etarget.config.ConfigDataSources;
import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.FoundationMedicineDAO;
import org.digitalecmt.etarget.dao.GeneSubsetDAO;
import org.digitalecmt.etarget.dao.MutationSelectionDAO;
import org.digitalecmt.etarget.dao.TumourNgsDAO;
import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.GeneSubset;
import org.digitalecmt.etarget.dbentities.GeneSubsetExt;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.SelectedGeneVariant;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;
import org.digitalecmt.etarget.support.Formater;

import com.google.gson.*;

public class GetMutationSelection extends API {
	private static final Logger log = Logger.getLogger(GetMutationSelection.class.getName());
	// Vars
	String personID;
	String userID;
	int personIDint;

	// Constructor
	public GetMutationSelection(String loggedInUserID, String dbPersonID) {
		userID = loggedInUserID;
		personID = dbPersonID;
		try {
			personIDint=Integer.parseInt(dbPersonID);
		} catch(NumberFormatException ex) {
			personIDint=-1;
		}
	}

	public String processRequest() {
		if (!super.isUserPermittedEndpoint(userID, "GetMutationSelection")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			Map<String,String> response = new HashMap<>();
	    	response.put("success", "false");
	    	response.put("error", "User not permitted to access: GetMutationSelection");
	    	return new Gson().toJson(response);
		} else {
			// Process the request
			try {
				//get configured sources
				List<ConfigDataSources> addSources = (List<ConfigDataSources>)this.getContext().getBean("configuredDataSources");
				
				// new version
//				List<Map<String,String>> allGenes = new ArrayList<Map<String,String>>();
				List<Map<String,Object>> ctDNA = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> tumourNGS = new ArrayList<Map<String,Object>>();
				Map<String,List<Map<String,String>>> fmTumour = new HashMap<>();
				List<Map<String,String>> fmTumourSV = new ArrayList<Map<String,String>>();
				List<Map<String,String>> fmTumourR = new ArrayList<Map<String,String>>();
				List<Map<String,String>> fmTumourCNA = new ArrayList<Map<String,String>>();
				Map<String,List<Map<String,String>>> fmBlood = new HashMap<>();
				List<Map<String,String>> fmBloodSV = new ArrayList<Map<String,String>>();
				List<Map<String,String>> fmBloodR = new ArrayList<Map<String,String>>();
				List<Map<String,String>> fmBloodCNA = new ArrayList<Map<String,String>>();
				Map<String,Map<String,List<Map<String,String>>>> addSourcesMap = new HashMap<>();
				for(ConfigDataSources cds : addSources) {
					List<Map<String,String>> sv = new ArrayList<Map<String,String>>();
					List<Map<String,String>> r = new ArrayList<Map<String,String>>();
					List<Map<String,String>> cna = new ArrayList<Map<String,String>>();
					Map<String,List<Map<String,String>>> newSource = new HashMap<>();
					newSource.put("shortVariant", sv);
					newSource.put("copyNumberAlteration", cna);
					newSource.put("rearrangement", r);
					addSourcesMap.put(cds.getFront_end_name(), newSource);
				}
				
				MutationSelectionDAO mutationSelectionDao = this.getContext().getBean(MutationSelectionDAO.class);
				GeneSubsetDAO genesubset = this.getContext().getBean(GeneSubsetDAO.class);
				TumourNgsDAO tumourNgs = this.getContext().getBean(TumourNgsDAO.class);
				FoundationMedicineDAO fm = this.getContext().getBean(FoundationMedicineDAO.class);
				List<SelectedGeneVariant> resultSet = mutationSelectionDao
						.getSelectedMutationsByPersonID(personIDint);
				for(SelectedGeneVariant sgv : resultSet) {
					if(sgv.getType().compareTo("CTDNA")==0) {
						GeneSubsetExt result =GeneSubsetExt.getGeneSubsetExt(genesubset.findGeneSubsetByGeneVarientID(personIDint, sgv.getMeasurement_gene_variant_id()));
						ctDNA.add(Formater.formatCTDNA(result));
					} else if(sgv.getType().compareTo("NGS")==0) {
						TumourNgsExt result = TumourNgsExt.getTumourNgsExt(tumourNgs.findTumourNgsByGeneVarientID(personIDint, sgv.getMeasurement_gene_variant_id()));
						tumourNGS.add(Formater.formatTumourNgs(result));
					} else if(sgv.getType().compareTo("FMTumourSV")==0) {
						ShortVariant sv = fm.getTumourShortVariantByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//						sv.setBaseline(fm.getTumourBaseline(sv.getMeasurement_gene_panel_id()));
						fmTumourSV.add(Formater.formatShortVariant(sv));
					} else if(sgv.getType().compareTo("FMTumourCNA")==0) {
						CopyNumberAlteration cna = fm.getTumourCopyNumberAlterationByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//						cna.setBaseline(fm.getTumourBaseline(cna.getMeasurement_gene_panel_id()));
						fmTumourCNA.add(Formater.formatCopyNumberAlteration(cna));
					} else if(sgv.getType().compareTo("FMTumourR")==0) {
						Rearrangement r = fm.getTumourRearrangementByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//						r.setBaseline(fm.getTumourBaseline(r.getMeasurement_gene_panel_id()));
						fmTumourR.add(Formater.formatRearrangement(r));
					} else if(sgv.getType().compareTo("FMBloodSV")==0) {
						ShortVariant sv = fm.getShortVariantByGeneVariantID(sgv.getMeasurement_gene_variant_id());
						log.info("FMBlood SV " + sv.getGene_name());
						fmBloodSV.add(Formater.formatShortVariant(sv));
					} else if(sgv.getType().compareTo("FMBloodCNA")==0) {
						CopyNumberAlteration cna = fm.getCopyNumberAlterationByGeneVariantID(sgv.getMeasurement_gene_variant_id());
						log.info("FMBlood CNA " + cna.getGene_name());
						fmBloodCNA.add(Formater.formatCopyNumberAlteration(cna));
					} else if(sgv.getType().compareTo("FMBloodR")==0) {
						Rearrangement r = fm.getRearrangementByGeneVariantID(sgv.getMeasurement_gene_variant_id());
						log.info("FMBlood R " + r.getRearr_gene_1_name());
						fmBloodR.add(Formater.formatRearrangement(r));
						log.info("size " + fmBloodR.size());
					}
					for(ConfigDataSources cds : addSources) {
						if(sgv.getType().compareTo(cds.getFront_end_name()+"SV")==0){
							ShortVariant sv = fm.getShortVariantByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//							if(cds.getIsTumour()) {
//								sv.setBaseline(fm.getTumourBaseline(sv.getMeasurement_gene_panel_id()));
//							}
							log.info("addSource " + sv.getGene_name());
							addSourcesMap.get(cds.getFront_end_name()).get("shortVariant").add(Formater.formatShortVariant(sv));
						} else if(sgv.getType().compareTo(cds.getFront_end_name()+"CNA")==0){
							CopyNumberAlteration cna = fm.getCopyNumberAlterationByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//							if(cds.getIsTumour()) {
//								cna.setBaseline(fm.getTumourBaseline(cna.getMeasurement_gene_panel_id()));
//							}
							log.info("addSource " + cna.getGene_name());
							addSourcesMap.get(cds.getFront_end_name()).get("copyNumberAlteration").add(Formater.formatCopyNumberAlteration(cna));
						} else if(sgv.getType().compareTo(cds.getFront_end_name()+"R")==0){
							Rearrangement r = fm.getRearrangementByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//							if(cds.getIsTumour()) {
//								r.setBaseline(fm.getTumourBaseline(r.getMeasurement_gene_panel_id()));
//							}
							log.info("addSource " + r.getRearr_gene_1_name());
							addSourcesMap.get(cds.getFront_end_name()).get("rearrangement").add(Formater.formatRearrangement(r));
						}
					}
				}
				
				fmTumour.put("copyNumberAlteration", fmTumourCNA);
				fmTumour.put("rearrangement", fmTumourR);
				fmTumour.put("shortVariant", fmTumourSV);
				fmBlood.put("copyNumberAlteration", fmBloodCNA);
				fmBlood.put("rearrangement", fmBloodR);
				log.info("size end " + fmBloodR.size() + " " + fmBlood.get("rearrangement").size());
				fmBlood.put("shortVariant", fmBloodSV);
				Map<String,Object> mutationData=new HashMap<String,Object>();
				mutationData.put("tumourNGS", tumourNGS);
				mutationData.put("ctDNA", ctDNA);
				mutationData.put("fmTumour", fmTumour);
				mutationData.put("fmBlood", fmBlood);
				mutationData.put("genericGenomic", addSourcesMap);
				
				log.info(new Gson().toJson(mutationData));
				return new Gson().toJson(mutationData);

			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(),e);
				Map<String,String> response = new HashMap<>();
		    	response.put("success", "false");
		    	response.put("error", "Failed to get mutation selection.");
		    	return new Gson().toJson(response);
			}
		}
	}
//	
//	private boolean checkIsNewBlood(List<GenePanelIdentifiers> panelIds, GeneSubsetExt gene) {
//		int baseline = gene.getBaseline_number();
//		int run = gene.getRun_number();
//		int specimen_concept=gene.getSpecimen_concept_id();
//		boolean tumour=gene.getNgs_run().compareTo("GDL")==0;
//
//		
//		return false;
//	}
	
	public String getLatest() {
		if (!super.isUserPermittedEndpoint(userID, "GetMutationSelection")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return "User not permitted to access: GetMutationSelection";
		} else {
			// Process the request
			try {
				//get configured sources
				List<ConfigDataSources> addSources = (List<ConfigDataSources>)this.getContext().getBean("configuredDataSources");
				// new version
				List<Map<String,String>> allGenes = new ArrayList<Map<String,String>>();
				List<Map<String,Object>> ctDNA = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> tumourNGS = new ArrayList<Map<String,Object>>();
				Map<String, List<Map<String,String>>> fmBlood = new HashMap<>();
				Map<String, List<Map<String,String>>> fmTumour = new HashMap<>();
				List<Map<String,String>> fmBloodCNAs = new ArrayList<>();
				List<Map<String,String>> fmBloodRs = new ArrayList<>();
				List<Map<String,String>> fmBloodSVs = new ArrayList<>();
				List<Map<String,String>> fmTumourCNAs = new ArrayList<>();
				List<Map<String,String>> fmTumourRs = new ArrayList<>();
				List<Map<String,String>> fmTumourSVs = new ArrayList<>();
				Map<String,String> meetingOutcomeSummarySV = new HashMap<String,String>();
				Map<String,String> meetingOutcomeSummaryR = new HashMap<String,String>();
				Map<String,String> meetingOutcomeSummaryCNA = new HashMap<String,String>();
				fmBlood.put("copyNumberAlteration", fmBloodCNAs);
				fmBlood.put("rearrangement", fmBloodRs);
				fmBlood.put("shortVariant", fmBloodSVs);
				fmTumour.put("copyNumberAlteration", fmTumourCNAs);
				fmTumour.put("rearrangement", fmTumourRs);
				fmTumour.put("shortVariant", fmTumourSVs);
				Map<String,Map<String,List<Map<String,String>>>> addSourcesMap = new HashMap<>();
				for(ConfigDataSources cds : addSources) {
					List<Map<String,String>> sv = new ArrayList<Map<String,String>>();
					List<Map<String,String>> r = new ArrayList<Map<String,String>>();
					List<Map<String,String>> cna = new ArrayList<Map<String,String>>();
					Map<String,List<Map<String,String>>> newSource = new HashMap<>();
					newSource.put("shortVariant", sv);
					newSource.put("copyNumberAlteration", cna);
					newSource.put("rearrangement", r);
					addSourcesMap.put(cds.getFront_end_name(), newSource);
				}
				MutationSelectionDAO mutationSelectionDao = this.getContext().getBean(MutationSelectionDAO.class);
				GeneSubsetDAO genesubset = this.getContext().getBean(GeneSubsetDAO.class);
				TumourNgsDAO tumourNgs = this.getContext().getBean(TumourNgsDAO.class);
				FoundationMedicineDAO fmdao = this.getContext().getBean(FoundationMedicineDAO.class);
				List<SelectedGeneVariant> resultSet = mutationSelectionDao
						.getLatestSelectedMutationsByPersonID(personIDint);
				
				List<Integer> genePanelIDs=genesubset.findLatestGenePanelIDsBlood(personIDint);
				List<Integer> newlySelected = new ArrayList<Integer>();
				boolean found=false;
				//first in list is newest, second the one to compare against.
				if(genePanelIDs.size()>=2) {
					List<GeneSubset> selectedLast =genesubset.findGeneSubsetByGeneVarientsIDs(mutationSelectionDao.getSelectedByGenePanelID(genePanelIDs.get(0)));
					List<Integer> selectedBefore = mutationSelectionDao.getSelectedByGenePanelID(genePanelIDs.get(1));
					List<GeneSubset> allGenesOfPanelBefore= genesubset.findGeneSubsetByGeneVarientsIDs(genesubset.findGeneVarientsByGenePanelID(genePanelIDs.get(1)));
					if(selectedLast == null || selectedBefore == null || allGenesOfPanelBefore==null) {
						log.info("selectedLast " +selectedLast);
						log.info("selectedBefore " +selectedBefore);
						log.info("allGenesOfPanelBefore " + allGenesOfPanelBefore);
					} else {
						for(GeneSubset check : selectedLast) {
							for(GeneSubset genePrevious: allGenesOfPanelBefore) {
								if(check.sameAs(genePrevious)) {
									if(selectedBefore.contains(genePrevious.getGene_varient_id())) {
										//was selected before --nothing new here
										found = true;
									} else {
										// not selected before
										newlySelected.add(check.getGene_varient_id());
									}
									break;
								}
							}
							if(found==false) {
								//not in previous list -- this is new
								newlySelected.add(check.getGene_varient_id());
							}
							found=false;
						}
					}
				}
				for(SelectedGeneVariant sgv : resultSet) {
					if(sgv.getType().compareTo("CTDNA")==0) {
						GeneSubsetExt result =GeneSubsetExt.getGeneSubsetExt(genesubset.findGeneSubsetByGeneVarientID(personIDint, sgv.getMeasurement_gene_variant_id()));
						if(newlySelected.contains(result.getGene_varient_id())) {
							result.setIsNew(true);
						}
						ctDNA.add(Formater.formatCTDNA(result));
						allGenes.add(Formater.formatCTDNASummery(result));
						String mosEntry=result.getGene_name()+result.getGeneResultPure()+result.getSpecimenDateFormatted1()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						
						meetingOutcomeSummarySV.put(mosEntry, result.getGene_name().trim()+" "+result.getGeneResultPure().trim());
					} else if(sgv.getType().compareTo("NGS")==0) {
						TumourNgsExt result = TumourNgsExt.getTumourNgsExt(tumourNgs.findTumourNgsByGeneVarientID(personIDint, sgv.getMeasurement_gene_variant_id()));
						tumourNGS.add(Formater.formatTumourNgs(result));
						allGenes.add(Formater.formatTumourNgsSummery(result));
						if(result.getGene_name()==null) {
							result.setGene_name("");
						}
						String mosEntry=result.getGene_name()+result.getGeneResultPure()+result.getSpecimentDateFormatted()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						meetingOutcomeSummarySV.put(mosEntry, result.getGene_name().trim()+" "+result.getGeneResultPure().trim());
					} else if(sgv.getType().compareTo("FMBloodCNA")==0) {
						CopyNumberAlteration cna = fmdao.getCopyNumberAlterationByGeneVariantID(sgv.getMeasurement_gene_variant_id());
						fmBloodCNAs.add(Formater.formatCopyNumberAlteration(cna));
						if(cna.getGene_name()==null) {
							cna.setGene_name("");
						}
						allGenes.add(Formater.formatCopyNumberAlterationSummery(cna, "FM blood"));
						String mosEntry=cna.getGene_name()+cna.getCna_type()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						meetingOutcomeSummaryCNA.put(mosEntry, cna.getGene_name().trim()+" "+cna.getCna_type().trim());
					} else if(sgv.getType().compareTo("FMBloodR")==0) {
						Rearrangement r = fmdao.getRearrangementByGeneVariantID(sgv.getMeasurement_gene_variant_id());
						fmBloodRs.add(Formater.formatRearrangement(r));
						allGenes.add(Formater.formatRearrangementSummery(r, "FM blood"));
						if(r.getRearr_gene_1_name()==null) {
							r.setRearr_gene_1_name("");
						}
						if(r.getRearr_gene_2_name()==null) {
							r.setRearr_gene_2_name("");
						}
						String mosEntry=r.getRearr_gene_1_name()+r.getRearr_gene_2_name()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						meetingOutcomeSummaryR.put(mosEntry, r.getRearr_gene_1_name().trim()+ " "+r.getRearr_gene_2_name().trim()+" "+r.getRearr_description().trim());
					} else if(sgv.getType().compareTo("FMBloodSV")==0) {
						ShortVariant sv = fmdao.getShortVariantByGeneVariantID(sgv.getMeasurement_gene_variant_id());
						fmBloodSVs.add(Formater.formatShortVariant(sv));
						allGenes.add(Formater.formatShortVariantSummery(sv, "FM blood"));
						if(sv.getGene_name()==null) {
							sv.setGene_name("");
						}
						String mosEntry=sv.getGene_name()+sv.getResult()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						meetingOutcomeSummarySV.put(mosEntry, sv.getGene_name().trim()+" "+sv.getResult().trim());
					} else if(sgv.getType().compareTo("FMTumourCNA")==0) {
						CopyNumberAlteration cna = fmdao.getCopyNumberAlterationByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//						cna.setBaseline(fmdao.getTumourBaseline(cna.getMeasurement_gene_panel_id()));
						fmTumourCNAs.add(Formater.formatCopyNumberAlteration(cna));
						allGenes.add(Formater.formatCopyNumberAlterationSummery(cna, "FM tumour"));
						if(cna.getGene_name()==null) {
							cna.setGene_name("");
						}
						String mosEntry=cna.getGene_name()+cna.getCna_type()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						meetingOutcomeSummaryCNA.put(mosEntry, cna.getGene_name().trim()+" "+cna.getCna_type().trim());
					} else if(sgv.getType().compareTo("FMTumourR")==0) {
						Rearrangement r = fmdao.getRearrangementByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//						r.setBaseline(fmdao.getTumourBaseline(r.getMeasurement_gene_panel_id()));
						fmTumourRs.add(Formater.formatRearrangement(r));
						allGenes.add(Formater.formatRearrangementSummery(r, "FM tumour"));
						if(r.getRearr_gene_1_name()==null) {
							r.setRearr_gene_1_name("");
						}
						if(r.getRearr_gene_2_name()==null) {
							r.setRearr_gene_2_name("");
						}
						String mosEntry=r.getRearr_gene_1_name()+r.getRearr_gene_2_name()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						meetingOutcomeSummaryR.put(mosEntry, r.getRearr_gene_1_name().trim()+ " "+r.getRearr_gene_2_name().trim()+" "+ r.getRearr_description().trim());
					} else if(sgv.getType().compareTo("FMTumourSV")==0) {
						ShortVariant sv = fmdao.getShortVariantByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//						sv.setBaseline(fmdao.getTumourBaseline(sv.getMeasurement_gene_panel_id()));
						fmTumourSVs.add(Formater.formatShortVariant(sv));
						allGenes.add(Formater.formatShortVariantSummery(sv, "FM tumour"));
						if(sv.getGene_name()==null) {
							sv.setGene_name("");
						}
						String mosEntry=sv.getGene_name()+sv.getResult()+sgv.getType();
						mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
						meetingOutcomeSummarySV.put(mosEntry, sv.getGene_name().trim()+" "+sv.getResult().trim());
					}
					for(ConfigDataSources cds : addSources) {
						if(sgv.getType().compareTo(cds.getFront_end_name()+"CNA")==0) {
							CopyNumberAlteration cna = fmdao.getCopyNumberAlterationByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//							if(cds.getIsTumour()) {
//								cna.setBaseline(fmdao.getTumourBaseline(cna.getMeasurement_gene_panel_id()));
//							}
							addSourcesMap.get(cds.getFront_end_name()).get("copyNumberAlteration").add(Formater.formatCopyNumberAlteration(cna));
							allGenes.add(Formater.formatCopyNumberAlterationSummery(cna, cds.getFront_end_name()));
							String mosEntry=cna.getGene_name()+cna.getCna_type()+sgv.getType();
							mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
							meetingOutcomeSummaryCNA.put(mosEntry, cna.getGene_name().trim()+" "+cna.getCna_type().trim());
						} else if(sgv.getType().compareTo(cds.getFront_end_name()+"R")==0) {
							Rearrangement r = fmdao.getRearrangementByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//							if(cds.getIsTumour()) {
//								r.setBaseline(fmdao.getTumourBaseline(r.getMeasurement_gene_panel_id()));
//							}
							addSourcesMap.get(cds.getFront_end_name()).get("rearrangement").add(Formater.formatRearrangement(r));
							allGenes.add(Formater.formatRearrangementSummery(r, cds.getFront_end_name()));
							String mosEntry=r.getRearr_gene_1_name()+r.getRearr_gene_2_name()+sgv.getType();
							mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
							meetingOutcomeSummaryR.put(mosEntry, r.getRearr_gene_1_name().trim()+ " "+r.getRearr_gene_2_name().trim()+" "+ r.getRearr_description().trim());
						} else if(sgv.getType().compareTo(cds.getFront_end_name()+"SV")==0) {
							ShortVariant sv = fmdao.getShortVariantByGeneVariantID(sgv.getMeasurement_gene_variant_id());
//							if(cds.getIsTumour()) {
//								sv.setBaseline(fmdao.getTumourBaseline(sv.getMeasurement_gene_panel_id()));
//							}
							addSourcesMap.get(cds.getFront_end_name()).get("shortVariant").add(Formater.formatShortVariant(sv));
							allGenes.add(Formater.formatShortVariantSummery(sv, cds.getFront_end_name()));
							String mosEntry=sv.getGene_name()+sv.getResult()+sgv.getType();
							mosEntry=mosEntry.replaceAll("[^A-Za-z0-9]", "");
							meetingOutcomeSummarySV.put(mosEntry, sv.getGene_name().trim()+" "+sv.getResult().trim());
						}
					}
				}
				log.info("size of list " + fmBloodRs.size());
				Map<String,Object> mutationData=new HashMap<String,Object>();
				mutationData.put("latestTumourNGS", tumourNGS);
				mutationData.put("latestCtDNA", ctDNA);
				mutationData.put("latestFmBlood", fmBlood);
				mutationData.put("latestFmTumour", fmTumour);
				mutationData.put("summery", allGenes);
				mutationData.put("meetingOutcomeSummarySV", meetingOutcomeSummarySV);
				mutationData.put("meetingOutcomeSummaryR", meetingOutcomeSummaryR);
				mutationData.put("meetingOutcomeSummaryCNA", meetingOutcomeSummaryCNA);
				mutationData.put("latestGenericGenomic", addSourcesMap);
				
				return new Gson().toJson(mutationData);

			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(),e);
				return "Failed to get mutation selection.";
			}
		}
	}

}
