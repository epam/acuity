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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ReportDAOImpl extends JdbcDaoSupport implements ReportDAO {
	
	private static final Logger log = Logger.getLogger(ReportDAOImpl.class.getName());
	
	private String selectGenePanelId="select top 1 measurement_gene_panel_id from MEASUREMENT_GENE_PANEL as mgp " + 
			"where mgp.specimen_id=? and mgp.run_number=? order by measurement_gene_panel_id desc";
	private String deletePathLabRef="delete from PATH_LAB_REF where measurement_gene_panel_id=?";
	private String deleteReport="delete from REPORT where measurement_gene_panel_id=?";
	private String deleteSelected="delete from SELECTED_GENE_VARIANT where measurement_gene_variant_id IN (select measurement_gene_variant_id from MEASUREMENT_GENE_VARIANT where measurement_gene_panel_id=?)";
	private String deleteGeneVariants="delete from MEASUREMENT_GENE_VARIANT where measurement_gene_panel_id=?";
	private String deleteGenePanel="delete from MEASUREMENT_GENE_PANEL where measurement_gene_panel_id=? ";
	
	public ReportDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	public Boolean deleteReportBySpecimenRun(int specimen_id, int run) {
		try {
			JdbcTemplate template = getJdbcTemplate();
			Integer gene_panel_id = template.queryForObject(selectGenePanelId, new Object[] {specimen_id,run}, Integer.class);
			template.update(deletePathLabRef,new Object[] {gene_panel_id});
			template.update(deleteReport,new Object[] {gene_panel_id});
			template.update(deleteSelected,new Object[] {gene_panel_id});
			template.update(deleteGeneVariants,new Object[] {gene_panel_id});
			if(template.update(deleteGenePanel,new Object[] {gene_panel_id})==1) {
				return true;
			};
		} catch (Exception ex) {
			log.log(Level.SEVERE, "can't find gene_panel_id", ex);
		}
		return false;
	}

}
