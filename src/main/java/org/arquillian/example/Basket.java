package org.arquillian.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@SessionScoped
public class Basket implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<String> items;

  @Inject
  private OrderRepository repository;

  @PostConstruct
  private void initialize() {
    items = new ArrayList<>();
  }

  public void addItem(String item) {
    items.add(item);
  }

  public List<String> getItems() {
    return Collections.unmodifiableList(items);
  }

  public int getItemCount() {
    return items.size();
  }

  public void placeOrder() {
    repository.addOrder(items);
    items.clear();
  }
}
