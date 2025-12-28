package com.testok.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.testok.entities.Rol;
import com.testok.services.UsuarioService;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "ok", required = false) String ok,
                         @RequestParam(value = "err", required = false) String err) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("ok", ok);
        model.addAttribute("err", err);
        return "admin-usuarios";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        model.addAttribute("roles", Rol.values());
        return "admin-usuario-form";
    }

    @PostMapping
    public String crear(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam Rol rol,
                        RedirectAttributes ra) {
        try {
            usuarioService.crearUsuario(username, password, rol);
            ra.addAttribute("ok", "Usuario creado correctamente");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
    }
}