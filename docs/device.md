[TOC]

# Tango Device

RESTful Tango device resource belongs to a particular Tango host. All URLs below must be prefixed with `/hosts/{tango host}[;port={tango port}]` 

URL                                                                     | Response   | Desc
------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
 `GET /devices/{device.name}`                                           | JSONObject | – displays device's data
 `GET /devices/{device.name}/state`                                     | JSONObject | – returns state and status of the device

Examples:

```http request
GET /devices/sys/tg_test/1
```
```JSON
{
  "id": "hzgxenvtest:10000/sys/tg_test/1",
  "name": "sys/tg_test/1",
  "alias": "my_test_device",
  "host": "hzgxenvtest:10000",
  "info": {
    "name": "sys/tg_test/1",
    "ior": "IOR:010000001700000049444c3a54616e676f2f4465766963655f353a312e3000000100000000000000ab000000010102000c000000687a6778656e76746573740025a600000e000000feb9e7d25b000018fb000000001c00000300000000000000080000000100000000545441010000001c00000001000000010001000100000001000105090101000100000009010100025454413f000000010000000c000000687a6778656e767465737400270000002f746d702f6f6d6e692d703037757365722f3030303030363339352d3135343035343835333700",
    "version": "5",
    "exported": true,
    "pid": 6395,
    "server": "TangoTest/test",
    "hostname": "hzgxenvtest.desy.de",
    "classname": "unknown",
    "is_taco": false,
    "last_exported": "26th October 2018 at 12:08:58",
    "last_unexported": "26th October 2018 at 12:08:47"
  },
  "attributes": "http://localhost:10001/tango/rest/rc5/hosts/hzgxenvtest/devices/sys/tg_test/1/attributes",
  "commands": "http://localhost:10001/tango/rest/rc5/hosts/hzgxenvtest/devices/sys/tg_test/1/commands",
  "pipes": "http://localhost:10001/tango/rest/rc5/hosts/hzgxenvtest/devices/sys/tg_test/1/pipes",
  "properties": "http://localhost:10001/tango/rest/rc5/hosts/hzgxenvtest/devices/sys/tg_test/1/properties",
  "state": "http://localhost:10001/tango/rest/rc5/hosts/hzgxenvtest/devices/sys/tg_test/1/state"
}
```

`GET /devices/sys/tg_test/1/state`:
```JSON
{
  "state": "RUNNING",
  "status": "The device is in RUNNING state."
}
```

### Device attributes

URL                                                                                        |  Response          | Desc
----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
 `GET /devices/{device.name}/attributes`                                           | JSONArray  | – displays device's attributes in array
 `GET /devices/{device.name}/attributes/{attribute}`                                          | JSONObject | – displays the attribute

Assuming _sys/tg_test/1_ has 2 attributes: __string_scalar__ and __long_scalar_w__:



```http request
GET /devices/sys/tg_test/1/attributes/long_scalar_w
```
```JSON
{
  "id": "localhost:10000/sys/tg_test/1/long_scalar_w",
  "name":"long_scalar_w",
  "device": "sys/tg_test/1",
  "host": "localhost:10000",  
  "info":{
           "name": "float",
           "writable": "READ",
           "data_format": "SCALAR",
           "data_type": "DevFloat",
           "max_dim_x": 1,
           "max_dim_y": 0,
           "description": "No description",
           "label": "float",
           "unit": "No unit",
           "standard_unit": "No standard unit",
           "display_unit": "No display unit",
           "format": "%6.2f",
           "min_value": "Not specified",
           "max_value": "Not specified",
           "min_alarm": "Not specified",
           "max_alarm": "Not specified",
           "writable_attr_name": "None",
           "level": "OPERATOR",
           "extensions": [],
           "alarms": {
             "min_alarm": "Not specified",
             "max_alarm": "Not specified",
             "min_warning": "Not specified",
             "max_warning": "Not specified",
             "delta_t": "Not specified",
             "delta_val": "Not specified",
             "extensions": []
           },
           "events": {
             "ch_event": {
               "rel_change": "Not specified",
               "abs_change": "Not specified",
               "extensions": []
             },
             "per_event": {
               "period": "100",
               "extensions": [],
               "tangoObj": {
                 "period": "100",
                 "extensions": []
               }
             },
             "arch_event": {
               "rel_change": "Not specified",
               "abs_change": "Not specified",
               "period": "Not specified",
               "extensions": []
             }
           },
           "sys_extensions": [],
           "isMemorized": false,
           "isSetAtInit": true,
           "memorized": "NOT_MEMORIZED",
           "root_attr_name": "Not specified",
           "enum_label": [
             "Not specified"
           ]
         },
  "value":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value",
  "history":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/history",
  "properties":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/properties"
}
```

The following returns an array of objects defined above for all device's attributes:

```http request
GET /devices/sys/tg_test/1/attributes
```

#### value

| URL                                                                                       |    Response        | Desc
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/value`                                    | JSONObject | – returns attribute value. Last-Modified = read timestamp from the Tango
| `PUT /devices/{device.name}/attributes/{attribute}/value?v={value}[&async=true]`               | JSONObject/NULL | – returns value after it is being written, i.e. synchronous write&read; empty response if async=true; argument can be passed in request's body. Last-Modified = write timestamp from the Tango. NULL = HTTP 204
| `PUT /devices/{device.name}/attributes/value?{attr1}={value}&{attr2}={value}[&async=true]`         | JSONArray/NULL  | – updates specified attributes. NULL = HTTP 204
| `GET /devices/{device.name}/attributes/value?attr={attr1}&attr={attr2}`         | JSONArray  | – reads specified attributes.

##### Scalar:

```http request
GET /devices/sys/tg_test/1/attributes/long_scalar/value
```
```JSON
{
  "name": "long_scalar",
  "host": "hzgxenvtest:10000",
  "device": "sys/tg_test/1",
  "value": 104,
  "quality": "ATTR_VALID",
  "timestamp": 1542638523634
}
```

##### Spectrum:

```http request
GET /devices/sys/tg_test/1/attributes/double_spectrum_ro/value
```
```JSON
{
  "name": "double_spectrum_ro",
  "host": "hzgxenvtest:10000",
  "device": "sys/tg_test/1",
  "value": [
    7.0,
    36.0,
    83.0,...],
  "quality": "ATTR_VALID",
  "timestamp": 123456789
}
```

##### Enum:

```http request
GET /devices/sys/tg_test/1/attributes/enum/value
```
```JSON
{
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "name": "enum",
    "value": "Label 1",
    "quality": "ATTR_VALID",
    "timestamp": 123456789
}
```

##### Image:

```http request
GET /devices/sys/tg_test/1/attributes/ushort_image_ro/value
```
```JSON
{
    "name": "ushort_image_ro",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": {
       "data": [
          32,
          111,
          185,207,115,227,137,54,...],
       "width": 251,
       "height": 251
    },
    "quality": "ATTR_VALID",
    "timestamp": 123456789
}
```


#### Read multiple attributes:

```http request
GET /devices/sys/tg_test/1/attributes/value?attr=long_scalar_w&attr=string_scalar
```
```json
[
  {
    "name": "long_scalar_w",
    "value": 123456,
    "quality": "ATTR_VALID",
    "timestamp": 1542639081340
  },
  {
    "name": "string_scalar",
    "value": "Default string",
    "quality": "ATTR_VALID",
    "timestamp": 1542639081340
  }
]
```

#### Write scalar attribute:

```http request
PUT /devices/sys/tg_test/1/attributes/long_scalar_w/value?v=42
```

```JSON
{
  "name": "long_scalar_w",
  "host": "hzgxenvtest:10000",
  "device": "sys/tg_test/1",
  "value": 42,
  "quality": "ATTR_VALID",
  "timestamp": 1542640345978
}
```

#### Write multiple scalar attributes:

```http request
PUT /devices/sys/tg_test/1/attributes/value?long_scalar_w=42&string_scalar=Hi!
```
```JSON
[
  {
    "name": "string_scalar",
    "value": "Hi!",
    "quality": "ATTR_VALID",
    "timestamp": 1542640393428
  },
  {
    "name": "long_scalar_w",
    "value": 42,
    "quality": "ATTR_VALID",
    "timestamp": 1542640393428
  }
]
```

__IMPLEMENTATION NOTE:__ Value related response's Last-Modified is set to timestamp from the remote Tango device.

Depending on the provided HTTP request [Accept header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept) implementation MUST return corresponding data type:

#### plain value

Responses with plain value, i.e. no JSON structure:

```http request
GET /devices/sys/tg_test/1/attributes/long_scalar/value
Accept: text/plain
```

```JSON
12345
```

```http request
GET /devices/sys/tg_test/1/attributes/double_scalar/value
Accept: text/plain
```

```JSON
3.14
```

```http request
GET /devices/sys/tg_test/1/attributes/string_scalar/value
Accept: text/plain
```

```JSON
"Hello World!!!"
```

```
GET /devices/sys/tg_test/1/attributes/double_spectrum/value
Accept: text/plain
```

```JSON
[3.14, 2.87]
```

#### image:

For image attributes image value type returns image embedded into response:

```
GET /devices/sys/tg_test/1/attributes/ushort_image_ro/value
Accept: image/jpeg
```

```
Content-Disposition: inline
 
data:/jpeg;base64,/9j/4AAQSkZJRgABAgAAAQABAAD...AKKKKACiiigAooooA//
```


#### info:

| URL                                                                                       | Response           | Desc
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `PUT /devices/{device.name}/attributes/{attribute}/info[?async=true]`                  | JSONObject/NULL | – updates writable elements of the info

```http request
PUT /devices/sys/tg_test/1/attributes/ushort_image_ro/info
Content-Type: application/json

{"name":"double_image_ro","writable":"READ","data_format":"IMAGE","data_type":"DevDouble","max_dim_x":251,"max_dim_y":251,"description":"No description","label":"double_image_ro","unit":"","standard_unit":"No standard unit","display_unit":"No display unit","format":"%6.2f","min_value":"Not specified","max_value":"Not specified","min_alarm":"Not specified","max_alarm":"Not specified","writable_attr_name":"None","level":"OPERATOR","extensions":[],"alarms":{"min_alarm":"Not specified","max_alarm":"Not specified","min_warning":"Not specified","max_warning":"Not specified","delta_t":"Not specified","delta_val":"Not specified","extensions":[],"tangoObj":{"min_alarm":"Not specified","max_alarm":"Not specified","min_warning":"Not specified","max_warning":"Not specified","delta_t":"Not specified","delta_val":"Not specified","extensions":[]}},"events":{"ch_event":{"rel_change":"Not specified","abs_change":"Not specified","extensions":[],"tangoObj":{"rel_change":"Not specified","abs_change":"Not specified","extensions":[]}},"per_event":{"period":"1000","extensions":[],"tangoObj":{"period":"1000","extensions":[]}},"arch_event":{"rel_change":"Not specified","abs_change":"Not specified","period":"Not specified","extensions":[],"tangoObj":{"rel_change":"Not specified","abs_change":"Not specified","period":"Not specified","extensions":[]}},"tangoObj":{"ch_event":{"rel_change":"Not specified","abs_change":"Not specified","extensions":[]},"per_event":{"period":"1000","extensions":[]},"arch_event":{"rel_change":"Not specified","abs_change":"Not specified","period":"Not specified","extensions":[]}}},"sys_extensions":[],"isMemorized":false,"isSetAtInit":false,"memorized":"NOT_MEMORIZED","root_attr_name":"Not specified","enum_label":[]}
```
```json
{"name":"double_image_ro","writable":"READ","data_format":"IMAGE","data_type":"DevDouble","max_dim_x":251,"max_dim_y":251,"description":"No description","label":"double_image_ro","unit":"","standard_unit":"No standard unit","display_unit":"No display unit","format":"%6.2f","min_value":"Not specified","max_value":"Not specified","min_alarm":"Not specified","max_alarm":"Not specified","writable_attr_name":"None","level":"OPERATOR","extensions":[],"alarms":{"min_alarm":"Not specified","max_alarm":"Not specified","min_warning":"Not specified","max_warning":"Not specified","delta_t":"Not specified","delta_val":"Not specified","extensions":[],"tangoObj":{"min_alarm":"Not specified","max_alarm":"Not specified","min_warning":"Not specified","max_warning":"Not specified","delta_t":"Not specified","delta_val":"Not specified","extensions":[]}},"events":{"ch_event":{"rel_change":"Not specified","abs_change":"Not specified","extensions":[],"tangoObj":{"rel_change":"Not specified","abs_change":"Not specified","extensions":[]}},"per_event":{"period":"1000","extensions":[],"tangoObj":{"period":"1000","extensions":[]}},"arch_event":{"rel_change":"Not specified","abs_change":"Not specified","period":"Not specified","extensions":[],"tangoObj":{"rel_change":"Not specified","abs_change":"Not specified","period":"Not specified","extensions":[]}},"tangoObj":{"ch_event":{"rel_change":"Not specified","abs_change":"Not specified","extensions":[]},"per_event":{"period":"1000","extensions":[]},"arch_event":{"rel_change":"Not specified","abs_change":"Not specified","period":"Not specified","extensions":[]}}},"sys_extensions":[],"isMemorized":false,"isSetAtInit":false,"memorized":"NOT_MEMORIZED","root_attr_name":"Not specified","enum_label":[]}
```


__IMPLEMENTATION NOTE:__ attribute info in REST API returns AttributeInfoEx from Tango API 

#### history:

| URL                                                                                        |  Response          | Desc
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/history`                            | JSONArray  | – displays the attribute's history

```JSON
[
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 105,
    "quality": "ATTR_VALID",
    "timestamp": 1542641956646
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 213,
    "quality": "ATTR_VALID",
    "timestamp": 1542641964665
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 184,
    "quality": "ATTR_VALID",
    "timestamp": 1542641974665
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 154,
    "quality": "ATTR_VALID",
    "timestamp": 1542641984665
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 229,
    "quality": "ATTR_VALID",
    "timestamp": 1542641994666
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 251,
    "quality": "ATTR_VALID",
    "timestamp": 1542642003685
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 89,
    "quality": "ATTR_VALID",
    "timestamp": 1542642013685
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 91,
    "quality": "ATTR_VALID",
    "timestamp": 1542642023686
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 74,
    "quality": "ATTR_VALID",
    "timestamp": 1542642033686
  },
  {
    "name": "long_scalar",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "value": 138,
    "quality": "ATTR_VALID",
    "timestamp": 1542642042605
  }
]
```

**CLIENT NOTE**: Tango polling MUST be configured properly for this feature to work!

#### properties:

| URL                                                                                        | Response           | Desc 
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/properties`                               | JSONArray  | – displays the attribute's properties
| `GET /devices/{device.name}/attributes/{attribute}/properties/{prop}`                        | JSONObject | – returns value of the property
| `PUT /devices/{device.name}/attributes/{attribute}/properties/{prop}?value={val}[&async=true]`            | JSONObject/NULL | – returns attribute value. Last-Modified = read timestamp from the Tango
| `DELETE /devices/{device.name}/attributes/{attribute}/properties/{prop}`                     | NULL  | – deletes attribute's property

```JSON
[
    {"prop1":["value1"]},
    {"prop2":["value2"]}
]
```

### Device commands:

| URL                                                                           | Response           | Desc
|---------------------------------------------------------------------------|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/commands`                                | JSONArray  | – displays all commands of the device
| `GET /devices/{device.name}/commands/{command}`                      | JSONObject | – displays command's data
| `GET /devices/{device.name}/commands/{command}/history`              | JSONArray  | – displays command's history
| `PUT /devices/{device.name}/commands/{command}[?async=true]` | JSONObject/NULL | – executes a command of the device; if not async returns specified JSONObject, i.e. blocks until finished, otherwise – returns immediately with empty response. NULL = HTTP 204

`GET /devices/sys/tg_test/1/commands` -  returns an array of objects defined below*):

`GET /devices/sys/tg_test/1/commands/DevString`:
```JSON
{
  "name":"DevString",
  "device":"sys/tg_test/1",
  "host":"localhost:10000",
  "history":"<prefix>/devices/sys/tg_test/1/commands/DevString/history",
  "info":{
    "level":"OPERATOR",
    "cmd_tag":0,
    "in_type":"DevString",
    "out_type":"DevString",
    "in_type_desc":"-",
    "out_type_desc":"-"
  }
}
```

*) Assuming _sys/tg_test/1_ has 2 commands: __DevString__ and __DevLong__

`PUT /devices/sys/tg_test/1/commands/DevVoid`:
```JSON
{
    "name":"DevVoid"
}
```

`PUT /devices/sys/tg_test/1/commands/DevString`:
```
"Hi!"
```
```JSON
{
    "name":"DevString",
    "output":"Hi!"
}
```

`PUT /devices/sys/tg_test/1/commands/DevVarDoubleStringArr`
```json
{
    "dvalue":[3.14, 2.87],
    "svalue":["Hello", "World", "!!!"]    
}
```
=>
```JSON
{
    "name":"DevVarDoubleStringArr",
    "output":{
       "dvalue":[3.14, 2.87],
       "svalue":["Hello", "World", "!!!"]    
    }
}
```


`GET /devices/sys/tg_test/1/commands/DevString/history`:
```JSON
[
    {
        "name":"DevString",
        "output":"Hi!",
        "timestamp":123456789
    },
    {
        "errors":[
            {       
                "reason":"TangoProxyException",
                "description":"sys/tg_test/1 proxy has throw an exception",
                "severity":"ERR",
                "origin":"DeviceProxy#executeCommand sys/tg_test/1/DevString"
            },
            {       
                "reason":"",
                "description":"",
                "severity":"PANIC",
                "origin":""
            }
        ],   
        "quality": "FAILURE",
        "timestamp": 123456789
    },
    ...
]
```


### Device properties:

When serving async request with no body HTTP 204 must be returned.

| URL                                                                         | Response           | Desc
|-------------------------------------------------------------------------|------------|---------------------------------------
| `GET    /devices/{device.name}/properties`                            | JSONArray | – lists all properties of the device
| `GET    /devices/{device.name}/properties/{property}`                            | JSONObject | – returns property value
| `PUT    /devices/{device.name}/properties/{property}?value={value1}&value={value2}&...[?async=true]` | JSONObject | – writes new value for the property
| `PUT    /devices/{device.name}/properties?{prop1}={value}&{prop2}={value}[?async=true]` | JSONArray | – writes new value for the specified properties, not specified are deleted
| `POST   /devices/{device.name}/properties/{property}?value={value1}&value={value2}&...[?async=true]`| JSONObject | – creates a new property
| `POST   /devices/{device.name}/properties?{prop1}={value}&{prop2}={value}[?async=true]`| JSONArray | – creates a new property
| `DELETE /devices/{device.name}/properties/{property}`                         | NULL | – deletes property. NULL = HTTP 204


`GET /devices/{device.name}/properties`:
```JSON
[
     {
         "name": "myProp", 
         "values": ["myPropValue"]
     }
]
```

`GET /devices/sys/tg_test/1/properties/myProp`:
```JSON
{
    "name": "myProp", 
    "values": ["myPropValue"]
}
```

`PUT /devices/sys/tg_test/1/properties?myProp="Hello"&myProp="World"&myProp="!!!"`:
```JSON
[
  {
    "name": "myProp", 
    "values": ["Hello","World","!!!"]
  },
  ..
]
```

### Device pipes

| URL               |  Response         | Desc
|----------------|-----------|---------------------------------------------------
| `GET /devices/{device.name}/pipes` | JSONArray | - displays device pipes
| `GET /devices/{device.name}/pipes/{pipe}` | JSONObject | - display single device pipe
| `GET /devices/{device.name}/pipes/{pipe}/value` | JSONObject | - read device pipe
| `PUT /devices/{device.name}/pipes/{pipe}/value[?async=true]` | JSONObject|NULL | - write device pipe

`GET /devices/sys/tg_test/1/pipes`:
```json
[
  {
    "name": "string_long_short_ro",
    "href": "<prefix>/devices/sys/tg_test/1/pipes/string_long_short_ro"
  }
]
```

`GET /devices/sys_tg/test/1/pipes/string_long_short_ro`
```json
{
  "id": "localhost:10000/sys/tg_test/1/string_long_short_ro",
  "name": "string_long_short_ro",
  "device": "sys/tg_test/1",
  "host": "localhost:10000",
  "info": {
    "name": "string_long_short_ro",
    "description": "Pipe example",
    "label": "string_long_short_ro",
    "level": "OPERATOR",
    "writeType": "PIPE_READ",
    "writable": false
  },
  "value": "<prefix>/devices/sys/tg_test/1/pipes/string_long_short_ro/value"
}
```

`GET /devices/sys_tg/test/1/pipes/string_long_short_ro/value`:
```JSON
{
  "host": "hzgxenvtest:10000",
  "device": "sys/tg_test/1",
  "name": "string_long_short_ro",
  "timestamp": 1542705769023,
  "data": [
    {
      "name": "FirstDE",
      "value": [
        "The string"
      ]
    },
    {
      "name": "SecondDE",
      "value": [
        666
      ]
    },
    {
      "name": "ThirdDE",
      "value": [
        12
      ]
    }
  ]
}
```

For writing __type__ information is required for each PipeBlobDataElement:

```
PUT /devices/sys_tg/test/1/pipes/string_long_short_ro/value
Content-Type: application/json

[
    {
      "name": "FirstDE",
      "type":"DevString",
      "value": [
        "The string"
      ]
    }, {
      "name": "SecondDE",
      "type":"DevLong",
      "value": [
        666
      ]
    }, {
      "name": "ThirdDE",
      "type":"DevShort",
      "value": [
        12
      ]
    }
]
```