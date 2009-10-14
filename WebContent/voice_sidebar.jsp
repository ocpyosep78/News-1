<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.helper.CookieHelper' %>

<div class="voice_sidebar">

<%
  User user = UserHelper.getUser(request);
  String sidebarKeywords=request.getParameter("keywords");
  if(sidebarKeywords==null) sidebarKeywords=CookieHelper.getCookie("keywords",request);
%>

<h3>The Voice</h3>

<%
List<IntegerResult> i = DAOFactory.queryMostRecentNewsletter(Publications.get("The Voice").getId());
Newsletter newsletter = null;

if(i!=null && i.size()>0)
	newsletter = Newsletters.get(i.get(0).getNumber());

if(newsletter!=null) {
	String storyType = "";
	for(NewsletterArticle item : DAOFactory.getNewsletterArticleFactory().getByNewsletterId(newsletter.getId(),0,100)) {
		Article article = Articles.get(item.getArticleId());
		if(!item.getSection().equalsIgnoreCase(storyType)) {
			if(storyType != "")
				out.println("</ul>");
			storyType = item.getSection();
			out.println("<h4>"+storyType+"</h4>");
			out.println("<ul>");
		}
%>

<li><a href="<%=Settings.baseUrl%>/<%=Articles.asLink(article)%>"><%= StringHelper.escapeHtml(article.getName()) %></a></li>

<% }
}%>
</ul>

</div>
