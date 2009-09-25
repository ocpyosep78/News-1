package au.edu.unimelb.news.model;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

public class SessionFeedback {

	public static void display(HttpSession session, JspWriter out) throws IOException {
		if(session.getAttribute("errors")!=null) {
			out.print("<div class=\"error\"> "+session.getAttribute("errors")+"</div>");
			session.setAttribute("errors",null);
		}
		if(session.getAttribute("info")!=null) {
			out.print("<div class=\"info\"> "+session.getAttribute("info")+"</div>");
			session.setAttribute("info",null);
		}
		if(session.getAttribute("login_message")!=null) {
			out.print("<div class=\"error\"> "+session.getAttribute("login_message")+"</div>");
			session.setAttribute("login_message",null);
		}

	}

}
