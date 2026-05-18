import '../entities/transaction.dart';
import '../entities/deposit_request.dart';
import '../entities/hold_request.dart';
import '../entities/payment_request.dart';
import '../entities/release_request.dart';
import '../entities/transfer_request.dart';
import '../entities/withdrawal_request.dart';

abstract class TransactionRepository {
  Future<List<Transaction>> getTransactions();
  Future<Transaction> getTransactionById(String id);
  Future<Transaction> createDeposit(DepositRequest request);
  Future<Transaction> createHold(HoldRequest request);
  Future<Transaction> createPayment(PaymentRequest request);
  Future<Transaction> createRelease(ReleaseRequest request);
  Future<Transaction> createTransfer(TransferRequest request);
  Future<Transaction> createWithdrawal(WithdrawalRequest request);
}
