# Use Case Model

## Part 1. Use Case Diagram

![alt text](./figures/UseCaseModel.png)


## Part 2. Use Case Descriptions

* Create Shopping List

    + Requirement: create an empty grocery list 
    + Pre-Condition: user must have app
    + Post Condition: named grocery list must exist
    + Scenario: User presses create list in UI, enters a list name, unpopulated list opens

* Open Shopping List:

    + Requirement: open previously created grocery list
    + Pre-Condition:  list must already exist
    + Post-Conditions: list is open
    + Scenario: user wants to retrieve previously created list, user clicks open list, selects list by name, populated list opens on screen

* Manage List Items
    
    + Requirements: Add, Delete, Modify Item qty
    + Pre-Conditions: new or pre-existing list must be open
    + Post-Conditions: items are added or removed, qty of items has increased or decreased
    + Scenario: 

        + If user wants to delete item, they select the item by scrolling to item, selecting and performing long press, they are then prompted to if they would like to:

            + Modify quantity
            + Delete item

        + If user wants to add item they click “+” button, user scrolls to item type and is given a list of items to select from, user selects item and is added to list  

* Request Item

    + Requirements: Add item to system that does not exist
    + Pre-Condition: User searched for item was not found
    + Post-Condition: New item is added to system
    + Scenario: User searches for item, is not found, user is prompted for item type, user enters item name, item is save to system

* Auto Save

    + Requirements: Save lists to system without user interaction
    + Pre-Condition: modifications to list exist
    + Post-Condition: list is saved
    + Scenario: user modifies list, system save changes

* Track Items In Basket

    + Requirements: Check/uncheck items in list
    + Pre-Condition: # of items in basket > 1
    + Post Condition: items checked/unchecked
    + Scenario: user can check/uncheck items in grocery list as they are shopping


