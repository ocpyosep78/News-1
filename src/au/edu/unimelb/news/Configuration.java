/*
 * Copyright (c)2008 The University of Melbourne, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of The
 * University of Melbourne. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms and conditions of contractual and/or employment
 * policy of the university.
 *
 * THE UNIVERSITY OF MELBOURNE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE UNIVERSITY OF MELBOURNE SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package au.edu.unimelb.news;

import java.io.InputStream;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import au.edu.unimelb.validator.Validator;

/**
 * All global configuration information is stored within this class. Some of
 * the configurable information is loaded from the context.xml file by the
 * LifecycleHandler class.
 * 
 * @see LifecycleListener
 */
public class Configuration {

	public static String appPrefix="";
	private static String attachmentFolder="/tmp";
	private static String pdfFolder="/tmp";
	private static String[] status={"Preliminary","Final","Revised Final"};
	private static String smtpServer="smtp.unimelb.edu.au";
	private static String databaseId = "Unknown";

	private static String ldapServer = "centaur.unimelb.edu.au";
	private static String ldapContext = "ou=people,o=unimelb";
	public static Validator validator=null;

	private static Hashtable<String,String> testUsers=null;
	private static final String buildString="0.1";

	public static String getAttachmentFolder() {
		return attachmentFolder;
	}

	public static void setAttachmentFolder(String attachmentFolder) {
		Configuration.attachmentFolder = attachmentFolder;
	}

	public static String getPdfFolder() {
		return pdfFolder;
	}

	public static void setPdfFolder(String pdfFolder) {
		Configuration.pdfFolder = pdfFolder;
	}

	public static String getDatabaseId() {
		return databaseId;
	}

	public static void setDatabaseId(String databaseId) {
		Configuration.databaseId = databaseId;
	}

	public static String[] getStatus() {
		return status;
	}	

	public static String getStatus(int index) {
		return status[index];
	}	

	public static String getSmtpServer() {
		return Configuration.smtpServer;
	}

	public static void setSmtpServer(String smtp) {
		if(smtp!=null && smtp.length()>5)
			Configuration.smtpServer=smtp;
		else {
			System.err.println("Misconfiguration: Please specify a valid smtp server");
		}
	}

	public static String getLdapServer() {
		return ldapServer;
	}

	public static String getLdapContext() {
		return ldapContext;
	}

	/***
	 * Initializes the testUsers table using the list of usernames and password passed in.
	 * @param initString This is the list of usernames and passwords. A username/password pair
	 * is concatenated using the forward slash character '/'. Each username/password pair is 
	 * separated from another pair by the space character. A password can have a forward slash in it
	 * but not a space character.
	 */
	public static void initializeTestUsers(String initString) {
		if (initString==null || initString=="") {
			testUsers = null;
			return;
		}
		String[] userList = initString.split(" ");
		testUsers = new Hashtable<String,String>();
		for (int i=0; i<userList.length; i++) {
			int separatorIdx = userList[i].indexOf("/");
			if (separatorIdx>=0) {
				String username = userList[i].substring(0, separatorIdx);
				String password = userList[i].substring(separatorIdx+1, userList[i].length());
				testUsers.put(username, password);
			}
		}
	}

	public static boolean authenticateTestUser(String username, String password) {
		if (testUsers==null)
			return false;
		
		if (testUsers.containsKey(username)) {
			return (testUsers.get(username).equals(password));
		} else
			return false;
	}

	public static String getBuildNumber() {
		return buildString;
	}

	public static void loadValidator(ServletContext appContext) throws ServletException {
		InputStream validationStream;
		try {
			validationStream = Configuration.class.getClassLoader().
			getResource("/validations.xml").openStream();
			validator=new Validator(validationStream);
		} catch (Exception e) {
			throw new ServletException("Problem loading validations. "+e.toString());
		}
	}

}
