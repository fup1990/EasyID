package com.gome.pop.fup.easyid.model;

import java.io.Serializable;

/**
 * Created by fupeng-ds on 2017/10/19.
 */
public class Response implements Serializable{

    public boolean isFinish;

    public String data;

    public long id;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Response{" +
                "isFinish=" + isFinish +
                ", data='" + data + '\'' +
                ", id=" + id +
                '}';
    }
}
