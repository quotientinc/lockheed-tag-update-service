package org.lockheedmartin.services.core.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PageUpdateEntry
{
    @Expose
    private String name;

    @Expose
    private String path;

    @Expose @SerializedName(value = "businessAreaTags")
    private List<String> baTags;

    @Expose @SerializedName(value = "contentTypeTags")
    private List<String> contentTypeTags;

    public PageUpdateEntry(String name, String path, List<String> businessAreaTags, List<String> contentTypeTags)
    {
        this.name = name;
        this.path = path;
        this.baTags = businessAreaTags;
        this.contentTypeTags = contentTypeTags;
    }
}
