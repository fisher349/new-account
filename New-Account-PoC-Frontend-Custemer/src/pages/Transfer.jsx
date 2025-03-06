import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Typography,
    TextField,
    Button,
    Stack,
    CircularProgress,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Alert,
    Card
} from '@mui/material';
import api from "../ utils/axiosInstance"

const Transfer = () => {
    const navigate = useNavigate();
    const [accountInfo, setAccountInfo] = useState(null);
    const [toAccount, setToAccount] = useState(null);
    const [toAccountNumber, setToAccountNumber] = useState('');
    const [remark, setRemark] = useState('');
    const [amount, setAmount] = useState('');
    const [pin, setPin] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [successOpen, setSuccessOpen] = useState(false);


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
                setAccountInfo(response.data.data);
            } else {
                setError('Get account infomation failed!');
            }
        }).catch(() => {
            setError('Get account infomation failed!');
        });
    };
    // 验证对方账户
    const handleVerify = async () => {
        if (!toAccountNumber) {
            setError('Please enter account number');
            return;
        }
        const token = localStorage.getItem("token");
        if (!token) {
            navigate("/"); // 没有 token，返回登录页
            return;
        }
        await api.get('/v1/backend/Account/getAccount', {
            headers: { Authorization: `Bearer ${token}` },
            params: { accountNumber: toAccountNumber }
        }).then(response => {
            if (response.data.code === 200) {
                if (response.data.data === null) {
                    setError('Recipient account not exist!');
                } else {
                    setToAccount(response.data.data);
                    setError('');
                }
            } else {
                setError('Get Recipient account infomation failed!');
            }
        }).catch(() => {
            setToAccount(null);
            setError(err.message);
            setError('Get Recipient account infomation failed!');
        });
    };

    // 处理转账提交
    const handleSubmit = async () => {
        // 输入验证
        if (!accountInfo || !toAccount || !amount || !pin) {
            setError('Please fill all fields');
            return;
        }

        const amountNum = parseInt(amount);
        if (isNaN(amountNum) || amountNum <= 0) {
            setError('Invalid amount');
            return;
        }

        if (amountNum > accountInfo.currentBalance) {
            setError('Amount exceeds available balance');
            return;
        }

        // 准备转账数据
        const transferData = {
            fromAccountNumber: accountInfo.accountNumber,
            toAccountNumber: toAccount.accountNumber,
            amount: amountNum,
            pin: pin,
            remark: remark
        };
        setLoading(true);
        await api.post('/v1/backend/transaction/transfer', transferData)
            .then(response => {
            if (response.data.code === 200) {
                setSuccessOpen(true);
                setTimeout(() => navigate('/home'), 3000);
            } else {
                setError("transfer failed, please retry");
            }
        }).catch(() => {
            setError(err.message);
        }).finally(() => {
            setLoading(false);
        });
    };

    return (
        <Box sx={{ maxWidth: 800, mx: 'auto', p: 3 }}>
            <Typography variant="h4" gutterBottom>Transfer Money</Typography>

            {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

            {/* 账户信息展示 */}
            {accountInfo && (
                <Card sx={{ p: 3, mb: 4 }}>
                    <Box sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        flexWrap: 'wrap',
                        gap: 2
                    }}>
                        <div>
                            <Typography variant="h6">Current Account</Typography>
                            <Typography>Account Number: {accountInfo.accountNumber}</Typography>
                            <Typography>Balance: ${accountInfo.currentBalance}</Typography>
                        </div>
                    </Box>
                </Card>
            )}
            <Stack spacing={3}>
                <Box sx={{
                    display: 'flex',
                    gap: 2,
                    flexDirection: { xs: 'column', sm: 'row' }
                }}>
                    <TextField
                        fullWidth
                        label="Recipient"
                        value={toAccountNumber}
                        onChange={(e) => setToAccountNumber(e.target.value.replace(/\D/g, ''))}
                    />
                    <TextField
                        fullWidth
                        label="Amount"
                        type="number"
                        value={amount}
                        onChange={(e) => setAmount(Math.max(0, parseInt(e.target.value) || 0))}
                        sx={{ flex: '1 1 300px' }}
                    />
                </Box>
                <Box sx={{
                    display: 'flex',
                    gap: 2,
                    flexWrap: 'wrap'
                }}>
                    <TextField
                        fullWidth
                        label="remark"
                        value={remark}
                        onChange={(e) => setRemark(e.target.value)}
                        sx={{ flex: '1 1 300px' }}
                    />
                </Box>
                <Button
                    variant="contained"
                    onClick={handleVerify}
                    sx={{ minWidth: 140 }}
                    disabled={!toAccountNumber}
                >
                    {loading ? <CircularProgress size={24} /> : 'verifing'}
                </Button>
                {toAccount && (
                    <Box sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        p: 2,
                        border: '1px solid',
                        borderColor: 'divider'
                    }}>
                        <Typography>Transfer to {toAccount.thaiName}({toAccount.accountNumber}) {amount} THB</Typography>
                    </Box>
                )}
                <Box sx={{
                    display: 'flex',
                    gap: 2,
                    flexWrap: 'wrap'
                }}>

                    <TextField
                        fullWidth
                        label="安全PIN码"
                        type="password"
                        value={pin}
                        onChange={(e) => setPin(e.target.value.replace(/\D/g, ''))}
                        sx={{ flex: '1 1 300px' }}
                    />
                </Box>

                <Button
                    variant="contained"
                    size="large"
                    onClick={handleSubmit}
                    disabled={!toAccount || loading}
                >
                    {loading ? <CircularProgress size={24} /> : '确认转账'}
                </Button>
            </Stack>

            {/* 成功弹窗 */}
            <Dialog open={successOpen}>
                <DialogTitle>Trasnfer success</DialogTitle>
                <DialogContent>
                    <Typography>Operation completed</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => navigate('/home')}>Return</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default Transfer;
