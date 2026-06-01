import 'package:flutter/material.dart';
import '../../../domain/usecases/get_transactions_usecase.dart';
import '../../../domain/usecases/get_transaction_by_id_usecase.dart';
import '../../../domain/usecases/create_deposit_usecase.dart';
import '../../../domain/usecases/create_hold_usecase.dart';
import '../../../domain/usecases/create_qr_transaction_intent_usecase.dart';
import '../../../domain/usecases/cancel_qr_transaction_intent_usecase.dart';
import '../../../domain/usecases/confirm_qr_transaction_usecase.dart';
import '../../../domain/usecases/create_payment_usecase.dart';
import '../../../domain/usecases/create_release_usecase.dart';
import '../../../domain/usecases/create_transfer_usecase.dart';
import '../../../domain/usecases/create_withdrawal_usecase.dart';
import '../../../domain/usecases/get_qr_transaction_intent_usecase.dart';
import '../../../domain/entities/transaction.dart';
import '../../../domain/entities/deposit_request.dart';
import '../../../domain/entities/confirm_qr_transaction_request.dart';
import '../../../domain/entities/hold_request.dart';
import '../../../domain/entities/payment_request.dart';
import '../../../domain/entities/qr_transaction_intent.dart';
import '../../../domain/entities/qr_transaction_intent_request.dart';
import '../../../domain/entities/release_request.dart';
import '../../../domain/entities/transfer_request.dart';
import '../../../domain/entities/withdrawal_request.dart';

class TransactionsViewModel extends ChangeNotifier {
  final GetTransactionsUseCase getTransactionsUseCase;
  final GetTransactionByIdUseCase getTransactionByIdUseCase;
  final CreateDepositUseCase createDepositUseCase;
  final CreateHoldUseCase createHoldUseCase;
  final CreateQrTransactionIntentUseCase createQrTransactionIntentUseCase;
  final GetQrTransactionIntentUseCase getQrTransactionIntentUseCase;
  final CancelQrTransactionIntentUseCase cancelQrTransactionIntentUseCase;
  final ConfirmQrTransactionUseCase confirmQrTransactionUseCase;
  final CreatePaymentUseCase createPaymentUseCase;
  final CreateReleaseUseCase createReleaseUseCase;
  final CreateTransferUseCase createTransferUseCase;
  final CreateWithdrawalUseCase createWithdrawalUseCase;

  List<Transaction> _transactions = [];
  Transaction? _selectedTransaction;
  bool _loading = false;
  bool _creating = false;
  bool _qrProcessing = false;
  String? _errorMessage;
  bool _transactionCreated = false;
  QrTransactionIntent? _qrIntent;
  Transaction? _qrConfirmedTransaction;
  String? _qrErrorMessage;

  TransactionsViewModel({
    required this.getTransactionsUseCase,
    required this.getTransactionByIdUseCase,
    required this.createDepositUseCase,
    required this.createHoldUseCase,
    required this.createQrTransactionIntentUseCase,
    required this.getQrTransactionIntentUseCase,
    required this.cancelQrTransactionIntentUseCase,
    required this.confirmQrTransactionUseCase,
    required this.createPaymentUseCase,
    required this.createReleaseUseCase,
    required this.createTransferUseCase,
    required this.createWithdrawalUseCase,
  });

  List<Transaction> get transactions => _transactions;
  Transaction? get selectedTransaction => _selectedTransaction;
  bool get loading => _loading;
  bool get creating => _creating;
  bool get qrProcessing => _qrProcessing;
  String? get errorMessage => _errorMessage;
  bool get transactionCreated => _transactionCreated;
  QrTransactionIntent? get qrIntent => _qrIntent;
  Transaction? get qrConfirmedTransaction => _qrConfirmedTransaction;
  String? get qrErrorMessage => _qrErrorMessage;

  void clearTransactionCreated() {
    _transactionCreated = false;
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  void clearQrState() {
    _qrIntent = null;
    _qrConfirmedTransaction = null;
    _qrErrorMessage = null;
    _qrProcessing = false;
    notifyListeners();
  }

  Future<void> loadTransactions() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _transactions = await getTransactionsUseCase();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> loadTransactionById(String id) async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _selectedTransaction = await getTransactionByIdUseCase(id);
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> createQrIntent({
    required String targetAccountId,
    required double amount,
    required String currency,
    required String idempotencyKey,
    String? externalReference,
    String? description,
  }) async {
    _qrProcessing = true;
    _qrErrorMessage = null;
    _qrIntent = null;
    _qrConfirmedTransaction = null;
    notifyListeners();

    try {
      final request = QrTransactionIntentRequest(
        targetAccountId: targetAccountId,
        amount: amount,
        currency: currency,
        idempotencyKey: idempotencyKey,
        externalReference: externalReference,
        description: description,
      );
      _qrIntent = await createQrTransactionIntentUseCase(request);
    } catch (e) {
      _qrErrorMessage = e.toString();
    } finally {
      _qrProcessing = false;
      notifyListeners();
    }
  }

  Future<void> loadQrIntent(String id) async {
    _qrProcessing = true;
    _qrErrorMessage = null;
    _qrIntent = null;
    _qrConfirmedTransaction = null;
    notifyListeners();

    try {
      _qrIntent = await getQrTransactionIntentUseCase(id);
    } catch (e) {
      _qrErrorMessage = e.toString();
    } finally {
      _qrProcessing = false;
      notifyListeners();
    }
  }

  Future<void> cancelQrIntent(String id) async {
    _qrProcessing = true;
    _qrErrorMessage = null;
    notifyListeners();

    try {
      _qrIntent = await cancelQrTransactionIntentUseCase(id);
    } catch (e) {
      _qrErrorMessage = e.toString();
    } finally {
      _qrProcessing = false;
      notifyListeners();
    }
  }

  Future<void> confirmQrIntent({
    required String intentId,
    required String sourceAccountId,
    required String idempotencyKey,
  }) async {
    _qrProcessing = true;
    _qrErrorMessage = null;
    _qrConfirmedTransaction = null;
    notifyListeners();

    try {
      final request = ConfirmQrTransactionRequest(
        sourceAccountId: sourceAccountId,
        idempotencyKey: idempotencyKey,
      );
      _qrConfirmedTransaction =
          await confirmQrTransactionUseCase(intentId, request);
      await loadTransactions();
    } catch (e) {
      _qrErrorMessage = e.toString();
    } finally {
      _qrProcessing = false;
      notifyListeners();
    }
  }

  Future<void> createDeposit({
    required double amount,
    required String currency,
    required String idempotencyKey,
    required String targetAccountId,
    required String method,
    String? externalReference,
    String? description,
  }) async {
    _creating = true;
    _errorMessage = null;
    _transactionCreated = false;
    notifyListeners();

    try {
      final request = DepositRequest(
        amount: amount,
        currency: currency,
        idempotencyKey: idempotencyKey,
        targetAccountId: targetAccountId,
        method: method,
        externalReference: externalReference,
        description: description,
      );
      await createDepositUseCase(request);
      _transactionCreated = true;
      await loadTransactions();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }

  Future<void> createHold({
    required String accountId,
    required double amount,
    required String currency,
    required String idempotencyKey,
    String? description,
  }) async {
    _creating = true;
    _errorMessage = null;
    _transactionCreated = false;
    notifyListeners();

    try {
      final request = HoldRequest(
        accountId: accountId,
        amount: amount,
        currency: currency,
        idempotencyKey: idempotencyKey,
        description: description,
      );
      await createHoldUseCase(request);
      _transactionCreated = true;
      await loadTransactions();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }

  // En TransactionsViewModel agregar método createPayment si no existe
  Future<void> createPayment({
    required double amount,
    required String currency,
    required String idempotencyKey,
    required String sourceAccountId,
    required String method,
    String? externalReference,
    String? description,
  }) async {
    _creating = true;
    _errorMessage = null;
    _transactionCreated = false;
    notifyListeners();

    try {
      final request = PaymentRequest(
        amount: amount,
        currency: currency,
        idempotencyKey: idempotencyKey,
        sourceAccountId: sourceAccountId,
        method: method,
        externalReference: externalReference,
        description: description,
      );
      await createPaymentUseCase(request);
      _transactionCreated = true;
      await loadTransactions();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }

  Future<void> createRelease({
    required String accountId,
    required double amount,
    required String currency,
    required String idempotencyKey,
    String? description,
  }) async {
    _creating = true;
    _errorMessage = null;
    _transactionCreated = false;
    notifyListeners();

    try {
      final request = ReleaseRequest(
        accountId: accountId,
        amount: amount,
        currency: currency,
        idempotencyKey: idempotencyKey,
        description: description,
      );
      await createReleaseUseCase(request);
      _transactionCreated = true;
      await loadTransactions();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }

  Future<void> createTransfer({
    required double amount,
    required String currency,
    required String idempotencyKey,
    required String sourceAccountId,
    required String targetAccountId,
    String? description,
  }) async {
    _creating = true;
    _errorMessage = null;
    _transactionCreated = false;
    notifyListeners();

    try {
      final request = TransferRequest(
        amount: amount,
        currency: currency,
        idempotencyKey: idempotencyKey,
        sourceAccountId: sourceAccountId,
        targetAccountId: targetAccountId,
        description: description,
      );
      await createTransferUseCase(request);
      _transactionCreated = true;
      await loadTransactions();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }

  Future<void> createWithdrawal({
    required double amount,
    required String currency,
    required String idempotencyKey,
    required String sourceAccountId,
    required String method,
    String? description,
  }) async {
    _creating = true;
    _errorMessage = null;
    _transactionCreated = false;
    notifyListeners();

    try {
      final request = WithdrawalRequest(
        amount: amount,
        currency: currency,
        idempotencyKey: idempotencyKey,
        sourceAccountId: sourceAccountId,
        method: method,
        description: description,
      );
      await createWithdrawalUseCase(request);
      _transactionCreated = true;
      await loadTransactions();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }

  List<Transaction> get recentTransactions {
    final sorted = List<Transaction>.from(_transactions);
    sorted.sort((a, b) => b.processedAt.compareTo(a.processedAt));
    return sorted.take(10).toList();
  }
}
