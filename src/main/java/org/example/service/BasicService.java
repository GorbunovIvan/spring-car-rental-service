package org.example.service;

import org.example.entity.HasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public abstract class BasicService<T extends HasId<ID>, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    public List<T> getAll() {
        return getRepository().findAll();
    }

    public T getById(ID id) {
        return getRepository().findById(id)
                .orElse(null);
    }

    public T create(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public T update(ID id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entity with id '" + id + "' is not found");
        }
        entity.setId(id);
        return getRepository().save(entity);
    }

    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }
}
