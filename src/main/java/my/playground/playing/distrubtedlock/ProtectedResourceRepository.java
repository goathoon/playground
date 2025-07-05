package my.playground.playing.distrubtedlock;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtectedResourceRepository extends MongoRepository<ProtectedResource, String> {
}
