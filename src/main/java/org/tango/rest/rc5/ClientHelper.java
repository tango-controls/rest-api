package org.tango.rest.rc5;

import fr.esrf.Tango.ErrSeverity;
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
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

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


        ObjectMapper mapper = new ObjectMapper();
        SimpleModule tangoRestClientModule = new SimpleModule("TangoRestClientModule", new Version(1, 0, 0, null));
        tangoRestClientModule.addDeserializer(ErrSeverity.class, new ErrSeverityDeserializer());
        mapper.registerModule(tangoRestClientModule);


        return new ResteasyClientBuilder()
                .register(new CustomTangoRestClientProvider(mapper))
                .httpEngine(engine)
                .build();
    }

    @Provider
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    private static class CustomTangoRestClientProvider extends JacksonJsonProvider {
        CustomTangoRestClientProvider(ObjectMapper mapper) {
            super(mapper);
        }
    }

    private static class ErrSeverityDeserializer extends JsonDeserializer<ErrSeverity> {
        @Override
        public ErrSeverity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            String severity = jsonParser.getText();
            switch(severity){
                case "PANIC":
                    return ErrSeverity.PANIC;
                case "ERR":
                    return ErrSeverity.ERR;
                case "WARN":
                    return ErrSeverity.WARN;
                default:
                    throw new IOException("Unknown ErrSeverity value: " + severity);
            }
        }
    }
}
