#!/bin/bash

# Script is executed after installation
version=$1
chmod a+x $PWD/mosmix-kml-tool.sh
mv $PWD/mosmix-kml-tool.sh $PWD/mosmix-kml-tool-${version}.sh

# Remove script itself after installation
rm $PWD/makeself.sh
