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

public class ConfigDataSources {
	public String getDb_panel_name() {
		return db_panel_name;
	}
	public void setDb_panel_name(String db_panel_name) {
		this.db_panel_name = db_panel_name;
	}
	public String getFront_end_name() {
		return front_end_name;
	}
	public void setFront_end_name(String front_end_name) {
		this.front_end_name = front_end_name;
	}
	public Boolean getIsBlood() {
		return isBlood;
	}
	public void setIsBlood(Boolean isBlood) {
		this.isBlood = isBlood;
	}
	public Boolean getIsTumour() {
		return isTumour;
	}
	public void setIsTumour(Boolean isTumour) {
		this.isTumour = isTumour;
	}	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	String db_panel_name;
	String front_end_name;
	Boolean isBlood;
	Boolean isTumour;
	String code;

}
