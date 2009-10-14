package au.edu.unimelb.news.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import au.edu.unimelb.news.dao.Article;

/**
 * Used by the Import process to translate text based staff news into proper individual
 * newsletters and articles.
 */
public class StaffNewsParser {

	private static Pattern headingPattern = Pattern.compile("^[0-9]+\\..*",Pattern.MULTILINE|Pattern.DOTALL);

	public static List<Article> parse(String message) {
		List<Article> articles = new ArrayList<Article>();

		List<String> sections = findSections(message);
		for(String section : sections) {
			section = cleanHtml(section);
			if(section.indexOf("\n")<1)
				continue;
			if(!headingPattern.matcher(section).matches())
				continue;
			Article article = new Article();
			String heading = trimbuffer(section.substring(0, section.indexOf("\n")));
			heading = trimbuffer(heading.substring(heading.indexOf('.')+1));
			article.setName(heading);
			article.setDetails(trimbuffer(section.substring(section.indexOf('\n'))));
			articles.add(article);
		}

		return articles;
	}

	private static List<String> findSections(String message) {
		List<String> sections = new ArrayList<String>();

		String[] lines = message.split("\n");
		StringBuffer buffer = new StringBuffer();
		for(String line : lines) {
			line = line.trim();
			if(line.startsWith("====")) {
				if(buffer.length()>0) {
					sections.add(trimbuffer(buffer.toString()));
					buffer = new StringBuffer();
				}
				continue;
			}
			buffer.append(trimbuffer(line));
			buffer.append("\n");
		}

		if(buffer.length()>0) {
			sections.add(trimbuffer(buffer.toString()));
			buffer = new StringBuffer();
		}

		return sections;
	}

	private static String cleanHtml(String buffer) {
		buffer = buffer.replace("<h1>", "");
		buffer = buffer.replace("&lt;h1&gt;", "");
		buffer = buffer.replace("<h2>", "");
		buffer = buffer.replace("&lt;h2&gt;", "");
		buffer = buffer.replace("<h3>", "");
		buffer = buffer.replace("&lt;h3&gt;", "");
		return buffer;
	}

	private static String trimbuffer(String buffer) {
		while(buffer.startsWith(" ") || buffer.startsWith("\n") || buffer.startsWith("\r") || buffer.startsWith("\t"))
			buffer = buffer.substring(1);
		while(buffer.endsWith(" ") || buffer.endsWith("\n") || buffer.endsWith("\r") || buffer.endsWith("\t"))
			buffer = buffer.substring(0,buffer.length()-1);
		return buffer;
	}

}
