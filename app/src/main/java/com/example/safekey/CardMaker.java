package com.example.safekey;

public class CardMaker {
    String cardName,userName,userPassword;

    public CardMaker(String cardName, String userName, String userPassword) {
        this.cardName = cardName;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getCardName() {
        return cardName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }
}
