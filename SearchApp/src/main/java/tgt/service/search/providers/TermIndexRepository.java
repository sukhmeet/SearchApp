package tgt.service.search.providers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TermIndexRepository extends  MongoRepository<SearchIndexEntity, String>  {

	Optional<SearchIndexEntity> findById(String term);

	List<SearchIndexEntity> findByTermRegex(String string);
}
