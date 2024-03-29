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
import au.edu.unimelb.news.dao.Article;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.model.Articles;
import au.edu.unimelb.security.LogHelper;
import au.edu.unimelb.security.UserHelper;
import au.edu.unimelb.security.action.AuthorisationFailAction;
import au.edu.unimelb.security.model.User;

/**
 * Handles requests to create new agenda items for a speciffic meeting.
 */
public class ArticlePublishAction extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	static final long serialVersionUID = 1L;

	public ArticlePublishAction() {
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

		Article article = Articles.load(request);

		if(!user.can("Publication","ArticleUpdate",article.getPublicationId())) {
			AuthorisationFailAction.display(request, response, true);
			return;
		}

		article = DAOFactory.getArticleFactory().get(article.getId());
		if(!article.isPublished()) {
			article.setPublished(true);
			DAOFactory.getArticleFactory().update(article);
			LogHelper.log("Publication", "ArticleUpdate", user.getPersonId(), "Article <i>"+StringHelper.escapeHtml(article.getName())+"</i> is now published.", user.getIP());
		}
		session.setAttribute("info", "Article <i>"+StringHelper.escapeHtml(article.getName())+"</i> is now published.");

		getServletContext().getRequestDispatcher("/article.jsp").forward(request, response);
	}



}