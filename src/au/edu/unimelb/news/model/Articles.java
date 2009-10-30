package au.edu.unimelb.news.model;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import au.edu.unimelb.news.dao.Article;
import au.edu.unimelb.news.dao.ArticleInfo;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.SearchResult;
import au.edu.unimelb.security.UserHelper;
import au.edu.unimelb.security.model.User;

public class Articles {

/*
	public static boolean contains(String name) {
		return publicationMap.containsKey(name);
	}

	public static Article get(String name) {
		return publicationMap.get(name);
	}
*/

	public static Article get(long id) throws IOException {
		return DAOFactory.getArticleFactory().get(id);
	}


	public static long count() throws IOException {
		return DAOFactory.getArticleFactory().countAll();
	}

	/*
	public static void add(String name) throws IOException {
		if(publicationMap.containsKey(name)) return;

		ArticleFactory publicationDao = DAOFactory.getArticleFactory();
			if(publicationDao.getByName(name, 0, 1).size()==0) {
				Publication publication = new Publication();
				publication.setName(name);
				publication = publicationDao.insert(publication);
				publicationMap.put(name, publication);
				publicationIdMap.put(publication.getId(), publication);
			}
	}
*/

	/**
	 * Return a subject entry based upon information in the http servlet request. If a category_id
	 * exists, it is loaded from the data store. If category field values are POST'ed then we load
	 * these over the category fields.
	 *
	 * @param request Web request details
	 * @return Glossary entry
	 */
	public static Article load(HttpServletRequest request) {
		Article article = null;

		try {
			article = DAOFactory.getArticleFactory().get(Long.parseLong(request.getParameter("article_id")));
		} catch(Exception e) {}
		if(article == null) {
			article = new Article();
			article.setDeleted(false);
			article.setPublished(false);
			User user = UserHelper.getUser(request);
			article.setLastUpdatePersonId(user.getPersonId());
		}

		try {
		if(request.getParameter("article_publication_id")!=null)
			article.setPublicationId(Long.parseLong(request.getParameter("article_publication_id")));
		} catch(Exception e) {}
		if(request.getParameter("article_name")!=null)
			article.setName(request.getParameter("article_name"));
		if(request.getParameter("article_byline")!=null)
			article.setByline(request.getParameter("article_byline"));
		if(request.getParameter("article_details")!=null)
			article.setDetails(request.getParameter("article_details"));

		return article;
	}

	public static String asLink(Article article) {
		return "article/" + article.getId();
	}

	public static String asLink(ArticleInfo article) {
		return "article/" + article.getId();
	}

	public static String asLink(SearchResult article) {
		return "article/" + article.getId();
	}

}
