package com.anf.core.servlets;


import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletPaths(
        value = "/bin/queryresultservlet"
)
public class QueryResultsServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(QueryResultsServlet.class);



    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        List<String> resultList = new ArrayList<>();
        ResourceResolver resourceResolver = req.getResourceResolver();
        getQueryResults(resultList ,resourceResolver);
        //resultList will have 10 pages, get jcr:title from each page
        resultList.forEach( item -> {
            Resource resource = resourceResolver.getResource(item);
            String pageTitle = (String) resource.getValueMap().getOrDefault("jcr:title", "");
            log.debug("Result entry path- {} ,Page Title - {}", item ,pageTitle);
            }
        );
    }

    private List<String> getQueryResults(List<String> resultList, ResourceResolver resourceResolver) {

        try {
            QueryBuilder builder = resourceResolver.adaptTo(QueryBuilder.class);
            Session session = resourceResolver.adaptTo(Session.class);
            Map<String, String> map = new HashMap<>();
            map.put("path", "/content/anf-code-challenge");
            map.put("type", "cq:PageContent");
            map.put("property", "anfCodeChallenge");
            map.put("property.value", "true");
            map.put("p.limit", "10");
            map.put("p.guessTotal", "true");
            log.debug("map = {}", map);
            Query query = builder.createQuery(PredicateGroup.create(map), session);
            SearchResult result = query.getResult();
            for (Hit hit : result.getHits()) {
               String pagePath = hit.getPath();
               resultList.add(pagePath);
            }
        } catch (RepositoryException e) {
            log.error("Exception in getPermitListedTags due to = {}", e.getMessage());
        }
        return resultList;
    }
}
