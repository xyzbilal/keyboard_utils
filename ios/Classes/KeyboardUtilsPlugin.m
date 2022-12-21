#import "KeyboardUtilsPlugin.h"
#import <keyboard_utils_fork/keyboard_utils_fork-Swift.h>

@implementation KeyboardUtilsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftKeyboardUtilsPlugin registerWithRegistrar:registrar];
}
@end
