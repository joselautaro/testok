package com.testok.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.testok.entities.Cliente;
import com.testok.services.ClienteService;

@Controller
@RequestMapping("/admin/clientes")
public class AdminClienteController {

    private final ClienteService clienteService;

    public AdminClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "ok", required = false) String ok,
                         @RequestParam(value = "err", required = false) String err,
                         @RequestParam(value = "ver", required = false) Long ver,
                         Authentication auth) {

        String username = auth.getName();

        // ✅ Ahora ve TODOS
        model.addAttribute("clientes", clienteService.listar());

        // ✅ Para que el HTML sepa quién está logueado
        model.addAttribute("usernameLogueado", username);

        model.addAttribute("ok", ok);
        model.addAttribute("err", err);

        // ✅ Puede ver detalle de cualquiera
        if (ver != null) {
            model.addAttribute("seleccionado", clienteService.buscarPorId(ver));
        }

        return "admin-clientes";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modo", "nuevo");
        return "admin-cliente-form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("cliente") Cliente cliente,
                          RedirectAttributes ra,
                          Authentication auth) {
        try {
            String username = auth.getName();
            Cliente creado = clienteService.guardar(cliente, username); // ✅ asigna createdBy
            ra.addAttribute("ok", "Empleado guardado correctamente");
            return "redirect:/admin/clientes?ver=" + creado.getId();
        } catch (Exception ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/clientes";
        }
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model, RedirectAttributes ra, Authentication auth) {
        try {
            String username = auth.getName();

            // 🔐 SOLO si es propio
            model.addAttribute("cliente", clienteService.buscarPropioPorId(id, username));
            model.addAttribute("modo", "editar");
            return "admin-cliente-form";

        } catch (Exception ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/clientes";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("cliente") Cliente cliente,
                             RedirectAttributes ra,
                             Authentication auth) {
        try {
            String username = auth.getName();

            // 🔐 SOLO si es propio
            clienteService.actualizarPropio(id, cliente, username);

            ra.addAttribute("ok", "Empleado actualizada correctamente");
            return "redirect:/admin/clientes?ver=" + id;

        } catch (Exception ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/clientes";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes ra,
                           Authentication auth) {
        try {
            String username = auth.getName();

            // 🔐 SOLO si es propio
            clienteService.eliminarPropio(id, username);

            ra.addAttribute("ok", "Empleado eliminado correctamente");
            return "redirect:/admin/clientes";

        } catch (Exception ex) {
            ra.addAttribute("err", ex.getMessage());
            return "redirect:/admin/clientes";
        }
    }
}
