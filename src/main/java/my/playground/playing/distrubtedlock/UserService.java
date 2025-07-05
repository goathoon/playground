package my.playground.playing.distrubtedlock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.playground.playing.distrubtedlock.lock.LockService;
import my.playground.playing.distrubtedlock.util.DelayUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final LockService lockService;
    private final ProtectedResourceRepository resourceRepository;
    private final DelayUtil delayUtil;

    public String upload(String id) throws InterruptedException {
        Boolean isSuccess = true;
        try{
            log.info("upload 수행");
            lockService.tryLock(id);

            // 비즈니스 로직 수행
            delayUtil.randomDelay();
            ProtectedResource resource = resourceRepository.findById(id)
                    .orElse(new ProtectedResource(id, 0));
            resourceRepository.save(new ProtectedResource(id, resource.getCount() + 1));

        } catch (DuplicateKeyException e) {
            log.info("DUP ERROR in Thread = {}", Thread.currentThread().getName());
            isSuccess = false;
            return "Fail (Lock)";
        } finally {
            if(isSuccess){
                lockService.unLock(id);
                log.info("UNLOCK in Thread = {}", Thread.currentThread().getName());
            }
        }

        return "Success";
    }

public String delete(String id) throws InterruptedException {
    Boolean isSuccess = true;
    try{
        log.info("delete 수행");
        lockService.tryLock(id);

        // 비즈니스 로직 수행
        delayUtil.randomDelay();
        resourceRepository.deleteById(id);

    } catch (DuplicateKeyException e) {
        log.info("DUP ERROR in Thread = {}", Thread.currentThread().getName());
        isSuccess = false;
        return "Fail (Lock)";
    } finally {
        if(isSuccess){
            lockService.unLock(id);
            log.info("UNLOCK in Thread = {}", Thread.currentThread().getName());
        }
    }
        return "Success";
    }
}
