package com.DIS.Practica2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByTituloStartsWithIgnoreCase(String lastName);
}