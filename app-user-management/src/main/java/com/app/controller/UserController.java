package com.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.constant.MappingConstant;

@RestController
@RequestMapping(MappingConstant.USER)
public class UserController {

	@GetMapping(MappingConstant.ADD)
	public String addRole(@RequestParam String roleName) {
		
		return "Google";
	}
	
}
