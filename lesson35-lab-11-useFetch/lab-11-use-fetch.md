### 1. `useEffect`

Implement a custom React Hook named **`useFetch`**, using **`useReducer`** and
**`useEffect`**.

---

### 📝 **Description**

Implement a custom React Hook, **`useFetch`**, that performs an HTTP GET request
whenever the given URL changes. The hook must:

* Use **`useReducer`** for state management.
* Use **`useEffect`** to trigger the fetch.
* Enforce valid state transitions inside the reducer.

Your hook must also **avoid state updates when the effect is cancelled**.

### 📌 **Requirements**

### **1. State type**

```ts
type State =
  | { type: 'begin' }
  | { type: 'loading'; url: string }
  | { type: 'loaded'; payload: string; url: string }
  | { type: 'error'; error: Error; url: string };
```

### **2. Action type**

```ts
type Action =
  | { type: 'load'; url: string }
  | { type: 'success'; payload: string; url: string }
  | { type: 'error'; error: Error };
```

## 2. PeriodicFetcher

Create a React component that receives a URL and a time period (in milliseconds).
This component must use the previously implemented `useFetch` hook.

When mounted, the component must periodically issue an HTTP GET request to the provided URL.

The result of each request must be displayed in a `<textarea>`:

* If the result is a successful HTTP response, the `<textarea>` must contain the response body.
* If the result is an exception, the `<textarea>` must contain the exception text.

The component must also display an indication of whether an HTTP request is currently pending.

The component must react to changes in its properties. At any moment, there must be **at most one pending HTTP request**.

Finally, create an example of how to use this component by building a small form with two input fields: one for the URL and another for the time period.
