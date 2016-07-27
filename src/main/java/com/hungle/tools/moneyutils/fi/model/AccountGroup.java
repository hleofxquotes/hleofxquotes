package com.hungle.tools.moneyutils.fi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lobas_av
 * 
 */
public class AccountGroup extends AbstractModelObject {
    private List<Account> m_persons = new ArrayList<Account>();
    private String m_name;

    public AccountGroup() {
    }

    public AccountGroup(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        String oldValue = m_name;
        m_name = name;
        firePropertyChange("name", oldValue, m_name);
    }

    public void addPerson(Account person) {
        List<Account> oldValue = m_persons;
        m_persons = new ArrayList<Account>(m_persons);
        m_persons.add(person);
        firePropertyChange("persons", oldValue, m_persons);
        firePropertyChange("personCount", oldValue.size(), m_persons.size());
    }

    public void removePerson(Account person) {
        List<Account> oldValue = m_persons;
        m_persons = new ArrayList<Account>(m_persons);
        m_persons.remove(person);
        firePropertyChange("persons", oldValue, m_persons);
        firePropertyChange("personCount", oldValue.size(), m_persons.size());
    }

    public List<Account> getPersons() {
        return m_persons;
    }

    public int getPersonCount() {
        return m_persons.size();
    }
}