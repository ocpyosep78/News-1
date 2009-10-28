package au.edu.unimelb.news.feed;

import java.util.ArrayList;
import java.util.List;

import au.edu.unimelb.feed.Feed;
import au.edu.unimelb.feed.FeedEntry;

public class NewsletterFeed extends Feed {

	public NewsletterFeed(String url) {
		this.setWebsite(url);
		this.setName("Newsletters");
		this.setSubtitle("Recently released newsletters");
		this.setLinkName("Newsletters");
	}

	protected List<FeedEntry> readEntries() {
		List<FeedEntry> entries = new ArrayList<FeedEntry>();
		return entries;
	}

}
