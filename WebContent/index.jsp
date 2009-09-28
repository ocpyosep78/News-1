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

<div id="content" class="content-wide">
<% SessionFeedback.display(session,out); %>

<%
  // If a username has been remembered (using a cookie), put that in the sign-in box.
  String keywords=request.getParameter("keywords");
  if(keywords==null) keywords=CookieHelper.getCookie("keywords",request);
%>


<div id="start">

<div class="about_box">
<h2>University News</h2>
<div class="about_contents">
<p>University News contains news and publications for various university publications.
</p>
</div>
<p class="jumpto">
<a href="http://www.policy.unimelb.edu.au/">More about policies »</a>
</p>
</div>

<div id="search">
<form method="post" action="search.jsp" id="policysearch">
<h2>Search news</h2>
<label for="keywords" class="offstage">Keywords</label>
<input type="text" name="keywords" id="keywords" value="<%=keywords%>" size="40" maxlength="100"/>
<input type="submit" value="Go" />
</form>
<script type="text/javascript">
    document.getElementById('keywords').focus();
</script>
</div>

<div class="clearboth"></div>

</div>

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

<div id="browse-activity">

<%
  int updateListLength = 13;
  if(user.isAuthenticated()) {
	  updateListLength = 5;
 %>


<div class="most_viewed_box">
<h3>Most viewed articles</h3>
<div class="curved_box_contents">
<ul>
<%
int i=0;
for(ArticleInfo article : DAOFactory.queryArticleMostPopular(0,30)) {
	//if(article.isPublished() && !user.can("Category","ViewPublished",article.getCategoryId())) continue;
	//if(!article.isPublished() && !user.can("Category","ViewUnpublished",article.getCategoryId())) continue;
	if(++i > 5) break;
%>
<li><a href="<%=Settings.baseUrl%>/<%=Articles.asLink(article)%>" title="<%=StringHelper.escapeHtml(article.getName())%>"><%=StringHelper.escapeHtml(StringHelper.maxLength(article.getName(),45))%></a> </li>
<% } %>
</ul>
</div>
<p class="jumpto">
<a href="<%=Settings.baseUrl%>/mostused.jsp">Most used policy documents</a>
</p>
</div>

<% } %>

<div class="recently_updated_box">
<h3>New or updated articles</h3>
<div class="curved_box_contents">
<ul>
<%
int i=0;
for(ArticleInfo article : DAOFactory.queryArticleRecentlyUpdated(0,updateListLength*3)) {
	//if(article.isPublished() && !user.can("Category","ViewPublished",article.getCategoryId())) continue;
	//if(!article.isPublished() && !user.can("Category","ViewUnpublished",article.getCategoryId())) continue;
	if(++i > updateListLength) break;
%>
<li><a href="<%=Settings.baseUrl%>/<%=Articles.asLink(article)%>" title="<%=StringHelper.escapeHtml(article.getName())%>"><%=StringHelper.escapeHtml(StringHelper.maxLength(article.getName(),45))%></a></li>
<% } %>
</ul>
</div>
<p class="jumpto">
<a href="<%=Settings.baseUrl%>/recentlyupdated.jsp">New or updated articles</a>
</p>
</div>

</div>
<!-- -->

</div>

</div>
<% LayoutHelper.footer(out); %>