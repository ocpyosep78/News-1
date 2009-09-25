package au.edu.unimelb.news.model;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import au.edu.unimelb.news.dao.Newsletter;
import au.edu.unimelb.news.dao.NewsletterInfo;
import au.edu.unimelb.news.dao.DAOFactory;

public class Newsletters {
	
	public static boolean contains(String name) {
		return false;
	}

	public static Newsletter get(String name) {
		return null;
	}

	public static Newsletter get(long id) {
		return null;
	}

	public static int count() {
		return -1;
	}
	
	public static void add(Newsletter newsletter) throws IOException {
		/*
		if(contains(name)) return;

		NewsletterFactory publicationDao = DAOFactory.getPublicationFactory();
			if(publicationDao.getByName(name, 0, 1).size()==0) {
				Publication publication = new Publication();
				publication.setName(name);
				publication = publicationDao.insert(publication);
			}
		*/
	}

	/**
	 * Return a subject entry based upon information in the http servlet request. If a category_id
	 * exists, it is loaded from the data store. If category field values are POST'ed then we load
	 * these over the category fields.
	 * 
	 * @param request Web request details
	 * @return Glossary entry
	 */
	public static Newsletter load(HttpServletRequest request) {
		Newsletter newsletter = null;

		try {
			newsletter = DAOFactory.getNewsletterFactory().get(Long.parseLong(request.getParameter("newsletter_id")));
		} catch(Exception e) {}
		if(newsletter == null)
			newsletter = new Newsletter();

		if(request.getParameter("newsletter_name")!=null)
			newsletter.setName(request.getParameter("newsletter_name"));

		return newsletter;
	}

	public static String asLink(Newsletter newsletter) {
		return "newsletter/" + newsletter.getId();
	}

	public static String asLink(NewsletterInfo newsletter) {
		return "newsletter/" + newsletter.getId();
	}

}
