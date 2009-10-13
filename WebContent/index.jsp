<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>
<%@ page import='au.edu.unimelb.security.UserHelper' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.helper.CookieHelper' %>
<% LayoutHelper.headerTitled(out,"University News"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	University News
</div>

<jsp:include page="public_sidebar.jsp" />
<jsp:include page="voice_sidebar.jsp" />

<div id="content">
<% SessionFeedback.display(session,out); %>

<%
  // If a username has been remembered (using a cookie), put that in the sign-in box.
  String keywords=request.getParameter("keywords");
  if(keywords==null) keywords=CookieHelper.getCookie("keywords",request);
%>



<h2>University News</h2>
<p>University News contains news and publications for various university publications.
</p>

<div id="browse">

<div id="browse-lists">

<div class="subject_options">
<h3>Browse</h3>
<ul style="margin-bottom: 1.5em;">
	<li class="a-z"><a href="browse/ABC">A-Z list of articles</a></li>
<% for(Publication publication : DAOFactory.getPublicationFactory().getAll(0,100)) {
	if(user.can("Publication","ViewPublished",publication.getId()) || user.can("Publication","ViewUnpublished",publication.getId()) || user.can("Publication","ViewArchived",publication.getId())) {
%>
	<li><a href="publication/<%=StringHelper.urlEscape(publication.getName())%>"><%=StringHelper.escapeHtml(publication.getName())%></a></li>
<%   } %>
<% } %>
</ul>

<% if(user.can("Publication","Add")||user.can("Publication","Update")) { %>
<a class="manage_publications" href="<%=Settings.baseUrl %>/publications.jsp">Manage publications</a>
<% } %>

</div>

<div class="audience_options">
<h3>Topics</h3>
<ul>
<% for(Topic topic : DAOFactory.getTopicFactory().getAll(0,100)) { %>
	<li><a href="topic/<%=StringHelper.urlEscape(topic.getName())%>"><%=StringHelper.escapeHtml(topic.getName())%></a></li>
<% } %>
</ul>
</div>

</div>


</div>

</div>
<% LayoutHelper.footer(out); %>