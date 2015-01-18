#!/bin/sh
sbt clean coverage test
sbt coverageReport
sbt coverageAggregate
sbt codacyCoverage

