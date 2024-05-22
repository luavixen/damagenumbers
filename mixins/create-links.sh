#!/bin/sh

cd ..

name=damagenumbers

for loader in fabric forge neoforge; do

    if [ -d $loader ]; then

        echo "creating links for $loader:"

        set -o xtrace

        cd $loader/src/main

        cd resources
        rm $name.mixins.json
        ln -s ../../../../mixins/$name.mixins.json $name.mixins.json
        cd ..

        cd java/dev/foxgirl/$name
        rm mixin
        ln -s ../../../../../../../mixins/mixin mixin
        cd ../../../..

        cd ../../..

        set +o xtrace

    fi

done
