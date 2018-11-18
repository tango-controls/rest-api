package org.tango.rest.test;

import com.google.common.base.Preconditions;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 11/14/18
 */
public class Context {
    public static final String SYS_TG_TEST_1 = "sys/tg_test/1";
    protected String url;
    protected URI uri;
    protected String tango_host;
    protected String tango_port;
    protected String auth;
    protected String user;
    protected String password;
    protected String token;
    protected URI devicesUri;
    protected URI longScalarWUri;
    protected URI uShortImageRO;

    public static Context create(String restApiVersion){
        Context result = new Context();
        result.url = System.getProperty("tango.rest.url");
        Preconditions.checkNotNull(result.url, "tango.rest.url is not defined! Rerun using `mvn test -Dtango.rest.url={url}`");
        result.uri = UriBuilder.fromUri(URI.create(result.url)).path(restApiVersion).build();

        result.tango_host = System.getProperty("tango.host");
        Preconditions.checkNotNull(result.tango_host, "tango.host is not defined! Rerun using `mvn test -Dtango.host={TANGO_HOST}`");

        result.tango_port = System.getProperty("tango.port");
        Preconditions.checkNotNull(result.tango_port, "tango.port is not defined! Rerun using `mvn test -Dtango.port=10000`");

        result.auth = System.getProperty("tango.rest.auth.method");
        Preconditions.checkNotNull(result.auth, "tango.rest.auth.method is not defined! Rerun using `mvn test -Dtango.rest.auth.method={auth}`. Auth = basic|oauth");

        switch (result.auth){
            case "basic":
                result.user = System.getProperty("tango.rest.user");
                Preconditions.checkNotNull(result.user,
                        "tango.rest.user is not defined! Rerun using `mvn test -Dtango.rest.user={user}`");
                result.password = System.getProperty("tango.rest.password");
                Preconditions.checkNotNull(result.password,
                        "tango.rest.password is not defined! Rerun using `mvn test -Dtango.rest.password={password}`");
                break;
            case "oauth":
                result.token = System.getProperty("tango.rest.oauth.token");
                Preconditions.checkNotNull(result.password,
                        "tango.rest.oauth.token is not defined! Rerun using `mvn test -Dtango.rest.oauth.token={token}`");
                break;
            default:
                throw new IllegalStateException("tango.rest.auth must be either basic or oauth!");
        }

        UriBuilder uriBuilder = UriBuilder.fromUri(result.uri).path("hosts").path(result.tango_host).matrixParam("port", result.tango_port).path("devices");
        result.devicesUri = uriBuilder.build();
        result.longScalarWUri = UriBuilder.fromUri(result.devicesUri).path(SYS_TG_TEST_1).path("attributes").path("long_scalar_w").build();
        result.uShortImageRO = UriBuilder.fromUri(result.devicesUri).path(SYS_TG_TEST_1).path("attributes").path("ushort_image_ro").build();

        return result;
    }
}
