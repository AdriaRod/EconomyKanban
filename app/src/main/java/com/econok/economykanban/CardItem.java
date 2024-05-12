package com.econok.economykanban;

public class CardItem {
    private String title;
    private String transactionType;
    private String tag;
    private String transactionNumber;

    public CardItem(String title, String transactionType, String tag, String transaction) {
        this.title = title;
        this.transactionType = transactionType;
        this.tag = tag;
        this.transactionNumber = transaction;
    }

    public String getTitle() {
        return title;
    }
    public String getTransactionType(){
        return transactionType;
    }
    public String getTag() {
        return tag;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

}
