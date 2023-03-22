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

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.NgsLibDAO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NgsLibExtTest {
	
	private static ApplicationContext context;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			context = new AnnotationConfigApplicationContext(TargetConfiguration.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() {
		NgsLibDAO ngsLibDao = context.getBean(NgsLibDAO.class);
		List<NgsLib> results = ngsLibDao.findNgsLibByPersonID(12);
		List<NgsLibExt> ngsLibs=NgsLibExt.getNgsLibExtList(results);
		assertTrue(ngsLibs.size()==results.size());
	}
	
	@Test
	public void testMap() {
		NgsLibDAO ngsLibDao = context.getBean(NgsLibDAO.class);
		List<NgsLib> results = ngsLibDao.findNgsLibByPersonID(12);
		List<NgsLibExt> ngsLibs=NgsLibExt.getNgsLibExtList(results);
		Map<String,Object> map=ngsLibs.get(0).getNgsLibMap();
		System.out.println(map.toString());
		assertTrue(map.isEmpty()==false);
	}

}
