package com.hungle.tools.moneyutils.fi.model;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class AccountGroup.
 *
 * @author lobas_av
 */
public class AccountGroup extends AbstractModelObject {
    
    /** The m persons. */
    private List<Account> m_persons = new ArrayList<Account>();
    
    /** The m name. */
    private String m_name;

    /**
     * Instantiates a new account group.
     */
    public AccountGroup() {
    }

    /**
     * Instantiates a new account group.
     *
     * @param name the name
     */
    public AccountGroup(String name) {
        m_name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        String oldValue = m_name;
        m_name = name;
        firePropertyChange("name", oldValue, m_name);
    }

    /**
     * Adds the person.
     *
     * @param person the person
     */
    public void addPerson(Account person) {
        List<Account> oldValue = m_persons;
        m_persons = new ArrayList<Account>(m_persons);
        m_persons.add(person);
        firePropertyChange("persons", oldValue, m_persons);
        firePropertyChange("personCount", oldValue.size(), m_persons.size());
    }

    /**
     * Removes the person.
     *
     * @param person the person
     */
    public void removePerson(Account person) {
        List<Account> oldValue = m_persons;
        m_persons = new ArrayList<Account>(m_persons);
        m_persons.remove(person);
        firePropertyChange("persons", oldValue, m_persons);
        firePropertyChange("personCount", oldValue.size(), m_persons.size());
    }

    /**
     * Gets the persons.
     *
     * @return the persons
     */
    public List<Account> getPersons() {
        return m_persons;
    }

    /**
     * Gets the person count.
     *
     * @return the person count
     */
    public int getPersonCount() {
        return m_persons.size();
    }
}