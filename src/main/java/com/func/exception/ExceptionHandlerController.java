package com.func.exception;

import com.func.model.Department;
import com.func.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerController {

    // Erros de validação (@Valid) → formato com "detalhes"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> errosDeValidacao(MethodArgumentNotValidException ex) {
        List<Map<String, String>> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "campo", error.getField(),
                        "mensagem", error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        Map<String, Object> body = Map.of(
                "erro", "Dados inválidos",
                "detalhes", detalhes,
                "status", 400,
                "timestamp", LocalDateTime.now().toString()
        );

        return ResponseEntity.badRequest().body(body);
    }

    // Enum inválido no JSON (ex: department ou role com valor desconhecido)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> enumInvalido(HttpMessageNotReadableException ex) {
        String mensagem = ex.getMessage();

        // Detecta se é um campo de Department ou Role
        String detalhe;
        if (mensagem != null && mensagem.contains("Department")) {
            String validos = Arrays.stream(Department.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            detalhe = "Departamento inválido. Valores aceitos: " + validos;
        } else if (mensagem != null && mensagem.contains("Role")) {
            String validos = Arrays.stream(Role.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            detalhe = "Cargo inválido. Valores aceitos: " + validos;
        } else {
            detalhe = "Valor inválido no corpo da requisição";
        }

        Map<String, Object> body = Map.of(
                "erro", detalhe,
                "status", 400,
                "timestamp", LocalDateTime.now().toString()
        );

        return ResponseEntity.badRequest().body(body);
    }

    // CPF já cadastrado
    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleCpfConflito(CpfJaCadastradoException ex) {
        return erroGenerico(ex.getMessage(), 400);
    }

    // Email já cadastrado
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailConflito(EmailJaCadastradoException ex) {
        return erroGenerico(ex.getMessage(), 400);
    }

    private ResponseEntity<Map<String, Object>> erroGenerico(String mensagem, int status) {
        Map<String, Object> body = Map.of(
                "erro", mensagem,
                "status", status,
                "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.status(status).body(body);
    }
}
