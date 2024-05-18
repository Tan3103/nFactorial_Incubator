package com.spring.sweeties.repositories;

import com.spring.sweeties.models.Sweetness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SweetsRepository extends JpaRepository<Sweetness, Integer> {
    List<Sweetness> findByCatalog(String catalog);

    List<Sweetness> findByNameContainingIgnoreCase(String name);
}
