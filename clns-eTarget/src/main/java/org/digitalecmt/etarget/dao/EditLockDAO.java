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

import org.springframework.dao.DataAccessException;

public interface EditLockDAO {
	/**
	 * locks or updates timestamp for patient lock; succeeds only if patient is currently unlocked or locked by user_id
	 * @param user_id String of users email address
	 * @param person_id integer or patient id
	 * @return 
	 * @throws DataAccessException
	 */
	Boolean lock(String user_id, int person_id) throws DataAccessException;
	/**
	 * checks whether a patient identified by person_id is locked by a editor which is not yourself
	 * @param user_id String of users email address
	 * @param person_id integer or patient id
	 * @return true if patient is locked by someone else, false if patient is not locked or by yourself
	 * @throws DataAccessException
	 */
	Boolean isLocked(String user_id, int person_id) throws DataAccessException;
	void unlock(String user_id, int person_id) throws DataAccessException;
	void unlockAll(String user_id) throws DataAccessException;
	String getLocker(int person_id) throws DataAccessException;
	Boolean isEditor(String user_id) throws DataAccessException;
}
