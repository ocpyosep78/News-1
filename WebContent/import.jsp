<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.util.*' %>
<%@ page import='au.edu.unimelb.news.model.*' %>
<%@ page import='au.edu.unimelb.news.*' %>
<%@ page import='au.edu.unimelb.helper.*' %>
<%@ page import='au.edu.unimelb.news.dao.*' %>
<%@ page import='au.edu.unimelb.security.*' %>
<%@ page import='au.edu.unimelb.security.model.User' %>
<%@ page import='au.edu.unimelb.template.LayoutHelper' %>
<%@ page import='au.edu.unimelb.helper.CookieHelper' %>
<% LayoutHelper.headerTitled(out,"Sign in"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	<a href="<%=Settings.baseUrl%>/">Policy Library</a> &gt;
	Document Import
</div>

<div id="content">
<% SessionFeedback.display(session,out); %>

<form method="POST" action="import" class="contentform" enctype="multipart/form-data">
<h3>Document import</h3>

<label for='committee_code'>
<span class='question'>File:</span>
<span class='answer'>
<input type="file" name="import_file"/>
</span>
</label>

<div class="submit"><input type="submit" value="Import file"/></div>
</form>

</div>
<% LayoutHelper.footer(out); %>