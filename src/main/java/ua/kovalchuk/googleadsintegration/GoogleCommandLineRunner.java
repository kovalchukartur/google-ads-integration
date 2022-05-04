package ua.kovalchuk.googleadsintegration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoogleCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("\uD83D\uDD25 Run GoogleCommandLineRunner");
    }

}
