import 'dart:typed_data';

import 'backup_file_saver_io.dart'
    if (dart.library.html) 'backup_file_saver_web.dart';

Future<String> saveBackupBytes(Uint8List bytes, String fileName) {
  return saveBackupBytesImpl(bytes, fileName);
}
