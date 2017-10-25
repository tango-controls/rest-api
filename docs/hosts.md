## Tango host:port (database)

|                                         |            |
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc3/hosts`     | JSONArray  | – tango hosts available through this API 
|`GET /tango/rest/rc3/hosts/{tango_host}/{tango_port}`  |   JSONObject   |  -- corresponding Tango database info  

_tango_host_ and _tango_port_ are not known in advance, as user may ask for an arbitrary Tango database. By default implementation tries to connect to TANGO_HOST=localhost:10000, i.e. to the database deployed on the same host. _localhost_ can be replaced with host name, e.g. _hzgxenvtest_. 

`GET /tango/rest/rc3/hosts/hzgxenvtest/10000`:
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

|                                         |            |
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