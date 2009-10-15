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
List<NewsletterInfo> newsletters = DAOFactory.queryNewsletterByPublication(publication.getId());
DateFormat year = new SimpleDateFormat("yyyy");
DateFormat f1 = new SimpleDateFormat("d MMMM");
DateFormat f2 = new SimpleDateFormat("d MMMM yyyy");
String currentYear = "";
String lastYear = "";
boolean wantHeadings = newsletters.size()>14;
%>

<% if(true) { %>

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
//	if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
//	if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
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
<li><a href="<%=Settings.baseUrl%>/<%=Newsletters.asLink(document)%>"><%=document.getName()%></a><br/>
<span class="faded">
<%
out.print(f1.format(document.getStartDate()));
if(document.getStartDate().getTime() != document.getEndDate().getTime())
	out.print(" - " + f2.format(document.getEndDate()));
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

<% } %>

</div>
<% LayoutHelper.footer(out); %>