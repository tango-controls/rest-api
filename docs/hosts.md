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

## Devices tree 

`GET /tango/rest/rc5/hosts/{tango_host};port={tango_port}/devices/tree[?f={devices filter}]`  
  
-- same as `hosts/tree?v={tango_host}:{tango_port}[&f={devices filter}]` by for particular Tango host