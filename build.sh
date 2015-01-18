#!/bin/sh

function checkRC {
  if [ $1 != 0 ]; then 
    printf "Build failed! ($1)\n"
    exit $1
  fi  
}

sbt clean coverage test
checkRC $?

sbt coverageReport
checkRC $?

sbt coverageAggregate
checkRC $?

sbt codacyCoverage
checkRC $?

