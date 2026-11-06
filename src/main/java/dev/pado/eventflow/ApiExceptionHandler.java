package dev.pado.eventflow;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOG=LoggerFactory.getLogger(ApiExceptionHandler.class);
    private ProblemDetail problem(HttpStatus status,String detail) {
        return ProblemDetail.forStatusAndDetail(status,detail);
    }
    @ExceptionHandler(EventNotFoundException.class)
    public ProblemDetail missing(EventNotFoundException e) { return problem(HttpStatus.NOT_FOUND,e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalidBody(MethodArgumentNotValidException e) {
        Map<String,List<String>> errors=new TreeMap<>();
        e.getBindingResult().getFieldErrors().forEach(x->errors.computeIfAbsent(x.getField(),k->new ArrayList<>()).add(x.getDefaultMessage()));
        var p=problem(HttpStatus.BAD_REQUEST,"Request validation failed");p.setProperty("errors",errors);return p;
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail invalidQuery(ConstraintViolationException e) {
        Map<String,List<String>> errors=new TreeMap<>();
        e.getConstraintViolations().forEach(x->errors.computeIfAbsent(x.getPropertyPath().toString(),k->new ArrayList<>()).add(x.getMessage()));
        var p=problem(HttpStatus.BAD_REQUEST,"Query validation failed");p.setProperty("errors",errors);return p;
    }
    @ExceptionHandler({IllegalArgumentException.class,MethodArgumentTypeMismatchException.class,HttpMessageNotReadableException.class})
    public ProblemDetail malformed(Exception e) { return problem(HttpStatus.BAD_REQUEST,"Invalid parameter or JSON value"); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail conflict() { return problem(HttpStatus.CONFLICT,"Event violates a database constraint"); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail method() { return problem(HttpStatus.METHOD_NOT_ALLOWED,"HTTP method is not supported on this route"); }
    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception e) {
        if (e instanceof ErrorResponse response)
            return ProblemDetail.forStatusAndDetail(response.getStatusCode(),"Request could not be handled");
        LOG.error("Unhandled API exception",e);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred");
    }
}
