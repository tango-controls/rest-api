[TOC]

## Tango host:port (database)

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc4/hosts`     | JSONArray  | – tango hosts available through this API 
|`GET /tango/rest/rc4/hosts/{tango_host}/{tango_port}`  |   JSONObject   |  -- corresponding Tango database info  

_tango_host_ and _tango_port_ are not known in advance, as user may ask for an arbitrary Tango database. By default implementation tries to connect to TANGO_HOST=localhost:10000, i.e. to the database deployed on the same host. _localhost_ can be replaced with host name, e.g. _hzgxenvtest_. 

`GET /tango/rest/rc4/hosts/hzgxenvtest/10000`:
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
    "devices" : "<prefix>/devices"
}

```

__IMPLEMENTATION NOTE:__ the database to which implementation connects at start may be configured.

__IMPLEMENTATION NOTE:__ this response's info is the same as output of the tango_host:tango_port/sys/DatabaseDs/2/DbInfo command via standard Tango API

## Devices:

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /devices[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API

`GET /devices`:

__OR__

`GET /devices?wildcard=sys*/*/1`:
```JSON
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

### Tango host devices tree

| URL                                         | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc4/hosts/{host}/{port}/devices/tree[?wildcard={wildcard}]`     | JSONArray  | – lists all devices visible through this API

`GET /tango/rest/rc4/hosts/localhost/10000/devices/tree?wildcard=sys/tg_test/*&wildcard=test2/*/*`

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
|`GET /tango/rest/rc4/devices/tree?host={tango_host}[:{tango_port}]&[wildcard={devices filter}]`         | JSONArray  | – Tango host(s) tree, devcice filter(s) - wildcard e.g. `sys/*/*`

`GET /tango/rest/rc4/devices/tree?host=localhost&wildcard=sys/tg_test/*`  
  
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

**NOTE**: above is the same as `GET tango/rest/rc4/hosts/localhost/devices/tree?wildcard=sys/tg_test/*]`

`GET /tango/rest/rc4/devices/tree?host=localhost&host=hzgxenvtest&wildcard=sys/tg_test/*`

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