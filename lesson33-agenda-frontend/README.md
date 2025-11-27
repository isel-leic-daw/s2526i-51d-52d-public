# Agenda Frontend Application

A React TypeScript frontend application for the Agenda HTTP API (folders lesson12), built with Vite and React Router.

## Features

- **User Authentication**: Register, login, and logout functionality
- **Event Management**: View all events, create new events, and view event details
- **Time Slot Management**: Add time slots to events and join available time slots
- **Real-time Updates**: Server-Sent Events (SSE) for live participant updates
- **Protected Routes**: Certain features require authentication
- **State Management**: idiomatic useReducer

## Tech Stack

- **React 19**: UI library
- **TypeScript**: Type-safe code
- **React Router 7**: Client-side routing
- **Vite**: Build tool and dev server

## Architecture Patterns

### State Management
- **useReducer Pattern**: All form components use useReducer:
  - State includes a `stage` property: `"editing" | "posting" | "succeed" | "failed"`
  - Actions use kebab-case naming: `input-change`, `post`, `success`, `error`
  - Error handling with `error: string | undefined`

### Custom Hooks
- **useFetch**: Generic hook for data fetching with loading/error states
- **useEventListener**: Manages Server-Sent Events connections and cleanup

### API Integration
- **Direct API calls**: EventDetails uses direct API methods instead of useFetch
- **SSE Integration**: Real-time updates for user join/leave events


## Application Structure

```
src/
├── components/             # React components
│   ├── Layout.tsx          # Main layout with navigation
│   ├── Login.tsx           # Login page (useReducer with stage pattern)
│   ├── Register.tsx        # Registration page (useReducer with stage pattern)
│   ├── UserProfile.tsx     # User profile page
│   ├── EventsList.tsx      # List all events (useFetch hook)
│   ├── EventDetails.tsx    # Event details with SSE updates (useReducer + direct API)
│   ├── CreateEvent.tsx     # Create event form (useReducer with stage pattern)
│   ├── CreateTimeSlot.tsx  # Create time slot form (useReducer with stage pattern)
│   ├── TimeSlotView.tsx    # Individual time slot component
│   └── ProtectedRoute.tsx  # Route guard for authenticated pages
├── hooks/
│   ├── useFetch.ts         # Generic fetch hook with loading/error states
│   └── useEventListener.ts # SSE (Server-Sent Events) management hook
├── api.ts                  # API client functions
├── types.ts                # TypeScript type definitions
├── AuthContext.tsx         # Authentication context provider (useFetch hook)
└── index.tsx               # Application entry point
```

## Routes

- `/` - Home page (Events list)
- `/login` - Login page
- `/register` - Registration page
- `/me` - User profile (protected)
- `/create-event` - Create event form (protected)
- `/events/:eventId` - Event details page

