package matdev.user.user_service.config;


import com.github.javafaker.Faker;

import java.security.SecureRandom;
import java.util.ArrayList;

import matdev.user.user_service.utils.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.repository.UsuarioRepository;
import matdev.user.user_service.entity.Usuario;


import java.util.List;
import java.util.Random;

@Profile("dev")
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);
    private final Random random= new SecureRandom();
    @Bean
    public ListItemReader<Usuario> reader() {
        LOGGER.info("Generating fake users");
        Faker faker = new Faker();
        List<Usuario> usuarios = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Usuario usuario = new Usuario();
            usuario.setUsername(faker.name().username());
            usuario.setEmail(faker.internet().emailAddress());
            usuario.setPassword("password");
            usuario.setRole(Role.ROLE_ADMIN);
            usuario.setTenantId("tenant" + random.nextInt(10));
            usuarios.add(usuario);
        }

        return new ListItemReader<>(usuarios);

       
    }

    @Bean
    public ItemProcessor <Usuario, Usuario> processor() {
        LOGGER.info("Processing fake users");
        return usuario -> {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuario;
        };
    }

    @Bean 
    public ItemWriter<Usuario> writer() {
        LOGGER.info("Saving fake users to database");
        return usuarios -> {
            for (Usuario usuario : usuarios) {
                usuarioRepository.save(usuario);
            }
        };
    }
    @Bean
    
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        LOGGER.info("Creating step");
        return new StepBuilder("step1", jobRepository)
                .<Usuario, Usuario>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
        
    }
    @Bean
    public Job populateDatabaseJob(JobRepository jobRepository, Step step1) {
        LOGGER.info("Creating job");
        return new JobBuilder("populateDatabase", jobRepository)
                .start(step1)
                .build();
    }


}
