// Error principal
abstract class Failure {
  final String message;
  Failure(this.message);
}

// Errores personalizados
class ServerFailure extends Failure {
  ServerFailure(super.message);
}

class CacheFailure extends Failure {
  CacheFailure(super.message);
}

class AuthFailure extends Failure {
  AuthFailure(super.message);
}
