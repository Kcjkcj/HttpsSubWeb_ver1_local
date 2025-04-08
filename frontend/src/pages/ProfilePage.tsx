// src/pages/ProfilePage.tsx

import React, {useState, useEffect, useCallback} from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Profile.css';
import axios from '../api/axiosInstance';
import { isAxiosError } from 'axios';

interface Account {
  accountId:number;
  email:string;
  createDt : string;
  accountName : string;
}


const ProfilePage = () => {
  const navigate = useNavigate();
  const [accountInfo, setAccountInfo] = useState<Account | null>(null);
  const [friendList, setFriendInfo] = useState<Account[]>([]);

  const handleAuthError = useCallback((error: any) => {
    if (isAxiosError(error)) {
      const status = error.response?.status;
      if (status === 401 || status === 403) {
        alert("로그인이 필요합니다.");
        navigate("/login");
      }
    }
    console.error("Error:", error);
  }, [navigate]);
  
  useEffect(() => {
    const fetchProfileData = async () => {
      try {
        const { data } = await axios.get('/api/profile');
        setAccountInfo(data);
      } catch (error) {
        handleAuthError(error);
      }
    };
    fetchProfileData();
  }, [handleAuthError]);
  
   //useEffect 내부에서 navigate 사용 중이므로 의존성 배열에 넣어줘야 함

   useEffect(() => {
    const fetchFriends = async () => {
      try {
        const { data } = await axios.get('/api/profile/myFriends');
        setFriendInfo(data);
      } catch (error) {
        handleAuthError(error);
      }
    };
    fetchFriends();
  }, [handleAuthError]);
   //useEffect 내부에서 navigate 사용 중이므로 의존성 배열에 넣어줘야 함


  const deleteFriend = async (friend: Account) => {
    try {
      const response = await axios.post('/api/profile/myFriends', {
        accountId: friend.accountId,
        accountName: friend.accountName,
        email: friend.email,
        createDt: friend.createDt
      });
  
      if (response.status === 200 || response.status === 201) {
        alert("해당 친구를 삭제하였습니다.");
        setFriendInfo(prev => prev.filter(f => f.accountId !== friend.accountId));
      } else {
        alert("삭제 실패");
      }
  
    } catch (error) {
      handleAuthError(error);
    }
  };



  return (
    <div className="profile-container">
      <div className="profile-grid">
        <div className="profile-item main-page">
          <button onClick={() => navigate("/")}>메인 페이지</button>
        </div>
        <div className="profile-header">
          <h1>나의 정보</h1>
        </div>
      {
      accountInfo && (
          <div className="profile-info">
            <table className="profile-info-table">
              <tbody>
                <tr>
                  <td>이메일:</td>
                  <td>{accountInfo.email}</td>
                </tr>
                <tr>
                  <td>활동명:</td>
                  <td>
                    {accountInfo.accountName}
                  </td>
                </tr>
                <tr>
                  <td>가입일:</td>
                  <td>{accountInfo.createDt}</td>
                </tr>
              </tbody>
            </table>
          </div>
        )}

        {/* 친구 리스트 세로 스크롤 */}
        <div className="friend-slider">
          <h2>함께하는 친구들</h2>
          <div className="friend-list">
            <ul>
              {friendList.map((friend, index) => (
                <li key={index}>
                  <p>이름: {friend.accountName} </p>
                  <p>이메일: {friend.email}</p>
                  <p>가입일: {friend.createDt}</p>
                  <button className='delete-friend-btn'
                  onClick={() => {
                    if(window.confirm("정말로 삭제하시겠습니까?"))
                      { 
                        deleteFriend(friend)
                      };
                      
                    }}>
                    친구 삭제
                  </button>
                  <br></br>
                  </li>
              ))}
            </ul>
          </div>
        </div>

        <br></br>
        {/* 친구 요청 확인 버튼 */}
        <div className="profile-item verify">
          <button onClick={() => navigate("/message")}>
            메시지
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;