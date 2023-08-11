package org.example.entity;

public interface HasId<T> {
    T getId();
    void setId(T id);
}
