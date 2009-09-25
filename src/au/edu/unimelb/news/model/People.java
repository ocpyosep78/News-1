package au.edu.unimelb.news.model;

import au.edu.unimelb.security.dao.DAOFactory;
import au.edu.unimelb.security.dao.Person;

public class People {

	/**
	 * Find the person uniquely identified by an email address. Returns the first non deleted
	 * person record that matches the specified email address.
	 *
	 * @param email
	 * @return
	 */
	public static Person getByEmail(String email) {
		try {
			return DAOFactory.getPersonFactory().getByDeletedEmail(false, email, 0, 1).get(0);
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns the name of a person, if the person can not be found, returns the email address.
	 */
	public static String getPersonsNameByEmail(String email) {
		Person person = getByEmail(email);
		if(person!=null) return person.getName();
		return email;
	}
	
	public static String getNameById(Long id) {
		try {
			return DAOFactory.getPersonFactory().get(id).getName();
		} catch(Exception e) {
			return null;
		}
	}

}
