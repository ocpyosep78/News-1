/*
 * Copyright (c)2007 The University of Melbourne, Inc. All Rights Reserved.
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
package au.edu.unimelb.news.model;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import au.edu.unimelb.helper.StringHelper;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.ArticleInfo;
import au.edu.unimelb.security.Settings;

/**
 * Handles requests to create new agenda items for a speciffic meeting.
 */
public class RssServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	static final long serialVersionUID = 1L;   

	public RssServlet() {
		super();
	}   	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}  	

	/**
	 * Reads all user input related to creating a new agenda item and creates the agenda item.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();

		recentUpdates(request, out);
	}

	protected void recentUpdates(HttpServletRequest request, PrintWriter out) throws IOException {
		out.println("<!DOCTYPE rss PUBLIC \"-//Netscape Communications//DTD RSS 0.91//EN\" \"http://f2.com.au/dtd/rss-0.91.dtd\">");
		out.println("<rss version=\"0.91\">");
		out.println("<channel>");
		out.println("<title>Recently published or updated policy documents</title>");
		out.println("<link>http://policy.unimelb.edu.au/</link>");
		out.println("<description>University of Melbourne policy documents that have been recently published or updated.</description>");
		out.println("<language>en-au</language>");

		for(ArticleInfo document : DAOFactory.queryArticleRecentlyUpdated(0,20)) {
			out.println("<item id=\""+document.getId()+"\">");
			out.println("<link>http://"+request.getLocalName()+Settings.baseUrl+"/"+document.getId()+"</link>");
			out.println("<title>"+StringHelper.escapeHtml(document.getName())+" ("+document.getId()+")</title>");
			out.println("<pubDate/>");
			out.println("</item>");
		}

		out.println("</channel>");
		out.println("</rss>");
	}

	protected void popularPolicy(HttpServletRequest request, PrintWriter out) throws IOException {
		out.println("<!DOCTYPE rss PUBLIC \"-//Netscape Communications//DTD RSS 0.91//EN\" \"http://f2.com.au/dtd/rss-0.91.dtd\">");
		out.println("<rss version=\"0.91\">");
		out.println("<channel>");
		out.println("<title>Most viewed policy documents</title>");
		out.println("<link>http://policy.unimelb.edu.au/</link>");
		out.println("<description>University of Melbourne policy documents that are most commonly referenced.</description>");
		out.println("<language>en-au</language>");

		for(ArticleInfo document : DAOFactory.queryArticleMostPopular(0, 20)) {
			out.println("<item id=\""+document.getId()+"\">");
			out.println("<link>http://"+request.getLocalName()+Settings.baseUrl+"/"+document.getId()+"</link>");
			out.println("<title>"+StringHelper.escapeHtml(document.getName())+" ("+document.getId()+")</title>");
			out.println("<pubDate/>");
			out.println("</item>");
		}

		out.println("</channel>");
		out.println("</rss>");
	}

}