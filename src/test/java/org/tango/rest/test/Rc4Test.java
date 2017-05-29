package org.tango.rest.test;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tango.rest.ClientHelper;
import org.tango.rest.entities.*;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class Rc4Test {
    public static final String SYS_TG_TEST_1 = "sys/tg_test/1";
    protected static String url;
    protected static URI uri;
    protected static String tango_host;
    protected static String tango_port;
    protected static String auth;
    protected static String user;
    protected static String password;
    protected static String token;
    protected static URI devicesUri;
    protected static URI longScalarWUri;
    private Client client;

    protected static String getVersion() {
        return "rc4";
    }

    @BeforeClass
    public static void beforeClass(){
        url = System.getProperty("tango.rest.url");
        Preconditions.checkNotNull(url, "tango.rest.url is not defined! Rerun using `mvn test -Dtango.rest.url={url}`");
        uri = URI.create(url);

        tango_host = System.getProperty("tango.host");
        Preconditions.checkNotNull(tango_host, "tango.host is not defined! Rerun using `mvn test -Dtango.host={TANGO_HOST}`");

        tango_port = System.getProperty("tango.port");
        Preconditions.checkNotNull(tango_port, "tango.port is not defined! Rerun using `mvn test -Dtango.port=10000`");

        auth = System.getProperty("tango.rest.auth.method");
        Preconditions.checkNotNull(auth, "tango.rest.auth.method is not defined! Rerun using `mvn test -Dtango.rest.auth.method={auth}`. Auth = basic|oauth");

        switch (auth){
            case "basic":
                user = System.getProperty("tango.rest.user");
                Preconditions.checkNotNull(user,
                        "tango.rest.user is not defined! Rerun using `mvn test -Dtango.rest.user={user}`");
                password = System.getProperty("tango.rest.password");
                Preconditions.checkNotNull(password,
                        "tango.rest.password is not defined! Rerun using `mvn test -Dtango.rest.password={password}`");
                break;
            case "oauth":
                token = System.getProperty("tango.rest.oauth.token");
                Preconditions.checkNotNull(password,
                        "tango.rest.oauth.token is not defined! Rerun using `mvn test -Dtango.rest.oauth.token={token}`");
                break;
            default:
                throw new IllegalStateException("tango.rest.auth must be either basic or oauth!");
        }

        devicesUri = UriBuilder.fromUri(uri).path(getVersion()).path("hosts").path(tango_host).path(tango_port).path("devices").build();
        longScalarWUri = UriBuilder.fromUri(uri).path(getVersion()).path("hosts").path(tango_host).path(tango_port).path("devices").path(SYS_TG_TEST_1).path("attributes").path("long_scalar_w").build();
    }

    @Before
    public void before(){
        client = ClientHelper.initializeClientWithBasicAuthentication(url, user, password);
    }

    @Test
    public void testVersion(){
        Map<String,String> result = client.target(url).request().get(HashMap.class);

        assertTrue(result.containsKey(getVersion()));
    }

    @Test
    public void testTangoTestIsPresent(){
        List<NamedEntity> result = client.target(devicesUri).request().get(new GenericType<List<NamedEntity>>() {
        });

        assertTrue(Iterables.tryFind(result, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(NamedEntity input) {
                return input.name.equals(SYS_TG_TEST_1);
            }
        }).isPresent());
    }

    @Test
    public void testAttribute(){
        //again if this one does not fail test passes
        Attribute attribute = client.target(longScalarWUri)
                .request().get(Attribute.class);

        assertNotNull(attribute);
        assertEquals("long_scalar_w", attribute.name);
    }

    @Test
    public void testTangoTestInfo() {
        //if it does not fail with deserialization exception response confronts API spec
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(devicesUri).path(SYS_TG_TEST_1);
        URI uri = uriBuilder.build();
        Device result = client.target(uri).request().get(Device.class);

        //just make sure we have all we need for further tests
        assertEquals("sys/tg_test/1", result.name);
        assertEquals(new ResteasyUriBuilder().uri(devicesUri).path(SYS_TG_TEST_1).path("attributes").build().toString(), result.attributes);
        assertEquals(new ResteasyUriBuilder().uri(devicesUri).path(SYS_TG_TEST_1).path("commands").build().toString(), result.commands);
        assertEquals(new ResteasyUriBuilder().uri(devicesUri).path(SYS_TG_TEST_1).path("pipes").build().toString(), result.pipes);
        assertEquals(new ResteasyUriBuilder().uri(devicesUri).path(SYS_TG_TEST_1).path("properties").build().toString(), result.properties);
        assertEquals(new ResteasyUriBuilder().uri(devicesUri).path(SYS_TG_TEST_1).path("state").build().toString(), result.state);

        DeviceInfo info = result.info;
        assertNotNull(info.ior);
        assertFalse(info.is_taco);
        assertTrue(info.exported);
        assertNotNull(info.last_exported);
        assertNotNull(info.last_unexported);
        assertEquals("sys/tg_test/1", info.name);
        assertEquals("unknown", info.classname);
        assertNotNull(info.version);
        assertEquals("TangoTest/test", info.server);
        assertNotNull(info.hostname);
    }

    @Test
    public void testWriteReadAttribute(){
        URI uri = UriBuilder.fromUri(longScalarWUri).path("value").queryParam("v", "123456").build();
        AttributeValue<Integer> result = client.target(uri)
                .request().put(null, new GenericType<AttributeValue<Integer>>() {
                });

        assertEquals(123456, result.value.intValue());
    }

    @Test
    public void testWriteAttributeAsync(){
        URI uri = UriBuilder.fromUri(longScalarWUri).path("value").queryParam("v", 123456).queryParam("async", true).build();
        AttributeValue<Integer> result = client.target(uri)
                .request().put(null, new GenericType<AttributeValue<Integer>>() {
                });

        assertNull(result);
    }

    @Test
    public void testWriteReadSpectrum(){
        URI uri = UriBuilder.fromUri(devicesUri).path(SYS_TG_TEST_1).path("attributes").path("double_spectrum").path("value").queryParam("v", "3.14,2.87,1.44").build();//TODO native array does not work

        AttributeValue<double[]> result = client.target(uri)
                .request().put(null, new GenericType<AttributeValue<double[]>>() {
                });

        assertArrayEquals(new double[]{3.14,2.87,1.44},result.value, 0.0);
    }

    @Test
    public void testCommand(){
        URI uri = UriBuilder.fromUri(devicesUri).path(SYS_TG_TEST_1).path("commands").path("DevString").build();

        //if parsed w/o exception consider test has passed
        Command cmd = client.target(uri)
                .request().get(Command.class);


        assertEquals("OPERATOR", cmd.info.level);
    }

    @Test
    public void testExecuteCommand(){
        URI uri = UriBuilder.fromUri(devicesUri).path(SYS_TG_TEST_1).path("commands").path("DevString").build();

        CommandResult<String> result = client.target(uri)
                .request()
                .put(
                        Entity.entity("Hello World!!!", MediaType.TEXT_PLAIN_TYPE),
                        new GenericType<CommandResult<String>>() {
                });

        assertEquals("Hello World!!!", result.output);
    }

    //TODO properties

    //TODO pipes

    //TODO events

    @Test
    public void testPartiotioning(){
        URI uri = UriBuilder.fromUri(devicesUri).path(SYS_TG_TEST_1).path("attributes").queryParam("range", "5-10").build();

        List<Map<String, Object>> result = client.target(uri).request().get(new GenericType<List<Map<String, Object>>>() {
        });

        assertTrue(result.size() == 6);
        assertTrue(Iterables.tryFind(result, new Predicate<Map<String,Object>>() {
            @Override
            public boolean apply(Map<String,Object> input) {
                return input.containsKey("name") && input.get("name").equals("partial_content");
            }
        }).isPresent());

    }

    @Test
    public void testFiltering(){
        URI uri = UriBuilder.fromUri(longScalarWUri).queryParam("filter", "name").build();

        Attribute result = client.target(uri)
                .request().get(Attribute.class);


        assertNotNull(result);
        assertEquals("long_scalar_w", result.name);
        assertNull(result.value);
        assertNull(result.info);
    }

    @Test
    public void testFilteringInverted(){
        URI uri = UriBuilder.fromUri(longScalarWUri).queryParam("filter", "!name").build();

        Attribute result = client.target(uri)
                .request().get(Attribute.class);

        assertNull(result.name);
        assertNotNull(result.value);
    }

    @Test(expected = InternalServerErrorException.class)//HTTP 500
    public void testNoValue(){
        URI uri = UriBuilder.fromUri(devicesUri).path(SYS_TG_TEST_1).path("attributes").path("no_value").path("value").build();

        AttributeValue<?> result = client.target(uri)
                .request().get(new GenericType<AttributeValue<?>>() {
                });
    }

    @Test
    public void testAttributeInfo(){
        URI uri = UriBuilder.fromUri(longScalarWUri).path("info").build();

        AttributeInfo result = client.target(uri)
                .request().get(AttributeInfo.class);

        assertNotNull(result);
        assertEquals("long_scalar_w", result.name);
        assertEquals("WRITE", result.writable);
        assertEquals("SCALAR", result.data_format);
        assertEquals("OPERATOR", result.level);
    }

    @Test
    public void testAttributeInfoPut(){
        URI uri = UriBuilder.fromUri(longScalarWUri).path("info").build();

        AttributeInfo info = client.target(uri)
                .request().get(AttributeInfo.class);

        info.alarms.max_alarm = "1000";
        info.events.ch_event.rel_change = "100";

        AttributeInfo result = client.target(uri)
                .request().put(Entity.entity(info, MediaType.APPLICATION_JSON_TYPE), AttributeInfo.class);

        assertEquals("1000", result.alarms.max_alarm);
        assertEquals("100", result.events.ch_event.rel_change);
    }
}
