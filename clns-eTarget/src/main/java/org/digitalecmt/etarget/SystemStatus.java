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
import com.google.gson.*;

public class SystemStatus extends API {
  // Vars
  String userID;
  String fileName;
  String inputType;
  String message;
  String messageTime;

  // Constructor
  public SystemStatus(String loggedInUserID) {
    userID = loggedInUserID;
  }

  public String processRequest() {
    if(!super.isUserPermittedEndpoint(userID, "SystemStatus")) {
      // Stop the request if user doesn't have permission for this API or web component
    	Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "User not permitted to access: SystemStatus");
    	return new Gson().toJson(response);
    } else {
      // Process the request
      try {
        String sql = "SELECT * FROM dbo.SYSTEM_STATUS ORDER BY ss_id DESC";
        ResultSet rs = super.getData(sql);

        List<Map<String,String>> systemData = new ArrayList<>();

        while(rs.next()) {
          Map<String,String> systemEntry = new HashMap<>();

          // Get the data
          fileName = rs.getString("filename");
          inputType = rs.getString("input_type");
          message = rs.getString("message");
          messageTime = rs.getString("message_time");

          // Add to a list
          systemEntry.put("filename", fileName);
          systemEntry.put("inputType", inputType);
          systemEntry.put("message", message);
          systemEntry.put("messageTime", messageTime);

          systemData.add(systemEntry);
        }

        // Return JSON of single patient
        String json = new Gson().toJson(systemData);
        return json;

      } catch(Exception e) {
        e.printStackTrace();
        Map<String,String> response = new HashMap<>();
    	response.put("success", "false");
    	response.put("error", "Failed to get SystemStatus.");
    	return new Gson().toJson(response);
      }
    }
  }
}
