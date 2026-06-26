import 'dart:io';
import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:path_provider/path_provider.dart';

Future<String> saveBackupBytesImpl(Uint8List bytes, String fileName) async {
  final safeName = fileName.trim().isEmpty ? 'backup.dump' : fileName.trim();
  final normalizedName = safeName.toLowerCase().endsWith('.dump')
      ? safeName
      : '${safeName.replaceAll(RegExp(r'\\.[^.]+$'), '')}.dump';
  final directory = await getApplicationDocumentsDirectory();
  final selectedPath = await FilePicker.platform.saveFile(
    dialogTitle: 'Guardar respaldo como',
    fileName: normalizedName,
    initialDirectory: directory.path,
    bytes: bytes,
    type: FileType.any,
  );

  if (selectedPath != null) {
    return selectedPath;
  }

  final fallbackPath = '${directory.path}${Platform.pathSeparator}$normalizedName';
  final file = File(fallbackPath);
  await file.writeAsBytes(bytes, flush: true);
  return file.path;
}
