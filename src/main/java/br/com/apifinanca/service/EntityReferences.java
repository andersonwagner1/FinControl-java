package br.com.apifinanca.service;

import jakarta.persistence.EntityManager;

public final class EntityReferences {

    private EntityReferences() {
    }

    public static <T> T obter(EntityManager entityManager, Class<T> type, Long id) {
        return id == null ? null : entityManager.getReference(type, id);
    }
}
