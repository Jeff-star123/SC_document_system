package com.sjc.schooldocuments.common;

/**
 * A named count used by dashboards, charts, and report tables.
 */
public class CountRow {

    private final String label;
    private final long count;
    private final String cssModifier;

    public CountRow(String label, long count) {
        this(label, count, "");
    }

    public CountRow(String label, long count, String cssModifier) {
        this.label = label;
        this.count = count;
        this.cssModifier = cssModifier;
    }

    public String getLabel() {
        return label;
    }

    public long getCount() {
        return count;
    }

    public String getCssModifier() {
        return cssModifier;
    }
}
