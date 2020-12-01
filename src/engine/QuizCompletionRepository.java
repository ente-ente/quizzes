package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface QuizCompletionRepository extends PagingAndSortingRepository<QuizCompletion, Integer> {

    @Query("SELECT q FROM QuizCompletion q WHERE q.user = :user")
    Page<QuizCompletion> findQuizCompletionsForUser(@Param("user") User user, Pageable pageable);


}
