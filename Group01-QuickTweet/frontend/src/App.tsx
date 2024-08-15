import { useEffect, useState } from "react";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import { getUserData } from "./api/userApi";
import AdminDashboard from "./components/AdminDashboard";
import Feed from "./components/Feed";
import ForgotPasswordForm from "./components/ForgotPasswordForm";
import GroupPage from "./components/GroupPage";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import UserLoginPage from "./components/UserLoginPage";
import UserProfile from "./components/UserProfile";
import { INTERESTS } from "./constants";
import { User, initialUser } from "./types";

const App = () => {
  const [isAuthenticated, setAuth] = useState(false);
  const [user, setUser] = useState<User>(initialUser);

  const loggedInUserJSON: string | null = sessionStorage.getItem("user");
  useEffect(() => {
    const fetchData = async (username: string | undefined) => {
      const userData = await getUserData(username);
      setUser(userData);
      setAuth(true);
    };

    if (loggedInUserJSON != null) {
      const loggedInUser = JSON.parse(loggedInUserJSON);
      fetchData(loggedInUser.username);
    }
  }, [loggedInUserJSON]);

  return (
    <Router>
      <Routes>
        <Route
          path='/'
          element={
            isAuthenticated && user.role ? (
              <Feed user={user} setAuth={setAuth} />
            ) : (
              <UserLoginPage Form={LoginForm} formComponents={{ setAuth, user, setUser }} />
            )
          }
        />
        <Route
          path='/register'
          element={
            <UserLoginPage Form={RegisterForm} formComponents={{ setAuth, user, setUser }} />
          }
        />
        <Route
          path='/forgotpassword'
          element={
            <UserLoginPage Form={ForgotPasswordForm} formComponents={{ setAuth, user, setUser }} />
          }
        />
        <Route
          path='/profile/:username'
          element={
            isAuthenticated ? (
              <UserProfile currentUsername={user.username} />
            ) : (
              <UserLoginPage Form={LoginForm} formComponents={{ setAuth, user, setUser }} />
            )
          }
        />
        <Route
          path='/admin'
          element={
            user.role === "ADMIN" ? (
              <AdminDashboard user={user} setAuth={setAuth} />
            ) : (
              <h1>User not authorized to access this page.</h1>
            )
          }
        />
        {INTERESTS.map(({ value }) => (
          <Route
            key={value}
            path={`/groups/${value}`}
            element={<GroupPage user={user} interest={value} />}
          />
        ))}
        <Route path='*' element={<h1>Page not found</h1>} />
      </Routes>
    </Router>
  );
};

export default App;
