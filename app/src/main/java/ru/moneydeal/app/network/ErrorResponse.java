package ru.moneydeal.app.network;

public class ErrorResponse extends BaseResponse {
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    public static class Data {
        public String message;

        Data(String message) {
            this.message = message;
        }
    }

    public Data data;

    ErrorResponse() {
        super();
        this.data = new Data(INTERNAL_ERROR);
    }
}
