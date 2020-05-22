package com.xuexiang.xpush.oppo.bean;

import java.util.List;

public class TagAliasRequest {

    /**
     * addTagList : ["string"]
     * deleteTagList : ["string"]
     * projectName : oa
     * registrationId : string
     * updateAlias : string
     */

    private String projectName;
    private String registrationId;
    private String updateAlias;
    private List<String> addTagList;
    private List<String> deleteTagList;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getUpdateAlias() {
        return updateAlias;
    }

    public void setUpdateAlias(String updateAlias) {
        this.updateAlias = updateAlias;
    }

    public List<String> getAddTagList() {
        return addTagList;
    }

    public void setAddTagList(List<String> addTagList) {
        this.addTagList = addTagList;
    }

    public List<String> getDeleteTagList() {
        return deleteTagList;
    }

    public void setDeleteTagList(List<String> deleteTagList) {
        this.deleteTagList = deleteTagList;
    }
}
