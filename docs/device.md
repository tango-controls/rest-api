[TOC]

## Device

URL                                                                     | Response   | Desc
------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
 `GET /devices/{device.name}`                                           | JSONObject | – displays device's data
 `GET /devices/{device.name}/state`                                     | JSONObject | – returns state and status of the device

Examples:

`GET /devices/sys/tg_test/1`:
```JSON
{
    "name":"sys/tg_test/1",
    "alias": "test_device",
    "info":{
        "last_exported":"7th July 2014 at 11:47:47",
        "last_unexported":"?",
        "name":"sys/tg_test/1",
        "ior":"IOR:0100000017...9010100",
        "version":"4",
        "exported":true,
        "pid":7036,
        "server":"TangoTest/test",
        "hostname":"hzgc103k.desy.de",
        "classname":"unknown",
        "is_taco":false
    },
    "state":"<prefix>/devices/sys/tg_test/1/state",
    "attributes":"<prefix>/devices/sys/tg_test/1/attributes",
    "commands":"<prefix>/devices/sys/tg_test/1/commands",
    "pipes":"<prefix>/devices/sys/tg_test/1/pipes",
    "properties":"<prefix>/devices/sys/tg_test/1/properties",
    "_links":{
            "_parent":"<prefix>/devices"
            "_self":"<prefix>/devices/sys/tg_test/1"
        }
}
```

`GET /devices/sys/tg_test/1/state`:
```JSON
{
    "state":"ON",
    "status":"Device is in ON state.",
    "_links":{
        "_state":"<prefix>/devices/sys/tg_test/1/attributes/State",
        "_status":"<prefix>/devices/sys/tg_test/1/attributes/Status",
        "_parent":"<prefix>/devices/sys/tg_test/1",
        "_self":"<prefix>/devices/sys/tg_test/1/state"
    }
}
```

### Device attributes

URL                                                                                        |  Response          | Desc
----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
 `GET /devices/{device.name}/attributes`                                           | JSONArray  | – displays device's attributes in array
 `GET /devices/{device.name}/attributes/{attribute}`                                          | JSONObject | – displays the attribute

Assuming _sys/tg_test/1_ has 2 attributes: __string_scalar__ and __long_scalar_w__:

`GET /devices/sys/tg_test/1/attributes` - returns an array of objects defined below:

`GET /devices/sys/tg_test/1/attributes/long_scalar_w`:
```JSON
{
  "name":"long_scalar_w",
  "value":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value",
  "info":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/info",
  "history":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/history",
  "properties":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/properties",
  "_links":{
    "_device":"<prefix>/devices/sys/tg_test/1",
    "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
    "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/info"
  }
}
```


#### value

| URL                                                                                       |    Response        | Desc
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/value`                                    | JSONObject | – returns attribute value. Last-Modified = read timestamp from the Tango
| `PUT /devices/{device.name}/attributes/{attribute}/value?v={value}[&async=true]`               | JSONObject/NULL | – returns value after it is being written, i.e. synchronous write&read; empty response if async=true; argument can be passed in request's body. Last-Modified = write timestamp from the Tango. NULL = HTTP 204
| `PUT /devices/{device.name}/attributes/value?{attr1}={value}&{attr2}={value}[&async=true]`         | JSONArray/NULL  | – updates specified attributes. NULL = HTTP 204
| `GET /devices/{device.name}/attributes/value?attr={attr1}&attr={attr2}`         | JSONArray  | – reads specified attributes.

##### Scalar:

`GET /devices/sys/tg_test/1/attributes/long_scalar_w/value`:
```JSON
{
    "name": "long_scalar_w",
    "value": 12345,
    "quality": "ATTR_VALID",
    "timestamp": 123456789
}
```

##### Spectrum:

`GET /devices/sys/tg_test/1/attributes/double_spectrum_ro/value`:
```JSON
{
    "name": "double_spectrum_ro",
    "value": [213,228,207,115,227,137,54,...],
    "quality": "ATTR_VALID",
    "timestamp": 123456789
}
```

##### Enum:

`GET /devices/sys/tg_test/1/attributes/enum/value`:
```JSON
{
    "name": "enum",
    "value": "Label 1",
    "quality": "ATTR_VALID",
    "timestamp": 123456789
}
```

##### Image:

`GET /devices/sys/tg_test/1/attributes/ushort_image_ro/value`:
```JSON
{
    "name": "ushort_image_ro",
    "value": {
       "data":[213,228,207,115,227,137,54,...],
       "width": 251,
       "height": 251
    },
    "quality": "ATTR_VALID",
    "timestamp": 123456789
}
```


#### Read multiple attributes:

`GET /devices/sys/tg_test/1/attributes/value?attr=long_scalar_w&attr=string_scalar`:
```json
[
    {
        "name": "long_scalar_w",
        "value": 12345,
        "quality": "ATTR_VALID",
        "timestamp": 123456789
    },
    {
        "name": "string_scalar",
        "value": "Hello World!!!",
        "quality": "ATTR_VALID",
        "timestamp": 123456789
    }
]
```

#### Write scalar attribute:

`PUT /devices/sys/tg_test/1/attributes/long_scalar_w/value?v=42`:

```JSON
{
    "name": "long_scalar_w",
    "value": 42,
    "quality": "ATTR_VALID",
    "timestamp": 123456789
}
```

#### Write multiple scalar attributes:

`PUT /devices/sys/tg_test/1/attributes/value?long_scalar_w=42&string_scalar=Hi!`:
```JSON
[
    {
        "name": "long_scalar_w",
        "value": 42,
        "quality": "ATTR_VALID",
        "timestamp": 123456789
    },
    {
        "name": "string_scalar",
        "value": "Hi!",
        "quality": "ATTR_VALID",
        "timestamp": 123456789
    }
]
```

__IMPLEMENTATION NOTE:__ Value related response's Last-Modified is set to timestamp from the remote Tango device.

#### plain value

Responses with plain value, i.e. no JSON structure:

`GET /devices/sys/tg_test/1/attributes/long_scalar/value/plain`:
```JSON
12345
```

`GET /devices/sys/tg_test/1/attributes/double_scalar/value/plain`:
```JSON
3.14
```

`GET /devices/sys/tg_test/1/attributes/string_scalar/value/plain`:
```JSON
"Hello World!!!"
```

`GET /devices/sys/tg_test/1/attributes/double_spectrum/value/plain`:
```JSON
[3.14, 2.87]
```

#### image:

For image attributes image value type returns image embedded into response:

`GET /devices/sys/tg_test/1/attributes/image-attr/value/image`:
```JSON
"data:/jpeg;base64,/9j/4AAQSkZJRgABAgAAAQABAAD...AKKKKACiiigAooooA//"
```


#### info:

| URL                                                                                       | Response           | Desc
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/info?attr={attr1}&attr={attr2}`                 | JSONArray   | – displays attribute infos
| `GET /devices/{device.name}/attributes/{attribute}/info`                               | JSONObject  | – displays the attribute's info
| `PUT /devices/{device.name}/attributes/{attribute}/info[?async=true]`                  | JSONObject/NULL | – updates writable elements of the info

`GET /devices/{device.name}/attributes/{attribute}/info`:

```JSON
{
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
}
```

__IMPLEMENTATION NOTE:__ attribute info in REST API returns AttributeInfoEx from Tango API 

#### history:

| URL                                                                                        |  Response          | Desc
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/history`                            | JSONArray  | – displays the attribute's history

```JSON
[
    {
        "name": "string_scalar"
        "value": "Hi!",
        "quality": "ATTR_VALID",
        "timestamp": 123456789
    },
    {
        "errors":[
            {       
                "reason":"TangoProxyException",
                "description":"sys/tg_test/1 proxy has throw an exception",
                "severity":"ERR",
                "origin":"DeviceProxy#readAttribute sys/tg_test/1/throwException"
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


### Device attributes events:

| URL                                                                                        | Response           | Desc
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/change[/periodic][/user][?timeout=30000][?last=12345678]` | JSONObject:same as read | – subscribes to the specified event. Blocks till gets a notification from the device or timeout exceeds

**IMPLEMENTATION REMARK:** In case client has specified a _last_ parameter in the request, the implementation replies with the first event that occurred after this timestamp. This means that the implementation must keep a buffer of events. 

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
  "history":"<prefix>/devices/sys/tg_test/1/commands/DevString/history",
  "info":{
    "level":"OPERATOR",
    "cmd_tag":0,
    "in_type":"DevString",
    "out_type":"DevString",
    "in_type_desc":"-",
    "out_type_desc":"-"
  },
  "_links":{
              "_parent":"<prefix>/devices/sys/tg_test/1",
              "_self":"<prefix>/devices/sys/tg_test/1/commands/DevString"
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
             },
    "_links":{
            "_parent":"<prefix>/devices/sys/tg_test/1",
            "_self":"<prefix>/devices/sys/tg_test/1/commands/DevString"
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
| `GET /devices/{device.name}/pipes/{pipe}` | JSONObject | - read device pipe
| `PUT /devices/{device.name}/pipes/{pipe}[?async=true]` | JSONObject|NULL | - write device pipe

`GET /devices/{device.name}/pipes`:
```json
[
    {   
        "name": "DevPipe",
        "info":{
            "description" : "",
            "label": "",
            "DispLevel": "level",
            "PipeWriteType": "writeType",
            "extensions":[]
        },
        "href": "<prefix>/devices/{device.name}/pipes/DevPipe"
    }
]
```

`GET /devices/sys_tg/test/1/pipes/DevPipe`:
```JSON
{
    "name":"DevPipeBlob",
    "size":12,
    "timestamp":123456789,    
    "data":[
            {
                "name":"DevPipeBlobValue1",                
                "value":["Hello Tango!"]
            },
            {
                "name":"DevPipeBlobValue2",
                "value":[123]
            },
            {
                "name":"DevPipeBlobValue3",
                "value":[{
                        "name":"DevPipeBlobValueInnerBlob",
                        "data":[...]
                    }]
            }
        ]
}
```

For writing type information is required for each PipeBlobDataElement:

`PUT /devices/sys_tg/test/1/pipes/DevPipe`
```json
{
    "name":"DevPipeBlob",
    "data":[
            {
                "name":"DevPipeBlobValue1",                
                "type": "DevString",
                "value":["Hello Tango!"]
            },
            {
                "name":"DevPipeBlobValue2",
                "type":"DevUShort",
                "value":[123]
            },
            {
                "name":"DevPipeBlobValue3",
                "type":"DevPipeBlob",
                "value":[{
                        "name":"DevPipeBlobValueInnerBlob",
                        "data":[...]
                    }]
            }
        ]
}
```