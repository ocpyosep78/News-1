<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.helper.CookieHelper' %>
<% LayoutHelper.headerTitled(out,"Browse"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>
<% String sections = request.getParameter("section"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	<a href="<%=Settings.baseUrl%>/browse/ABC">Browse</a> &gt;
	<%= StringHelper.escapeHtml(sections) %>
</div>

<jsp:include page="public_sidebar.jsp" />
<jsp:include page="voice_sidebar.jsp" />

<div id="content">

<% SessionFeedback.display(session,out); %>

<h2>Browse</h2>

<ul class="browse_options">
	<li><a href="<%=Settings.baseUrl%>/browse/ABC">ABC</a></li>
	<li><a href="<%=Settings.baseUrl%>/browse/DEF">DEF</a></li>
	<li><a href="<%=Settings.baseUrl%>/browse/GHI">GHI</a></li>
	<li><a href="<%=Settings.baseUrl%>/browse/JKL">JKL</a></li>
	<li><a href="<%=Settings.baseUrl%>/browse/MNO">MNO</a></li>
	<li><a href="<%=Settings.baseUrl%>/browse/PQR">PQR</a></li>
	<li><a href="<%=Settings.baseUrl%>/browse/STU">STU</a></li>
	<li><a href="<%=Settings.baseUrl%>/browse/VWXYZ">VWXYZ</a></li>
</ul>

<%
  if(session.getAttribute("errors")!=null) {
      out.print("<div class=\"error\"> "+session.getAttribute("errors")+"</div>");
      session.setAttribute("errors",null);
  }
%>

<%
  for(String section : sections.split("")) {
	if(section.length()==0) continue;

    out.println("<h3>"+section+"</h3>");
    List<ArticleInfo> documents = DAOFactory.queryArticleByBrowse(section+"%");
    out.println("<ul class='browse-policy-list'>");
	int i=0;
	for(ArticleInfo document : documents) {
	  //if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
	  //if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
	  i++;
	  %>
<li>
	<a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>"><%=StringHelper.escapeHtml(document.getName())%></a>
    <span class="policy-number">(<%=StringHelper.escapeHtml(Publications.get(document.getPublicationId()).getName())%>)</span>
	<span class="policy-category"></span>
</li>
<%  }
	if(i==0)
	  out.println("<li class=\"empty_browse_list\">There are currently no articles starting with '"+StringHelper.escapeHtml(section)+"'</li>");
	out.println("</ul>");

  }
%>


</div>
<% LayoutHelper.footer(out); %>