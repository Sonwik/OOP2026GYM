package repositories;
import entities.edu.aitu.oop3.entities.FitnessClass;
import java.util.Optional;
public interface FitnessClassRepository {
    Optional<FitnessClass> findById(long id);
}
