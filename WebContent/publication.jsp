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
	String newsletterName = request.getParameter("name");
	Publication publication = Publications.get(newsletterName);
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

<% List<NewsletterInfo> newsletters = DAOFactory.queryNewsletterByPublication(publication.getId()); %>

<h2><%= StringHelper.escapeHtml(publication.getName()) %></h2>
<p>Most recent newsletters for <i><%= StringHelper.escapeHtml(publication.getName()) %></i>.

<ul>
<% for(NewsletterInfo document : newsletters) {
//	if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
//	if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
%>
<li><a href="<%=Settings.baseUrl%>/<%=Newsletters.asLink(document)%>"><%=document.getName()%></a> <span class="faded">(<%=Newsletters.asLink(document)%>)</span></li>
<% } %>
</ul>

<%
if(newsletters.size()==0 || !user.isAuthenticated()) {
	out.print("<div class=\"info\">");
	if(newsletters.size()==0)
	    out.print("This publication currently has no newsletters. ");
	if(!user.isAuthenticated())
		out.print("Some newsletters are only available once you <a href=\""+Settings.baseUrl+"/signin\">Sign in</a>.");
	out.println("</div>");
}
%>

</div>
<% LayoutHelper.footer(out); %>