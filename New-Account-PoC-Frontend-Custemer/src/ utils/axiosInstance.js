import axios from "axios";
import { useNavigate } from "react-router-dom";

// 创建 Axios 实例
const api = axios.create({
  //podman
  baseURL: 'http://localhost:8081/api',
  //local
  // baseURL: 'http://localhost:8080', 
  headers: {
    'Content-Type': 'application/json',
  }
});

// 请求拦截器：自动附加 Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：如果未授权，自动跳转到登录页
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token"); // 清除 Token
      window.location.href = "/"; // 强制跳转到登录页
    }
    return Promise.reject(error);
  }
);

export default api;
