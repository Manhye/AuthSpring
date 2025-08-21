package com.example.authspring;

import com.example.authspring.domain.UserRepository;
import com.example.authspring.dto.request.LoginRequest;
import com.example.authspring.dto.request.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSpringApplicationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper om;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void clean(){
		userRepository.deleteAll();
	}

	@Test
	void signup_success_and_duplicate_fail() throws Exception{

		var body = om.writeValueAsString(new SignupRequest("SAE HEE", "123412314", "Alan"));

		mvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("SAE HEE"));

		mvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"));
	}

	@Test
	void login_success_and_fail() throws Exception{

		mvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new SignupRequest("SAE HEE", "123412314", "Alan"))))
				.andExpect(status().isOk());

		mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new LoginRequest("SAE HEE", "123412314"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());

		mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new LoginRequest("SAE HEE", "wrongpw"))))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
	}

	@Test
	void admin_grant_access_control() throws Exception{

		mvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new SignupRequest("SAE HEE", "123412314", "Alan"))))
				.andExpect(status().isOk());

		mvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new SignupRequest("admin", "12341234", "admin"))))
				.andExpect(status().isOk());

		var token = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new LoginRequest("admin", "12341234"))))
				.andReturn().getResponse().getContentAsString();

		String jwt = new ObjectMapper()
				.readTree(token)
				.get("token")
				.asText();

		mvc.perform(patch("/admin/users/{id}/roles", 1)
				.header("Authorization", "Bearer " + jwt))
				.andExpect(status().isForbidden());

		var adminUser = userRepository.findByUsername("admin").orElseThrow();
		adminUser.getRoles().add(com.example.authspring.domain.Role.ADMIN);
		userRepository.saveAndFlush(adminUser);

		var token2 = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new LoginRequest("admin", "12341234"))))
				.andReturn().getResponse().getContentAsString();

		String jwtAdmin = new ObjectMapper()
				.readTree(token2)
				.get("token")
				.asText();

		mvc.perform(patch("/admin/users/1/roles", 1)
				.header("Authorization", "Bearer " + jwtAdmin))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.roles").isArray())
				.andExpect(jsonPath("$.roles").value(hasItem("ADMIN")));
	}

}
