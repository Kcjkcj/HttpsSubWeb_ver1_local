// src/pages/FriendRequestPage.tsx
import React,{useState, useEffect, useCallback} from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Message.css';
import axios from '../api/axiosInstance';

interface Message{
  messageId:number;
  sendId:number;
  receiveId:number;
  notice:boolean;
  requst:boolean;
  messageBody:string;
  createDt:string;
  sendUser:Account;
}

interface Account{
  accountName:string;
  email:string;
}



const MessagePage = () => {
  const navigate = useNavigate();
  const [messages, setMessages] = useState<Message[]>([]);
  const [messageType, setMessageType] = useState<string>('received');


  const fetchMessages = useCallback(async (type: string) => {
    try {
      let url = '/api/getMessage';
      if (type === 'sent') url += '?sent';
      else if (type === 'notice') url += '?notice';
      else if (type === 'request') url += '?request';

      const response = await axios.get<Message[]>(url);
      setMessages(response.data);
    } catch (error: any) {
      console.error('Error fetching messages:', error);
      if (error.response?.status === 403 || error.response?.status === 401) {
        navigate('/login');
      }
    }
  }, [navigate]);
  
    useEffect(() => {
      fetchMessages(messageType);
    }, [messageType, fetchMessages]); //이는 useEffect에 의존성을 넣어주는 것
   //useEffect 내부에서 navigate 사용 중이므로 의존성 배열에 넣어줘야 함

 
   const handleRequestResponse = async (message: Message, approve: boolean) => {
    try {
      await axios.post('/api/profile/request', {
        message,
        approve
      });
      fetchMessages(messageType); // 갱신
    } catch (error: any) {
      if (error.response?.status === 403) {
        alert('로그인 해주세요');
        navigate('/login');
      }
      console.error('친구 요청 처리 중 오류:', error);
    }
  };
  
  return (
    <div className="friend-container">
      <button 
        onClick={() => navigate('/profile')}
      >
        나의 계정으로
      </button>

    <select value={messageType} onChange={(e)=> 
      setMessageType(e.target.value)}>
        <option value="received">내가 받은 메시지</option>
        <option value="sent">내가 보낸 메시지</option>
        <option value="notice">공지사항</option>
        <option value="request">친구 요청</option>
      </select>
    

      <div className="request-list">
        {messages.map((message) => (
          <li key={message.messageId} className="request-item">
            <div className="request-info">
              <div className="nickname">닉네임: {message.sendUser.accountName}</div>
              <div className="send-time">보낸 시간: {message.createDt}</div>
              <div className='message-body'>{message.messageBody}</div>
            </div>
            {messageType === 'request' && message.messageBody.includes('친구 요청') && (
            <div className="request-actions">
              <button className="action-btn accept" onClick={
                ()=>handleRequestResponse(message, true)}>승인</button>
              <button className="action-btn reject" onClick={
                ()=>handleRequestResponse(message, false)}>거절</button>
            </div>
            )}
          </li>
        ))}
            <button onClick={() => navigate("/friend_request")}>
            사용자 검색
          </button>
          <button onClick={() => navigate("/write_message")}>
            메시지 작성
          </button>
      </div>
    </div>
    
  );
};

export default MessagePage;
