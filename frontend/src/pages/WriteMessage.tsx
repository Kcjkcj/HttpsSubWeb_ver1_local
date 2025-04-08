import React, { useState, useEffect, useCallback } from 'react';
import Select from 'react-select';
import '../styles/Register.css';
import { useNavigate } from 'react-router-dom';
import axios from '../api/axiosInstance';

interface FriendOption {
  value: number;
  label: string;
}

const WriteMessagePage = () => {
  const [options, setOptions] = useState<FriendOption[]>([]);
  const [selectedFriend, setSelectedFriend] = useState<FriendOption | null>(null);
  const [messageBody, setMessageBody] = useState('');
  const navigate = useNavigate();


  // 서버에서 친구 리스트 가져오기
  const fetchFriends = useCallback (async () => {
    try {
      const response = await axios.get(`/api/profile/myFriends`);
      if (response.status===200 || response.status===201) {
        const friendOptions = response.data.map((friend: { accountId: number; accountName: string }) => ({
          value: friend.accountId,
          label: friend.accountName,
        }));
        setOptions(friendOptions); // 옵션 업데이트
      } else {
        alert('친구 리스트를 가져오지 못했습니다.');
      }
    } catch (error:any) {
      if(error.response?.status === 403){
        alert('로그인 해주세요');
        navigate('/login');
      }
      console.error('Error fetching friends:', error);
    }
  }, [navigate]);

  // 메시지 보내기
  const sendMessage = async () => {
    if (!selectedFriend) {
      alert('메시지를 보낼 친구를 선택하세요.');
      return;
    }
    try {
      const response = await axios.post(`/api/postMessage`, {
          receiveId: selectedFriend.value,
          messageBody,
        });
      if (response.status===200 || response.status===201) {
        alert('메시지 전송 성공');
        setMessageBody('');
        setSelectedFriend(null);
      } else {
        alert('메시지 전송 실패');
      }
    } catch (error:any) {
      if(error.response?.status === 403){
        alert('로그인 해주세요');
        navigate('/login');
      }
      console.error('Error sending message:', error);
    }
  };

  useEffect(() => {
    fetchFriends(); // 컴포넌트 마운트 시 친구 리스트 가져오기
  }, [fetchFriends]);

  const handleChange = (option : FriendOption | null) => {
    setSelectedFriend(option)
  };
  return (
    <div className="message-container">
      <h1>메시지 보내기</h1>

      {/* 친구 선택 드롭다운 */}
      <Select
        options={options}
        onChange={handleChange}
        placeholder="친구를 선택하세요"
        isSearchable={true} // 검색 가능 설정
      />

      {/* 메시지 작성 텍스트 박스 */}
      <textarea
        value={messageBody}
        onChange={(e) => setMessageBody(e.target.value)}
        placeholder="메시지를 입력하세요"
        required
      />

      {/* 메시지 전송 버튼 */}
      <button onClick={sendMessage}>보내기</button>
    </div>
  );
};

export default WriteMessagePage;
