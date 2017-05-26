package org.tango.rest;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.ws.rs.client.Client;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.14
 */
public class ClientHelper {
    private ClientHelper() {
    }

    /**
     * Creates a new client with prepopulated user and password for host
     * <p/>
     * Resulting client uses {@link PoolingClientConnectionManager} to handle connections with default setup
     * <p/>
     * {@link JsonResponseReader} is registered with this client
     *
     * @param host     host name to which bind provided credentials
     * @param user     user name
     * @param password user password
     * @return a new Client instance
     */
    public static Client initializeClientWithBasicAuthentication(String host, String user, String password) {
        // 1. Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();

// 2. Generate BASIC scheme object and add it to the local auth cache
        AuthScheme basicAuth = new BasicScheme();
        authCache.put(new HttpHost(host), basicAuth);

// 3. Add AuthCache to the execution context
        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);

// 4. Create client executor and proxy
        ClientConnectionManager cm = new PoolingClientConnectionManager();
        DefaultHttpClient httpClient = new DefaultHttpClient(cm);
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        httpClient.setCredentialsProvider(basicCredentialsProvider);
        ClientHttpEngine engine = new ApacheHttpClient4Engine(httpClient, localContext);
        return new ResteasyClientBuilder()
                .httpEngine(engine)
                .build();
    }
}
