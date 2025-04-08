// src/pages/SignupPage.tsx
import React, { useState, useRef, useMemo, useCallback, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Register.css';
import ReactQuill from 'react-quill'; //게시판 에디터 - 다양한 기능 지원
import 'react-quill/dist/quill.snow.css';
import axios from '../api/axiosInstance';

const PostPage = () => {
  const [title, setTitle] = useState('');
  const [editorContent, setEditorContent] = useState('');
  const navigate = useNavigate();
  //const location = useLocation();
  const subID = Number(localStorage.getItem('subID'));
  const quillRef = useRef<ReactQuill>(null);

  const handlePost = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
        const formData = new FormData();
        formData.append('subcultureId',subID.toString())
        formData.append('title',title)
        formData.append('postBody',editorContent)
      const response = await axios.post(`/api/post`, formData);
      if (response.status===200 || response.status===201) {
        navigate('/board');
      } else {
        alert('작성 실패');
      }
    } catch (error:any) {
      if(error.response?.status === 403 || error.response?.status === 401){
        alert('로그인 해주세요');
        navigate('/login');
      }
      console.error('Register error:', error);
    }
  };


   // 커스텀 이미지 핸들러 (에디터 툴바에 연결)
     // imageHandler wrapped with useCallback to ensure stability.// 제목을 ref로 관리
const titleRef = useRef(title);
useEffect(() => {
  titleRef.current = title;
}, [title]);

const getBase64 = (file:File):Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
  });
};
// imageHandler를 useCallback으로 감싸고 title 대신 titleRef.current를 사용
const imageHandler = useCallback(() => {
  const input = document.createElement('input');
  input.setAttribute('type', 'file');
  input.setAttribute('accept', 'image/*');
  input.click(); //이게 있어야 이미지 삽입 기능이 동작함
  input.onchange = async () => {
    const file = input.files ? input.files[0] : null;
    if(file) {
      const base64String = await getBase64(file);
      const quill = quillRef.current?.getEditor();
      const range = quill?.getSelection()?.index;
      if(quill && range !== undefined){
        quill?.insertEmbed(range, 'image',base64String);
      }
    }
    };
  }, []);
    /*
    if (file) {
      const formData = new FormData();
      formData.append('subcultureId', subID.toString());
      // ref를 사용하여 최신 title 값을 읽음
      formData.append('title', titleRef.current);
      formData.append('image', file);
     // const csrfToken = getCsrfToken();

      
      try{
      const response = await fetch('http://localhost:8080/postImage', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'X-XSRF-TOKEN': csrfToken || '',
        },
        body: formData,
        credentials: 'include'
      });

      if (response.ok) {
        const data = await response.json();
        const imageUrl = data.imageUrl;
        const quill = quillRef.current?.getEditor();
        const range = quill?.getSelection()?.index;
        if (range !== undefined && imageUrl) {
          quill?.insertEmbed(range, 'image', imageUrl);
        }
      } else {
        alert('이미지 업로드에 실패했습니다.');
      }
    } catch (error) {
      console.error('이미지 업로드 오류:',error);
    }
      */


  const modules = useMemo(() => ({
    toolbar: {
      container: [
        [{ header: [1, 2, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        ['link', 'image']
      ],
      handlers: {
        image: imageHandler
      }
    }
  }), [imageHandler]);
  


  return (
    <div>
    <li> 작품 번호 :{subID}</li>
    <button onClick={() => navigate(`/board?SubID=${subID}`)}>게시판으로</button>
    <form onSubmit={handlePost}>
      <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="title" required />
      <ReactQuill
        ref={quillRef}
        value={editorContent}
        onChange={setEditorContent}
        modules={modules}
        placeholder="내용을 입력하세요..."
      />
      <button type="submit">게시글 작성</button>
    </form>
    </div>
  );
};

export default PostPage;
