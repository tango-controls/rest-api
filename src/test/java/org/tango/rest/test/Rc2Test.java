package org.tango.rest.test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tango.rest.ClientHelper;
import org.tango.rest.entities.*;
import org.tango.rest.response.Response;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class Rc2Test {
    protected static String url;
    protected static String auth;
    protected static String user;
    protected static String password;
    protected static String token;

    protected String getVersion(){
        return "rc2";
    }

    @BeforeClass
    public static void beforeClass(){
        url = System.getProperty("tango.rest.url");
        if(url == null) throw new IllegalArgumentException("tango.rest.url is not defined! Rerun using `mvn test -Dtango.rest.url={url}`");

        auth = System.getProperty("tango.rest.auth.method");
        if(auth == null) throw new IllegalArgumentException("tango.rest.auth.method is not defined! Rerun using `mvn test -Dtango.rest.auth.method={auth}`. Auth = basic|oauth");

        if(auth.equalsIgnoreCase("oauth")) throw new IllegalArgumentException("oauth authentication method is not yet implemented!");

        switch (auth){
            case "basic":
                user = System.getProperty("tango.rest.user");
                password = System.getProperty("tango.rest.password");
                break;
            case "oauth":
                token = System.getProperty("tango.rest.oauth.token");
                break;
            default:
                throw new IllegalArgumentException("tango.rest.auth must either basic or oauth!");
        }
    }

    private Client client;

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
        List<NamedEntity> result = client.target(url + "/"+getVersion()+"/devices").request().get(new GenericType<List<NamedEntity>>(){});

        assertTrue(Iterables.tryFind(result, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(NamedEntity input) {
                return input.name.equals("sys/tg_test/1");
            }
        }).isPresent());
    }

    @Test
    public void testTangoTestInfo(){
        //if it does not fail with deserialization exception response confronts API spec
        Device result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1").request().get(Device.class);

        //just make sure we have all we need for further tests
        assertTrue(Iterables.tryFind(result.attributes, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(NamedEntity input) {
                return input.name.equals("long_scalar_w");
            }
        }).isPresent());
        assertTrue(Iterables.tryFind(result.attributes, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(NamedEntity input) {
                return input.name.equals("double_spectrum");
            }
        }).isPresent());
        assertTrue(Iterables.tryFind(result.attributes, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(NamedEntity input) {
                return input.name.equals("no_value");
            }
        }).isPresent());
        assertTrue(Iterables.tryFind(result.commands, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(NamedEntity input) {
                return input.name.equals("DevString");
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
        Response<AttributeValue<?>> result = client.target(url + "/"+getVersion()+"/devices/sys/tg_test/1/attributes/no_value/value")
                .request().get(new GenericType<Response<AttributeValue<?>>>(){});

        assertNull(result.argout);
        assertTrue(result.errors.length == 1);
        assertTrue(result.errors[0].description.contains("API_AttrValueNotSet"));
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
