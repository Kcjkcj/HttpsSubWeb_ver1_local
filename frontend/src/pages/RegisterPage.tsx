// src/pages/SignupPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Register.css';
import axios from '../api/axiosInstance';

const RegisterPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const navigate = useNavigate();

  const handleRegister = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await axios.post(`/api/register`, {
          email:email,
          accountPwd: password,
          accountName: name,
        });
      if (response.status===200 || response.status===201) {
        alert('회원가입 성공');
        navigate('/login');
      } else {
        alert('회원가입 실패');
      }
    } catch (error) {
      console.error('Register error:', error);
    }
  };


  return (
    <form onSubmit={handleRegister}>
      <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />
      <input type="text" value={name} onChange={(e) => setName(e.target.value)} placeholder="Name" required />
      <button type="submit">회원가입</button>
    </form>
  );
};

export default RegisterPage;
