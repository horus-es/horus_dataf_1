import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'horus_dataf_1_platform_interface.dart';

/// An implementation of [HorusDataf_1Platform] that uses method channels.
class MethodChannelHorusDataf_1 extends HorusDataf_1Platform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('horus_dataf_1');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
