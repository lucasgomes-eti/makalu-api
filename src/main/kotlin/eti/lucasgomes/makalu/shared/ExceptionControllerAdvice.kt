package eti.lucasgomes.makalu.shared

import eti.lucasgomes.makalu.shared.exceptions.AuthErrorException
import eti.lucasgomes.makalu.shared.exceptions.BadRequestException
import eti.lucasgomes.makalu.shared.exceptions.InternalErrorException
import eti.lucasgomes.makalu.shared.exceptions.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(Exception::class)
    fun handleNotMappedException(exception: Exception): ResponseEntity<ErrorResponse> {
        return handleInternalErrorException(InternalErrorException(GlobalError.InternalError(exception), exception))
    }

    @ExceptionHandler(InternalErrorException::class)
    fun handleInternalErrorException(
        exception: InternalErrorException
    ): ResponseEntity<ErrorResponse> {
        val error = GlobalError.InternalError(exception.cause)
        val errorResponse = ErrorResponse(
            httpCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = error.message,
            internalCode = error.code,
            fieldErrors = error.fieldErrors
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AuthErrorException::class)
    fun handleAuthErrorException(
        exception: AuthErrorException
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = HttpStatus.UNAUTHORIZED.value(),
            message = exception.mkError.message,
            internalCode = exception.mkError.code,
            fieldErrors = exception.mkError.fieldErrors
        )
        return ResponseEntity(error, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(
        exception: NotFoundException
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = HttpStatus.NOT_FOUND.value(),
            message = exception.mkError.message,
            internalCode = exception.mkError.code,
            fieldErrors = exception.mkError.fieldErrors
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(
        exception: BadRequestException
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = HttpStatus.BAD_REQUEST.value(),
            message = exception.mkError.message,
            internalCode = exception.mkError.code,
            fieldErrors = exception.mkError.fieldErrors
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException,
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = HttpStatus.UNPROCESSABLE_ENTITY.value(),
            message = GlobalError.InvalidRequestObject.message,
            internalCode = GlobalError.InvalidRequestObject.code,
            fieldErrors = exception.bindingResult.fieldErrors.map {
                ErrorResponse.FieldError(
                    it.field,
                    it.defaultMessage ?: "Invalid"
                )
            }
        )
        return ResponseEntity(error, HttpStatus.UNPROCESSABLE_ENTITY)
    }
}