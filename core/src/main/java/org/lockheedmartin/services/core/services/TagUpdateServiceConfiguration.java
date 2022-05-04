package org.lockheedmartin.services.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Tag Update Service Configuration",
        description = "Tag Update Service OSGi Configuration")
public @interface TagUpdateServiceConfiguration
{
    /**
     * CSV Path
     *
     * @return {@link String}
     */
    @AttributeDefinition(
            name = "CSV Path",
            description = "Path to tag update csv in AEM.")
    public String csvPath();
}
