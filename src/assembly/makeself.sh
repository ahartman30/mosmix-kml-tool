#!/bin/bash

# Script is executed after installation
version=$1
chmod a+x $PWD/mosmix-kml-tool.sh
chmod a+x $PWD/mosmix2json/mosmix2json.py

# Remove script itself after installation
rm $PWD/makeself.sh
