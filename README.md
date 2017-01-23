# Reductor: Redux for Android

[![Download](https://api.bintray.com/packages/yarikx/Reductor/Reductor/images/download.svg)](https://bintray.com/yarikx/Reductor/Reductor/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Reductor-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/4402)
[![Build Status](https://travis-ci.org/Yarikx/reductor.svg?branch=master)](https://travis-ci.org/Yarikx/reductor)
[![Codecov](https://img.shields.io/codecov/c/github/Yarikx/reductor.svg?maxAge=2592000)](https://codecov.io/gh/Yarikx/reductor)


[Redux](http://redux.js.org/) inspired predictable state container library for Java/Android.

Reductor can help you make your state mutations easier to read, write and reason about.
It leverages annotation processing to validate correctness and generate boilerplate code at compile time, allowing you to express state reducers in a concise way as pure java functions.
As Redux it's based on three principles (from [Redux documentation](http://redux.js.org/docs/introduction/ThreePrinciples.html)):

* Single source of truth
* State is read-only
* Changes are made with pure functions

Key point of this implementation was to keep the original concept of Redux to reuse most of existing approaches
but provide nice Java API and preserve types as much as possible.  

### Reductor advantages:
* Lightweight
* Do not use reflection
* Follow implementation of [Redux](http://redux.js.org/)
* Allow to compose a state with [@CombinedState](#combine-reducers)
* Allow to define Reducers in typesafe way with [@AutoReducer](#autoreducer)

Note: This version is still under development, API may change till version 1.0

### Blog posts
* [Part 0: Prologue](https://yarikx.github.io/Reductor-prologue/)
* [Part 1: Introduction](https://yarikx.github.io/Reductor-introduction/)
* [Part 2: Composing reducers](https://yarikx.github.io/Reductor-composition/)

## Installation
```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.yheriatovych:reductor:x.x.x'
    apt     'com.yheriatovych:reductor-processor:x.x.x'
}
```

Apt dependency on `reductor-processor` is only necessary 
if you want to use features as [@CombinedState](#combine-reducers) and [@AutoReducer](#autoreducer)

## Getting started
### First example: Simple counter

```java
//state will be just integer
//define actions
@ActionCreator
interface CounterActions {
    String INCREMENT = "INCREMENT";
    String ADD = "ADD";
            
    @ActionCreator.Action(INCREMENT)
    Action increment();
    
    @ActionCreator.Action(ADD)
    Action add(int value);
}
  
//Define reducer                                                                 
@AutoReducer
abstract class CounterReducer implements Reducer<Integer> {
    @AutoReducer.InitialState
    int initialState() {
        return 0;
    }

    @AutoReducer.Action(
            value = CounterActions.INCREMENT,
            from = CounterActions.class)
    int increment(int state) {
        return state + 1;
    }

    @AutoReducer.Action(
            value = CounterActions.ADD,
            from = CounterActions.class)
    int add(int state, int value) {
        return state + value;
    }
            
    public static CounterReducer create() {
        return new CounterReducerImpl(); //Note: usage of generated class
    }
}

//Now you can create store and dispatch some actions
public static void main(String[] args) {
    //Now you can create store and dispatch some actions
    Store<Integer> counterStore = Store.create(CounterReducer.create());

    //you can access state anytime with Store.getState()
    System.out.println(counterStore.getState());             //print 0  
    
    //no need to implement CounterActions, we can do it for you
    CounterActions actions = Actions.from(CounterActions.class);

    //you can subscribe to state changes 
    counterStore.subscribe(state -> System.out.println(state));

    counterStore.dispatch(actions.increment()); //print 1  

    counterStore.dispatch(actions.increment()); //print 2  

    counterStore.dispatch(actions.add(5));      //print 7
}
```

## API

Main point of interaction with state is `Store` object. `Store` is actually container for your state. 

There are two ways of accessing the state inside the `Store`:
* Call `store.getState()` to get the state `Store` holds at the moment
* Call `store.subscribe(state -> doSomething(state))`. Calling subscribe will notify provided listener
every time state changes 

And only one way how to change the state: 
* Call `store.dispatch(action)` to deliver and process it by corresponding `Reducer`.

## Advanced use

### Combine Reducers

For one store you need one Reducer, however usually state is complex 
and it's good practice to separate logic to separate it into multiple reducers.

You can do it manually by creating Reducer which delegate reducing logic to 'smaller' reducers

```java
 //Complex state
 class Todo {
     List<String> items;
     String searchQuery;

     public Todo(List<String> items, String searchQuery) {
         this.items = items;
         this.searchQuery = searchQuery;
     }
 }
 
 //define reducer per sub-states
 class ItemsReducer implements Reducer<List<String>> {
     @Override
     public List<String> reduce(List<String> strings, Action action) {...} 
 }
 
 class QueryReducer implements Reducer<String> {
     @Override
     public String reduce(String filter, Action action) {...}
 }
 
 //define combined reducer
 class TodoReducer implements Reducer<Todo> {
     private ItemsReducer itemsReducer = new ItemsReducer();
     private QueryReducer queryReducer = new QueryReducer();
     
     @Override
     public Todo reduce(Todo todo, Action action) {
         //composing new state based on sub-reducers
         return new Todo(
                 itemsReducer.reduce(todo.items, action),
                 queryReducer.reduce(todo.searchQuery, action)
         );
     }
 }
```

This approach works but requires developer to write a bit of boilerplate of dispatching sub-states to sub-reducers.
That's why Reductor can do boring work for you. Just use `@CombinedState` to generate corresponding `Reducer`

```java
//Complex state
@CombinedState
interface Todo {
    List<String> items();
    String searchQuery();
}

//define reducer per sub-states
class ItemsReducer implements Reducer<List<String>> {
    @Override
    public List<String> reduce(List<String> strings, Action action) {return null;}
}

class QueryReducer implements Reducer<String> {
    @Override
    public String reduce(String filter, Action action) {return null;}
}

public static void main(String[] args) {
    //Using generated TodoReducer
    Reducer<Todo> todoReducer = TodoReducer.builder()
            .itemsReducer(new ItemsReducer())
            .searchQueryReducer(new QueryReducer())
            .build();
}
```

Note that `@CombinedState` annotated class needs to be interface or `AutoValue` abstract class.

### AutoReducer 

Consider following `Reducer` which manages `List<String>`. 

Note: [PCollections](http://pcollections.org/) library is used as implementation of persistent collections.
```java
// 
class ItemsReducer implements Reducer<List<String>> {
    @Override
    public List<String> reduce(List<String> items, Action action) {
        switch (action.type) {
            case "ADD_ITEM": {
                String value = (String) action.value;
                return TreePVector.from(items)
                        .plus(value);
            }
            case "REMOVE_ITEM": {
                String value = (String) action.value;
                return TreePVector.from(items)
                        .minus(value);
            }
            case "REMOVE_BY_INDEX": {
                int index = (int) action.value;
                return TreePVector.from(items)
                        .minus(index);
            }
            default:
                return items;
        }
    }
}
```

This way of writing reducers is canonical but have some disadvantages:
* Boilerplate code for switch statement
* Unsafe casting `action.value` to expected type

That's why Reductor has `@AutoReducer` to help developer by generating `reduce` method

```java
//AutoReducer annotated class should be abstract class which implement Reducer interface
@AutoReducer
abstract class ItemsReducer implements Reducer<List<String>> {
    
    //Each 'handler' should be annotated with @AutoReducer.Action
    @AutoReducer.Action("ADD_ITEM")
    List<String> add(List<String> state, String value) {
        return TreePVector.from(state)
                .plus(value);
    }

    @AutoReducer.Action("REMOVE_ITEM")
    List<String> remove(List<String> state, String value) {
        return TreePVector.from(state)
                .minus(value);
    }

    @AutoReducer.Action("REMOVE_BY_INDEX")
    List<String> removeByIndex(List<String> state, int index) {
        return TreePVector.from(state)
                .minus(index);
    }

    static ItemsReducer create() {
        //Note: ItemsReducerImpl is generated class
        return new ItemsReducerImpl();
    }
}
```

`@AutoReducer.Action` annotation supports declaring action creator.
That will check at compile time if there is such action creator with the same parameters. 
Example: [interface](https://github.com/Yarikx/reductor/blob/master/example/src/main/java/com/yheriatovych/reductor/example/reductor/notelist/NotesActions.java),
   [usage](https://github.com/Yarikx/reductor/blob/master/example/src/main/java/com/yheriatovych/reductor/example/MainActivity.java#L34).

### Defining action creators

In Reductor, an action is represented with class `Action`. 
It contains two fields: 
 - type: defines action id or name.
 - values: arrays of arbitrary objects that can be added as payload.
 
You can create this Actions ad-hoc, just before dispatching.
However usually it's more natural and readable way to encapsulate it into "Action creator" function, like:
```java
Action addItemToCart(int itemId, String name, int price) {
    return Action.create("CART_ADD_ITEM", itemId, name, price);
}
```

But that's not fun to write code for just bundling arguments inside.
That's why Reductor lets you define all your action creators as just interface functions.

```java
@ActionCreator
interface CartActions{
    @ActionCreator.Action("CART_ADD_ITEM")
    Action addItem(int itemId, String name, int price);

    @ActionCreator.Action("CART_REMOVE_ITEM")
    Action removeItem(int itemId);
}
```

Reductor will generate implementation. To get the instance just call `Actions.from(class)`:

```java
CartActions cartActions = Actions.from(CartActions.class);
store.dispatch(cartActions.addItem(42, "Phone", 350));
```

The information about actions structure is also used to check if `@AutoReducer` 
reducer actions have the same structure and name.

## Roadmap

* Support Kotlin data classes to use with `@CombinedState`
* Better documentation
* Minimize usage of generated code from a source code
* Add more example:
    - Async actions
    - Time-traveling
    - Dispatching custom actions with Middleware
    - Using Rx with store
