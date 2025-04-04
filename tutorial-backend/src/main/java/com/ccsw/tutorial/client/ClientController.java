package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Client", description = "API of Client")
@RequestMapping(value = "/client")
@RestController
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    ClientService clientService;

    @Autowired
    ModelMapper mapper;

    @Operation(summary = "Find", description = "Method that returns the list of all clients")
    @GetMapping("")
    public List<ClientDto> findAll() {
        List<Client> clients = this.clientService.findAll();
        return clients.stream().map(e -> mapper.map(e, ClientDto.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Save", description = "Method that saves a new client")
    @PostMapping
    public void save(@RequestBody ClientDto dto) throws Exception {
        this.clientService.save(dto);
    }

    @Operation(summary = "Delete", description = "Method that deletes a client by ID")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws Exception {
        this.clientService.delete(id);
    }

}
