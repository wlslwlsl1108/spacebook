"use client";

import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  type ReactNode,
} from "react";
import type { User, LoginRequest, SignupRequest, TokenResponse, UpdateUserRequest } from "@/types";
import {
  api,
  setTokens,
  clearTokens,
  getAccessToken,
  setSessionExpiredCallback,
  withdraw as withdrawApi,
  updateMyInfo,
} from "@/lib/api";

// Context에 담길 인증 상태와 함수들
interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: (request: LoginRequest) => Promise<void>;
  signup: (request: SignupRequest) => Promise<void>;
  logout: () => Promise<void>;
  withdraw: (password: string) => Promise<void>;
  updateProfile: (request: UpdateUserRequest) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

// 앱 전체를 감싸는 Provider
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // 토큰이 있으면 사용자 정보 조회
  const fetchUser = useCallback(async () => {
    const token = getAccessToken();
    if (!token) {
      setIsLoading(false);
      return;
    }

    try {
      const res = await api<User>("/users/me");
      setUser(res.data);
    } catch {
      clearTokens();
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  // 토큰 재발급 실패 시 user 상태 초기화
  useEffect(() => {
    setSessionExpiredCallback(() => {
      setUser(null);
    });
    return () => setSessionExpiredCallback(null);
  }, []);

  const login = async (request: LoginRequest) => {
    const res = await api<TokenResponse>("/auth/login", {
      method: "POST",
      body: JSON.stringify(request),
    });
    setTokens(res.data.accessToken, res.data.refreshToken);
    await fetchUser();
  };

  const signup = async (request: SignupRequest) => {
    const res = await api<TokenResponse>("/auth/signup", {
      method: "POST",
      body: JSON.stringify(request),
    });
    setTokens(res.data.accessToken, res.data.refreshToken);
    await fetchUser();
  };

  const logout = async () => {
    try {
      await api<void>("/auth/logout", { method: "POST" });
    } finally {
      clearTokens();
      setUser(null);
    }
  };

  const withdraw = async (password: string) => {
    await withdrawApi(password);
    clearTokens();
    setUser(null);
  };

  const updateProfile = async (request: UpdateUserRequest) => {
    await updateMyInfo(request);
    await fetchUser();
  };

  return (
    <AuthContext.Provider value={{ user, isLoading, login, signup, logout, withdraw, updateProfile }}>
      {children}
    </AuthContext.Provider>
  );
}

// 컴포넌트에서 인증 상태를 사용하는 훅
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth는 AuthProvider 안에서만 사용할 수 있습니다.");
  }
  return context;
}
