// src/App.tsx
import React from 'react';
import {useEffect} from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import BoardPage from './pages/BoardPage';
import RegisterPage from './pages/RegisterPage';
import Message from './pages/Message';
import ContentPage from './pages/ContentPage';
import CreateSubculture from './pages/CreateSubculture';
import PostPage from './pages/PostPage';
import FriendRequest from './pages/FriendRequest';
import WriteMessage from './pages/WriteMessage';
function App() {

    useEffect(() => {
      const hasToken = document.cookie.includes("XSRF-TOKEN=");
      if (!hasToken) {
        fetch(`${process.env.REACT_APP_API_BASE_URL}/api/csrf`, {
          credentials: 'include'
        });
      }
    }, []);


  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage/>} />
        <Route path="/profile" element={<ProfilePage/>} />
        <Route path="/board" element={<BoardPage/>} />
        <Route path="/register" element={<RegisterPage/>} />
        <Route path="/message" element={<Message/>} />
        <Route path="/content" element={<ContentPage/>} />
        <Route path="/create_subculture" element={<CreateSubculture/>} />
        <Route path="/post" element={<PostPage/>} />
        <Route path="/friend_request" element={<FriendRequest/>} />
        <Route path="/write_message" element={<WriteMessage/>} />
          {/* 추후 추가될 라우트들이 여기에 들어갈 예정 */}
       </Routes>
    </BrowserRouter>
  );
}

export default App;
