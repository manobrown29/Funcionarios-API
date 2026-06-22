package com.func.service;

import com.func.exception.*;
import com.func.model.Funcionario;
import com.func.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository repository;

    public List<Funcionario> listarTodos() {
        return repository.findAll();
    }

    public Funcionario criarFuncionario(Funcionario funcionario) {
        if (repository.existsByCpf(funcionario.getCpf())) {
            throw new CpfJaCadastradoException(funcionario.getCpf());
        }
        if (repository.existsByEmail(funcionario.getEmail())) {
            throw new EmailJaCadastradoException(funcionario.getEmail());
        }
        return repository.save(funcionario);
    }

    public void DelFuncionario (Long id){
        repository.findById(id).orElseThrow(() -> new FuncionarioNotFoundException(id));
        repository.deleteById(id);
    }
}