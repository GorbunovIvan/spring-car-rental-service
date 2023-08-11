package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.user.Lessor;
import org.example.repository.LessorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessorService extends BasicService<Lessor, Long> {

    private final LessorRepository lessorRepository;

    @Override
    protected JpaRepository<Lessor, Long> getRepository() {
        return lessorRepository;
    }
}
