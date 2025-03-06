import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { TextField, Button, Container, Typography, Card, CardContent, Alert } from "@mui/material";
import api from "../ utils/axiosInstance"

function Login() {

  const [credentials, setCredentials] = useState({
    citizenId: '',
    password: ''
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async () => {

    setError(""); // 清空错误信息
    api.post('/v1/backend/auth/login', credentials).then(response => {
      // 获取 code 值
      if(response.data.code === 200){
        const token = response.data.data; // 这里是 JWT Token
        localStorage.setItem("token", token); // 保存 Token
        navigate("/home"); // 跳转到 Home 页面
      }else{
        setError("Login failed, please check your credencial");
      }
    })
    .catch(error => {
      setError("Login failed, please check your network or credencial");
    });
  }
    return (
      <Container maxWidth="xs" sx={{ mt: 10 }}>
        <Card elevation={3}>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              用户登录
            </Typography>
            {error && <Alert severity="error">{error}</Alert>}
            <TextField
              fullWidth
              label="Citizen ID"
              variant="outlined"
              margin="normal"
              value={credentials.citizenId}
              onChange={(e) => setCredentials({ ...credentials, citizenId: e.target.value })}
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              variant="outlined"
              margin="normal"
              value={credentials.password}
              onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
            />
            <Button fullWidth variant="contained" color="primary" sx={{ mt: 2 }} onClick={handleLogin}>
              登录
            </Button>
          </CardContent>
        </Card>
      </Container>
    );
  }

  export default Login;
