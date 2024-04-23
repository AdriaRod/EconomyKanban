package com.econok.economykanban;

public class CardItem {
    private String title;
    private String tag;
    private int transaction;

    public CardItem(String title, String tag, int transaction) {
        this.title = title;
        this.tag = tag;
        this.transaction = transaction;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    public int getTransaction() {
        return transaction;
    }
}
