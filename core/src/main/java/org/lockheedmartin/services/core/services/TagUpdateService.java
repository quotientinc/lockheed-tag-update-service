package org.lockheedmartin.services.core.services;

import javax.jcr.RepositoryException;
public interface TagUpdateService
{
    /**
     * This method makes the HTTP call on the given URL
     *
     * @return {@link String}
     */
    String getResponse(boolean testRun) throws RepositoryException;
}
