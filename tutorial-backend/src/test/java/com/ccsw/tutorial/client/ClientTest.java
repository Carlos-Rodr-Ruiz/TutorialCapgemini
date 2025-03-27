package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void shouldThrowExceptionIfNameAlreadyExists() {
        ClientDto dto = new ClientDto();
        dto.setName("Laura");

        Client clienteExistente = new Client();
        clienteExistente.setId(2L);
        clienteExistente.setName("Laura");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(clientRepository.findByName("Laura")).thenReturn(clienteExistente);

        Exception exception = assertThrows(Exception.class, () -> {
            clientService.save(1L, dto);
        });

        assertTrue(exception.getMessage().contains("ya está en uso"));
    }

    @Test
    void shouldSaveWhenNameIsUnique() throws Exception {
        ClientDto dto = new ClientDto();
        dto.setName("NuevoCliente");

        Client clienteActual = new Client();
        clienteActual.setId(1L);
        clienteActual.setName("AntiguoNombre");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(clienteActual));
        when(clientRepository.findByName("NuevoCliente")).thenReturn(null);

        clientService.save(1L, dto);

        verify(clientRepository, times(1)).save(clienteActual);
        assertEquals("NuevoCliente", clienteActual.getName());
    }
}
