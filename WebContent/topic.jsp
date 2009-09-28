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
<% LayoutHelper.headerTitled(out,"Sign in"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>
<% String topicString = request.getParameter("topic"); %>
<% Topic topic = Topics.get(topicString); %>
<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	<%= StringHelper.escapeHtml(topic.getName()) %>
</div>

<jsp:include page="public_sidebar.jsp" />

<div id="content">
<% SessionFeedback.display(session,out); %>

<h2><%= StringHelper.escapeHtml(topic.getName()) %></h2>
<p>Below is the list of articles in the <%= StringHelper.escapeHtml(topic.getName()) %> category.</p>

<ul class='browse-policy-list'>
<%
for(ArticleInfo document : DAOFactory.queryArticleByTopic(topic.getId(),0,100)) { 
	//if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
	//if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
%>
<li>
<a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>"><%=StringHelper.escapeHtml(document.getName())%></a>
<span class="policy-number">(<%=Articles.asLink(document)%>)</span>
<span class="policy-category"></span>
</li>
<% } %>
</ul>

<a href="<%=Settings.baseUrl%>/rss/mostused.rss" class="rssfeed">Most popular article RSS feed</a>

</div>
<% LayoutHelper.footer(out); %>