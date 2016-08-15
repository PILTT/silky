#!/bin/sh

pip install --user codecov && codecov
sbt coverageReport coveralls clean compile "+ publishSigned" sonatypeReleaseAll
