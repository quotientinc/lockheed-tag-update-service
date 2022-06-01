package org.lockheedmartin.services.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.lockheedmartin.services.core.services.TagUpdateService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=HTTP servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/services/tags-post" })
public class TagUpdatePostServlet extends SlingSafeMethodsServlet
{
    /**
     * Generated serialVersionUid
     */
    private static final long serialVersionUID = -2014397651676211439L;

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(TagUpdateService.class);

    @Reference
    private TagUpdateService tagUpdateService;

    /**
     * Overridden doGet() method
     * NOTE: This is a get that functions like a POST because AEM restricts AJAX Post Requests.
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {

            String res = tagUpdateService.getResponse(true);
            response.setContentType("application/json");

            /**
             * Printing the json response on the browser
             */
            response.getWriter().println(res);

        } catch (Exception e) {

            log.error(e.getMessage(), e);
        }
    }
}
