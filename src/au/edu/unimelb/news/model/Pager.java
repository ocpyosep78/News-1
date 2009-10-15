package au.edu.unimelb.news.model;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class Pager {

	int currentPage = 0;
	int pageCount;
	String url = "";
	public static final int MAX_PAGE_LINKS = 16;

	public void setLink(String url) {
		this.url = url;
	}

	public void setPage(int page) {
		currentPage = page;
	}

	public void setPageCount(int count) {
		this.pageCount = count;
	}

	public void display(JspWriter out) throws IOException {
		if(pageCount<2) return;

		out.println("<div class=\"pagebar\">");
		out.println("<p class=\"indicator\">Page "+(currentPage+1)+" of "+pageCount+"</p>");

		out.println("<ul>");
		if(currentPage>0)
			out.println("<li><a href=\""+url+(currentPage-1)+"\">«</a></li>");

		int minPageDisplayed = currentPage-(MAX_PAGE_LINKS/2);
		if(minPageDisplayed<0) minPageDisplayed=0;
		int maxPageDisplayed = minPageDisplayed+MAX_PAGE_LINKS;
		if(maxPageDisplayed>pageCount) {
			maxPageDisplayed = pageCount;
			if(maxPageDisplayed-MAX_PAGE_LINKS>0)
				minPageDisplayed = maxPageDisplayed-MAX_PAGE_LINKS;
		}


		for(int i = minPageDisplayed; i<maxPageDisplayed; i++) {
			out.print("<li><a href=\""+url+i+"\">");
			if(i==currentPage)
				out.print("<b>"+(i+1)+"</b>");
			else
				out.print((i+1));
			out.println("</a></li>");
		}

		if(currentPage<pageCount-1)
			out.print("<li><a href=\""+url+(currentPage+1)+"\">»</a></li>");
		out.println("</ul>");

		out.println("</div>");
	}
}
