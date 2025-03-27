package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class ClientIT {

    @Autowired
    ClientService clientService;

    @Test
    void shouldReturnClientsList() {
        List<Client> clients = clientService.findAll();
        assertNotNull(clients);
        assertTrue(clients.size() > 0, "Lista de clientes es de: " + clients.size());
    }
}
