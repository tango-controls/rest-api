[TOC]

# HTTP/2.0

Since v1.0 implementation MUST provide upgrade to h2 protocol (HTTP/2.0). Practically this means that implementation MUST support only https scheme and MAY support http (HTTP/1.1).

For proper https support implementation MUST be supplied with a valid SSL certificate (OpenSSL). For development a self-signed certificate will do the trick, but for production implementation SHOULD be supplied with properly signed certificate.

# API version and Security

_api_version_ follows URL prefix and defines which version of this API supports current implementation.

Example:


`GET /tango/rest` =>
```JSON
{
    "v1.0":"http://hzgcttest:8080/tango/rest/v1.0"
}
```

`GET /tango/rest/non_existing_version` => `HTTP 404`

All resources under _api_version_ must be protected and require an authentication (specification allows non-protected resources but this is strictly not recommended).

Implementation SHOULD implement some of the standard [WWW-Authenticate](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#Authentication_schemes) types.

For instance, when protected by Basic:

`GET /tango/rest/v1.0` =>
```
HTTP OK
WWW-Authenticate: Basic realm="Tango-Controls Realm" 

{
    "hosts":"/tango/rest/v1.0/hosts"
}
```

In case of _Basic_ any unauthorized request to any protected resource under _rest/api_version_ must get _HTTP 401 Not Authorized_ response
and follow the standard Web Basic Authorization mechanism.

__IMPLEMENTATION NOTE:__ consider integration with TangoAccessControl so that each request is validated against it.

## Links

Implementation SHOULD attach a number of links to a particular response. For instance most of the response types may include _self_ link:

```
HTTP 200

Link: <link>; rel="self"
```

as well as external relationship links:

`GET /tango/rest/v1.0/hosts/localhost`
```
HTTP 200

Link: </tango/rest/v1.0/hosts>; rel="parent"
```

Or pagination related links:

```
HTTP 206

Link: <http://localhost:10001/tango/rest/v1.0/hosts/localhost/devices>; rel="first"; range="0-10"
Link: <http://localhost:10001/tango/rest/v1.0/hosts/localhost/devices>; rel="last"; range="31-35"
Link: <http://localhost:10001/tango/rest/v1.0/hosts/localhost/devices>; rel="prev"; range="0-10"
Link: <http://localhost:10001/tango/rest/v1.0/hosts/localhost/devices>; rel="next"; range="21-30"
```

See [Link header](http://tools.ietf.org/html/rfc5988)

Implementation MUST prefer lower case urls in links e.g. `DevString` (Tango command name) -> `devstring`.

# Filters

Any response can be supplied with a filter parameter:

 URL                |   Response  | Desc
------------------- | ----------- | ---------------------------------------------------
`GET /{any}?filter={fld1}&filter={fld2}&...[filter=!{fld1}&filter=!{fld2}&...]` | Depends on the response type: JSONArray or JSONObject | - response contains only required fields (or inverse)


This one shows only _name_ and _server_ fields;

`GET /devices/sys/tg_test/1?filter=name&filter=server`:
```JSON
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
                       "name":"DevString"
                   },
                   {
                       "name":"DevLong"
                   }
               ]
}
```

This one shows everything except _info_ and _properties_ fields:

`GET /devices/sys/tg_test/1/attributes?filter=!info&filter=!properties`:
```JSON
[
    {
        "name": "long_scalar_w",
        "value":"<prefix>/devices/sys/tg_test/1/attributes/long_scalar_w/value"
    },
    {
        "name": "string_scalar",
        "value":"<prefix>/devices/sys/tg_test/1/attributes/string_scalar/value"
    }
]
```


# Range

URL                |   Response  | Desc
------------------- | ----------- | ---------------------------------------------------
`GET /{any collection}?range={start}-{end}` | JSONArray | - responses with sub-collection extracted from the original


Implementation MUST include "Accept-Ranges: items" for collection like resources e.g. devices list. Also it MUST include "X-size" response header to indicate how many items are in the collection.

```
GET /hosts/localhost/devices
```

```
Accept-Ranges: items
X-size:26

[...]
```

**NOTE**: we can not use standard _Content-Length_ header here because it is strictly bound to bytes i.e. client may shrink incoming response hence partial JSON and JSONParse exception.

Client includes "Range" header into request to specify the desired range of the collection, while implementation MUST include "Content-Range" header:

```
GET /hosts/localhost/devices?range=10-20
```

```
HTTP 206
Content-Range: items 10-20/26

[...]
```

For instance, 
```
GET /hosts/localhost/devices?range=0-25
``` 

will display only the first 25 devices of a particular Tango host

Implementation MUST respond with **416** in case _Range_ is not satisfiable.

# Cache

Implementation MUST provide _Cache-Control_ headers for Tango resources. Implementation MUST add _max-age-millis_ Cache-Control extension to specify cache delay in millis.

Implementation SHOULD distinguish between fast changing and slow changing values. For instance a list of available devices may be considered as slow changing value and cached for a longer time. Also slow changing values may be cached publicly.

Implementation SHOULD export configuration parameters for cache delays and etc  

`GET /hosts/localhost/devices`

```
HTTP 200

Cache-Control: no-transform, max-age=300, max-age-millis="300000"
Expires: Wed, 21 Nov 2018 11:06:11 GMT
ETag: AC6CB07B377F93434998D8556D60A575

[...]
```

`GET /hosts/localhost/devices/sys/tg_test/1/attributes/double_scalar_ro`

```
HTTP 200

Cache-Control: no-transform, max-age=0, max-age-millis="200"
Expires: Wed, 21 Nov 2018 11:06:11 GMT
ETag: AC6CB07B377F93434998D8556D60A575

[...]
```


# Errors

Most of the errors that happen inside implementation usually indicate invalid request e.g. request of a non-existent attribute or writing a value of a wrong type. Hence implementation MUST respond with HTTP 400:

`GET /devices/sys/tg_test/1/attributes/throw_exception`

```
HTTP 400

{
    "errors":[
        {       
            "reason":"TangoProxyException",
            "description":"sys/tg_test/1 proxy has throw an exception",
            "severity":"ERR",
            "origin":"DeviceProxy#readAttribute sys/tg_test/1/throw_exception"
        }
    ],   
    "quality": "FAILURE",
    "timestamp": 123456789
}
```

__IMPLEMENTATION NOTE:__ any exception that can be handled on the server side must be handled, i.e. a proper JSONObject must be returned.

### 401

Unauthorized request -- client has failed to provide valid credentials

### 404

Resource does not exist e.g. `GET devices/x/y/z` should return status code 404 if `x/y/z` is not defined in th Tango db.

### 500

Tango REST server crashes - indicates bug in the REST server

### 503

In case of REST server receives CORBA timeout it returns 503 if upstream server does not respond within the specified timeout. In case of event subscription may indicate that there is no event though i.e. is not a failure.

__IMPLEMENTATION NOTE:__ server should not log any error except 500 when it is a server's failure.