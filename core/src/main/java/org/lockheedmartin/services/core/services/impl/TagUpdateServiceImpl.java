package org.lockheedmartin.services.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.lockheedmartin.services.core.models.PageTagMapping;
import org.lockheedmartin.services.core.models.PageUpdateEntry;
import org.lockheedmartin.services.core.services.TagUpdateService;
import org.lockheedmartin.services.core.services.TagUpdateServiceConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.jcr.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component(service = TagUpdateService.class, immediate = true)
@Designate(ocd = TagUpdateServiceConfiguration.class)
public class TagUpdateServiceImpl implements TagUpdateService
{
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(TagUpdateServiceImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Session session;
    private ResourceResolver resourceResolver;

    /**
     * Instance of the OSGi configuration class
     */
    private TagUpdateServiceConfiguration configuration;

    @Activate
    protected void activate(TagUpdateServiceConfiguration configuration) throws LoginException
    {
        resourceResolver = resolverFactory.getServiceResourceResolver(null);
        session = resourceResolver.adaptTo(Session.class);
        this.configuration = configuration;
    }

    @Override
    public String getResponse(boolean testRun) throws RepositoryException {
        List<PageTagMapping> mappings = getPageTagMappings(getNodeData());
        log.info("# of Mappings: " + mappings.size());

        PageManager pageManager;
        pageManager = resourceResolver.adaptTo(PageManager.class);

        List<PageUpdateEntry> entries = new ArrayList<>();

        for(int i = 0; i < mappings.size(); i++)
        {
            PageTagMapping ptm = mappings.get(i);
            Page p = pageManager.getPage(ptm.getPath());

            if(i == 0)
            {
                log.info("PATH: " + ptm.getPath());
            }

            ValueFactory factory = session.getValueFactory();

            boolean shouldUpdate = false;
            boolean shouldUpdateBaTags = false;
            boolean shouldUpdateContentTypeTags = false;

            if(p != null)
            {
                log.info("Page: " + ptm.getPath());
                String name = p.getName();
                String path = p.getPath();

                Node pNode = p.adaptTo(Node.class);

                if(pNode.hasNode("jcr:content") && (ptm.getContentTagCount() > 0 || ptm.getBaTagCount() > 0))
                {
                    Node content = pNode.getNode("jcr:content");

                    if(!content.hasProperty("businessAreaTag") || (content.getProperty("businessAreaTag").isMultiple() && content.getProperty("businessAreaTag").getLength() < 1))
                    {
                        if(testRun)
                        {
                            if(ptm.getBaTagCount() > 1)
                            {
                                Value[] baContent = new Value[ptm.getBaTagCount()];

                                for(int j = 0; j < ptm.getBaTagCount(); j++)
                                {
                                    baContent[j] = factory.createValue(ptm.getBaTags().get(j));
                                }

                                content.setProperty("businessAreaTag", baContent);
                            }
                            else
                            {
                                Value baContent = factory.createValue(ptm.getBaTags().get(0));
                                content.setProperty("businessAreaTag", baContent);
                            }
                        }

                        shouldUpdate = true;
                        shouldUpdateBaTags = true;
                    }

                    if(!content.hasProperty("contentTypeTag") || (content.getProperty("contentTypeTag").isMultiple() && content.getProperty("contentTypeTag").getLength() < 1))
                    {
                        if(testRun)
                        {
                            if(ptm.getContentTagCount() > 1)
                            {
                                Value[] contentTypeContent = new Value[ptm.getContentTagCount()];

                                for(int j = 0; j < ptm.getContentTagCount(); j++)
                                {
                                    contentTypeContent[j] = factory.createValue(ptm.getContentTypes().get(j));
                                }

                                content.setProperty("contentTypeTag", contentTypeContent);
                            }
                            else
                            {
                                Value contentTypeContent = factory.createValue(ptm.getContentTypes().get(0));
                                content.setProperty("contentTypeTag", contentTypeContent);
                            }
                        }

                        shouldUpdate = true;
                        shouldUpdateContentTypeTags = true;
                    }

                    session.save();

                    if(shouldUpdate)
                    {
                        log.info("Should Update hit");
                        List<String> baEntryTags = shouldUpdateBaTags ? ptm.getBaTags() : new ArrayList<>();
                        List<String> contentTypeEntryTags = shouldUpdateContentTypeTags ? ptm.getContentTypes() : new ArrayList<>();

                        entries.add(new PageUpdateEntry(p.getName(), p.getPath(), baEntryTags, contentTypeEntryTags));
                    }
                }
            }
        }

        Gson gson = new Gson();

        return gson.toJson(entries);
    }

    protected List<PageTagMapping> getPageTagMappings(Binary data)
    {
        List<PageTagMapping> mappings = new ArrayList<>();

        try
        {
            CSVParser csvParser = new CSVParser(new InputStreamReader(data.getStream()), CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());

            for (CSVRecord csvRecord : csvParser) {
                // Accessing values by Header names
                String url = csvRecord.get("URL");
                String baTag = csvRecord.get("Desired BA Tag");
                String contentType = csvRecord.get("Desired Content Type");

                PageTagMapping ptm = new PageTagMapping(url, baTag, contentType);
                mappings.add(ptm);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        data.dispose();

        return mappings;
    }

    protected Binary getNodeData() throws RepositoryException
    {
        Node root = session.getRootNode();
        return root.getNode(configuration.csvPath()).getNode("jcr:content").getProperty("jcr:data").getBinary();
    }
}
