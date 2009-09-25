package au.edu.unimelb.news.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.Publication;
import au.edu.unimelb.news.model.Publications;
import au.edu.unimelb.security.resource.Resource;
import au.edu.unimelb.security.resource.ResourceManager;

public class PublicationResourceManager extends ResourceManager{

	private static String[] actions = { "ViewUnpublished","ViewPublished","ViewArchived","Add","Update","Delete","ArticleCreate","ArticleUpdate","ArticleDelete", "Publish" };

	public String[] getActions() {
		return actions;
	}

	public String getResourceType() {
		return "Publication";
	}

	public long count() throws IOException {
		return Publications.count();
	}

	public List<Resource> find(String arg0) throws IOException {
		return getAll();
	}

	public Resource get(String uid) throws IOException {
		Publication c = Publications.get(Integer.parseInt(uid));
		Resource resource = new PublicationResource();
		resource.setUid(uid);
		resource.setName(c.getName());
		return resource;
	}

	public List<Resource> getAll() throws IOException {
		List<Resource> resources = new ArrayList<Resource>();

		for(Publication item : DAOFactory.getPublicationFactory().getAll(0, 200)) {
			Resource r = new PublicationResource();
			r.setName(item.getName());
			r.setUid(String.valueOf(item.getId()));
			resources.add(r);
		}

		return resources;
	}

}
