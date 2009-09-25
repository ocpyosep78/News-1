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
	String audience = request.getParameter("name");
%>
<% LayoutHelper.headerTitled(out,StringHelper.escapeHtml(audience)); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>


<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">Policy Library</a> &gt;
	<a href="<%=Settings.baseUrl%>/">Audience</a> &gt;
	<%= audience %>
</div>

<jsp:include page="public_sidebar.jsp" />


<div id="content">

<% SessionFeedback.display(session,out); %>

<% List<ArticleInfo> documents =DAOFactory.queryArticleByPublication(audience); %>

<h2><%= StringHelper.escapeHtml(audience) %></h2>
<p>Key policy documents relevant for <%= StringHelper.escapeHtml(audience) %>.

<ul>
<% for(ArticleInfo document : documents) {
//	if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
//	if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
%>
<li><a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>"><%=document.getName()%></a> <span class="faded">(<%=Articles.asLink(document)%>)</span></li>
<% } %>
</ul>

<%
if(documents.size()==0 || !user.isAuthenticated()) {
	out.print("<div class=\"info\">");
	if(documents.size()==0)
	    out.print("No policy documents are currently available for this category. ");
	if(!user.isAuthenticated())
		out.print("Some documents are only available once you <a href=\""+Settings.baseUrl+"/signin\">Sign in</a>.");
	out.println("</div>");
}
%>

</div>
<% LayoutHelper.footer(out); %>