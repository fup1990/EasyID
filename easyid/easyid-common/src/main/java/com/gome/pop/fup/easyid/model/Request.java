package com.gome.pop.fup.easyid.model;

import java.io.Serializable;

/**
 * Created by fupeng-ds on 2017/8/3.
 */
public class Request implements Serializable{

    /**
     * 请求类型：0-创建id
     */
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Request(int type) {
        this.type = type;
    }

    public Request() {
    }


}
