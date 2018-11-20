[TOC]

# Tango Controls as RESTful resources

Tango REST API provides RESTful view of Tango Controls. Tango Controls entities are mapped to a tree structure:

```
/hosts/
   {tango host}/devices
       {tango device}/attributes
                     {tango attribute}/properties
                                      {tango attribute property}
                                      /history
                                      /value
                     /commands
                     {tango command}/history
                     /pipes
                     {tango pipe}/value          
                     /properties
                     {tango device property}                     
```

In addition Tango REST API provides several entry points for bulk operations:

```
/devices
/attributes
/commands
/pipes
```

# TangoHost

Tango host resides under _hosts_ collection. Each Tango host is specified by the host name and optionally a port.

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /hosts/{tango_host};port={tango_port}`  |   JSONObject   |  -- corresponding Tango database info. Tango port is 10000 by default
  
_tango_host_ and _tango_port_ are not known in advance, as user may ask for an arbitrary Tango database. By default implementation tries to connect to TANGO_HOST=localhost:10000, i.e. to the database deployed on the same host. _localhost_ can be replaced with host name, e.g. _hzgxenvtest_. 

`GET /tango/rest/rc5/hosts/hzgxenvtest`
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
    "tree" : "<prefix>/devices/tree"
}

```

__IMPLEMENTATION NOTE:__ this response's info is the same as output of the tango_host:tango_port/sys/DatabaseDs/2/DbInfo command via standard Tango API


### Tango host devices

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /hosts/{host}[;{port}]/devices[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API

```http request
GET /hosts/localhost/devices
```

__OR__

`GET /hosts/localhost/devices?wildcard=sys*/*/1`
```JSON
[
    {
        "name":"sys/tg_test/1",
        "alias":"test_device",
        "href":"<prefix>/devices/sys/tg_test/1"
    },
    {
        "name":"sys/access_control/1",
        "alias":null,//maybe skipped
        "href":"<prefix>/devices/sys/access_control/1"
    }
]
```

__IMPLEMENTATION NOTE:__ this response is the same as when execute command: sys/databaseds/2/DbGetDeviceWideList(wildcard) via standard Tango API

### Tango host devices tree

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /hosts/{host}[;{port}]/devices/tree[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API

`GET /hosts/localhost/devices/tree?wildcard=sys/tg_test/*&wildcard=test2/*/*`

```json
[
  {
    "id": "localhost:10000",
    "value": "localhost:10000",
    "$css": "tango_host",
    "data": [
      {
        "value": "aliases",
        "$css": "aliases",
        "data": []
      },
      {
        "value": "sys",
        "$css": "tango_domain",
        "data": [
          {
            "value": "tg_test",
            "$css": "tango_family",
            "data": [
              {
                "id": "localhost:10000/sys/tg_test/1",
                "value": "1",
                "$css": "member",
                "isMember": true,
                "device_name": "sys/tg_test/1"
              }
            ]
          }
        ]
      },
      {
        "value": "test2",
        "$css": "tango_domain",
        "data": [
          {
            "value": "debian8",
            "$css": "tango_family",
            "data": [
              {
                "id": "localhost:10000/test2/debian8/20",
                "value": "20",
                "$css": "member",
                "isMember": true,
                "device_name": "test2/debian8/20"
              }
            ]
          }
        ]
      }
    ],
    "isAlive": true
  }
]
```

# Devices tree 

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc5/devices/tree?host={tango_host}[:{tango_port}]&[wildcard={devices filter}]`         | JSONArray  | – Tango host(s) tree, devcice filter(s) - wildcard e.g. `sys/*/*`

`GET /tango/rest/rc5/devices/tree?host=localhost&wildcard=sys/tg_test/*`  
  
```json
[
  {
    "id": "localhost:10000",
    "value": "localhost:10000",
    "$css": "tango_host",
    "data": [
      {
        "value": "aliases",
        "$css": "aliases",
        "data": []
      },
      {
        "value": "sys",
        "$css": "tango_domain",
        "data": [
          {
            "value": "tg_test",
            "$css": "tango_family",
            "data": [
              {
                "id": "localhost:10000/sys/tg_test/1",
                "value": "1",
                "$css": "member",
                "isMember": true,
                "device_name": "sys/tg_test/1"
              }
            ]
          }
        ]
      }
    ],
    "isAlive": true
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


# Attributes

Attributes resource allows bulk listing, reading and writing of tango attributes

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /attributes?wildcard={tango_host}[:{port}]/*/*/*/*`   | JSONArray  | -- returns an array of attributes filtered by wildcard(s) 

`GET /attributes?wildcard=localhost/sys/tg_test/*/State` 


```json
[
  {
    "id": "localhost:10000/sys/tg_test/1/State",
    "name": "State",
    "device": "sys/tg_test/1",
    "host": "localhost:10000",
    "info": {
      "name": "State",
      "writable": "READ",
      "data_format": "SCALAR",
      "data_type": "State",
      "max_dim_x": 1,
      "max_dim_y": 0,
      "description": "No description",
      "label": "State",
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
          "period": "1000",
          "extensions": []
        },
        "arch_event": {
          "rel_change": "Not specified",
          "abs_change": "Not specified",
          "period": "Not specified",
          "extensions": []
        }
      },
      "extensions": [],
      "sys_extensions": [],
      "isMemorized": false,
      "isSetAtInit": false,
      "memorized": "UNKNOWN",
      "root_attr_name": "Not specified",
      "enum_label": null
    },
    "value": "http://localhost:10001/tango/rest/rc5/hosts/localhost;port=10000/devices/sys/tg_test/1/attributes/State/value",
    "properties": "http://localhost:10001/tango/rest/rc5/hosts/localhost;port=10000/devices/sys/tg_test/1/attributes/State/properties",
    "history": "http://localhost:10001/tango/rest/rc5/hosts/localhost;port=10000/devices/sys/tg_test/1/attributes/State/history"
  }
]
```

**read**

`GET /attributes/value?wildcard=localhost/sys/tg_test/*/State&wildcard=localhost/sys/database/*/State`
```json
[
  {
    "name": "State",
    "host": "localhost:10000",
    "device": "sys/tg_test/1",
    "value": "RUNNING",
    "quality": "ATTR_VALID",
    "timestamp": 1542635644117
  },
  {
    "name": "State",
    "host": "localhost:10000",
    "device": "sys/database/2",
    "value": "ON",
    "quality": "ATTR_VALID",
    "timestamp": 1542635644131
  }
]
```

**write**

```
PUT /attributes

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
  }
]
```

```json
[
  {
    "name": "long_scalar_w",
    "host": "localhost:10000",
    "device": "sys/tg_test/1",
    "value": 1234,
    "quality": "ATTR_VALID",
    "timestamp": 1542635799983
  },
  {
    "name": "double_scalar_w",
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/2",
    "value": 3.14,
    "quality": "ATTR_VALID",
    "timestamp": 1542635800004
  }
]
```

```
PUT /attributes?async=true

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
  }
]
```

```
HTTP/1.1 204 

<Response body is empty>
```

**error**

`GET /attributes/value?wildcard=localhost/sys/tg_test/*/ampli&wildcard=localhost/sys/tg_test/*/throw_exception`

```json
[
  {
    "name": "ampli",
    "host": "localhost:10000",
    "device": "sys/tg_test/1",
    "value": 0.0,
    "quality": "ATTR_VALID",
    "timestamp": 1542637586634
  },
  {
    "errors": [
      {
        "reason": "exception test",
        "description": "here is the exception you requested",
        "severity": "ERR",
        "origin": "TangoTest::read_throw_exception"
      }
    ],
    "quality": "FAILURE",
    "timestamp": 1542637586657
  }
]
```

# Commands

Commands resource allows bulk listing and execution of tango commands

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /commands?wildcard={host}[:{port}]/*/*/*/*`    | JSONArray | same as for [attributes](#attributes)

`GET /commands?wildcard=localhost/sys/*/*/DevDouble`

```json
[
  {
    "id": "localhost:10000/sys/tg_test/1/DevDouble",
    "name": "DevDouble",
    "device": "sys/tg_test/1",
    "host": "localhost:10000",
    "info": {
      "cmd_name": "DevDouble",
      "level": "OPERATOR",
      "cmd_tag": 0,
      "in_type": "DevDouble",
      "out_type": "DevDouble",
      "in_type_desc": "Any DevDouble value",
      "out_type_desc": "Echo of the argin value"
    },
    "history": "http://localhost:10001/tango/rest/rc5/hosts/localhost;port=10000/devices/sys/tg_test/1/commands/DevDouble/history"
  }
]
```

**execute**

`PUT /commands`
```json
[
  {
    "host": "localhost:10000",
    "device":"sys/tg_test/1",
    "name":"DevString",
    "input": "Hello World!!!"
  },
  {
    "host": "localhost:10000",
    "device":"sys/tg_test/1",
    "name":"DevDouble",
    "input": 3.14
  },
  {
    "host": "localhost:10000",
    "device":"sys/tg_test/1",
    "name":"CrashFromDeveloperThread"
  }
]
```

Response:
```json
[
  {
    "host": "localhost:10000",
    "device": "sys/tg_test/1",
    "name": "DevString",
    "input": "Hello World!!!",
    "output": "Hello World!!!"
  },
  {
    "host": "localhost:10000",
    "device": "sys/tg_test/1",
    "name": "DevDouble",
    "input": 3.14,
    "output": 3.14
  },
  {
    "host": "localhost:10000",
    "device": "sys/tg_test/1",
    "name": "CrashFromDeveloperThread",
    "errors": [
      {
        "reason": "API_CommandNotFound",
        "severity": "ERR",
        "desc": "Command CrashFromDeveloperThread not found",
        "origin": "Device_2Impl::command_query_2"
      }
    ]
  }
]
```

# Pipes

| URL                                     | Response   | Desc
|-----------------------------------------|------------|--------------------------
| `GET /pipes?wildcard={tango_host}[:{port}]/*/*/*/*`| JSONArray  | same as for [attributes](#attributes)
| `GET /pipes/value?wildcard={tango_host}[:{port}]/*/*/*/*`| JSONArray  | same as for [attributes](#attributes)
| `PUT /pipes/value?wildcard={tango_host}[:{port}]/*/*/*/*`| JSONArray  | same as for [attributes](#attributes)

`GET /pipes?wildcard=hzgxenvtest/sys/tg_test/1/*`

```json
[
  {
    "id": "hzgxenvtest:10000/sys/tg_test/1/string_long_short_ro",
    "name": "string_long_short_ro",
    "device": "sys/tg_test/1",
    "host": "hzgxenvtest:10000",
    "info": {
      "name": "string_long_short_ro",
      "description": "Pipe example",
      "label": "string_long_short_ro",
      "level": "OPERATOR",
      "writeType": "PIPE_READ",
      "writable": false
    },
    "value": "http://localhost:10001/tango/rest/rc5/hosts/hzgxenvtest;port=10000/devices/sys/tg_test/1/pipes/string_long_short_ro/value"
  }
]
```


`GET /pipes/value?wildcard=hzgxenvtest/sys/tg_test/1/*`
```json
[
  {
    "host": "hzgxenvtest:10000",
    "device": "sys/tg_test/1",
    "name": "string_long_short_ro",
    "timestamp": 1542710007862,
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
]
```
