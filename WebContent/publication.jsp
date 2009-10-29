<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='java.text.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.helper.CookieHelper' %>
<%
	String newsletterName = request.getParameter("name");
	Publication publication = Publications.get(newsletterName);
	int pageNumber = 0;
	int pageSize = 20;
	try {
		pageNumber = Integer.parseInt(request.getParameter("page"));
	} catch(Exception e) {}
%>
<% LayoutHelper.headerTitled(out,StringHelper.escapeHtml(newsletterName)); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>


<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	<%= StringHelper.escapeHtml(newsletterName) %>
</div>

<jsp:include page="public_sidebar.jsp" />
<jsp:include page="voice_sidebar.jsp" />

<div id="content">

<% SessionFeedback.display(session,out); %>

<%
DateFormat year = new SimpleDateFormat("yyyy");
DateFormat f1 = new SimpleDateFormat("d MMMM");
DateFormat f2 = new SimpleDateFormat("d MMMM yyyy");
String currentYear = "";
String lastYear = "";
%>

<%
if(publication.isHasNewsletters()) {
	List<NewsletterInfo> newsletters;
	if(user.can("Publication","ViewUnpublished",publication.getId())) {
		newsletters = DAOFactory.queryNewsletterByPublicationAll(publication.getId());
	} else {
		newsletters = DAOFactory.queryNewsletterByPublication(publication.getId());
	}
	boolean wantHeadings = newsletters.size()>14;
%>

<h2><%= StringHelper.escapeHtml(publication.getName()) %></h2>
<p>Most recent newsletters for <i><%= StringHelper.escapeHtml(publication.getName()) %></i>.

<%
Pager pager = new Pager();
pager.setLink(Settings.baseUrl+"/publication/"+StringHelper.escapeHtml(publication.getName())+"?page=");
pager.setPage(pageNumber);
pager.setPageCount((newsletters.size()/pageSize)+1);
pager.display(out);
%>

<ul class="newsletter_list">
<%
	int pageStart=pageNumber*pageSize;
	int pageEnd=pageStart+pageSize;
	for(int i=0;i<newsletters.size();i++) {
		if(i>pageEnd) break;
		if(i<pageStart) continue;

		NewsletterInfo document = newsletters.get(i);
	if(wantHeadings) {
		currentYear = year.format(document.getStartDate());
		if(!currentYear.equals(lastYear)) {
			if(lastYear.length()!=0)
				out.println("</ul>");
			out.println("<h3>"+currentYear+"</h3>");
			out.println("<ul class=\"newsletter_list\">");
		}
		lastYear = currentYear;
	}
%>
<li <%=document.isPublished()?"":"class=\"unpublished\""%>><a href="<%=Settings.baseUrl%>/<%=Newsletters.asLink(document)%>"><%=document.getName()%></a> <%=document.isPublished()?"":"<span class=\"faded\">(Unpublished)</span>" %><br/>
<span class="faded">
<%
if(document.getStartDate().getTime() != document.getEndDate().getTime()) {
	out.print(f1.format(document.getStartDate()));
	out.print(" - " + f2.format(document.getEndDate()));
} else {
	out.print(f2.format(document.getEndDate()));
}
%>
</span></li>
<% } %>
</ul>

<%
if(newsletters.size()==0) {
	out.print("<div class=\"info\">");
	if(newsletters.size()==0)
	    out.print("This publication currently has no newsletters. ");
	out.println("</div>");
}
%>

<% pager.display(out); %>

<% } else { %>
<h2><%= StringHelper.escapeHtml(publication.getName()) %></h2>
<p>Most recent news for <i><%= StringHelper.escapeHtml(publication.getName()) %></i>.

<%
List<ArticleInfo> articles;
int articleCount = 0;

articles = DAOFactory.queryArticleByDate(publication.getId(),pageNumber*pageSize,pageSize);
articleCount = (int)(DAOFactory.getArticleFactory().countByPublicationId(publication.getId())/pageSize)+1;
year = new SimpleDateFormat("MMMM yyyy");

Pager pager = new Pager();
pager.setLink(Settings.baseUrl+"/publication/"+StringHelper.escapeHtml(publication.getName())+"?page=");
pager.setPage(pageNumber);
pager.setPageCount(articleCount);
pager.display(out);

out.println("<ul>");
boolean wantHeadings = true;
for(ArticleInfo article : articles) {
	if(wantHeadings) {
		currentYear = year.format(article.getPublishedDate());
		if(!currentYear.equals(lastYear)) {
			if(lastYear.length()!=0)
				out.println("</ul>");
			out.println("<h3>"+currentYear+"</h3>");
			out.println("<ul class=\"newsletter_list\">");
		}
		lastYear = currentYear;
	}
%>
<li <%=article.isPublished()?"":"class=\"unpublished\""%>><a href="<%=Settings.baseUrl+"/"+Articles.asLink(article)%>"><%=StringHelper.escapeHtml(article.getName())%></a></li>
<%
}
out.println("</ul>");

if(articleCount==0) {
	out.print("<div class=\"info\">");
	out.print("This publication currently has no news articles. ");
	out.println("</div>");
}
%>

<% pager.display(out); %>


<% } %>

</div>
<% LayoutHelper.footer(out); %>