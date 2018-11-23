[![logo](http://www.tango-controls.org/static/tango/img/logo_tangocontrols.png)](http://www.tango-controls.org)

[![Documentation Status](https://readthedocs.org/projects/tango-rest-api/badge/?version=latest)](http://tango-rest-api.readthedocs.io/en/latest/?badge=latest)

This project is the effort to define a standard REST API for [Tango controls](http://www.tango-controls.org).

REST API is requested by the community and discussed in [this forum thread](http://www.tango-controls.org/community/forum/c/general/development/tango-feature-request-4-defining-a-standard-tango-rest-api/)

Please read about Tango REST API in the Tango documentation: [link](http://tango-controls.readthedocs.io/en/latest/development/advanced/rest-api.html)

# Reference implementations

Reference implementations provide request/response entities definition:

Platform  | Link        | Compatibility
----------|-------------|---------
Java      | [rest-api-java](https://github.com/tango-controls/rest-api-java) | rc4, v1.0


# Known implementations

Listed implementations provide REST server capabilities for a given platform.  

Platform  | Link        | Compatibility
----------|-------------|---------
Java      | [rest-server](https://github.com/tango-controls/rest-server) | rc4, v1.0
C++       | [RestDS](http://tangodevel.jinr.ru/git/tango/web/RestDS) | rc4 (partial) 
Python    | [mtango-py](https://github.com/MaxIV-KitsControls/mtango-py) | rc3 (partial)

# Test suites

Test suites provide integration tests for implementations to verify their compatibility with a given Tango REST API version

Platform  | Link        | Compatibility
----------|-------------|---------
Java      | [rest-test-suite](https://github.com/tango-controls/rest-test-suite) | rc4, v1.0
