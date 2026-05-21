import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/notifications_viewmodel.dart';

class NotificationsPage extends StatefulWidget {
  const NotificationsPage({super.key});

  @override
  State<NotificationsPage> createState() => _NotificationsPageState();
}

class _NotificationsPageState extends State<NotificationsPage> {
  late NotificationsViewModel _viewModel;
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<NotificationsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadInitial();
    _scrollController.addListener(_onScroll);
  }

  void _onViewModelChanged() {
    setState(() {});
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      _viewModel.loadMore();
    }
  }

  Future<void> _onRefresh() async {
    await _viewModel.loadInitial();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Notificaciones'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
        actions: [
          if (_viewModel.unreadCount > 0)
            IconButton(
              icon: const Icon(Icons.done_all),
              tooltip: 'Marcar todas como leídas',
              onPressed: () async {
                await _viewModel.markAllAsRead();
              },
            ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading && _viewModel.notifications.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_viewModel.errorMessage != null && _viewModel.notifications.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 64, color: Colors.red.shade300),
            const SizedBox(height: 16),
            Text(_viewModel.errorMessage!, textAlign: TextAlign.center),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => _viewModel.loadInitial(),
              child: const Text('Reintentar'),
            ),
          ],
        ),
      );
    }
    if (_viewModel.notifications.isEmpty) {
      return const Center(child: Text('No hay notificaciones'));
    }
    return RefreshIndicator(
      onRefresh: _onRefresh,
      child: ListView.builder(
        controller: _scrollController,
        itemCount:
            _viewModel.notifications.length + (_viewModel.loadingMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == _viewModel.notifications.length) {
            return const Padding(
              padding: EdgeInsets.symmetric(vertical: 16),
              child: Center(child: CircularProgressIndicator()),
            );
          }
          final notif = _viewModel.notifications[index];
          return Card(
            margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            color: notif.isUnread ? const Color(0xFFF1F8E9) : Colors.white,
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: notif.isUnread
                    ? const Color(0xFF2E7D32)
                    : Colors.grey.shade300,
                child: Icon(
                  _getIconForCategory(notif.category),
                  color: Colors.white,
                  size: 20,
                ),
              ),
              title: Text(
                notif.title,
                style: TextStyle(
                  fontWeight: notif.isUnread
                      ? FontWeight.bold
                      : FontWeight.normal,
                ),
              ),
              subtitle: Text(
                notif.body,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              trailing: notif.isUnread
                  ? Container(
                      width: 8,
                      height: 8,
                      decoration: const BoxDecoration(
                        color: Color(0xFF2E7D32),
                        shape: BoxShape.circle,
                      ),
                    )
                  : null,
              onTap: () async {
                if (notif.isUnread) {
                  await _viewModel.markAsRead(notif.id);
                }
                if (notif.actionUrl != null && notif.actionUrl!.isNotEmpty) {
                  // Navegar a la ruta indicada (por ej. /transactions/:id)
                  context.push(notif.actionUrl!.replaceFirst('/api/me', ''));
                }
              },
              onLongPress: () {
                _showOptionsModal(notif.id);
              },
            ),
          );
        },
      ),
    );
  }

  IconData _getIconForCategory(String category) {
    switch (category) {
      case 'TRANSACTIONS':
        return Icons.payment;
      case 'SECURITY':
        return Icons.security;
      default:
        return Icons.notifications;
    }
  }

  void _showOptionsModal(String id) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.archive_outlined),
              title: const Text('Archivar'),
              onTap: () async {
                Navigator.pop(context);
                await _viewModel.archive(id);
              },
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    _scrollController.dispose();
    super.dispose();
  }
}
