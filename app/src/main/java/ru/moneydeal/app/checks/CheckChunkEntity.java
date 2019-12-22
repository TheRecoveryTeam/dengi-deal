package ru.moneydeal.app.checks;


public class CheckChunkEntity {
    public String checkId;

    public String userId;

    public Integer amount;

    CheckChunkEntity(String checkId, String userId, Integer amount) {
        this.checkId = checkId;
        this.userId = userId;
        this.amount = amount;
    }
}
