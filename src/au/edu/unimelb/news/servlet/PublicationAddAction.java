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
package au.edu.unimelb.news.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import au.edu.unimelb.helper.StringHelper;
import au.edu.unimelb.news.Configuration;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.Publication;
import au.edu.unimelb.news.model.Publications;
import au.edu.unimelb.security.LogHelper;
import au.edu.unimelb.security.UserHelper;
import au.edu.unimelb.security.action.AuthorisationFailAction;
import au.edu.unimelb.security.model.User;

/**
 * Handles requests to create new agenda items for a speciffic meeting.
 */
public class PublicationAddAction extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	static final long serialVersionUID = 1L;   

	public PublicationAddAction() {
		super();
	}   	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}  	

	/**
	 * Reads all user input related to creating a new agenda item and creates the agenda item.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		User user = UserHelper.getUser(request);

	    Publication publication = Publications.load(request);

		StringBuffer warnings=new StringBuffer();
		warnings.append(Configuration.validator.checkField("publication_name", publication.getName()));
		if(publication.getId()==0 && DAOFactory.getPublicationFactory().countByName(publication.getName())>0)
			warnings.append("<li>A publication entry already exists with this exact name.</li>");
		
		if(warnings.length()>0) {
			session.setAttribute("errors", warnings.toString());
			getServletContext().getRequestDispatcher("/publications.jsp").forward(request, response);
			return;
		}

		if(publication.getId()==0) {
			if(!user.can("Publication","Add")) {
				AuthorisationFailAction.display(request, response, true);
				return;
			}
			DAOFactory.getPublicationFactory().insert(publication);
			LogHelper.log("Publication", "Add", user.getPersonId(), "Category <i>"+StringHelper.escapeHtml(publication.getName())+"</i> was added.", user.getIP());
			session.setAttribute("info", "Category <i>"+StringHelper.escapeHtml(publication.getName())+"</i> has been saved.");
		}

		getServletContext().getRequestDispatcher("/publications.jsp").forward(request, response);
	}



}