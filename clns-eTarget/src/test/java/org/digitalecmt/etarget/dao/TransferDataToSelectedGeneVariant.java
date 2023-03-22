package org.digitalecmt.etarget.dao;

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

import static org.junit.Assert.*;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dbentities.GeneSubsetExt;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;

public class TransferDataToSelectedGeneVariant {
	private static ApplicationContext context;
	@BeforeClass
	public static void setUpClass() {
		try {
			context = new AnnotationConfigApplicationContext(TargetConfiguration.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void transferData() throws SQLException{
		GeneSubsetDAO genesubset = context.getBean(GeneSubsetDAO.class);
		TumourNgsDAO tumourNgs = context.getBean(TumourNgsDAO.class);
		
		
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_UP);
		DataSource datasource = context.getBean(DataSource.class);
		Connection con = datasource.getConnection();
		Statement statement = con.createStatement();
		String selectSQL = "SELECT * FROM MEETING_OUTCOME_LATEST ";
		ResultSet rs = statement.executeQuery(selectSQL);
		while(rs.next()) {
			Integer person_id=rs.getInt("person_id");
			
			List<GeneSubsetExt> all_genes = GeneSubsetExt.getGeneSubsetExtList(genesubset.findGeneExploratoryByPersonID(person_id));
			all_genes.addAll(GeneSubsetExt.getGeneSubsetExtList(genesubset.findGeneSubsetByPersonID(person_id)));
			
			List<TumourNgsExt> all_tumour = TumourNgsExt.getTumourNgsExtList(tumourNgs.findTumourNgsByPersonID(person_id));
			
			String mutation_data=rs.getString("mutation_data");
			Map<String,Object> meetingOutcome = new HashMap<String,Object>();
			meetingOutcome =(Map<String,Object>)  new Gson().fromJson(mutation_data, meetingOutcome.getClass());
			if(meetingOutcome.get("ctDNA") instanceof List) {
				List<Map<String,Object>> ctdna=(List<Map<String,Object>>)meetingOutcome.get("ctDNA");
				for(Map<String,Object> ind : ctdna) {
					String geneName = (String) ind.get("geneName");
					String result = (String) ind.get("result");
					String date = (String) ind.get("specimenDate");
					String frequ = (String) ind.get("cfdnaFrequency");
					System.out.println(geneName + " " + result);
					//find the right one in GeneSubsetExt
					for(GeneSubsetExt gse : all_genes) {
						if(gse.getGene_name().equals(geneName) && gse.getGeneResultPure().equals(result) && gse.getSpecimenDateFormatted1().equals(date) &&
								df.format((double)gse.getVariant_allele_frequency()).equals(frequ)){
							System.out.println("found it: " + person_id + " " + gse.getGene_varient_id() + " "+ ind.get("cfdnaFrequency") +" " + gse.getVariant_allele_frequency());
//							mutationSelection.addMutation(person_id, gse.getGene_varient_id(), "CTDNA");
						}
					}
				}
			}
			
			if(meetingOutcome.get("tumourNGS") instanceof List) {
				List<Map<String,Object>> tumour=(List<Map<String,Object>>)meetingOutcome.get("tumourNGS");
				for(Map<String,Object> ind : tumour) {
					String geneName = (String) ind.get("geneName");
					String result = (String) ind.get("result");
					String date = (String) ind.get("specimenDate");
					String mutant = (String) ind.get("mutantAllele");
					System.out.println("Tumour "+ geneName + " " + result);
					for(TumourNgsExt tn : all_tumour) {
						//System.out.println(tn.getGeneResultPure() + " " + result);
						if(tn.getGene_name().equals(geneName) && tn.getGeneResultPure().equals(result) && tn.getSpecimentDateFormatted().equals(date) && 
								Integer.toString(tn.getApproximate_mutatnt_allele_frequency()).equals(mutant)) {
							
							System.out.println("found it: " + person_id + " " +tn.getGene_varient_id() + " "+ ind.get("mutantAllele") +" " + tn.getApproximate_mutatnt_allele_frequency());
//							mutationSelection.addMutation(person_id, tn.getGene_varient_id(), "NGS");
						}
					}
				}
				
			}
			
		}
		assertTrue(true);
	}

}
