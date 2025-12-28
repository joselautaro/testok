package com.testok.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.testok.entities.Cliente;
import com.testok.entities.Usuario;
import com.testok.exceptions.AccesoDenegadoException;
import com.testok.repositories.ClienteRepository;
import com.testok.repositories.UsuarioRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ listar solo propios
    public List<Cliente> listarPropios(String username) {
        return clienteRepository.findAllByCreatedBy_Username(username);
    }

    // ✅ crear: asignar propietario
    public Cliente guardar(Cliente cliente, String usernameCreador) {
        Usuario creador = usuarioRepository.findByUsername(usernameCreador)
                .orElseThrow(() -> new IllegalArgumentException("Usuario creador no encontrado"));

        cliente.setCreatedBy(creador);
        return clienteRepository.save(cliente);
    }

    // ✅ ver detalle (solo si es propio)
    public Cliente buscarPropioPorId(Long id, String username) {
        return clienteRepository.findByIdAndCreatedBy_Username(id, username)
                .orElseThrow(() -> new AccesoDenegadoException("No tenés permisos para ver este cliente."));
    }

    // ✅ actualizar (solo si es propio)
    public Cliente actualizarPropio(Long id, Cliente form, String username) {
        Cliente existente = buscarPropioPorId(id, username);

        existente.setNombre(form.getNombre());
        existente.setApellido(form.getApellido());
        existente.setEdad(form.getEdad());
        existente.setFotoPerfilUrl(form.getFotoPerfilUrl());

        existente.setDesempeno(form.getDesempeno());
        existente.setReputacionLaboral(form.getReputacionLaboral());
        existente.setCumplimiento(form.getCumplimiento());
        existente.setHabilidadesBlandas(form.getHabilidadesBlandas());
        existente.setHistorial(form.getHistorial());
        existente.setValoracionesGenerales(form.getValoracionesGenerales());

        return clienteRepository.save(existente);
    }

    // ✅ eliminar (solo si es propio)
    public void eliminarPropio(Long id, String username) {
        if (!clienteRepository.existsByIdAndCreatedBy_Username(id, username)) {
            throw new AccesoDenegadoException("No tenés permisos para eliminar este cliente.");
        }
        clienteRepository.deleteById(id);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado (id=" + id + ")"));
    }

}
