"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/use-auth";
import { getMyReservations } from "@/lib/api";
import type { ReservationListItem } from "@/types";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

export default function MyPage() {
  const router = useRouter();
  const { user, isLoading: authLoading, withdraw } = useAuth();

  // 예약 목록 상태
  const [reservations, setReservations] = useState<ReservationListItem[]>([]);
  const [page, setPage] = useState(0);
  const [isLast, setIsLast] = useState(false);
  const [isLoadingReservations, setIsLoadingReservations] = useState(true);

  // 회원 탈퇴 Dialog 상태
  const [withdrawOpen, setWithdrawOpen] = useState(false);
  const [withdrawPassword, setWithdrawPassword] = useState("");
  const [withdrawError, setWithdrawError] = useState("");
  const [isWithdrawing, setIsWithdrawing] = useState(false);

  // 비로그인 → 로그인 페이지로 리다이렉트
  useEffect(() => {
    if (!authLoading && !user) {
      router.push("/login");
    }
  }, [authLoading, user, router]);

  // 예약 목록 조회
  useEffect(() => {
    if (!user) return;

    async function fetchReservations() {
      try {
        const res = await getMyReservations(0);
        setReservations(res.data.content);
        setIsLast(res.data.last);
      } catch {
        // 예약 목록 로드 실패 시 빈 배열 유지
      } finally {
        setIsLoadingReservations(false);
      }
    }

    fetchReservations();
  }, [user]);

  // 더 보기
  async function handleLoadMore() {
    const nextPage = page + 1;
    try {
      const res = await getMyReservations(nextPage);
      setReservations((prev) => [...prev, ...res.data.content]);
      setIsLast(res.data.last);
      setPage(nextPage);
    } catch {
      // 추가 로드 실패 시 무시
    }
  }

  // 회원 탈퇴
  async function handleWithdraw() {
    if (!withdrawPassword.trim()) {
      setWithdrawError("비밀번호를 입력해주세요.");
      return;
    }

    setIsWithdrawing(true);
    setWithdrawError("");

    try {
      await withdraw(withdrawPassword);
      router.push("/");
    } catch (err) {
      setWithdrawError(
        err instanceof Error ? err.message : "탈퇴에 실패했습니다.",
      );
    } finally {
      setIsWithdrawing(false);
    }
  }

  // Dialog 닫을 때 상태 초기화
  function handleWithdrawOpenChange(open: boolean) {
    setWithdrawOpen(open);
    if (!open) {
      setWithdrawPassword("");
      setWithdrawError("");
    }
  }

  if (!user) return null;

  return (
    <div className="max-w-6xl mx-auto px-4 pt-16 pb-8">
      <h1 className="text-3xl font-bold underline underline-offset-8 decoration-2 mb-16">마이페이지</h1>

      <div className="grid lg:grid-cols-3 gap-8">
        {/* 좌측: 프로필 */}
        <div className="lg:col-span-1 space-y-4">
          <h2 className="text-2xl font-bold pl-4">내 정보</h2>
          <Card>
            <CardContent className="space-y-4 pt-6">
              <div>
                <p className="text-sm text-muted-foreground">이름</p>
                <p className="font-medium">{user.username}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">이메일</p>
                <p className="font-medium">{user.email}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">전화번호</p>
                <p className="font-medium">{user.phoneNumber}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">가입일</p>
                <p className="font-medium">{user.createdAt.slice(0, 10)}</p>
              </div>

              <Separator />

              <div className="space-y-2">
                <Button
                  variant="outline"
                  className="w-full"
                  onClick={() => router.push("/mypage/edit")}
                >
                  내 정보 수정
                </Button>
                <Button
                  variant="destructive"
                  className="w-full"
                  onClick={() => setWithdrawOpen(true)}
                >
                  회원 탈퇴
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 우측: 예약 목록 */}
        <div className="lg:col-span-2 space-y-4">
          <h2 className="text-2xl font-bold pl-4">예약 내역</h2>

          {isLoadingReservations ? (
            <p className="text-center text-muted-foreground py-12">
              불러오는 중...
            </p>
          ) : reservations.length === 0 ? (
            <p className="text-center text-muted-foreground py-12">
              예약 내역이 없습니다.
            </p>
          ) : (
            <>
              {reservations.map((reservation) => (
                <Card
                  key={reservation.id}
                  className="cursor-pointer hover:bg-accent/50 transition-colors"
                  onClick={() => router.push(`/reservations/${reservation.id}`)}
                >
                  <CardContent className="flex items-center justify-between py-4">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <p className="font-medium">{reservation.spaceName}</p>
                        {reservation.status === "CONFIRMED" ? (
                          <Badge>확정</Badge>
                        ) : (
                          <Badge variant="secondary">취소됨</Badge>
                        )}
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {reservation.startTime.slice(0, 10)}{" "}
                        {reservation.startTime.slice(11, 16)} ~{" "}
                        {reservation.endTime.slice(11, 16)}
                      </p>
                    </div>
                  </CardContent>
                </Card>
              ))}

              {!isLast && (
                <div className="text-center pt-4">
                  <Button variant="outline" onClick={handleLoadMore}>
                    더 보기
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* 회원 탈퇴 확인 Dialog */}
      <Dialog open={withdrawOpen} onOpenChange={handleWithdrawOpenChange}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>회원 탈퇴</DialogTitle>
            <DialogDescription>
              탈퇴하면 모든 데이터가 삭제되며 복구할 수 없습니다.
              계속하려면 비밀번호를 입력해주세요.
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-2">
            <Label htmlFor="withdraw-password">비밀번호</Label>
            <Input
              id="withdraw-password"
              type="password"
              value={withdrawPassword}
              onChange={(e) => setWithdrawPassword(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleWithdraw();
              }}
              placeholder="비밀번호를 입력하세요"
            />
            {withdrawError && (
              <p className="text-sm text-destructive">{withdrawError}</p>
            )}
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => handleWithdrawOpenChange(false)}
            >
              취소
            </Button>
            <Button
              variant="destructive"
              onClick={handleWithdraw}
              disabled={isWithdrawing}
            >
              {isWithdrawing ? "처리 중..." : "탈퇴하기"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
