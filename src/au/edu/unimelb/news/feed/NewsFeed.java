package au.edu.unimelb.news.feed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unimelb.feed.Feed;
import au.edu.unimelb.feed.FeedEntry;
import au.edu.unimelb.news.dao.ArticleInfo;
import au.edu.unimelb.news.dao.DAOFactory;
import au.edu.unimelb.news.model.Articles;
import au.edu.unimelb.security.Settings;
import au.edu.unimelb.security.UserHelper;
import au.edu.unimelb.security.model.User;

public class NewsFeed extends Feed {

	public NewsFeed(String url) {
		this.setWebsite(url);
		this.setName("News");
		this.setSubtitle("Recently released news items");
		this.setLinkName("News");
	}

	protected List<FeedEntry> readEntries(String parameters) {
		List<FeedEntry> entries = new ArrayList<FeedEntry>();

		try {
			int i=0;
			User guest = UserHelper.getGuestUser();
			for(ArticleInfo document : DAOFactory.queryArticleRecentlyUpdated(0,100)) {
				if(document.isPublished() && !guest.can("Category","ViewPublished",document.getPublicationId())) continue;
				if(++i > 30) break;
				FeedEntry entry = new FeedEntry();
				entry.title = document.getName();
				entry.content = "";
				entry.updated = document.getPublishedDate();
				entry.link = Settings.baseUrl+"/"+Articles.asLink(document);
				entry.id = entry.link;
				entries.add(entry);
			}
		} catch (IOException e) {
		}

		return entries;
	}

}
