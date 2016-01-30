# How to run this tests suite #

```
#!BASH
$> hg clone https://bitbucket.org/Ingvord/tango-rest-api 
destination directory: tango-rest-api
requesting all changes
adding changesets
adding manifests
adding file changes
added 1 changesets with 2 changes to 2 files
updating to branch default
2 files updated, 0 files merged, 0 files removed, 0 files unresolved
$> cd tango-rest-api
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