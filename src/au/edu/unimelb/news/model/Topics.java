package au.edu.unimelb.news.model;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import au.edu.unimelb.news.dao.TopicFactory;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.Topic;

public class Topics {
	
	private static Map<String,Topic> topicMap = new Hashtable<String, Topic>();
	private static Map<Long,Topic> topicIdMap = new Hashtable<Long,Topic>();
	
	static {
		try {
			for(Topic category : DAOFactory.getTopicFactory().getAll(0, 500)) {
				topicMap.put(category.getName(),category);
				topicIdMap.put(category.getId(),category);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean contains(String name) {
		return topicMap.containsKey(name);
	}

	public static Topic get(String name) {
		return topicMap.get(name);
	}

	public static Topic get(long id) {
		return topicIdMap.get(id);
	}

	public static void add(String name) throws IOException {
		Topics.add(0,name);
	}

	public static int count() {
		return topicMap.size();
	}

	public static void add(int parentId,String name) throws IOException {
		if(topicMap.containsKey(name)) return;

			TopicFactory topicDao = DAOFactory.getTopicFactory();
			if(topicDao.getByName(name, 0, 1).size()==0) {
				Topic category = new Topic();
				category.setName(name);
				category = topicDao.insert(category);
				topicMap.put(name, category);
				topicIdMap.put(category.getId(), category);
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
	public static Topic load(HttpServletRequest request) {
		Topic category = null;

		try {
			category = DAOFactory.getTopicFactory().get(Long.parseLong(request.getParameter("topic_id")));
		} catch(Exception e) {}
		if(category == null)
			category = new Topic();

		if(request.getParameter("topic_name")!=null)
			category.setName(request.getParameter("topic_name"));

		return category;
	}

}
