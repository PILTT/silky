#!/bin/sh

bash <(curl -s https://codecov.io/bash)
sbt coveralls clean compile "+ publishSigned" sonatypeReleaseAll
