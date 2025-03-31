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
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de préstamos.
 * Encargado de gestionar las operaciones de negocio relacionadas con los préstamos.
 */
@Service
public class LoanServiceImpl implements LoanService {

    // Repositorio para acceder a los datos de préstamo
    @Autowired
    private LoanRepository loanRepository;

    // Servicio para acceder a datos de juegos
    @Autowired
    private GameService gameService;

    // Servicio para acceder a datos de clientes
    @Autowired
    private ClientService clientService;

    /**
     * Recupera un listado paginado de préstamos según los filtros.
     *
     * @param pageableRequest Objeto con la información de paginación.
     * @param filters Filtros aplicados sobre los préstamos.
     * @return Página de resultados con los préstamos encontrados.
     */
    @Override
    public ResponsePage<LoanDto> find(PageableRequest pageableRequest, LoanSearchDto filters) {
        // Asegura que pageableRequest no sea null
        if (pageableRequest == null) {
            pageableRequest = new PageableRequest(0, 10); // valores por defecto
        }

        // Valida el tamaño de la página
        int pageSize = pageableRequest.getPageSize();
        if (pageSize < 1) {
            throw new IllegalArgumentException("El tamaño de la página debe ser al menos 1.");
        }

        // Crea objeto Pageable
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageSize);

        // Verifica que los filtros incluyan al menos un gameId
        if (filters == null || filters.getGameId() == null) {
            throw new IllegalArgumentException("El filtro 'gameId' es obligatorio.");
        }

        // Construye la especificación usando los filtros
        Specification<Loan> spec = Specification.where(LoanSpecification.hasGame(filters.getGameId())).and(LoanSpecification.hasClient(filters.getClientId())).and(LoanSpecification.includesDate(filters.getDate()));

        // Ejecuta la consulta paginada
        Page<Loan> page = loanRepository.findAll(spec, pageable);

        // Convierte entidades a DTO
        List<LoanDto> dtos = page.getContent().stream().map(entity -> {
            LoanDto dto = new LoanDto();
            dto.setId(entity.getId());
            dto.setGameId(entity.getGame().getId());
            dto.setGameTitle(entity.getGame().getTitle());
            dto.setClientId(entity.getClient().getId());
            dto.setClientName(entity.getClient().getName());
            dto.setStartDate(entity.getStartDate());
            dto.setEndDate(entity.getEndDate());
            return dto;
        }).collect(Collectors.toList());

        // Devuelve la respuesta paginada
        return new ResponsePage<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * Guarda un préstamo nuevo o actualizado con sus respectivas validaciones.
     *
     * @param dto Datos del préstamo a guardar.
     */
    @Override
    public void save(LoanDto dto) {
        // Crea o recupera el préstamo
        Loan loan;
        if (dto.getId() == null) {
            loan = new Loan();
        } else {
            loan = loanRepository.findById(dto.getId()).orElse(new Loan());
        }

        // Copia los campos excepto id, game y client
        BeanUtils.copyProperties(dto, loan, "id", "game", "client");

        // Valida que la fecha de fin no sea anterior a la de inicio
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }

        // Valida que el préstamo no supere los 14 días
        if (ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) > 14) {
            throw new IllegalArgumentException("El periodo del préstamo no puede superar los 14 días");
        }

        // Valida que el juego no esté prestado en el rango de fechas
        List<Loan> overlappingLoans = loanRepository.findByGameIdAndDateOverlap(dto.getGameId(), dto.getStartDate(), dto.getEndDate());

        // Excluye el préstamo actual si estamos editando
        if (dto.getId() != null) {
            overlappingLoans = overlappingLoans.stream().filter(existing -> !existing.getId().equals(dto.getId())).toList();
        }

        // Si hay préstamos solapados, lanza excepción
        if (!overlappingLoans.isEmpty()) {
            throw new IllegalArgumentException("El juego ya está prestado en el rango de fechas indicado.");
        }

        // Valida que el cliente no tenga más de dos préstamos en el mismo rango
        List<Loan> clientLoans = loanRepository.findByClientIdAndDateOverlap(dto.getClientId(), dto.getStartDate(), dto.getEndDate());

        // Excluye el préstamo actual si es edición
        if (dto.getId() != null) {
            clientLoans = clientLoans.stream().filter(existing -> !existing.getId().equals(dto.getId())).toList();
        }

        // Si ya tiene dos préstamos, lanza excepción
        if (clientLoans.size() >= 2) {
            throw new IllegalArgumentException("El cliente ya tiene 2 préstamos en el rango de fechas indicado.");
        }

        // Valida que Game ID no sea null
        if (dto.getGameId() == null) {
            throw new IllegalArgumentException("Game ID must not be null");
        }

        // Asocia el juego al préstamo
        loan.setGame(gameService.get(dto.getGameId()));

        // Verifica que el juego existe
        if (loan.getGame() == null) {
            throw new IllegalArgumentException("Game must not be null");
        }

        // Asocia el cliente al préstamo
        loan.setClient(clientService.get(dto.getClientId()));

        // Guarda el préstamo
        loanRepository.save(loan);
    }

    /**
     * Elimina un préstamo por su ID.
     *
     * @param id Identificador del préstamo a eliminar.
     */
    @Override
    public void delete(Long id) {
        loanRepository.deleteById(id);
    }
}
