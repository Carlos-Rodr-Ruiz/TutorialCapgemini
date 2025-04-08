package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public List<Client> findAll() {
        return (List<Client>) clientRepository.findAll();
    }

    @Override
    public void save(ClientDto data) throws Exception {

        Client client;

        if (data.getId() == null) {
            client = new Client();
        } else {
            // Editar cliente existente
            client = clientRepository.findById(data.getId()).orElseThrow(() -> new Exception("Cliente no encontrado"));
        }

        // Verificar si hay otro cliente con el mismo nombre
        Client otroConMismoNombre = clientRepository.findByName(data.getName());

        if (otroConMismoNombre != null && !otroConMismoNombre.getId().equals(data.getId())) {
            throw new Exception("Ese nombre ya está en uso por otro cliente");
        }

        client.setName(data.getName());

        clientRepository.save(client);
    }

    @Override
    public Client get(Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }

        this.clientRepository.deleteById(id);
    }
}
