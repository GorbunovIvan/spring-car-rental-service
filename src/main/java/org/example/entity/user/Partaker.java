package org.example.entity.user;

import org.example.entity.HasId;

public interface Partaker<T> extends HasId<Long> {
    T get();
}
