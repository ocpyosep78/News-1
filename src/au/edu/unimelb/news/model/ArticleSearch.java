package au.edu.unimelb.news.model;

import au.edu.unimelb.helper.StringHelper;
import au.edu.unimelb.news.dao.*;
import au.edu.unimelb.security.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspWriter;

class SearchResultRanking implements Comparator<SearchResult>{

    public int compare(SearchResult o1, SearchResult o2) {
    	if(o1.getRank() == o2.getRank())
    		return 0;
    	if(o1.getRank() > o2.getRank())
    		return -1;
    	else
    		return 1;
    }
}


public class ArticleSearch {
	
	public static final int PAGESIZE = 10;

	public String error = "";
	private List<SearchResult> results = null;
	Map<String,SearchResult> hash = new Hashtable<String,SearchResult>();

	/*
	public void addToResults(List<SearchResult> entries, int rank) {
		for(SearchResult item : entries) {
			item.setRank(item.getRank()*rank);
			if(!hash.containsKey(item.getNumber()))
				hash.put(item.getNumber(), item);
			else {
				item.setRank(item.getRank()+hash.get(item.getNumber()).getRank());
				hash.put(item.getNumber(), item);
			}
		}
	}
	*/
	
	public boolean search(String keywords, User user) throws IOException {
		
		results = new ArrayList<SearchResult>();
		List<SearchResult> headings = DAOFactory.queryArticleSimpleSearch("article_name","%"+keywords+"%");
		List<SearchResult> details = DAOFactory.queryArticleSimpleSearch("article_details","%"+keywords+"%");
		if(headings == null || details == null) {
			error = "Database problem occured during search.";
			return false;
		}

		// Scale ranks appropriately
		for(SearchResult item : headings)
			item.setRank(item.getRank()*10);
		for(SearchResult item : details)
			item.setRank(item.getRank()*2);

		// Merge lists into one using hash
		for(SearchResult item : headings)
				hash.put(""+item.getId(), item);
		for(SearchResult item : details)
			if(!hash.containsKey(""+item.getId()))
				hash.put(""+item.getId(), item);
			else {
				item.setRank(item.getRank()+hash.get(""+item.getId()).getRank());
				hash.put(""+item.getId(), item);
			}

		// Add all permitted search results to the results list
		for(SearchResult item : hash.values()) {
			//if(item.isPublished() && !user.can("Category","ViewPublished",item.getCategoryId())) continue;
			//if(!item.isPublished() && !user.can("Category","ViewUnpublished",item.getCategoryId())) continue;
			results.add(item);
		}
		Collections.sort(results, new SearchResultRanking());

		return true;
	}
	
	public List<SearchResult> getResults() {
		return results;
	}
	
	public String getError() {
		return error;
	}
	
	public void pageBar(JspWriter out, int page, String keywords) throws IOException {
		int last = ((page*10)+10);
		if(last>results.size()) last = results.size();
		out.println("<p>Showing results "+((page*10)+1)+" to "+last+" of "+results.size()+" policies.");
		
		int pagecount=(results.size()-1)/PAGESIZE;
		
		out.println("<span style=\"padding-left: 21em;\">&nbsp;</span>");
		if(page>0) {
			out.println("<a href=\"search.jsp?keywords="+StringHelper.urlEscape(keywords)+"&amp;page="+(page-1)+"\">« Previous</a>");
		} else {
			out.println("<span class=\"faded\">« Previous</span>");
		}
		for(int i=0;i<=pagecount;i++) {
			if(page==i) {
				out.println("<span class=\"faded\">"+(i+1)+"</span> ");
			} else {
				out.println("<a href=\"search.jsp?keywords="+StringHelper.urlEscape(keywords)+"&amp;page="+i+"\">"+(i+1)+"</a> ");
			}
		}
		if(page<pagecount) {
			out.println("<a href=\"search.jsp?keywords="+StringHelper.urlEscape(keywords)+"&amp;page="+(page+1)+"\">Next »</a>");
		} else {
			out.println("<span class=\"faded\">Next »</span>");
		}
		
		out.println("</p>");
	}
}
