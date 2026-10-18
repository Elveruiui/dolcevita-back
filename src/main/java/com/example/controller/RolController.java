package com.example.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Rol;
import com.example.service.RolService;

@RestController
@RequestMapping("/rol")
public class RolController {
	@Autowired
	private RolService rolS;
	
	@GetMapping("/listarrol")
	public ResponseEntity<?> listarRoles(){
		try {
			List<Rol> roles=rolS.listarAll();
			return ResponseEntity.ok(roles);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener roles");
		}
	}

}
