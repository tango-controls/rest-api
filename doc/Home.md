[TOC]

# Tango REST API RC2

__NOTE__: this is spec of version RC2 for RC1 clone this wiki and update to revision rc1: `hg clone ... & hg up -r rc1`

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

All URLs in this section omit prefix part: `http://host[:port]/tango_host[_port]/rest`*). A valid example of a prefix:
`http://hzgcttest:8080/hzgcttest1/rest` - here _hzgcttest:8080_ is the host where implementation server is deployed;
_hzgcttest1_ refers to TANGO_HOST in this case implementation and user may assume TANGO_HOST=hzgcttest1:10000, this defines which Tango database
is effectively exported via this API.

URL prefix must follow snake_case. Other parts of the URL are equivalent to case used in Tango. 

*) - here and below [_url_part_] – optional part of the URL

## API version and Security

_api_version_ follows URL prefix and defines which version of this API supports current implementation.

Example:


`http://hzgcttest:8080/hzgcttest/rest` =>
```
#!JSON
{
    "rc1":"http://hzgcttest:8080/hzgcttest/rest/rc1",
    "mtango-1.0.1":"http://hzgcttest:8080/hzgcttest/rest/mtango-1.0.1",
    "mtango-1.0.2":"http://hzgcttest:8080/hzgcttest/rest/mtango-1.0.2"
}
```

`http://hzgcttest:8080/hzgcttest/rest/non_existing_version` => `HTTP 404`

All resources under _api_version_ must be protected and require an authentication (specification allows non-protected resources but this is strictly not recommended).

API implementation must support 2 authentication methods:

* Basic Web authentication
* OAuth2

When protected by Basic:

`http://hzgcttest:8080/hzgcttest/rest/rc1` =>
```
#!JSON
{
    "devices":"http://hzgcttest:8080/hzgcttest/rest/rc1/devices",
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
    "devices":"http://hzgcttest:8080/hzgcttest/rest/rc1/devices",
    "x-auth-method":"oauth2",
    "x-auth-resource":"https://hzgcttest:8080/hzgcttest/oauth2/authorize"
}
```

Client uses _x-auth-resource_ to get access_token following the standard OAuth2 authentication procedure. This token is then provided
with each request to the protected resources either in Authentication request header:`Authorization: token access_token` or as a parameter:
`http://hzgcttest:8080/hzgcttest/rest/rc1/devices?token={access_token}`.

_x-auth-method_ = _none_ is not recommended but allowed.

__IMPLEMENTATION NOTE:__ consider integration with TangoAccessControl so that each request is validated against it.

API version in the following sections is considered to be a part of the URL prefix, i.e. _<prefix>_ = `http://host[:port]/tango_host[_port]/rest/api_version`

In some example URLs _<prefix>_ is omitted at all, so `GET /devices[?wildcard={wildcard}]` effectively means `GET http://host[:port]/tango_host[_port]/rest/api_version/devices[?wildcard={wildcard}]`

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
    "attributes":[
                    {
                        "name": "string_scalar"
                        "href":"<prefix>/devices/sys/tg_test/1/attributes/string_scalar"
                    },
                    {
                        "name": "long_scalar_w"
                        "href":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w"
                    }
                ],
    "commands":[
                   {
                       "name":"DevString",
                       "href":"<prefix>/devices/sys/tg_test/1/commands/DevString"
                   },
                   {
                       "name":"DevLong",
                       "href":"<prefix>/devices/sys/tg_test/1/commands/DevLong"
                   }
               ],
    "pipes":[
                {
                    "name":"DevPipe",
                    "href":"<prefix>/devices/sys/tg_test/1/pipes/DevPipe"
                }
            ],
    "properties":[],
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
| `GET /devices/{device.name}/attributes/{attribute}`                                          | JSONObject | – displays the attribute's info
| `GET /devices/{device.name}/attributes/{attribute}/value`                                    | JSONObject | – returns attribute value. Last-Modified = read timestamp from the Tango
| `PUT /devices/{device.name}/attributes/{attribute}?value={value}[&async=true]`               | JSONObject/NULL | – returns value after it is being written, i.e. synchronous write&read; empty response if async=true; argument can be passed in request's body. Last-Modified = write timestamp from the Tango. NULL = HTTP 204
| `PUT /devices/{device.name}/attributes?{attr1}={value}&{attr2}={value}[&async=true]`         | JSONArray/NULL  | – updates specified attributes. Last-Modified = write timestamp from the Tango. NULL = HTTP 204

Assuming _sys/tg_test/1_ has 2 attributes: __string_scalar__ and __long_scalar_w__:

`GET /devices/sys/tg_test/1/attributes` - returns an array of objects defined below:

`GET /devices/sys/tg_test/1/attributes/long_scalar_w`:
```
#!JSON
{
  "name":"long_scalar_w",
  "value":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value",
  "info":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/info",
  "properties":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/properties",
  "_links":{
    "_device":"<prefix>/devices/sys/tg_test/1",
    "_parent":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w",
    "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/info"
  }
}
```


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

`PUT /devices/sys/tg_test/1/attributes/long_scalar_w?value=42`:
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

```
#!JSON
{
      "writable":"READ_WRITE",
      "data_format":"SCALAR",
      "data_type":"DevLong64",
      "max_dim_x":1,
      "max_dim_y":0,
      "description":"No description",
      "label":"long_scalar_w",
      "unit":"No unit",
      "standard_unit":"No standard unit",
      "display_unit":"No display unit",
      "format":"%d",
      "min_value":"Not specified",
      "max_value":"Not specified",
      "min_alarm":"Not specified",
      "max_alarm":"Not specified",
      "writable_attr_name":"None",
      "level":"OPERATOR",
      "extensions":[],
      "_links":[...]
  }
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
    {"prop1":["value1"]}.
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
| `PUT /devices/{device.name}/commands/{command}[?input={value}][&async=true]` | JSONObject/NULL | – executes a command of the device; if not async returns specified JSONObject, i.e. blocks until finished, otherwise – returns immediately with empty response. NULL = HTTP 204

Assuming _sys/tg_test/1_ has 2 commands: __DevString__ and __DevLong__:

`GET /devices/sys/tg_test/1/commands` -  returns an array of objects defined below:

`GET /devices/sys/tg_test/1/commands/DevString`:
```
#!JSON
{
  "name":"DevString",
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

`PUT /devices/sys/tg_test/1/commands/DevVoid`:
```
#!JSON
{
    "name":"DevVoid",
    "input":null,
    "output":null,
    "_links":{
            "_parent":"<prefix>/devices/sys/tg_test/1",
            "_self":"<prefix>/devices/sys/tg_test/1/commands/DevVoid"
        }
}
```


`PUT /devices/sys/tg_test/1/commands/DevString?input=Hi!`:
```
#!JSON
{
    "name":"DevString",
    "input":"Hi!",
    "output":"Hi!",
    "_links":{
            "_parent":"<prefix>/devices/sys/tg_test/1",
            "_self":"<prefix>/devices/sys/tg_test/1/commands/DevString"
        }
}
```


### Device properties:

POST create an instance of collection by the URI of this collection.
POST returns the URI and the id of the newly created instance in the http header

PUT is used systematically to make a full update. Any attributes not specified will be deleted (or reset by default). Use PATCH to avoid this behaviour.

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
         “name”: “myProp”, 
         “values”: [“myPropValue”]
     }
]
```

`GET /devices/sys/tg_test/1/properties/myProp`:
```
#!JSON
{
    “name”: “myProp”, 
    “values”: [“myPropValue”]
}
```

### Device pipes

|                |           |
|----------------|-----------|---------------------------------------------------
| `GET /devices/{device.name}/pipes` | JSONArray | - displays device pipes
| `GET /devices/{device.name}/pipes/{pipe}` | JSONObject | - read device pipe
| `PUT /devices/{device.name}/pipes/{pipe}?value={object}[&async=true]` | JSONObject|NULL | - write device pipe

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

`PUT /devices/sys_tg/test/1/pipes/DevPipe`:
```
#!JSON
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