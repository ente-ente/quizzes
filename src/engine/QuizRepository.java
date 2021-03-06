package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface QuizRepository extends PagingAndSortingRepository<Quiz, Integer> {
    @Query("SELECT q FROM Quiz q WHERE q.user = :user")
    Page<Quiz> findQuizzesForUser(@Param("user") User user, Pageable pageable);

    Page<Quiz> findAll(Pageable pageable);
}
