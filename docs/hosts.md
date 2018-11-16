[TOC]

## Tango host:port (database)

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
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


### Devices:

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /hosts/{host}[;{port}]/devices[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API
|`GET /hosts/{host}[;{port}]/devices/tree[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API

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

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc5/devices/tree?host={tango_host}[:{tango_port}]&[wildcard={devices filter}]`         | JSONArray  | – Tango host(s) tree, devcice filter(s) - wildcard e.g. `sys/*/*`

`GET /tango/rest/rc5/devices/tree?host=localhost&wildcard=sys/tg_test/*`  
  
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
  }
]
```

**NOTE**: above is the same as `GET tango/rest/rc5/hosts/localhost/devices/tree?wildcard=sys/tg_test/*]`

`GET /tango/rest/rc5/devices/tree?host=localhost&host=hzgxenvtest&wildcard=sys/tg_test/*`

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

__IMPLEMENTATION NOTE:__ this response is based on sequential execution of TangoDatabase.DbGetDeviceDomain[Family|Member|Alias]List commands


## Attributes

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /attributes?wildcard=*[:{port}]/*/*/*/*`   | JSONArray  | -- returns an array of attributes filtered by wildcard(s) 

`GET /attributes?wildcard=localhost/sys/*/1/long_scalar_w&wildcard=hzgxenvtest/sys/*/2/double_scalar_w` 


```json
[
    {
      "name":"long_scalar_w",
      "device": "sys/tg_test/1",
      "host": "localhost:10000"
    },
    {
      "name":"double_scalar_w",
      "device": "sys/tg_test/2",
      "host":"localhost:10000"
    },
    ...
]
```

### read

`GET /attributes/value?wildcard=localhost/sys/*/1/long_scalar_w&wildcard=hzgxenvtest/sys/*/2/double_scalar_w`
```json
[
    {
        "name": "long_scalar_w",
        "device": "sys/tg_test/1",
        "host":"localhost:10000",
        "value": 12345,
        "quality": "ATTR_VALID",
        "timestamp": 123456789
    },
    {
        "name": "double_scalar_w",
        "device": "sys/tg_test/2",
        "host":"hzgxenvtest:10000",
        "value": 3.14,
        "quality": "ATTR_VALID",
        "timestamp": 123456789
    }
]
```

### write

`PUT /attributes[?async=true]`
```json
[
  {
    "name":"long_scalar_w",
    "device": "sys/tg_test/1",
    "host":"localhost:10000",
    "value":1234
  },
  {
    "name":"double_scalar_w",
    "device": "sys/tg_test/2",
    "host":"hzgxenvtest:10000",
    "value":3.14
  },
  ...
]
```

If not _async_ returns array as in [device/attributes write multiple scalar attributes](device.md#write-multiple-scalar-attributes)

## Commands

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /commands?wildcard=*[:{port}]/*/*/*/*`    | JSONArray | same as for [attributes](#attributes)

`GET /commands?wildcard=localhost/sys/*/*/Dev*`

```json
[
    {
      "name":"DevString",
      "device":"sys/tg_test/1",
      "host": "localhost:10000",
      "history":"<prefix>/devices/sys/tg_test/1/commands/devstring/history",
      "info":{
        "level":"OPERATOR",
        "cmd_tag":0,
        "in_type":"DevString",
        "out_type":"DevString",
        "in_type_desc":"-",
        "out_type_desc":"-"
      }
    },
    {
      "name":"DevDouble",
      "device":"sys/tg_test/1",
      "host": "localhost:10000",
      "history":"<prefix>/devices/sys/tg_test/1/commands/devdouble/history",
      "info":{
        "level":"OPERATOR",
        "cmd_tag":0,
        "in_type":"DevDouble",
        "out_type":"DevDouble",
        "in_type_desc":"-",
        "out_type_desc":"-"
      }
    },
    ...
]
```

`PUT /commands`
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

## Pipes

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /pipes?wildcard=*[:{port}]/*/*/*/*`| JSONArray  | same as for [attributes](#attributes)

Same as for attributes or commands
