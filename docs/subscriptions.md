[TOC]

## Subscriptions

Provides an entry point for subscriptions (Tango Controls event system)

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`POST /tango/rest/rc6/subscriptions?event={event1}&event={event2}&...`             | JSONObject | – creates a new subscription  

**Create a new subscription**

`POST /tango/rest/rc6/subscriptions`


```json
{
  "id":0
}
```

**Create a new subscription with events**

`POST /tango/rest/rc6/subscriptions`

```json
{
  "events":[
      {
        "host":"hzgxenvtest:10000",
        "device":"sys/tg_test/1",
        "attribute":"double_scalar",
        "type":"change",
        "rate":100
      },
      {
        "host":"hzgxenvtest:10000",
        "device":"sys/tg_test/1",
        "attribute":"long_scalar",
        "type":"periodic",
        "rate":3000
      }
  ]
}
```


```json
{
  "id":0,
  "events":[
      {
        "id":0,
        "host":"hzgxenvtest:10000",
        "device":"sys/tg_test/1",
        "attribute":"double_scalar",
        "type":"change",
        "rate":100
      },
      {
        "id":1,
        "host":"hzgxenvtest:10000",
        "device":"sys/tg_test/1",
        "attribute":"long_scalar",
        "type":"periodic",
        "rate":3000
      }
  ]
}
```

### Subscription

Represents single subscription

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc6/subscriptions/{id}`              | JSONObject  | – this subscription as JSON 
|`PUT /tango/rest/rc6/subscriptions/{id}`              | JSONObject  | – this subscription as JSON
|`GET /tango/rest/rc6/subscriptions/{id}/event-stream` | text/event-stream  | – events stream
|`DELETE /tango/rest/rc6/subscriptions/{id}`           | NULL  | – closes events stream and cancels subscription

**Get subscription**

`GET /tango/rest/rc6/subscriptions/{id}`

```json
{
  "id":0,
  "events": [
    {
      "id":0,
      "host":"hzgxenvtest:10000",
      "device":"sys/tg_test/1",
      "attribute":"double_scalar",
      "type":"change",
      "rate":100
    },
    {
      "id":1,
      "host":"hzgxenvtest:10000",
      "device":"sys/tg_test/1",
      "attribute":"long_scalar",
      "type":"periodic",
      "rate":3000
    }
  ]
}
```

**Add new event to subscription**

`PUT /tango/rest/rc6/subscriptions/0`

```json
{
  "events": [
    {
      "host":"hzgxenvtest:10000",
      "device":"sys/tg_test/1",
      "attribute":"short_scalar",
      "type":"change",
      "rate":100
    }
  ]
}
```

```json
{
  "id":0,
  "events": [
    {
      "id":2,
      "host":"hzgxenvtest:10000",
      "device":"sys/tg_test/1",
      "attribute":"short_scalar",
      "type":"change",
      "rate":100
    }
  ]
}
```

**Change event rate**

`PUT /tango/rest/rc6/subscriptions/0`

```json
{
  "events": [
    {
      "id":2,
      "rate":1000
    }
  ]
}
```

#### fallback_to_polling

Implementation may provide *fallback_to_polling* configuration option via maintenance interface. If set to true implementation MUST perform client polling in case Tango event subscription has failed. Polling rate is specified for each event.  

### Event stream

Implementation MUST multiplex all events into a single subscription stream:

```
id: <upstream event time>
event: <event id> 
data: <upstream event data as plain text e.g. 3.14>
```

In case of an error:

```
id: <upstream event time or server time when error has occured>
event: error 
data: <event e.g. hzgxenvtest:10000/sys/tg_test/1/double_scalar/change>:<upstream error cause>
```

## Implementations notes

1. Implementation MUST broadcast events from a single upstream
2. Implementation MUST un-subscribe when there is no client for a particular upstream
3. Implementation MAY export reconnection timeout to the configuration
4. Implementation MAY separate subscriptions by client/app/user

## References

[1] [Server sent events overview](https://www.w3schools.com/html/html5_serversentevents.asp)
