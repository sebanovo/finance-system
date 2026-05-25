// lib/presentation/pages/notifications_page.dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/notifications_viewmodel.dart';
import '../widgets/notification_item.dart';
import '../widgets/notification_item_skeleton.dart';
import '../widgets/notifications_empty_widget.dart';
import '../widgets/notification_options_modal.dart';

class NotificationsPage extends StatefulWidget {
  const NotificationsPage({super.key});

  @override
  State<NotificationsPage> createState() => _NotificationsPageState();
}

class _NotificationsPageState extends State<NotificationsPage> {
  late NotificationsViewModel _viewModel;
  final ScrollController _scrollController = ScrollController();
  static const int _skeletonItemCount = 5;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<NotificationsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadInitial();
    _viewModel.loadUnreadCount(); // ✅ Cargar contador inicial
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
    await _viewModel.loadUnreadCount(); // ✅ Actualizar contador al refrescar
  }

  // ✅ Método para marcar como leída y actualizar contador
  Future<void> _markAsRead(String id) async {
    await _viewModel.markAsRead(id);
    await _viewModel.loadUnreadCount();
    setState(() {});
  }

  // ✅ Método para marcar todas como leídas y actualizar contador
  Future<void> _markAllAsRead() async {
    await _viewModel.markAllAsRead();
    await _viewModel.loadUnreadCount();
    setState(() {});
  }

  // ✅ Método para archivar y actualizar contador
  Future<void> _archive(String id) async {
    await _viewModel.archive(id);
    await _viewModel.loadUnreadCount();
    setState(() {});
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
              onPressed: _markAllAsRead,
            ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading && _viewModel.notifications.isEmpty) {
      return ListView.builder(
        padding: const EdgeInsets.symmetric(vertical: 8),
        itemCount: _skeletonItemCount,
        itemBuilder: (context, index) => const NotificationItemSkeleton(),
      );
    }

    if (_viewModel.errorMessage != null && _viewModel.notifications.isEmpty) {
      return _buildErrorWidget();
    }

    if (_viewModel.notifications.isEmpty) {
      return const NotificationsEmptyWidget();
    }

    return RefreshIndicator(
      onRefresh: _onRefresh,
      color: const Color(0xFF2E7D32),
      child: ListView.builder(
        controller: _scrollController,
        padding: const EdgeInsets.symmetric(vertical: 8),
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
          return NotificationItem(
            notification: notif,
            onTap: () async {
              if (notif.isUnread) {
                await _markAsRead(notif.id);
              }
              if (notif.actionUrl != null && notif.actionUrl!.isNotEmpty) {
                if (mounted) {
                  context.push(notif.actionUrl!.replaceFirst('/api/me', ''));
                }
              }
            },
            onLongPress: () {
              _showOptionsModal(notif.id);
            },
          );
        },
      ),
    );
  }

  Widget _buildErrorWidget() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.error_outline, size: 64, color: Colors.red.shade300),
          const SizedBox(height: 16),
          Text(_viewModel.errorMessage!, textAlign: TextAlign.center),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: () async {
              await _viewModel.loadInitial();
              await _viewModel.loadUnreadCount();
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2E7D32),
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(30),
              ),
            ),
            child: const Text('Reintentar'),
          ),
        ],
      ),
    );
  }

  void _showOptionsModal(String id) {
    showModalBottomSheet(
      context: context,
      builder: (context) => NotificationOptionsModal(
        notificationId: id,
        onArchive: () => _archive(id),
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
