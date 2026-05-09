package com.wastely.model;

/**
 * Represents the result of a CRUD operation (Create, Read, Update, Delete).
 * Provides status, message, and optional data result for operations.
 *
 * @param <T> The type of data returned by the operation (can be Void if no data)
 */
public class OperationResult<T> {

    public enum Status {
        SUCCESS("Operation completed successfully"),
        ERROR("Operation failed"),
        VALIDATION_ERROR("Validation failed"),
        NOT_FOUND("Resource not found"),
        DUPLICATE("Resource already exists"),
        CONFLICT("Operation conflict"),
        UNAUTHORIZED("Operation not authorized");

        private final String defaultMessage;

        Status(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }

    private final Status status;
    private final String message;
    private final T data;
    private final Throwable exception;

    /**
     * Private constructor - use factory methods instead.
     */
    private OperationResult(Status status, String message, T data, Throwable exception) {
        this.status = status;
        this.message = message != null ? message : status.getDefaultMessage();
        this.data = data;
        this.exception = exception;
    }

    /**
     * Create a successful operation result with a message and data.
     *
     * @param message The success message
     * @param data The data result (can be null)
     * @return OperationResult with SUCCESS status
     */
    public static <T> OperationResult<T> success(String message, T data) {
        return new OperationResult<>(Status.SUCCESS, message, data, null);
    }

    /**
     * Create a successful operation result with data.
     *
     * @param data The data result (can be null)
     * @return OperationResult with SUCCESS status
     */
    public static <T> OperationResult<T> success(T data) {
        return success("Operation completed successfully", data);
    }

    /**
     * Create a successful operation result with a message.
     *
     * @param message The success message
     * @return OperationResult with SUCCESS status
     */
    public static <T> OperationResult<T> success(String message) {
        return success(message, null);
    }

    /**
     * Create a successful operation result with default message.
     *
     * @return OperationResult with SUCCESS status
     */
    public static OperationResult<Void> success() {
        return success("Operation completed successfully", null);
    }

    /**
     * Create an error operation result.
     *
     * @param message The error message
     * @param exception The exception that caused the error (optional)
     * @return OperationResult with ERROR status
     */
    public static <T> OperationResult<T> error(String message, Throwable exception) {
        return new OperationResult<>(Status.ERROR, message, null, exception);
    }

    /**
     * Create an error operation result.
     *
     * @param message The error message
     * @return OperationResult with ERROR status
     */
    public static <T> OperationResult<T> error(String message) {
        return error(message, null);
    }

    /**
     * Create an error operation result from an exception.
     *
     * @param exception The exception that caused the error
     * @return OperationResult with ERROR status
     */
    public static <T> OperationResult<T> error(Throwable exception) {
        return error(exception.getMessage(), exception);
    }

    /**
     * Create a validation error operation result.
     *
     * @param message The validation error message
     * @return OperationResult with VALIDATION_ERROR status
     */
    public static <T> OperationResult<T> validationError(String message) {
        return new OperationResult<>(Status.VALIDATION_ERROR, message, null, null);
    }

    /**
     * Create a not found operation result.
     *
     * @param message The not found message
     * @return OperationResult with NOT_FOUND status
     */
    public static <T> OperationResult<T> notFound(String message) {
        return new OperationResult<>(Status.NOT_FOUND, message, null, null);
    }

    /**
     * Create a duplicate operation result.
     *
     * @param message The duplicate message
     * @return OperationResult with DUPLICATE status
     */
    public static <T> OperationResult<T> duplicate(String message) {
        return new OperationResult<>(Status.DUPLICATE, message, null, null);
    }

    // ============================================================
    // GETTERS
    // ============================================================

    /**
     * Get the operation status.
     *
     * @return The Status of this operation
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Get the operation message.
     *
     * @return The message (never null)
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the operation data (if any).
     *
     * @return The data result (can be null)
     */
    public T getData() {
        return data;
    }

    /**
     * Get the exception that caused the error (if any).
     *
     * @return The exception (can be null)
     */
    public Throwable getException() {
        return exception;
    }

    // ============================================================
    // STATUS CHECKS
    // ============================================================

    /**
     * Check if the operation was successful.
     *
     * @return true if status is SUCCESS
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    /**
     * Check if the operation failed.
     *
     * @return true if status is ERROR or any error status
     */
    public boolean isError() {
        return status != Status.SUCCESS;
    }

    /**
     * Check if the operation has a validation error.
     *
     * @return true if status is VALIDATION_ERROR
     */
    public boolean isValidationError() {
        return status == Status.VALIDATION_ERROR;
    }

    /**
     * Check if the resource was not found.
     *
     * @return true if status is NOT_FOUND
     */
    public boolean isNotFound() {
        return status == Status.NOT_FOUND;
    }

    @Override
    public String toString() {
        return "OperationResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
