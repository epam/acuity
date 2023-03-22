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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.dao.GeneSubsetDAO;
import org.digitalecmt.etarget.dao.TumourNgsDAO;
import org.digitalecmt.etarget.dbentities.GeneSubsetTumour;
import org.digitalecmt.etarget.dbentities.TumourNgs;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;
import org.digitalecmt.etarget.support.Formater;

import com.google.gson.Gson;

public class TumourNGS extends API {
	private static final Logger log = Logger.getLogger(TumourNGS.class.getName());
			
	// Vars
	String personID;
	String userID;
	int personIDint;


	// Constructor
	public TumourNGS(String loggedInUserID, String dbPersonID) {
		userID = loggedInUserID;
		personID = dbPersonID;
		personIDint = Integer.parseInt(dbPersonID);
		
	}
	
	public String processRequest() {
		if (!super.isUserPermittedEndpoint(userID, "TumourNGS")) {
			Map<String,String> response = new HashMap<>();
	    	response.put("success", "false");
	    	response.put("error", "User not permitted to access: TumourNGS");
	    	return new Gson().toJson(response);
		}
		//lock patient
		EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
		editLockDao.lock(userID, personIDint);
		
		TumourNgsDAO tumourNGSDao= this.getContext().getBean(TumourNgsDAO.class);
		List<TumourNgs> tngsl= tumourNGSDao.findTumourNgsByPersonID(personIDint);
		//TumourNgsExt.adjustBaselines(tngsl);
		List<TumourNgsExt> results = TumourNgsExt.getTumourNgsExtList(tngsl);
		log.info("tumour result size: " + results.size());
		Map<String,Object> tmap = Formater.getTumourMap(results);
		GeneSubsetDAO geneSubsetDao = this.getContext().getBean(GeneSubsetDAO.class);
		List<GeneSubsetTumour> genes = geneSubsetDao.findTumourGeneVariantsByPersonID(personIDint);
		GeneSubsetTumour.adjustBaseline(genes, tngsl);
		tmap = Formater.addTumourGeneSubset(genes, tmap);
		Formater.cleanTumourMap(tmap);
		return new Gson().toJson(tmap);
	}


}
