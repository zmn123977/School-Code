// 直接使用axios，不搞复杂配置，确保和后端8080端口通信
import axios from 'axios';

// 硬编码后端地址，杜绝任何端口/路径错误
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000, // 延长超时时间，避免网络延迟问题
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
});

// 简单拦截器，只打印错误
request.interceptors.response.use(
  response => response,
  error => {
    console.log('后端接口地址：http://localhost:8080/examSubject/list');
    console.error('请求失败详情：', error);
    return Promise.reject(error);
  }
);

export default request;
export interface ApiResult {
  code: number;
  message: string;
  data?: any;
}