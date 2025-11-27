import { useAuth } from "../AuthContext";
import "../styles/App.css";

export function UserProfile() {
  const { user } = useAuth();

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="user-profile-container">
      <div className="user-profile-card">
        <h2>My Profile</h2>
        <div className="user-profile-info">
          <div className="user-profile-field">
            <strong>ID:</strong> {user.id}
          </div>
          <div className="user-profile-field">
            <strong>Name:</strong> {user.name}
          </div>
          <div className="user-profile-field">
            <strong>Email:</strong> {user.email}
          </div>
        </div>
      </div>
    </div>
  );
}
