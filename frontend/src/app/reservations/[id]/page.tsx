"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { useAuth } from "@/hooks/use-auth";
import { getReservationDetail, cancelReservation } from "@/lib/api";
import type { Reservation } from "@/types";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

export default function ReservationDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const { user, isLoading: authLoading } = useAuth();

  const [reservation, setReservation] = useState<Reservation | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  // 취소 Dialog 상태
  const [cancelOpen, setCancelOpen] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);
  const [cancelError, setCancelError] = useState("");

  // 비로그인 → 로그인 페이지로 리다이렉트
  useEffect(() => {
    if (!authLoading && !user) {
      router.push("/login");
    }
  }, [authLoading, user, router]);

  // 예약 상세 조회
  useEffect(() => {
    if (!user) return;

    async function fetchReservation() {
      try {
        const res = await getReservationDetail(Number(id));
        setReservation(res.data);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "예약 정보를 불러올 수 없습니다.",
        );
      } finally {
        setIsLoading(false);
      }
    }

    fetchReservation();
  }, [user, id]);

  // 예약 취소
  async function handleCancel() {
    setIsCancelling(true);
    setCancelError("");

    try {
      await cancelReservation(Number(id));
      setReservation((prev) =>
        prev ? { ...prev, status: "CANCELLED" } : prev,
      );
      setCancelOpen(false);
    } catch (err) {
      setCancelError(
        err instanceof Error ? err.message : "예약 취소에 실패했습니다.",
      );
    } finally {
      setIsCancelling(false);
    }
  }

  if (!user) return null;

  if (isLoading) {
    return (
      <p className="text-center text-muted-foreground py-12">불러오는 중...</p>
    );
  }

  if (error || !reservation) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-8 text-center space-y-4">
        <p className="text-muted-foreground py-12">
          {error || "예약 정보를 찾을 수 없습니다."}
        </p>
        <Button variant="outline" onClick={() => router.push("/mypage")}>
          마이페이지로 돌아가기
        </Button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 pt-16 pb-8">
      <Button
        variant="ghost"
        className="mb-4 px-0"
        onClick={() => router.push("/mypage")}
      >
        &larr; 마이페이지로 돌아가기
      </Button>

      <div className="flex items-center gap-3 mb-8">
        <h1 className="text-3xl font-bold">예약 상세</h1>
        {reservation.status === "CONFIRMED" ? (
          <Badge>확정</Badge>
        ) : (
          <Badge variant="secondary">취소됨</Badge>
        )}
      </div>

      <Card>
        <CardContent className="pt-6 space-y-6">
          {/* 공간 정보 */}
          <div>
            <h2 className="text-lg font-semibold mb-3">공간 정보</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">공간명</p>
                <p
                  className="font-medium cursor-pointer hover:underline"
                  onClick={() =>
                    router.push(`/spaces/${reservation.spaceId}`)
                  }
                >
                  {reservation.spaceName}
                </p>
              </div>
            </div>
          </div>

          <Separator />

          {/* 예약 일시 */}
          <div>
            <h2 className="text-lg font-semibold mb-3">예약 일시</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">날짜</p>
                <p className="font-medium">
                  {reservation.startTime.slice(0, 10)}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">시간</p>
                <p className="font-medium">
                  {reservation.startTime.slice(11, 16)} ~{" "}
                  {reservation.endTime.slice(11, 16)}
                </p>
              </div>
            </div>
          </div>

          <Separator />

          {/* 예약 상세 */}
          <div>
            <h2 className="text-lg font-semibold mb-3">예약 내역</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">인원</p>
                <p className="font-medium">{reservation.peopleCount}명</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">총 가격</p>
                <p className="font-medium">
                  {reservation.totalPrice.toLocaleString()}원
                </p>
              </div>
              <div className="sm:col-span-2">
                <p className="text-sm text-muted-foreground">사용 목적</p>
                <p className="font-medium">
                  {reservation.purpose || "-"}
                </p>
              </div>
            </div>
          </div>

          <Separator />

          {/* 예약일 */}
          <div>
            <p className="text-sm text-muted-foreground">예약일</p>
            <p className="font-medium">
              {reservation.createdAt.slice(0, 10)}
            </p>
          </div>

          {/* 취소 버튼 */}
          {reservation.status === "CONFIRMED" && (
            <>
              <Separator />
              <div className="flex justify-end">
                <Button
                  variant="destructive"
                  onClick={() => setCancelOpen(true)}
                >
                  예약 취소
                </Button>
              </div>
            </>
          )}
        </CardContent>
      </Card>

      {/* 예약 취소 확인 Dialog */}
      <Dialog open={cancelOpen} onOpenChange={setCancelOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>예약 취소</DialogTitle>
            <DialogDescription>
              정말로 이 예약을 취소하시겠습니까? 취소 후에는 되돌릴 수 없습니다.
            </DialogDescription>
          </DialogHeader>

          {cancelError && (
            <p className="text-sm text-destructive">{cancelError}</p>
          )}

          <DialogFooter>
            <Button variant="outline" onClick={() => setCancelOpen(false)}>
              닫기
            </Button>
            <Button
              variant="destructive"
              onClick={handleCancel}
              disabled={isCancelling}
            >
              {isCancelling ? "처리 중..." : "예약 취소"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
