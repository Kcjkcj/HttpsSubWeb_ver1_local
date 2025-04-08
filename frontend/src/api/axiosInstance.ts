// src/api/axiosInstance.ts
import axios from 'axios';

const instance = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL,
  withCredentials: true
});

instance.interceptors.request.use((config) => {
  const csrfToken = document.cookie
    .split('; ')
    .find(row => row.startsWith('XSRF-TOKEN'))
    ?.split('=')[1];
  if (csrfToken) {
    config.headers['X-XSRF-TOKEN'] = csrfToken;
  }
  return config;
});

// 응답 인터셉터
instance.interceptors.response.use( 
  //여기에 refreshToken에 따라 새로운 jwt를 발급하기 위한 로직이 포함되어 있어서
  //axiosInstance를 활용하도록 axios RestAPI로 리펙토링
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        await axios.post(
          '/api/token',
          {},
          { withCredentials: true }
        );

        return instance(originalRequest); // 원래 요청 재전송
      } catch (refreshError) {
        console.error('Refresh token 실패:', refreshError);
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  }
);

export default instance;
