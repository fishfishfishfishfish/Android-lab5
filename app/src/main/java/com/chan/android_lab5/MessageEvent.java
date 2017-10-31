package com.chan.android_lab5;

/**
 * Created by 61915 on 17/10/31.
 */

public class MessageEvent {
    private String name_msg;
    private String price_msg;

    public MessageEvent(String name, String price) {
        super();
        name_msg = name;
        price_msg= price;
    }

    public String getNameMsg() {
        return name_msg;
    }

    public String getPriceMsg() {
        return price_msg;
    }
}
