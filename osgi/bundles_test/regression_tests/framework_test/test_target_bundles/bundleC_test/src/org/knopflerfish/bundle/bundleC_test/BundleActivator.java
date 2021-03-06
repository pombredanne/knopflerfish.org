/*
 * Copyright (c) 2004, KNOPFLERFISH project
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

package org.knopflerfish.bundle.bundleC_test;

import java.util.*;
import org.osgi.framework.*;
import org.knopflerfish.service.bundleC_test.*;

/*
   The start method tries to install the bundle(s) needed to do the test
   The user has to fill in the bundles array with the bundles needed
   to do the test.

   To facilitate that the test may take place in another location
   the POSGRUNDIR property is prepended before the bundle names
   before an attempt to load them is made
*/

public class BundleActivator implements org.osgi.framework.BundleActivator {
  BundleContext bc;
  BundC s;
  ServiceReference sr1;
  String []serviceDescription = {"org.knopflerfish.service.bundleC_test.BundleC", "java.lang.Object"};
  ServiceRegistration sreg;

  public void start(BundleContext bc) {
    this.bc = bc;
    s = new BundC(bc);

    Hashtable dict = new Hashtable();
    dict.put ("key1","value1");
    dict.put ("key2","value2");
    try {
      sreg = bc.registerService(serviceDescription, s, dict);
    }

    catch (RuntimeException ru) {
      System.out.println ("Exception " + ru + " in BundleC start");
      ru.printStackTrace();
    }
    sr1 = bc.getServiceReference("org.knopflerfish.service.bundleC_test.BundleC");
  }

  public void stop(BundleContext bc) {
    bc.ungetService(sr1);
  }
}
