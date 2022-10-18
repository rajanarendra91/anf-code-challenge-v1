package com.anf.core.worflow;

import com.adobe.granite.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component(service= com.day.cq.workflow.exec.WorkflowProcess.class, immediate = true,
        property={
                "metatype = true",
                "process.label=ANF - Assignment",
                Constants.SERVICE_DESCRIPTION +"=Workflow process step to add property when Page created",
               })


public class AddPropertyProcessStep implements com.day.cq.workflow.exec.WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(AddPropertyProcessStep.class);

    @Override
    public void execute(WorkItem wItem, WorkflowSession workflowSession, MetaDataMap args) {
        WorkflowData workflowData = wItem.getWorkflowData();
        String payloadPath = workflowData.getPayload().toString();
        log.debug("payload  -{}",payloadPath);
        Session session = workflowSession.getSession();
        try {
            Node currentNode = session.getNode(payloadPath+"/jcr:content");
            currentNode.setProperty("pageCreated",true);
            session.save();
        } catch (RepositoryException e) {
            log.error("exception while adding node property to Page");
        }


    }
}
