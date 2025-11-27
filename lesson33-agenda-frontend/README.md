# Agenda Frontend Application

A React TypeScript frontend application for the Agenda API, built with Vite and React Router.

## Features

- **User Authentication**: Register, login, and logout functionality
- **Event Management**: View all events, create new events, and view event details
- **Time Slot Management**: Add time slots to events and join available time slots
- **Protected Routes**: Certain features require authentication
- **Responsive UI**: Clean and intuitive user interface

## Tech Stack

- **React 19**: UI library
- **TypeScript**: Type-safe code
- **React Router 7**: Client-side routing
- **Vite**: Build tool and dev server

## Getting Started

### Prerequisites

- Node.js and npm installed
- Backend API running on `http://localhost:8080`

### Installation

Dependencies are already installed. If you need to reinstall:

```bash
npm install
```

### Running the Application

Start the development server:

```bash
npm run dev
```

The application will be available at `http://localhost:3000`

### Building for Production

```bash
npm run build
```

## Application Structure

```
src/
├── components/          # React components
│   ├── Layout.tsx      # Main layout with navigation
│   ├── Login.tsx       # Login page
│   ├── Register.tsx    # Registration page
│   ├── UserProfile.tsx # User profile page
│   ├── EventsList.tsx  # List all events
│   ├── EventDetails.tsx # Event details with time slots
│   ├── CreateEvent.tsx # Create event form
│   └── ProtectedRoute.tsx # Route guard for authenticated pages
├── api.ts              # API client functions
├── types.ts            # TypeScript type definitions
├── AuthContext.tsx     # Authentication context provider
└── index.tsx           # Application entry point
```

## Routes

- `/` - Home page (Events list)
- `/login` - Login page
- `/register` - Registration page
- `/me` - User profile (protected)
- `/create-event` - Create event form (protected)
- `/events/:eventId` - Event details page

## API Integration

The application integrates with the Agenda API backend:

- Base URL: `http://localhost:8080/api`
- Authentication: Bearer token stored in localStorage
- Error handling with custom ApiError class

## Features by Page

### Events List
- View all available events
- Click on an event to view details

### Event Details
- View event information
- See time slots (if any)
- Add new time slots (authenticated users)
- Join time slots (authenticated users)

### Create Event
- Create a new event with title, description
- Choose selection type (SINGLE or MULTIPLE)
- Requires authentication

### User Profile
- View current user information
- Requires authentication

## Authentication Flow

1. Register a new account at `/register`
2. Login at `/login` with your credentials
3. Token is stored in localStorage
4. Protected routes check for valid token
5. Logout clears the token and redirects to login

## Styling

The application uses inline styles for simplicity. Key design features:
- Clean navigation bar with authentication status
- Card-based layout for events
- Form validation and error messages
- Responsive design with max-width containers
- Consistent color scheme

