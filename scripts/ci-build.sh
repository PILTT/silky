#!/bin/sh

sbt clean coverage test "+ publishSigned" sonatypeReleaseAll
