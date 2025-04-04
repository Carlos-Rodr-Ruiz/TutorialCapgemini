package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las operaciones relacionadas con los préstamos de juegos.
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ClientService clientService;

    /**
     * Recupera todos los préstamos existentes en la base de datos.
     *      //Lo utilize para pruebas en postman
     * @return Lista completa de préstamos
     */
    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    /**
     * Devuelve una página de préstamos con filtros opcionales por cliente, juego o fecha.
     *
     * @param pageableRequest Parámetros de paginación (número de página y tamaño)
     * @param filters         Filtros de búsqueda opcionales
     * @return Página de resultados con préstamos encontrados
     */
    @Override
    public ResponsePage<LoanDto> find(PageableRequest pageableRequest, LoanSearchDto filters) {
        if (pageableRequest == null) {
            pageableRequest = new PageableRequest(0, 10);
        }

        int pageSize = pageableRequest.getPageSize();
        if (pageSize < 1) {
            throw new IllegalArgumentException("El tamaño de la página debe ser al menos 1.");
        }

        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageSize);
        Specification<Loan> spec = LoanSpecification.buildSpecification(filters != null ? filters.getClientId() : null, filters != null ? filters.getGameId() : null, filters != null ? filters.getDate() : null);

        Page<Loan> page = loanRepository.findAll(spec, pageable);
        List<LoanDto> dtos = page.getContent().stream().map(this::convertToDto).collect(Collectors.toList());

        return new ResponsePage<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * Recupera todos los préstamos que coincidan con los filtros, sin paginación.
     *
     * @param clientId ID del cliente
     * @param gameId   ID del juego
     * @param date     Fecha a comprobar en el rango de préstamo
     * @return Lista de préstamos filtrados
     */
    @Override
    public List<Loan> findFiltered(Long clientId, Long gameId, LocalDate date) {
        Specification<Loan> spec = LoanSpecification.buildSpecification(clientId, gameId, date);
        return loanRepository.findAll(spec);
    }

    /**
     * Crea o actualiza un préstamo en base a los datos proporcionados.
     * Realiza validaciones de solapamientos, duración máxima y cantidad de préstamos por cliente.
     *
     * @param dto Objeto DTO con los datos del préstamo
     * @throws ResponseStatusException si alguna validación no se cumple
     */
    @Override
    public void save(LoanDto dto) {
        Loan loan;

        // Si no tiene ID, es un nuevo préstamo
        if (dto.getId() == null) {
            loan = new Loan();
            System.out.println("🟢 Nuevo préstamo");
        } else {
            loan = loanRepository.findById(dto.getId()).orElse(new Loan());
            System.out.println("✏️ Editando préstamo con ID: " + dto.getId());
        }

        System.out.println("📦 Datos recibidos: " + dto);

        // Copiar propiedades básicas, excluyendo game y client que se asignan aparte
        BeanUtils.copyProperties(dto, loan, "id", "game", "client");

        // Validar fechas
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior a la de inicio");
        }

        if (ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) > 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El periodo del préstamo no puede superar los 14 días");
        }

        // Validar si el juego está ya prestado en el rango de fechas
        List<Loan> overlappingLoans = loanRepository.findByGameIdAndDateOverlap(dto.getGameId(), dto.getStartDate(), dto.getEndDate());
        if (dto.getId() != null) {
            overlappingLoans = overlappingLoans.stream().filter(existing -> !existing.getId().equals(dto.getId())).toList();
        }
        if (!overlappingLoans.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El juego ya está prestado en el rango de fechas indicado.");
        }

        // Validar si el cliente ya tiene 2 préstamos en el rango
        List<Loan> clientLoans = loanRepository.findByClientIdAndDateOverlap(dto.getClientId(), dto.getStartDate(), dto.getEndDate());
        if (dto.getId() != null) {
            clientLoans = clientLoans.stream().filter(existing -> !existing.getId().equals(dto.getId())).toList();
        }
        if (clientLoans.size() >= 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente ya tiene 2 préstamos en el rango de fechas indicado.");
        }

        if (dto.getGameId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar un juego.");
        }

        loan.setGame(gameService.get(dto.getGameId()));

        if (loan.getGame() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El juego seleccionado no existe.");
        }

        loan.setClient(clientService.get(dto.getClientId()));

        // Guardar en base de datos
        loanRepository.save(loan);
        System.out.println("💾 Préstamo guardado con éxito");
    }

    /**
     * Elimina un préstamo por su ID.
     *
     * @param id Identificador del préstamo a eliminar
     */
    @Override
    public void delete(Long id) {
        loanRepository.deleteById(id);
    }

    /**
     * Convierte una entidad Loan a su DTO correspondiente.
     *
     * @param entity Entidad Loan
     * @return DTO con los datos del préstamo
     */
    private LoanDto convertToDto(Loan entity) {
        LoanDto dto = new LoanDto();
        dto.setId(entity.getId());
        dto.setGameId(entity.getGame().getId());
        dto.setGameTitle(entity.getGame().getTitle());
        dto.setClientId(entity.getClient().getId());
        dto.setClientName(entity.getClient().getName());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }
}

