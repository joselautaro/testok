package com.testok.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false)
    private Integer edad;

    // Guardamos una URL (ej: https://... o /images/...)
    @Column(name = "foto_perfil_url", length = 500)
    private String fotoPerfilUrl;

    // NUEVOS CAMPOS (escala sugerida 1-10)
    @Column(nullable = false)
    private Integer desempeno;

    @Column(nullable = false)
    private Integer reputacionLaboral;

    @Column(nullable = false)
    private Integer cumplimiento;

    @Column(nullable = false)
    private Integer habilidadesBlandas;

    // Texto libre
    @Column(columnDefinition = "TEXT")
    private String historial;

    @Column(columnDefinition = "TEXT")
    private String valoracionesGenerales;

    // ✅ NUEVO: administrador/usuario que creó este cliente (propietario)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private Usuario createdBy;

    public Cliente() {
    }

    public Cliente(String nombre, String apellido, Integer edad, String fotoPerfilUrl,
                   Integer desempeno, Integer reputacionLaboral, Integer cumplimiento, Integer habilidadesBlandas,
                   String historial, String valoracionesGenerales, Usuario createdBy) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.fotoPerfilUrl = fotoPerfilUrl;
        this.desempeno = desempeno;
        this.reputacionLaboral = reputacionLaboral;
        this.cumplimiento = cumplimiento;
        this.habilidadesBlandas = habilidadesBlandas;
        this.historial = historial;
        this.valoracionesGenerales = valoracionesGenerales;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public Integer getDesempeno() {
        return desempeno;
    }

    public Integer getReputacionLaboral() {
        return reputacionLaboral;
    }

    public Integer getCumplimiento() {
        return cumplimiento;
    }

    public Integer getHabilidadesBlandas() {
        return habilidadesBlandas;
    }

    public String getHistorial() {
        return historial;
    }

    public String getValoracionesGenerales() {
        return valoracionesGenerales;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }

    public void setDesempeno(Integer desempeno) {
        this.desempeno = desempeno;
    }

    public void setReputacionLaboral(Integer reputacionLaboral) {
        this.reputacionLaboral = reputacionLaboral;
    }

    public void setCumplimiento(Integer cumplimiento) {
        this.cumplimiento = cumplimiento;
    }

    public void setHabilidadesBlandas(Integer habilidadesBlandas) {
        this.habilidadesBlandas = habilidadesBlandas;
    }

    public void setHistorial(String historial) {
        this.historial = historial;
    }

    public void setValoracionesGenerales(String valoracionesGenerales) {
        this.valoracionesGenerales = valoracionesGenerales;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }
}
