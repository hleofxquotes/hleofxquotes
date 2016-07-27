package com.hungle.tools.moneyutils.fi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lobas_av
 * 
 */
public class AccountGroups extends AbstractModelObject {
    private List<AccountGroup> m_groups = new ArrayList<AccountGroup>();

    public void addGroup(AccountGroup group) {
        List<AccountGroup> oldValue = m_groups;
        m_groups = new ArrayList<AccountGroup>(m_groups);
        m_groups.add(group);
        firePropertyChange("groups", oldValue, m_groups);
    }

    public void removeGroup(AccountGroup group) {
        List<AccountGroup> oldValue = m_groups;
        m_groups = new ArrayList<AccountGroup>(m_groups);
        m_groups.remove(group);
        firePropertyChange("groups", oldValue, m_groups);
    }

    public List<AccountGroup> getGroups() {
        return m_groups;
    }
}