/*
 * Copyright (c)2007 The University of Melbourne, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of The
 * University of Melbourne. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms and conditions of contractual and/or employment
 * policy of the university.
 *
 * THE UNIVERSITY OF MELBOURNE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE UNIVERSITY OF MELBOURNE SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package au.edu.unimelb.news.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import au.edu.unimelb.news.Configuration;
import au.edu.unimelb.news.model.ArticleImport;
import au.edu.unimelb.news.model.ArticleImportResponder;
import au.edu.unimelb.security.UserHelper;
import au.edu.unimelb.security.model.User;
import au.edu.unimelb.template.LayoutHelper;

/**
 * Handles requests to create new agenda items for a speciffic meeting.
 */
@SuppressWarnings("serial")
public class ImportServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final Random random=new Random();

	public ImportServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * Reads all user input related to creating a new agenda item and creates the agenda item.
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user=UserHelper.getUser(request);
		response.setContentType("text/html");
		PrintWriter out = new PrintWriter(response.getOutputStream());
		LayoutHelper.headerTitled(out, "Import");
		LayoutHelper.menubar(out,user);

		out.println("<div id=\"breadcrumbs\">");
		out.println("<a href=\"http://www.unimelb.edu.au\">University home</a> &gt;");
		out.println("<a href=\""+Configuration.appPrefix+"/\">University News</a> &gt;");
		out.println("Document Import");
		out.println("</div>");

		out.flush();

		out.println("<div id=\"content\">");
		out.println("<h2>Importing</h2>");

		/*
		 *  This chunk calls the Jakarta Commons Fileupload component to
		 *  process the file upload information.
		 */
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e1) {
			e1.printStackTrace();
			throw new IOException(e1.getMessage());
		}

		/*
		 * Use the Jakarta Commons Fileupload component to read the
		 * field variables.
		 */
		out.println("<ul>");
		String filename="";
		for(FileItem field : items) {
			if (!field.isFormField()) {
				filename=field.getName();
				if(filename.contains("/")) filename=filename.substring(filename.lastIndexOf('/')+1);
				if(filename.contains("\\")) filename=filename.substring(filename.lastIndexOf('\\')+1);
				int no=random.nextInt();
				if(no<1) no=-no;
				if(filename.length()>0 && field.getSize()>0 && field.getFieldName().equals("import_file")) {
					ArticleImport helper = new ArticleImport(new ArticleImportResponder(user,out));
					helper.process(field.getInputStream(), user);
				}
			}
		}
		out.println("</ul>");

		out.println("</div>");
		LayoutHelper.footer(out);

		out.flush();
		out.close();
	}


}
