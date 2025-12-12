package com.example.festival_system.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handle404(NoResourceFoundException ex,
                            HttpServletRequest request,
                            Model model) {

        boolean isApi = request.getRequestURI().startsWith("/api/");

        if (isApi) {
            return new ResponseEntity<>(
                    Map.of("error", "Not Found"),
                    HttpStatus.NOT_FOUND
            );
        }

        model.addAttribute("message", "Страница не найдена");
        return "404";
    }

    @ExceptionHandler(Exception.class)
    public Object handleOther(Exception ex,
                              HttpServletRequest request,
                              Model model) {

        boolean isApi = request.getRequestURI().startsWith("/api/");

        if (isApi) {
            return new ResponseEntity<>(
                    Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Internal Server Error"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        model.addAttribute("message",
                ex.getMessage() != null ? ex.getMessage() : "Неизвестная ошибка");

        return "error";
    }
}
