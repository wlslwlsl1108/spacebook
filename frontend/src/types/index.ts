// 공통 API 응답 (백엔드 ApiResponse<T> 대응)
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
  timestamp: string;
}

// 사용자
export interface User {
  id: number;
  username: string;
  email: string;
  phoneNumber: string;
  createdAt: string;
}

// 인증 요청
export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  phoneNumber: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

// 인증 응답
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

// 공간
export interface Space {
  id: number;
  spaceName: string;
  imageUrl: string;
  spaceType: "STUDY" | "PARTY" | "MEETING";
  pricePerHour: number;
  location: string;
  capacity: number;
  spaceStatus: "OPEN" | "CLOSED";
  createdAt: string;
}

// 예약
export interface Reservation {
  id: number;
  userId: number;
  spaceId: number;
  startTime: string;
  endTime: string;
  totalPrice: number;
  reservationStatus: "CONFIRMED" | "CANCELLED";
  createdAt: string;
}

// AI 추천
export interface RecommendationRequest {
  query: string;
}
