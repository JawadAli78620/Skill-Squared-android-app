package com.example.skillsquared2.models.postAJob.postJob;


import java.util.List;

public class GetCategoriesModelForPostAJob {

    private String catId;
    private String title;
    private List<SubCatModelForPostAJob> subcat = null;

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SubCatModelForPostAJob> getSubcat() {
        return subcat;
    }

    public void setSubcat(List<SubCatModelForPostAJob> subcat) {
        this.subcat = subcat;
    }

}
