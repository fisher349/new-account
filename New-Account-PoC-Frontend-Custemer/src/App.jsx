import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Home from "./pages/Home";
import Transfer from "./pages/Transfer";
import AuthGuard from "./components/AuthGuard";

function App() {
  return (
    <Router>
      <Routes>
        {/* 公开页面：仅登录页 */}
        <Route path="/" element={<Login />} />

        {/* 受保护的页面（所有子页面都需要登录） */}
        <Route element={<AuthGuard />}>
          <Route path="/home" element={<Home />} />
          <Route path="/transfer" element={<Transfer />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
