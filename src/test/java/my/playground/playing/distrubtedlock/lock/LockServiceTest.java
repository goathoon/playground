package my.playground.playing.distrubtedlock.lock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class LockServiceTest {
    @Autowired
    LockService lockService;
    @Autowired
    LockRepository lockRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void insert_withDuplicatedIdOfLockDocument_shouldThrowDuplicatedKeyException() {
        lockService.tryLock("same_id");
        assertThatThrownBy(() -> lockService.tryLock("same_id"))
                .isInstanceOf(DuplicateKeyException.class);

    }
}