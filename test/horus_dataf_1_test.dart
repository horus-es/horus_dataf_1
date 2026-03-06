import 'package:flutter_test/flutter_test.dart';
import 'package:horus_dataf_1/horus_dataf_1.dart';
import 'package:horus_dataf_1/horus_dataf_1_platform_interface.dart';
import 'package:horus_dataf_1/horus_dataf_1_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHorusDataf_1Platform
    with MockPlatformInterfaceMixin
    implements HorusDataf_1Platform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HorusDataf_1Platform initialPlatform = HorusDataf_1Platform.instance;

  test('$MethodChannelHorusDataf_1 is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHorusDataf_1>());
  });

  test('getPlatformVersion', () async {
    HorusDataf_1 horusDataf_1Plugin = HorusDataf_1();
    MockHorusDataf_1Platform fakePlatform = MockHorusDataf_1Platform();
    HorusDataf_1Platform.instance = fakePlatform;

    expect(await horusDataf_1Plugin.getPlatformVersion(), '42');
  });
}
