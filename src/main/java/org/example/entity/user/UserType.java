package org.example.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserType {

    LESSOR("Lessor"),
    RENTER("Renter");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
