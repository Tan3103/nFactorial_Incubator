package com.spring.sweeties.services;

import com.spring.sweeties.models.Sweetness;
import com.spring.sweeties.repositories.SweetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SweetsService {

    private final SweetsRepository productRepository;

    @Autowired
    public SweetsService(SweetsRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Sweetness> findAll() {
        return productRepository.findAll();
    }

    public Sweetness findById(int id) {
        Optional<Sweetness> foundProduct = productRepository.findById(id);

        return foundProduct.orElse(null);
    }

    public List<Sweetness> findByCatalog(String catalog) {
        return productRepository.findByCatalog(catalog);
    }

    public List<Sweetness> findByName(String name) {
        if (name == null || name.isEmpty())
            return productRepository.findAll();

        System.out.println();
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public void save(Sweetness product) {
        productRepository.save(product);
    }

    @Transactional
    public void delete(int id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void update(int id, Sweetness updatedProduct) {
        updatedProduct.setId(id);
        productRepository.save(updatedProduct);
    }

    public Sweetness findOne(int id) {
        Optional<Sweetness> foundPerson = productRepository.findById(id);

        return foundPerson.orElse(null);
    }
}
