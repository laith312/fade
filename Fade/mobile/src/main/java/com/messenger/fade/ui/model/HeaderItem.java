package com.messenger.fade.ui.model;

public class HeaderItem implements Comparable, ItemType {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int compareTo(Object another) {

        if (this == another) {
            return 0;
        }

        if (another == null) {
            return -1;
        }

        if (getClass() != another.getClass()) {
            return -1;
        }
        HeaderItem headerItem = (HeaderItem) another;

        return title.compareTo(headerItem.getTitle());
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    @Override
    public String toString() {
        return title;
    }
}
