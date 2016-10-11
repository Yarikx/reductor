# Reductor Releases #

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