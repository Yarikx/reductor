# Reductor

[ ![Download](https://api.bintray.com/packages/yarikx/Reductor/Reductor/images/download.svg) ](https://bintray.com/yarikx/Reductor/Reductor/_latestVersion)

[Redux](http://redux.js.org/) inspired predictable state container library for Java/Android.

Reductor can help you make your state mutations easier to read, write and reason about.
As Redux it's based on three principles (from [Redux documentation](http://redux.js.org/docs/introduction/ThreePrinciples.html)):

* Single source of truth
* State is read-only
* Changes are made with pure functions

Key point of this implementation was to keep the original concept of Redux to reuse most of existing approaches
but provide nice Java API and preserve types as much as possible.  

### Reductor advantages:
* Lightweight (109 loc w/o comments)
* Do not use reflection
* Follow implementation of [Redux](http://redux.js.org/)
* Allow to compose a state with [@CombinedState](#combine-reducers)
* Allow to define Reducers in typesafe way with [@AutoReducer](#autoreducer)

Note: This version is still under development, API may change till version 1.0

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
public static final String ACTION_INCREMENT = "ACTION_INCREMENT";
public static final String ACTION_ADD = "ACTION_ADD";
  
//Define reducer                                                                 
public class CounterReducer implements Reducer<Integer> {        
    @Override                                                    
    public Integer reduce(Integer state, Action action) {        
        switch (action.type){                                    
            case ACTION_INCREMENT:                               
                return state + 1;
            case ACTION_ADD:
                return state + (int) action.value;  
            default:                                             
                return state;                                    
        }                                                        
    }                                                            
}                                

//(Optional) Create action creators
public class CounterActionCreator {         
    public static Action increment() {             
        return new Action(ACTION_INCREMENT);
    }                                       
                                            
    public static Action add(int n) {             
        return new Action(ACTION_DECREMENT, n);
    }                                       
}  


//Now you can create store and dispatch some actions
public static void main(String[] args) {
    //Now you can create store and dispatch some actions
    Store<Integer> counterStore = Store.create(new CounterReducer(), 0);
    
    //you can access state anytime with Store.getState()
    System.out.println(counterStore.getState());             //print 0  

    //you can subscribe to state changes 
    counterStore.subscribe(state -> System.out.println(state));

    counterStore.dispatch(CounterActionCreator.increment()); //print 1  

    counterStore.dispatch(CounterActionCreator.increment()); //print 2  

    counterStore.dispatch(CounterActionCreator.add(5));      //print 7  
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

## Roadmap

* Support [AutoValue](#https://github.com/google/auto/tree/master/value) to be used with `@CombinedState`
* Better documentation
* Add more example:
    - Async actions
    - Time-traveling
    - Dispatching custom actions with Middleware
    - Using Rx with store

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

Note that `@CombinedState` annotated class needs to be interface with only accessor methods.

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

Reductor will also generate static `ActionCreator` class with typesafe action creation functions
```java
//Static class in generated ItemsReducerImpl
public static class ActionCreator {
  public static Action add(String value) {
    return new Action("ADD_ITEM", value);
  }
  public static Action remove(String value) {
    return new Action("REMOVE_ITEM", value);
  }
  public static Action removeByIndex(int index) {
    return new Action("REMOVE_BY_INDEX", index);
  }
}

//Usage
Action action = ItemsReducerImpl.ActionCreator.add("foobar");
```
