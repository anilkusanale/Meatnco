package com.mytodokart.app.models;

public class GetDataOnClick {

    private static GetDataOnClick onClick = new GetDataOnClick();


    private getDataFromMain getDataFromMain;


    public static GetDataOnClick getInstance() {
        return onClick;
    }


    public void sendDataFromMain(String orderId) {
        if (getDataFromMain != null)
            getDataFromMain.getDataFromMain(orderId);
    }



    public void setGetMainDataListener(getDataFromMain listener) {
        this.getDataFromMain = listener;
    }


    public interface getDataFromMain {
        void getDataFromMain(String orderId);


    }
}
