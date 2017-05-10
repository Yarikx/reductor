# Reductor Observable

Combine RxJava streams to dispatch async actions and handle side effects.

### Epic

The core primitive is `Epic`. Epic is defined as a simple interface:

```java
public interface Epic<T> {
    Observable<Object> run(Observable<Action> actions, Store<T> store);
}
```

Epic is run once `Store` is created. 
It's basically a function that takes a stream of actions and returns stream of actions.
Each object emitted by returned Observable will be dispatched back to `Store`.

A simple example of async flow is Ping-Pong Epic which listens for `PING` action and responds with `PONG` after one second.

```java
Epic<String> pingPongEpic = (actions, store) ->            
        actions.filter(Epics.ofType("PING"))           
                .delay(1, TimeUnit.SECONDS)            
                .map(action -> Action.create("PONG")); 
```

Each 'PONG' message will be dispatched back to store to be handled by `Reducer`.

### Creating Middleware

To connect `Epic` to `Store`, create `EpicMiddleware` with provided epic.
Once middleware is created, it can be passed to `Store.create`:

```java
EpicMiddleware<String> middleware = EpicMiddleware.create(pingPongEpic);

Store<String> store = Store.create(reducer, middleware);
```

#### Combining epics

EpicMiddleware takes only one rootEpic in `EpicMiddleware.create`.
However, you can combine more than one epics into single Epic by using `Epics.combineEpics(epics)`.
This will simply merge all returned streams into one.
