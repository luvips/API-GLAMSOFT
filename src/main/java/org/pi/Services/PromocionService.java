package org.pi.Services;

import org.pi.Models.Promocion;
import org.pi.Models.Servicio;
import org.pi.Repositories.PromocionRepository;
import org.pi.dto.PromocionDTO;
import org.pi.dto.ServicioPromocionDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PromocionService {
    private final PromocionRepository promocionRepository;

    public PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    private PromocionDTO toDTO(Promocion promo) {
        PromocionDTO dto = new PromocionDTO();
        dto.setIdPromocion(promo.getIdPromocion());
        dto.setNombre(promo.getNombrePromocion());
        // El campo 'descripcion' no existe en el modelo Promocion.java
        dto.setPorcentajeDescuento(BigDecimal.valueOf(promo.getDescuento()));
        dto.setFechaInicio(promo.getFechaInicio());
        dto.setFechaFin(promo.getFechaFin());
        
        LocalDate hoy = LocalDate.now();
        boolean enRango = !hoy.isBefore(promo.getFechaInicio()) && !hoy.isAfter(promo.getFechaFin());
        // El campo 'activo' no existe en el modelo Promocion.java, asumimos que si está en rango, está activa
        dto.setActiva(enRango);
        
        return dto;
    }

    public List<PromocionDTO> findAll() throws SQLException {
        return promocionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PromocionDTO findById(int id) throws SQLException {
        Promocion promo = promocionRepository.findById(id);
        if (promo == null) return null;

        PromocionDTO dto = toDTO(promo);
        dto.setServicios(getServiciosConDescuento(id, BigDecimal.valueOf(promo.getDescuento())));
        return dto;
    }

    public Promocion create(Promocion promo) throws SQLException {
        return promocionRepository.save(promo);
    }

    public boolean update(int id, Promocion promo) throws SQLException {
        promo.setIdPromocion(id);
        return promocionRepository.update(promo);
    }

    public boolean delete(int id) throws SQLException {
        return promocionRepository.delete(id);
    }

    public boolean addServicioToPromocion(int idPromocion, int idServicio) throws SQLException {
        return promocionRepository.addServicioToPromocion(idPromocion, idServicio);
    }

    public List<ServicioPromocionDTO> getServiciosConDescuento(int idPromocion, BigDecimal descuento) throws SQLException {
        List<Servicio> servicios = promocionRepository.findServiciosByPromocionId(idPromocion);
        BigDecimal cien = new BigDecimal("100");
        BigDecimal factorDescuento = cien.subtract(descuento).divide(cien, 2, RoundingMode.HALF_UP);

        return servicios.stream().map(s -> {
            ServicioPromocionDTO dto = new ServicioPromocionDTO();
            dto.setIdServicio(s.getIdServicio());
            dto.setNombre(s.getNombreServicio());
            dto.setPrecioOriginal(BigDecimal.valueOf(s.getPrecio()));
            dto.setDescuento(descuento);
            dto.setPrecioConDescuento(dto.getPrecioOriginal().multiply(factorDescuento).setScale(2, RoundingMode.HALF_UP));
            return dto;
        }).collect(Collectors.toList());
    }
}
