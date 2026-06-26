import 'package:finance_mobile/presentation/pages/account_detail_page.dart';
import 'package:finance_mobile/presentation/pages/accounts_page.dart';
import 'package:finance_mobile/presentation/pages/create_deposit_page.dart';
import 'package:finance_mobile/presentation/pages/create_hold_page.dart';
import 'package:finance_mobile/presentation/pages/create_payment_page.dart';
import 'package:finance_mobile/presentation/pages/create_release_page.dart';
import 'package:finance_mobile/presentation/pages/create_transfer_page.dart';
import 'package:finance_mobile/presentation/pages/create_withdrawal_page.dart';
import 'package:finance_mobile/presentation/pages/create_qr_charge_page.dart';
import 'package:finance_mobile/presentation/pages/devices_page.dart';
import 'package:finance_mobile/presentation/pages/limits_page.dart';
import 'package:finance_mobile/presentation/pages/notification_preferences_page.dart';
import 'package:finance_mobile/presentation/pages/notifications_page.dart';
import 'package:finance_mobile/presentation/pages/backups_page.dart';
import 'package:finance_mobile/presentation/pages/service_payments_page.dart';
import 'package:finance_mobile/presentation/pages/qr_payment_page.dart';
import 'package:finance_mobile/presentation/pages/transaction_detail_page.dart';
import 'package:finance_mobile/presentation/pages/transactions_page.dart';
import 'package:finance_mobile/core/routes/app_route_observer.dart';
import 'package:finance_mobile/core/di/injection_container.dart' as di;
import 'package:finance_mobile/core/network/api_client.dart';
import 'package:go_router/go_router.dart';
import 'package:finance_mobile/presentation/pages/home_page.dart';
import 'package:finance_mobile/presentation/pages/profile_page.dart';
import 'package:finance_mobile/presentation/pages/permissions_pages.dart';
import 'package:finance_mobile/presentation/pages/roles_pages.dart';
import 'package:finance_mobile/presentation/pages/login_page.dart';
import 'package:finance_mobile/presentation/pages/reset_password_page.dart';
import 'package:finance_mobile/presentation/pages/verify_email_page.dart';
import 'package:finance_mobile/presentation/pages/my_loans_page.dart';
import 'package:finance_mobile/presentation/pages/signup_page.dart';
import 'package:finance_mobile/presentation/pages/forgot_password_page.dart';
import 'package:finance_mobile/presentation/pages/users_page.dart';

final GoRouter appRouter = GoRouter(
  initialLocation: _initialLocation(),
  observers: [appRouteObserver],
  refreshListenable: di.sl<ApiClient>(),
  redirect: (context, state) {
    final apiClient = di.sl<ApiClient>();
    final loggedIn = apiClient.hasSession;
    final onPublicRoute = _publicRoutes.contains(state.matchedLocation);
    final onClientOnlyRoute = _clientOnlyRoutes.contains(state.matchedLocation);
    final onServiceOnlyRoute = _serviceOnlyRoutes.contains(state.matchedLocation);
    final onOwnerOnlyRoute = _ownerOnlyRoutes.contains(state.matchedLocation);

    if (!loggedIn && !onPublicRoute) {
      return '/login';
    }

    if (loggedIn && onPublicRoute) {
      return '/home';
    }

    if (loggedIn && onClientOnlyRoute) {
      final hasClientContext = apiClient.hasAnyPermissionPrefix('me.');
      if (apiClient.isOwnerAdmin || !hasClientContext) {
        return '/home';
      }
    }

    if (loggedIn && onServiceOnlyRoute) {
      final hasServiceContext = apiClient.hasAnyPermissionPrefix('me.service-');
      if (!apiClient.isOwnerAdmin && !hasServiceContext) {
        return '/home';
      }
    }

    if (loggedIn && onOwnerOnlyRoute && !apiClient.isOwnerAdmin) {
      return '/home';
    }

    return null;
  },
  routes: [
    GoRoute(path: '/login', builder: (context, _) => const LoginPage()),
    GoRoute(path: '/signup', builder: (context, _) => const SignupPage()),
    GoRoute(path: '/home', builder: (context, _) => const HomePage()),
    GoRoute(
      path: '/forgot-password',
      builder: (context, _) => const ForgotPasswordPage(),
    ),
    GoRoute(path: '/users', builder: (context, _) => const UsersPage()),
    GoRoute(path: '/profile', builder: (context, _) => const ProfilePage()),
    GoRoute(path: '/roles', builder: (context, _) => const RolesPage()),
    GoRoute(
      path: '/permissions',
      builder: (context, _) => const PermissionsPage(),
    ),
    GoRoute(
      path: '/reset-password',
      builder: (context, state) {
        final query = state.uri.queryParameters;
        final extraTenant = state.extra as String?;
        return ResetPasswordPage(
          initialTenant: query['tenant'] ?? extraTenant,
          token: query['token'],
        );
      },
    ),
    GoRoute(
      path: '/activate-account',
      builder: (context, state) {
        final query = state.uri.queryParameters;
        final extraTenant = state.extra as String?;
        return ActivateAccountPage(
          initialTenant: query['tenant'] ?? extraTenant,
          token: query['token'],
        );
      },
    ),
    GoRoute(
      path: '/verify-email',
      builder: (context, state) {
        final query = state.uri.queryParameters;
        final extraTenant = state.extra as String?;
        return ActivateAccountPage(
          initialTenant: query['tenant'] ?? extraTenant,
          token: query['token'],
        );
      },
    ),
    GoRoute(
      path: '/activate',
      builder: (context, state) {
        final query = state.uri.queryParameters;
        final extraTenant = state.extra as String?;
        return ActivateAccountPage(
          initialTenant: query['tenant'] ?? extraTenant,
          token: query['token'],
        );
      },
    ),
    GoRoute(path: '/accounts', builder: (context, _) => const AccountsPage()),
    GoRoute(path: '/loans', builder: (context, _) => const MyLoansPage()),
    GoRoute(
      path: '/accounts/:id',
      builder: (context, state) {
        final id = state.pathParameters['id']!;
        return AccountDetailPage(accountId: id);
      },
    ),
    GoRoute(
      path: '/transactions',
      builder: (context, _) => const TransactionsPage(),
    ),
    GoRoute(
      path: '/transactions/deposit',
      builder: (context, _) => const CreateDepositPage(),
    ),
    GoRoute(
      path: '/transactions/transfer',
      builder: (context, _) => const CreateTransferPage(),
    ),
    GoRoute(
      path: '/transactions/withdrawal',
      builder: (context, _) => const CreateWithdrawalPage(),
    ),
    GoRoute(
      path: '/transactions/payment',
      builder: (context, _) => const CreatePaymentPage(),
    ),
    GoRoute(
      path: '/transactions/qr/charge',
      builder: (context, _) => const CreateQrChargePage(),
    ),
    GoRoute(
      path: '/transactions/qr/pay',
      builder: (context, _) => const QrPaymentPage(),
    ),
    GoRoute(
      path: '/transactions/hold',
      builder: (context, _) => const CreateHoldPage(),
    ),
    GoRoute(
      path: '/transactions/release',
      builder: (context, _) => const CreateReleasePage(),
    ),
    GoRoute(
      path: '/transactions/:id',
      builder: (context, state) {
        final id = state.pathParameters['id']!;
        return TransactionDetailPage(transactionId: id);
      },
    ),
    GoRoute(
      path: '/notifications',
      builder: (context, _) => const NotificationsPage(),
    ),
    GoRoute(
      path: '/service-payments',
      builder: (context, _) => const ServicePaymentsPage(),
    ),
    GoRoute(
      path: '/backups',
      builder: (context, _) => const BackupsPage(),
    ),
    GoRoute(path: '/limits', builder: (context, _) => const LimitsPage()),
    GoRoute(path: '/devices', builder: (context, _) => const DevicesPage()),
    GoRoute(
      path: '/notification-preferences',
      builder: (context, _) => const NotificationPreferencesPage(),
    ),
  ],
);

const Set<String> _publicRoutes = {
  '/login',
  '/signup',
  '/forgot-password',
  '/reset-password',
  '/activate-account',
  '/verify-email',
  '/activate',
};

const Set<String> _clientOnlyRoutes = {
  '/notifications',
  '/devices',
  '/notification-preferences',
};

const Set<String> _serviceOnlyRoutes = {
  '/service-payments',
};

const Set<String> _ownerOnlyRoutes = {
  '/backups',
};

String _initialLocation() {
  final apiClient = di.sl<ApiClient>();
  return apiClient.hasSession ? '/home' : '/login';
}
