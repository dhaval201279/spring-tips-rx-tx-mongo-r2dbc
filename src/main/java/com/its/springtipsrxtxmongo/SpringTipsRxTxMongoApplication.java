package com.its.springtipsrxtxmongo;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
@Slf4j
public class SpringTipsRxTxMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTipsRxTxMongoApplication.class, args);
	}

	@Bean
	TransactionalOperator transactionalOperator(ReactiveTransactionManager rxTxnMgr) {
		log.info("Creating Rx Txn Mgr");
		return TransactionalOperator.create(rxTxnMgr);
	}

	/**
	 * transaction mangager for reactive mongo db
	 * OR
	 * transaction manager for r2dbc
	 *
	 * So at a given time (based on usage of postgresql or mongodb, one of them will be used
	 * */
	/*@Bean
	ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory rxMongoDbFactory) {
		log.info("Creating Rx Mongodb factory");
		return new ReactiveMongoTransactionManager(rxMongoDbFactory);
	}*/

	@Bean
	ReactiveTransactionManager transactionManager(ConnectionFactory cf) {
		log.info("Creating r2dbc txn manager ");
		return new R2dbcTransactionManager(cf);
	}

	/**
	 * For PgSQL we need to also configure Connection factory as below
	 * */
	@Bean
	ConnectionFactory postgresConnectionFactory(@Value("${spring.r2dbc.url}") String url) {
		return ConnectionFactories.get(url);
	}


}

@Service
@RequiredArgsConstructor
@Slf4j
class CustomerService {
	private final CustomerRepository customerRepository;

	private final TransactionalOperator transactionalOperator;

	/**
	 * We can use @Transactional i.e. declarative txn.
	 * If we use declarative txn than we dont need to do either transactionalOperator.execute OR
	 * .as(this.transactionalOperator :: transactional)
	 * */

	@Transactional
	public Flux<Customer> saveAll(String...  emails) {
		log.info("Entering CustomerService : saveAll with params {} ", emails);

		/*Flux<Customer> customerFlux = Flux
										.just(emails)
										.map(email -> new Customer(null, email))
										.flatMap(this.customerRepository :: save)
										.doOnNext(customer -> Assert.isTrue(customer.getEmail().contains("@"), "The email must contain '@'!"));

		log.info("Email saved and now leaving saveAll after executing rx txn");
		return this.transactionalOperator.execute(reactiveTransaction -> customerFlux);*/

		/**
		 * Other way to do the same thing as above i.e. by using 'as' API as shown below
		 *
		 * Since its an alternative  above / below code will be commented
		 * */

		Flux<Customer> customerFlux = Flux
				.just(emails)
				.map(email -> new Customer(null, email))
				.flatMap(this.customerRepository :: save)
				.doOnNext(customer -> Assert.isTrue(customer.getEmail().contains("@"), "The email must contain '@'!"))
				.as(this.transactionalOperator :: transactional);

		log.info("Email saved and now leaving saveAll after executing rx txn");
		return customerFlux;
	}
}

/**
 * For pgsql change string to integer
 * */
interface CustomerRepository extends ReactiveCrudRepository<Customer, String> {

}

/**
 * For pgsql remove @Document and change String id to Integer Id*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
class Customer {
	@Id
	private String id;
	private String email;
}
