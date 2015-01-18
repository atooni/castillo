#!/bin/bash

set -ev

function checkRC {
  if [ $1 != 0 ]; then 
    printf "Build failed! ($1)\n"
    exit $1
  fi  
}

sbt clean coverage test

sbt coverageReport

sbt coverageAggregate

sbt codacyCoverage

