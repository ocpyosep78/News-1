<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.news.dao.SearchResult' %>
<%@ page import='au.edu.unimelb.news.model.ArticleSearch' %>
<%@ page import='au.edu.unimelb.news.model.Articles' %>
<%@ page import='au.edu.unimelb.news.model.Topics' %>
<%@ page import='au.edu.unimelb.security.UserHelper' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<%@ page import='au.edu.unimelb.security.UserHelper' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<% LayoutHelper.headerTitled(out,"Search results"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">Policy Library</a> &gt;
	Search results
</div>

<jsp:include page="public_sidebar.jsp" />

<div id="content">
<% SessionFeedback.display(session,out); %>

<%
	int _page=0;
	String keywords="";
	if(request.getParameter("page")!=null) _page=Integer.parseInt(request.getParameter("page"));
	if(request.getParameter("keywords")!=null) keywords=request.getParameter("keywords");

%>


<div>
<form method="post" action="search.jsp" class="contentform" id="searchform">
<h2>Search policies</h2>

<input type="text" name="keywords" id="keywords" value="<%=keywords%>" size="40" maxlength="100"/>
<input type="submit" value="Go"/>

</form>

<script type="text/javascript">
    document.getElementById('keywords').focus();
</script>
</div>

<%
	ArticleSearch search = new ArticleSearch();
	if(!search.search(keywords,user)) {
%>
	<div class="error">Sorry, but an error occured while attempting to complete your search. <%=search.getError()%></div>
<%
	} else {
		List<SearchResult> results = search.getResults();
		if(results.size()==0) {
			%>
			<div class="info">No documents could be found matching those keywords.</div>
		<%
			
		} else {
%>
<div class="search results" style="">

<%search.pageBar(out,_page,keywords);%>

<ul>
<%
	for(int i=0;i<ArticleSearch.PAGESIZE&&(_page*ArticleSearch.PAGESIZE+i<results.size());i++) {
		SearchResult document = results.get(_page*ArticleSearch.PAGESIZE+i);
%>
<li><a href="<%=Articles.asLink(document)%>"><%=document.getName()%></a> <span class="faded">(<%=Articles.asLink(document)%>)</span><br/>
<%=document.getStatus() %> 

<% } %>

</ul>

<%search.pageBar(out,_page,keywords);%>


</div>
<% }
}%>


</div>
<% LayoutHelper.footer(out); %>