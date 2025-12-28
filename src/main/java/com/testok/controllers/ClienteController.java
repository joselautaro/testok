package com.testok.controllers;

import com.testok.services.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/clientes")
    public String listarPublico(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes-publico";
    }
}