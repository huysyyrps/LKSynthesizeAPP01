package com.example.lksynthesizeapp.Constant.wifi.bean;

public class MessageEvent {

    private int state;
    private String result;

    public MessageEvent(int state,String result){
        this.state=state;
        this.result=result;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "state=" + state +
                ", result='" + result + '\'' +
                '}';
    }

}
