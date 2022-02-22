package com.example.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blogpessoal.model.Postagem;
import com.example.blogpessoal.model.Tema;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {
	public List <Tema> findAllByDescricaoContainingIgnoreCase(String descricao);

	public Tema getById(Postagem postagem);




}
