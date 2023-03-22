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

import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.digitalecmt.etarget.dbentities.IHCReport;
import org.digitalecmt.etarget.dbentities.MeetingOutcome;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class IHCReportDAOImpl extends JdbcDaoSupport implements IHCReportDAO {
	
	private static final Logger log = Logger.getLogger(IHCReportDAOImpl.class.getName());
	
	private static String selection="ihc_report_id,sample_received_date,report_date,cd3_total_tissue_area,cd3_intratumoural,cd3_intrastromal,cd8_total_tissue_area,cd8_intratumoural,cd8_intrastromal,pdl1_tps,estimated_results,comments,ingestion_date, SPECIMEN.person_id, SPECIMEN.specimen_date, SPECIMEN.preclin_id, SPECIMEN.specimen_id";
	private static String select="select "+selection+" from SPECIMEN LEFT JOIN IHC_REPORT on SPECIMEN.specimen_id=IHC_REPORT.specimen_id where (SPECIMEN.specimen_concept_id=2 or SPECIMEN.specimen_concept_id=3) and SPECIMEN.person_id=?";
	
	public IHCReportDAOImpl(DataSource datasource) {
		setDataSource(datasource);
	}
	
	@Override
	public List<IHCReport> getIHCReport(Integer personID) {
		List<IHCReport> values = null;
		JdbcTemplate template = getJdbcTemplate();
		values= template.query(select, new Object[] {personID}, 
				new BeanPropertyRowMapper<IHCReport>(IHCReport.class));
		return values;
	}

}
