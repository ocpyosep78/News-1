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
package au.edu.unimelb.news;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import au.edu.unimelb.security.Settings;

//import au.edu.unimelb.policy.model.Categories;

/**
 * The AuthorisationFilter is a JEE servlet filter, that is called before the loading
 * of every page/document. This filter intercepts requests and does the following:
 * <ul>
 * <li>Ensure all page requests are attached to a valid user.</li>
 * <li>Redirect invalid users to a sign-in page</li>
 * <li>Re-write meeting page URL to remove get variable (for unimelb template support)</li>
 * </ul>
 *
 */
public class UrlFilter implements Filter {

	/**
	 * Filter should be configured with an system error page.
	 */
	public void init (FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * Obtain user from current session and invoke a singleton AuthorizationManager to determine if
	 * user is authorised for the requested resource. If not, forward them to a standard error page.
	 */
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain)
	throws ServletException, IOException {

		String uri = ((HttpServletRequest)request).getRequestURI().substring(Settings.baseUrl.length()+1);

		String[] parts=uri.split("/");
		if(parts[0].equals("topic") && parts.length > 1) {
			request.getRequestDispatcher("/topic.jsp?topic="+parts[1]).forward(request,response);
			return;
		}
		if(parts[0].equals("article") && parts.length > 1) {
			request.getRequestDispatcher("/article.jsp?article_id="+parts[1]).forward(request,response);
			return;
		}
		if(parts[0].equals("publication") && parts.length > 1) {
			request.getRequestDispatcher("/publication.jsp?name="+parts[1]).forward(request,response);
			return;
		}
		if(parts[0].equals("newsletter") && parts.length > 1) {
			request.getRequestDispatcher("/newsletter.jsp?newsletter_id="+parts[1]).forward(request,response);
			return;
		}
		if(parts[0].toLowerCase().startsWith("uom")) {
			request.getRequestDispatcher("/document.jsp?number="+parts[0]).forward(request,response);
			return;
		}
		if(parts[0].equals("browse") && parts.length > 1) {
			request.getRequestDispatcher("/browse.jsp?section="+parts[1]).forward(request,response);
			return;
		}

		chain.doFilter(request,response);
	}

	public void destroy(){}

}
