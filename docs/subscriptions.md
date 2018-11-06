[TOC]

## Subscriptions

Provides an entry point for subscriptions (Tango Controls event system)

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`POST /tango/rest/rc6/subscriptions?event={event1}&event={event2}&...`             | JSONObject | – creates a new subscription  

`POST /tango/rest/rc6/subscriptions`


```json
{
  "id":0
}
```

`POST /tango/rest/rc6/subscriptions?event=hzgxenvtest:10000/sys/tg_test/1/double_scalar/change`

```json
{
  "id":0,
  "events":[
    "hzgxenvtest:10000/sys/tg_test/1/double_scalar/change"    
  ]
}
```

### Subscription

Represents single subscription

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc6/subscriptions/{id}`              | JSONObject  | – this subscription as JSON 
|`PUT /tango/rest/rc6/subscriptions/{id}?event={event1}&event={event2}&...`  | JSONObject  | – this subscription as JSON
|`GET /tango/rest/rc6/subscriptions/{id}/event-stream` | text/event-stream  | – events stream
|`DELETE /tango/rest/rc6/subscriptions/{id}`           | NULL  | – closes events stream and cancels subscription


`GET /tango/rest/rc6/subscriptions/{id}`

```json
{
  "id":0,
  "events": [
    "hzgxenvtest:10000/sys/tg_test/1/double_scalar/change",
    "hzgxenvtest:10000/sys/tg_test/1/long_scalar/periodic"
  ]
}
```

### Event stream

Implementation MUST multiplex all events into a single subscription stream:

```
id: <upstream event time>
event: hzgxenvtest:10000/sys/tg_test/1/double_scalar/change 
data: <upstream event data as plain text e.g. 3.14>
```

In case of an error:

```
id: <upstream event time or server time when error has occured>
event: error 
data: <event e.g. hzgxenvtest:10000/sys/tg_test/1/double_scalar/change>:<upstream error cause>
```



## Implementations notes

1. Implementation MUST broadcast events from single upstream
2. Implementation MUST un-subscribe when there is no client for a particular upstream
3. Implementation MAY export reconnection timeout to the configuration
4. Implementation MAY separate subscriptions by client/app/user

## References

[1] [Server sent events overview](https://www.w3schools.com/html/html5_serversentevents.asp)
