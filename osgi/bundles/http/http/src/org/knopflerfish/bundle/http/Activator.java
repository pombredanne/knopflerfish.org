/*
 * Copyright (c) 2003-2013, KNOPFLERFISH project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *
 * - Neither the name of the KNOPFLERFISH project nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.knopflerfish.bundle.http;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import org.knopflerfish.service.log.LogRef;

public class Activator
  implements BundleActivator
{

  // public constants

  public static final String FACTORY_PID =
    "org.knopflerfish.bundle.http.factory.HttpServer";

  // public fields

  public static BundleContext bc = null;

  static LogRef log = null;

  // private fields

  private HttpServerFactory serverFactory = null;

  private ServiceRegistration<ManagedServiceFactory> configReg = null;

  // implements BundleActivator

  public void start(BundleContext bc)
  {

    Activator.bc = bc;

    log = new LogRef(bc, true);

    serverFactory = new HttpServerFactory(bc, log);

    final Dictionary<String, String> parameters =
      new Hashtable<String, String>();
    parameters.put("service.pid", FACTORY_PID);
    configReg =
      bc.registerService(ManagedServiceFactory.class, serverFactory, parameters);

    ServiceReference<ConfigurationAdmin> adminRef = null;
    try {
      ConfigurationAdmin admin = null;
      Configuration[] configs = null;
      try {
        adminRef = bc.getServiceReference(ConfigurationAdmin.class);

        // Potential start order problem!
        if (adminRef != null) {
          admin = bc.getService(adminRef);
          final String filter =
            "(&(service.factoryPid=" + FACTORY_PID + ")"
                + "(|(service.bundleLocation=" + bc.getBundle().getLocation()
                + ")" + "(service.bundleLocation=NULL)"
                + "(!(service.bundleLocation=*))))";
          configs = admin.listConfigurations(filter);
        }
      } catch (final Exception e) {
        if (log.doDebug()) {
          log.debug("Exception when trying to get CM", e);
        }
      }
      if (admin == null) {
        if (log.doInfo()) {
          log.info("No CM present, using default configuration");
        }
        serverFactory.updated(HttpServerFactory.DEFAULT_PID,
                              HttpConfig.getDefaultConfig(bc));
      } else {
        if (configs == null || configs.length == 0) {
          if (log.doInfo()) {
            log.info("No configuration present, creating default configuration");
          }

          serverFactory.updated(HttpServerFactory.DEFAULT_PID,
                                HttpConfig.getDefaultConfig(bc));
        }
      }
    } catch (final ConfigurationException ce) {
      if (log.doError()) {
        log.error("Configuration error", ce);
      }

    } finally {
      if (adminRef != null) {
        bc.ungetService(adminRef);
      }
    }
    registerCommandGroup();

  }

  public void stop(BundleContext bc)
  {

    bc = null;

    configReg.unregister();
    configReg = null;

    serverFactory.destroy();
    serverFactory = null;

    log.close();
    log = null;
  }

  private void registerCommandGroup()
  {
    // Try to see if we can create the command group object.
    try {
      new org.knopflerfish.bundle.http.console.HttpCommandGroup(bc,
                                                                serverFactory);
    } catch (final Exception ex) {
      log.error("Failed to create console command group: " + ex, ex);
    } catch (final LinkageError ce) {
      log.info("There is no KF-console available, command group not created.",
               ce);
    }
  }

} // Activator
