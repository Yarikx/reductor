# Reductor Releases #

### Version 0.14.0 - June 29, 2017

- Behaviour change: Initialize `Store` before initializing `Middleware`s.
Fixes the issue of having uninitialized store/state on `create` method, making it crash if action is dispatched in `Middleware.create`.

### Version 0.13.2 - May 11, 2017

- Fixed lost state update when listener dispatches an action on `Cursors.forEach`.
- Fixed processor to fail on projects without AutoValue in classpath.

### Version 0.13.1 - January 12, 2017

- Rebuilt libraries with latest version of Retrolambda (2.4.0).
No need to add retrolambda-related proguard rules anymore.
- For processor, made dependency to auto-value compile only.

### Version 0.13.0 - January 2, 2017

#### New Module: ReductorObservable

Added [ReductorObservable](https://github.com/Yarikx/reductor/tree/master/reductor-observable) module that mimics [redux-observable](https://github.com/redux-observable/redux-observable).
This allows to dispatch async actions and do side-effects in composable way using RxJava.

### Version 0.12.0 - December 17, 2016

#### New feature: Dispatcher and Cursor

Introduced two interfaces, both implemented by `Store`:

```java
public interface Dispatcher {
    void dispatch(Object action);
}
```

```java
public interface Cursor<State> {
    State getState();
    Cancelable subscribe(StateChangeListener<State> listener);
}
```

* `Cursor` responsible for retrieving the state and observing updates.
* `Dispatcher` responsible for dispatching actions.

That allows you to expose `Store` functionality as interfaces to your components.

##### State mapping

Having `Cursor` interface, it is possible now to have `map` operation similar to `rx.Observable.map(func)`.
For that purpose new helper class `Cursors` with `map` function was introduced. 
Usage example: 

```java
Store<AppState> store = ...;
Cursor<User> userCursor = Cursors.map(store, state -> state.getUser());
//use mapped cursor in component 
UserPresenter presenter = new UserPresenter(userCursor);
```

Mapped cursors allow your components to depend only on specific substate they need, instead of depending on the whole state, 
 knowing on how to get to particular substate. 

Current implementation of `map` will only propagate unique values, so it's more efficient to pass mapped `Cursor`
to upper levels of application.

#### Other (breaking) changes

* `store.forEach(listener)` was moved as static function to `Cursors.forEach(cursor, listeners)`;
* `action.getValue(index)` now returns generic parameter instead of Object.

##### 

#### Breaking changes

### Version 0.11.1 - November 30, 2016

Replaced `@Generated` annotation with just a comment

### Version 0.11.0 - November 27, 2016

#### Breaking changes

##### Middleware can be initialized with Store

Now `Middleware` is split into two functional interfaces. 
So if old middleware signature was `(store, action, nextDisaptcher) => ()`,
the new one will be `(store, nextDispatcher) => (action) => ()`. 
This middleware structure is much closer to original Redux middleware.

##### New generated Action Creators (experimental)

If `@AutoReducer.Action(generateActionCreators = true)`, 
reductor will generate [ActionCreator](https://github.com/Yarikx/reductor#defining-action-creators) interface.
The interface will have the same name as reducer with 'Actions' suffix. 
Old generated static nested `ActionCreator` classes in reducer implementations are no longer supported.

As you may use generated action creators directly, explicitly defining action creator interface is recommended.
However generated action creators can be used to prototype your reducer faster, 
and then generated interface can be copied to the source code.

#### Other

Added `Generated` annotation to all emitted code.


### Version 0.10.0 - November 3, 2016

#### New feature: Action creators

This feature allows defining action creators as interface separately from reducer.
This allows:

 - Separate action definition and reducer handler.
 - Get rid of direct usage of some of the generated code (action creators generated in `AutoReducer` reducers).
 - Per-action validation for `AutoReducer` reducers to have the same values as in corresponding action creator. 
 - Actions can be "shared" between multiple reducers.
 - One reducer can handle actions from multiple Action creators.
 
To create an instance of Action creator `Actions.from(class)` can be used. 
Example: [interface](https://github.com/Yarikx/reductor/blob/master/example/src/main/java/com/yheriatovych/reductor/example/reductor/notelist/NotesActions.java),
   [usage](https://github.com/Yarikx/reductor/blob/master/example/src/main/java/com/yheriatovych/reductor/example/MainActivity.java#L34).
   
"Old" action creators in `AutoReducer` is deprecated but still supported (will be removed in next version).

#### Other changes
 - Action class now has multiple values (`Object[] values` instead of `Object value`).
 - New `Store` method `forEach`: similar to `subscribe` but propagate initial value immediately. 
 - New module `reductor-rxjava2` to observe `Store` as RxJava2 `Observable` or `Flowable`.


### Version 0.9.3 - October 11, 2016
  - Fix reducer code generation for `@CombinedState` class with no properties 

### Version 0.9.2 - October 11, 2016

#### New Features
  - Big update on `@CombinedState`. 
  Now `@AutoValue` value classes are supported as combined state!
  Interfaces as combined state are still supported.
  
#### Other improvements
Update code generator for `@CombinedState` reducers. 
  - Remove unnecessary state object allocation if all sub-states are the same.
  - Use boxed version of sub-state types in reducer, to remove boxing/unboxing when passing to sub-reducers.

### Version 0.9.1 - October 10, 2016
  - Rename `reductor-rx` maven artifact to `reductor-rxjava`
  - Updated `rxjava` version to 1.2.1

### Version 0.9.0 - October 7, 2016
Major update aimed to improve and simplify initial state population.

#### API changes
  - Every `Reducer` now is responsible to populate initial state if `null` is received as current state in `reduce` method;
  - `@CombinedState` reducers now initialize state automatically with empty values (`null` for objects, `false` for booleans, `0` for numbers, `\u0000` for char);
  - `@AutoReducer` annotated reducers now can specify how initial state can be created
  by introducing method with `@AutoReducer.InitialState` annotation (See `@AutoReducer.InitialState` javadoc or [example](https://github.com/Yarikx/reductor/blob/master/example/src/main/java/com/yheriatovych/reductor/example/reducers/NotesListReducer.java#L17));
  - Added `Store.create` overload without `inialState` argument;
  - Now before `Store` is created, special internal action is dispatched to populate initial state;
  
#### Other fixes
  - Simplify generated code for `@CombinedState` reducers;
  - Add more compile-time checks and tests;
  - Support primitives as state return and argument types for `@AutoReducer.Action` methods.
  
### Version 0.8.2 - October 6, 2016
  - @CombinedState: Exclude static and default methods from processing as substates. 
  Allowing to use default methods as selectors 
  - Cover library and processors with tests

### Version 0.8.1 - August 25, 2016
  - Added more compile time validation for @AutoReducer and @CombinedState processors
  - Processor code cleanup

### Version 0.8.0 - Initial Release