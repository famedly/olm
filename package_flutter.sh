#!/bin/sh -e
rm -rf publishing/flutter
mkdir -p publishing
cp -rL flutter publishing/flutter

sed "$(for i in MAJOR MINOR PATCH; do
v=$(sed -n '/'$i' *=/{s/^[^=]*=//;/^[0-9 ]*$/p;q;}' OLMKit.podspec)
[ -n "$v" ] && echo 's/^\([ \t]*'$i' *=\).*$/\1'"$v/"
done)" <flutter/ios/flutter_olm.podspec >publishing/flutter/ios/flutter_olm.podspec

sed '/^publish_to: none/d' <flutter/pubspec.yaml >publishing/flutter/pubspec.yaml
