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
<% LayoutHelper.headerTitled(out,"Sign in"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>
<% Article article = Articles.load(request); %>
<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	<%= StringHelper.escapeHtml(article.getName()) %>
</div>

<jsp:include page="public_sidebar.jsp" />
<jsp:include page="voice_sidebar.jsp" />

<div id="content">
<% SessionFeedback.display(session,out); %>

<div id="article">
<h2><%= StringHelper.escapeHtml(article.getName()) %></h2>
<%
boolean locationFound = false;
for(NewsletterArticle item : DAOFactory.getNewsletterArticleFactory().getByArticleId(article.getId(),0,100)) {
	Newsletter newsletter = Newsletters.get(item.getNewsletterId());
	if(newsletter != null) {
		out.print("<p class=\"source\"><a href=\""+Settings.baseUrl+"/newsletter/"+newsletter.getId()+"\">"+StringHelper.escapeHtml(newsletter.getName())+"</a>");
		DateFormat f1 = new SimpleDateFormat("d MMMM");
		DateFormat f2 = new SimpleDateFormat("d MMMM yyyy");
		out.print(", " + f1.format(newsletter.getStartDate()));
		if(newsletter.getStartDate().getTime() != newsletter.getEndDate().getTime())
			out.print(" - " + f2.format(newsletter.getEndDate()));
		out.println("</p>");
		locationFound = true;
	}
}
if(!locationFound) {
	DateFormat format = new SimpleDateFormat("EEEE d MMMM yyyy");
	out.println("<p class=\"source\">"+Publications.get(article.getPublicationId()).getName()+", "+format.format(article.getLastUpdate())+"</p>");
}

if(article.getName().startsWith("No ")) {

	List<Article> articles = StaffNewsParser.parse(article.getDetails());
	for(Article i : articles) {
		out.println("<hr/>");
		out.println("<h3>"+i.getName()+"</h3>");
		out.println(i.getDetails());
	}

} else {
%>
<p><b><%= StringHelper.escapeHtml(article.getIntroduction()) %></b></p>

<p><%= article.getDetails() %></p>

<% } %>

</div>

</div>
<% LayoutHelper.footer(out); %>