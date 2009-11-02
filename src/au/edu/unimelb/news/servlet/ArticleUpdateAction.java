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
public class ArticleUpdateAction extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	static final long serialVersionUID = 1L;

	public ArticleUpdateAction() {
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

		StringBuffer warnings=new StringBuffer();
		warnings.append(Configuration.validator.checkField("article_title", article.getName()));
		warnings.append(Configuration.validator.checkField("article_byline", article.getByline()));
		warnings.append(Configuration.validator.checkField("article_summary", article.getIntroduction()));
		warnings.append(Configuration.validator.checkField("article_details", article.getDetails()));

		/*
		if(article.getId()==0 && DAOFactory.getArticleFactory().countByName(publication.getName())>0)
			warnings.append("<li>A publication entry already exists with this exact name.</li>");
		 */

		if(warnings.length()>0) {
			session.setAttribute("errors", warnings.toString());
			getServletContext().getRequestDispatcher("/new_article.jsp").forward(request, response);
			return;
		}

		if(!user.can("Publication","ArticleUpdate",article.getPublicationId())) {
			AuthorisationFailAction.display(request, response, true);
			return;
		}
		DAOFactory.getArticleFactory().update(article);
		LogHelper.log("Publication", "ArticleUpdate", user.getPersonId(), "Article <i>"+StringHelper.escapeHtml(article.getName())+"</i> was updated.", user.getIP());
		session.setAttribute("info", "Article <i>"+StringHelper.escapeHtml(article.getName())+"</i> has been saved.");

		getServletContext().getRequestDispatcher("/article.jsp").forward(request, response);
	}



}