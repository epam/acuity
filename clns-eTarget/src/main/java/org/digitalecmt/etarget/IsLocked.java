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

import org.digitalecmt.etarget.dao.EditLockDAO;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class IsLocked extends API {
	private static final Logger log = Logger.getLogger(IsLocked.class.getName());
	private String userID=null;
	private Integer personID=null;
	
	public IsLocked(String userID, String personID) {
		this.userID=userID;
		try {
			this.personID=Integer.parseInt(personID);
		}catch(NumberFormatException ex) {
			log.severe("personID "+personID+" not an integer");
			personID=null;
		}
	}
	
	public String processRequest() {
		EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
		Map<String,String> result= new HashMap<String,String>();
		Boolean isLocked=Boolean.FALSE;
		if(userID!=null && personID!=null) {
			isLocked = editLockDao.isLocked(userID, personID);
			result.put("success", "true");
			result.put("locked", isLocked.toString());
		} else {
			result.put("success", "false");
		}
		return new Gson().toJson(result);
	}
}
