package repositories;

import entities.edu.aitu.oop3.entities.MembershipTransaction;
import repositories.core.Repository;

import java.util.List;

public interface MembershipTransactionRepository extends Repository<MembershipTransaction, Long> {
    List<MembershipTransaction> findByMemberId(long memberId);
}
