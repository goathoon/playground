package my.playground.playing.distrubtedlock.lock;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LockRepository extends MongoRepository<LockDocument, String> {
}
