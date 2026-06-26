import 'package:flutter/material.dart';

import '../../domain/entities/journal_entry.dart';
import '../../domain/usecases/get_journal_entries_usecase.dart';
import '../../domain/usecases/get_journal_entry_by_id_usecase.dart';

class JournalEntriesViewModel extends ChangeNotifier {
  final GetJournalEntriesUseCase getJournalEntriesUseCase;
  final GetJournalEntryByIdUseCase getJournalEntryByIdUseCase;

  JournalEntriesViewModel({
    required this.getJournalEntriesUseCase,
    required this.getJournalEntryByIdUseCase,
  });

  List<JournalEntry> _items = [];
  bool _loading = false;
  bool _loadingDetail = false;
  String? _errorMessage;
  int _page = 0;
  final int _size = 20;
  int _totalPages = 0;
  int _totalElements = 0;
  JournalEntry? _selectedEntry;

  List<JournalEntry> get items => _items;
  bool get loading => _loading;
  bool get loadingDetail => _loadingDetail;
  String? get errorMessage => _errorMessage;
  int get page => _page;
  int get size => _size;
  int get totalPages => _totalPages;
  int get totalElements => _totalElements;
  bool get hasPreviousPage => _page > 0;
  bool get hasNextPage => _page + 1 < _totalPages;
  JournalEntry? get selectedEntry => _selectedEntry;

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  void clearSelectedEntry() {
    _selectedEntry = null;
    notifyListeners();
  }

  Future<void> loadEntries({int page = 0}) async {
    _loading = true;
    _page = page;
    _errorMessage = null;
    notifyListeners();
    try {
      final result = await getJournalEntriesUseCase(page: page, size: _size);
      _items = result.items;
      _totalPages = result.totalPages;
      _totalElements = result.totalElements;
      _page = result.number;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<JournalEntry?> loadEntryById(String id) async {
    _loadingDetail = true;
    _errorMessage = null;
    notifyListeners();
    try {
      final entry = await getJournalEntryByIdUseCase(id);
      _selectedEntry = entry;
      return entry;
    } catch (e) {
      _errorMessage = e.toString();
      return null;
    } finally {
      _loadingDetail = false;
      notifyListeners();
    }
  }
}
