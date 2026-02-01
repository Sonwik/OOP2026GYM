package repositories.core;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    boolean deleteById(ID id);

    default boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}
