import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container, Typography, Button, TextField, Dialog, DialogTitle, DialogContent, DialogActions,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, MenuItem, Select, Alert
} from "@mui/material";
import api from "../ utils/axiosInstance"

function Home() {
  const [account, setAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [pin, setPin] = useState("");
  const [openPinDialog, setOpenPinDialog] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [pinError, setPinError] = useState("");

  useEffect(() => {
    fetchAccountInfo();
  }, []);

  // 获取当前用户账户信息
  const fetchAccountInfo = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/"); // 没有 token，返回登录页
      return;
    }
    await api.get("/v1/backend/Account/viewAccountInfo", {
      headers: { Authorization: `Bearer ${token}` },
    }).then(response => {
      if (response.data.code === 200) {
        setAccount(response.data.data);
      } else {
        console.error("获取账户信息失败:", response.data.message);
      }
    }).catch(error => {
      console.error("API 请求失败:", error);
    });
  };

  // 查询交易记录（弹出 PIN 验证框）
  const handleSearchClick = () => {
    setOpenPinDialog(true);
  };

  // 确认 PIN 后查询交易记录
  const handleConfirmPin = async () => {

    if (!pin || !account) return;

    setLoading(true);
    const token = localStorage.getItem("token");
    await api.get("/v1/backend/Account/statement", {
      headers: { Authorization: `Bearer ${token}` },
      params: {
        citizenId: account.citizenId,
        year: selectedYear,
        month: selectedMonth,
        pin: pin,
      },
    }).then(response => {
      if (response.data.code === 200) {
        setOpenPinDialog(false);
        setTransactions(response.data.data);
        console.log(response.data.data);
      } else {
        setPinError(response.data.msg);
        console.error("查询失败:", response.data.message);
      }
    }).catch(error => {
      console.error("API 请求失败:", error);
    }).finally(() => {
      setLoading(false);
      setPin("");
    });
  };

  // 退出登录
  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <Container maxWidth="md" sx={{ mt: 5 }}>
      <Typography variant="h4">Welcome Back</Typography>

      {account && (
        <Paper sx={{ p: 3, mt: 2 }}>
          <Typography variant="h6">Account Information</Typography>
          <Typography>Citizen ID: {account.citizenId}</Typography>
          <Typography>Current Balance: {account.currentBalance} THB</Typography>
          <Button
            variant="contained"
            color="primary"
            onClick={() => navigate("/transfer")}
            style={{ marginTop: "20px" }}
          >
            Transfer Money
          </Button>
        </Paper>
      )}
      {/* 年月选择 & 查询按钮 */}
      <Paper sx={{ p: 3, mt: 3 }}>
        <Typography variant="h6">Statement</Typography>
        <Select
          value={selectedYear}
          onChange={(e) => setSelectedYear(e.target.value)}
          sx={{ mr: 2, minWidth: 120 }}
        >
          {[...Array(10)].map((_, i) => (
            <MenuItem key={i} value={2025 - i}>{2025 - i}</MenuItem>
          ))}
        </Select>
        <Select
          value={selectedMonth}
          onChange={(e) => setSelectedMonth(e.target.value)}
          sx={{ mr: 2, minWidth: 120 }}
        >
          {Array.from({ length: 12 }, (_, i) => i + 1).map((month) => (
            <MenuItem key={month} value={month}>{month} Month</MenuItem>
          ))}
        </Select>
        <Button variant="contained" onClick={handleSearchClick}>Query</Button>
      </Paper>

      {/* PIN 输入框 */}
      <Dialog open={openPinDialog} onClose={() => setOpenPinDialog(false)}>
        <DialogTitle>Input PIN</DialogTitle>
        <DialogContent>
          {pinError && <Alert severity="error">{pinError}</Alert>} {/* 这里显示错误信息 */}
          <TextField
            label="PIN Code"
            type="password"
            fullWidth
            error={!!pinError} // 如果有错误，就变成红色
            helperText={pinError} // 显示错误信息
            value={pin}
            onChange={(e) => setPin(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => {
            setOpenPinDialog(false);
            setPinError("");
          }}>
            取消
          </Button>

          <Button variant="contained" color="primary" onClick={handleConfirmPin}>Confirm</Button>
        </DialogActions>
      </Dialog>

      {/* 交易记录表格 */}
      {transactions.length > 0 && (
        <TableContainer component={Paper} sx={{ mt: 3 }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell>Time</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Channel</TableCell>
                <TableCell>Amount</TableCell>
                <TableCell>Balance</TableCell>
                <TableCell>Remark</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions.map((tx, index) => (
                <TableRow key={index}>
                  <TableCell>{tx.date}</TableCell>
                  <TableCell>{new Date(`1970-01-01T${tx.time}Z`).toLocaleTimeString()}</TableCell>
                  <TableCell>{tx.type}</TableCell>
                  <TableCell>{tx.channel}</TableCell>
                  <TableCell>{tx.type.toLowerCase() === "debit" ? "-" : "+"} {tx.amount}</TableCell>
                  <TableCell>{tx.balance}</TableCell>
                  <TableCell>{tx.remark}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* 退出登录 */}
      <Button variant="outlined" color="secondary" sx={{ mt: 3 }} onClick={handleLogout}>
        Log out
      </Button>
    </Container>
  );
}

export default Home;

