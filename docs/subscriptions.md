[TOC]

## Subscriptions

Provides an entry point for subscriptions (Tango Controls event system)

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc6/subscriptions`              | JSONArray  | – all subscriptions optionally associated with this client
|`POST /tango/rest/rc6/subscriptions`             | JSONArray | – creates a new subscription  
|`PUT /tango/rest/rc6/subscriptions?id={id1}&id={id2}&...`             | JSONArray | – updates existing subscriptions identified by id
|`DELETE /tango/rest/rc6/subscriptions?id={id1}&id={id2}&...`         | NULL  | – deletes subscription(s) identified by id

`POST /tango/rest/rc6/subscriptions`
```json
[
  {
    "clientId":"appGUID",
    "name":"my double scalar subscription",
    "retry":10000,
    "target": {
      "host":"hzgxenvtest:10000",
      "device":"sys/tg_test/1",
      "channel":"double_scalar",
      "type":"change"     
    }
  }
]
```

---
```json
[
  {
    "id":1
  }
]
```

### Subscription

Represents single subscription

| URL                                        | Response           | Desc
|-----------------------------------------|------------|--------------------------
|`GET /tango/rest/rc6/subscriptions/{id}`              | text/event-stream  | – events stream
|`DELETE /tango/rest/rc6/subscriptions/{id}`              | NULL  | – closes events stream and cancels subscription

# Implementations notes

1) Implementation MUST broadcast events from single upstream
2) Implementation MUST un-subscribe when there is no client for a particular target 
2) Implementation MAY separate subscriptions by client/app/user