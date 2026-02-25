import type {
  ApiResponse,
  Space,
  SpaceListItem,
  PageResponse,
  SpaceSearchParams,
} from "@/types";

const BASE_URL = "http://localhost:8080/api/v1";

// localStorage에서 토큰 읽기/쓰기/삭제
export function getAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("accessToken");
}

export function getRefreshToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("refreshToken");
}

export function setTokens(accessToken: string, refreshToken: string) {
  localStorage.setItem("accessToken", accessToken);
  localStorage.setItem("refreshToken", refreshToken);
}

export function clearTokens() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
}

// 공통 fetch 래퍼
export async function api<T>(
  endpoint: string,
  options: RequestInit = {},
): Promise<ApiResponse<T>> {
  const token = getAccessToken();

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...((options.headers as Record<string, string>) || {}),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  const body: ApiResponse<T> = await response.json();

  if (!body.success) {
    throw new Error(body.message || "요청에 실패했습니다.");
  }

  return body;
}

// 공간 목록 조회 (검색/필터/정렬/페이지네이션)
export async function getSpaces(params: SpaceSearchParams = {}) {
  const query = new URLSearchParams();

  if (params.location) query.set("location", params.location);
  if (params.spaceType) query.set("spaceType", params.spaceType);
  if (params.minPrice !== undefined) query.set("minPrice", String(params.minPrice));
  if (params.maxPrice !== undefined) query.set("maxPrice", String(params.maxPrice));
  if (params.page !== undefined) query.set("page", String(params.page));
  if (params.size !== undefined) query.set("size", String(params.size));
  if (params.sort) query.set("sort", params.sort);

  const qs = query.toString();
  return api<PageResponse<SpaceListItem>>(`/spaces${qs ? `?${qs}` : ""}`);
}

// 공간 상세 조회
export async function getSpaceDetail(id: number) {
  return api<Space>(`/spaces/${id}`);
}

// AI 추천 (로그인 필요)
export async function getRecommendations(queryText: string) {
  return api<SpaceListItem[]>("/recommendations", {
    method: "POST",
    body: JSON.stringify({ query: queryText }),
  });
}
