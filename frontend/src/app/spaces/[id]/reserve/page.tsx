"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { useAuth } from "@/hooks/use-auth";
import { getSpaceDetail, createReservation } from "@/lib/api";
import type { Space } from "@/types";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";

const spaceTypeLabel: Record<Space["spaceType"], string> = {
  STUDY: "스터디",
  PARTY: "파티",
  MEETING: "회의",
};

// 정시 시간 옵션 생성 (09:00 ~ 22:00)
const HOUR_OPTIONS = Array.from({ length: 14 }, (_, i) => {
  const hour = i + 9;
  return { value: String(hour), label: `${String(hour).padStart(2, "0")}:00` };
});

export default function ReservePage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const { user } = useAuth();

  const [space, setSpace] = useState<Space | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  // 폼 상태
  const [date, setDate] = useState("");
  const [startHour, setStartHour] = useState("");
  const [endHour, setEndHour] = useState("");
  const [peopleCount, setPeopleCount] = useState("");
  const [purpose, setPurpose] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState("");

  // 비로그인 시 로그인 페이지로 리다이렉트
  useEffect(() => {
    if (!user) {
      router.push("/login");
    }
  }, [user, router]);

  // 공간 정보 로드
  useEffect(() => {
    async function fetchSpace() {
      try {
        const res = await getSpaceDetail(Number(id));
        setSpace(res.data);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "공간 정보를 불러올 수 없습니다."
        );
      } finally {
        setIsLoading(false);
      }
    }
    fetchSpace();
  }, [id]);

  // 오늘 날짜 (min 속성용)
  const today = new Date().toISOString().split("T")[0];

  // 가격 계산
  const hours =
    startHour && endHour ? Number(endHour) - Number(startHour) : 0;
  const totalPrice = hours > 0 && space ? hours * space.pricePerHour : 0;

  // 종료 시간 옵션 (시작 시간 이후만)
  const endHourOptions = startHour
    ? HOUR_OPTIONS.filter((opt) => Number(opt.value) > Number(startHour))
    : [];

  // 시작 시간 변경 시 종료 시간 초기화
  const handleStartHourChange = (value: string) => {
    setStartHour(value);
    if (endHour && Number(endHour) <= Number(value)) {
      setEndHour("");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitError("");

    if (!date || !startHour || !endHour || !peopleCount) {
      setSubmitError("필수 항목을 모두 입력해주세요.");
      return;
    }

    setIsSubmitting(true);

    try {
      const startTime = `${date}T${String(Number(startHour)).padStart(2, "0")}:00:00`;
      const endTime = `${date}T${String(Number(endHour)).padStart(2, "0")}:00:00`;

      await createReservation({
        spaceId: Number(id),
        startTime,
        endTime,
        peopleCount: Number(peopleCount),
        purpose: purpose || undefined,
      });

      alert("예약이 완료되었습니다.");
      router.push("/");
    } catch (err) {
      setSubmitError(
        err instanceof Error ? err.message : "예약에 실패했습니다."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!user) return null;

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <p className="text-center text-muted-foreground py-12">불러오는 중...</p>
      </div>
    );
  }

  if (error || !space) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8 text-center space-y-4">
        <p className="text-muted-foreground py-12">
          {error || "공간을 찾을 수 없습니다."}
        </p>
        <Button variant="outline" onClick={() => router.push("/")}>
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* 공간 정보 요약 */}
        <div className="lg:col-span-2 space-y-6">
          <div>
            <Button
              variant="ghost"
              className="mb-4 px-0"
              onClick={() => router.push(`/spaces/${id}`)}
            >
              ← 공간 상세로 돌아가기
            </Button>
            <div className="flex items-center gap-3 mb-2">
              <h1 className="text-2xl font-bold">{space.spaceName}</h1>
              <Badge>{spaceTypeLabel[space.spaceType]}</Badge>
            </div>
            <p className="text-muted-foreground">{space.location}</p>
          </div>

          <Separator />

          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-sm text-muted-foreground">수용 인원</p>
              <p className="font-medium">최대 {space.capacity}명</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">시간당 가격</p>
              <p className="font-medium">
                {space.pricePerHour.toLocaleString()}원
              </p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">상태</p>
              <p className="font-medium">예약 가능</p>
            </div>
          </div>

          {space.description && (
            <>
              <Separator />
              <div>
                <h2 className="text-lg font-semibold mb-2">공간 소개</h2>
                <p className="text-muted-foreground whitespace-pre-line">
                  {space.description}
                </p>
              </div>
            </>
          )}
        </div>

        {/* 예약 폼 */}
        <div>
          <Card>
            <CardHeader>
              <CardTitle>예약하기</CardTitle>
            </CardHeader>
            <form onSubmit={handleSubmit}>
              <CardContent className="space-y-4">
                {/* 날짜 */}
                <div className="space-y-2">
                  <Label htmlFor="date">날짜</Label>
                  <Input
                    id="date"
                    type="date"
                    min={today}
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    required
                  />
                </div>

                {/* 시작 시간 */}
                <div className="space-y-2">
                  <Label>시작 시간</Label>
                  <Select
                    value={startHour}
                    onValueChange={handleStartHourChange}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="시작 시간 선택" />
                    </SelectTrigger>
                    <SelectContent>
                      {HOUR_OPTIONS.slice(0, -1).map((opt) => (
                        <SelectItem key={opt.value} value={opt.value}>
                          {opt.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* 종료 시간 */}
                <div className="space-y-2">
                  <Label>종료 시간</Label>
                  <Select
                    value={endHour}
                    onValueChange={setEndHour}
                    disabled={!startHour}
                  >
                    <SelectTrigger>
                      <SelectValue
                        placeholder={
                          startHour
                            ? "종료 시간 선택"
                            : "시작 시간을 먼저 선택"
                        }
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {endHourOptions.map((opt) => (
                        <SelectItem key={opt.value} value={opt.value}>
                          {opt.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* 인원 수 */}
                <div className="space-y-2">
                  <Label htmlFor="peopleCount">인원 수</Label>
                  <Input
                    id="peopleCount"
                    type="number"
                    min={1}
                    max={space.capacity}
                    placeholder={`최대 ${space.capacity}명`}
                    value={peopleCount}
                    onChange={(e) => setPeopleCount(e.target.value)}
                    required
                  />
                </div>

                {/* 목적 (선택) */}
                <div className="space-y-2">
                  <Label htmlFor="purpose">
                    목적 <span className="text-muted-foreground">(선택)</span>
                  </Label>
                  <Input
                    id="purpose"
                    type="text"
                    placeholder="예: 팀 회의, 스터디 등"
                    value={purpose}
                    onChange={(e) => setPurpose(e.target.value)}
                  />
                </div>

                <Separator />

                {/* 가격 표시 */}
                <div className="space-y-1">
                  {totalPrice > 0 ? (
                    <>
                      <div className="flex justify-between text-sm text-muted-foreground">
                        <span>
                          {space.pricePerHour.toLocaleString()}원 x {hours}시간
                        </span>
                      </div>
                      <div className="flex justify-between text-lg font-bold">
                        <span>총 금액</span>
                        <span>{totalPrice.toLocaleString()}원</span>
                      </div>
                    </>
                  ) : (
                    <p className="text-sm text-muted-foreground text-center">
                      시간을 선택하면 금액이 표시됩니다
                    </p>
                  )}
                </div>

                {submitError && (
                  <p className="text-sm text-destructive">{submitError}</p>
                )}
              </CardContent>

              <CardFooter className="flex-col gap-2">
                <Button
                  type="submit"
                  className="w-full"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? "예약 중..." : "예약하기"}
                </Button>
                <p className="text-xs text-muted-foreground text-center">
                  결제 기능은 추후 추가 예정입니다
                </p>
              </CardFooter>
            </form>
          </Card>
        </div>
      </div>
    </div>
  );
}
