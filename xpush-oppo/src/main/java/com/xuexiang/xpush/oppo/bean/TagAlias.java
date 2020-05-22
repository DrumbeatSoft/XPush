package com.xuexiang.xpush.oppo.bean;

import java.util.List;

public class TagAlias {

    /**
     * alias : string
     * tagList : ["string"]
     */

    private String alias;
    private List<String> tagList;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}
