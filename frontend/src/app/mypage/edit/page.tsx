"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/use-auth";
import type { UpdateUserRequest } from "@/types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";

export default function ProfileEditPage() {
  const router = useRouter();
  const { user, isLoading: authLoading, updateProfile } = useAuth();

  // 전화번호 상태
  const [phoneNumber, setPhoneNumber] = useState("");

  // 비밀번호 상태
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");

  // 공통 상태
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 비로그인 → 로그인 페이지로 리다이렉트
  useEffect(() => {
    if (!authLoading && !user) {
      router.push("/login");
    }
  }, [authLoading, user, router]);

  // 현재 전화번호를 초기값으로 설정
  useEffect(() => {
    if (user) {
      setPhoneNumber(user.phoneNumber);
    }
  }, [user]);

  function validate(): string | null {
    const phoneChanged = phoneNumber !== user?.phoneNumber;
    const passwordFilled = currentPassword || newPassword;

    // 아무것도 변경하지 않은 경우
    if (!phoneChanged && !passwordFilled) {
      return "변경할 내용이 없습니다.";
    }

    // 전화번호 유효성 검사
    if (phoneChanged && !/^01[0-9]-?\d{3,4}-?\d{4}$/.test(phoneNumber)) {
      return "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)";
    }

    // 비밀번호: 둘 다 입력하거나 둘 다 비워야 함
    if (currentPassword && !newPassword) {
      return "새 비밀번호를 입력해주세요.";
    }
    if (!currentPassword && newPassword) {
      return "현재 비밀번호를 입력해주세요.";
    }

    // 현재 비밀번호와 새 비밀번호 동일 여부
    if (currentPassword && newPassword && currentPassword === newPassword) {
      return "새 비밀번호가 현재 비밀번호와 동일합니다.";
    }

    // 새 비밀번호 유효성 검사
    if (newPassword) {
      if (newPassword.length < 8) {
        return "새 비밀번호는 8자 이상이어야 합니다.";
      }
      if (!/(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])/.test(newPassword)) {
        return "새 비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)를 모두 포함해야 합니다.";
      }
    }

    return null;
  }

  async function handleSubmit() {
    setError("");

    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    setIsSubmitting(true);

    try {
      const request: UpdateUserRequest = {};

      // 변경된 필드만 전송
      if (phoneNumber !== user?.phoneNumber) {
        request.phoneNumber = phoneNumber;
      }
      if (currentPassword && newPassword) {
        request.currentPassword = currentPassword;
        request.newPassword = newPassword;
      }

      await updateProfile(request);
      router.push("/mypage");
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "정보 수정에 실패했습니다.",
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  if (!user) return null;

  return (
    <div className="max-w-2xl mx-auto px-4 pt-16 pb-8">
      <Button
        variant="ghost"
        className="mb-4 px-0"
        onClick={() => router.push("/mypage")}
      >
        &larr; 마이페이지로 돌아가기
      </Button>

      <h1 className="text-3xl font-bold mb-8">내 정보 수정</h1>

      <div className="space-y-6">
        {/* 전화번호 변경 */}
        <Card>
          <CardHeader>
            <CardTitle>전화번호 변경</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="phoneNumber">전화번호</Label>
              <Input
                id="phoneNumber"
                type="tel"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                placeholder="010-1234-5678"
              />
            </div>
          </CardContent>
        </Card>

        {/* 비밀번호 변경 */}
        <Card>
          <CardHeader>
            <CardTitle>비밀번호 변경</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="currentPassword">현재 비밀번호</Label>
              <Input
                id="currentPassword"
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                placeholder="현재 비밀번호를 입력하세요"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="newPassword">새 비밀번호</Label>
              <Input
                id="newPassword"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="새 비밀번호를 입력하세요 (8자 이상)"
              />
            </div>
          </CardContent>
        </Card>

        {/* 에러 메시지 */}
        {error && (
          <p className="text-sm text-destructive text-center">{error}</p>
        )}

        {/* 저장 버튼 */}
        <Separator />
        <div className="flex justify-end gap-3">
          <Button variant="outline" onClick={() => router.push("/mypage")}>
            취소
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting}>
            {isSubmitting ? "처리 중..." : "저장"}
          </Button>
        </div>
      </div>
    </div>
  );
}
