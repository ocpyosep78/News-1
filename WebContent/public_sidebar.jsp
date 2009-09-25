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
<h3>Most viewed articles</h3>
<div class="box_contents">
<ul>
<%
int i=0;
for(ArticleInfo document : DAOFactory.queryArticleMostPopular(0,30)) {
    //if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
    //if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
    if(++i > 5) break;
%>
<li><a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>" title="<%=StringHelper.escapeHtml(document.getName())%>"><%=StringHelper.escapeHtml(StringHelper.maxLength(document.getName(),30))%></a> <span class="policy-number">(<%=Articles.asLink(document)%>)</span></li>
<% } %>
</ul>
</div>
<p class="jumpto">
<a href="<%=Settings.baseUrl%>/mostused.jsp">Most viewed articles »</a>
</p>
</div>

<% } %>

<div class="recently_updated_box">
<h3>New or updated articles</h3>
<div class="box_contents">
<ul>
<%
int i=0;
for(ArticleInfo document : DAOFactory.queryArticleRecentlyUpdated(0,updateListLength*3)) {
    //if(document.isPublished() && !user.can("Category","ViewPublished",document.getCategoryId())) continue;
    //if(!document.isPublished() && !user.can("Category","ViewUnpublished",document.getCategoryId())) continue;
    if(++i > updateListLength) break;
%>
<li><a href="<%=Settings.baseUrl%>/<%=Articles.asLink(document)%>" title="<%=StringHelper.escapeHtml(document.getName())%>"><%=StringHelper.escapeHtml(StringHelper.maxLength(document.getName(),30))%></a> <span class="policy-number">(<%=Articles.asLink(document)%>)</span></li>
<% } %>
</ul>
</div>
<p class="jumpto">
<a href="<%=Settings.baseUrl%>/recentlyupdated.jsp">New or updated articles »</a>
</p>
</div>

</div>
