## 1.4.1

Reverts the CMakeList.txt change in 1.4.0. This change was done to fix the git
build on Windows without symlink support enabled. It however broke the android
release build everywhere. As such the change is reverted and a proper fix (for
integrating the git package directly) will come in the future.

## 1.4.0

- chore: upgrade Android project to latest gradle (The one with the braid)
- fix: use correct CMakeLists.txt file for Android (The one with the braid)

## 1.3.2

- chore: create release on tag and upload olm there (td)

## 1.3.1

- fix a build issue which broke the iOS version of 1.3.0.

## 1.3.0

- update olm to 3.2.15
- chore(flutter): bump Kotlin Version to 1.5.20 (brainwo)
- Add github workflows for publishing and bump sdk

## 1.2.0

- update olm 3.2.3 to 3.2.9
- update to dart 2.12

## 1.1.2

- update olm 3.2.2 to 3.2.3

## 1.1.1

- update olm 3.2.1 to 3.2.2

## 1.1.0

- update olm 3.1.5 to 3.2.1
- build olm from source on ios too

## 1.0.1

- fix build on android

## 1.0.0

- initial release
