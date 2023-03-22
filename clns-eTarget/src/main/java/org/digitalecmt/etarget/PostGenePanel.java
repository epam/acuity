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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.digitalecmt.etarget.dao.GenePanelDAO;
import org.digitalecmt.etarget.dao.GenePanelDAOImpl;
import org.digitalecmt.etarget.dbentities.Genes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PostGenePanel extends API {

	String userID;
	String genePanelName;
	List<Genes> genePanelGenes;
	String date;
	private static Logger log = Logger.getLogger(PostGenePanel.class.getName());
	
	public PostGenePanel(String loggedInUserID, String panelName, String panelGenes, String date) {
		userID = loggedInUserID;
		genePanelName = panelName;
		this.date = date;
		genePanelGenes = new Gson().fromJson(panelGenes, new TypeToken<ArrayList<Genes>>() {
		}.getType());
		log.info("genes " + panelGenes);
	}

	public String processRequest() {
		if (!super.isUserPermittedEndpoint(userID, "PostMeetingOutcome")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			Map<String, String> response = new HashMap<>();
			response.put("success", "false");
			response.put("error", "User not permitted to access: PostGenePanel");
			return new Gson().toJson(response);
		} else {
			Map<String, String> responseData = new HashMap<String, String>();
			GenePanelDAO genePanelDao = this.getContext().getBean(GenePanelDAO.class);
			List<Integer> geneIds = new ArrayList<Integer>();
			for (Genes gene : genePanelGenes) {
				if (gene.getGene_concept_id() == -1) {
					gene.setGene_concept_id(genePanelDao.addGene(gene.getGene_name()));
				}
				geneIds.add(gene.getGene_concept_id());
			}

			int genePanelId = genePanelDao.addGenePanel(genePanelName, date, geneIds);
			if (genePanelId == -1) {
				responseData.put("success", "false");
				responseData.put("error", "Database operation did not succeed");
			} else {
				responseData.put("genePanelId", Integer.toString(genePanelId));
				responseData.put("success", "true");
			}
			return new Gson().toJson(responseData);
		}
	}

}
