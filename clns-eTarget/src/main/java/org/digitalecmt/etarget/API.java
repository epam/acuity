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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.dao.UserAccessDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;
import java.nio.file.*;

public class API {
	private static final Logger log = Logger.getLogger(API.class.getName());
	
	private ApplicationContext appContext;
	
	 private ResourceBundle resource;
  // Constructor
  public API() {
	  resource = ResourceBundle.getBundle("config");
	  appContext= new AnnotationConfigApplicationContext(TargetConfiguration.class);
  }

  protected ApplicationContext getContext() {
	  return appContext;
  }
  
  public boolean isUserPermittedEndpoint(String userID, String endpoint) {
	  UserAccessDAO userAccess=this.getContext().getBean(UserAccessDAO.class);
	  return userAccess.isUserAllowedEndpoint(userID, endpoint);	  
  }
  
  public boolean isUserPermittedComponent(String userID, String component) {
	  UserAccessDAO userAccess=this.getContext().getBean(UserAccessDAO.class);
	  return userAccess.isUserAllowedComponent(userID, component);  
  }
    
  public boolean checkAccessPermission(String component, String userID, String personID) {
	  UserAccessDAO userAccess=this.getContext().getBean(UserAccessDAO.class);
//	  log.info("component " + component);
//	  log.info("userID " + userID);
//	  log.info("personID " + personID);
	  //components nothing to do with editing
	  if(component.equals("patienttable")|| 
			  component.equals("patienttable_search") || 
			  component.equals("reportextraction") || 
			  component.equals("adminlink") ) {
		  return true;
	  }
	  //check if userID is editor
	  if(userID==null || userID.isEmpty() || userID.equals("undefined")) {
		  log.severe("can't check user role -- userID is empty");
		  return false;
	  }
	  
	  if(!userAccess.isUserOfType(userID, "Editor") && !userAccess.isUserOfType(userID, "Chief Investigator")) {
		  return true;
	  }

	  if(personID==null || personID.isEmpty()) {
		  log.severe("PersonID not present");
		  return false;
	  }
	  EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
	  try {
		  //need the opposite true if editable not if locked
		  if(component.equals("tumourcheckbox") || 
				  component.equals("ctdnangscheckbox") || 
				  component.equals("ctdnaexpcheckbox") || 
				  component.equals("fmtumourcheckbox") ||
				  component.equals("fmbloodcheckbox") ||
				  component.equals("genericgenomiccheckbox") ||
				  component.equals("meetingoutcome") ||
				  component.equals("editmeetingoutcome")) {
			  log.info("return from is locked " +editLockDao.isLocked(userID, Integer.parseInt(personID)));
			  return !editLockDao.isLocked(userID, Integer.parseInt(personID));
		  } else {
			  log.info("return from is locked " +editLockDao.isLocked(userID, Integer.parseInt(personID)));
			  
			  return editLockDao.isLocked(userID, Integer.parseInt(personID));
		  }
	  } catch (NumberFormatException ex) {
		  log.severe(ex.getMessage());
		  return false;
	  }
  }

  // Database connection
  protected Connection dbConnection() {
    // Get the database details from the config
    java.sql.Connection con = null;
    String url = resource.getString("databaseURL");
    String id = resource.getString("dbuser");
    String pass = resource.getString("dbpassword");

    try {
      Class.forName("net.sourceforge.jtds.jdbc.Driver");
      con = java.sql.DriverManager.getConnection(url, id, pass);
      return con;

    } catch(Exception e) {
      System.out.println("Method API::dbConnection failed");
      System.out.println(e);
    }

    return con;
  }

  // Get data from the database
  protected ResultSet getData(String sql) {
    java.sql.Connection con = null;
    java.sql.Statement s = null;
    java.sql.ResultSet rs = null;

    try {
      con = this.dbConnection();

      if(con != null) {
        s = con.createStatement();
        rs = s.executeQuery(sql);
      } else {
        System.out.println("No database connection.");
        throw new java.lang.Exception();
      }

    } catch(Exception e) {
      System.out.println("Method API::getData failed");
      System.out.println(e);
    } finally {
      //try { rs.close(); } catch (Exception e) {}
      //try { s.close(); } catch (Exception e) {}
      //try { con.close(); } catch (Exception e) {}
    }

    return rs;
  }

  // Send data to the database: INSERT
  protected boolean insertData(String table, Map<String, String> data) {
    // Build the insert statement
    java.sql.Connection con = null;
    PreparedStatement statement = null;
    String sql = "INSERT INTO "+table+" ";
    String keys = "(";
    String values = "(";
    int index = 1;

    for(Map.Entry<String, String> d: data.entrySet()) {
        keys = keys+d.getKey()+",";
        values = values+"?,";
    }

    keys = keys.substring(0, keys.length() -1);
    values = values.substring(0, values.length() -1);

    keys = keys+")";
    values = values+")";

    sql = sql+keys+" VALUES"+values;

    //System.out.println(sql);

    try {
      // Set the prepared statement then execute
      con = this.dbConnection();
      statement = con.prepareStatement(sql);
      for(Map.Entry<String, String> d: data.entrySet()) {
          statement.setString(index, d.getValue());
          index = index+1;
      }
      statement.execute();

      return true;
    } catch(Exception e) {
    	log.log(Level.SEVERE, e.getMessage(), e);
      System.out.println("Method API::insertData failed");
      System.out.println(e);

      return false;
    } finally {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
      try { con.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  // Send data to the database: UPDATE
  protected boolean updateData(String updateSQL) {
    java.sql.Connection con = null;
    java.sql.Statement s = null;

    try {
      con = this.dbConnection();
      s = con.createStatement();
      s.executeUpdate(updateSQL);

      return true;

    } catch(Exception e) {
      System.out.println("Method API::updateData failed");
      System.out.println(e);
      return false;

    } finally {
      try { s.close(); } catch (Exception e) {}
      try { con.close(); } catch (Exception e) {}

    }
  }

  // Translate amino acid string from 3 letter to one letter abbreviation
  protected String translateGene(String geneResult) {
    Map<String, String> convertAminoCode = new HashMap<String, String>();
    convertAminoCode.put("Gly", "G");
    convertAminoCode.put("Ala", "A");
    convertAminoCode.put("Leu", "L");
    convertAminoCode.put("Met", "M");
    convertAminoCode.put("Phe", "F");
    convertAminoCode.put("Trp", "W");
    convertAminoCode.put("Lys", "K");
    convertAminoCode.put("Gln", "Q");
    convertAminoCode.put("Glu", "E");
    convertAminoCode.put("Ser", "S");
    convertAminoCode.put("Pro", "P");
    convertAminoCode.put("Val", "V");
    convertAminoCode.put("Ile", "I");
    convertAminoCode.put("Cys", "C");
    convertAminoCode.put("Tyr", "Y");
    convertAminoCode.put("His", "H");
    convertAminoCode.put("Arg", "R");
    convertAminoCode.put("Asn", "N");
    convertAminoCode.put("Asp", "D");
    convertAminoCode.put("Thr", "T");

    // Iterate over the Map and replace all instances in the string
    for (Map.Entry<String, String> entry : convertAminoCode.entrySet()) {
      String caseInsensitiveKey = "(?i)"+entry.getKey();
    	geneResult = geneResult.replaceAll(caseInsensitiveKey, entry.getValue());
    }

    // Return the result
    return geneResult;
  }

  public String fetchHTMLComponent(String component, String userID, String personID) {
    //// This implements a permission system for sections of the web application
    // Check the current user has permission for the requested component
	  Date start = Calendar.getInstance().getTime();
    if(this.isUserPermittedComponent(userID, component) && this.checkAccessPermission(component, userID, personID)) {
      // Return the requested block of HTML
      try {
    	  Date step1 = Calendar.getInstance().getTime();
        String content = "";
        String path = resource.getString("webapp.include.path");
        //String path = System.getProperty("catalina.base")+"\\site\\wwwroot\\webapps\\ROOT\\includes\\";
        //String path = "D:\\home\\site\\wwwroot\\webapps\\ROOT\\includes\\";
        log.info("include path " + path);
        Date step2 = Calendar.getInstance().getTime();
        content = new String(Files.readAllBytes(Paths.get(path+component+".html")));
        Date finish = Calendar.getInstance().getTime();
        return content;
      } catch(Exception e) {
        System.out.println(e);
        e.printStackTrace();
        log.log(Level.SEVERE, "error", e);
        return "An error occurred fetching the HTML component.";
      }
    } else {
      // Return an error message
      String message = "";
      /*
      if(component.equals("patienttable") || component.equals("reportextraction") || component.equals("admin")) {
        message = "<p>You do not have permission to access this section of eTarget. Please contact the admin for access.</p>";
      } else {
        message = "";
      }
      */

      // Check if this user's account is deactivated
      if(component.equals("patienttable") || component.equals("patienttable_search") || component.equals("reportextraction") || component.equals("admin") || component.equals("genepanel")) {
        if(this.isUserDeactivated(userID)) {
          message = "Your account has been deactivated. Please contact the MTB Chair if you require access.";
          return message;
        }
        if(!this.hasUserAccount(userID)) {
          message = "You do not have an account. Please contact the MTB Chair if you require access.";
          return message;
        }
      }
      return message;
    }
  }

  public boolean isUserDeactivated(String userID) {
	  UserAccessDAO userAccess=this.getContext().getBean(UserAccessDAO.class);
	  return userAccess.isUserDeactivated(userID);
  }
  
  protected boolean isAdmin(String userID) {
	  UserAccessDAO userAccess=this.getContext().getBean(UserAccessDAO.class);
	  return userAccess.isAdmin(userID);
  }
  
  protected boolean isChiefInvestigator(String userID) {
	  UserAccessDAO userAccess=this.getContext().getBean(UserAccessDAO.class);
	  return userAccess.isChiefInvestigator(userID);
  }
  
  public boolean hasUserAccount(String userID) {
	  UserAccessDAO userAccess=this.getContext().getBean(UserAccessDAO.class);
	  return userAccess.hasUserAccount(userID);
  }
   
}
