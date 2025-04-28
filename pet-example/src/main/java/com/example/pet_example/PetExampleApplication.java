package com.example.pet_example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
public class PetExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetExampleApplication.class, args);
    }

    record Dog(int id, String name, String owner, String description) {
    }

    @Bean
    ItemReader<Dog> dogReader(@Value("classpath:/dogs.csv") Resource resource) {
        return new FlatFileItemReaderBuilder<Dog>()
                .resource(resource)
                .name("dogReader")
                .delimited().names("person,id,name,description,dob,owner,gender,image".split(","))
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> new Dog(fieldSet.readInt("id"),
                        fieldSet.readString("name"),
                        fieldSet.readString("owner"),
                        fieldSet.readString("description")))
                .build();
    }

    @Bean
    ItemWriter<Dog> dogWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Dog>()
                .dataSource(dataSource)
                .sql("INSERT INTO dog (id, name, owner, description) VALUES (?, ?, ?, ?)")
                .assertUpdates(true)
                .itemPreparedStatementSetter(new ItemPreparedStatementSetter<Dog>() {
                    @Override
                    public void setValues(Dog item, PreparedStatement ps) throws SQLException {
                        ps.setInt(1, item.id());
                        ps.setString(2, item.name());
                        ps.setString(3, item.owner());
                        ps.setString(4, item.description());
                    }
                })
                .build();
    }

    @Bean
    Step step(JobRepository repository,
              PlatformTransactionManager transactionManager,
              ItemReader<Dog> reader,
              ItemWriter<Dog> writer) {
        return new StepBuilder("step", repository)
                .<Dog, Dog>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    Job job(JobRepository repository, Step step) {
        return new JobBuilder("job", repository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}