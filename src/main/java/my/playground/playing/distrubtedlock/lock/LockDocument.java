package my.playground.playing.distrubtedlock.lock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Document
@NoArgsConstructor
@Getter
public class LockDocument {

    @Id
    private String id;
    private Date createdDate;

    private LockDocument(String id) {
        this.id = id;
        this.createdDate = Date.from(Instant.now());
    }

    public static LockDocument of(String id) {
        return new LockDocument(id);
    }
}
