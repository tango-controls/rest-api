package org.tango.rest.test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import fr.esrf.Tango.ErrSeverity;
import fr.esrf.TangoApi.PipeBlob;
import fr.esrf.TangoApi.PipeBlobBuilder;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tango.rest.ClientHelper;
import org.tango.rest.entities.*;
import org.tango.rest.entities.pipe.Pipe;
import org.tango.rest.entities.pipe.PipeValue;

import javax.annotation.Nullable;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
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
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("hosts/localhost;port=10000");
        TangoHost result = client.target(uriBuilder.build()).request().get(TangoHost.class);

        assertEquals("localhost:10000", result.id);
        assertEquals("localhost", result.host);
        assertEquals("10000", result.port);
        assertEquals("sys/database/2", result.name);
    }

    @Test(expected = BadRequestException.class)
    public void testHost_wrongPort(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("hosts/localhost;port=12345");
        TangoHost result = client.target(uriBuilder.build()).request().get(TangoHost.class);

        fail();
    }

    @Test(expected = NotFoundException.class)
    public void testHost_nonExistingHost(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("hosts/XXXX");
        TangoHost result = client.target(uriBuilder.build()).request().get(TangoHost.class);

        fail();
    }

    @Test
    public void testTangoHostDevicesWith(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.devicesUri);
        List<NamedEntity> result = client.target(uriBuilder.build()).request().get(new GenericType<List<NamedEntity>>(){});

        assertFalse(result.isEmpty());
        NamedEntity entity = Iterables.find(result, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(@Nullable NamedEntity input) {
                return input.name.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertNotNull(entity);
    }

    @Test
    public void testTangoHostDevicesWithWildCard(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.devicesUri).queryParam("wildcard","sys/tg_test/1");
        List<NamedEntity> result = client.target(uriBuilder.build()).request().get(new GenericType<List<NamedEntity>>(){});

        assertFalse(result.isEmpty());
        NamedEntity entity = Iterables.find(result, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(@Nullable NamedEntity input) {
                return input.name.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertNotNull(entity);
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

    @Test(expected = NotFoundException.class)
    public void testTangoDeviceNotDefinedInDb(){
        Device result = client.target(UriBuilder.fromUri(CONTEXT.devicesUri).path("X/Y/Z").build()).request().get(Device.class);

        fail();
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

        Command command = Iterables.find(result, new Predicate<Command>() {
            @Override
            public boolean apply(@Nullable Command input) {
                return input.device.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertNotNull(command);
        assertEquals("localhost:10000/sys/tg_test/1/Init", command.id);
        assertEquals("Init", command.name);
        assertEquals("sys/tg_test/1", command.device);
        assertEquals("localhost:10000", command.host);
        assertEquals("OPERATOR", command.info.level);
    }

    @Test
    public void testCommands_execute(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("commands");
        List<CommandInOut<?,?>> result = client.target(uriBuilder.build()).request().put(
                Entity.entity(Lists.<CommandInOut<?,?>>newArrayList(
                        new CommandInOut<Double,Double>("localhost:10000","sys/tg_test/1","DevDouble",3.14D),
                        new CommandInOut<String,String>("localhost:10000","sys/tg_test/1","DevString","Hello World!")
                ),MediaType.APPLICATION_JSON_TYPE)
                ,new GenericType<List<CommandInOut<?,?>>>(){});

        CommandInOut<?, ?> command = Iterables.find(result, new Predicate<CommandInOut<?, ?>>() {
            @Override
            public boolean apply(@Nullable CommandInOut<?, ?> input) {
                return input.name.equalsIgnoreCase("DevString");
            }
        });

        assertNotNull(command);
        assertEquals("Hello World!", command.output);

        command = Iterables.find(result, new Predicate<CommandInOut<?, ?>>() {
            @Override
            public boolean apply(@Nullable CommandInOut<?, ?> input) {
                return input.name.equalsIgnoreCase("DevDouble");
            }
        });

        assertNotNull(command);
        assertEquals(3.14D, command.output);
    }

    @Test
    public void testPipes(){
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.uri).path("pipes").queryParam("wildcard", "localhost:10000/*/*/*/string_long_short_ro");
        List<Pipe> result = client.target(uriBuilder.build()).request().get(new GenericType<List<Pipe>>(){});

        Pipe pipe = Iterables.find(result, new Predicate<Pipe>() {
            @Override
            public boolean apply(@Nullable Pipe input) {
                return input.device.equalsIgnoreCase("sys/tg_test/1");
            }
        });

        assertNotNull(pipe);
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

    @Test(expected = NotFoundException.class)
    public void testAttribute_notFound(){
        //again if this one does not fail test passes
        Attribute attribute = client.target(UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1)).path("attributes/XXXX")
                .request().get(Attribute.class);

        fail();
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
    public void testTangoTestAttributes() {
        //if it does not fail with deserialization exception response confronts API spec
        UriBuilder uriBuilder = new ResteasyUriBuilder().uri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("attributes");
        URI uri = uriBuilder.build();
        List<Attribute> result = client.target(uri).request().get(new GenericType<List<Attribute>>(){});

        Attribute attribute = Iterables.find(result, new Predicate<Attribute>() {
            @Override
            public boolean apply(@Nullable Attribute input) {
                return input.name.equalsIgnoreCase("double_scalar");
            }
        });

        assertNotNull(attribute);
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

    @Test(expected = javax.ws.rs.BadRequestException.class)
    public void testAttributeValueImage_nonImageAttribute(){
        //again if this one does not fail test passes
        String value = client.target(CONTEXT.longScalarWUri).path("value")

                .request().header("Accept", "image/jpeg").get(String.class);

        fail();
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

    @Test(expected = NotFoundException.class)
    public void testWriteAttribute_doesNotExists(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("attributes").path("XXX").path("value").queryParam("v", "3.14,2.87,1.44").build();//TODO native array does not work

        AttributeValue<double[]> result = client.target(uri)
                .request().put(null, new GenericType<AttributeValue<double[]>>() {
                });

        fail();
    }

    @Test
    public void testCommand(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("commands").path("DevString").build();

        //if parsed w/o exception consider test has passed
        Command cmd = client.target(uri)
                .request().get(Command.class);


        assertEquals("localhost:10000/sys/tg_test/1/DevString", cmd.id);
        assertEquals("DevString", cmd.name);
        assertEquals("sys/tg_test/1", cmd.device);
        assertEquals("localhost:10000", cmd.host);
        assertEquals("OPERATOR", cmd.info.level);
    }

    @Test(expected = NotFoundException.class)
    public void testCommand_NotFound(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("commands").path("XXXX").build();

        //if parsed w/o exception consider test has passed
        Command cmd = client.target(uri)
                .request().get(Command.class);


        fail();
    }

    @Test
    public void testExecuteCommand(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("commands").path("DevString").build();

        CommandInOut<String, String> input = new CommandInOut<>();
        input.input = "Hello World!!!";

        CommandInOut<String,String> result = client.target(uri)
                .request()
//                .header("Accept", MediaType.APPLICATION_JSON)
                .put(
                        Entity.entity(input, MediaType.APPLICATION_JSON_TYPE),
                        new GenericType<CommandInOut<String, String>>() {
                });

        assertEquals("Hello World!!!", result.output);
    }

//    @Test
    public void testExecuteCommand_AcceptPlain(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("commands").path("DevString").build();

        CommandInOut<String, String> input = new CommandInOut<>();
        input.input = "Hello World!!!";

        String result = client.target(uri)
                .request()
                .header("Accept", MediaType.TEXT_PLAIN)
                .put(
                        Entity.entity(input, MediaType.APPLICATION_JSON_TYPE),
                        String.class);

        assertEquals("Hello World!!!", result);
    }

    //TODO properties

    @Test
    public void testDevicePipes(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("pipes").build();

        List<NamedEntity> result = client.target(uri)
                .request()
//                .header("Accept", MediaType.APPLICATION_JSON)
                .get(
                        new GenericType<List<NamedEntity>>() {
                        });

        NamedEntity pipe = Iterables.find(result, new Predicate<NamedEntity>() {
            @Override
            public boolean apply(@Nullable NamedEntity input) {
                return input.name.equalsIgnoreCase("string_long_short_ro");
            }
        });

        assertNotNull(pipe);
    }

    @Test
    public void testDevicePipe(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("pipes/string_long_short_ro").build();

        Pipe result = client.target(uri)
                .request()
//                .header("Accept", MediaType.APPLICATION_JSON)
                .get(Pipe.class);

        assertNotNull(result);
    }

    @Test
    public void testDevicePipeValueRead(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("pipes/string_long_short_ro/value").build();

        PipeValue result = client.target(uri)
                .request()
//                .header("Accept", MediaType.APPLICATION_JSON)
                .get(PipeValue.class);

        assertNotNull(result);
        assertEquals(CONTEXT.tango_host + ":" + CONTEXT.tango_port, result.host);
        assertEquals("sys/tg_test/1", result.device);
        assertEquals("string_long_short_ro", result.name);
        assertNotNull(result.data);
        assertEquals("FirstDE", result.data.get(0).name);
        assertArrayEquals(new String[]{"The string"}, result.data.get(0).value.toArray());
        assertEquals("SecondDE", result.data.get(1).name);
        assertArrayEquals(new int[]{666}, Ints.toArray((List<Integer>)result.data.get(1).value));
        assertEquals("ThirdDE", result.data.get(2).name);
        assertArrayEquals(new int[]{12}, Ints.toArray((List<Integer>)result.data.get(2).value));
    }

    //TODO writable Pipe in TangoTest
    @Test(expected = BadRequestException.class)
    public void testDevicePipeValueWrite(){
        URI uri = UriBuilder.fromUri(CONTEXT.devicesUri).path(CONTEXT.SYS_TG_TEST_1).path("pipes/string_long_short_ro/value").build();

        PipeValue result = client.target(uri)
                .request()
//                .header("Accept", MediaType.APPLICATION_JSON)
                .put(Entity.entity(
                        new PipeBlobBuilder("blob1").add("FirstDE", "Hello World!").build()
                        ,MediaType.APPLICATION_JSON),PipeValue.class);

        assertNotNull(result);
    }


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
        assertNotNull(result.info);
        assertEquals("long_scalar_w", result.info.name);
        assertNull(result.info.label);
    }

    @Test
    public void testFilteringInverted(){
        URI uri = UriBuilder.fromUri(CONTEXT.longScalarWUri).queryParam("filter", "!name").build();

        Attribute result = client.target(uri)
                .request().get(Attribute.class);

        assertNull(result.name);
        assertNotNull(result.value);
    }

    @Test(expected = BadRequestException.class)
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
