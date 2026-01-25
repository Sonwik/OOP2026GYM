package repositories;

import entities.edu.aitu.oop3.entities.Member;

import java.time.LocalDate;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(long id);
    void updateMembership(long memberId, long typeId, LocalDate endDate);
}