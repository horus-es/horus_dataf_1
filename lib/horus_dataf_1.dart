
import 'horus_dataf_1_platform_interface.dart';

class HorusDataf_1 {
  Future<String?> getPlatformVersion() {
    return HorusDataf_1Platform.instance.getPlatformVersion();
  }
}
