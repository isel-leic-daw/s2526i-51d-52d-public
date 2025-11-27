import { useEffect, useReducer } from "react";
import { fetchApi } from "../api";

// Generic State type
type State<T> =
  | { type: "begin" }
  | { type: "loading"; url: string }
  | { type: "loaded"; payload: T; url: string }
  | { type: "error"; error: Error; url: string };

// Generic Action type
type Action<T> =
  | { type: "load"; url: string }
  | { type: "success"; payload: T; url: string }
  | { type: "error"; error: Error }
  | { type: "reset" };

function unexpectedAction<T>(action: Action<T>, state: State<T>) {
  console.log(`Unexpected action ${action.type} in state ${state.type}`);
  return state;
}

// Generic reducer
function reducer<T>(state: State<T>, action: Action<T>): State<T> {
  switch (action.type) {
    case "reset":
      return { type: "begin" };
    case "load":
      return { type: "loading", url: action.url };
    case "success":
      if (state.type !== "loading") {
        return unexpectedAction(action, state);
      }
      return { type: "loaded", payload: action.payload, url: state.url };
    case "error":
      if (state.type !== "loading") {
        return unexpectedAction(action, state);
      }
      return { type: "error", error: action.error, url: state.url };
  }
}

// Options for the hook
interface UseFetchOptions {
  headers?: HeadersInit;
  method?: string;
  body?: string;
}

const firstState = { type: "begin" } as const;

export function useFetch<T>(
  url: string | undefined,
  options?: UseFetchOptions
): State<T> {
  const [state, dispatch] = useReducer(reducer<T>, firstState as State<T>);

  useEffect(() => {
    if (!url) {
      dispatch({ type: "reset" });
      return;
    }

    const urlToUse = url;
    let cancelled = false;
    const abortController = new AbortController();
    async function doFetch() {
      dispatch({ type: "load", url: urlToUse });
      try {
        const json = await fetchApi<T>(urlToUse, {
          ...options,
          signal: abortController.signal,
        });
        if (!cancelled) {
          dispatch({ type: "success", payload: json, url: urlToUse });
        }
      } catch (error) {
        if (!cancelled) {
          dispatch({
            type: "error",
            error:
              error instanceof Error ? error : new Error("An error occurred"),
          });
        }
      }
    }

    doFetch();

    return () => {
      cancelled = true;
      abortController.abort();
    };
  }, [url, options?.method, options?.body, options?.headers]);

  return state;
}
