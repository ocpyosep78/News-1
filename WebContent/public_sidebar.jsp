<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>

<div class="policy_sidebar">

<%
  User user = UserHelper.getUser(request);
  String sidebarKeywords=request.getParameter("keywords");
  if(sidebarKeywords==null) sidebarKeywords=CookieHelper.getCookie("keywords",request);
%>

<div id="search">
<form method="post" action="<%=Settings.baseUrl%>/search.jsp" id="policysearch">
<h2>Search news</h2>
<label for="keywords" class="offstage">Keywords</label>
<input type="text" name="keywords" id="keywords" value="<%=sidebarKeywords%>" size="40" maxlength="100" />
<input type="submit" value="Go" />
</form>
<script type="text/javascript">
    document.getElementById('keywords').focus();
</script>
</div>

<%
  int updateListLength = 13;
  if(user.isAuthenticated()) {
	  updateListLength = 5;
 %>

<div class="most_viewed_box">
<h3>Publications</h3>
<div class="box_contents">
<ul>
<% for(Publication publication : DAOFactory.getPublicationFactory().getAll(0,100)) {
	if(user.can("Publication","ViewPublished",publication.getId()) || user.can("Publication","ViewUnpublished",publication.getId()) || user.can("Publication","ViewArchived",publication.getId())) {
%>
	<li><a href="<%=Settings.baseUrl%>/publication/<%=StringHelper.urlEscape(publication.getName())%>"><%=StringHelper.escapeHtml(publication.getName())%></a></li>
<%   } %>
<% } %>

</ul>
</div>
</div>

<br/>
<ul>
<li><a href="http://newsroom.melbourne.edu/">Media</a></li>
<li><a href="http://www.research.unimelb.edu.au/mediacontact/">Find an Expert</a></li>
<li><a href="http://www.marcom.unimelb.edu.au/">Marketing and Communications</a></li>
<li><a href="http://www.marcom.unimelb.edu.au/public/story">Got a story?</a></li>
</ul>

<% } %>

<div class="recently_updated_box">
<h3>New or updated articles</h3>
<div class="box_contents">
<ul>
<%
int i=0;
for(ArticleInfo document : DAOFactory.queryArticleRecentlyUpdated(0,updateListLength*3)) {
    //if(document.isPublished() && !user.can("Publication","ViewPublished",document.getPublicationId())) continue;
    //if(!document.isPublished() && !user.can("Publication","ViewUnpublished",document.getPublicationId())) continue;
    if(++i > updateListLength) break;
%>
<li><a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>" title="<%=StringHelper.escapeHtml(document.getName())%>"><%=StringHelper.escapeHtml(StringHelper.maxLength(document.getName(),30))%></a> <span class="policy-number">(<%=StringHelper.escapeHtml(Publications.get(document.getPublicationId()).getName())%>)</span></li>
<% } %>
</ul>
</div>
<p class="jumpto">
<a href="<%=Settings.baseUrl%>/recentlyupdated.jsp">New or updated articles »</a>
</p>
</div>

</div>
