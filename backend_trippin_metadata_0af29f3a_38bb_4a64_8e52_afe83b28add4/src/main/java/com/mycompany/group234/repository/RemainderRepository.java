package com.mycompany.group234.repository;


import com.mycompany.group234.model.Remainder;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;


@Component
public class RemainderRepository extends SimpleJpaRepository<Remainder, String> {
    private final EntityManager em;
    public RemainderRepository(EntityManager em) {
        super(Remainder.class, em);
        this.em = em;
    }
    @Override
    public List<Remainder> findAll() {
        return em.createNativeQuery("Select * from \"trippin_metadata\".\"Remainder\"", Remainder.class).getResultList();
    }
}