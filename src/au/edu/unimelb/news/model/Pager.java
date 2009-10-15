package au.edu.unimelb.news.model;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class Pager {

	int currentPage = 0;
	int pageCount;
	String url = "";

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
		out.println("<div class=\"pagebar\">");
		out.println("<p class=\"indicator\">Page "+(currentPage+1)+" of "+pageCount+"</p>");

		out.println("<ul>");
		if(currentPage>0)
			out.println("<li><a href=\""+url+(currentPage-1)+"\">«</a></li>");

		for(int i = 0; i<pageCount; i++) {
			if(i-currentPage>8 || i-currentPage<-8) continue;
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
