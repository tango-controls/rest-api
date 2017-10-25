## API version and Security

_api_version_ follows URL prefix and defines which version of this API supports current implementation.

Example:


`http://hzgcttest:8080/tango/rest` =>
```JSON
{
    "rc4":"http://hzgcttest:8080/tango/rest/rc4",
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
```JSON
{
    "hosts":"/tango/rest/rc3/hosts",
    "x-auth-method":"basic"
}
```

_x-auth-method_ = basic|oauth2|none

In case of _basic_ any unauthorized request to any protected resource under _rest/api_version_ must get _HTTP 401 Not Authorized_ response
and follow the standard Web Basic Authorization mechanism.

In case of _oauth2_ response must provide OAuth2 authorisation resource as well:
```JSON
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

## Filters

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
```JSON
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


## Pages

  URL           |  Response | Desc
----------------|-----------|---------------------------------------------------
 `GET /{any_collection}?range={range}` | JSONArray | - response contains only required number of resources

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

```JSON
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

## Failure

```JSON
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