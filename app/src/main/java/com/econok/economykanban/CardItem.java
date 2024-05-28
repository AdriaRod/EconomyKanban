package com.econok.economykanban;

public class CardItem {
    private String title;
    private String transactionType;
    private String tag;
    private String transactionNumber;
    private String fecha;


    public CardItem(String title, String transactionType, String tag, String transaction, String fecha) {
        this.title = title;
        this.transactionType = transactionType;
        this.tag = tag;
        this.transactionNumber = transaction;
        this.fecha=fecha;
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
    public String getFecha(){return fecha;}
    public String getTransactionNumber() {
        return transactionNumber;
    }

}
