# Assignment 01

## How to run tests

```
$ git clone https://github.com/diptadas/ejm-samples.git
$ cd ejm-samples/chapter4
$ mvn clean test
```

## Changes

- Added tests for update and delete in [CategoryResourceTest](admin/src/test/java/ejm/admin/CategoryResourceTest.java).
- Added client methods in [AdminClient](admin-client/src/main/java/ejm/adminclient/AdminClient.java) 
and pact tests in [ConsumerPactTest](admin-client/src/test/java/ejm/adminclient/ConsumerPactTest.java) for get-all, create, update and delete.
