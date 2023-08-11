package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.user.Renter;
import org.example.repository.RenterRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RenterService extends BasicService<Renter, Long> {

    private final RenterRepository renterRepository;

    @Override
    protected JpaRepository<Renter, Long> getRepository() {
        return renterRepository;
    }
}
