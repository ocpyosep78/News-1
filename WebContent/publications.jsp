<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.*' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.helper.CookieHelper' %>
<% LayoutHelper.headerTitled(out,"Sign in"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	Publications
</div>

<jsp:include page="public_sidebar.jsp" />

<div id="content">

<% SessionFeedback.display(session,out); %>

<%
  // If a username has been remembered (using a cookie), put that in the sign-in box.
  String signinUsername=request.getParameter("signin_username");
  if(signinUsername==null || signinUsername.length()==0) signinUsername=CookieHelper.getCookie("username",request);

%>



<div class="subject_options">
<h3>Publications</h3>

<p>Below are the current publications available within University News. Some subject areas are only visible once you have signed in to the system.</p>
<ul>
<% for(Category category : DAOFactory.getCategoryFactory().getAll(0,100)) {
	if(user.can("Publication","ViewPublished",category.getId()) || user.can("Publication","ViewUnpublished",category.getId()) || user.can("Publication","ViewArchived",category.getId())) {
%>
	<li><a href="category/<%=StringHelper.urlEscape(category.getName())%>"><%=StringHelper.escapeHtml(category.getName())%></a></li>
<% }
} %>
</ul>
</div>

<% if(user.can("Publication","Add")) { %>
<a class="add_subject" href="<%=Settings.baseUrl %>/publication_add.jsp">Add new publication</a>
<% } %>


</div>
<% LayoutHelper.footer(out); %>