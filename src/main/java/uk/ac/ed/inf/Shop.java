package uk.ac.ed.inf;

/**
 * Class encapsulating all the info about the shops in the
 * delivery service for the web server's JSON data.
 *
 * @author Selina Zhan (s1953505)
 */
public class Shop {
  String name;
  String location; //as a WhatThreeWords address
  Item[] menu; //the items on sale in the shop
  
  /**
   * Inner class encapsulating each item in the menu
   */
  public class Item {
    String item; //the item name
    int pence; //the item price in pence
  }
}
