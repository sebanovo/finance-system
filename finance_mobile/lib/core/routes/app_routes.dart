import 'package:finance_mobile/presentation/pages/account_detail_page.dart';
import 'package:finance_mobile/presentation/pages/accounts_page.dart';
import 'package:finance_mobile/presentation/pages/create_deposit_page.dart';
import 'package:finance_mobile/presentation/pages/create_payment_page.dart';
import 'package:finance_mobile/presentation/pages/create_transfer_page.dart';
import 'package:finance_mobile/presentation/pages/create_withdrawal_page.dart';
import 'package:finance_mobile/presentation/pages/transaction_detail_page.dart';
import 'package:finance_mobile/presentation/pages/transactions_page.dart';
import 'package:go_router/go_router.dart';
import 'package:finance_mobile/presentation/pages/home_page.dart';
import 'package:finance_mobile/presentation/pages/permissions_pages.dart';
import 'package:finance_mobile/presentation/pages/roles_pages.dart';
import 'package:finance_mobile/presentation/pages/login_page.dart';
import 'package:finance_mobile/presentation/pages/reset_password_page.dart';
import 'package:finance_mobile/presentation/pages/signup_page.dart';
import 'package:finance_mobile/presentation/pages/forgot_password_page.dart';
import 'package:finance_mobile/presentation/pages/users_page.dart';

final appRouter = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(path: '/login', builder: (context, _) => const LoginPage()),
    GoRoute(path: '/signup', builder: (context, _) => const SignupPage()),
    GoRoute(path: '/home', builder: (context, _) => const HomePage()),
    GoRoute(
      path: '/forgot-password',
      builder: (context, _) => const ForgotPasswordPage(),
    ),
    GoRoute(path: '/users', builder: (context, _) => const UsersPage()),
    GoRoute(path: '/roles', builder: (context, _) => const RolesPage()),
    GoRoute(
      path: '/permissions',
      builder: (context, _) => const PermissionsPage(),
    ),
    GoRoute(
      path: '/reset-password',
      builder: (context, state) {
        final tenant = state.extra as String?;
        return ResetPasswordPage(initialTenant: tenant);
      },
    ),
    GoRoute(path: '/accounts', builder: (context, _) => const AccountsPage()),
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
      path: '/transactions/:id',
      builder: (context, state) {
        final id = state.pathParameters['id']!;
        return TransactionDetailPage(transactionId: id);
      },
    ),
  ],
);
