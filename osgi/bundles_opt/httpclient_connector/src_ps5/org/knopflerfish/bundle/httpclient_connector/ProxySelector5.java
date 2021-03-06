/*
 * Copyright (c) 2006-2012, KNOPFLERFISH project
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
package org.knopflerfish.bundle.httpclient_connector;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;

import org.osgi.framework.BundleContext;

/**
 * Uses java.net.ProxySelector to configure the HttpClient.
 */
public class ProxySelector5
{
  /**
   * Set proxy properties for connecting to the given uri on the given
   * client-object.
   *
   * @param bc bundle context to use to fetch property values.
   * @param client the http-client to configure.
   * @param url the URL to configure connection for. The url string
   *            is assumed to be in non-escaped form, that is not RFC
   *            2396 compatible. I.e., it should not contain any
   *            %-encoded chars.
   */
  public static Object[] configureProxy(final HttpClient client,
                                        final String url)
    throws URIException
  {
    final URI uri;

    try {
      // URI requires proper encoding accoring to RFC 2396, URL does
      // not do that. To handle url-strings with spaces and other non
      // 2396 compatible stuff we first create an URL then build an
      // UIR from the parts of the URL. Note that URL.toURI() requires
      // that the initial URL is on 2396 format, but the constructor
      // below does not (it encodes when needed).
      final URL url1 = new URL(url);
      uri = new URI(url1.getProtocol(),
                    url1.getUserInfo(), url1.getHost(), url1.getPort(),
                    url1.getPath(), url1.getQuery(), url1.getRef());
    } catch (Exception e) {
      throw new URIException(e.toString());
    }

    final ProxySelector ps = ProxySelector.getDefault();
    final List<Proxy> proxies = ps.select(uri);

    if (0==proxies.size()) {
      return null;
    }
    final Proxy proxy = proxies.get(0);
    if (Proxy.Type.DIRECT.equals(proxy.type())) {
      return null;
    }

    final SocketAddress saddr = proxy.address();
    if (saddr instanceof InetSocketAddress) {
      final InetSocketAddress isaddr = (InetSocketAddress) saddr;
      final HostConfiguration conf = client.getHostConfiguration();

      conf.setProxy(isaddr.getHostName(), isaddr.getPort());
      return new Object[]{isaddr.getHostName(),
                          Integer.valueOf(isaddr.getPort())};
    }
    return null;
  }

}
