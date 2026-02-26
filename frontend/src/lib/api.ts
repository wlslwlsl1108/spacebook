import type {
  ApiResponse,
  TokenResponse,
  Space,
  SpaceListItem,
  PageResponse,
  SpaceSearchParams,
  CreateReservationRequest,
  Reservation,
  ReservationListItem,
  DeleteAccountRequest,
  UpdateUserRequest,
  User,
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

// 세션 만료 시 호출되는 콜백 (AuthProvider에서 등록)
let onSessionExpired: (() => void) | null = null;

export function setSessionExpiredCallback(callback: (() => void) | null) {
  onSessionExpired = callback;
}

// 동시 재발급 방지용 Promise
let reissuePromise: Promise<boolean> | null = null;

// refreshToken으로 토큰 재발급 시도
async function tryReissue(): Promise<boolean> {
  const refreshToken = getRefreshToken();
  if (!refreshToken) return false;

  try {
    const response = await fetch(`${BASE_URL}/auth/reissue`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });

    const body: ApiResponse<TokenResponse> = await response.json();
    if (!body.success) return false;

    setTokens(body.data.accessToken, body.data.refreshToken);
    return true;
  } catch {
    return false;
  }
}

// 공통 fetch 래퍼 (401 시 자동 재발급 + 재요청)
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

  // 401이고 재발급 대상인 경우 → 토큰 재발급 시도
  if (response.status === 401 && endpoint !== "/auth/reissue") {
    // 동시에 여러 요청이 401을 받아도 재발급은 1번만 실행
    if (!reissuePromise) {
      reissuePromise = tryReissue().finally(() => {
        reissuePromise = null;
      });
    }

    const reissued = await reissuePromise;

    if (reissued) {
      // 새 토큰으로 원래 요청 재시도
      const newToken = getAccessToken();
      if (newToken) {
        headers["Authorization"] = `Bearer ${newToken}`;
      }

      const retryResponse = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers,
      });

      const retryBody: ApiResponse<T> = await retryResponse.json();
      if (!retryBody.success) {
        throw new Error(retryBody.message || "요청에 실패했습니다.");
      }
      return retryBody;
    }

    // 재발급 실패 → 세션 만료 처리
    clearTokens();
    onSessionExpired?.();
    throw new Error("인증이 만료되었습니다. 다시 로그인해주세요.");
  }

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

// 예약 생성 (로그인 필요)
export async function createReservation(request: CreateReservationRequest) {
  return api<Reservation>("/reservations", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

// 내 예약 목록 조회 (로그인 필요)
export async function getMyReservations(page: number = 0) {
  return api<PageResponse<ReservationListItem>>(`/reservations/my?page=${page}`);
}

// 예약 상세 조회 (로그인 필요)
export async function getReservationDetail(reservationId: number) {
  return api<Reservation>(`/reservations/${reservationId}`);
}

// 예약 취소 (로그인 필요)
export async function cancelReservation(reservationId: number) {
  return api<void>(`/reservations/${reservationId}/cancel`, {
    method: "PATCH",
  });
}

// 내 정보 수정 (로그인 필요)
export async function updateMyInfo(request: UpdateUserRequest) {
  return api<User>("/users/me", {
    method: "PATCH",
    body: JSON.stringify(request),
  });
}

// 회원 탈퇴 (로그인 필요)
export async function withdraw(password: string) {
  return api<void>("/auth/withdraw", {
    method: "DELETE",
    body: JSON.stringify({ password } satisfies DeleteAccountRequest),
  });
}

// AI 추천 (로그인 필요)
export async function getRecommendations(queryText: string) {
  return api<SpaceListItem[]>("/recommendations", {
    method: "POST",
    body: JSON.stringify({ query: queryText }),
  });
}
