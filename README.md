[![logo](http://www.tango-controls.org/static/tango/img/logo_tangocontrols.png)](http://www.tango-controls.org)

[![Documentation Status](https://readthedocs.org/projects/tango-rest-api/badge/?version=latest)](http://tango-rest-api.readthedocs.io/en/latest/?badge=latest)

This project is the effort to define a standard REST API for [Tango controls](http://www.tango-controls.org).

REST API is requested by the community and discussed in [this forum thread](http://www.tango-controls.org/community/forum/c/general/development/tango-feature-request-4-defining-a-standard-tango-rest-api/)

Please read about Tango REST API in the Tango documentation: [link](http://tango-controls.readthedocs.io/en/latest/development/advanced/rest-api.html)

# Tango REST API specification

Specification defines RESTful view on Tango Controls in terms of REST resources. Each version defines allowed requests and corresponding responses to/from Tango REST API implementation. Also each version defines expected behaviour of the implementation in a given situation/feature.

Version  | Link
----------|-------------
v1.1 (v1.0 + Subscriptions)     | [rest-api-v1.1](https://tango-rest-api.readthedocs.io/en/v1.1/)
v1.0 (aka rc5+rc6)      | [rest-api-v1.0](https://tango-rest-api.readthedocs.io/en/v1.0/)
rc4       | [rest-api-rc4](https://tango-rest-api.readthedocs.io/en/rc4_a/)
rc3       | [rest-api-rc3](https://tango-rest-api.readthedocs.io/en/rc3/)
rc2       | [rest-api-rc2](https://github.com/tango-controls/rest-api/blob/rc2/Home.md)
rc1       | [rest-api-rc1](https://github.com/tango-controls/rest-api/blob/rc1/Home.md)


# Reference client implementations

Reference implementations provide request/response entities definition:

Platform  | Link        | Compatibility
----------|-------------|---------
Java      | [rest-api-java](https://github.com/tango-controls/rest-api-java) | rc4, v1.1
ES6, Browser      | [tango-rest-client](https://github.com/waltz-controls/tango-rest-client) | v1.1


# Known server implementations

Listed implementations provide REST server capabilities for a given platform.  

Platform  | Link        | Compatibility
----------|-------------|---------
Java      | [rest-server](https://github.com/tango-controls/rest-server) | rc4 + subscriptions, v1.1
C++       | [RestDS](http://tangodevel.jinr.ru/git/tango/web/RestDS) | rc4 (partial) 
Python    | [mtango-py](https://github.com/MaxIV-KitsControls/mtango-py) | rc3 (partial)

# Test suites

Test suites provide integration tests for implementations to verify their compatibility with a given Tango REST API version

Platform  | Link        | Compatibility
----------|-------------|---------
Java      | [rest-test-suite](https://github.com/tango-controls/rest-test-suite) | rc4 (partial), v1.1
