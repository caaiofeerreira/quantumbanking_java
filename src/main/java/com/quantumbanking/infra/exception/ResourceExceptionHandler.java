package com.quantumbanking.infra.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ResourceExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String message, String path
    ) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now(),
                path
        );
        return ResponseEntity.status(status).body(error);
    }

    private String getPath() {
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getRequestURI();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return buildResponse(HttpStatus.BAD_REQUEST, message, getPath());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Tentativa de login inválido: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "CPF ou senha inválidos.", getPath());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedAccessException ex) {
        log.warn("Acesso não autorizado: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), getPath());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        log.warn("Conta não encontrada: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), getPath());
    }

    @ExceptionHandler(ManagerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleManagerNotFoundException(ManagerNotFoundException ex) {
        log.warn("Conta Gerente não encontrada: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), getPath());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("Falha na busca de usuário: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), getPath());
    }

    @ExceptionHandler(TransactionNotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotAuthorizedException(TransactionNotAuthorizedException ex) {
        log.warn("Erro de transação: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), getPath());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado na aplicação", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.",
                getPath());
    }

    @ExceptionHandler(AgencyAccountMismatchException.class)
    public ResponseEntity<ErrorResponse> handleAgencyAccountMismatchException(AgencyAccountMismatchException ex) {
        log.warn("Falha na validação de transação: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), getPath());
    }

    @ExceptionHandler(AgencyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAgencyNotFoundException(AgencyNotFoundException ex) {
        log.warn("Falha na localização de recurso: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), getPath());
    }

    @ExceptionHandler(MinimumAmountException.class)
    public ResponseEntity<ErrorResponse> handleMinimumAmountException(MinimumAmountException ex) {
        log.warn("Tentativa de transação negada por valor mínimo: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), getPath());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        log.warn("Falha no processamento: Saldo insuficiente. Contexto: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), getPath());
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccountStatusException(AccountStatusException ex) {
        log.error("Transação bloqueada: A conta não possui status para operar. Detalhes: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), getPath());
    }

    @ExceptionHandler(PixKeyLimitException.class)
    public ResponseEntity<ErrorResponse> handlePixKeyLimitException(PixKeyLimitException ex) {
        log.warn("Limite de chaves atingido: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), getPath());
    }

    @ExceptionHandler(PixKeyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePixKeyAlreadyExistsException(PixKeyAlreadyExistsException ex) {
        log.warn("Tentativa de duplicidade de chave Pix: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), getPath());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), getPath());
    }

    @ExceptionHandler(InvalidPixKeyTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPixKeyTypeException(InvalidPixKeyTypeException ex) {
        log.warn("Solicitação de chave Pix inválida: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), getPath());
    }

    @ExceptionHandler(AgencyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAgencyAlreadyExistsException(AgencyAlreadyExistsException ex) {
        log.warn("Tentativa de cadastro de agência duplicada: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), getPath());
    }

    @ExceptionHandler(CpfAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleCpfAlreadyRegisteredException(CpfAlreadyRegisteredException ex) {
        log.warn("Conflito de CPF no cadastro de gerente: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), getPath());
    }

    @ExceptionHandler(IncompleteCompanyDataException.class)
    public ResponseEntity<ErrorResponse> handleIncompleteCompanyDataException(IncompleteCompanyDataException ex) {
        log.warn("Falha no cadastro de cliente PJ: Dados da empresa ausentes. {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), getPath());
    }

    @ExceptionHandler(InvalidTransactionValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionValueException(InvalidTransactionValueException ex) {
        log.warn("Tentativa de transação com valor inválido ou nulo: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), getPath());
    }

    @ExceptionHandler(IncompatibleAccountTypeException.class)
    public ResponseEntity<ErrorResponse> handleIncompatibleAccountTypeException(IncompatibleAccountTypeException ex) {
        log.warn("Tipo de conta incompatível com o cliente: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), getPath());
    }
}