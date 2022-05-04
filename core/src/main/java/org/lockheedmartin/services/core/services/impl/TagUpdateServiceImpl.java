package org.lockheedmartin.services.core.services.impl;

import org.lockheedmartin.services.core.services.TagUpdateService;
import org.lockheedmartin.services.core.services.TagUpdateServiceConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = TagUpdateService.class, immediate = true)
@Designate(ocd = TagUpdateServiceConfiguration.class)
public class TagUpdateServiceImpl implements TagUpdateService
{
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(TagUpdateServiceImpl.class);

    /**
     * Instance of the OSGi configuration class
     */
    private TagUpdateServiceConfiguration configuration;

    @Activate
    protected void activate(TagUpdateServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getResponse()
    {
        return "Response";
    }
}
