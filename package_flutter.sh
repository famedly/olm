#!/bin/sh -e
rm -rf build/flutter
mkdir -p build
cp -rL flutter build/flutter

sed "$(for i in MAJOR MINOR PATCH; do
v=$(sed -n '/'$i' *=/{s/^[^=]*=//;/^[0-9 ]*$/p;q;}' OLMKit.podspec)
[ -n "$v" ] && echo 's/^\([ \t]*'$i' *=\).*$/\1'"$v/"
done)" <flutter/ios/flutter_olm.podspec >build/flutter/ios/flutter_olm.podspec

sed '/^publish_to: none/d' <flutter/pubspec.yaml >build/flutter/pubspec.yaml
