package org.arquillian.example;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BasketTest {

  @Inject
  private Basket basket;

  @Inject
  private OrderRepository repository;

  @Deployment
  public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClasses(Basket.class, OrderRepository.class, SingletonOrderRepository.class)
            .addPackage(Basket.class.getPackage())
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
  }

  @Test
  @InSequence(1)
  public void placeOrderShouldAddOrder() {
    basket.addItem("sunglasses");
    basket.addItem("suit");
    basket.placeOrder();

    assertEquals(0, basket.getItemCount());
    assertEquals(1, repository.getOrderCount());

    basket.addItem("raygun");
    basket.addItem("spaceship");
    basket.placeOrder();

    assertEquals(2, repository.getOrderCount());
    assertEquals(0, basket.getItemCount());
  }

  @Test
  @InSequence(2)
  public void orderShouldBePersistent() {
    assertEquals(2, repository.getOrderCount());
  }
}