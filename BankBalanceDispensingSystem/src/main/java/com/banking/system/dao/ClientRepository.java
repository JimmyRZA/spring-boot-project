package com.banking.system.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.banking.system.domain.Client;

public interface ClientRepository extends CrudRepository<Client, Integer>{
	
	public Optional<Client> findById(int id);

}
