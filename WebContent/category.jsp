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
<%
	String name = request.getParameter("name");
	Topic category = DAOFactory.getTopicFactory().getByName(name,0,1).get(0);
%>
<% LayoutHelper.headerTitled(out,category.getName()); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>


<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	<a href="<%=Settings.baseUrl%>/categories.jsp">Topics</a> &gt;
	<%= category.getName() %>
</div>

<jsp:include page="public_sidebar.jsp" />


<div id="content">
<% SessionFeedback.display(session,out); %>


<% List<ArticleInfo> articles = DAOFactory.queryArticleByTopic(category.getId()); %>

<h2><%= category.getName() %></h2>
<p>The policy documents related to <%= category.getName() %>.</p>

<ul>
<% for(ArticleInfo document : articles) {
//	if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
//	if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
%>
<li>
<a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>"><%=StringHelper.escapeHtml(document.getName())%></a>
<span class="policy-number faded">(<%=Articles.asLink(document)%>)</span>
</li>
<% } %>
</ul>

<%
if(articles.size()==0 || !user.isAuthenticated()) {
	out.print("<div class=\"info\">");
	if(articles.size()==0)
	    out.print("No articles are currently available for this topic. ");
	out.println("</div>");
}
%>

</div>
<% LayoutHelper.footer(out); %>