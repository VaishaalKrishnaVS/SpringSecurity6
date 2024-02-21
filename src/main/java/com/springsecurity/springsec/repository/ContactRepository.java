package com.springsecurity.springsec.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.springsecurity.springsec.model.Contact;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {


}
