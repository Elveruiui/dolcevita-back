package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Rol;
import com.example.repository.RolRepository;

@Service
public class RolService {
	@Autowired
	private RolRepository rolRepo;
	
	public List<Rol> listarAll(){
		return rolRepo.findAll()
;	}

}
