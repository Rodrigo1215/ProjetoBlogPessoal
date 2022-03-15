package com.example.blogpessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import com.example.blogpessoal.model.Usuario;
import com.example.blogpessoal.model.UsuarioLogin;
import com.example.blogpessoal.repository.UsuarioRepository;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *  A Classe UsuarioService implementa as regras de negócio do Recurso Usuario.
 *  
 *  Regras de negócio são as particularidades das funcionalidades a serem 
 *  implementadas no objeto, tais como:
 *  
 *  1) O Usuário não pode estar duplicado no Banco de dados
 *  2) A senha do Usuario deve ser criptografada
 *  
 *  Observe que toda a implementação dos metodos Cadastrar, Atualizar e 
 *  Logar estão implmentadas na classe de serviço, enquanto a Classe
 *  Controller se limitará a checar a resposta da requisição.
 */

 /**
 * A Anotação @Service indica que esta é uma Classe de Serviço, ou seja,
 * implementa todas regras de negócio do Recurso Usuário.
 */


@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
		
		
		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		
		return Optional.of(usuarioRepository.save(usuario));
	
	}
		
		
	public Optional<Usuario> logarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
		
			
		return Optional.of(usuarioRepository.save(usuario));
	
	}

	/**
	 *  Atualizar Usuário
	 * 
	 *  Checa se o usuário já existe no Banco de Dados através do método findById, 
	 *  porquê não é possíve atualizar 1 usuário inexistente. 
	 *  Se não existir retorna um Optional vazio.
	 *  
	 *  isPresent() -> Se um valor estiver presente retorna true, caso contrário
	 *  retorna false.
	 * 
	 */
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		
		if(usuarioRepository.findById(usuario.getId()).isPresent()) {
			
			
			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			
			return Optional.ofNullable(usuarioRepository.save(usuario));
			
		}
		
		
		return Optional.empty();
	
	}	

	
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

		if (usuario.isPresent()) {

			
			if (compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {

				
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get().setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));
				usuarioLogin.get().setSenha(usuario.get().getSenha());

				
				return usuarioLogin;

			}
		}	
	
		return Optional.empty();
		
	}

	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.encode(senha);

	}

	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.matches(senhaDigitada, senhaBanco);

	}


	private String gerarBasicToken(String usuario, String senha) {

		String token = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));
		return "Basic " + new String(tokenBase64);

	}

}