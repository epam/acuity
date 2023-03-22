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

public class Admin extends API {
	// Vars
	String personID;
	String userID;
	String userEmail;
	int whitelistID;
	int roleID;
	Map<Integer, Map<String, String>> users = new HashMap<Integer, Map<String, String>>();

	// Constructor
	public Admin(String loggedInUserID) {
		userID = loggedInUserID;
	}

	public String listUsers() {
		if (!super.isUserPermittedEndpoint(userID, "Admin")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			Map<String, String> response = new HashMap<>();
			response.put("success", "false");
			response.put("error", "User not permitted to access: Admin");
			return new Gson().toJson(response);
		} else {
			// Process the request
			try {
				String sql = "SELECT * FROM dbo.WHITELIST";
				ResultSet rs = super.getData(sql);

				while (rs.next()) {
					// Get the data
					whitelistID = rs.getInt("whitelistID");
					userEmail = rs.getString("userID");
					roleID = rs.getInt("roleID");

					// Add to the user list
					Map<String, String> user = new HashMap<String, String>();
					user.put("email", userEmail);
					user.put("roleID", Integer.toString(roleID));
					users.put(whitelistID, user);
				}

				String json = new Gson().toJson(users);
				return json;

			} catch (Exception e) {
				e.printStackTrace();
				Map<String, String> response = new HashMap<>();
				response.put("success", "false");
				response.put("error", "Failed to get users from WHITELIST.");
				return new Gson().toJson(response);
			}
		}
	}

	public String addUser(String email, String roleID) {
		Map<String, String> responseData = new HashMap<String, String>();
		try {
			Map<String, String> sendData = new HashMap<String, String>();
			sendData.put("userID", email);
			sendData.put("roleID", roleID);

			// Otherwise add the user
			if (super.insertData("WHITELIST", sendData)) {
				responseData.put("success", "true");
			} else {
				responseData.put("success", "false");
			}

			String json2 = new Gson().toJson(responseData);
			return json2;

		} catch (Exception e) {
			e.printStackTrace();
			responseData.put("success", "false");
			responseData.put("error", "Failed to add user to WHITELIST.");
			return new Gson().toJson(responseData);
		}
	}

	public String updateUser(String whitelistID, String roleID) {
		try {
			if (!super.isUserPermittedEndpoint(userID, "Admin")) {
				// Stop the request if user doesn't have permission for this API or web
				// component
				Map<String, String> response = new HashMap<>();
				response.put("success", "false");
				response.put("error", "User not permitted to access: Admin");
				return new Gson().toJson(response);
			} else {
				Map<String, String> responseData = new HashMap<String, String>();

				// Otherwise update the user role
				String updateUserSQL = "UPDATE WHITELIST SET roleID = " + roleID + " WHERE whitelistID=" + whitelistID;
				super.updateData(updateUserSQL);

				responseData.put("success", "true");
				String json = new Gson().toJson(responseData);
				return json;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> response = new HashMap<>();
			response.put("success", "false");
			response.put("error", "Failed to update user in WHITELIST.");
			return new Gson().toJson(response);
		}
	}
}
