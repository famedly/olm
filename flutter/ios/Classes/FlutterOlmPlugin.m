#import "FlutterOlmPlugin.h"
#if __has_include(<flutter_olm/flutter_olm-Swift.h>)
#import <flutter_olm/flutter_olm-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_olm-Swift.h"
#endif

@implementation FlutterOlmPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterOlmPlugin registerWithRegistrar:registrar];
}
@end
