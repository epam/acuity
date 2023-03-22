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

import java.util.Calendar;
import java.util.List;

import org.digitalecmt.etarget.config.ConfigDataSources;
import org.digitalecmt.etarget.dbentities.MeetingOutcome;
import org.springframework.dao.DataAccessException;

public interface MeetingOutcomeDAO {
	List<MeetingOutcome> getMeetingOutcomesByPersonID(int personID) throws DataAccessException;
	MeetingOutcome getMeetingOutcomeByMeetingID(int meetingOutcomeID) throws DataAccessException;
	boolean addMeetingOutcome(String loggedInUserID, String dbPersonID, String meetingDate, 
			String outcome, String notes, String ctDNA, String tumourNGS, String fmBlood, String fmTumour, String genericGenomic) 
					throws DataAccessException;
	boolean editMeetingOutcome(String loggedInuserId, String meetingID, String type, String value) throws DataAccessException;
	boolean deleteMeetingOutcome(int mo_id);
	boolean printMeetingOutcome(int mo_id);
	List<Integer> getPersonIdsForPrinting() throws DataAccessException;
	List<Integer> getMeetingOutcomesForPrinting(int personID) throws DataAccessException;
	Calendar getLastMeetingOutcomeDate(int personID) throws DataAccessException;
	Calendar getSampleDateBlood(java.sql.Date meeting_date, Integer person_id);
	Calendar getSampleDateTumour(java.sql.Date meeting_date, Integer person_id);
	void setAdditionalSources(List<ConfigDataSources> addSources);
}
