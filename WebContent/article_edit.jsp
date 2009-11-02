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
<% LayoutHelper.headerTitled(out,"Update article"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>
<% Article article = Articles.load(request); %>
<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">University News</a> &gt;
	<%= StringHelper.escapeHtml(article.getName()) %>
</div>

<jsp:include page="public_sidebar.jsp" />

<div id="content">
<%
	SessionFeedback.display(session,out);
%>

<h2>Update article</h2>

<div id="article_edit">
<form method="post" action="<%=Settings.baseUrl%>/ArticleUpdateAction" class="contentform">
<input type="hidden" name="article_id" value="<%=article.getId()%>" />

<label for='article_publication_id' class='firstquestion'>
<span class='question'>Publication:</span>
<span class='answer'><select name="article_publication_id" id="article_publication_id">

<% for(Publication publication : DAOFactory.getPublicationFactory().getAll(0,2000)) {
	if(!user.can("Publication","ArticleCreate",publication.getId())) continue; %>
	<option value="<%=publication.getId()%>"><%=publication.getName()%></option>
<% }%>
</select>
</span>
</label>

<label for='article_title'>
<span class='question'>Title:</span>
<span class='answer'>
    <input type="text" name="article_title" id="article_title" value="<%=StringHelper.escapeHtml(article.getName())%>" size="35"/><br/>
    <span class="faded">May <i>not</i> contain HTML, may contain characters like &rdquo; &ldquo; and &mdash;</span>
</span>
</label>

<label for='article_byline'>
<span class='question'>Byline:</span>
<span class='answer'><input type="text" name="article_byline" id="article_byline" value="<%=StringHelper.escapeHtml(article.getByline())%>" size="35"/></span>
</label>


<label for='article_introduction'>
<span class='question'>Summary</span>
<span class='answer'><textarea name="article_introduction" id="article_introduction" rows="2" cols="40">
<%=StringHelper.escapeHtml(article.getIntroduction())%></textarea>
</span>
</label>

<label for='article_details'>
<span class='question'>Document</span>
<span class='answer'><textarea name="article_details" id="article_details" rows="14" cols="40">
<%=StringHelper.escapeHtml(article.getDetails())%></textarea>
</span>
</label>

<div class="array article_topic">
<span class='question'>Topic</span>
<span class='answer'>
<% for(Topic topic : DAOFactory.getTopicFactory().getAll(0,200)) { %>
<label><input type="checkbox" name="article_topic" value="<%=topic.getId()%>"><%=topic.getName()%></input></label>
<% } %>
</span>
</div>

<%
	Calendar cal = new GregorianCalendar();
	cal.setTime(article.getPublishedDate());
	int startYear = cal.get(Calendar.YEAR)-2;
	int year = cal.get(Calendar.YEAR);
	int month = cal.get(Calendar.MONTH);
	int day = cal.get(Calendar.DAY_OF_MONTH);
%>

<div class="array article_date">
<span class='question'>Date</span>
<span class='answer'>
<select name="article_date_day">
<% for(int i=1;i<=31;i++) { %>
<option value="<%=i%>"<%=(i==day)?"selected=\"selected\"":""%>><%=i%></option>
<% } %>
</select>
<select name="article_date_month">
<% for(int i=0;i<12;i++) { %>
<option value="<%=i%>"<%=(i==month)?"selected=\"selected\"":""%>><%=DateFormatHelper.monthToString(i)%></option>
<% } %>
</select>
<select name="article_date_year">
<% for(int i=0;i<5;i++,startYear++) { %>
<option value="<%=year%>"<%=(startYear==year)?"selected=\"selected\"":""%>><%=year%></option>
<% } %>
</select>
</span>
</div>

<div class="submit">
<input type="submit" name="save_button" value="Update article"/>
</div>

</form>
</div>



</div>
<% LayoutHelper.footer(out); %>