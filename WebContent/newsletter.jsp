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
<% Newsletter newsletter = Newsletters.load(request); %>
<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	<%= StringHelper.escapeHtml(newsletter.getName()) %>
</div>

<jsp:include page="public_sidebar.jsp" />
<jsp:include page="voice_sidebar.jsp" />

<div id="content">
<% SessionFeedback.display(session,out); %>

<div class="newsletter">

<h2><%= StringHelper.escapeHtml(newsletter.getName()) %></h2>
<%
DateFormat f1 = new SimpleDateFormat("d MMMM");
DateFormat f2 = new SimpleDateFormat("d MMMM yyyy");
out.print("<p class=\"newsletter_date\">");
if(newsletter.getStartDate().getTime() != newsletter.getEndDate().getTime()) {
	out.print(f1.format(newsletter.getStartDate()));
	out.print(" - " + f2.format(newsletter.getEndDate()));
} else {
	out.print(f2.format(newsletter.getEndDate()));
}
out.print("</p>");

String storyType = "";
for(NewsletterArticle item : DAOFactory.getNewsletterArticleFactory().getByNewsletterId(newsletter.getId(),0,100)) {
	Article article = Articles.get(item.getArticleId());
	if(!item.getSection().equalsIgnoreCase(storyType)) {
		if(storyType.length()>0&&item.getSection().length()==0)
			out.println("<h3>More news</h3>");
		storyType = item.getSection();
		out.println("<h3>"+storyType+"</h3>");
	}

	if(article != null) {
%>

<a href="../<%=Articles.asLink(article)%>"><%= StringHelper.escapeHtml(article.getName()) %></a><br/>

<p><%= article.getIntroduction() %></p>

<%
	} else {
		out.println("<p>Article "+item.getArticleId()+" does not exist.</p>");
	}
} %>
</div>

</div>
<% LayoutHelper.footer(out); %>