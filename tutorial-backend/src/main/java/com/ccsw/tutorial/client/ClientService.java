package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;

import java.util.List;

public interface ClientService {

    void save(ClientDto data) throws Exception;

    Client get(Long id);

    List<Client> findAll();

    void delete(Long id) throws Exception;
}
