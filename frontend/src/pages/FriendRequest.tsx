// src/pages/FriendRequestPage.tsx
import React,{useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Message.css';
import axios from '../api/axiosInstance';

interface SearchResult{
    accountId:number;
    nickname:string;
}

const FriendRequestPage = () => {
    const [nickname, setNickname] = useState('');
    const [searchResult, setSearchResult] = useState<SearchResult[] | null>(null);
    const navigate = useNavigate();

    const searchFriend = async () => {
        const formData = new FormData();
        formData.append("nickname",nickname);
        try {
          const response = await axios.post(`/api/profile/search`, formData);
      
          if (response.status===200 || response.status===201) {
            setSearchResult(response.data);
          } else {
            setSearchResult(null); // 이전 검색 결과 초기화
            alert("해당 닉네임의 사용자를 찾을 수 없습니다.");
          }
        } catch (error:any ) {
          if(error.response?.status === 403){
            alert('로그인 해주세요');
            navigate('/login');
          }
          setSearchResult(null); // 이전 검색 결과 초기화
          console.error("Error searching for friend:", error);
          alert("해당 닉네임의 사용자를 찾을 수 없습니다.");
        }
      };
      
      
  
      const sendFriendRequest = async (friendID: number) => {
        try {
            const response = await axios.post(`/api/postMessage`, {
              receiveId:friendID,
              notice:false,
              request:true,
              messageBody:"친구 요청"
          });
          if(response.status===200 || response.status===201)
          alert("친구 요청이 성공적으로 전송되었습니다.");
        } catch (error:any) {
          if(error.response?.status === 403){
            alert('로그인 해주세요');
            navigate('/login');
          }
          console.error("Error sending friend request:", error);
          alert("친구 요청 전송에 실패했습니다.");
        }
      };
      

  
return (
    <div className="friend-request-container">
      <input
        type="text"
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
        placeholder="닉네임을 입력하세요"
      />
      <button onClick={searchFriend}>검색</button>
      {searchResult && (
        <div className="search-result">
          {searchResult.map((result, index) => (
            <div key={index}>
              <p>닉네임: {result.nickname}</p>
              <button onClick={() => sendFriendRequest(result.accountId)}>친구 요청 보내기</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
  
  };
  
export default FriendRequestPage;