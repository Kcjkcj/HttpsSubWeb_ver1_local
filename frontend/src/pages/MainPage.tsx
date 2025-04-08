// src/paged/MainPage.tsx
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Main.css';
import axios from '../api/axiosInstance';

interface Subculture {
  subcultureId: number; //post man에서 JSON으로 어떻게 받는지 보고 그 이름에 맞추는게 좋음
  title : string;
  imagePath: string;
  genre:string;
}


const MainPage = () => {
  const navigate = useNavigate();
  const [subcultures, setSubculture] = useState<Subculture[]>([]);
  //const [subcultures, setSubculture] = useState([]);
  const [currentPage, setCurrentPage] = useState(0); //0부터
  const itemsPerPage=10;


  const isFetched = useRef(false); //요청 중복 방지를 위한 플래그
  const [authenticated, setAuthenticated] = useState<boolean>(false);

  useEffect(()=>{
    const fetchData = async () => {
      try {
        // 인증 체크
        const authRes = await axios.get('/api/auth/validate');
        if (authRes.data.authenticated) {
          setAuthenticated(true);
        }

        // 메인 데이터
        const dataRes = await axios.get('/api/main');
        setSubculture(dataRes.data);

      } catch (error) {
        setAuthenticated(false);
        console.error('MainPage 초기 로딩 오류:', error);
      }
    };

    if (!isFetched.current) {
      fetchData();
      isFetched.current = true;
    }
  }, []);

  const handleLogout = async () => {
    try {
      const response = await axios.post(`/api/logout`);
      if (response.status===200 || response.status===201) {
        alert("로그아웃 되었습니다."); //서버에서 메시지를 받아서 하기에는 LogoutHandler를 만들어야 하는데 그렇게 유의미하지 않음
        setAuthenticated(false);
        navigate('/');
      } else {
        console.error('Logout failed');
      }
    } catch (error) {
      console.error('Logout error:', error);
    }
  };

    const indexOfLastItem = (currentPage + 1)*itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentItems = subcultures.slice(indexOfFirstItem,indexOfLastItem);
    //페이지 번호에 따라 해당 slice를 계산해서 보여주므로 1번 페이지로 가면 state에서 다시 불러옴
    //매번 fetch해서 다시 불러오는게 아님
    const totalPages = Math.ceil(subcultures.length / itemsPerPage);
  return ( //여러개의 버튼을 넣으려면 <> </>로 묶어야 하는듯
    <div className="main-page">
      <header className="main-header">
        <div className="header-buttons">
          {!authenticated ? (
            <button onClick={() => navigate('/login')}>로그인</button>
          ) : (
            <>
              <button onClick={() => navigate('/profile')}>내 계정</button>
              <button onClick={handleLogout}>로그아웃</button>
            </>
          )}
        </div>
      </header>

      <main className="main-content">
        <div className="works-list">
          <h2>작품 리스트</h2>
          <button className="add-work-btn" onClick={()=>navigate('/create_subculture')}>작품 추가</button>
          <div className="works-grid">
            {currentItems.map((subculture) => (
              <div
                key={subculture.subcultureId}
                className="work-item"
                onClick={() => {
                    localStorage.setItem('subID',subculture.subcultureId.toString());
                    localStorage.setItem('subTitle',subculture.title);
                    navigate(`/board?SubID=${subculture.subcultureId}`)}}
              >
                <img src={`${process.env.REACT_APP_API_BASE_URL}${(subculture.imagePath)}`} alt={`${subculture.title}`} />
                <h3>{subculture.title}</h3>
              </div>
            ))}
          </div>
          <div className='pagination'>
            {Array.from({length : totalPages}).map((_,index) =>
            ( <button key={index} onClick={() => setCurrentPage(index)}>
              {index + 1}
            </button>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
};

export default MainPage;
