// src/pages/ContentPage.tsx
import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/Content.css';
import { useEffect, useState } from 'react';
import axios from '../api/axiosInstance';

interface Post{
  postId:number;
  title:string;
  postBody:string;
  writeUser:Account;
  notice:boolean;
  createDt:string;
}

interface Comments{
  commentId:number;
  commentBody:string;
  createDt:string;
  post:Post;
  writeUser:Account;
}

interface Account{
  accountName:string;
  email:string;
}

const ContentPage = () => {
  const navigate = useNavigate();
  const [comments, setComments] = useState<Comments[]>([]);
  const [commentBody,setCommentBody ] = useState('');
  const location = useLocation();
  const subID = Number(localStorage.getItem('subID'));
  const {Post} = location.state || {};


    const handleWriteComment = async (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      try {
        const response = await axios.post(`/api/board/content`, {
            commentBody:commentBody,
            postId:Post.postId
          });

        if (response.status === 200 || response.status === 201) {
        } else {
          alert('댓글작성 실패');
        }
      } catch (error:any) {
        if(error.response && error.response.status === 403) {
        navigate('/login');
        } else {
        console.error('comment error:', error);
        alert('댓글작성 실패');
        }
      }
    };

  useEffect(() => {
    const fetchPosts = async () => {

      try {
        const response = await axios.get<Comments[]>(`${process.env.REACT_APP_API_BASE_URL}/api/board/content?post_id=${Post.postId}`, {
          withCredentials:true
        });
        setComments(response.data);
      } catch (error) {
        console.error("Error fetching profile data:",error);
      }
    };
    fetchPosts();
  }, [Post, navigate]);


  return (
    <div className="content-container">
      <button 
        className="back-button"
        onClick={() => navigate(`/board?SubID=${subID}`)}
      >
        게시판으로
      </button>
      <div className="content-box">
        <div className="content-header">
          <div className="title-row">
            <span>글 제목: {Post.title}</span>
            <span>작성자: {Post.writeUser.accountName}</span>
          </div>
        </div>

        <div className="content-body">
          <p>글 내용:</p>
          <div dangerouslySetInnerHTML={{__html:Post.postBody}}></div>
        </div>
      </div>

      
      <div className="comment-section">
        <h3>댓글</h3>
        <div className="comment-list">
            {comments.map((comment)=>
          <div key={comment.commentId} className="comment-item">     
            <p>{comment.writeUser.accountName} : {comment.commentBody}</p>     
          </div>
           )}   
        </div>
          <form onSubmit={handleWriteComment}>
            <input type="text" value={commentBody} onChange={(e) => setCommentBody(e.target.value)} placeholder="Name" required />
          <button type="submit">작성</button>
    </form>
      </div>
    </div>
  );
};

export default ContentPage;
