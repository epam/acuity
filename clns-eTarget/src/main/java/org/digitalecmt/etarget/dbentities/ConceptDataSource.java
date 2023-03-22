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

import java.sql.Date;

public class ConceptDataSource {
	Integer data_source_concept_id;
	String panel_name;
	String reference_source;
	Date valid_start_date;
	
	public Integer getData_source_concept_id() {
		return data_source_concept_id;
	}
	public void setData_source_concept_id(Integer data_source_concept_id) {
		this.data_source_concept_id = data_source_concept_id;
	}
	public String getPanel_name() {
		return panel_name;
	}
	public void setPanel_name(String panel_name) {
		this.panel_name = panel_name;
	}
	public String getReference_source() {
		return reference_source;
	}
	public void setReference_source(String reference_source) {
		this.reference_source = reference_source;
	}
	public Date getValid_start_date() {
		return valid_start_date;
	}
	public void setValid_start_date(Date valid_start_date) {
		this.valid_start_date = valid_start_date;
	}
}
