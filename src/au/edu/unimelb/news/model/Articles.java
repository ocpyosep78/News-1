package au.edu.unimelb.news.model;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import au.edu.unimelb.news.dao.Article;
import au.edu.unimelb.news.dao.ArticleInfo;
import au.edu.unimelb.news.dao.PublicationFactory;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.Publication;
import au.edu.unimelb.news.dao.SearchResult;

public class Articles {
	
	private static Map<String,Publication> publicationMap = new Hashtable<String, Publication>();
	private static Map<Long,Publication> publicationIdMap = new Hashtable<Long,Publication>();
	
	static {
		try {
			for(Publication publication : DAOFactory.getPublicationFactory().getAll(0, 500)) {
				publicationMap.put(publication.getName(),publication);
				publicationIdMap.put(publication.getId(),publication);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static boolean contains(String name) {
		return publicationMap.containsKey(name);
	}

	public static Publication get(String name) {
		return publicationMap.get(name);
	}

	public static Publication get(long id) {
		return publicationIdMap.get(id);
	}

	public static int count() {
		return publicationMap.size();
	}
	
	public static void add(String name) throws IOException {
		if(publicationMap.containsKey(name)) return;

		PublicationFactory publicationDao = DAOFactory.getPublicationFactory();
			if(publicationDao.getByName(name, 0, 1).size()==0) {
				Publication publication = new Publication();
				publication.setName(name);
				publication = publicationDao.insert(publication);
				publicationMap.put(name, publication);
				publicationIdMap.put(publication.getId(), publication);
			}
	}

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
		if(article == null)
			article = new Article();

		if(request.getParameter("article_name")!=null)
			article.setName(request.getParameter("article_name"));

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
