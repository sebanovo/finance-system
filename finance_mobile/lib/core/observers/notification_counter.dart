import 'package:flutter/material.dart';

class NotificationCounter extends ChangeNotifier {
  static final NotificationCounter _instance = NotificationCounter._internal();
  factory NotificationCounter() => _instance;
  NotificationCounter._internal();

  int _count = 0;
  int get count => _count;

  void update(int newCount) {
    _count = newCount;
    notifyListeners();
  }
}
