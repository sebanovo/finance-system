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
import '../../../domain/repositories/transaction_repository.dart';
import '../datasources/transaction_remote_datasource.dart';

class TransactionRepositoryImpl implements TransactionRepository {
  final TransactionRemoteDataSource remoteDataSource;

  TransactionRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<Transaction>> getTransactions() async {
    final models = await remoteDataSource.getTransactions();
    return models.map((model) => model.toEntity()).toList();
  }

  @override
  Future<Transaction> getTransactionById(String id) async {
    final model = await remoteDataSource.getTransactionById(id);
    return model.toEntity();
  }

  @override
  Future<Transaction> createDeposit(DepositRequest request) async {
    final model = await remoteDataSource.createDeposit({
      'amount': request.amount,
      'currency': request.currency,
      'idempotencyKey': request.idempotencyKey,
      'targetAccountId': request.targetAccountId,
      'method': request.method,
      if (request.externalReference != null)
        'externalReference': request.externalReference,
      if (request.description != null) 'description': request.description,
    });
    return model.toEntity();
  }

  @override
  Future<Transaction> createHold(HoldRequest request) async {
    final model = await remoteDataSource.createHold({
      'accountId': request.accountId,
      'amount': request.amount,
      'currency': request.currency,
      'idempotencyKey': request.idempotencyKey,
      if (request.description != null) 'description': request.description,
    });
    return model.toEntity();
  }

  @override
  Future<Transaction> createPayment(PaymentRequest request) async {
    final model = await remoteDataSource.createPayment({
      'amount': request.amount,
      'currency': request.currency,
      'idempotencyKey': request.idempotencyKey,
      'sourceAccountId': request.sourceAccountId,
      'method': request.method,
      if (request.externalReference != null)
        'externalReference': request.externalReference,
      if (request.description != null) 'description': request.description,
    });
    return model.toEntity();
  }

  @override
  Future<QrTransactionIntent> createQrIntent(
    QrTransactionIntentRequest request,
  ) async {
    final model = await remoteDataSource.createQrIntent({
      'targetAccountId': request.targetAccountId,
      'amount': request.amount,
      'currency': request.currency,
      'idempotencyKey': request.idempotencyKey,
      if (request.externalReference != null)
        'externalReference': request.externalReference,
      if (request.description != null) 'description': request.description,
    });
    return model.toEntity();
  }

  @override
  Future<QrTransactionIntent> getQrIntent(String id) async {
    final model = await remoteDataSource.getQrIntent(id);
    return model.toEntity();
  }

  @override
  Future<QrTransactionIntent> cancelQrIntent(String id) async {
    final model = await remoteDataSource.cancelQrIntent(id);
    return model.toEntity();
  }

  @override
  Future<Transaction> confirmQrTransaction(
    String intentId,
    ConfirmQrTransactionRequest request,
  ) async {
    final model = await remoteDataSource.confirmQrTransaction(intentId, {
      'sourceAccountId': request.sourceAccountId,
      'idempotencyKey': request.idempotencyKey,
    });
    return model.toEntity();
  }

  @override
  Future<Transaction> createRelease(ReleaseRequest request) async {
    final model = await remoteDataSource.createRelease({
      'accountId': request.accountId,
      'amount': request.amount,
      'currency': request.currency,
      'idempotencyKey': request.idempotencyKey,
      if (request.description != null) 'description': request.description,
    });
    return model.toEntity();
  }

  @override
  Future<Transaction> createTransfer(TransferRequest request) async {
    final model = await remoteDataSource.createTransfer({
      'amount': request.amount,
      'currency': request.currency,
      'idempotencyKey': request.idempotencyKey,
      'sourceAccountId': request.sourceAccountId,
      'targetAccountId': request.targetAccountId,
      if (request.description != null) 'description': request.description,
    });
    return model.toEntity();
  }

  @override
  Future<Transaction> createWithdrawal(WithdrawalRequest request) async {
    final model = await remoteDataSource.createWithdrawal({
      'amount': request.amount,
      'currency': request.currency,
      'idempotencyKey': request.idempotencyKey,
      'sourceAccountId': request.sourceAccountId,
      'method': request.method,
      if (request.description != null) 'description': request.description,
    });
    return model.toEntity();
  }
}
