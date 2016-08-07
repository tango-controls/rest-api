[TOC]

# Tango REST API RC3

__NOTE__: this is spec of version RC3 for RC2 clone this wiki and update to revision rc1: `hg clone ... & hg up -r rc2`

There are three parts in this proposal: URL specification; Implementation remarks; Implementation recommendations. The first one names valid URLs that must be handled by the implementation. 
Each URL is presented following this format:

 URL  | RESPONSE TYPE  | NOTE
----- | ---------- | ------
`METHOD url`      |  JSONArray/JSONObject/NULL | short comment          |

Such table is followed by a JSON response's examples block:

`URL`:
`{'JSON response':'example'}`

In two following sections several implementation guidelines are highlighted.

In general API follows standard CRUD idiom:

HTTP verb | CRUD| collection | instance
----------|-----|------------|----------
GET  | READ | Read a list. 200 OK | Read the details of one instance. 200 OK
POST | CREATE | Create a new instance. 201 OK | -
PUT | UPDATE/CREATE | Full Update. 200 OK | Create an instance. 201 Created
DELETE | DELETE | - | Delete instance. 200 OK

POST create an instance of collection by the URI of this collection.
POST returns the URI and the id of the newly created instance.

# URL example driven specification:

All URLs in this section omit protocol//host:port part: `http://host:port`. An implementation may or may not add this to the hrefs. 

For shortness all URLs use `<prefix>` for an API entry point: `/tango/rest/rc3/hosts/tango_host/tango_port`, or omit it completely. So `<prefix>/devices/sys/tg_test/1/attributes` (or `/devices/sys/tg_test/1/attributes`) actually means `/tango/rest/rc3/tango_host/tango_port/devices/sys/tg_test/1/attributes`, where _tango_host_ is a Tango host name, e.g. _hzgxenvtest_; _tango_port_ is a Tango database port number, e.g. _10000_.

_tango_host_ and _tango_port_ are not known in advance, as user may ask for an arbitrary Tango database. By default implementation tries to connect to TANGO_HOST=localhost:10000, i.e. to the database deployed on the same with implementation host. _localhost_ can be replaced with the host name, e.g. _hzgxenvtest_. The database to which implementation connects at start can be specified via environmental variable, or any other way. 

## API version and Security

_api_version_ follows URL prefix and defines which version of this API supports current implementation.

Example:


`http://hzgcttest:8080/tango/rest` =>
```
#!JSON
{
    "rc3":"http://hzgcttest:8080/tango/rest/rc3",
    "mtango-1.0.1":"http://hzgcttest:8080/tango/rest/mtango-1.0.1",
    "mtango-1.0.2":"http://hzgcttest:8080/tango/rest/mtango-1.0.2"
}
```

`GET /tango/rest/non_existing_version` => `HTTP 404`

All resources under _api_version_ must be protected and require an authentication (specification allows non-protected resources but this is strictly not recommended).

API implementation must support 2 authentication methods:

* Basic Web authentication
* OAuth2

When protected by Basic:

`http://hzgcttest:8080/tango/rest/rc3` =>
```
#!JSON
{
    "hosts":"/tango/rest/rc3/hosts",
    "x-auth-method":"basic"
}
```

_x-auth-method_ = basic|oauth2|none

In case of _basic_ any unauthorized request to any protected resource under _rest/api_version_ must get _HTTP 401 Not Authorized_ response
and follow the standard Web Basic Authorization mechanism.

In case of _oauth2_ response must provide OAuth2 authorisation resource as well:
```
#!JSON
{
    "hosts":"/tango/rest/rc3/hosts",
    "x-auth-method":"oauth2",
    "x-auth-resource":"https://hzgcttest:8080/hzgcttest/oauth2/authorize"
}
```

Client uses _x-auth-resource_ to get access_token following the standard OAuth2 authentication procedure. This token is then provided
with each request to the protected resources either in Authentication request header:`Authorization: token access_token` or as a parameter:
`GET /tango/rest/rc3/hosts?token={access_token}`.

_x-auth-method_ = _none_ is not recommended but allowed.

__IMPLEMENTATION NOTE:__ consider integration with TangoAccessControl so that each request is validated against it.

## Tango host:port (database)

|                                         |            |
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc3/hosts`     | JSONArray  | – tango hosts available through this API 
|`GET /tango/rest/rc3/hosts/{tango_host}/{tango_port}`  |   JSONObject   |  -- corresponding Tango database info  

_tango_host_ and _tango_port_ are not known in advance, as user may ask for an arbitrary Tango database. By default implementation tries to connect to TANGO_HOST=localhost:10000, i.e. to the database deployed on the same host. _localhost_ can be replaced with host name, e.g. _hzgxenvtest_. 

```
#!json

{
    "host": "hzgxenvtest",
    "port": 10000,
    "name": "sys/DatabaseDs/2",
    "info": [
            "TANGO Database sys/database/2",
            "",	  
            "Running since 2016-06-30 13:21:32",
            "",	  
            "Devices defined  = 58",
            "Devices exported  = 18",
            "Device servers defined  = 26",
            "Device servers exported  = 9",
            "",	  
            "Device properties defined  = 40 [History lgth = 247]",
            "Class properties defined  = 76 [History lgth = 144]",
            "Device attribute properties defined  = 327 [History lgth = 490]",
            "Class attribute properties defined  = 0 [History lgth = 0]",
            "Object properties defined  = 0 [History lgth = 0]"
        ],
    "devices" : "<prefix>/devices"
}

```

__IMPLEMENTATION NOTE:__ the database to which implementation connects at start may be configured.

__IMPLEMENTATION NOTE:__ this response's info is the same as output of the tango_host:tango_port/sys/DatabaseDs/2/DbInfo command via standard Tango API

## Devices:

|                                         |            |
|-----------------------------------------|------------|--------------------------
|`GET /devices[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API

`GET /devices`:

__OR__

`GET /devices?wildcard=sys*/*/1`:
```
#!JSON
[
    {
        "name":"sys/tg_test/1",
        "href":"<prefix>/devices/sys/tg_test/1"
    },
    {
        "name":"sys/tg_test/2",
        "href":"<prefix>/devices/sys/tg_test/2"
    }
    ...
]
```

__IMPLEMENTATION NOTE:__ this response is the same as when execute command: sys/databaseds/2/DbGetDeviceWideList(wildcard) via standard Tango API

## Device:

|                                                                        |            |
|------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}`                                           | JSONObject | – displays device's data
| `GET /devices/{device.name}/state`                                     | JSONObject | – returns state and status of the device

Examples:

`GET /devices/sys/tg_test/1`:
```
#!JSON
{
    "name":"sys/tg_test/1",
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
```
#!JSON
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

### Device attributes:

|                                                                                        |            |
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes`                                           | JSONArray  | – displays device's attributes in array
| `GET /devices/{device.name}/attributes/{attribute}`                                          | JSONObject | – displays the attribute

Assuming _sys/tg_test/1_ has 2 attributes: __string_scalar__ and __long_scalar_w__:

`GET /devices/sys/tg_test/1/attributes` - returns an array of objects defined below:

`GET /devices/sys/tg_test/1/attributes/long_scalar_w`:
```
#!JSON
{
  "name":"long_scalar_w",
  "value":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value",
  "info":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/info",
  "info_ex":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/info_ex",
  "history":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/history",
  "properties":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/properties",
  "_links":{
    "_device":"<prefix>/devices/sys/tg_test/1",
    "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
    "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/info"
  }
}
```


#### value:

|                                                                                        |            |
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/value`                                    | JSONObject | – returns attribute value. Last-Modified = read timestamp from the Tango
| `PUT /devices/{device.name}/attributes/{attribute}/value?v={value}[&async=true]`               | JSONObject/NULL | – returns value after it is being written, i.e. synchronous write&read; empty response if async=true; argument can be passed in request's body. Last-Modified = write timestamp from the Tango. NULL = HTTP 204
| `PUT /devices/{device.name}/attributes?{attr1}={value}&{attr2}={value}[&async=true]`         | JSONArray/NULL  | – updates specified attributes. Last-Modified = write timestamp from the Tango. NULL = HTTP 204
| `GET /devices/{device.name}/attributes/value?attr={attr1}&attr={attr2}`         | JSONArray  | – reads specified attributes.


`GET /devices/sys/tg_test/1/attributes/long_scalar_w/value`:
```
#!JSON
{
    "name": "long_scalar_w"
    "value": 12345,
    "quality": "VALID",
    "timestamp": 123456789,
    "_links":{
        "_device":"<prefix>/devices/sys/tg_test/1"
        "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
        "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value"
    }
}
```

`GET /devices/sys/tg_test/1/attributes/value?attr=long_scalar_w&attr=string_scalar`:
```
#!json
[
    {
        "name": "long_scalar_w",
        "value": 12345,
        "quality": "VALID",
        "timestamp": 123456789,
        "_links":{
            "_device":"<prefix>/devices/sys/tg_test/1"
            "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
            "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value"
        }
    },
    {
        "name": "string_scalar",
        "value": "Hello World!!!",
        "quality": "VALID",
        "timestamp": 123456789,
        "_links":{
            "_device":"<prefix>/devices/sys/tg_test/1"
            "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
            "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value"
        }
    }
]
```



`PUT /devices/sys/tg_test/1/attributes/long_scalar_w/value?v=42`:

```
#!JSON
{
    "name": "long_scalar_w"
    "value": 42,
    "quality": "VALID",
    "timestamp": 123456789,
    "_links":{
        "_device":"<prefix>/devices/sys/tg_test/1"
        "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
        "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value"
    }
}
```

`PUT /devices/sys/tg_test/1/attributes?long_scalar_w=42&string_scalar=Hi!`:
```
#!JSON
[
    {
        "name": "long_scalar_w"
        "value": 42,
        "quality": "VALID",
        "timestamp": 123456789,
        "_links":{
            "_device":"<prefix>/devices/sys/tg_test/1"
            "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
            "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value"
        }
    },
    {
        "name": "string_scalar"
        "value": "Hi!",
        "quality": "VALID",
        "timestamp": 123456789,
        "_links":{
            "_device":"<prefix>/devices/sys/tg_test/1"
            "_parent":"<prefix>/devices/sys/tg_test/1/attributes/string_scalar",
            "_self":"<prefix>/devices/sys/tg_test/1/attributes/string_scalar/value"
        }
    }
]
```

__IMPLEMENTATION NOTE:__ Value related response's Last-Modified is set to timestamp from the remote Tango device.



#### info:

|                                                                                        |            |
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/info`                               | JSONObject  | – displays the attribute's info
| `PUT /devices/{device.name}/attributes/{attribute}/info[?async=true]`                  | JSONObject/NULL | – updates writable elements of the info

`GET /devices/{device.name}/attributes/{attribute}/info`:

```
#!JSON
{
  "name": "float",
  "writable": "READ",
  "data_format": "SCALAR",
  "data_type": 4,
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

|                                                                                        |            |
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/history`                            | JSONArray  | – displays the attribute's history

```
#!JSON
[
    {
        "name": "string_scalar"
        "value": "Hi!",
        "quality": "VALID",
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

|                                                                                        |            |
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/properties`                               | JSONArray  | – displays the attribute's properties
| `GET /devices/{device.name}/attributes/{attribute}/properties/{prop}`                        | JSONObject | – returns value of the property
| `PUT /devices/{device.name}/attributes/{attribute}/properties/{prop}?value={val}[&async=true]`            | JSONObject/NULL | – returns attribute value. Last-Modified = read timestamp from the Tango
| `DELETE /devices/{device.name}/attributes/{attribute}/properties/{prop}`                     | NULL  | – deletes attribute's property

```
#!JSON
[
    {"prop1":["value1"]},
    {"prop2":["value2"]}
]
```


### Device attributes events:

|                                                                                        |            |
|----------------------------------------------------------------------------------------|------------|---------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/attributes/{attribute}/change[/periodic][/user][?timeout=30000]` | JSONObject:same as read | – subscribes to the specified event. Blocks till gets a notification from the device or timeout exceeds


### Device commands:

|                                                                           |            |
|---------------------------------------------------------------------------|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------
| `GET /devices/{device.name}/commands`                                | JSONArray  | – displays all commands of the device
| `GET /devices/{device.name}/commands/{command}`                      | JSONObject | – displays command's data
| `GET /devices/{device.name}/commands/{command}/history`              | JSONArray  | – displays command's history
| `PUT /devices/{device.name}/commands/{command}[?async=true]` | JSONObject/NULL | – executes a command of the device; if not async returns specified JSONObject, i.e. blocks until finished, otherwise – returns immediately with empty response. NULL = HTTP 204

`GET /devices/sys/tg_test/1/commands` -  returns an array of objects defined below*):

`GET /devices/sys/tg_test/1/commands/DevString`:
```
#!JSON
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
  }
  "_links":{
              "_parent":"<prefix>/devices/sys/tg_test/1",
              "_self":"<prefix>/devices/sys/tg_test/1/commands/DevString"
          }
}
```

*) Assuming _sys/tg_test/1_ has 2 commands: __DevString__ and __DevLong__

`PUT /devices/sys/tg_test/1/commands/DevVoid`:
```
#!JSON
{
    "name":"DevVoid",
    "output":null,
    "_links":{
            "_parent":"<prefix>/devices/sys/tg_test/1",
            "_self":"<prefix>/devices/sys/tg_test/1/commands/DevVoid"
        }
}
```

`PUT /devices/sys/tg_test/1/commands/DevString`:
```
"Hi!"
```
```
#!JSON
{
    "name":"DevString",
    "output":"Hi!",
    "_links":{
            "_parent":"<prefix>/devices/sys/tg_test/1",
            "_self":"<prefix>/devices/sys/tg_test/1/commands/DevString"
        }
}
```

`PUT /devices/sys/tg_test/1/commands/DevVarDoubleStringArr`:
```
#!json
{
    "dvalue":[3.14, 2.87],
    "svalue":["Hello", "World", "!!!"]    
}
```
```
#!JSON
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
```
#!JSON
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

|                                                                         |            |
|-------------------------------------------------------------------------|------------|---------------------------------------
| `GET    /devices/{device.name}/properties`                            | JSONArray | – lists all properties of the device
| `GET    /devices/{device.name}/properties/{property}`                            | JSONObject | – returns property value
| `PUT    /devices/{device.name}/properties/{property}?value={value1}&value={value2}&...[?async=true]` | JSONObject | – writes new value for the property
| `PUT    /devices/{device.name}/properties?{prop1}={value}&{prop2}={value}[?async=true]` | JSONArray | – writes new value for the specified properties, not specified are deleted
| `POST   /devices/{device.name}/properties/{property}?value={value1}&value={value2}&...[?async=true]`| JSONObject | – creates a new property
| `POST   /devices/{device.name}/properties?{prop1}={value}&{prop2}={value}[?async=true]`| JSONArray | – creates a new property
| `DELETE /devices/{device.name}/properties/{property}`                         | NULL | – deletes property. NULL = HTTP 204


`GET /devices/{device.name}/properties`:
```
#!JSON
[
     {
         "name": "myProp", 
         "values": ["myPropValue"]
     }
]
```

`GET /devices/sys/tg_test/1/properties/myProp`:
```
#!JSON
{
    "name": "myProp", 
    "values": ["myPropValue"]
}
```

`PUT /devices/sys/tg_test/1/properties?myProp="Hello"&myProp="World"&myProp="!!!"`:
```
#!JSON
[
  {
    "name": "myProp", 
    "values": ["Hello","World","!!!"]
  },
  ..
]
```

### Device pipes

|                |           |
|----------------|-----------|---------------------------------------------------
| `GET /devices/{device.name}/pipes` | JSONArray | - displays device pipes
| `GET /devices/{device.name}/pipes/{pipe}` | JSONObject | - read device pipe
| `PUT /devices/{device.name}/pipes/{pipe}[?async=true]` | JSONObject|NULL | - write device pipe

`GET /devices/{device.name}/pipes` returns an array of objects shown below

`GET /devices/sys_tg/test/1/pipes/DevPipe`:
```
#!JSON
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
```
#!json
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


## Filters:

Any response can be supplied with a filter parameter:

|                |           |
|----------------|-----------|---------------------------------------------------
| `GET /{any}?filter={fld1}&filter={fld2}&...[filter=!{fld1}&filter=!{fld2}&...]` | Depends on the response type: JSONArray or JSONObject | - response contains only required fields (or inverse)


This one shows only _name_ and _server_ fields;

`GET /devices/sys/tg_test/1?filter=name&filter=server`:
```
#!JSON
{
    "name":"sys/tg_test/1",
    "info":{
        "name":"sys/tg_test/1",
        "server":"TangoTest/test",
    },
    "attributes":[
                    {
                        "name": "string_scalar"
                    },
                    {
                        "name": "long_scalar_w"
                    }
                ],
    "commands":[
                   {
                       "name":"DevString",
                   },
                   {
                       "name":"DevLong",
                   }
               ],
    "_links":{
            "_parent":"<prefix>/devices"
            "_self":"<prefix>/devices/sys/tg_test/1"
        }
}
```

This one shows everything except _info_ and _properties_ fields:

`GET /devices/sys/tg_test/1/attributes?filter=!info&filter=!properties`:
```
#!JSON
[
    {
        "name": "long_scalar_w"
        "value":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value",
        "_links":{
            "_device":"<prefix>/devices/sys/tg_test/1"
            "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
            "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value"
        }
    },
    {
        "name": "string_scalar"
        "value":"<prefix>/devices/sys/tg_test/1/attributes/string_scalar/value",
         "_links":{
            "_device":"<prefix>/devices/sys/tg_test/1"
            "_parent":"<prefix>/devices/sys/tg_test/1/attributes/string_scalar",
            "_self":"<prefix>/devices/sys/tg_test/1/attributes/string_scalar/value"
        }
    }
]
```


## Pages:

|                |           |
|----------------|-----------|---------------------------------------------------
| `GET /{any_collection}?range={range}` | JSONArray | - response contains only required number of resources

For instance, `GET /device?range=0-25` will display only the first 25 devices

The HTTP answer is 206 - Partial Content.
The HTTP header should return some useful information:

    Content-Range: offset – limit / count
        offset: index of the first element
        limit : index of the last element
        count : total number of elements from the collection
    Accept-Range: resource and max
        resource : type of the element
        max : maximum number of element per request
    Link: can return several URI to the previous and next range, the first and last range ...

This information is also available in a dedicated item in the collection:

```
#!JSON
[
    ...,
    {
        "name":"partial_content",
        "total":113,
        "offset":0,
        "limit":25,
        "_links":{
            "_prev":null,
            "_next":"<prefix>/devices?range=26-50",
            "_first":"<prefix>/devices?range=0-25",
            "_last":"<prefix>/devices?range=101-113"
        }
    }
]
```

Here *_prev* in *_links* is __null__ because the first range were returned.

If the entire collection fits into range response is the same as there is no _range_ parameter (HTTP 200 - OK; no additional info in response's header; no special element in the collections)

## Failure:

`HTTP 500`

```
#!JSON
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
}
```

__IMPLEMENTATION NOTE:__ any exception that can be handled on the server side must be handled, i.e. a proper JSONObject must be returned.

# Implementation remarks:

1. Implement async where possible (almost any PUT, POST and DELETE methods)
2. All constants and magic numbers in responses, i.e. data type, data format, writable, level must be replaced with their string representation
3. Image attributes must be handled on the server side, i.e. server safes image as jpeg (or tiff) and sends URL or embeds this image into response.
4. API must be allowed only for authorized users.
5. Optionally provide integration with TangoAccessControl
6. Optional shortcuts may be implemented to reduce data transfer
7. Provide access to _set_attribute_config()_ via admin panel
8. PUT attribute can be implemented as _write_read_ call.

# Implementation recommendations:

1. Implementation must cache Tango proxy objects
2. Implementation must provide Expires response header value related requests (attribute value read)
3. Implementation must export configuration for all caches, i.e. how long keep read value


# References

[1] [Brian Mulloy, Web API Design. Crafting Interfaces that Developers Love](https://pages.apigee.com/rs/apigee/images/api-design-ebook-2012-03.pdf)