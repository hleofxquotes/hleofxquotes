package com.le.tools.moneyutils.stockprice;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class FieldInfo {
    private int index;

    private String key;

    private String name;

    public FieldInfo(int index, String key, String name) {
        this.index = index;
        this.key = key;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

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