package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Loan", description = "API of Loan")
@RequestMapping(value = "/loan")
@RestController
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    LoanService loanService;

    @Autowired
    ModelMapper mapper;

    /**
     * Método para obtener una lista paginada de préstamos
     * Permite filtrar por ID de juego, ID de cliente y una fecha intermedia
     *
     * @param request Objeto que contiene los filtros y la información de paginación
     * @return Una página de resultados con préstamos convertidos a LoanDto
     */
    @Operation(summary = "Find Page", description = "Returns a paginated list of loans with optional filters")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponsePage<LoanDto> find(@org.springframework.web.bind.annotation.RequestBody LoanSearchRequest request) {
        return loanService.find(request.getPageable(), request.getFilters());
    }

    /**
     * Método para guardar un préstamo.
     * Si el DTO no contiene ID, se creará un nuevo préstamo
     * Si contiene ID, se actualizará el préstamo existente
     *
     * @param dto Objeto DTO con los datos del préstamo a guardar
     */
    @Operation(summary = "Save", description = "Create or update a loan")
    @PutMapping
    public void save(@RequestBody LoanDto dto) {
        loanService.save(dto);
    }

    /**
     * Método para eliminar un préstamo por su ID
     *
     * @param id ID del préstamo a eliminar
     */
    @Operation(summary = "Delete", description = "Delete a loan by its ID")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        loanService.delete(id);
    }

}
