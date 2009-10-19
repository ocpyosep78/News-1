package au.edu.unimelb.news.model;

import java.io.PrintWriter;

import au.edu.unimelb.security.LogHelper;
import au.edu.unimelb.security.model.User;

/**
 * Handle callback messages from the article importer.
 */
public class ArticleImportResponder {

	private User user;
	private PrintWriter out;

	public ArticleImportResponder(User user, PrintWriter out) {
		this.user = user;
		this.out = out;
	}

	public void feedback(String message) {
		System.out.println("IMPORT: "+message);
		LogHelper.log("Sysetm","Import",user.getPersonId(),message,user.getIP());
		out.println("<li>"+message+"</li>");
		//out.flush();
	}

}
