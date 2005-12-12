/*
 * @(#)Activator.java        1.0 2005/06/28
 *
 * Copyright (c) 2003-2005 Gatespace telematics AB
 * Otterhallegatan 2, 41670,Gothenburgh, Sweden.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Gatespace telematics AB. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Gatespace telematics AB.
 */
package org.knopflerfish.bundle.event;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.event.EventAdmin;
import org.knopflerfish.service.log.LogRef;

/**
 * The Activator class is the startup class for the EventHandlerService.
 *
 * @author Magnus Klack
 */
public class Activator implements BundleActivator {

    /** the service id string */
    final static String SERVICE_PID = "org.osgi.service.event.EventAdmin";

    protected static BundleContext bundleContext;
    protected static LogRef log;

    private EventAdminService eventAdmin;

    /**
     * Main entry for the service
     *
     * @param context the BundleContext ,ie, the handle to the OSGi framework
     */
    public void start(BundleContext context) throws Exception {
        /* assign the context variable to a local variable */
        bundleContext = context;
        log = new LogRef(context);

        /* create the event admin service */
        eventAdmin = new EventAdminService(bundleContext);
        /* create the hashtable */
        Hashtable propsTable = new Hashtable();
        /* add the Constant variable and the id to the Hashtable */
        propsTable.put(Constants.SERVICE_PID, SERVICE_PID);
        /* register the service to the framework */
        bundleContext.registerService(EventAdmin.class.getName(), eventAdmin,
                propsTable);
    }

    /**
     * Stop the service
     *
     * @param context the BundleContext ,ie, the handle to the OSGi framework
     */
    public void stop(BundleContext context) throws Exception {
      eventAdmin.stop();
      eventAdmin = null;
    }
}