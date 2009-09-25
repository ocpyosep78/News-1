<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" import="java.io.*" %>
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
<% LayoutHelper.headerTitled(out,"Policy Library"); %>
<% User user = UserHelper.getUser(request); %>
<% LayoutHelper.menubar(out,user); %>
<% ResourceBundle messages = ResourceBundle.getBundle("messages"); %>

<div id="breadcrumbs">
	<a href="http://www.unimelb.edu.au">University Home</a> &gt;
	Policy library
</div>

<div id="content" class="content-wide">

<% if(exception.getCause()!=null && exception.getCause().getClass() == AuthorisationException.class) { %>

<h2>Authorisation Failure</h2>
<p>You do not have the correct permissions to complete this task.</p><ul><li>If you have been inactive for for a while you may need to sign in again before completing this task.</li><li>If your permissions have just been changed you may need to sign out then sign in before gaining access to complete this task.</li></ul>
<% } else { %>

<h2>System error</h2>
<p>Sorry, an error occured while attempting to complete this task. Please try again.</p>

<% if(exception!=null && exception.getMessage()!=null) { %>
<div class="error">
<%= exception.getMessage() %>
</div>
<% } %>


<p>
If the problem persists please report your problem to the <a href="http://servicedesk.unimelb.edu.au/">service desk</a>.
</p>


<p>
The technical details of the problem are as follows:
</p>
<pre>
<%
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
exception.printStackTrace(pw);
out.print(sw);
sw.close();
pw.close();
%>
</pre>

<% } %>

</div>
<% LayoutHelper.footer(out); %>