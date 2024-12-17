#!/bin/bash

root=$(dirname $0)
$root/jdk/bin/java -jar $root/mosmix-kml-tool-${project.version}.jar $*

