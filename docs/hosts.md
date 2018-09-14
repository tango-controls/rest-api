[TOC]

## Tango host:port (database)

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc5/hosts`              | JSONArray  | – tango hosts available through this API 
|`GET /tango/rest/rc5/hosts/tree?v={tango_host}:{tango_port}&[f={devices filter}]`         | JSONArray  | – Tango host(s) tree, devcice filter(s) - wildcard e.g. `sys/*/*`
|`GET /tango/rest/rc5/hosts/{tango_host};port={tango_port}`  |   JSONObject   |  -- corresponding Tango database info. Tango port is 10000 by default
  
_tango_host_ and _tango_port_ are not known in advance, as user may ask for an arbitrary Tango database. By default implementation tries to connect to TANGO_HOST=localhost:10000, i.e. to the database deployed on the same host. _localhost_ can be replaced with host name, e.g. _hzgxenvtest_. 

`GET /tango/rest/rc5/hosts/hzgxenvtest`:
```json

{
    "host": "hzgxenvtest",
    "port": 10000,
    "name": "sys/Database/2",
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
    "devices" : "<prefix>/devices",
    "tree" : "<prefix>/tree"
}

```

__IMPLEMENTATION NOTE:__ this response's info is the same as output of the tango_host:tango_port/sys/DatabaseDs/2/DbInfo command via standard Tango API

`GET /tango/rest/rc5/hosts/tree?v=localhost:10000&v=hzgxenvtest:10000&f=sys/tg_test/*`:

```json
[
  {
    "id":"localhost:10000",
    "$css":"tango_host",
    "value":"localhost:10000",
    "data":[
      {
        "value":"aliases",
        "$css":"aliases",
        "data":[]
      },
      {
        "value":"sys",
        "data":[
          {
            "value":"tg_test",
            "data":[
              {"value":"1","$css":"member","isMember":true,"device_name":"sys/tg_test/1","device_id":"localhost:10000/sys/tg_test/1"}
            ]
          }
        ]
      }
    ]
  },
  {
    "id":"hzgxenvtest:10000",
    "$css":"tango_host",
    "value":"hzgxenvtest:10000",
    "data":[
      {
        "value":"aliases",
        "$css":"aliases",
        "data":[
          {
            "value":"my_test_device","$css":"member","isAlias":true,"device_name":"sys/tg_test/1"
          }
        ]
      },
      {
        "value":"sys",
        "data":[
          {
            "value":"tg_test",
            "data":[
              {"value":"0","$css":"member","isMember":true,"device_name":"sys/tg_test/0","device_id":"hzgxenvtest:10000/sys/tg_test/0"},{"value":"1","$css":"member","isMember":true,"device_name":"sys/tg_test/1","device_id":"hzgxenvtest:10000/sys/tg_test/1"},{"value":"2","$css":"member","isMember":true,"device_name":"sys/tg_test/2","device_id":"hzgxenvtest:10000/sys/tg_test/2"},{"value":"3","$css":"member","isMember":true,"device_name":"sys/tg_test/3","device_id":"hzgxenvtest:10000/sys/tg_test/3"},{"value":"4","$css":"member","isMember":true,"device_name":"sys/tg_test/4","device_id":"hzgxenvtest:10000/sys/tg_test/4"},{"value":"6","$css":"member","isMember":true,"device_name":"sys/tg_test/6","device_id":"hzgxenvtest:10000/sys/tg_test/6"}
            ]
          }
        ]
      }
    ]
  }
]
```

__IMPLEMENTATION NOTE:__ this responseis based on sequential execution of TangoDatabase.DbGetDeviceDomain[Family|Member|Alias]List commands

## Devices:

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /hosts/{host}[;{port}]/devices[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API

`GET /hosts/localhost/devices`:

__OR__

`GET /hosts/localhost/devices?wildcard=sys*/*/1`:
```JSON
[
    {
        "name":"sys/tg_test/1",
        "alias":"test_device",
        "href":"<prefix>/devices/sys/tg_test/1"
    },
    {
        "name":"sys/tg_test/2",
        "alias":null,//maybe skipped
        "href":"<prefix>/devices/sys/tg_test/2"
    },
    ...
]
```

__IMPLEMENTATION NOTE:__ this response is the same as when execute command: sys/databaseds/2/DbGetDeviceWideList(wildcard) via standard Tango API

## Devices tree 

`GET /tango/rest/rc5/hosts/{tango_host};port={tango_port}/devices/tree[?f={devices filter}]`  
  
-- same as `hosts/tree?v={tango_host}:{tango_port}[&f={devices filter}]` by for particular Tango host

# Attributes

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /attributes?wildcard=*[:{port}]/*/*/*/*`   | JSONArray  | -- returns an array of attributes filtered by wildcard(s) 

`GET /attributes?wildcard=localhost[:10000]/sys/*/1/*` 
```json
[
    {
      "name":"long_scalar_w",
      "device": "sys/tg_test/1",
      "host": "localhost:10000",
     "_links":{
        "_self":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w"
      }
    },
    {
      "name":"double_scalar_w",
      "device": "sys/tg_test/1",
      "host":"localhost:10000",
      "_links":{
        "_self":"<prefix>/devices/sys/tg_test/1/attributes/double_scalar_w"
      }
    },
    ...
]
```

`PUT /attributes[?async=true]`

PUT body:
```json
[
  {
    "attr":"localhost:10000/sys/tg_test/1/long_scalar_w",
    "value":1234
  },
  {
    "attr":"hzgxenvtest:10000/sys/tg_test/2/double_scalar_w",
    "value":3.14
  },
  ...
]
```

If not _async_ returns array as in [device/attributes write multiple scalar attributes](device.md#write-multiple-scalar-attributes)

## Attributes info

`GET /attributes/info?wildcard=localhost:10000/sys*/*/1/*`:

An array of infos as in [attribute info](device.md#info:)

## Attributes history

`GET /attributes/history?wildcard=localhost:10000/sys*/*/1/*`:

```json
[
  [
      {
          "attribute": "localhost:10000/sys/tg_tes/1/string_scalar",
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
  ],
  ...
]
```

# Commands

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /commands?wildcard=*[:{port}]/*/*/*/*`    | JSONArray | same as for [attributes](#attributes)

`GET /commands?wildcard=localhost/sys/*/*/Dev*`

```json
[
    {
      "name":"DevString",
      "device":"sys/tg_test/1",
      "history":"<prefix>/devices/sys/tg_test/1/commands/devstring/history",
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
                  "_self":"<prefix>/devices/sys/tg_test/1/commands/devstring"
              }
    },
    {
      "name":"DevDouble",
      "device":"sys/tg_test/1",
      "history":"<prefix>/devices/sys/tg_test/1/commands/devdouble/history",
      "info":{
        "level":"OPERATOR",
        "cmd_tag":0,
        "in_type":"DevDouble",
        "out_type":"DevDouble",
        "in_type_desc":"-",
        "out_type_desc":"-"
      },
      "_links":{
                  "_parent":"<prefix>/devices/sys/tg_test/1",
                  "_self":"<prefix>/devices/sys/tg_test/1/commands/devdouble
              }
    },
    ...
]
```

`PUT /commands`

PUT body:
```json
[
  {
    "command":"localhost:10000/sys/tg_test/1/DevString",
    "input": "Hello World!!!"
  },
  {
    "command":"localhost:10000/sys/tg_test/2/DevDouble",
    "input": 3.14
  },
  ...
]
```

Response:
```json
[
  {
    "command":"sys/tg_test/1/DevString",
    "output": "Hello World!!!"
  },
  {
    "command":"sys/tg_test/2/DevDouble",
    "output": 3.14
  },
  ...
]
```

If one of the command has failed an error is returned instead of output.

# Pipes

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /pipes?wildcard=*[:{port}]/*/*/*/*`| JSONArray  | same as for [attributes](#attributes)

Same as for attributes or commands
