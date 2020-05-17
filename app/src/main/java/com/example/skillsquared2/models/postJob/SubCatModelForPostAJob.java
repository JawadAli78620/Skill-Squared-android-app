package com.example.skillsquared2.models.postAJob.postJob;


import com.example.skillsquared2.models.postAJob.postJob.SubCatChildModelForPostAJob;

import java.util.List;

public class SubCatModelForPostAJob {


    private String id;

    private String title;

    private List<SubCatChildModelForPostAJob> subcatChild = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SubCatChildModelForPostAJob> getSubcatChild() {
        return subcatChild;
    }

    public void setSubcatChild(List<SubCatChildModelForPostAJob> subcatChild) {
        this.subcatChild = subcatChild;
    }

}
