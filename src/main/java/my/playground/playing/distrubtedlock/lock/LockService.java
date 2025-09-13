package my.playground.playing.distrubtedlock.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {
    private final LockRepository lockRepository;

    public void tryLock(String id) throws DuplicateKeyException {
        lockRepository.insert(LockDocument.of(id));
    }

    public void unLock(String id) {
        lockRepository.deleteById(id);
    }
}
