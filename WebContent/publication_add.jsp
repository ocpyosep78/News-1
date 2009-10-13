<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>
<%@ page import='au.edu.unimelb.security.action.*' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.helper.CookieHelper' %>
<% LayoutHelper.headerTitled(out,"Add new subject area"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">Policy Library</a> &gt;
	<a href="<%=Settings.baseUrl%>/publications.jsp">Publications</a> &gt;
	Add publication
</div>

<jsp:include page="public_sidebar.jsp" />
<jsp:include page="voice_sidebar.jsp" />

<div id="content">
<% SessionFeedback.display(session,out); %>

<%
if(!user.can("Publication","Add"))
	throw new AuthorisationException();

Publication publication = Publications.load(request);
%>

<div id="subject_edit">
<form method="post" action="<%=Settings.baseUrl%>/PublicationAddAction" class="contentform">

<h2>Add new publication</h2>

<p><b>WARNING:</b> Choose your publications names very carefully. A publication, once added, becomes a permanent record.</p>

<%
  if(session.getAttribute("errors")!=null) {
      out.print("<div class=\"error\"> "+session.getAttribute("errors")+"</div>");
      session.setAttribute("errors",null);
  }
%>

<input type="text" id="publication_name" name="publication_name" value="<%=StringHelper.escapeHtml(publication.getName())%>"/>

<input type="submit" name="save_button" value="Add new publication"/>
</form>
</div>

</div>
<% LayoutHelper.footer(out); %>