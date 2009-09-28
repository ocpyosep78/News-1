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

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	New or updated articles
</div>

<jsp:include page="public_sidebar.jsp" />

<div id="content">
<% SessionFeedback.display(session,out); %>

<h2>New or updated articles</h2>
<p>Below are the most recently released and/or updated news articles.</p>


<ul class='browse-policy-list'>
<%
int i=0;
for(ArticleInfo document : DAOFactory.queryArticleRecentlyUpdated(0,100)) {
	//if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
	//if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
	if(++i > 20) break;
%>
<li>
<a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>"><%=StringHelper.escapeHtml(document.getName())%></a>
<span class="policy-number">(<%=StringHelper.escapeHtml(Publications.get(document.getPublicationId()).getName())%>)</span>
<span class="policy-category"></span>
</li>
<% } %>
</ul>

<a href="<%=Settings.baseUrl%>/rss/recentupdates.rss" class="rssfeed">Recent updated article RSS feed</a>

</div>
<% LayoutHelper.footer(out); %>