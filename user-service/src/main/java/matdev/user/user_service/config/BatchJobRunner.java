package matdev.user.user_service.config;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import matdev.user.user_service.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class BatchJobRunner implements CommandLineRunner {
private final JobLauncher jobLauncher;
private final Job populateDatabaseJob;
private final UsuarioRepository usuarioRepository;
private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Running batch job to populate database with fake users");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(populateDatabaseJob, jobParameters);
    }

    @PreDestroy
    public void cleanUp() {
        LOGGER.info("Cleaning up database after batch job");
        usuarioRepository.deleteAll();
        System.out.println("Database cleaned up");
    }
}
