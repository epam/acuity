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

import java.util.*;
import java.sql.*;
import java.text.*;
import com.google.gson.*;

public class PDXCDX extends API {
  // Vars
  String personID;
  String userID;
  java.sql.Date createdDate;
  String pdx_or_cdx;
  String createdDateFormatted;
  String timepoint;

  // Constructor
  public PDXCDX(String loggedInUserID, String dbPersonID) {
    userID = loggedInUserID;
    personID = dbPersonID;
  }

  public String processRequest() {
    if(!super.isUserPermittedEndpoint(userID, "PDXCDX")) {
      // Stop the request if user doesn't have permission for this API or web component
    	Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "User not permitted to access: PDXCDX");
    	return new Gson().toJson(response);
    } else {
      // Process the request
      try {
        String pdxcdxSQL = "SELECT DISTINCT date_created, person_id, pdx_or_cdx, timepoint FROM PDXCDX WHERE person_id = "+personID+" ORDER BY date_created DESC";
        ResultSet rs = super.getData(pdxcdxSQL);

        Map<String,Object> pdxcdxCombo = new HashMap<>();
        List<String> pdxDates = new ArrayList<>();
        List<String> cdxDates = new ArrayList<>();

        while(rs.next()) {
          // Get the data
          createdDate = rs.getDate("date_created");
          pdx_or_cdx = rs.getString("pdx_or_cdx");
          timepoint = rs.getString("timepoint");

          // Reformat the date
          if(createdDate != null) {
            DateFormat dfcreatedDate = new SimpleDateFormat("dd/MM/yyyy");
            createdDateFormatted = dfcreatedDate.format(createdDate);
          } else {
            createdDateFormatted = "yes";
          }

          // Add to the data
          if(pdx_or_cdx.equals("PDX")) {
            System.out.println(createdDateFormatted);
            pdxDates.add(createdDateFormatted);
          }

          if(pdx_or_cdx.equals("CDX")) {
            cdxDates.add(createdDateFormatted);
          }
        }

        pdxcdxCombo.put("pdx", pdxDates);
        pdxcdxCombo.put("cdx", cdxDates);

        // Return JSON for patient treatment
        String json = new Gson().toJson(pdxcdxCombo);
        return json;

      } catch(Exception e) {
        e.printStackTrace();
        Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "Failed to get PDXCDX.");
    	return new Gson().toJson(response);
      }
    }
  }
}
