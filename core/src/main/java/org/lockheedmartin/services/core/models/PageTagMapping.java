package org.lockheedmartin.services.core.models;

import java.util.ArrayList;
import java.util.List;

public class PageTagMapping
{
    private String path;
    private List<String> baTags;
    private List<String>  contentTypes;

    public PageTagMapping(String url, String baStr, String contentTypeStr)
    {
        this.path = "/content/lockheed-martin" + url.replace("https://www.lockheedmartin.com", "").replace(".html", "");
        this.baTags = parseTags("business-area:", baStr);
        this.contentTypes = parseTags("content-type:", contentTypeStr);
    }

    private List<String> parseTags(String tagPrefix, String tagStr)
    {
        if(!tagStr.trim().isEmpty())
        {
            String[] splitTags = tagStr.split(",");
            List<String> tags = new ArrayList<>();

            for(int i = 0; i < splitTags.length; i++)
            {
                tags.add(tagPrefix + splitTags[i].replace(" ", "-").toLowerCase());
            }

            return tags;
        }

        return new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public List<String> getBaTags() {
        return baTags;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public int getBaTagCount() {
        return baTags.size();
    }

    public int getContentTagCount() {
        return contentTypes.size();
    }
}


