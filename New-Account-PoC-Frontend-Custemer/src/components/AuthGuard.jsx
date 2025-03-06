import { Navigate, Outlet } from "react-router-dom";

// 认证拦截组件
const AuthGuard = () => {
  const isAuthenticated = !!localStorage.getItem("token");
  return isAuthenticated ? <Outlet /> : <Navigate to="/" />;
};

export default AuthGuard;

