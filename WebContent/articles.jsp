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
	int pageNumber = 0;
	int pageSize = 20;
	try {
		pageNumber = Integer.parseInt(request.getParameter("page"));
	} catch(Exception e) {}
%>
<% LayoutHelper.headerTitled(out,"News articles"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	News articles
</div>

<div id="content">

<% SessionFeedback.display(session,out); %>

<%
DateFormat year = new SimpleDateFormat("yyyy");
DateFormat f1 = new SimpleDateFormat("d MMMM");
DateFormat f2 = new SimpleDateFormat("d MMMM yyyy");
String currentYear = "";
String lastYear = "";
%>

<h2>News articles</h2>

<%
int articleCount = (int)(DAOFactory.getArticleFactory().countAll()/pageSize)+1;
year = new SimpleDateFormat("MMMM yyyy");

Pager pager = new Pager();
pager.setLink(Settings.baseUrl+"/articles.jsp?page=");
pager.setPage(pageNumber);
pager.setPageCount(articleCount);
pager.display(out);

out.println("<table width=\"100%\">");
for(ArticleInfo article : DAOFactory.queryArticleListByDate(pageNumber*pageSize,pageSize)) {
%>
<tr>
<td><%=article.isPublished()?article.getPublishedDate():"Unpublished" %></td>
<td><a href="<%=Settings.baseUrl+"/"+Articles.asLink(article)%>"><%=StringHelper.maxLength(StringHelper.escapeHtml(article.getName()),60)%></a></td>
<td><%=Publications.get(article.getPublicationId()).getName()%></td>
<td>Edit Delete</td>
</tr>
<%
}
out.println("</table>");

if(articleCount==0) {
	out.print("<div class=\"info\">");
	out.print("This publication currently has no news articles. ");
	out.println("</div>");
}
%>

<% pager.display(out); %>



</div>
<% LayoutHelper.footer(out); %>