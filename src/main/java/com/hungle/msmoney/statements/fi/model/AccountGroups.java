package com.hungle.msmoney.statements.fi.model;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class AccountGroups.
 *
 * @author lobas_av
 */
public class AccountGroups extends AbstractModelObject {
    
    /** The m groups. */
    private List<AccountGroup> m_groups = new ArrayList<AccountGroup>();

    /**
     * Adds the group.
     *
     * @param group the group
     */
    public void addGroup(AccountGroup group) {
        List<AccountGroup> oldValue = m_groups;
        m_groups = new ArrayList<AccountGroup>(m_groups);
        m_groups.add(group);
        firePropertyChange("groups", oldValue, m_groups);
    }

    /**
     * Removes the group.
     *
     * @param group the group
     */
    public void removeGroup(AccountGroup group) {
        List<AccountGroup> oldValue = m_groups;
        m_groups = new ArrayList<AccountGroup>(m_groups);
        m_groups.remove(group);
        firePropertyChange("groups", oldValue, m_groups);
    }

    /**
     * Gets the groups.
     *
     * @return the groups
     */
    public List<AccountGroup> getGroups() {
        return m_groups;
    }
}