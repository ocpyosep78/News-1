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

import java.io.File;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import au.edu.unimelb.feed.Feed;
import au.edu.unimelb.feed.FeedManager;
import au.edu.unimelb.news.feed.NewsFeed;
import au.edu.unimelb.news.feed.NewsletterFeed;
import au.edu.unimelb.news.model.Topics;
import au.edu.unimelb.news.resource.PublicationResourceManager;
import au.edu.unimelb.security.AdministrationMenu;
import au.edu.unimelb.security.AdministrationMenuItem;
import au.edu.unimelb.security.LogHelper;
import au.edu.unimelb.security.Settings;
import au.edu.unimelb.security.resource.ResourceFactory;
import au.edu.unimelb.template.LayoutHelper;

/**
 * The life cycle handler is called on application startup and shutdown.
 * This life cycle handler, ensures the following tasks are completed on
 * application startup:
 *
 * <ul>
 * <li>Read system configuration parameters into the global Configuration
 *     class</li>
 * <li>Configure web page template to contain required information such as
 *     system name Authorisor name, and so on.</li>
 * <li>If the database committee table is empty, pre-fill it with the valid
 *     committee information.</li>
 * </ul>
 */
public class LifecycleListener implements ServletContextListener {


	public void contextInitialized(ServletContextEvent sce)  {
		LogHelper.log("System", "startup", 0, "Application startup", "");

		loadConfigurationSettings(sce);
		loadDatabaseId();
		initialiseLayoutHelper();
		initialiseDatabaseContents();
		initialiseFeeds();

		ResourceFactory.addResourceManager(new PublicationResourceManager());
	}

	public void contextDestroyed(ServletContextEvent sce)  {
		LogHelper.log("System", "shutdown", 0, "Application shutdown", "");
	}

	/**
	 * Reads configuration parameters into the global Configuration class.
	 *
	 * @param sce Software startup parameter object
	 */
	private void loadConfigurationSettings(ServletContextEvent sce) {
		Configuration.appPrefix=sce.getServletContext().getContextPath();
		try {
			Configuration.loadValidator(sce.getServletContext());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Configuration.setSmtpServer(sce.getServletContext().getInitParameter("smtp.host"));
		Configuration.setSmtpPort(sce.getServletContext().getInitParameter("smtp.port"));
		Configuration.setApplicationUrl(sce.getServletContext().getInitParameter("application.url"));

		// Detect the tomcat home folder
		String catalinaHome = null;
		for(Object item : System.getProperties().keySet()) {
			String key = (String) item;
			if(key.equals("catalina.home"))
				catalinaHome = System.getProperty(key);
			//System.out.println(key+" = "+ System.getProperty(key));
		}
		if(catalinaHome == null)
			System.err.println("Unable to find catalina.home in the Java System Properties. Could not establish default data folder.");

		// Ensure the data folder exists
		File home = new File(catalinaHome);
		File data = new File(home.getAbsolutePath()+"/data/");
		File applicationData = new File(data.getAbsolutePath()+"/"+sce.getServletContext().getServletContextName()+"/");
		if(!data.exists()) data.mkdir();
		if(!applicationData.exists()) applicationData.mkdir();

		// Ensure our app folders exist
		File pdfFolder = new File(applicationData.getAbsoluteFile()+"/pdfs/");
		if(!pdfFolder.exists()) pdfFolder.mkdir();
		File attachmentFolder = new File(applicationData.getAbsoluteFile()+"/attachments/");
		if(!attachmentFolder.exists()) attachmentFolder.mkdir();

		Configuration.setAttachmentFolder(applicationData.getAbsolutePath()+"/attachments/");
		Configuration.setPdfFolder(applicationData.getAbsolutePath()+"/pdfs/");
	}

	private void loadDatabaseId() {
		Context ctx=null;
		try {
			ctx = new InitialContext();
			if(ctx==null) return;
			DataSource dataSource=(DataSource)ctx.lookup("java:comp/env/jdbc/news");
			if(dataSource==null) return;
			String dbUsername = dataSource.getConnection().getMetaData().getUserName();
			if(dbUsername.contains("@")) dbUsername=dbUsername.substring(0,dbUsername.indexOf('@'));
			Configuration.setDatabaseId(dbUsername);
		} catch(Exception e) {
			return;
		}
	}

	/**
	 * Initialises the LayoutHelper with variables specific to this application.
	 */
	private void initialiseLayoutHelper() {

		Settings.appVersion = Configuration.getBuildNumber();
		Settings.defaultPage = "index.jsp";
		Settings.breadcrumbHtml = "<a href=\"http://www.unimelb.edu.au/\">University Home</a> &gt; "+
		"<a href=\""+Configuration.appPrefix+"/\">News</a> &gt; ";

		try {
			Settings.setAuthentication("IMAP");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Initialise the layout helper
		LayoutHelper.setSystemName("University News (beta)");
		LayoutHelper.setSystemTag("Uninews");

		LayoutHelper.setSiteCreationDate("July 2, 2009");
		LayoutHelper.setMaintainerName("Application Services, Information Technology Services");
		LayoutHelper.setMaintainerEmail("mailto:it-help@unimelb.edu.au");

		// Inform the template manager of new items to display on the administration page
		AdministrationMenuItem menu = new AdministrationMenuItem();
		menu.name="Import";
		menu.description="Load previously exported data files";
		menu.link="import.jsp";
		AdministrationMenu.add(menu);

		LayoutHelper.addMenuItem("Browse", "browse/ABC", false, false, 10);
		LayoutHelper.addMenuItem("Browse", "browse/ABC", false, true, 10);

		LayoutHelper.addMenuItem("Faculties", "http://www.unimelb.edu.au/az/faculties.html", false, false, 60);
		LayoutHelper.addMenuItem("A-Z Directory", "http://www.unimelb.edu.au/az/index.html", false, false, 64);
		LayoutHelper.addMenuItem("Library", "http://www.library.unimelb.edu.au/", false, false, 68);

	}

	/**
	 * If database is missing pre-loaded default values, load database
	 * with default values.
	 */
	private void initialiseDatabaseContents() {

		/*
		 * Here we attempt to add all required categories to the
		 * database. Only stores new categories if they don't
		 * already exist.
		 */
		try {
			Topics.add("Business");
			Topics.add("Sport");
			Topics.add("Politics");
			Topics.add("Law");
			Topics.add("Technology");
			Topics.add("Science & Nature");
			Topics.add("Health & Medicine");
			Topics.add("Entertainment");
			Topics.add("Education");
			Topics.add("Arts");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initialiseFeeds() {
		Feed newsletterFeed = new NewsletterFeed(Configuration.fullUrl);
		Feed newsFeed = new NewsFeed(Configuration.fullUrl);

		FeedManager.registerFeed(newsletterFeed);
		FeedManager.registerFeed(newsFeed);
	}

}
