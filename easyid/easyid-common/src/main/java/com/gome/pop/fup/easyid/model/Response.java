package com.gome.pop.fup.easyid.model;

import java.io.Serializable;

/**
 * Created by fupeng-ds on 2017/10/19.
 */
public class Response implements Serializable{

    public String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
