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

//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.GeneSearch;
import org.digitalecmt.etarget.dbentities.PDXCDXSearch;
import org.digitalecmt.etarget.dbentities.Search;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import java.util.logging.Logger;

public class SearchDAOImpl  extends JdbcDaoSupport implements SearchDAO {
	private static final Logger log = Logger.getLogger(SearchDAOImpl.class.getName());
	
	private String query="SELECT top 200 * from search  where cdna_change IS NOT NULL order by cdna_change;";
	private String queryCondition="SELECT DISTINCT * from search where condition_name=? or subtype_name=? order by gene_name, condition_name, subtype_name;";
	private String queryGene="SELECT * from search where gene_name=? or rearr_gene1=? or rearr_gene2=? order by gene_name,rearr_gene1,rearr_gene2, condition_name;";
	private String condition="select distinct condition_name from SEARCH where condition_name is not null\n" + 
			"union select distinct subtype_name as condition_name from SEARCH where subtype_name is not null;";
	private String gene="select gene_name,unknown_significant from GENE_SEARCH";

	private String queryPDXCDX="select distinct PDXCDX.person_id, PERSON.target_id, pdx_or_cdx as PDXCDX from PDXCDX join PERSON on PERSON.person_id=PDXCDX.person_id";

	@Override
	public List<Search> getAllPatientsSearch() {
		List<Search> values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(query, new Object[] {}, 
					new BeanPropertyRowMapper<Search>(Search.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
//		return orderByGeneFrequency(values);
	}
	
//	private List<Search> orderByGeneFrequency(List<Search>dblist){
//		HashMap<String,Integer>frequencies=new HashMap<>();
//		for(Search s : dblist) {
//			if(frequencies.containsKey(s.getGene_name())) {
//				frequencies.put(s.getGene_name(), frequencies.get(s.getGene_name())+1);
//			} else {
//				frequencies.put(s.getGene_name(),1);
//			}
//		}
//			
//		ValueComparator bvc = new ValueComparator(frequencies);
//		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
//		sorted_map.putAll(frequencies);
//		List<Search> ret = new ArrayList<Search>();
//		ArrayList<String> genes =new ArrayList<String>(sorted_map.keySet());
//		for(String gene : genes) {
//			for(Search s : dblist) {
//				if(s.getGene_name().compareTo(gene)==0) {
//					ret.add(s);
//				}
//			}
//		}
//			
//		
//		return dblist;
//	}

	public SearchDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
//	class ValueComparator implements Comparator<String> {
//	    Map<String, Integer> base;
//
//	    public ValueComparator(Map<String, Integer> base) {
//	        this.base = base;
//	    }
//
//	    // Note: this comparator imposes orderings that are inconsistent with
//	    // equals.
//	    public int compare(String a, String b) {
//	    	if (base.get(a) < base.get(b)) {
//	            return 1;
//	        } else if(base.get(a) == base.get(b)){
//	        	return a.compareTo(b);
//	        } else {
//	            return -1;
//	        } 
//	    }
//	}

	@Override
	public List<String> getAllConditionsSearch() {
		List<String> values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.queryForList(condition, String.class);//(condition, new Object[] {}, 
					//new BeanPropertyRowMapper<String>(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	@Override
	public List<GeneSearch> getAllGenesSearch() {
		List<GeneSearch> values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(gene, new Object[] {}, 
					new BeanPropertyRowMapper<GeneSearch>(GeneSearch.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public List<Search> getCondition(String cond) {
		List<Search> values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(queryCondition, new Object[] {cond, cond}, 
					new BeanPropertyRowMapper<Search>(Search.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	
	@Override
	public List<Search> getGene(String gene) {
		List<Search> values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(queryGene, new Object[] {gene, gene, gene}, 
					new BeanPropertyRowMapper<Search>(Search.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public List<PDXCDXSearch> getPDXCDXPatients() {
		List<PDXCDXSearch> values = null;
		try {
			JdbcTemplate template = getJdbcTemplate();
			values= template.query(queryPDXCDX, new BeanPropertyRowMapper<PDXCDXSearch>(PDXCDXSearch.class));
			log.info("values " + values);
		} catch (Exception e) {
			log.severe("exception " + e.getMessage());
			e.printStackTrace();
		}
		return values;
	}
}
