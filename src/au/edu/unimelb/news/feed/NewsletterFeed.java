package au.edu.unimelb.news.feed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unimelb.feed.Feed;
import au.edu.unimelb.feed.FeedEntry;
import au.edu.unimelb.helper.StringHelper;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.dao.NewsletterInfo;
import au.edu.unimelb.news.dao.Publication;
import au.edu.unimelb.news.model.Newsletters;
import au.edu.unimelb.news.model.Publications;
import au.edu.unimelb.security.Settings;

public class NewsletterFeed extends Feed {

	public NewsletterFeed(String url) {
		this.setWebsite(url);
		this.setName("Newsletter");
		this.setSubtitle("Recently released newsletters");
		this.setLinkName("Newsletter");
	}

	protected List<FeedEntry> readEntries(String parameters) throws IOException {
		List<FeedEntry> entries = new ArrayList<FeedEntry>();

		if(parameters == null) return entries;
		Publication publication = Publications.get(StringHelper.urlUnescape(parameters));
		if(publication == null) return entries;
		List<NewsletterInfo> newsletters = DAOFactory.queryNewsletterByPublication(publication.getId());

		for(NewsletterInfo newsletter : newsletters) {
			FeedEntry entry = new FeedEntry();
			entry.title=newsletter.getName();
			entry.updated=newsletter.getStartDate();
			entry.content = "";
			entry.link = Settings.baseUrl+"/"+Newsletters.asLink(newsletter);
			entry.id = entry.link;
			entries.add(entry);
		}

		return entries;
	}

}
