[![logo](http://www.tango-controls.org/static/tango/img/logo_tangocontrols.png)](http://www.tango-controls.org)

[![Documentation Status](https://readthedocs.org/projects/tango-rest-api/badge/?version=latest)](http://tango-rest-api.readthedocs.io/en/latest/?badge=latest)

This project is the effort to define a standard REST API for [Tango controls](http://www.tango-controls.org).

REST API is requested by the community and discussed in [this forum thread](http://www.tango-controls.org/community/forum/c/general/development/tango-feature-request-4-defining-a-standard-tango-rest-api/)

# Implementations:

1. [mTangoREST.server](https://bitbucket.org/hzgwpn/mtangorest.server/wiki/Home) [![Build Status](https://travis-ci.org/tango-controls/rest-api.svg)](https://travis-ci.org/tango-controls/rest-api)

# How to run the tests suite #

```BASH
$> git clone https://github.com/tango-controls/rest-api.git 
destination directory: rest-api
requesting all changes
adding changesets
adding manifests
adding file changes
added 1 changesets with 2 changes to 2 files
updating to branch default
2 files updated, 0 files merged, 0 files removed, 0 files unresolved
$> cd rest-api
$> mvn clean test \
    -Dtango.rest.url=http://localhost:8080/hzgcttest/rest \
    -Dtango.rest.auth.method=basic \
    -Dtango.rest.user={user} -Dtango.rest.password={password}

[INFO] Scanning for projects…
[…]
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.518 sec

Results :

Tests run: 13, Failures: 0, Errors: 0, Skipped: 0

[INFO] ————————————————————————
[INFO] BUILD SUCCESS
[INFO] ————————————————————————
[INFO] Total time: 3.289 s
[INFO] Finished at: 2015-12-17T18:40:41+01:00
[INFO] Final Memory: 14M/490M
[INFO] ————————————————————————
$> ^_^
```
