package com.marklogic.spring.batch.sql;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.marklogic.migration.sql.SqlMigrator;
import com.marklogic.spring.batch.AbstractSpringBatchTest;

/**
 * "Debug" program for manual testing, as it depends on MySQL running locally.
 */
public class ReadFromMysqlDebug extends AbstractSpringBatchTest {

    private SqlMigrator sqlMigrator;

    @Before
    public void setup() {
        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setDriverClassName("com.mysql.jdbc.Driver");
        dmds.setUrl("jdbc:mysql://localhost:3306/sakila");
        dmds.setUsername("root");
        dmds.setPassword("admin");

        sqlMigrator = new SqlMigrator(dmds, getClient());
    }

    /**
     * Sakila data model notes:
     * <ol>
     * <li>Film and actor should be separate documents</li>
     * <li>actor and actor_info are 1:1</li>
     * <li>actor and film are many:many on film_actor. We'll store actor IDs on films.</li>
     * <li>film and category are many:many on film_category. We'll store the category name on films. Little annoying - a
     * film only has one category, it seems?</li>
     * <li>film and language is 1:many, and we want to replace the ID with the actual language value. TODO Not sure how
     * to do this yet since there are two languages.</li>
     * <li>film and film_text are 1:1</li>
     * <li>Store should be a separate document</li>
     * <li>Store will merge in address, city, and country.</li>
     * <li>A store has a film in inventory. Since there are only two stores in the database, we'll denormalize their
     * name onto each film, though in reality, I think doing a custom constraint would be more appropriate (which is
     * also easier to handle during migration).</li>
     * <li>I think a rental makes sense as a separate document, as we'd like to count them easily. I think we can stuff
     * a payment into one as well.</li>
     * <li>Customer is a separate document. It has a store_id, but since there's no name for a store, I think we just
     * leave it as-is.</li>
     * </ol>
     */
    @Test
    public void mysqlTest() {
        migrateActors();
        migrateFilms();
        migrateStores();
        migrateCustomers();
        migrateStaff();
        migrateRentals();
    }

    /**
     * For rentals, I think we keep "foreign key" elements for staff and customer - I don't think any denormalizing
     * would make sense for either of those. But I do think we want to denormalize in the film name. Inventory also has
     * store_id on it - there's not much useful to do with that (why doesn't a store have a name in this dataset???), so
     * we'll just stick store_id on the rental document as well.
     */
    protected void migrateRentals() {
        sqlMigrator.migrate(
                "SELECT rental.rental_id, rental.rental_date, rental.return_date, rental.staff_id, rental.customer_id, rental.last_update"
                        + ", inventory.store_id, film.title as filmTitle"
                        + ", payment.amount as \"payment/amount\", payment.payment_date as \"payment/paymentDate\" FROM rental"
                        + " LEFT JOIN inventory ON rental.inventory_id = inventory.inventory_id"
                        + " LEFT JOIN film ON inventory.film_id = film.film_id"
                        + " LEFT JOIN payment on rental.rental_id = payment.rental_id",
                "rental");
    }

    protected void migrateStaff() {
        sqlMigrator.migrate("SELECT staff.*, " + buildAddressSelect() + " FROM staff"
                + " LEFT JOIN address ON staff.address_id = address.address_id"
                + " LEFT JOIN city ON address.city_id = city.city_id"
                + " LEFT JOIN country ON city.country_id = country.country_id", "staff");
    }

    protected void migrateCustomers() {
        sqlMigrator.migrate("SELECT customer.*, " + buildAddressSelect() + " FROM customer"
                + " LEFT JOIN address ON customer.address_id = address.address_id"
                + " LEFT JOIN city ON address.city_id = city.city_id"
                + " LEFT JOIN country ON city.country_id = country.country_id", "customer");
    }

    protected void migrateActors() {
        sqlMigrator.migrate("SELECT actor.*, actor_info.film_info FROM actor "
                + "LEFT JOIN actor_info ON actor.actor_id = actor_info.actor_id", "actor");
    }

    protected void migrateFilms() {
        sqlMigrator.migrate("SELECT film.*, film_text.description as filmText, category.name as category, "
                + "actor.actor_id as \"actor/id\", actor.first_name as \"actor/firstName\", actor.last_name as \"actor/lastName\" "
                + "FROM film LEFT JOIN film_category ON film.film_id = film_category.film_id "
                + "LEFT JOIN category ON film_category.category_id = category.category_id "
                + "LEFT JOIN film_actor ON film.film_id = film_actor.film_id "
                + "LEFT JOIN actor ON film_actor.actor_id = actor.actor_id "
                + "LEFT JOIN film_text ON film.film_id = film_text.film_id", "film");
    }

    protected void migrateStores() {
        sqlMigrator.migrate("SELECT store.store_id, " + buildAddressSelect() + " FROM store"
                + " LEFT JOIN address ON store.address_id = address.address_id "
                + " LEFT JOIN city ON address.city_id = city.city_id"
                + " LEFT JOIN country ON city.country_id = country.country_id", "store");
    }

    private String buildAddressSelect() {
        return "address.address as \"address/address\", address.address2 as \"address/address2\", "
                + "address.district as \"address/district\", address.postal_code as \"address/postalCode\", "
                + "address.phone as \"address/phone\", city.city as \"address/city\", country.country as \"address/country\"";
    }
}
