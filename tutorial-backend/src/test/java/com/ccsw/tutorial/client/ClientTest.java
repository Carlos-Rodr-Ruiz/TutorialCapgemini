package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ClientTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveNewClientSuccessfully() throws Exception {
        ClientDto dto = new ClientDto();
        dto.setId(null);
        dto.setName("Nuevo Cliente");

        when(clientRepository.findByName("Nuevo Cliente")).thenReturn(null);

        clientService.save(dto);

        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void shouldThrowWhenClientNameAlreadyExists() throws Exception {
        ClientDto dto = new ClientDto();
        dto.setId(null);
        dto.setName("Cliente Duplicado");

        Client existingClient = new Client();
        existingClient.setId(99L); // ⚠️ IMPORTANTE: poner un ID para evitar el null

        when(clientRepository.findByName("Cliente Duplicado")).thenReturn(existingClient);

        Exception exception = assertThrows(Exception.class, () -> clientService.save(dto));

        assertTrue(exception.getMessage().contains("ya existe"));
    }

    @Test
    void shouldDeleteClient() throws Exception {
        Long clientId = 1L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));

        clientService.delete(clientId);

        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    void shouldThrowWhenClientToDeleteNotFound() {
        Long clientId = 2L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> clientService.delete(clientId));
    }
}
