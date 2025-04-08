// src/pages/BoardPage.tsx
import React, {useState, useEffect} from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Board.css';
import axios from '../api/axiosInstance';
interface Posts{
  postId:number;
  title:string;
  postBody:string;
  writeUser:Accont;
  notice:boolean;
  createDt:string;
}

interface Accont{
  accountName:string;
  email:string;
}



const BoardPage = () => {
  const navigate = useNavigate();
  const [posts, setPosts] = useState<Posts[]>([]);

  const subID = Number(localStorage.getItem('subID'));
  const subTitle = (localStorage.getItem('subTitle'));

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await axios.get<Posts[]>(`/api/board?id=${subID}`, {
        });
        setPosts(response.data);
      } catch (error) {
        console.error("Error fetching profile data:",error);
      }
    };
    fetchPosts();
  }, [subID,navigate]);
  //의존성 배열에 id와 navigate를 넣어주면 됨. 다만 id를 넣으면 id값이 변할 때 fetchPosts 함수가 로딩되어 데이터를 새롭게 가져옴 + ESLint 경고해소
  //-> 유저가 임의로 url을 건드려서 데이터를 들고오는 것이 가능. 하지만 유의미한 데이터는 아니고 post의 경우는 서버상에서 필터링도 가능
   //useEffect 내부에서 navigate 사용 중이므로 의존성 배열에 넣어줘야 함
  return (
    <div className="board-container">
      <div className="content-section">
        <div className="title-bar">
          <button className="title-left" onClick={()=>navigate ('/')}>메인 페이지</button>
          <div className="title-center">{subTitle}</div>
          <button className="title-left" onClick={()=>navigate ('/post')}>게시글 작성하기</button>
        </div>

        <div className="post-list">
          {posts.map((post) => (
            <div key={post.postId} className="post-item" 
            onClick={()=>
            navigate(`/content`, {state: {Post : post}})}>
              <p>{post.title}</p>
            </div>
          ))}
        </div>

        <div className="pagination">
          <button className="page-btn">페이지</button>
        </div>
      </div>
    </div>
  );
};

export default BoardPage;
