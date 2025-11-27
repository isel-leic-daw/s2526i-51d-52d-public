import { Link, Outlet, useNavigate } from "react-router";
import { useAuth } from "../AuthContext";
import "../styles/App.css";

export function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="layout-container">
      <nav className="layout-nav">
        <div className="layout-nav-left">
          <h1 className="layout-nav-title">Agenda App</h1>
          <div className="layout-nav-links">
            <Link to="/" className="layout-nav-link">
              Events
            </Link>
            <Link to="/create-event" className="layout-nav-link">
              Create Event
            </Link>
          </div>
        </div>
        <div className="layout-nav-right">
          {user ? (
            <>
              <Link to="/me" className="layout-nav-link">
                {user.name}
              </Link>
              <button onClick={handleLogout} className="layout-logout-btn">
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="layout-nav-link">
                Login
              </Link>
              <Link to="/register" className="layout-nav-link">
                Register
              </Link>
            </>
          )}
        </div>
      </nav>
      <main className="layout-main">
        <Outlet />
      </main>
    </div>
  );
}
