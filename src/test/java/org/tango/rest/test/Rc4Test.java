package org.tango.rest.test;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tango.rest.ClientHelper;
import org.tango.rest.entities.*;

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
    protected static String url;
    protected static URI uri;
    protected static String tango_host;
    protected static String tango_port;
    protected static String auth;
    protected static String user;
    protected static String password;
    protected static String token;
    protected static URI devicesUri;
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
                return input.name.equals("sys/tg_test/1");
            }
        }).isPresent());
    }

    @Test
    public void testAttribute(){
        //again if this one does not fail test passes
        Attribute attribute = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/long_scalar_w")
                .request().get(Attribute.class);

        assertNotNull(attribute);
        assertEquals("long_scalar_w", attribute.name);
    }

    @Test
    public void testTangoTestInfo() {
        //if it does not fail with deserialization exception response confronts API spec
        URI deviceUri = devicesUri.resolve("sys/tg_test/1");
        Device result = client.target(deviceUri).request().get(Device.class);

        //just make sure we have all we need for further tests
        assertEquals("sys/tg_test/1", result.name);
        assertEquals(deviceUri.resolve("attributes").toString(), result.attributes);
        assertEquals(deviceUri.resolve("commands").toString(), result.commands);
        assertEquals(deviceUri.resolve("pipes").toString(), result.pipes);
        assertEquals(deviceUri.resolve("properties").toString(), result.properties);
        assertEquals(deviceUri.resolve("state").toString(), result.state);

        DeviceInfo info = result.info;
        assertNotNull(info.ior);
        assertFalse(info.is_taco);
        assertTrue(info.exported);
        assertNotNull(info.last_exported);
        assertNotNull(info.last_unexported);
        assertEquals("sys/tg_test/1", info.name);
        assertEquals("TangoTest", info.classname);
        assertNotNull(info.version);
        assertNotNull(info.server);
        assertNotNull(info.hostname);
    }

    @Test
    public void testWriteReadAttribute(){
        AttributeValue<Integer> result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/long_scalar_w?value=123456")
                .request().put(null, new GenericType<AttributeValue<Integer>>() {
                });

        assertEquals(123456, result.value.intValue());
    }

    @Test
    public void testWriteAttributeAsync(){
        AttributeValue<Integer> result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/long_scalar_w?value=123456&async=true")
                .request().put(null, new GenericType<AttributeValue<Integer>>() {
                });

        assertNull(result);
    }

    @Test
    public void testWriteReadSpectrum(){
        AttributeValue<double[]> result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/double_spectrum?value=3.14,2.87,1.44")
                .request().put(null, new GenericType<AttributeValue<double[]>>() {
                });

        assertArrayEquals(new double[]{3.14,2.87,1.44},result.value, 0.0);
    }

    @Test
    public void testCommand(){
        //if parsed w/o exception consider test has passed
        Command cmd = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/commands/DevString")
                .request().get(Command.class);


        assertEquals("OPERATOR", cmd.info.level);
    }

    @Test
    public void testExecuteCommand(){
        CommandResult<String,String> result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/commands/DevString?input=Hello World!!!")
                .request()
                .put(null, new GenericType<CommandResult<String, String>>() {
                });

        assertEquals("Hello World!!!", result.output);
    }

    //TODO properties

    //TODO pipes

    //TODO events

    @Test
    public void testPartiotioning(){
        List<Map<String,Object>> result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes?range=5-10").request().get(new GenericType<List<Map<String,Object>>>(){});

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
        Attribute result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/long_scalar_w?filter=name")
                .request().get(Attribute.class);


        assertNotNull(result);
        assertEquals("long_scalar_w", result.name);
        assertNull(result.value);
        assertNull(result.info);
    }

    @Test
    public void testFilteringInverted(){
        Attribute result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/long_scalar_w?filter=!name")
                .request().get(Attribute.class);

        assertNull(result.name);
        assertNotNull(result.value);
    }

    @Test
    public void testNoValue(){
        AttributeValue<?> result = client.target(url + "/" + getVersion() + "/devices/sys/tg_test/1/attributes/no_value/value")
                .request().get(new GenericType<AttributeValue<?>>() {
                });
        //TODO
    }

    @Test
    public void testAttributeInfo(){
        AttributeInfo result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/double_scalar/info")
                .request().get(AttributeInfo.class);

        assertNotNull(result);
        assertEquals("double_scalar", result.name);
        assertEquals("READ_WRITE", result.writable);
        assertEquals("SCALAR", result.data_format);
        assertEquals("OPERATOR", result.level);
    }

    @Test
    public void testAttributeInfoPut(){
        AttributeInfo info = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/double_scalar/info")
                .request().get(AttributeInfo.class);

        info.max_alarm = "1000";

        AttributeInfo result = client.target(url + "/" + getVersion() + "/devices/sys/tg_test/1/attributes/double_scalar/info")
                .request().put(Entity.entity(info, MediaType.APPLICATION_JSON_TYPE), AttributeInfo.class);

        assertEquals("1000", result.max_alarm);
    }
}
