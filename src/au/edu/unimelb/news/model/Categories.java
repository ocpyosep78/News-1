package au.edu.unimelb.news.model;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import au.edu.unimelb.news.dao.CategoryFactory;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.Category;

public class Categories {
	
	private static Map<String,Category> categoryMap = new Hashtable<String, Category>();
	private static Map<Long,Category> categoryIdMap = new Hashtable<Long,Category>();
	
	static {
		try {
			for(Category category : DAOFactory.getCategoryFactory().getAll(0, 500)) {
				categoryMap.put(category.getName(),category);
				categoryIdMap.put(category.getId(),category);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static boolean contains(String name) {
		return categoryMap.containsKey(name);
	}

	public static Category get(String name) {
		return categoryMap.get(name);
	}

	public static Category get(long id) {
		return categoryIdMap.get(id);
	}

	public static void add(String name) throws IOException {
		Categories.add(0,name);
	}

	public static int count() {
		return categoryMap.size();
	}
	
	public static void add(int parentId,String name) throws IOException {
		if(categoryMap.containsKey(name)) return;

			CategoryFactory categoryDao = DAOFactory.getCategoryFactory();
			if(categoryDao.getByName(name, 0, 1).size()==0) {
				Category category = new Category();
				category.setName(name);
				category = categoryDao.insert(category);
				categoryMap.put(name, category);
				categoryIdMap.put(category.getId(), category);
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
	public static Category load(HttpServletRequest request) {
		Category category = null;

		try {
			category = DAOFactory.getCategoryFactory().get(Long.parseLong(request.getParameter("category_id")));
		} catch(Exception e) {}
		if(category == null)
			category = new Category();

		if(request.getParameter("category_name")!=null)
			category.setName(request.getParameter("category_name"));
		if(request.getParameter("subject_name")!=null)
			category.setName(request.getParameter("subject_name"));

		return category;
	}

}
