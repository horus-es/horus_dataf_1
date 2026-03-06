import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'horus_dataf_1_method_channel.dart';

abstract class HorusDataf_1Platform extends PlatformInterface {
  /// Constructs a HorusDataf_1Platform.
  HorusDataf_1Platform() : super(token: _token);

  static final Object _token = Object();

  static HorusDataf_1Platform _instance = MethodChannelHorusDataf_1();

  /// The default instance of [HorusDataf_1Platform] to use.
  ///
  /// Defaults to [MethodChannelHorusDataf_1].
  static HorusDataf_1Platform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [HorusDataf_1Platform] when
  /// they register themselves.
  static set instance(HorusDataf_1Platform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
