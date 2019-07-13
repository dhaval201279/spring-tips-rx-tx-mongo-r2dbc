package com.its.springtipsrxtxmongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void saveAll() throws Exception {
        StepVerifier
            .create(this.customerRepository.deleteAll())
            .verifyComplete();

        StepVerifier
            .create(this.customerService.saveAll("a.a@a.com","b.b@b.com","c.c@c.com", "d.d@d.com"))
            .expectNextCount(4)
            .verifyComplete();

        StepVerifier
            .create(this.customerRepository.findAll())
            .expectNextCount(4)
            .verifyComplete();

        StepVerifier
            .create(this.customerService.saveAll("e.e@e.com", "2"))
            .expectNextCount(1)
            .expectError()
            .verify();

        StepVerifier
            .create(this.customerRepository.findAll())
            .expectNextCount(4)
            .verifyComplete();

    }
}
