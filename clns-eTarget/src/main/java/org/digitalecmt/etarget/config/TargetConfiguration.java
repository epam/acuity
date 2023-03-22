package org.digitalecmt.etarget.config;

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

import net.sourceforge.jtds.jdbc.Driver;

import org.digitalecmt.etarget.dao.ChangeLogDAO;
import org.digitalecmt.etarget.dao.ChangeLogDAOImpl;
import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.dao.EditLockDAOImpl;
import org.digitalecmt.etarget.dao.FoundationMedicineDAO;
import org.digitalecmt.etarget.dao.FoundationMedicineDAOImpl;
import org.digitalecmt.etarget.dao.GeneNameDAO;
import org.digitalecmt.etarget.dao.GeneNameDAOImpl;
import org.digitalecmt.etarget.dao.GenePanelDAO;
import org.digitalecmt.etarget.dao.GenePanelDAOImpl;
import org.digitalecmt.etarget.dao.GeneSubsetDAO;
import org.digitalecmt.etarget.dao.GeneSubsetDAOImpl;
import org.digitalecmt.etarget.dao.GenomicsDataDAO;
import org.digitalecmt.etarget.dao.GenomicsDataDAOImpl;
import org.digitalecmt.etarget.dao.IHCReportDAO;
import org.digitalecmt.etarget.dao.IHCReportDAOImpl;
import org.digitalecmt.etarget.dao.MeetingOutcomeDAO;
import org.digitalecmt.etarget.dao.MeetingOutcomeDAOImpl;
import org.digitalecmt.etarget.dao.MeetingOutcomeGeneVariantDAO;
import org.digitalecmt.etarget.dao.MeetingOutcomeGeneVariantDAOImpl;
import org.digitalecmt.etarget.dao.MutationSelectionDAO;
import org.digitalecmt.etarget.dao.MutationSelectionDAOImpl;
import org.digitalecmt.etarget.dao.NgsLibDAO;
import org.digitalecmt.etarget.dao.NgsLibDAOImpl;
import org.digitalecmt.etarget.dao.PersonDAO;
import org.digitalecmt.etarget.dao.PersonDAOImpl;
import org.digitalecmt.etarget.dao.ReportDAO;
import org.digitalecmt.etarget.dao.ReportDAOImpl;
import org.digitalecmt.etarget.dao.SearchDAO;
import org.digitalecmt.etarget.dao.SearchDAOImpl;
import org.digitalecmt.etarget.dao.TumourNgsDAOImpl;
import org.digitalecmt.etarget.dao.UserAccessDAO;
import org.digitalecmt.etarget.dao.UserAccessDAOImpl;
import org.digitalecmt.etarget.dao.TumourNgsDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;



@Configuration
public class TargetConfiguration {
	
	static HikariDataSource dataSource=null;
	static List<ConfigDataSources> configuredSourceNames=null;
	
    String url;
    String id;
    String pass;	
    
    Integer timeoutMin=10;
	
	public TargetConfiguration() {
		ResourceBundle resource = ResourceBundle.getBundle("config");
		url = resource.getString("databaseURL");
	    id = resource.getString("dbuser");
	    pass = resource.getString("dbpassword");
	    
	    System.out.println("ResourceBundle " + resource);
	    System.out.println("databaseURL" + url);
	    
	    ResourceBundle application = ResourceBundle.getBundle("application");
	    if(application.containsKey("edit.timeout")) {
	    	try {
	    		timeoutMin = Integer.parseInt(application.getString("edit.timeout"));
	    	} catch(NumberFormatException ex) {;}
	    }
	    
	    if(configuredSourceNames==null) {
			configuredSourceNames=new ArrayList<ConfigDataSources>();
			if(application.containsKey("page.name")) {
				String[] pageNames =application.getString("page.name").trim().split("\\s*,\\s*");
				for(String pn : pageNames) {
					Boolean isBlood = Boolean.parseBoolean(application.getString("page.name."+pn+".blood"));
					Boolean isTumour = Boolean.parseBoolean(application.getString("page.name."+pn+".tumour"));
					String code = application.getString("page.name."+pn+".code");
					if(isBlood) {
						ConfigDataSources cds = new ConfigDataSources();
						cds.setDb_panel_name(pn);
						cds.setIsBlood(true);
						cds.setIsTumour(false);
						cds.setFront_end_name(pn+"_blood");
						cds.setCode(code);
						configuredSourceNames.add(cds);
					}
					if(isTumour) {
						ConfigDataSources cds = new ConfigDataSources();
						cds.setDb_panel_name(pn);
						cds.setIsBlood(false);
						cds.setIsTumour(true);
						cds.setFront_end_name(pn+"_tumour");
						cds.setCode(code);
						configuredSourceNames.add(cds);
					}
				}
			}
		}
	}
	
	@Bean
	public SearchDAO searchDao() {
		return new SearchDAOImpl(dataSource());
	}
	
	@Bean
	public GeneSubsetDAO geneSubsetDao() {
		return new GeneSubsetDAOImpl(dataSource());
	}
	
	@Bean
	public NgsLibDAO ngsLibDao() {
		return new NgsLibDAOImpl(dataSource());
	}
	
	@Bean
	public GeneNameDAO geneNameDao() {
		return new GeneNameDAOImpl(dataSource());
	}
	
	@Bean
	public TumourNgsDAO tumourNgsDao() {
		return new TumourNgsDAOImpl(dataSource());
	}
	
	@Bean
	public DataSource dataSource() {
		if(dataSource==null) {
			dataSource = new HikariDataSource();
			dataSource.setDriverClassName(Driver.class.getName());
			dataSource.setJdbcUrl(this.url);
			dataSource.setUsername(this.id);
			dataSource.setPassword(this.pass);
			dataSource.setConnectionTestQuery("select 1 from WHITELIST");
			dataSource.setMinimumIdle(1);
			dataSource.setMaximumPoolSize(10);
		}
		return dataSource;
	}
	
	@Bean
	public MutationSelectionDAO mutationSelectionDao() {
		return new MutationSelectionDAOImpl(dataSource());
	}

	@Bean
	public MeetingOutcomeDAO meetingOutcomeDao() {
		return new MeetingOutcomeDAOImpl(dataSource());
	}
	
	@Bean
	public ChangeLogDAO changeLogDao() {
		return new ChangeLogDAOImpl(dataSource());
	}
	
	@Bean
	public EditLockDAO editLockDao() {
		return new EditLockDAOImpl(dataSource(), timeoutMin);
	}
	
	@Bean
	public MeetingOutcomeGeneVariantDAO meetingOutcomeGeneVariantDao() {
		return new MeetingOutcomeGeneVariantDAOImpl(dataSource());
	}
	
	@Bean
	public ReportDAO reportDao() {
		return new ReportDAOImpl(dataSource());
	}
	
	@Bean
	public GenePanelDAO genePanelDao() {
		return new GenePanelDAOImpl(dataSource());
	}
	
	@Bean
	public UserAccessDAO userAccessDao() {
		return new UserAccessDAOImpl(dataSource());
	}
	
	@Bean
	public FoundationMedicineDAO foundationMedicineDao() {
		return new FoundationMedicineDAOImpl(dataSource());
	}
	
	@Bean
	public IHCReportDAO ihcReportDao() {
		return new IHCReportDAOImpl(dataSource());
	}
	
	@Bean
	public PersonDAO personDao() {
		return new PersonDAOImpl(dataSource());
	}
	
	@Bean
	public GenomicsDataDAO genomicsDataDao() {
		return GenomicsDataDAOImpl.getInstance(dataSource());
	}
	
	@Bean 
	public List<ConfigDataSources> configuredDataSources() {
		return configuredSourceNames;
	}

}
