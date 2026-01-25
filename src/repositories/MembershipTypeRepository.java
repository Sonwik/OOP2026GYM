package repositories;

import entities.edu.aitu.oop3.entities.MembershipType;

import java.util.Optional;

public interface MembershipTypeRepository {
    Optional<MembershipType> findById(long id);
}