package com.hungle.tools.moneyutils.stockprice;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

// TODO: Auto-generated Javadoc
/**
 * The Class FieldInfo.
 */
public class FieldInfo {
    
    /** The index. */
    private int index;

    /** The key. */
    private String key;

    /** The name. */
    private String name;

    /**
     * Instantiates a new field info.
     *
     * @param index the index
     * @param key the key
     * @param name the name
     */
    public FieldInfo(int index, String key, String name) {
        this.index = index;
        this.key = key;
        this.name = name;
    }

    /**
     * Gets the index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Creates the field info set.
     *
     * @return the sorted set
     */
    public static SortedSet<FieldInfo> createFieldInfoSet() {
        Comparator<FieldInfo> comparator = new Comparator<FieldInfo>() {

            @Override
            public int compare(FieldInfo o1, FieldInfo o2) {
                return o1.getIndex() - o2.getIndex();
            }
        };
        SortedSet<FieldInfo> sortedFields = new TreeSet<FieldInfo>(comparator);
        return sortedFields;
    }

}