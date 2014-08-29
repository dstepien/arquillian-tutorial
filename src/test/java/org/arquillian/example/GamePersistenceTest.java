package org.arquillian.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GamePersistenceTest {

  private static final String[] GAME_TITLES = {
          "Super Mario Brothers",
          "Mario Kart",
          "F-Zero"
  };

  @PersistenceContext
  EntityManager entityManager;

  @Inject
  UserTransaction userTransaction;

  @Deployment
  public static Archive<?> createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackage(Game.class.getPackage())
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsWebInfResource("wildfly-ds.xml");
  }

  @Before
  public void preparePersistenceTest() throws Exception {
    clearData();
    insertData();
    startTransaction();
  }

  private void clearData() throws Exception {
    userTransaction.begin();
    entityManager.joinTransaction();

    System.out.println("Dumping old records...");

    entityManager.createQuery("delete from Game").executeUpdate();
    userTransaction.commit();
  }

  private void insertData() throws Exception {
    userTransaction.begin();
    entityManager.joinTransaction();

    System.out.println("Inserting records...");

    for (String title : GAME_TITLES) {
      Game game = new Game(title);
      entityManager.persist(game);
    }

    userTransaction.commit();

    // clear the persistence context (first-level cache)
    entityManager.clear();
  }

  private void startTransaction() throws Exception {
    userTransaction.begin();
    entityManager.joinTransaction();
  }

  @After
  public void commitTransaction() throws Exception {
    userTransaction.commit();
  }

  @Test
  @InSequence(1)
  public void shouldFindAllGamesUsingJpqlQuery() throws Exception {
    // given
    String fetchingAllGamesInJpql = "select game from Game game order by game.id";

    // when
    System.out.println("Selecting (using JPQL)...");

    List<Game> games = entityManager.createQuery(fetchingAllGamesInJpql, Game.class).getResultList();

    // then
    System.out.println("Found " + games.size() + " games (using JPQL): ");

    assertContainsAllGames(games);
  }

  private void assertContainsAllGames(Collection<Game> games) {
    final Set<String> gameTitles = new HashSet<>();

    for (Game game : games) {
      System.out.println("* " + game);
      gameTitles.add(game.getTitle());
    }

    assertEquals(GAME_TITLES.length, games.size());
    assertTrue(gameTitles.containsAll(Arrays.asList(GAME_TITLES)));
  }

  @Test
  @InSequence(2)
  public void shouldFindAllGamesUsingCriteriaApi() throws Exception {
    // given
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Game> criteria = builder.createQuery(Game.class);

    Root<Game> game = criteria.from(Game.class);
    criteria.select(game);

    // TIP: If you don't want to use the JPA 2 Metamodel,
    // you can change the get() method call to get("id")
    criteria.orderBy(builder.asc(game.get("id")));
    // No WHERE clause, which implies select all

    // when
    System.out.println("Selecting (using Criteria)...");

    List<Game> games = entityManager.createQuery(criteria).getResultList();

    // then
    System.out.println("Found " + games.size() + " games (using Criteria):");
    assertContainsAllGames(games);
  }
}