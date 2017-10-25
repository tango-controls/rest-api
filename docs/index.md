[TOC]

# Tango REST API RC4

There are three parts in this proposal: URL specification; Implementation remarks; Implementation recommendations. The first one names valid URLs that must be handled by the implementation. 
Each URL is presented following this format:

 URL  | RESPONSE TYPE  | NOTE
----- | ---------- | ------
`METHOD url`      |  JSONArray/JSONObject/NULL | short comment          |

Such table is followed by a JSON response's examples block:

`URL`:
`{'JSON response':'example'}`

In two following sections several implementation guidelines are highlighted.

In general API follows standard CRUD idiom:

HTTP verb | CRUD| collection | instance
----------|-----|------------|----------
GET  | READ | Read a list. 200 OK | Read the details of one instance. 200 OK
POST | CREATE | Create a new instance. 201 OK | -
PUT | UPDATE/CREATE | Full Update. 200 OK | Create an instance. 201 Created
DELETE | DELETE | - | Delete instance. 200 OK

POST create an instance of collection by the URI of this collection.
POST returns the URI and the id of the newly created instance.

# URL example driven specification:

All URLs in this section omit protocol//host:port part: `http://host:port`. An implementation may or may not add this to the hrefs. 

For shortness all URLs use `<prefix>` for an API entry point: `/tango/rest/rc3/hosts/tango_host/tango_port`, or omit it completely. So `<prefix>/devices/sys/tg_test/1/attributes` (or `/devices/sys/tg_test/1/attributes`) actually means `/tango/rest/rc3/tango_host/tango_port/devices/sys/tg_test/1/attributes`, where _tango_host_ is a Tango host name, e.g. _hzgxenvtest_; _tango_port_ is a Tango database port number, e.g. _10000_.

_tango_host_ and _tango_port_ are not known in advance, as user may ask for an arbitrary Tango database. The database to which implementation connects at start can be specified via environmental variable, or any other way. 