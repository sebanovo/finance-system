import 'dart:typed_data';

import 'package:universal_html/html.dart' as html;

Future<String> saveBackupBytesImpl(Uint8List bytes, String fileName) async {
  final blob = html.Blob([bytes]);
  final url = html.Url.createObjectUrlFromBlob(blob);
  final safeName = fileName.trim().isEmpty ? 'backup.dump' : fileName.trim();
  final normalizedName = safeName.toLowerCase().endsWith('.dump')
      ? safeName
      : '${safeName.replaceAll(RegExp(r'\\.[^.]+$'), '')}.dump';
  final anchor = html.AnchorElement(href: url)
    ..download = normalizedName
    ..style.display = 'none';

  html.document.body?.append(anchor);
  anchor.click();
  anchor.remove();
  html.Url.revokeObjectUrl(url);
  return normalizedName;
}
