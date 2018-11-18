package org.tango.rest.test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fr.esrf.Tango.ErrSeverity;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tango.rest.ClientHelper;
import org.tango.rest.entities.*;
import org.tango.rest.tree.TangoContainer;

import javax.annotation.Nullable;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 17.12.2015
 */
public class Rc5Test {
    public static final String REST_API_VERSION = "rc5";
    private static Context CONTEXT;
    private Client client;

    @BeforeClass
    public static void beforeClass(){
        CONTEXT = Context.create(REST_API_VERSION);
    }

    @Before
    public void before(){
        client = ClientHelper.initializeClientWithBasicAuthentication(CONTEXT.url, CONTEXT.user, CONTEXT.password);
    }

    @Test
    public void testVersion(){
        Map<String,String> result = client.target(CONTEXT.url).request().get(HashMap.class);

        assertTrue(result.containsKey(REST_API_VERSION));
    }

    @Test
    public void testHost(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("hosts/localhost");
        TangoHost result = client.target(uriBuilder.build()).request().get(TangoHost.class);

        //TODO
        assertFalse(true);
    }

    @Test
    public void testHost_wrongPort(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("hosts/localhost;port=12345");
        Response result = client.target(uriBuilder.build()).request().get();

        org.junit.Assert.assertSame(result.getStatusInfo(), Response.Status.BAD_REQUEST);
    }

    @Test
    public void testDevicesTree(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("devices/tree").queryParam("host","localhost").queryParam("wildcard","sys/tg_test/1");
        List<org.tango.rest.tree.TangoHost> result = client.target(uriBuilder.build()).request().get(new GenericType<List<org.tango.rest.tree.TangoHost>>(){});

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).isAlive);
        assertEquals("aliases",result.get(0).data.get(0).value);
        assertEquals("sys",result.get(0).data.get(1).value);
    }

    @Test
    public void testDevicesTreeForLocalhost(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path("tree");
        List<org.tango.rest.tree.TangoHost> result = client.target(uriBuilder.build()).request().get(new GenericType<List<org.tango.rest.tree.TangoHost>>(){});

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).isAlive);
        assertEquals("aliases",result.get(0).data.get(0).value);
        assertEquals("sys",result.get(0).data.get(1).value);
    }

    //TODO tree -- wrong Tango host e.g. port, host

    @Test
    public void testAttributes(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("attributes").queryParam("wildcard", "localhost:10000/*/*/*/State");
        List<Attribute> result = client.target(uriBuilder.build()).request().get(new GenericType<List<Attribute>>(){});

        Attribute attribute = Iterables.find(result, new Predicate<Attribute>() {
            @Override
            public boolean apply(@Nullable Attribute input) {
                return input.device.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertNotNull(attribute);
    }

    @Test
    public void testAttributeValuesRead(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("attributes/value").queryParam("wildcard", "localhost:10000/*/*/*/State");
        List<AttributeValue> result = client.target(uriBuilder.build()).request().get(new GenericType<List<AttributeValue>>(){});

        AttributeValue attribute = Iterables.find(result, new Predicate<AttributeValue>() {
            @Override
            public boolean apply(@Nullable AttributeValue input) {
                return input.device.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertEquals("RUNNING", attribute.value);
    }

    @Test
    public void testAttributeValuesWrite(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("attributes/value");
        List<AttributeValue> result = client.target(uriBuilder.build()).request().put(
                Entity.entity(Lists.<AttributeValue>newArrayList(
                    new AttributeValue<>("double_scalar_w","localhost:10000","sys/tg_test/1",3.14D,null,0L)
                ),MediaType.APPLICATION_JSON_TYPE),
                new GenericType<List<AttributeValue>>(){});

        AttributeValue attribute = Iterables.find(result, new Predicate<AttributeValue>() {
            @Override
            public boolean apply(@Nullable AttributeValue input) {
                return input.device.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertEquals(3.14, attribute.value);
    }

    @Test
    public void testAttributeValuesWrite_wrongValueType(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("attributes/value");
        List<AttributeValue> result = client.target(uriBuilder.build()).request().put(
                Entity.entity(Lists.<AttributeValue>newArrayList(
                        new AttributeValue<>("double_scalar_w","localhost:10000","sys/tg_test/1","Hello World!",null,0L)
                ),MediaType.APPLICATION_JSON_TYPE),
                new GenericType<List<AttributeValue>>(){});

        AttributeValue attribute = Iterables.find(result, new Predicate<AttributeValue>() {
            @Override
            public boolean apply(@Nullable AttributeValue input) {
                return input.device.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertSame(ErrSeverity.PANIC, attribute.errors[0].severity);
    }


    @Test
    public void testCommands(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("commands").queryParam("wildcard", "localhost:10000/*/*/*/init");
        List<Command> result = client.target(uriBuilder.build()).request().get(new GenericType<List<Command>>(){});

        //TODO
        assertFalse(true);
    }

    @Test
    public void testPipes(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("pipes").queryParam("wildcard", "localhost:10000/*/*/*/*");
        List<Pipe> result = client.target(uriBuilder.build()).request().get(new GenericType<List<Pipe>>(){});

        //TODO
        assertFalse(true);
    }


    @Test
    public void testTangoTestIsPresent(){
        List<NamedEntity> result = client.target(CONTEXT.devicesUri).request().get(new GenericType<List<NamedEntity>>() {
        });

        assertTrue(Iterables.tryFind(result, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(NamedEntity input) {
                return input.name.equals(CONTEXT.SYS_TG_TEST_1);
            }
        }).isPresent());
    }

    @Test
    public void testAttribute(){
        //again if this one does not fail test passes
        Attribute attribute = client.target(CONTEXT.longScalarWUri)
                .request().get(Attribute.class);

        assertNotNull(attribute);
        assertEquals("localhost:10000/sys/tg_test/1/long_scalar_w", attribute.id);
        assertEquals("localhost:10000", attribute.host);
        assertEquals("sys/tg_test/1", attribute.device);
        assertEquals("long_scalar_w", attribute.name);
        assertEquals("long_scalar_w", attribute.info.name);
    }

    @Test
    public void testTangoTestInfo() {
        //if it does not fail with deserialization exception response confronts API spec
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1);
        URI uri = uriBuilder.build();
        Device result = client.target(uri).request().get(Device.class);

        //just make sure we have all we need for further tests
        assertEquals("sys/tg_test/1", result.name);
        assertEquals(new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("attributes").build().toString(), result.attributes);
        assertEquals(new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("commands").build().toString(), result.commands);
        assertEquals(new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("pipes").build().toString(), result.pipes);
        assertEquals(new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("properties").build().toString(), result.properties);
        assertEquals(new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("state").build().toString(), result.state);

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
        URI uri = UriBuilder.fromUri(CONTEXT.longScalarWUri).path("value").queryParam("v", "123456").build();
        AttributeValue<Integer> result = client.target(uri)
                .request().put(null, new GenericType<AttributeValue<Integer>>() {
                });

        assertEquals(123456, result.value.intValue());
    }

    @Test
    public void testAttributeValuePlain(){
        //again if this one does not fail test passes
        int value = client.target(CONTEXT.longScalarWUri).path("value")

                .request().header("Accept", MediaType.TEXT_PLAIN).get(int.class);

        assertEquals(123456, value);
    }

    @Test
    public void testAttributeValueImage(){
        //again if this one does not fail test passes
        String value = client.target(CONTEXT.uShortImageRO).path("value")

                .request().header("Accept", "image/jpeg").get(String.class);

        assertTrue(value.startsWith("data:/jpeg;base64"));
    }

    @Test
    public void testWriteAttributeAsync(){
        URI uri = UriBuilder.fromUri(CONTEXT.longScalarWUri).path("value").queryParam("v", 123456).queryParam("async", true).build();
        AttributeValue<Integer> result = client.target(uri)
                .request().put(null, new GenericType<AttributeValue<Integer>>() {
                });

        assertNull(result);
    }

    @Test
    public void testWriteReadSpectrum(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("attributes").path("double_spectrum").path("value").queryParam("v", "3.14,2.87,1.44").build();//TODO native array does not work

        AttributeValue<double[]> result = client.target(uri)
                .request().put(null, new GenericType<AttributeValue<double[]>>() {
                });

        assertArrayEquals(new double[]{3.14,2.87,1.44},result.value, 0.0);
    }

    @Test
    public void testCommand(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("commands").path("DevString").build();

        //if parsed w/o exception consider test has passed
        Command cmd = client.target(uri)
                .request().get(Command.class);


        assertEquals("OPERATOR", cmd.info.level);
    }

    @Test
    public void testExecuteCommand(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("commands").path("DevString").build();

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
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("attributes").queryParam("range", "5-10").build();

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
        URI uri = UriBuilder.fromUri(CONTEXT.longScalarWUri).queryParam("filter", "name").build();

        Attribute result = client.target(uri)
                .request().get(Attribute.class);


        assertNotNull(result);
        assertEquals("long_scalar_w", result.name);
        assertNull(result.value);
        assertNull(result.info);
    }

    @Test
    public void testFilteringInverted(){
        URI uri = UriBuilder.fromUri(CONTEXT.longScalarWUri).queryParam("filter", "!name").build();

        Attribute result = client.target(uri)
                .request().get(Attribute.class);

        assertNull(result.name);
        assertNotNull(result.value);
    }

    @Test(expected = InternalServerErrorException.class)//HTTP 500
    public void testNoValue(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("attributes").path("no_value").path("value").build();

        AttributeValue<?> result = client.target(uri)
                .request().get(new GenericType<AttributeValue<?>>() {
                });
    }

    @Test
    public void testAttributeInfo(){
        URI uri = UriBuilder.fromUri(CONTEXT.longScalarWUri).path("info").build();

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
        URI uri = UriBuilder.fromUri(CONTEXT.longScalarWUri).path("info").build();

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
