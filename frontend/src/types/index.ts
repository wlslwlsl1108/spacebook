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

// 공간 목록 (백엔드 SpaceListResponse 대응)
export interface SpaceListItem {
  id: number;
  spaceName: string;
  spaceType: "STUDY" | "PARTY" | "MEETING";
  capacity: number;
  location: string;
  pricePerHour: number;
  imageUrl: string;
}

// 공간 상세 (백엔드 SpaceResponse 대응)
export interface Space {
  id: number;
  spaceName: string;
  description: string;
  imageUrl: string;
  spaceType: "STUDY" | "PARTY" | "MEETING";
  pricePerHour: number;
  location: string;
  capacity: number;
  spaceStatus: "OPEN" | "CLOSED";
  ownerId: number;
  createdAt: string;
  updatedAt: string;
}

// Spring Page 응답
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

// 공간 검색 파라미터
export interface SpaceSearchParams {
  location?: string;
  spaceType?: "STUDY" | "PARTY" | "MEETING";
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
  sort?: string;
}

// 예약 생성 요청 (백엔드 CreateReservationRequest 대응)
export interface CreateReservationRequest {
  spaceId: number;
  startTime: string;
  endTime: string;
  peopleCount: number;
  purpose?: string;
}

// 예약 응답 (백엔드 ReservationResponse 대응)
export interface Reservation {
  id: number;
  spaceId: number;
  spaceName: string;
  userId: number;
  startTime: string;
  endTime: string;
  peopleCount: number;
  totalPrice: number;
  purpose: string;
  status: "CONFIRMED" | "CANCELLED";
  createdAt: string;
}

// AI 추천
export interface RecommendationRequest {
  query: string;
}
